package ui;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * Created by romang on 3/19/17.
 */
public class OneIssue extends JBPanel {
    private JBLabel summaryField = new JBLabel();
    private JTextArea descField = new JTextArea();

    OneIssue() {
        super(new BorderLayout());

        MatteBorder border = BorderFactory.createMatteBorder(1, 5, 1, 1, Color.RED);
        setBorder(border);

        summaryField.setOpaque(true);
        descField.setOpaque(true);

        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);

        summaryField.setBorder(BorderFactory.createEmptyBorder());
        descField.setBorder(BorderFactory.createEmptyBorder());

        this.add(summaryField, BorderLayout.NORTH);
        this.add(descField, BorderLayout.CENTER);
    }

    void setSummary(String summary) {
        summaryField.setText(summary);
    }

    void setDesc(String desc) {
        descField.setText(desc);
        descField.updateUI();
        descField.invalidate();
        updateUI();
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (summaryField != null) {
            summaryField.setBackground(bg);
        }
        if (descField != null) {
            descField.setBackground(bg);
        }
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (summaryField != null) {
            summaryField.setForeground(fg);
        }
        if (descField != null) {
            descField.setForeground(fg);
        }
    }
}
