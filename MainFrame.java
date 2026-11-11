import javax.swing.*;

/** Window that hosts the application screen. */
public class MainFrame extends JFrame {
    public MainFrame(String user) {
        setTitle("Cricket Management System");
        setSize(1200, 740);
        setMinimumSize(new java.awt.Dimension(1050, 650));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        ImageIcon logo = Imgs.icon("logo.png", 64, 64);
        if (logo != null) setIconImage(logo.getImage());
        setContentPane(new AppPanel(user,
                () -> { dispose(); new LoginFrame().setVisible(true); },
                () -> System.exit(0)));
    }
}
