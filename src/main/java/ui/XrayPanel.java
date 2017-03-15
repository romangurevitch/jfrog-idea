package ui;

import com.intellij.ide.OccurenceNavigator;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.content.Content;

/**
 * Created by romang on 3/7/17.
 */
public class XrayPanel extends SimpleToolWindowPanel implements OccurenceNavigator, DataProvider, Disposable {
    protected Project project;


    XrayPanel(Project project, Content content) {
        super(false, true);
        this.project = project;

    }

    public XrayPanel(boolean vertical) {
        super(vertical);
    }

    @Override
    public boolean hasNextOccurence() {
        return false;
    }

    @Override
    public boolean hasPreviousOccurence() {
        return false;
    }

    @Override
    public OccurenceInfo goNextOccurence() {
        return null;
    }

    @Override
    public OccurenceInfo goPreviousOccurence() {
        return null;
    }

    @Override
    public String getNextOccurenceActionName() {
        return null;
    }

    @Override
    public String getPreviousOccurenceActionName() {
        return null;
    }

    @Override
    public void dispose() {

    }
}
