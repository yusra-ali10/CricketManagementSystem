/** Node: statistics of one team. */
class TeamStat {
    String name;
    int players, runs, wickets;
    Player best;          // top run scorer of the team
    TeamStat next;
    TeamStat(String name) { this.name = name; }
}

/** Linked list of team statistics, built by traversing the players list. */
public class TeamList {
    private TeamStat head;

    public void addPlayer(Player p) {
        TeamStat t = head, last = null;
        while (t != null && !t.name.equalsIgnoreCase(p.team)) { last = t; t = t.next; }
        if (t == null) {
            t = new TeamStat(p.team);
            if (last == null) head = t; else last.next = t;
        }
        t.players++;
        t.runs += p.runs;
        t.wickets += p.wickets;
        if (t.best == null || p.runs > t.best.runs) t.best = p;
    }

    public TeamStat getHead() { return head; }

    public static TeamList build(PlayerList list) {
        TeamList tl = new TeamList();
        for (Player p = list.getHead(); p != null; p = p.next) tl.addPlayer(p);
        return tl;
    }
}
