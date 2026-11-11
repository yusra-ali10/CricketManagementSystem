import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Home page: banner, summary cards and three live charts / lists. */
public class DashboardPage extends JPanel {
    private static final String[] ROLES = {"Batsman", "Bowler", "All-rounder", "Wicket-keeper"};
    private static final Color[] ROLE_COLORS = {Theme.BLUE, Theme.RED, Theme.GREEN, Theme.AMBER};

    private final Store s;
    private final String user;
    private final JLabel playersL = new JLabel("0");
    private final JLabel runsL = new JLabel("0");
    private final JLabel wicketsL = new JLabel("0");
    private final JLabel matchesL = new JLabel("0");
    private final BufferedImage bannerImg = Imgs.image("banner.png");
    private final JPanel bars, donut, activity;

    public DashboardPage(Store s, String user) {
        this.s = s;
        this.user = user;
        setLayout(new BorderLayout(0, 15));
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel banner = new JPanel() {
            protected void paintComponent(Graphics g0) {
                super.paintComponent(g0);
                Graphics2D g = (Graphics2D) g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                if (bannerImg != null) g.drawImage(bannerImg, 0, 0, getWidth(), getHeight(), null);
                else { g.setColor(Theme.GREEN); g.fillRect(0, 0, getWidth(), getHeight()); }
                int h = LocalTime.now().getHour();
                String hello = h < 12 ? "Good Morning" : h < 17 ? "Good Afternoon" : "Good Evening";
                g.setColor(Color.WHITE);
                g.setFont(Theme.font(Font.BOLD, 28));
                g.drawString(hello + ", " + DashboardPage.this.user + "!", 25, 52);
                g.setFont(Theme.font(Font.PLAIN, 15));
                g.drawString(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")), 25, 80);
                g.drawString("Cricket Management System  |  built with Linked Lists", 25, 104);
            }
        };
        banner.setPreferredSize(new Dimension(0, 125));

        JPanel row = new JPanel(new GridLayout(1, 4, 15, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 92));
        row.add(Theme.statCard("Total Players", playersL, "icon_players.png", Theme.BLUE));
        row.add(Theme.statCard("Total Runs", runsL, "icon_runs.png", Theme.GREEN));
        row.add(Theme.statCard("Total Wickets", wicketsL, "icon_wickets.png", Theme.AMBER));
        row.add(Theme.statCard("Upcoming Matches", matchesL, "icon_matches.png", Theme.PURPLE));

        JPanel top = new JPanel(new BorderLayout(0, 15));
        top.setOpaque(false);
        top.add(banner, BorderLayout.NORTH);
        top.add(row, BorderLayout.CENTER);

        bars = new JPanel() {
            protected void paintComponent(Graphics g) { super.paintComponent(g); paintBars((Graphics2D) g, getWidth(), getHeight()); }
        };
        donut = new JPanel() {
            protected void paintComponent(Graphics g) { super.paintComponent(g); paintDonut((Graphics2D) g, getWidth(), getHeight()); }
        };
        activity = new JPanel() {
            protected void paintComponent(Graphics g) { super.paintComponent(g); paintActivity((Graphics2D) g, getWidth(), getHeight()); }
        };
        bars.setBackground(Color.WHITE);
        donut.setBackground(Color.WHITE);
        activity.setBackground(Color.WHITE);

        JPanel lower = new JPanel(new GridLayout(1, 3, 15, 0));
        lower.setOpaque(false);
        lower.add(bars);
        lower.add(donut);
        lower.add(activity);

        add(top, BorderLayout.NORTH);
        add(lower, BorderLayout.CENTER);
    }

    public void refresh() {
        playersL.setText(String.valueOf(s.players.count()));
        runsL.setText(String.valueOf(s.players.totalRuns()));
        wicketsL.setText(String.valueOf(s.players.totalWickets()));
        matchesL.setText(String.valueOf(s.matches.countByStatus("Upcoming")));
        bars.repaint();
        donut.repaint();
        activity.repaint();
    }

    private void heading(Graphics2D g, String text) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Theme.NAVY);
        g.setFont(Theme.font(Font.BOLD, 16));
        g.drawString(text, 20, 30);
    }

    /** Top 5 run scorers. */
    private void paintBars(Graphics2D g, int w, int h) {
        heading(g, "Top 5 Run Scorers");
        Player[] top = s.players.top(5);
        if (top[0] == null) { g.setFont(Theme.font(Font.PLAIN, 13)); g.setColor(Color.GRAY); g.drawString("No players yet.", 20, 60); return; }
        int max = Math.max(1, top[0].runs);
        int y0 = 48, rowH = Math.max(34, (h - y0 - 12) / 5);
        int barMax = Math.max(40, w - 40);
        Color[] cols = {Theme.GREEN, new Color(34, 197, 94), Theme.BLUE, new Color(96, 165, 250), new Color(148, 163, 184)};
        for (int i = 0; i < 5 && top[i] != null; i++) {
            Player p = top[i];
            int by = y0 + i * rowH;
            g.setFont(Theme.font(Font.BOLD, 13));
            g.setColor(Theme.NAVY);
            g.drawString(p.name, 20, by + 14);
            g.setColor(Theme.MUTED);
            g.drawString(p.runs + " runs", w - 20 - g.getFontMetrics().stringWidth(p.runs + " runs"), by + 14);
            int bw = (int) ((long) p.runs * barMax / max);
            g.setColor(new Color(226, 232, 240));
            g.fillRoundRect(20, by + 20, barMax, 10, 10, 10);
            g.setColor(cols[i]);
            g.fillRoundRect(20, by + 20, Math.max(bw, 8), 10, 10, 10);
        }
    }

    /** Players per role as a donut chart. */
    private void paintDonut(Graphics2D g, int w, int h) {
        heading(g, "Players by Role");
        int total = s.players.count();
        int legendH = 4 * 22 + 8;
        int d = Math.min(w - 50, h - 50 - legendH);
        if (d < 40) return;
        int x = (w - d) / 2, y = 44;
        if (total == 0) { g.setColor(new Color(226, 232, 240)); g.fillOval(x, y, d, d); }
        else {
            double start = 90;
            int[] counts = new int[ROLES.length];
            int used = 0;
            int last = 0;
            for (int i = 0; i < ROLES.length; i++) {
                counts[i] = s.players.countByRole(ROLES[i]);
                if (counts[i] > 0) last = i;
            }
            for (int i = 0; i < ROLES.length; i++) {
                if (counts[i] == 0) continue;
                // the last non-empty slice takes the remaining angle, so the circle closes exactly
                int angle = (i == last) ? 360 - used : (int) Math.round(counts[i] * 360.0 / total);
                g.setColor(ROLE_COLORS[i]);
                g.fillArc(x, y, d, d, (int) start, -angle);
                start -= angle;
                used += angle;
            }
        }
        int hole = (int) (d * 0.58);
        g.setColor(Color.WHITE);
        g.fillOval(x + (d - hole) / 2, y + (d - hole) / 2, hole, hole);
        g.setColor(Theme.NAVY);
        g.setFont(Theme.font(Font.BOLD, 26));
        String t = String.valueOf(total);
        g.drawString(t, x + (d - g.getFontMetrics().stringWidth(t)) / 2, y + d / 2 + 6);
        g.setFont(Theme.font(Font.PLAIN, 12));
        g.setColor(Theme.MUTED);
        g.drawString("Players", x + (d - g.getFontMetrics().stringWidth("Players")) / 2, y + d / 2 + 24);
        int ly = y + d + 22;
        for (int i = 0; i < ROLES.length; i++) {
            g.setColor(ROLE_COLORS[i]);
            g.fillRoundRect(24, ly + i * 22 - 11, 12, 12, 4, 4);
            g.setColor(Theme.NAVY);
            g.setFont(Theme.font(Font.PLAIN, 13));
            g.drawString(ROLES[i], 44, ly + i * 22);
            String c = String.valueOf(s.players.countByRole(ROLES[i]));
            g.drawString(c, w - 24 - g.getFontMetrics().stringWidth(c), ly + i * 22);
        }
    }

    /** Latest actions, read from the activity linked list. */
    private void paintActivity(Graphics2D g, int w, int h) {
        heading(g, "Recent Activity");
        int y = 56;
        for (Activity a = s.log.getHead(); a != null && y < h - 10; a = a.next) {
            g.setColor(Theme.GREEN);
            g.fillOval(20, y - 9, 10, 10);
            g.setColor(Theme.NAVY);
            g.setFont(Theme.font(Font.BOLD, 13));
            g.drawString(clip(g, a.text, w - 60), 40, y);
            g.setColor(Theme.MUTED);
            g.setFont(Theme.font(Font.PLAIN, 11));
            g.drawString(a.time, 40, y + 15);
            y += 40;
        }
    }

    private String clip(Graphics2D g, String t, int maxW) {
        FontMetrics fm = g.getFontMetrics();
        while (t.length() > 4 && fm.stringWidth(t) > maxW) t = t.substring(0, t.length() - 4) + "...";
        return t;
    }
}
