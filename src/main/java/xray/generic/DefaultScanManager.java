package xray.generic;

import com.intellij.openapi.project.Project;
import dependencies.ScanTreeNode;
import xray.ScanManager;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 * Created by romang on 4/26/17.
 */
public class DefaultScanManager extends ScanManager {
    public DefaultScanManager(Project project) {
        super(project);
    }

    @Override
    protected TreeModel scanAndUpdateResults(TreeModel currentScanResults) {
        return new DefaultTreeModel(new ScanTreeNode("All Components"), false);
    }
}
