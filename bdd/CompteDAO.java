package jungleGame.bdd;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
public class CompteDAO {
	
	
	
	public static boolean createAccount(String username, String password) {
		String checkQuery = "SELECT COUNT(*) FROM COMPTE WHERE username = ?";
        String insertQuery = "INSERT INTO COMPTE (username, password) VALUES (?, ?)";
        
        try (Connection connection = DbConnexion.getConnection()) { // Get connection using try-with-resources
            // Check if username exists
            try (PreparedStatement ps = connection.prepareStatement(checkQuery)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new DatabaseException("Nom d'utilisateur déjà existant");
                    }
                }
            }
            
            // Insert new account
            try (PreparedStatement insertPs = connection.prepareStatement(insertQuery)) {
                insertPs.setString(1, username);
                insertPs.setString(2, password);
                int rowsAffected = insertPs.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la création du compte", e);
        }

	}
	
	public static Compte login(String username, String password) {
		String query = "SELECT id, username, password FROM COMPTE WHERE username = ? AND password = ?";
        try (Connection connection = DbConnexion.getConnection(); // Get connection using try-with-resources
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) { // Use try-with-resources for ResultSet
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String dbUsername = rs.getString("username");
                    String dbPassword = rs.getString("password");
                    return new Compte(id, dbUsername, dbPassword);
                } else {
                    return null; // Login failed
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la connexion", e);
        }
    
    }
	
	/*public void getMatchHistory(String username) {
	    String query = "SELECT * FROM HISTOIRE WHERE joueur1 = ? OR joueur2 = ?";

	    try (Connection conn = DbConnexion.getConnection();
	         PreparedStatement ps = conn.prepareStatement(query);
	         ResultSet rs = ps.executeQuery()) {

	        ps.setString(1, username);
	        ps.setString(2, username);

	        System.out.println("+----+---------------+---------------+---------------+---------------------+");
	        System.out.println("| ID | Joueur 1      | Joueur 2      | Gagnant       | Date de la partie   |");
	        System.out.println("+----+---------------+---------------+---------------+---------------------+");

	        while (rs.next()) {
	            int id = rs.getInt("id");
	            String joueur1 = rs.getString("joueur1") != null ? rs.getString("joueur1") : "-";
	            String joueur2 = rs.getString("joueur2") != null ? rs.getString("joueur2") : "-";
	            String gagnant = rs.getString("gagnant") != null ? rs.getString("gagnant") : "-";
	            Timestamp datePartie = rs.getTimestamp("date_partie");

	            System.out.printf("| %-2d | %-13s | %-13s | %-13s | %-19s |%n",
	                    id, joueur1, joueur2, gagnant, datePartie);
	        }

	        System.out.println("+----+---------------+---------------+---------------+---------------------+");

	    } catch (SQLException e) {
	        throw new DatabaseException("Erreur lors de la récupération de l'historique des matchs", e);
	    }
	}*/
}
