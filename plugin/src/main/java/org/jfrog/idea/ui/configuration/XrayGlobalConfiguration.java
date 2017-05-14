package org.jfrog.idea.ui.configuration;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.jfrog.xray.client.Xray;
import com.jfrog.xray.client.impl.XrayClient;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import org.jfrog.idea.configuration.JfrogGlobalSettings;
import org.jfrog.idea.configuration.XrayServerConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by romang on 1/29/17.
 */
public class XrayGlobalConfiguration implements Configurable, Configurable.NoScroll {

    private JPanel config;
    private JBTextField url;
    private JBTextField username;
    private JBPasswordField password;
    private JButton testConnectionButton;
    private JLabel connectionResults;
    private XrayServerConfig xrayConfig;

    public XrayGlobalConfiguration() {
        testConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            connectionResults.setText("Connecting Xray...");
                            config.updateUI();
                            // use as a workaround to version not being username password validated
                            Xray xrayClient = XrayClient.create(url.getText(), username.getText(), String.valueOf(password.getPassword()));
                            xrayClient.binaryManagers().artifactoryConfigurations();
                            connectionResults.setText("Successfully connected to Xray version: " + xrayClient.system().version().getVersion());
                        } catch (IOException | IllegalArgumentException e1) {
                            connectionResults.setText("Could not connect to Xray: " + e1.getMessage());
                        }
                        config.updateUI();
                    }
                });
            }
        });
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "JFrog Xray configuration";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "Setup page for JFrog Xray URL and credentials";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return config;
    }

    @Override
    public boolean isModified() {
        xrayConfig = XrayServerConfig.newBuilder()
                .setUrl(url.getText())
                .setUsername(username.getText())
                .setPassword(String.valueOf(password.getPassword()))
                .build();

        return !xrayConfig.equals(JfrogGlobalSettings.getInstance().getXrayConfig());
    }

    @Override
    public void apply() throws ConfigurationException {
        JfrogGlobalSettings jfrogGlobalSettings = JfrogGlobalSettings.getInstance();
        jfrogGlobalSettings.setXrayConfig(xrayConfig);
        connectionResults.setText("");
    }

    @Override
    public void reset() {
        loadConfig();
    }

    @Override
    public void disposeUIResources() {

    }

    private void loadConfig() {
        url.getEmptyText().setText("Example: http://localhost:9000");
        connectionResults.setText("");

        xrayConfig = JfrogGlobalSettings.getInstance().getXrayConfig();
        if (xrayConfig != null) {
            url.setText(xrayConfig.getUrl());
            username.setText(xrayConfig.getUsername());
            password.setText(xrayConfig.getPassword());
        }
    }

    private void createUIComponents() {
        xrayConfig = JfrogGlobalSettings.getInstance().getXrayConfig();
        url = new JBTextField();
        username = new JBTextField();
        password = new JBPasswordField();

        loadConfig();
    }
}
