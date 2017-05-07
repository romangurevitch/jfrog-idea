package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import xray.ScanManagerFactory;

/**
 * Created by romang on 3/6/17.
 */
public class RefreshScanAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        ScanManagerFactory.getScanManager(e.getProject()).asyncUpdateResults();
    }
}
