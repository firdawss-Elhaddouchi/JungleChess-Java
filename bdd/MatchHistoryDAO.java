package jungleGame.bdd;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class MatchHistoryDAO {

    public static class Match {
        private int id;
        private String joueur1;
        private String joueur2;
        private String gagnant;
        private Timestamp datePartie;

        public Match(int id, String joueur1, String joueur2, String gagnant, Timestamp datePartie) {
            this.id = id;
            this.joueur1 = joueur1;
            this.joueur2 = joueur2;
            this.gagnant = gagnant;
            this.datePartie = datePartie;
        }

        public int getId() {
        	return id; 
        }
        public String getJoueur1(){
        	return joueur1; 
        }
        public String getJoueur2(){
        	return joueur2; 
        }
        public String getGagnant(){
        	return gagnant; 
        }
        public Timestamp getDatePartie(){
        	return datePartie; 
        }
    }

    public static List<Match> getMatchHistory(String username) {
        String query = "SELECT * FROM HISTOIRE WHERE joueur1 = ? OR joueur2 = ?";
        List<Match> matches = new ArrayList<>();

        try (Connection conn = DbConnexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String joueur1 = rs.getString("joueur1") != null ? rs.getString("joueur1") : "-";
                    String joueur2 = rs.getString("joueur2") != null ? rs.getString("joueur2") : "-";
                    String gagnant = rs.getString("gagnant") != null ? rs.getString("gagnant") : "-";
                    Timestamp datePartie = rs.getTimestamp("date_partie");

                    matches.add(new Match(id, joueur1, joueur2, gagnant, datePartie));
                }
            }

            printMatchHistory(matches);
            return matches;

        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération de l'historique des matchs", e);
        }
    }

    private static void printMatchHistory(List<Match> matches) {
        System.out.println("+----+---------------+---------------+---------------+---------------------+");
        System.out.println("| ID | Joueur 1      | Joueur 2      | Gagnant       | Date de la partie   |");
        System.out.println("+----+---------------+---------------+---------------+---------------------+");

        for (Match match : matches) {
            System.out.printf("| %-2d | %-13s | %-13s | %-13s | %-19s |%n",
                    match.getId(),
                    match.getJoueur1(),
                    match.getJoueur2(),
                    match.getGagnant(),
                    match.getDatePartie());
        }

        System.out.println("+----+---------------+---------------+---------------+---------------------+");
    }
    
    public void saveMatchResult(String player1Username, String player2Username, String winnerUsername, List<String> eventLog) {
        String sql = "INSERT INTO HISTOIRE (joueur1, joueur2, gagnant) VALUES (?, ?, ?)"; // date_partie defaults to CURRENT_TIMESTAMP
        try (Connection conn = DbConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, player1Username);
            pstmt.setString(2, player2Username);
            pstmt.setString(3, winnerUsername);
            pstmt.executeUpdate();
            System.out.println("Match result saved to database.");
            // You could optionally save the full eventLog to another column if needed, but HISTOIRE table doesn't have it.
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la sauvegarde du résultat du match", e);
        }
    }
}
