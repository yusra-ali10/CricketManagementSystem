import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Node of the activity log. */
class Activity {
    String time;
    String text;
    Activity next;
    Activity(String time, String text) { this.time = time; this.text = text; }
}

/** Linked list that remembers the latest actions (newest first, max 8). */
public class ActivityList {
    private Activity head;
    private int size = 0;
    private static final int MAX = 8;

    public void addFirst(String text) {
        String t = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM, HH:mm"));
        Activity a = new Activity(t, text);
        a.next = head;
        head = a;
        size++;
        if (size > MAX) {                 // cut the oldest nodes from the tail
            Activity temp = head;
            for (int i = 1; i < MAX; i++) temp = temp.next;
            temp.next = null;
            size = MAX;
        }
    }

    public Activity getHead() { return head; }
}
