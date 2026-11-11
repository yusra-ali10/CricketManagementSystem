import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Team statistics, calculated by traversing the players list. */
public class TeamsPage extends JPanel {
    private final Store s;
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Team", "Players", "Total Runs", "Total Wickets", "Top Scorer"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };

    public TeamsPage(Store s) {
        this.s = s;
        setLayout(new BorderLayout(0, 12));
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        add(Theme.title("Team Statistics"), BorderLayout.NORTH);
        JTable t = new JTable(model);
        Theme.styleTable(t, 44);
        JScrollPane sp = new JScrollPane(t);
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);
        JLabel note = new JLabel("These numbers are calculated live from the players linked list.");
        note.setForeground(Theme.MUTED);
        add(note, BorderLayout.SOUTH);
    }

    public void refresh() {
        model.setRowCount(0);
        for (TeamStat t = TeamList.build(s.players).getHead(); t != null; t = t.next)
            model.addRow(new Object[]{t.name, t.players, t.runs, t.wickets, t.best == null ? "-" : t.best.name + " (" + t.best.runs + ")"});
    }
}
