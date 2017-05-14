package org.jfrog.idea.xray;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.sun.istack.NotNull;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jfrog.idea.xray.generic.DefaultScanManager;
import org.jfrog.idea.xray.maven.MavenScanManager;

/**
 * Created by romang on 3/2/17.
 */
public class ScanManagerFactory {
    private ScanManager scanManager;

    public static ScanManager getScanManager(@NotNull Project project) {
        ScanManagerFactory scanManagerFactory = ServiceManager.getService(project, ScanManagerFactory.class);

        if (scanManagerFactory.scanManager != null) {
            return scanManagerFactory.scanManager;
        }

        //create the proper scan manager according to the project type.
        if (MavenProjectsManager.getInstance(project).hasProjects()) {
            scanManagerFactory.scanManager = new MavenScanManager(project);
            return scanManagerFactory.scanManager;
        }

        scanManagerFactory.scanManager = new DefaultScanManager(project);
        return scanManagerFactory.scanManager;
    }
}
