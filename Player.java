import javax.swing.ImageIcon;

/** Node of the linked list: one cricket player. */
public class Player {
    int id;
    String name;
    String team;
    String role;
    int matches;
    int runs;
    int wickets;
    String photo;      // relative path of the player's photo ("" = default avatar)
    Player next;       // pointer to the next node
    ImageIcon thumb;   // cached small picture for the table

    Player(int id, String name, String team, String role,
           int matches, int runs, int wickets, String photo) {
        this.id = id;
        this.name = name;
        this.team = team;
        this.role = role;
        this.matches = matches;
        this.runs = runs;
        this.wickets = wickets;
        this.photo = photo;
        this.next = null;
    }
}
