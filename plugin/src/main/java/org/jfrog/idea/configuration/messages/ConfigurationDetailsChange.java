package org.jfrog.idea.configuration.messages;

import com.intellij.util.messages.Topic;

/**
 * Created by romang on 3/5/17.
 */
public interface ConfigurationDetailsChange {

    Topic<ConfigurationDetailsChange> CONFIGURATION_DETAILS_CHANGE_TOPIC = Topic.create("Configuration details changed", ConfigurationDetailsChange.class);

    /**
     * Called when the store of issues in changed files is modified. It is modified only as a result of a user action to analyse all changed files.
     */
    void update();
}
