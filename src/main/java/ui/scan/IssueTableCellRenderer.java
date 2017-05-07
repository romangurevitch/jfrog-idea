package ui.scan;

import xray.persistency.XrayIssue;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by romang on 3/26/17.
 */
public class IssueTableCellRenderer implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        XrayIssue issue = (XrayIssue) value;
        JLabel summaryl = new JLabel(issue.summary);
        summaryl.setIconTextGap(8);
        summaryl.setIcon(SevirityIcons.load(issue.sevirity));
        summaryl.setOpaque(true);
        summaryl.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 0));
        if (isSelected) {
            summaryl.setForeground(table.getSelectionForeground());
            summaryl.setBackground(table.getSelectionBackground());
        } else {
            summaryl.setForeground(table.getForeground());
            summaryl.setBackground(table.getBackground());
        }
        return summaryl;
    }
}
