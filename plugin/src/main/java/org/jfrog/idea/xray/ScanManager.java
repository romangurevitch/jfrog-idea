package org.jfrog.idea.xray;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.jfrog.xray.client.Xray;
import com.jfrog.xray.client.impl.ComponentsFactory;
import com.jfrog.xray.client.impl.XrayClient;
import com.jfrog.xray.client.services.summary.Artifact;
import com.jfrog.xray.client.services.summary.ComponentDetail;
import com.jfrog.xray.client.services.summary.Components;
import com.jfrog.xray.client.services.summary.SummaryResponse;
import org.jetbrains.annotations.NotNull;
import org.jfrog.idea.configuration.JfrogGlobalSettings;
import org.jfrog.idea.configuration.XrayServerConfig;
import org.jfrog.idea.configuration.messages.ConfigurationDetailsChange;
import org.jfrog.idea.xray.messages.ScanComponentsChange;
import org.jfrog.idea.xray.messages.ScanFilterChange;
import org.jfrog.idea.xray.messages.ScanIssuesChange;
import org.jfrog.idea.xray.persistency.ScanCache;
import org.jfrog.idea.xray.persistency.XrayArtifact;
import org.jfrog.idea.xray.persistency.XrayLicense;
import org.jfrog.idea.xray.utils.Utils;

import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by romang on 4/26/17.
 */
public abstract class ScanManager {

    protected final Project project;
    private TreeModel scanResults;
    private final static int NUMBER_OF_ARTIFACTS_BULK_SCAN = 100;

    //Lock to prevent multiple simultaneous scans
    AtomicBoolean scanInProgress = new AtomicBoolean(false);
    private static final Logger log = Logger.getInstance(ScanManager.class);

    protected ScanManager(Project project) {
        this.project = project;
        registerOnChangeHandlers();
    }

    protected abstract Components collectComponentsToScan();

    protected abstract TreeModel updateResultsTree(TreeModel currentScanResults);

    private void scanAndUpdate(boolean quickScan, ProgressIndicator indicator) {
        // Don't scan if Xray is not configured
        if (!JfrogGlobalSettings.getInstance().isCredentialsSet()) {
            Notifications.Bus.notify(new Notification("JFrog", "JFrog Xray scan failed", "Xray server is not configured.", NotificationType.ERROR));
            return;
        }
        // Prevent multiple simultaneous scans
        if (!scanInProgress.compareAndSet(false, true)) {
            if (!quickScan) {
                Notifications.Bus.notify(new Notification("JFrog", "JFrog Xray", "Scan already in progress.", NotificationType.INFORMATION));
            }
            return;
        }
        // Collect -> Scan and store to cache -> update view
        Components components = collectComponentsToScan();
        scanAndCacheArtifacs(components, quickScan, indicator);
        scanResults = updateResultsTree(scanResults);
        MessageBus messageBus = project.getMessageBus();
        messageBus.syncPublisher(ScanComponentsChange.SCAN_COMPONENTS_CHANGE_TOPIC).update();
        scanInProgress.set(false);
    }

