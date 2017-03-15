package xray;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import dependencies.maven.MavenArtifactsManager;

/**
 * Created by romang on 3/6/17.
 */
public class RefreshScanAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        MavenArtifactsManager.asyncUpdate(e.getProject());
    }
}
