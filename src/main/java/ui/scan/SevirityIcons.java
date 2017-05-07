package ui.scan;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by romang on 4/12/17.
 */
public class SevirityIcons {
    public static Icon load(String severity) {
        try {
            return IconLoader.getIcon("/icons/" + severity.toLowerCase() + ".png");
        } catch (Exception e) {
            return IconLoader.getIcon("/icons/default.png");
        }
    }
}
