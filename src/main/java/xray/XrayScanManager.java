package xray;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.jfrog.xray.client.Xray;
import com.jfrog.xray.client.services.summary.SummaryResponse;
import com.sun.istack.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by romang on 3/2/17.
 */
public class XrayScanManager {
    private final MessageBus messageBus;
    private ArtifactsSummary xrayArtifactsSummary;
    private Project project;
    private AtomicBoolean scanning = new AtomicBoolean();

    public XrayScanManager(Project project) {
        this.project = project;
        messageBus = project.getMessageBus();
    }

    public static XrayScanManager getInstance(@NotNull Project project) {
        return (XrayScanManager) ServiceManager.getService(project, XrayScanManager.class);
    }

    public synchronized void asyncScanAndUpdate(List<String> checksumList) {
        if (!scanning.compareAndSet(false, true)) {
            return;
        }
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            public void run() {
                try {
                    Xray xray = ClientUtils.createClient();
                    SummaryResponse summary = xray.summary().artifactSummary(checksumList, null);
                    xrayArtifactsSummary = new ArtifactsSummary(summary);
//                    updateXrayWindowTool();
                    scanning.set(false);
                } catch (IOException e) {

                }
            }
        });
    }

//    private void updateXrayWindowTool() {
//        Vector<Pair<String, String>> xrayScanResult = new Vector<>();
//        SummaryResponse summary = xrayArtifactsSummary.getSummary();
//        for (Artifact artifact : summary.getArtifacts()) {
//            for (Issue issue : artifact.getIssues()) {
//                xrayScanResult.add(Pair.create(artifact.getGeneral().getComponentId(), issue.getSummary()));
//            }
//        }
//        this.messageBus.syncPublisher(XrayIssuesListener.XRAY_ISSUES_LISTENER_TOPIC).update(xrayScanResult);
//    }

}
