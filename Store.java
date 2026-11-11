/** Holds the three linked lists of the program and loads / saves the data files. */
public class Store {
    static final String PLAYERS_FILE = "players.csv";
    static final String MATCHES_FILE = "matches.csv";

    final PlayerList players = new PlayerList();
    final MatchList matches = new MatchList();
    final ActivityList log = new ActivityList();

    public Store() {
        if (!players.load(PLAYERS_FILE)) {       // first run: sample data
            players.add(new Player(1, "Ali Raza", "Indus Eagles", "Batsman", 52, 2840, 2, "photos/player_1.png"));
            players.add(new Player(2, "Hamza Khan", "Sindh Tigers", "Bowler", 41, 310, 76, "photos/player_2.png"));
            players.add(new Player(3, "Usman Tariq", "Indus Eagles", "All-rounder", 47, 1520, 48, "photos/player_3.png"));
            players.add(new Player(4, "Bilal Ahmed", "Mehran XI", "Wicket-keeper", 38, 1275, 0, "photos/player_4.png"));
            players.add(new Player(5, "Saad Memon", "Sindh Tigers", "Batsman", 60, 3410, 5, "photos/player_5.png"));
            players.add(new Player(6, "Faizan Ali", "Mehran XI", "Bowler", 35, 190, 69, "photos/player_6.png"));
        }
        if (!matches.load(MATCHES_FILE)) {
            matches.add(new Match(1, "Indus Eagles", "Sindh Tigers", "National Stadium", "2026-11-20", "Upcoming", "-"));
            matches.add(new Match(2, "Mehran XI", "Indus Eagles", "Gaddafi Ground", "2026-11-27", "Upcoming", "-"));
            matches.add(new Match(3, "Sindh Tigers", "Mehran XI", "Niaz Stadium", "2026-10-30", "Completed", "Tigers won by 24 runs"));
            matches.add(new Match(4, "Indus Eagles", "Mehran XI", "Multan Ground", "2026-10-12", "Completed", "Eagles won by 5 wickets"));
        }
        log.addFirst("Loaded " + matches.count() + " matches");
        log.addFirst("Loaded " + players.count() + " players");
        log.addFirst("System started");
        saveAll();
    }

    public void saveAll() {
        players.save(PLAYERS_FILE);
        matches.save(MATCHES_FILE);
    }
}
