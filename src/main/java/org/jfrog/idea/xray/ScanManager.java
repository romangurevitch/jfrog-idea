package org.jfrog.idea.xray;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.jfrog.xray.client.Xray;
import com.jfrog.xray.client.impl.XrayClient;
import com.jfrog.xray.client.services.summary.Artifact;
import com.jfrog.xray.client.services.summary.SummaryResponse;
import org.jfrog.idea.configuration.JfrogGlobalSettings;
import org.jfrog.idea.configuration.XrayServerConfig;
import org.jfrog.idea.xray.messages.ScanComponentsChange;
import org.jfrog.idea.xray.messages.ScanFilterChange;
import org.jfrog.idea.xray.messages.ScanIssuesChange;
import org.jetbrains.annotations.NotNull;
import org.jfrog.idea.xray.persistency.ScanCache;
import org.jfrog.idea.xray.persistency.XrayArtifact;
import org.jfrog.idea.xray.persistency.XrayLicense;

import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by romang on 4/26/17.
 */
public abstract class ScanManager {

    protected final Project project;
    private final Xray xray;
    private TreeModel scanResults;
    AtomicBoolean scanningInProgress = new AtomicBoolean(false);

    protected ScanManager(Project project) {
        this.project = project;
        XrayServerConfig xrayConfig = JfrogGlobalSettings.getInstance().getXrayConfig();
        this.xray = XrayClient.create(xrayConfig.getUrl(), xrayConfig.getUsername(), xrayConfig.getPassword());
        registerFilterChangeHandler();
    }

    protected abstract Set<String> collectArtifactsToScan();

    protected abstract TreeModel updateResultsTree(TreeModel currentScanResults);

    private void scanAndUpdate(boolean quickScan) {
        // Not allowing multiple scans
        if (!scanningInProgress.compareAndSet(false, true)) {
            return;
        }

        Set<String> artifactsToScan = collectArtifactsToScan();
        scanAndCacheArtifacs(artifactsToScan, quickScan);
        scanResults = updateResultsTree(scanResults);
        MessageBus messageBus = project.getMessageBus();
        messageBus.syncPublisher(ScanComponentsChange.SCAN_COMPONENTS_CHANGE_TOPIC).update();
        scanningInProgress.set(false);
    }

    public void asyncScanAndUpdateResults(boolean quickScan) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Xray: scanning for vulnerabilities...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                scanAndUpdate(quickScan);
                indicator.finishNonCancelableSection();
            }
        });
    }

    private void registerFilterChangeHandler() {
        MessageBusConnection busConnection = project.getMessageBus().connect(project);
        busConnection.subscribe(ScanFilterChange.SCAN_FILTER_CHANGE_TOPIC, () -> {
            MessageBus messageBus = project.getMessageBus();
            messageBus.syncPublisher(ScanComponentsChange.SCAN_COMPONENTS_CHANGE_TOPIC).update();
            messageBus.syncPublisher(ScanIssuesChange.SCAN_ISSUES_CHANGE_TOPIC).update();
        });
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

    public XrayArtifact getArtifactSummary(String checksum) {
        ScanCache scanCache = ScanCache.getInstance(project);
        return scanCache.getArtifact(checksum);
    }

    private void scanAndCacheArtifacs(Set<String> checksums, boolean quickScan) {
        ArrayList<String> artifactsToScan = new ArrayList<>(checksums);
        ScanCache scanCache = ScanCache.getInstance(project);
        if (quickScan) {
            for (String checksum : checksums) {
                LocalDateTime dateTime = scanCache.getLastUpdateTime(checksum);
                if (dateTime != null && LocalDateTime.now().minusWeeks(1).isBefore(dateTime)) {
                    artifactsToScan.remove(checksum);
                }
            }
        }

        if (artifactsToScan.isEmpty()) {
            return;
        }

        try {
            SummaryResponse summary = xray.summary().artifactSummary(artifactsToScan, null);
            // Update cached artifact summary
            for (Artifact summaryArtifact : summary.getArtifacts()) {
                if (summaryArtifact == null || summaryArtifact.getGeneral() == null) {
                    continue;
                }
                String checksum = summaryArtifact.getGeneral().getSha256();
                scanCache.updateArtifact(checksum, summaryArtifact);
            }
            // Update cached scan time
            for (String checksum : checksums) {
                scanCache.setLastUpdated(checksum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
