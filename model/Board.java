package jungleGame.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jungleGame.bdd.Compte;
import jungleGame.bdd.MatchHistoryDAO;

public class Board {
    private Arena arena;
    private List<Animal> animals;
    private int currentPlayer; 
    private MatchHistoryDAO matchHistoryDAO = new MatchHistoryDAO();
    private List<String> eventLog; //?
    private Compte player1Account;
    private Compte player2Account;
    private List<String> currentMatchEventLog; // To store events for the current match
    private boolean gameOver = false;



    public Board() {
        arena = new Arena();
        animals = new ArrayList<>();
        currentPlayer = 1;
        initializeGame();
    }
    public Board(Compte p1, Compte p2) { // Constructor now takes player accounts
        arena = new Arena();
        animals = new ArrayList<>();
        currentPlayer = 1; // Player 1 starts
        this.player1Account = p1;
        this.player2Account = p2;
        this.matchHistoryDAO = new MatchHistoryDAO(); // Initialize MatchHistoryDAO
        this.eventLog = new ArrayList<>();
        initializeGame();
    }
    
    private void initializeGame() {
    	// Player 1's animals (bottom of the board)
        animals.add(new Rat(1, new Position(6, 6))); // Rank 1
        animals.add(new Cat(1, new Position(7, 1))); // Rank 2
        animals.add(new Wolf(1, new Position(6, 2))); // Rank 3
        animals.add(new Dog(1, new Position(7, 5))); // Rank 4
        animals.add(new Leopard(1, new Position(6, 4))); // Rank 5
        animals.add(new Tiger(1, new Position(8, 0))); // Rank 6
        animals.add(new Lion(1, new Position(8, 6))); // Rank 7
        animals.add(new Elephant(1, new Position(6, 0))); // Rank 8

        // Player 2's animals (top of the board)
        animals.add(new Rat(2, new Position(2, 0))); // Rank 1
        animals.add(new Cat(2, new Position(1, 5))); // Rank 2
        animals.add(new Wolf(2, new Position(2, 4))); // Rank 3
        animals.add(new Dog(2, new Position(1, 1))); // Rank 4
        animals.add(new Leopard(2, new Position(2, 2))); // Rank 5
        animals.add(new Tiger(2, new Position(0, 6))); // Rank 6
        animals.add(new Lion(2, new Position(0, 0))); // Rank 7
        animals.add(new Elephant(2, new Position(2, 6))); // Rank 8

        // Initialize event log
        eventLog.add("Game started. Player 1's turn.");
    }
    

    /*
    public boolean moveAnimal(Position from, Position to) {
        Animal animal = findAnimal(from);
        if (animal != null && animal.getPlayer() == currentPlayer && animal.canMoveTo(to, arena,animals)) {
            Animal target = findAnimal(to);
            if (target != null && animal.canCapture(target,arena)){
                animals.remove(target); 
            }
            animal.setPosition(to);
            if (arena.isSanctuary(to) && arena.getSanctuaryOwner(to) != currentPlayer) {
                System.out.println("اللاعب " + currentPlayer + " فاز بالدخول إلى الملاذ!");
                endGame(currentPlayer);
                return true;
            }
            currentPlayer = (currentPlayer == 1) ? 2 : 1; 
            return true;
        }
        return false;
    }
    ***************/
    public boolean moveAnimal(Position from, Position to) {
    	if (gameOver) {
            System.out.println("Le jeu est terminé. Aucun mouvement possible.");
            return false;
        }
        Animal animal = findAnimal(from);
        if (animal == null || animal.getPlayer() != currentPlayer) {
            System.out.println("Invalid: No animal at " + from + " for current player " + currentPlayer);
            return false;
        }

        if (!arena.isValidPosition(to)) {
            System.out.println("Invalid: Target position " + to + " is out of bounds.");
            return false;
        }

        // Prevent moving into own sanctuary
        if (arena.isSanctuary(to) && arena.getSanctuaryOwner(to) == currentPlayer) {
            System.out.println("Invalid: Cannot move into your own sanctuary.");
            return false;
        }

        // Check general movement rules for the animal
        if (!animal.canMoveTo(to, arena, animals)) {
            System.out.println("Invalid: " + animal.getName() + " cannot move to " + to + " according to its rules.");
            return false;
        }

        Animal target = findAnimal(to);

        // Capture logic
        if (target != null) {
            if (target.getPlayer() == currentPlayer) {
                System.out.println("Invalid: Cannot capture your own animal.");
                return false; // Cannot capture own animal
            }

            // Special rule: Rat cannot capture after leaving water
            if (animal instanceof Rat && !arena.isLake(from) && arena.isLake(target.getPosition())) {
                 System.out.println("Invalid: Rat cannot capture when leaving the river.");
                 return false;
            }

            if (checkWinCondition(to)) {
                endGame(currentPlayer);
                return true;
            }
            
            if (animal.canCapture(target, arena)) { // Pass arena to canCapture for trap logic
                animals.remove(target);
                eventLog.add(animal.getName() + " from Player " + currentPlayer + " captured " + target.getName() + " at " + to);
                System.out.println(animal.getName() + " captured " + target.getName() + "!");
            } else {
                System.out.println("Invalid: " + animal.getName() + " cannot capture " + target.getName() + " at " + to);
                return false; // Cannot capture the target
            }
        }

        // Update animal's position
        animal.setPosition(to);
        eventLog.add(animal.getName() + " from Player " + currentPlayer + " moved from " + from + " to " + to);
        System.out.println(animal.getName() + " moved to " + to);

        // Check for victory condition (entering opponent's sanctuary)
        if (arena.isSanctuary(to) && arena.getSanctuaryOwner(to) != currentPlayer) {
            System.out.println("Player " + currentPlayer + " has won by entering the opponent's sanctuary!");
            endGame(currentPlayer);
            return true; // Game ends
        }
        // Check if all opponent animals are captured (another win condition)
        List<Animal> opponentAnimals = animals.stream()
                                            .filter(a -> a.getPlayer() != currentPlayer)
                                            .collect(Collectors.toList());
        if (opponentAnimals.isEmpty()) {
            System.out.println("Player " + currentPlayer + " has won by capturing all opponent animals!");
            endGame(currentPlayer);
            return true; // Game ends
        }

        // Switch turn if move was successful and game not ended
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        eventLog.add("It's now Player " + currentPlayer + "'s turn.");
        return true;
    }
   
    
    private void endGame(int winnerPlayer) {
        if (!gameOver) {
            System.out.println("Game over! Winner: Player " + winnerPlayer);
            eventLog.add("Game ended. Winner: Player " + winnerPlayer);
            gameOver = true;
        }
    }

    
    
