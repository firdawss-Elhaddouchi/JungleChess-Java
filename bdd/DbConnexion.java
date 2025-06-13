package jungleGame.bdd;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnexion {
	/**private String url  = "jdbc:h2:~/biblioapp/.data";
    private String user = "sa";
    private String pass = "";
    private String driverName=  "org.h2.Driver";

    public static Connection con ;

    private DbConnexion()  {
        try {
            Class.forName(driverName);
            con = DriverManager.getConnection(url, user, pass);
        }catch(Exception e) {
            throw new DatabaseException("Erreur lors du chargement du pilote ou de la connexion", e);
        }
    }

    public static Connection getConnection() {
        if(con == null){
            new DbConnexion();
        }
        return con;
    }*/
	
	private static final String URL = "jdbc:h2:~/biblioapp/.data";
    private static final String USER = "sa";
    private static final String PASS = "";
    private static final String DRIVER_NAME = "org.h2.Driver";

    // Static initializer block to load the driver once
    static {
        try {
            Class.forName(DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Erreur lors du chargement du pilote H2", e);
        }
    }

    // This method now creates and returns a NEW connection each time it's called
    public static Connection getConnection() throws SQLException {
        // No need for 'con' field anymore as we return a new connection
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
