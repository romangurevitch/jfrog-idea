package messages;

import com.intellij.util.messages.Topic;

/**
 * Created by romang on 3/5/17.
 */
public interface XrayScanIssuesChange {

    Topic<XrayScanIssuesChange> XRAY_SCAN_ISSUES_CHANGE_TOPIC = Topic.create("Changed files issues changed", XrayScanIssuesChange.class);

    /**
     * Called when the store of issues in changed files is modified. It is modified only as a result of a user action to analyse all changed files.
     */
    void update();
}
