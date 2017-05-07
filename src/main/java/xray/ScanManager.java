package xray;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.jfrog.xray.client.Xray;
import com.jfrog.xray.client.impl.XrayClient;
import com.jfrog.xray.client.services.summary.Artifact;
import com.jfrog.xray.client.services.summary.SummaryResponse;
import configuration.JfrogGlobalSettings;
import configuration.XrayServerConfig;
import dependencies.ScanTreeNode;
import messages.XrayScanComponentsChange;
import messages.XrayScanFilterChange;
import messages.XrayScanIssuesChange;
import xray.persistency.ScanCache;
import xray.persistency.XrayArtifact;
import xray.persistency.XrayLicense;

import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by romang on 4/26/17.
 */
public abstract class ScanManager {

    protected final Project project;
    private final Xray xray;
    private TreeModel scanResults;

    protected ScanManager(Project project) {
        this.project = project;
        XrayServerConfig xrayConfig = JfrogGlobalSettings.getInstance().getXrayConfig();
        this.xray = XrayClient.create(xrayConfig.getUrl(), xrayConfig.getUsername(), xrayConfig.getPassword());
        registerFilterChangeHandler();
    }

    protected abstract TreeModel scanAndUpdateResults(TreeModel currentScanResults);

    private void registerFilterChangeHandler() {
        MessageBusConnection busConnection = project.getMessageBus().connect(project);
        busConnection.subscribe(XrayScanFilterChange.XRAY_SCAN_FILTER_CHANGE_TOPIC, () -> {
            MessageBus messageBus = project.getMessageBus();
            messageBus.syncPublisher(XrayScanComponentsChange.XRAY_SCAN_COMPONENTS_CHANGE_TOPIC).update();
            messageBus.syncPublisher(XrayScanIssuesChange.XRAY_SCAN_ISSUES_CHANGE_TOPIC).update();
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

    public void asyncUpdateResults() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            scanResults = scanAndUpdateResults(scanResults);
            MessageBus messageBus = project.getMessageBus();
            messageBus.syncPublisher(XrayScanComponentsChange.XRAY_SCAN_COMPONENTS_CHANGE_TOPIC).update();
        });
    }

    protected XrayArtifact scanArtifact(String checksum) {
        ScanCache scanCache = ScanCache.getInstance(project);
        XrayArtifact artifact = scanCache.getArtifact(checksum);
        LocalDateTime dateTime = scanCache.getLastUpdateTime(checksum);
        if (dateTime != null && LocalDateTime.now().minusWeeks(1).isBefore(dateTime)) {
            return artifact;
        }

        List<String> checksumList = new ArrayList<>();
        checksumList.add(checksum);
        try {
            SummaryResponse summary = xray.summary().artifactSummary(checksumList, null);
            scanCache.setLastUpdated(checksum);
            for (Artifact summaryArtifact : summary.getArtifacts()) {
                scanCache.updateArtifact(checksum, summaryArtifact);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scanCache.getArtifact(checksum);
    }

}
