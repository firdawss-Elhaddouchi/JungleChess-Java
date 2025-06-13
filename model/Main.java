// File: jungleGame/model/Main.java
package jungleGame.model;

import jungleGame.bdd.Compte;
import jungleGame.bdd.CompteDAO;
import jungleGame.bdd.DbCreateTable;
import jungleGame.bdd.MatchHistoryDAO; // To display history

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static GameLogic gameLogic;
    private static CompteDAO compteDAO = new CompteDAO(); // Use your CompteDAO

    // Players for the current game
    private static Compte player1Account = null; // Stored here after login
    private static Compte player2Account = null; // Stored here after login/guest

    public static void main(String[] args) {
        // Ensure database tables are created
        System.out.println("Vérification/Installation de la base de données...");
        try {
            if (!DbCreateTable.checkIfDbExist()) {
                System.out.println("Tables non trouvées, installation...");
                DbCreateTable.install();
                System.out.println("Installation de la base de données terminée.");
            } else {
                System.out.println("Tables existantes.");
            }
        } catch (Exception e) {
            System.err.println("Erreur fatale lors de l'initialisation de la base de données: " + e.getMessage());
            e.printStackTrace();
            scanner.close();
            return;
        }

        System.out.println("\nBienvenue dans le jeu Jungle !");

        showMainMenu();
        scanner.close(); // Close scanner when done
    }

    private static void showMainMenu() {
        int choice = -1;
        while (choice != 6) { // Loop until user chooses to exit (now option 6)
            System.out.println("\nOptions d'accès au jeu:");
            System.out.println("1. Se connecter (pour le joueur 1)");
            System.out.println("2. Créer un compte");
            System.out.println("3. Démarrer une partie à deux joueurs (connexion/enregistrement nécessaire)");
            System.out.println("4. Jouer en mode invité (un seul joueur)");
            System.out.println("5. Afficher l'historique de mes matchs"); // NEW OPTION
            System.out.println("6. Quitter"); // Changed from 5 to 6

            System.out.print("Votre choix: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide. Veuillez entrer un nombre.");
                continue; // Continue loop to re-prompt
            }

            switch (choice) {
                case 1: // Login for Player 1
                    player1Account = handleLogin("Player 1");
                    if (player1Account != null) {
                        System.out.println("Player 1 (" + player1Account.getUsername() + ") est connecté.");
                        // After login, give options
                        showLoggedInMenu(); // New function for options after login
                    } else {
                        System.out.println("Échec de la connexion. Veuillez réessayer ou créer un compte.");
                    }
                    break;
                case 2: // Register a new user
                    handleRegistration();
                    break;
                case 3: // Start a game for two registered players
                    System.out.println("\n--- Connexion pour le Joueur 1 ---");
                    player1Account = handleLogin("Player 1");
                    if (player1Account == null) {
                        System.out.println("Connexion du Joueur 1 échouée. Impossible de démarrer une partie à deux joueurs.");
                        break;
                    }

                    System.out.println("\n--- Connexion pour le Joueur 2 ---");
                    player2Account = handleLogin("Player 2");
                    if (player2Account == null) {
                        System.out.println("Connexion du Joueur 2 échouée. Impossible de démarrer une partie à deux joueurs.");
                        player1Account = null; // Reset P1 account too
                        break;
                    }

                    if (player1Account.getId() == player2Account.getId()) {
                        System.out.println("Erreur: Les deux joueurs ne peuvent pas être le même compte. Veuillez utiliser des comptes différents.");
                        player1Account = null;
                        player2Account = null;
                        break;
                    }
                    System.out.println("Démarrage de la partie entre " + player1Account.getUsername() + " et " + player2Account.getUsername() + ".");
                    startGame();
                    break;
                case 4: // Play as guest
                    System.out.println("Démarrage du jeu en mode invité (Joueur 1 = Invité1, Joueur 2 = Invité2).");
                    player1Account = new Compte(0, "Invité1", ""); // Dummy accounts for guest mode
                    player2Account = new Compte(0, "Invité2", "");
                    startGame();
                    break;
                case 5: // Display match history
                    // This option is accessible directly from main menu
                    // It should prompt for username if no one is logged in, or use player1Account if available
                    if (player1Account != null) {
                        displayUserMatchHistory(player1Account.getUsername());
                    } else {
                        System.out.print("Veuillez entrer le nom d'utilisateur dont vous souhaitez voir l'historique: ");
                        String username = scanner.nextLine();
                        displayUserMatchHistory(username);
                    }
                    break;
                case 6: // Exit (now option 6)
                    System.out.println("Merci d'avoir joué !");
                    break;
                default:
                    System.out.println("Choix invalide. Veuillez entrer un nombre entre 1 et 6."); // Updated range
            }
        }
    }

    // New menu after a player logs in
    private static void showLoggedInMenu() {
        int choice = -1;
        while (choice != 3) {
            System.out.println("\nOptions pour " + player1Account.getUsername() + ":");
            System.out.println("1. Démarrer une nouvelle partie (Joueur 2 devra se connecter ou sera Invité)");
            System.out.println("2. Afficher mon historique de matchs");
            System.out.println("3. Retour au menu principal");

            System.out.print("Votre choix: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide. Veuillez entrer un nombre.");
                continue;
            }

            switch (choice) {
                case 1:
                    setupGameForTwoPlayersOrGuest(); // This function already handles Player 2 setup
                    return; // Return to main menu after game ends or setup is cancelled
                case 2:
                    displayUserMatchHistory(player1Account.getUsername());
                    break;
                case 3:
                    // Return to main menu
                    break;
                default:
                    System.out.println("Choix invalide. Veuillez entrer un nombre entre 1 et 3.");
            }
        }
    }


    private static void setupGameForTwoPlayersOrGuest() {
        System.out.println("\nQue voulez-vous faire ensuite?");
        System.out.println("1. Connecter le Joueur 2");
        System.out.println("2. Jouer seul (Joueur 2 sera 'Invité2')");
        int subChoice = -1;
        while (subChoice < 1 || subChoice > 2) {
            System.out.print("Votre choix: ");
            try {
                subChoice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide. Veuillez entrer 1 ou 2.");
            }
        }

        if (subChoice == 1) {
            player2Account = handleLogin("Player 2");
            if (player2Account == null) {
                System.out.println("Connexion du Joueur 2 échouée. Le Joueur 2 sera 'Invité2'.");
                player2Account = new Compte(0, "Invité2", "");
            } else if (player1Account != null && player1Account.getId() == player2Account.getId()) { // Check if player1Account is not null
                System.out.println("Erreur: Les deux joueurs ne peuvent pas être le même compte. Le Joueur 2 sera 'Invité2'.");
                player2Account = new Compte(0, "Invité2", ""); // Reset player2 if it's the same
            }
        } else {
            player2Account = new Compte(0, "Invité2", ""); // Player 2 is a guest
        }
        startGame();
    }

    private static Compte handleLogin(String playerLabel) {
        System.out.print("Entrez le nom d'utilisateur pour " + playerLabel + ": ");
        String username = scanner.nextLine();
        System.out.print("Entrez le mot de passe pour " + playerLabel + ": ");
        String password = scanner.nextLine();
        return compteDAO.login(username, password); // Use CompteDAO login
    }

    private static void handleRegistration() {
        System.out.print("Choisissez un nom d'utilisateur: ");
        String username = scanner.nextLine();

        String password;
        String confirmPassword;
        while (true) {
            System.out.print("Choisissez un mot de pass: ");
            password = scanner.nextLine();
            System.out.print("Confirmez le mot de pass: ");
            confirmPassword = scanner.nextLine();

            if (password.equals(confirmPassword)) {
                if (password.isEmpty()) {
                    System.out.println("Le mot de passe ne peut pas être vide.");
                } else {
                    break;
                }
            } else {
                System.out.println("Les mots de passe ne correspondent pas. Veuillez réessayer.");
            }
        }
        try {
            if (compteDAO.createAccount(username, password)) { // Use CompteDAO createAccount
                System.out.println("Compte créé avec succès pour " + username + " !");
            } else {
                System.out.println("Échec de la création du compte."); // Should be caught by DatabaseException
            }
        } catch (jungleGame.bdd.DatabaseException e) {
            System.out.println("Erreur lors de la création du compte: " + e.getMessage());
        }
    }

    private static void startGame() {
        // Initialize GameLogic with player accounts
        gameLogic = new GameLogic(player1Account, player2Account);

        // --- Main Game Loop ---
        while (!gameLogic.isGameOver()) {
            gameLogic.displayBoard(); // Display the current board state
            String currentPlayerName = (gameLogic.getCurrentPlayer() == 1) ? player1Account.getUsername() : player2Account.getUsername();
            System.out.println("Tour du joueur: " + currentPlayerName + " (Joueur " + gameLogic.getCurrentPlayer() + ")");
            System.out.print("Entrez le mouvement (ex: A0 B0 pour déplacer de (0,0) à (0,1)): ");
            String command = scanner.nextLine();

            // Special commands
            if (command.equalsIgnoreCase("history")) {
                displayGameHistory(currentPlayerName);
                continue;
            }
            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Partie terminée par l'utilisateur.");
                break;
            }
            if (command.equalsIgnoreCase("show my match history")) { // Option to view personal history within game
                displayUserMatchHistory(currentPlayerName);
                continue;
            }


            if (!gameLogic.performMove(command)) {
                System.out.println("Mouvement invalide ! Réessayez.");
            }
        }

        System.out.println("Fin de la partie.");
        // The game winner and saving to DB is handled within Board.endGame()
        // No need to display current game history again here, as endGame already saves to DB
        // If you want to show it again, use: displayGameHistory(currentPlayerName);

        // Reset player accounts after a game ends
        player1Account = null;
        player2Account = null;
    }

    private static void displayGameHistory(String currentPlayerName) {
        System.out.println("\n--- Historique de la partie actuel ---");
        List<String> events = gameLogic.getMatchHistory();
        if (events.isEmpty()) {
            System.out.println("Aucun événement enregistré pour cette partie.");
        } else {
            for (String event : events) {
                System.out.println("  " + event);
            }
        }
        System.out.println("-----------------------------------------\n");
    }

    private static void displayUserMatchHistory(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Impossible d'afficher l'historique. Nom d'utilisateur non fourni.");
            return;
        }
        System.out.println("\n--- Historique des matchs pour " + username + " ---");
        // MatchHistoryDAO.getMatchHistory prints the history directly, no need to store in a list here
        MatchHistoryDAO.getMatchHistory(username); 
        System.out.println("-----------------------------------------------\n");
    }
}