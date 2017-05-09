package org.jfrog.idea.xray.messages;

import com.intellij.util.messages.Topic;

/**
 * Created by romang on 3/5/17.
 */
public interface ScanComponentsChange {

    Topic<ScanComponentsChange> SCAN_COMPONENTS_CHANGE_TOPIC = Topic.create("Component view changed", ScanComponentsChange.class);

    /**
     * Called when the store of issues in changed files is modified. It is modified only as a result of a user action to analyse all changed files.
     */
    void update();

}
