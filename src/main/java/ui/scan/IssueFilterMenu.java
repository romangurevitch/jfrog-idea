package ui.scan;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ex.CheckboxAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.messages.MessageBus;
import messages.XrayScanFilterChange;
import xray.FilterManager;
import xray.persistency.Severity;

/**
 * Created by romang on 4/13/17.
 */
public class IssueFilterMenu extends FilterMenu {

    protected IssueFilterMenu() {
        super("Issues filter:");
    }

    @Override
    protected DefaultActionGroup createActionGroup() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        for (Severity severity : Severity.values()) {
            actionGroup.add(new CheckboxAction(StringUtil.toTitleCase(severity.toString())) {
                @Override
                public boolean isSelected(AnActionEvent e) {
                    try {
                        FilterManager filterManager = FilterManager.getInstance(e.getProject());
                        if (filterManager.selectedSeverity != null) {
                            return filterManager.selectedSeverity.contains(Severity.valueOf(e.getPresentation().getText().toLowerCase()));
                        }
                    } catch (NullPointerException e1) {

                    }
                    return false;
                }

                @Override
                public void setSelected(AnActionEvent e, boolean state) {
                    if (e.getProject() == null || e.getPresentation().getText() == null) {
                        return;
                    }

                    FilterManager filterManager = ServiceManager.getService(e.getProject(), FilterManager.class);
                    if (state) {
                        filterManager.selectedSeverity.add(Severity.valueOf(e.getPresentation().getText().toLowerCase()));
                    } else {
                        filterManager.selectedSeverity.remove(Severity.valueOf(e.getPresentation().getText().toLowerCase()));
                    }


                    MessageBus messageBus = e.getProject().getMessageBus();
                    messageBus.syncPublisher(XrayScanFilterChange.XRAY_SCAN_FILTER_CHANGE_TOPIC).update();
                }
            });
        }
        return actionGroup;
    }
}
