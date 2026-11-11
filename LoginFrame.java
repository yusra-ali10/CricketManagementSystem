import javax.swing.*;
import java.awt.*;

/** Login screen. Admin: admin / admin123 (full access)   User: user / user123 (view only) */
public class LoginFrame extends JFrame {
    private final JTextField userF = new JTextField();
    private final JPasswordField passF = new JPasswordField();

    public LoginFrame() {
        setTitle("Cricket Management System - Login");
        setSize(780, 510);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        ImageIcon logo = Imgs.icon("logo.png", 64, 64);
        if (logo != null) setIconImage(logo.getImage());
        setLayout(new GridLayout(1, 2));

        add(new JLabel(Imgs.icon("login_side.png", 390, 480)));

        JPanel right = new JPanel(null);
        right.setBackground(Color.WHITE);

        JLabel lg = new JLabel(Imgs.icon("logo.png", 70, 70));
        lg.setBounds(160, 30, 70, 70);
        right.add(lg);

        JLabel title = new JLabel("Login", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(15, 23, 42));
        title.setBounds(0, 105, 390, 40);
        right.add(title);

        JLabel sub = new JLabel("Cricket Management System", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);
        sub.setBounds(0, 143, 390, 20);
        right.add(sub);

        JLabel ul = new JLabel("Username");
        ul.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ul.setBounds(50, 185, 290, 20);
        right.add(ul);
        userF.setBounds(50, 208, 290, 34);
        right.add(userF);

        JLabel pl = new JLabel("Password");
        pl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pl.setBounds(50, 252, 290, 20);
        right.add(pl);
        passF.setBounds(50, 275, 290, 34);
        right.add(passF);

        JCheckBox show = new JCheckBox("Show password");
        show.setOpaque(false);
        show.setBounds(50, 315, 200, 22);
        char echo = passF.getEchoChar();
        show.addActionListener(e -> passF.setEchoChar(show.isSelected() ? (char) 0 : echo));
        right.add(show);

        JButton login = new JButton("Login");
        login.setBackground(new Color(22, 163, 74));
        login.setForeground(Color.WHITE);
        login.setFont(new Font("Segoe UI", Font.BOLD, 15));
        login.setFocusPainted(false);
        login.setBorderPainted(false);
        login.setOpaque(true);
        login.setBounds(50, 350, 290, 42);
        login.addActionListener(e -> doLogin());
        right.add(login);
        getRootPane().setDefaultButton(login);

        JLabel hint = new JLabel("<html><center>Admin: admin / admin123 (full access)<br>User: user / user123 (view only)</center></html>", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(Color.GRAY);
        hint.setBounds(0, 405, 390, 40);
        right.add(hint);

        add(right);
    }

    private void doLogin() {
        String u = userF.getText().trim();
        String p = new String(passF.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
        } else if (u.equals("admin") && p.equals("admin123")) {
            dispose();
            new MainFrame("Admin").setVisible(true);
        } else if (u.equals("user") && p.equals("user123")) {
            dispose();
            new MainFrame("User").setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Wrong username or password.", "Login failed",
                    JOptionPane.ERROR_MESSAGE);
            passF.setText("");
        }
    }
}
