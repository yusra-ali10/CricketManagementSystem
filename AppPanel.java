import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** The whole application screen: sidebar + pages. */
public class AppPanel extends JPanel {
    private final Store store = new Store();
    private final CardLayout cards = new CardLayout();
    private final JPanel pages = new JPanel(cards);
    private final DashboardPage dashboard;
    private final PlayersPage players;
    private final MatchesPage matches;
    private final TeamsPage teams;
    private final JButton[] nav = new JButton[4];
    private final String[] names = {"dashboard", "players", "matches", "teams"};

    public AppPanel(String user, Runnable onLogout, Runnable onExit) {
        boolean canEdit = user.equals("Admin");
        setLayout(new BorderLayout());

        dashboard = new DashboardPage(store, user);
        players = new PlayersPage(store, canEdit, this::refreshAll);
        matches = new MatchesPage(store, canEdit, this::refreshAll);
        teams = new TeamsPage(store);
        pages.add(dashboard, names[0]);
        pages.add(players, names[1]);
        pages.add(matches, names[2]);
        pages.add(teams, names[3]);

        add(buildSidebar(user, onLogout, onExit), BorderLayout.WEST);
        add(pages, BorderLayout.CENTER);
        refreshAll();
        showPage(0);
    }

    private void refreshAll() {
        store.saveAll();
        dashboard.refresh();
        players.refresh();
        matches.refresh();
        teams.refresh();
    }

    private void showPage(int i) {
        cards.show(pages, names[i]);
        for (int k = 0; k < nav.length; k++) nav[k].setBackground(k == i ? Theme.GREEN : Theme.NAVY);
    }

    private JPanel buildSidebar(String user, Runnable onLogout, Runnable onExit) {
        JPanel side = new JPanel(new BorderLayout());
        side.setBackground(Theme.NAVY);
        side.setPreferredSize(new Dimension(230, 0));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.setBorder(new EmptyBorder(25, 10, 20, 10));
        JLabel lg = new JLabel(Imgs.icon("logo.png", 80, 80));
        lg.setAlignmentX(0.5f);
        JLabel t1 = new JLabel("CRICKET");
        t1.setFont(Theme.font(Font.BOLD, 22));
        t1.setForeground(Color.WHITE);
        t1.setAlignmentX(0.5f);
        JLabel t2 = new JLabel("MANAGEMENT SYSTEM");
        t2.setFont(Theme.font(Font.PLAIN, 11));
        t2.setForeground(new Color(148, 163, 184));
        t2.setAlignmentX(0.5f);
        head.add(lg);
        head.add(Box.createVerticalStrut(8));
        head.add(t1);
        head.add(t2);
        side.add(head, BorderLayout.NORTH);

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        String[] labels = {"Dashboard", "Players", "Matches", "Teams"};
        String[] icons = {"icon_home.png", "icon_players.png", "icon_matches.png", "icon_teams.png"};
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            nav[i] = navBtn(labels[i], icons[i]);
            nav[i].addActionListener(e -> showPage(idx));
            menu.add(nav[i]);
        }
        JButton logout = navBtn("Logout", "icon_exit.png");
        JButton exit = navBtn("Exit", "icon_exit.png");
        logout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Do you really want to logout?", "Logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) onLogout.run();
        });
        exit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Do you really want to exit?", "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) onExit.run();
        });
        menu.add(Box.createVerticalStrut(20));
        menu.add(logout);
        menu.add(exit);
        side.add(menu, BorderLayout.CENTER);

        JLabel foot = new JLabel("<html><center>Logged in as: <b>" + user + "</b><br>Data Structure: Linked List</center></html>", SwingConstants.CENTER);
        foot.setForeground(new Color(148, 163, 184));
        foot.setFont(Theme.font(Font.PLAIN, 11));
        foot.setBorder(new EmptyBorder(10, 0, 15, 0));
        side.add(foot, BorderLayout.SOUTH);
        return side;
    }

    private JButton navBtn(String text, String icon) {
        JButton b = new JButton(text, Imgs.icon(icon, 22, 22));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setIconTextGap(14);
        b.setForeground(Color.WHITE);
        b.setBackground(Theme.NAVY);
        b.setFont(Theme.font(Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(0, 22, 0, 10));
        b.setAlignmentX(0f);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
