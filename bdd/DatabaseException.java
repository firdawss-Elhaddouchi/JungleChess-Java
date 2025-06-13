package jungleGame.bdd;

public class DatabaseException extends RuntimeException{
	
	public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(Throwable cause) {
        super("erreur d'execution d'un ordre SQL", cause);
    }
}
