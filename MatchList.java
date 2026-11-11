import java.io.*;

/** Second linked list of the project: match schedule with full CRUD. */
public class MatchList {
    private Match head;

    public boolean add(Match m) {
        if (search(m.id) != null) return false;
        if (head == null) head = m;
        else {
            Match temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = m;
        }
        return true;
    }

    public boolean addAtHead(Match m) {
        if (search(m.id) != null) return false;
        m.next = head;
        head = m;
        return true;
    }

    public Match search(int id) {
        for (Match m = head; m != null; m = m.next) if (m.id == id) return m;
        return null;
    }

    public Match getHead() { return head; }

    public int count() {
        int c = 0;
        for (Match m = head; m != null; m = m.next) c++;
        return c;
    }

    public int countByStatus(String status) {
        int c = 0;
        for (Match m = head; m != null; m = m.next) if (m.status.equals(status)) c++;
        return c;
    }

    public boolean update(int id, String teamA, String teamB, String venue, String date, String status, String result) {
        Match m = search(id);
        if (m == null) return false;
        m.teamA = teamA; m.teamB = teamB; m.venue = venue;
        m.date = date; m.status = status; m.result = result;
        return true;
    }

    public boolean delete(int id) {
        if (head == null) return false;
        if (head.id == id) { head = head.next; return true; }
        Match temp = head;
        while (temp.next != null && temp.next.id != id) temp = temp.next;
        if (temp.next == null) return false;
        temp.next = temp.next.next;
        return true;
    }

    /** Bubble sort. mode 0 = ID, 1 = Date (earliest first). */
    public void sort(int mode) {
        if (head == null) return;
        boolean swapped;
        do {
            swapped = false;
            for (Match m = head; m.next != null; m = m.next) {
                boolean swap = mode == 1 ? m.date.compareTo(m.next.date) > 0 : m.id > m.next.id;
                if (swap) { swapData(m, m.next); swapped = true; }
            }
        } while (swapped);
    }

    private void swapData(Match a, Match b) {
        int i = a.id; a.id = b.id; b.id = i;
        String s;
        s = a.teamA; a.teamA = b.teamA; b.teamA = s;
        s = a.teamB; a.teamB = b.teamB; b.teamB = s;
        s = a.venue; a.venue = b.venue; b.venue = s;
        s = a.date; a.date = b.date; b.date = s;
        s = a.status; a.status = b.status; b.status = s;
        s = a.result; a.result = b.result; b.result = s;
    }

    public void save(String file) {
        try (PrintWriter w = new PrintWriter(new FileWriter(file))) {
            for (Match m = head; m != null; m = m.next)
                w.println(m.id + "," + m.teamA + "," + m.teamB + "," + m.venue + "," + m.date + "," + m.status + "," + m.result);
        } catch (IOException e) {
            System.out.println("Could not save matches: " + e.getMessage());
        }
    }

    public boolean load(String file) {
        File f = new File(file);
        if (!f.exists()) return false;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] t = line.split(",", -1);
                if (t.length < 7) continue;
                add(new Match(Integer.parseInt(t[0]), t[1], t[2], t[3], t[4], t[5], t[6]));
            }
        } catch (Exception e) {
            System.out.println("Could not load matches: " + e.getMessage());
        }
        return true;
    }
}
