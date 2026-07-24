package theme;

import javax.swing.*;
import java.awt.Font;
import java.awt.Frame;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;

public class ThemeManager {

    private static boolean darkTheme = false;

    public static void init() {
        // Set rounded corners globally
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 12);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("CheckBox.arc", 4);
        UIManager.put("ProgressBar.arc", 8);
        
        // Modern spacing
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.trackArc", 999);
        
        // Font setup
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("defaultFont", baseFont);

        applyTheme(darkTheme);
    }

    public static void applyTheme(boolean isDark) {
        darkTheme = isDark;
        try {
            if (isDark) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                UIManager.put("Panel.background", new java.awt.Color(30, 30, 30));
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
                UIManager.put("Panel.background", new java.awt.Color(245, 246, 248));
            }
            
            // Re-apply properties to take effect
            for (Frame frame : Frame.getFrames()) {
                SwingUtilities.updateComponentTreeUI(frame);
            }
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf theme");
            ex.printStackTrace();
        }
    }

    public static boolean isDark() {
        return darkTheme;
    }
}
