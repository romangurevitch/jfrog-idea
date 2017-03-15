package messages;

import com.intellij.util.messages.Topic;

import javax.swing.tree.TreeModel;

/**
 * Created by romang on 3/5/17.
 */
public interface XrayLicensesListener {

    Topic<XrayLicensesListener> XRAY_LICENSES_LISTENER_TOPIC = Topic.create("Changed files issues changed", XrayLicensesListener.class);

    /**
     * Called when the store of issues in changed files is modified. It is modified only as a result of a user action to analyse all changed files.
     */
    void update(TreeModel listData);

}
