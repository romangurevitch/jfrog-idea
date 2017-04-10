package ui;

import com.jfrog.xray.client.services.summary.Issue;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by romang on 3/26/17.
 */
public class IssueTableCellRenderer implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Issue issue = (Issue) value;

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel summaryField = new JLabel(issue.getSummary());
        JTextArea descField = new JTextArea(issue.getDescription());

        MatteBorder border = BorderFactory.createMatteBorder(1, 5, 1, 1, Color.RED);
        panel.setBorder(border);

        descField.setOpaque(true);
        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);
        descField.setBorder(BorderFactory.createEmptyBorder());

        summaryField.setOpaque(true);
        summaryField.setBorder(BorderFactory.createEmptyBorder());

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(summaryField, c);
        c.gridx = 0;
        c.gridy = 1;
        panel.add(descField, c);

        if (isSelected) {
            summaryField.setForeground(table.getSelectionForeground());
            summaryField.setBackground(table.getSelectionBackground());
            descField.setForeground(table.getSelectionForeground());
            descField.setBackground(table.getSelectionBackground());
//            panel.setForeground(list.getSelectionForeground());
//            panel.setBackground(list.getSelectionBackground());
        } else {
            summaryField.setForeground(table.getForeground());
            summaryField.setBackground(table.getBackground());
            descField.setForeground(table.getForeground());
            descField.setBackground(table.getBackground());
//            panel.setForeground(list.getForeground());
//            panel.setBackground(list.getBackground());
        }
//        panel.setMinimumSize(new Dimension(table.getWidth(), table.getWidth() / 2));
        return panel;
    }
}
