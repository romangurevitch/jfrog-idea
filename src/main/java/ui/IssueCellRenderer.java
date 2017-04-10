package ui;

import com.jfrog.xray.client.services.summary.Issue;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * Created by romang on 3/26/17.
 */
public class IssueCellRenderer implements ListCellRenderer<Issue> {

    @Override
    public Component getListCellRendererComponent(JList<? extends Issue> list, Issue value, int index, boolean isSelected, boolean cellHasFocus) {
//        JPanel panel = new JPanel(new GridBagLayout());
//        GridBagConstraints c = new GridBagConstraints();
//
//        JLabel summaryField = new JLabel(value.getSummary());
//        JTextArea descField = new JTextArea(value.getDescription());
//
//        MatteBorder border = BorderFactory.createMatteBorder(1, 5, 1, 1, Color.RED);
//        panel.setBorder(border);
//
//        descField.setOpaque(true);
//        descField.setLineWrap(true);
//        descField.setWrapStyleWord(true);
//        descField.setBorder(BorderFactory.createEmptyBorder());
//
//        summaryField.setOpaque(true);
//        summaryField.setBorder(BorderFactory.createEmptyBorder());
//
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.weightx = 1.0;
//        c.gridx = 0;
//        c.gridy = 0;
//        panel.add(summaryField, c);
//        c.gridx = 0;
//        c.gridy = 1;
//        panel.add(descField, c);
//
//        if (isSelected) {
//            summaryField.setForeground(list.getSelectionForeground());
//            summaryField.setBackground(list.getSelectionBackground());
//            descField.setForeground(list.getSelectionForeground());
//            descField.setBackground(list.getSelectionBackground());
////            panel.setForeground(list.getSelectionForeground());
////            panel.setBackground(list.getSelectionBackground());
//        } else {
//            summaryField.setForeground(list.getForeground());
//            summaryField.setBackground(list.getBackground());
//            descField.setForeground(list.getForeground());
//            descField.setBackground(list.getBackground());
////            panel.setForeground(list.getForeground());
////            panel.setBackground(list.getBackground());
//        }
//        panel.setMinimumSize(new Dimension(list.getWidth(), list.getWidth() / 2));
//        return panel;

        JPanel rootPanel = new JPanel(new GridBagLayout());


        Border raisedbevel = BorderFactory.createRaisedBevelBorder();
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        //        Border compound = BorderFactory.createCompoundBorder(new EmptyBorder(3, 0, 3, 0), loweredbevel);
        rootPanel.setBorder(loweredetched);

        JPanel panel = new JPanel(new GridBagLayout());

        JLabel summaryField = new JLabel(value.getSummary());
        summaryField.setBorder(new EmptyBorder(0, 10, 0, 0));
        JTextArea descField = new JTextArea();
        descField.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel severityField = new JLabel(value.getSeverity());
        severityField.setOpaque(true);
        JLabel issueTypeField = new JLabel(value.getIssueType());
        issueTypeField.setOpaque(true);

        severityField.setHorizontalAlignment(SwingConstants.CENTER);
        severityField.setUI(new VerticalLabelUI(false));

        issueTypeField.setHorizontalAlignment(SwingConstants.CENTER);
        issueTypeField.setUI(new VerticalLabelUI(false));

        descField.setText(value.getDescription());
        descField.setOpaque(true);
        descField.setEditable(false);

        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);
        summaryField.setOpaque(true);

        if (isSelected) {
            rootPanel.setPreferredSize(new Dimension(-1, 80));
            summaryField.setForeground(list.getSelectionForeground());
            summaryField.setBackground(list.getSelectionBackground());
            issueTypeField.setForeground(list.getSelectionForeground());
            issueTypeField.setBackground(list.getSelectionBackground());
            severityField.setForeground(list.getSelectionForeground());
            severityField.setBackground(list.getSelectionBackground());
            descField.setForeground(list.getSelectionForeground());
            descField.setBackground(list.getSelectionBackground());
//            panel.setForeground(list.getSelectionForeground());
//            panel.setBackground(list.getSelectionBackground());
            rootPanel.setForeground(list.getSelectionForeground());
            rootPanel.setBackground(list.getSelectionBackground());

        } else {
            rootPanel.setPreferredSize(new Dimension(-1, 40));
            summaryField.setForeground(list.getForeground());
            summaryField.setBackground(list.getBackground());
            issueTypeField.setForeground(list.getForeground());
            issueTypeField.setBackground(list.getBackground());
            severityField.setForeground(list.getForeground());
            severityField.setBackground(list.getBackground());
            descField.setForeground(list.getForeground());
            descField.setBackground(list.getBackground());
//            panel.setForeground(list.getForeground());
//            panel.setBackground(list.getBackground());
            rootPanel.setForeground(list.getForeground());
            rootPanel.setBackground(list.getBackground());
        }

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        panel.add(summaryField, c);

        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(descField, c);
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        rootPanel.add(severityField, c);

        c.gridx = 1;
        rootPanel.add(issueTypeField, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.ipadx = 5;
        rootPanel.add(panel, c);
        return rootPanel;
    }
}
