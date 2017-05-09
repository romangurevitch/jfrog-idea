package org.jfrog.idea.xray;

import com.intellij.openapi.project.Project;
import com.jfrog.xray.client.Xray;
import com.jfrog.xray.client.impl.XrayClient;
import com.sun.istack.NotNull;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jfrog.idea.configuration.JfrogGlobalSettings;
import org.jfrog.idea.configuration.XrayServerConfig;
import org.jfrog.idea.xray.generic.DefaultScanManager;
import org.jfrog.idea.xray.maven.MavenScanManager;

/**
 * Created by romang on 3/2/17.
 */
public class ScanManagerFactory {
    private final Xray xray;
    Project project;
    private static ScanManager scanManager;

    public ScanManagerFactory(Project project) {
        this.project = project;
        XrayServerConfig xrayConfig = JfrogGlobalSettings.getInstance().getXrayConfig();
        this.xray = XrayClient.create(xrayConfig.getUrl(), xrayConfig.getUsername(), xrayConfig.getPassword());
    }

    public static ScanManager getScanManager(@NotNull Project project) {
        if (scanManager != null) {
            return scanManager;
        }

        //create the proper scan manager according to the project type.
        if (MavenProjectsManager.getInstance(project).hasProjects()) {
            scanManager = new MavenScanManager(project);
            return scanManager;
        }

        scanManager = new DefaultScanManager(project);
        return scanManager;
    }
}
