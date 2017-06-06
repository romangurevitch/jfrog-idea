package org.jfrog.idea.xray.generic;

import com.intellij.openapi.project.Project;
import com.jfrog.xray.client.services.summary.Components;
import org.jfrog.idea.xray.ScanManager;

import javax.swing.tree.TreeModel;

/**
 * Created by romang on 4/26/17.
 */
public class DefaultScanManager extends ScanManager {
    public DefaultScanManager(Project project) {
        super(project);
    }

    @Override
    protected Components collectComponentsToScan() {
        return null;
    }

    @Override
    protected TreeModel updateResultsTree(TreeModel currentScanResults) {
        return null;
    }
}
