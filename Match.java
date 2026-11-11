/** Node of the matches linked list. */
public class Match {
    int id;
    String teamA;
    String teamB;
    String venue;
    String date;      // yyyy-mm-dd
    String status;    // Upcoming / Completed
    String result;
    Match next;

    Match(int id, String teamA, String teamB, String venue, String date, String status, String result) {
        this.id = id;
        this.teamA = teamA;
        this.teamB = teamB;
        this.venue = venue;
        this.date = date;
        this.status = status;
        this.result = result;
        this.next = null;
    }
}
