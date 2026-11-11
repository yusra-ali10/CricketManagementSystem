import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Players page: CRUD, search, filter, sort, photos and report export on the PlayerList. */
public class PlayersPage extends JPanel {
    private static final String[] ROLES = {"Batsman", "Bowler", "All-rounder", "Wicket-keeper"};

    private final Store s;
    private final Runnable onChange;
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Photo", "ID", "Name", "Team", "Role", "Matches", "Runs", "Wickets"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
        public Class<?> getColumnClass(int c) { return c == 0 ? ImageIcon.class : Object.class; }
    };
    private final JTable table = new JTable(model);
    private final JTextField idF = new JTextField(), nameF = new JTextField(), teamF = new JTextField();
    private final JTextField matchesF = new JTextField(), runsF = new JTextField(), wicketsF = new JTextField();
    private final JTextField searchF = new JTextField(12);
    private final JComboBox<String> roleBox = new JComboBox<>(ROLES);
    private final JComboBox<String> filterBox = new JComboBox<>(new String[]{"All Roles", "Batsman", "Bowler", "All-rounder", "Wicket-keeper"});
    private final JComboBox<String> sortBox = new JComboBox<>(new String[]{"ID (low to high)", "Runs (high to low)", "Wickets (high to low)", "Name (A-Z)"});
    private final JLabel photoPreview = new JLabel();
    private File chosenPhoto = null;
    private String currentPhoto = "";

    public PlayersPage(Store s, boolean canEdit, Runnable onChange) {
        this.s = s;
        this.onChange = onChange;
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ---- tool bar
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tools.setOpaque(false);
        tools.add(Theme.title("Players"));
        tools.add(Box.createHorizontalStrut(10));
        tools.add(new JLabel("Search:"));
        tools.add(searchF);
        tools.add(filterBox);
        tools.add(new JLabel("Sort:"));
        tools.add(sortBox);
        JButton sortBtn = Theme.button("Sort", Theme.PURPLE);
        sortBtn.addActionListener(e -> { s.players.sort(sortBox.getSelectedIndex()); s.log.addFirst("Players sorted by " + sortBox.getSelectedItem()); onChange.run(); });
        JButton report = Theme.button("Export Report", Theme.GREEN);
        report.addActionListener(e -> exportReport());
        tools.add(sortBtn);
        tools.add(report);
        searchF.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }
        });
        filterBox.addActionListener(e -> refresh());
        add(tools, BorderLayout.NORTH);

        // ---- table
        Theme.styleTable(table, 54);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(1).setMaxWidth(60);
        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0 && !e.getValueIsAdjusting()) {
                Player p = s.players.search(Integer.parseInt(model.getValueAt(r, 1).toString()));
                if (p != null) fill(p);
            }
        });
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);

        // ---- form
        JPanel form = new JPanel(new BorderLayout(15, 8));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        JPanel photoBox = new JPanel(new BorderLayout(0, 6));
        photoBox.setOpaque(false);
        photoPreview.setPreferredSize(new Dimension(110, 110));
        photoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        photoPreview.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        JButton chooseBtn = Theme.button("Choose Photo", Theme.SLATE);
        chooseBtn.setEnabled(canEdit);
        chooseBtn.addActionListener(e -> choosePhoto());
        photoBox.add(photoPreview, BorderLayout.CENTER);
        photoBox.add(chooseBtn, BorderLayout.SOUTH);
        form.add(photoBox, BorderLayout.WEST);

        JPanel fields = new JPanel(new GridLayout(4, 4, 10, 2));
        fields.setOpaque(false);
        fields.add(Theme.label("ID")); fields.add(Theme.label("Name")); fields.add(Theme.label("Team")); fields.add(Theme.label("Role"));
        fields.add(idF); fields.add(nameF); fields.add(teamF); fields.add(roleBox);
        fields.add(Theme.label("Matches")); fields.add(Theme.label("Runs")); fields.add(Theme.label("Wickets")); fields.add(new JLabel());
        fields.add(matchesF); fields.add(runsF); fields.add(wicketsF); fields.add(new JLabel());
        form.add(fields, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btns.setOpaque(false);
        JButton addEnd = Theme.button("Add (End)", Theme.BLUE);
        JButton addStart = Theme.button("Add (Start)", Theme.PURPLE);
        JButton upd = Theme.button("Update", Theme.AMBER);
        JButton del = Theme.button("Delete", Theme.RED);
        JButton clr = Theme.button("Clear", Theme.SLATE);
        addEnd.addActionListener(e -> addPlayer(false));
        addStart.addActionListener(e -> addPlayer(true));
        upd.addActionListener(e -> updatePlayer());
        del.addActionListener(e -> deletePlayer());
        clr.addActionListener(e -> clear());
        for (JButton b : new JButton[]{addEnd, addStart, upd, del}) { b.setEnabled(canEdit); btns.add(b); }
        btns.add(clr);
        if (!canEdit) btns.add(new JLabel("   (User account: view only)"));
        form.add(btns, BorderLayout.SOUTH);
        add(form, BorderLayout.SOUTH);
        photoPreview.setIcon(Imgs.photo("", 100));
    }

    // ---------------------------------------------------  CRUD (linked list methods)
    private void addPlayer(boolean atStart) {
        Player p = read();
        if (p == null) return;
        if (s.players.search(p.id) != null) { msg("A player with this ID already exists!"); return; }
        p.photo = savePhoto(p.id);
        if (atStart) s.players.addAtHead(p); else s.players.add(p);
        s.log.addFirst("Player added: " + p.name);
        clear();
        onChange.run();
    }

    private void updatePlayer() {
        Player p = read();
        if (p == null) return;
        if (s.players.search(p.id) == null) { msg("Player not found. Select a row or enter an existing ID."); return; }
        s.players.update(p.id, p.name, p.team, p.role, p.matches, p.runs, p.wickets, savePhoto(p.id));
        s.log.addFirst("Player updated: " + p.name);
        clear();
        onChange.run();
    }

    private void deletePlayer() {
        int id;
        try { id = Integer.parseInt(idF.getText().trim()); }
        catch (NumberFormatException ex) { msg("Select a row or enter a valid ID to delete."); return; }
        Player p = s.players.search(id);
        if (p == null) { msg("Player not found."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete player " + p.name + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            String name = p.name;
            s.players.delete(id);
            s.log.addFirst("Player deleted: " + name);
            clear();
            onChange.run();
        }
    }

    private Player read() {
        try {
            int id = Integer.parseInt(idF.getText().trim());
            String name = nameF.getText().trim().replace(",", " ");
            String team = teamF.getText().trim().replace(",", " ");
            int matches = Integer.parseInt(matchesF.getText().trim());
            int runs = Integer.parseInt(runsF.getText().trim());
            int wickets = Integer.parseInt(wicketsF.getText().trim());
            if (id <= 0) { msg("ID must be greater than 0."); return null; }
            if (name.isEmpty() || team.isEmpty()) { msg("Name and Team cannot be empty."); return null; }
            if (matches < 0 || runs < 0 || wickets < 0) { msg("Matches, Runs and Wickets cannot be negative."); return null; }
            return new Player(id, name, team, roleBox.getSelectedItem().toString(), matches, runs, wickets, currentPhoto);
        } catch (NumberFormatException ex) {
            msg("ID, Matches, Runs and Wickets must be numbers.");
            return null;
        }
    }

    // ---------------------------------------------------  PHOTO
    private void choosePhoto() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Images (jpg, jpeg, png)", "jpg", "jpeg", "png"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            chosenPhoto = fc.getSelectedFile();
            ImageIcon ic = Imgs.photo(chosenPhoto.getPath(), 100);
            if (ic != null) photoPreview.setIcon(ic);
        }
    }

    private String savePhoto(int id) {
        if (chosenPhoto == null) return currentPhoto;
        try {
            new File("photos").mkdirs();
            String n = chosenPhoto.getName();
            String ext = n.contains(".") ? n.substring(n.lastIndexOf('.')) : ".png";
            Files.copy(chosenPhoto.toPath(), Paths.get("photos", "player_" + id + ext), StandardCopyOption.REPLACE_EXISTING);
            return "photos/player_" + id + ext;
        } catch (Exception e) {
            msg("Could not copy the photo.");
            return currentPhoto;
        }
    }

    // ---------------------------------------------------  REPORT
    private void exportReport() {
        try (PrintWriter w = new PrintWriter(new FileWriter("report.txt"))) {
            w.println("CRICKET MANAGEMENT SYSTEM - PLAYER REPORT");
            w.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")));
            w.println();
            w.printf("%-5s %-18s %-16s %-14s %8s %8s %8s%n", "ID", "Name", "Team", "Role", "Matches", "Runs", "Wickets");
            for (Player p = s.players.getHead(); p != null; p = p.next)
                w.printf("%-5d %-18s %-16s %-14s %8d %8d %8d%n", p.id, p.name, p.team, p.role, p.matches, p.runs, p.wickets);
            w.println();
            w.println("Total players : " + s.players.count());
            w.println("Total runs    : " + s.players.totalRuns());
            w.println("Total wickets : " + s.players.totalWickets());
            Player best = s.players.top(1)[0];
            if (best != null) w.println("Top scorer    : " + best.name + " (" + best.runs + " runs)");
            s.log.addFirst("Report exported (report.txt)");
            onChange.run();
            msg("Report saved as report.txt in the project folder.");
        } catch (Exception e) {
            msg("Could not save the report.");
        }
    }

    // ---------------------------------------------------  REFRESH / HELPERS
    public void refresh() {
        model.setRowCount(0);
        String q = searchF.getText().trim().toLowerCase();
        String role = filterBox.getSelectedItem().toString();
        for (Player p = s.players.getHead(); p != null; p = p.next) {          // linked list traversal
            boolean matchText = q.isEmpty() || p.name.toLowerCase().contains(q) || String.valueOf(p.id).equals(q);
            boolean matchRole = role.equals("All Roles") || p.role.equals(role);
            if (!matchText || !matchRole) continue;
            if (p.thumb == null) p.thumb = Imgs.photo(p.photo, 46);
            model.addRow(new Object[]{p.thumb, p.id, p.name, p.team, p.role, p.matches, p.runs, p.wickets});
        }
    }

    private void fill(Player p) {
        idF.setText(String.valueOf(p.id)); nameF.setText(p.name); teamF.setText(p.team);
        roleBox.setSelectedItem(p.role);
        matchesF.setText(String.valueOf(p.matches)); runsF.setText(String.valueOf(p.runs)); wicketsF.setText(String.valueOf(p.wickets));
        currentPhoto = p.photo;
        chosenPhoto = null;
        photoPreview.setIcon(Imgs.photo(p.photo, 100));
    }

    private void clear() {
        idF.setText(""); nameF.setText(""); teamF.setText("");
        matchesF.setText(""); runsF.setText(""); wicketsF.setText("");
        roleBox.setSelectedIndex(0);
        currentPhoto = "";
        chosenPhoto = null;
        photoPreview.setIcon(Imgs.photo("", 100));
        table.clearSelection();
    }

    private void msg(String m) { JOptionPane.showMessageDialog(this, m); }
}
