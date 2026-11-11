import java.io.*;

/** Singly Linked List that stores all players. No array / ArrayList is used for storage. */
public class PlayerList {
    private Player head;

    // ---------- CREATE ----------
    /** Add a player at the END of the list. */
    public boolean add(Player p) {
        if (search(p.id) != null) return false;      // IDs must be unique
        if (head == null) {
            head = p;
        } else {
            Player temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = p;
        }
        return true;
    }

    /** Add a player at the START of the list (O(1)). */
    public boolean addAtHead(Player p) {
        if (search(p.id) != null) return false;
        p.next = head;
        head = p;
        return true;
    }

    // ---------- READ ----------
    public Player search(int id) {
        Player temp = head;
        while (temp != null) {
            if (temp.id == id) return temp;
            temp = temp.next;
        }
        return null;
    }

    public Player getHead() { return head; }

    public int count() {
        int c = 0;
        for (Player p = head; p != null; p = p.next) c++;
        return c;
    }

    public int totalRuns() {
        int t = 0;
        for (Player p = head; p != null; p = p.next) t += p.runs;
        return t;
    }

    public int totalWickets() {
        int t = 0;
        for (Player p = head; p != null; p = p.next) t += p.wickets;
        return t;
    }

    public int countByRole(String role) {
        int c = 0;
        for (Player p = head; p != null; p = p.next) if (p.role.equals(role)) c++;
        return c;
    }

    /** Returns the best n run scorers (highest first). Unused slots stay null. */
    public Player[] top(int n) { return topBy(n, false); }

    /** Returns the best n wicket takers (highest first). Unused slots stay null. */
    public Player[] topWickets(int n) { return topBy(n, true); }

    private Player[] topBy(int n, boolean wickets) {
        Player[] best = new Player[n];
        for (Player p = head; p != null; p = p.next) {
            int pos = n;
            while (pos > 0 && (best[pos - 1] == null || key(best[pos - 1], wickets) < key(p, wickets))) pos--;
            if (pos < n) {
                for (int k = n - 1; k > pos; k--) best[k] = best[k - 1];
                best[pos] = p;
            }
        }
        return best;
    }

    private int key(Player p, boolean wickets) { return wickets ? p.wickets : p.runs; }

    // ---------- UPDATE ----------
    public boolean update(int id, String name, String team, String role,
                          int matches, int runs, int wickets, String photo) {
        Player p = search(id);
        if (p == null) return false;
        p.name = name;
        p.team = team;
        p.role = role;
        p.matches = matches;
        p.runs = runs;
        p.wickets = wickets;
        if (!photo.equals(p.photo)) { p.photo = photo; p.thumb = null; }
        return true;
    }

    // ---------- DELETE ----------
    public boolean delete(int id) {
        if (head == null) return false;
        if (head.id == id) {              // case 1: delete the head
            head = head.next;
            return true;
        }
        Player temp = head;               // case 2: delete a middle / last node
        while (temp.next != null && temp.next.id != id) temp = temp.next;
        if (temp.next == null) return false;
        temp.next = temp.next.next;       // skip the deleted node
        return true;
    }

    // ---------- SORT (bubble sort on the list) ----------
    // mode 0 = ID (low to high), 1 = Runs (high to low), 2 = Wickets (high to low), 3 = Name (A-Z)
    public void sort(int mode) {
        if (head == null) return;
        boolean swapped;
        do {
            swapped = false;
            for (Player p = head; p.next != null; p = p.next) {
                if (shouldSwap(p, p.next, mode)) {
                    swapData(p, p.next);
                    swapped = true;
                }
            }
        } while (swapped);
    }

    private boolean shouldSwap(Player a, Player b, int mode) {
        switch (mode) {
            case 1:  return a.runs < b.runs;
            case 2:  return a.wickets < b.wickets;
            case 3:  return a.name.compareToIgnoreCase(b.name) > 0;
            default: return a.id > b.id;
        }
    }

    private void swapData(Player a, Player b) {
        int i; String s; javax.swing.ImageIcon ic;
        i = a.id; a.id = b.id; b.id = i;
        s = a.name; a.name = b.name; b.name = s;
        s = a.team; a.team = b.team; b.team = s;
        s = a.role; a.role = b.role; b.role = s;
        i = a.matches; a.matches = b.matches; b.matches = i;
        i = a.runs; a.runs = b.runs; b.runs = i;
        i = a.wickets; a.wickets = b.wickets; b.wickets = i;
        s = a.photo; a.photo = b.photo; b.photo = s;
        ic = a.thumb; a.thumb = b.thumb; b.thumb = ic;
    }

    // ---------- FILE (data stays after closing the program) ----------
    public void save(String file) {
        try (PrintWriter w = new PrintWriter(new FileWriter(file))) {
            for (Player p = head; p != null; p = p.next) {
                w.println(p.id + "," + p.name + "," + p.team + "," + p.role + ","
                        + p.matches + "," + p.runs + "," + p.wickets + "," + p.photo);
            }
        } catch (IOException e) {
            System.out.println("Could not save: " + e.getMessage());
        }
    }

    public boolean load(String file) {
        File f = new File(file);
        if (!f.exists()) return false;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] t = line.split(",", -1);
                if (t.length < 8) continue;
                add(new Player(Integer.parseInt(t[0]), t[1], t[2], t[3],
                        Integer.parseInt(t[4]), Integer.parseInt(t[5]), Integer.parseInt(t[6]), t[7]));
            }
        } catch (Exception e) {
            System.out.println("Could not load: " + e.getMessage());
        }
        return true;
    }
}
