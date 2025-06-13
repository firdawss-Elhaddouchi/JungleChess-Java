package jungleGame.bdd;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class DbCreateTable {
	
	/*********************************************/
	public  static  boolean checkIfDbExist() {
        try{
	        Connection connection = DbConnexion.getConnection();
	
	        // Vérifier si la table compte existe
	        String checkTableCompte ="SELECT COUNT(*) AS tableCount FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME IN ('COMPTE', 'HISTOIRE')";
	                       
	
	        Statement statement = connection.createStatement();
	        ResultSet resultSet = statement.executeQuery(checkTableCompte);
	
	        return resultSet.next() && resultSet.getInt("tableCount")==2;

        } catch (Exception ex) {
            throw new DatabaseException("Erreur de vérification de l'installation", ex);
        }
    }
	/************************************************/
	public static boolean install() {
        try {
        	Connection connection = DbConnexion.getConnection();
            Statement statement = connection.createStatement();

           String createTableCompteSQL = 
               "CREATE TABLE IF NOT EXISTS COMPTE (\n"+
                   "id INT AUTO_INCREMENT PRIMARY KEY,\n"+
                   "username VARCHAR(50) NOT NULL UNIQUE,\n"+
                   "password VARCHAR(50) NOT NULL);";

           String createTableHistoireSQL = 
               "CREATE TABLE IF NOT EXISTS HISTOIRE (\n"+
                   "id INT AUTO_INCREMENT PRIMARY KEY,\n"+
                   "joueur1 VARCHAR(50),\n"+
                   "joueur2 VARCHAR(50),\n"+
                   "gagnant VARCHAR(50),\n"+
                   "date_partie TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

           statement.execute(createTableCompteSQL);
           statement.execute(createTableHistoireSQL);

           return true;

       } catch (Exception ex) {
           throw new DatabaseException("Erreur lors de l'installation de la base de données", ex);
       }
    }
	
}
