package org.jfrog.idea.xray.messages;

import com.intellij.util.messages.Topic;

/**
 * Created by romang on 3/5/17.
 */
public interface ScanFilterChange {

    Topic<ScanFilterChange> SCAN_FILTER_CHANGE_TOPIC = Topic.create("Scan filter changed", ScanFilterChange.class);

    /**
     * Called when the store of issues in changed files is modified. It is modified only as a result of a user action to analyse all changed files.
     */
    void update();

}