    public void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }
    private boolean checkWinCondition(Position to) {
        if (arena.isSanctuary(to) && arena.getSanctuaryOwner(to) != currentPlayer) {
            eventLog.add("Player " + currentPlayer + " has entered the opponent's sanctuary!");
            return true;
        }

        boolean opponentStillHasAnimals = animals.stream()
            .anyMatch(a -> a.getPlayer() != currentPlayer);
        if (!opponentStillHasAnimals) {
            eventLog.add("Player " + currentPlayer + " has captured all opponent animals!");
            return true;
        }

        return false;
    }

    private boolean checkWinCondition() {
        // Win by capturing opponent's sanctuary
        // Player 1's sanctuary is at (8,3)
        if (findAnimal(new Position(8, 3)) != null && findAnimal(new Position(8, 3)).getPlayer() == 1) {
            return true; // Player 1 captured Player 2's sanctuary
        }
        // Player 2's sanctuary is at (0,3)
        if (findAnimal(new Position(0, 3)) != null && findAnimal(new Position(0, 3)).getPlayer() == 2) {
            return true; // Player 2 captured Player 1's sanctuary
        }

        // Win by capturing all opponent's animals
        boolean player1HasAnimals = false;
        boolean player2HasAnimals = false;
        for (Animal animal : animals) {
            if (animal.getPlayer() == 1) {
                player1HasAnimals = true;
            } else if (animal.getPlayer() == 2) {
                player2HasAnimals = true;
            }
        }
        return !player1HasAnimals || !player2HasAnimals;
    }
    
   
   
 


    private Animal findAnimal(Position pos) {
        for (Animal animal : animals) {
            if (animal.getPosition().equals(pos)) {
                return animal;
            }
        }
        return null;
    }
   

  

    public void displayBoard() {
        char[][] display = new char[9][7];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
                display[i][j] = '.'; 
            }
        }

        
        display[3][1] = '#'; display[3][2] = '#';
        display[4][1] = '#'; display[4][2] = '#';
        display[5][1] = '#'; display[5][2] = '#';

        display[3][4] = '#'; display[3][5] = '#';
        display[4][4] = '#'; display[4][5] = '#';
        display[5][4] = '#'; display[5][5] = '#';

       
        display[0][2] = '&'; display[0][4] = '&';
        display[1][3] = '&';

        display[7][3] = '&';
        display[8][2] = '&'; display[8][4] = '&';

       
        display[0][3] = '*';
        display[8][3] = '*';

        for (Animal animal : animals) {
            Position pos = animal.getPosition();
            display[pos.getX()][pos.getY()] = getAnimalChar(animal); 
        }

        System.out.println("\n   A B C D E F G"); 
        System.out.println("  +---------------+");
        for (int i = 0; i < 9; i++) {
            System.out.print(i + " |"); 
            for (int j = 0; j < 7; j++) {
                System.out.print(display[i][j] + " "); 
            }
            System.out.println("|"); 
        }
        System.out.println("  +---------------+\n");
    }

    private char getSpecialChar(Position pos) {
        if (arena.isLake(pos)) return '#';      
        if (arena.isTrap(pos)) return '&';     
        if (arena.isSanctuary(pos)) return '*';
        return '.';                             
    }
    /*private char getAnimalChar(Animal animal) {
        char c = animal.getName().charAt(0);
        return animal.getPlayer() == 1 ? Character.toUpperCase(c) : Character.toLowerCase(c);
    }*/
    private char getAnimalChar(Animal animal) {
        String c = animal.getName();
        char firstChar = c.charAt(0);  
        return animal.getPlayer() == 1 ? Character.toUpperCase(firstChar) : Character.toLowerCase(firstChar);
    }
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public List<String> getEventLog() {
        return eventLog;
    }
    
    public boolean isGameOver() {
    	return gameOver;
    }

	public Arena getArena() {
		return arena;
	}
    
}
