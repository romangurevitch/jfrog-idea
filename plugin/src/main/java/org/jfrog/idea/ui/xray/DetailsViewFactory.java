package org.jfrog.idea.ui.xray;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.UIUtil;
import org.jfrog.idea.xray.ScanTreeNode;
import org.jfrog.idea.xray.persistency.XrayIssue;
import org.jfrog.idea.xray.persistency.XrayLicense;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.ArrayList;

import static org.jfrog.idea.ui.utils.ComponentUtils.createDisabledTextLabel;
import static org.jfrog.idea.ui.utils.ComponentUtils.createJTextArea;

/**
 * Created by romang on 5/4/17.
 */
public class DetailsViewFactory extends JBPanel {

    public static void createDetailsView(JBPanel panel, XrayIssue issue) {
        if (issue == null) {
            return;
        }

        JBPanel gridPanel = new JBPanel(new GridBagLayout());
        gridPanel.setBackground(UIUtil.getTableBackground());
        addJlabel(gridPanel, "Issue Details");
        addJtext(gridPanel, 1, "Summary:", issue.summary);
        addJtext(gridPanel, 2, "Severity:", issue.sevirity);
        addJtext(gridPanel, 3, "Issue Type:", StringUtil.capitalize(issue.issueType));
        addJtext(gridPanel, 4, "Description:", issue.description);
        addJtext(gridPanel, 5, "Provider:", issue.provider);
        LocalDateTime created = LocalDateTime.parse(issue.created, DateTimeFormatter.ISO_DATE_TIME);
        addJtext(gridPanel, 6, "Created:", created.format(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm")));
        replaceAndRevalidate(panel, gridPanel, BorderLayout.NORTH);
    }

    public static void createDetailsView(JBPanel panel, ScanTreeNode node) {
        if (node == null || node.getGeneralInfo() == null) {
            replaceAndRevalidate(panel, createDisabledTextLabel("Component information is not available"),
                    BorderLayout.CENTER);
            return;
        }

        ArrayList<String> licenses = new ArrayList<>();
        for (XrayLicense xrayLicense : node.getLicenses()) {
            licenses.add(xrayLicense.fullName);
        }

        JBPanel gridPanel = new JBPanel(new GridBagLayout());
        gridPanel.setBackground(UIUtil.getTableBackground());
        addJlabel(gridPanel, "Component Details");
        addJtext(gridPanel, 1, "Component ID:", node.getGeneralInfo().componentId);
        addJtext(gridPanel, 2, "Component Name:", node.getGeneralInfo().name);
        addJtext(gridPanel, 3, "Path:", node.getGeneralInfo().path);
        addJtext(gridPanel, 4, "Package type:", node.getGeneralInfo().pkgType);
        addJtext(gridPanel, 5, "SHA256:", node.getGeneralInfo().sha256);
        addJtext(gridPanel, 6, "License:", StringUtil.join(licenses, ", "));

        replaceAndRevalidate(panel, gridPanel, BorderLayout.NORTH);
    }

    private static void replaceAndRevalidate(JBPanel panel, JComponent component, Object constraint) {
        panel.removeAll();
        panel.add(component, constraint);
        panel.revalidate();
        panel.repaint();
    }

    private static void addJtext(JBPanel panel, int place, String header, String text) {
        JBLabel headerLabel = new JBLabel(header);
        headerLabel.setBackground(UIUtil.getTableBackground());
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.ipadx = 20;
        c.ipady = 3;

        c.gridy = place;
        panel.add(headerLabel, c);

        c.gridx = 1;
        c.weightx = 0.9;
        panel.add(createJTextArea(text), c);
    }

    private static void addJlabel(JBPanel gridPanel, String text) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.ipadx = 20;
        c.ipady = 3;
        c.gridwidth = 2;
        gridPanel.add(createDisabledTextLabel(text), c);
    }

}
