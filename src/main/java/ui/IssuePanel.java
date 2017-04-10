package ui;

import com.intellij.ide.OccurenceNavigator;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

/**
 * Created by romang on 3/19/17.
 */
public class IssuePanel extends SimpleToolWindowPanel implements OccurenceNavigator, DataProvider, Disposable {

    public IssuePanel(boolean vertical) {
        super(vertical);
    }

    public IssuePanel(boolean vertical, boolean borderless) {
        super(vertical, borderless);
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
