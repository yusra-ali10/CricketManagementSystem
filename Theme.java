import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Colours, fonts and small helpers shared by all pages. */
public class Theme {
    static final Color NAVY = new Color(15, 23, 42);
    static final Color GREEN = new Color(22, 163, 74);
    static final Color BG = new Color(241, 245, 249);
    static final Color MUTED = new Color(100, 116, 139);
    static final Color BLUE = new Color(59, 130, 246);
    static final Color AMBER = new Color(245, 158, 11);
    static final Color RED = new Color(239, 68, 68);
    static final Color PURPLE = new Color(139, 92, 246);
    static final Color SLATE = new Color(100, 116, 139);

    static Font font(int style, int size) { return new Font("Segoe UI", style, size); }

    static JButton button(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(font(Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(font(Font.BOLD, 13));
        l.setForeground(NAVY);
        return l;
    }

    static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(font(Font.BOLD, 22));
        l.setForeground(NAVY);
        return l;
    }

    static void styleTable(JTable t, int rowHeight) {
        t.setRowHeight(rowHeight);
        t.setFont(font(Font.PLAIN, 14));
        t.getTableHeader().setFont(font(Font.BOLD, 14));
        t.setSelectionBackground(new Color(187, 247, 208));
        t.setSelectionForeground(Color.BLACK);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setGridColor(new Color(226, 232, 240));
    }

    static JPanel statCard(String label, JLabel value, String iconName, Color color) {
        JPanel c = new JPanel(new BorderLayout(12, 0));
        c.setBackground(color);
        c.setBorder(new EmptyBorder(12, 16, 12, 16));
        c.add(new JLabel(Imgs.icon(iconName, 40, 40)), BorderLayout.WEST);
        JPanel t = new JPanel(new GridLayout(2, 1));
        t.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(new Color(255, 255, 255, 225));
        l.setFont(font(Font.PLAIN, 13));
        value.setForeground(Color.WHITE);
        value.setFont(font(Font.BOLD, 24));
        t.add(l);
        t.add(value);
        c.add(t, BorderLayout.CENTER);
        return c;
    }
}
