# Cricket Management System (Linked List)

**Course:** SW 212 - Data Structures & Algorithms (Open Ended Lab)
**Group:** A (Linked List)
**Student:** Yusra Khuhro (K25SW-004)
**Instructor:** Engr. Asmatullah Zubair

A Java Swing desktop application that manages a cricket club. All data is stored in
**custom Singly Linked Lists**. No array or ArrayList is used to store the data.

## Linked lists used
| List | Node class | Stores |
|---|---|---|
| `PlayerList` | `Player` | players (CRUD, sort, top scorers, save/load) |
| `MatchList` | `Match` | match schedule (CRUD, sort, save/load) |
| `ActivityList` | `Activity` | latest actions shown on the dashboard |
| `TeamList` | `TeamStat` | team statistics built from the players list |

## Features
- Login with two roles: **Admin** (full access) and **User** (view only)
- Dashboard: banner, summary cards, Top 5 run scorers chart, players-by-role donut chart, recent activity
- **Players:** Create (`add`, `addAtHead`), Read (`search`, live search, role filter), Update, Delete, Sort (bubble sort), photo for every player, Export Report (`report.txt`)
- **Matches:** full CRUD with date validation, search, sort by ID or date
- **Teams:** live statistics (players, runs, wickets, top scorer per team)
- Data validation, duplicate ID check, confirmation before delete
- Data is saved to `players.csv` and `matches.csv` and loaded on start

## Login
- Admin: `admin` / `admin123`
- User: `user` / `user123`

## Files
| File | Purpose |
|---|---|
| `Player.java`, `PlayerList.java` | Node and linked list of players |
| `Match.java`, `MatchList.java` | Node and linked list of matches |
| `ActivityList.java`, `TeamList.java` | Activity log and team statistics lists |
| `Store.java` | Holds the lists, loads and saves the files |
| `LoginFrame.java`, `MainFrame.java`, `AppPanel.java` | Login window, main window, sidebar |
| `DashboardPage.java`, `PlayersPage.java`, `MatchesPage.java`, `TeamsPage.java` | The four pages |
| `Theme.java`, `Imgs.java` | Colours, helpers, pictures |
| `Main.java` | Starts the program |
| `images/`, `photos/` | App pictures and player photos |

## How to Run
1. Install JDK and VS Code with the Extension Pack for Java
2. Open this folder in VS Code
3. Open `Main.java` and click **Run**