    public void asyncScanAndUpdateResults(boolean quickScan) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Xray: scanning for vulnerabilities...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                scanAndUpdate(quickScan, indicator);
                indicator.finishNonCancelableSection();
            }
        });
    }

    private void registerOnChangeHandlers() {
        MessageBusConnection busConnection = project.getMessageBus().connect(project);
        busConnection.subscribe(ScanFilterChange.SCAN_FILTER_CHANGE_TOPIC, () -> {
            MessageBus messageBus = project.getMessageBus();
            messageBus.syncPublisher(ScanComponentsChange.SCAN_COMPONENTS_CHANGE_TOPIC).update();
            messageBus.syncPublisher(ScanIssuesChange.SCAN_ISSUES_CHANGE_TOPIC).update();
        });

        busConnection.subscribe(ConfigurationDetailsChange.CONFIGURATION_DETAILS_CHANGE_TOPIC,
                () -> asyncScanAndUpdateResults(true));
    }

    public Set<XrayLicense> getAllLicenses() {
        Set<XrayLicense> allLicenses = new HashSet<>();
        if (scanResults == null) {
            return allLicenses;
        }
        ScanTreeNode node = (ScanTreeNode) scanResults.getRoot();
        for (int i = 0; i < node.getChildCount(); i++) {
            allLicenses.addAll(((ScanTreeNode) node.getChildAt(i)).getLicenses());
        }
        return allLicenses;
    }

    public TreeModel getFilteredScanTreeModel() {
        return FilterManager.getInstance(project).filterComponents(scanResults);
    }

    public TableModel getFilteredScanIssues(ScanTreeNode node) {
        return FilterManager.getInstance(project).filterIssues(node.getAllIssues());
    }

    public XrayArtifact getArtifactSummary(String componentId) {
        ScanCache scanCache = ScanCache.getInstance(project);
        return scanCache.getArtifact(componentId);
    }

    private void scanAndCacheArtifacs(Components components, boolean quickScan, ProgressIndicator indicator) {
        if (components == null) {
            return;
        }

        ScanCache scanCache = ScanCache.getInstance(project);
        Components componentsToScan = ComponentsFactory.create();
        for (ComponentDetail details : components.getComponentDetails()) {
            String component = Utils.removeComponentIdPrefix(details.getComponentId());
            LocalDateTime dateTime = scanCache.getLastUpdateTime(component);
            if (!quickScan || dateTime == null || LocalDateTime.now().minusWeeks(1).isAfter(dateTime)) {
                componentsToScan.addComponent(details.getComponentId(), details.getSha1());
            }
        }

        if (componentsToScan.getComponentDetails().isEmpty()) {
            return;
        }

        XrayServerConfig xrayConfig = JfrogGlobalSettings.getInstance().getXrayConfig();
        Xray xray = XrayClient.create(xrayConfig.getUrl(), xrayConfig.getUsername(), xrayConfig.getPassword());

        try {
            int currentIndex = 0;
            List<ComponentDetail> componentsList = componentsToScan.getComponentDetails();
            while (currentIndex + NUMBER_OF_ARTIFACTS_BULK_SCAN < componentsList.size()) {
                if (indicator.isCanceled()) {
                    log.info("Xray scan was canceled");
                    return;
                }

                List<ComponentDetail> partialComponentsDetails = componentsList.subList(currentIndex, currentIndex + NUMBER_OF_ARTIFACTS_BULK_SCAN);
                Components partialComponents = ComponentsFactory.create(partialComponentsDetails);
                scanComponents(xray, partialComponents);
                indicator.setFraction(((double) currentIndex + 1) / (double) componentsList.size());
                currentIndex += NUMBER_OF_ARTIFACTS_BULK_SCAN;
            }

            List<ComponentDetail> partialComponentsDetails = componentsList.subList(currentIndex, componentsList.size());
            Components partialComponents = ComponentsFactory.create(partialComponentsDetails);
            scanComponents(xray, partialComponents);
            indicator.setFraction(1);
        } catch (IOException e) {
            Notifications.Bus.notify(new Notification("JFrog", "JFrog Xray scan failed", e.getMessage(), NotificationType.ERROR));
        }
    }

    private void scanComponents(Xray xray, Components artifactsToScan) throws IOException {
        ScanCache scanCache = ScanCache.getInstance(project);
        SummaryResponse summary = xray.summary().componentSummary(artifactsToScan);
        // Update cached artifact summary
        for (Artifact summaryArtifact : summary.getArtifacts()) {
            if (summaryArtifact == null || summaryArtifact.getGeneral() == null) {
                continue;
            }
            String componentId = summaryArtifact.getGeneral().getComponentId();
            scanCache.updateArtifact(componentId, summaryArtifact);
            scanCache.setLastUpdated(componentId);
        }
    }
}
