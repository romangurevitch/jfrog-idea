package org.jfrog.idea.xray;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

/**
 * Created by romang on 3/2/17.
 */
public class XrayStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        ScanManagerFactory.getScanManager(project).asyncScanAndUpdateResults(true);
    }
}
