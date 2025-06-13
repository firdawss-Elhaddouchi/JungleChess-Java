package jungleGame.model;

import java.util.List;

import jungleGame.bdd.Compte;

public class GameLogic {
    private Board board;
    private boolean gameOver;
    private Compte player1Account;
    private Compte player2Account;
    

    public GameLogic(Compte p1, Compte p2) {
        this.player1Account = p1;
        this.player2Account = p2;
        this.board = new Board(p1, p2); // Pass player accounts to Board
        this.gameOver = false;
    }
    public GameLogic() {
        this(new Compte(0, "Guest1", ""), new Compte(0, "Guest2", ""));
    }
    public void displayBoard() {
        board.displayBoard();
    }

   
    public boolean performMove(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 2) {
            System.out.println("Format de commande invalide. Utilisez 'A0 B0'.");
            return false;
        }
        try {
            Position from = parsePosition(parts[0]);
            Position to = parsePosition(parts[1]);
            
            boolean moveSuccessful = board.moveAnimal(from, to);
            if (moveSuccessful) {
                // Check for game over conditions after each successful move
                if (board.isGameOver()) {
                    gameOver = true;
                }
            }
            return moveSuccessful;
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur de position: " + e.getMessage());
            return false;
        }
    }
    private Position parsePosition(String s) {
        if (s.length() != 2) {
            throw new IllegalArgumentException("La position doit être de 2 caractères (ex: A0).");
        }
        char colChar = s.charAt(0);
        int row = Character.getNumericValue(s.charAt(1));

        int col = -1;
        if (colChar >= 'A' && colChar <= 'G') {
            col = colChar - 'A';
        } else if (colChar >= 'a' && colChar <= 'g') {
            col = colChar - 'a';
        } else {
            throw new IllegalArgumentException("La colonne doit être entre A et G.");
        }

        if (row < 0 || row > 8) {
            throw new IllegalArgumentException("La ligne doit être entre 0 et 8.");
        }
        return new Position(row, col); 
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public List<String> getMatchHistory() {
        return board.getEventLog();
    }

   


    public int getCurrentPlayer() {
        return board.getCurrentPlayer();
    }


    public Board getBoard() {
        return board; // Useful for Main to access for display
    }
    
}
