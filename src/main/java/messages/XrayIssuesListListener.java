package messages;

import com.intellij.util.messages.Topic;

import javax.swing.*;
import javax.swing.tree.TreeModel;

/**
 * Created by romang on 3/5/17.
 */
public interface XrayIssuesListListener {

    Topic<XrayIssuesListListener> XRAY_ISSUES_LIST_LISTENER_TOPIC = Topic.create("Changed files issues changed", XrayIssuesListListener.class);

    /**
     * Called when the store of issues in changed files is modified. It is modified only as a result of a user action to analyse all changed files.
     */
    void update(ListModel listData);

}
