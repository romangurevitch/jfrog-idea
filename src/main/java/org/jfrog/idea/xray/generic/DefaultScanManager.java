package org.jfrog.idea.xray.generic;

import com.intellij.openapi.project.Project;
import org.jfrog.idea.xray.ScanManager;

import javax.swing.tree.TreeModel;
import java.util.Set;

/**
 * Created by romang on 4/26/17.
 */
public class DefaultScanManager extends ScanManager {
    public DefaultScanManager(Project project) {
        super(project);
    }

    @Override
    protected Set<String> collectArtifactsToScan() {
        return null;
    }

    @Override
    protected TreeModel updateResultsTree(TreeModel currentScanResults) {
        return null;
    }
}
