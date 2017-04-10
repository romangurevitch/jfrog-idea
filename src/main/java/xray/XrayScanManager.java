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
}
