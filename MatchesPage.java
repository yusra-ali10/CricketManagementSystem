import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

/** Match schedule: full CRUD on the MatchList linked list. */
public class MatchesPage extends JPanel {
    private final Store s;
    private final Runnable onChange;
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Team A", "Team B", "Venue", "Date", "Status", "Result"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField idF = new JTextField(), aF = new JTextField(), bF = new JTextField();
    private final JTextField venueF = new JTextField(), dateF = new JTextField(), resultF = new JTextField();
    private final JTextField searchF = new JTextField(14);
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"Upcoming", "Completed"});
    private final JComboBox<String> sortBox = new JComboBox<>(new String[]{"ID (low to high)", "Date (earliest first)"});

    public MatchesPage(Store s, boolean canEdit, Runnable onChange) {
        this.s = s;
        this.onChange = onChange;
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tools.setOpaque(false);
        tools.add(Theme.title("Matches"));
        tools.add(Box.createHorizontalStrut(20));
        tools.add(new JLabel("Search (ID, team or venue):"));
        tools.add(searchF);
        tools.add(Box.createHorizontalStrut(10));
        tools.add(new JLabel("Sort by:"));
        tools.add(sortBox);
        JButton sortBtn = Theme.button("Sort", Theme.PURPLE);
        sortBtn.addActionListener(e -> { s.matches.sort(sortBox.getSelectedIndex()); s.log.addFirst("Matches sorted"); onChange.run(); });
        tools.add(sortBtn);
        searchF.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }
        });
        add(tools, BorderLayout.NORTH);

        Theme.styleTable(table, 36);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0 && !e.getValueIsAdjusting()) {
                Match m = s.matches.search(Integer.parseInt(model.getValueAt(r, 0).toString()));
                if (m != null) fill(m);
            }
        });
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);

        JPanel form = new JPanel(new BorderLayout(0, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        JPanel fields = new JPanel(new GridLayout(4, 4, 10, 2));
        fields.setOpaque(false);
        fields.add(Theme.label("ID")); fields.add(Theme.label("Team A")); fields.add(Theme.label("Team B")); fields.add(Theme.label("Venue"));
        fields.add(idF); fields.add(aF); fields.add(bF); fields.add(venueF);
        fields.add(Theme.label("Date (yyyy-mm-dd)")); fields.add(Theme.label("Status")); fields.add(Theme.label("Result")); fields.add(new JLabel());
        fields.add(dateF); fields.add(statusBox); fields.add(resultF); fields.add(new JLabel());
        form.add(fields, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btns.setOpaque(false);
        JButton addEnd = Theme.button("Add (End)", Theme.BLUE);
        JButton addStart = Theme.button("Add (Start)", Theme.PURPLE);
        JButton upd = Theme.button("Update", Theme.AMBER);
        JButton del = Theme.button("Delete", Theme.RED);
        JButton clr = Theme.button("Clear", Theme.SLATE);
        addEnd.addActionListener(e -> add(false));
        addStart.addActionListener(e -> add(true));
        upd.addActionListener(e -> update());
        del.addActionListener(e -> delete());
        clr.addActionListener(e -> clear());
        for (JButton b : new JButton[]{addEnd, addStart, upd, del}) { b.setEnabled(canEdit); btns.add(b); }
        btns.add(clr);
        form.add(btns, BorderLayout.SOUTH);
        add(form, BorderLayout.SOUTH);
    }

    private void add(boolean atStart) {
        Match m = read();
        if (m == null) return;
        if (s.matches.search(m.id) != null) { msg("A match with this ID already exists!"); return; }
        if (atStart) s.matches.addAtHead(m); else s.matches.add(m);
        s.log.addFirst("Match added: " + m.teamA + " vs " + m.teamB);
        clear();
        onChange.run();
    }

    private void update() {
        Match m = read();
        if (m == null) return;
        if (s.matches.search(m.id) == null) { msg("Match not found. Select a row or enter an existing ID."); return; }
        s.matches.update(m.id, m.teamA, m.teamB, m.venue, m.date, m.status, m.result);
        s.log.addFirst("Match " + m.id + " updated");
        clear();
        onChange.run();
    }

    private void delete() {
        int id;
        try { id = Integer.parseInt(idF.getText().trim()); }
        catch (NumberFormatException ex) { msg("Select a row or enter a valid ID to delete."); return; }
        if (s.matches.search(id) == null) { msg("Match not found."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete match " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            s.matches.delete(id);
            s.log.addFirst("Match " + id + " deleted");
            clear();
            onChange.run();
        }
    }

    private Match read() {
        try {
            int id = Integer.parseInt(idF.getText().trim());
            String a = aF.getText().trim().replace(",", " "), b = bF.getText().trim().replace(",", " ");
            String v = venueF.getText().trim().replace(",", " "), r = resultF.getText().trim().replace(",", " ");
            String d = dateF.getText().trim();
            if (id <= 0) { msg("ID must be greater than 0."); return null; }
            if (a.isEmpty() || b.isEmpty() || v.isEmpty()) { msg("Team A, Team B and Venue cannot be empty."); return null; }
            if (a.equalsIgnoreCase(b)) { msg("A team cannot play against itself."); return null; }
            try { LocalDate.parse(d); } catch (Exception e) { msg("Date must be valid and in yyyy-mm-dd format (e.g. 2026-12-05)."); return null; }
            if (r.isEmpty()) r = "-";
            return new Match(id, a, b, v, d, statusBox.getSelectedItem().toString(), r);
        } catch (NumberFormatException ex) {
            msg("ID must be a number.");
            return null;
        }
    }

    public void refresh() {
        model.setRowCount(0);
        String q = searchF.getText().trim().toLowerCase();
        for (Match m = s.matches.getHead(); m != null; m = m.next) {
            boolean ok = q.isEmpty() || String.valueOf(m.id).equals(q) || m.teamA.toLowerCase().contains(q)
                    || m.teamB.toLowerCase().contains(q) || m.venue.toLowerCase().contains(q);
            if (ok) model.addRow(new Object[]{m.id, m.teamA, m.teamB, m.venue, m.date, m.status, m.result});
        }
    }

    private void fill(Match m) {
        idF.setText(String.valueOf(m.id)); aF.setText(m.teamA); bF.setText(m.teamB);
        venueF.setText(m.venue); dateF.setText(m.date); statusBox.setSelectedItem(m.status);
        resultF.setText(m.result);
    }

    private void clear() {
        idF.setText(""); aF.setText(""); bF.setText(""); venueF.setText(""); dateF.setText(""); resultF.setText("");
        statusBox.setSelectedIndex(0);
        table.clearSelection();
    }

    private void msg(String m) { JOptionPane.showMessageDialog(this, m); }
}
