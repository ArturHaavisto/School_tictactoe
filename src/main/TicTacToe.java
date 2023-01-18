package main;

import main.util.*;

/**
 * The main class of the game.
 * 
 * @author Artur Haavisto
 * @version 2020.1214
 * @since 15.0.1
 */
public class TicTacToe {

    private static main.util.MyWindow window;
    private static main.util.Computer computer;

    /**
     * Default window sizes. 
     */ 
    private final static int windowSizeX = 800;
    private final static int windowSizeY = 300;
    private final static int newWindowSizeY = 920;


    private final static char playerSymbol = 'X';
    private final static char computerSymbol = 'O';
    private final static String message1 = "Board size";
    private final static String message2 = "How many to win";
    private final static String valueError = "-Value must be between ";
    private final static String formatError = "-You must give an integer!";
    private final static String windowTitle = "TicTacToe";
    private static String [] messages = {message1, message2};

    /**
     * Game conditions. 
     */ 
    private static int boardSizeMin = 3;
    private static int boardSizeMax = 200;
    private static int boardSize;
    private static int winCondMin = 1;
    private static int winCond;

    /**
     * If true, player starts. If false, computer starts.
    */
    private final static boolean playerStart = true; 

    private final static int playerInt = 1;
    private final static int computerInt = 2;
    private static boolean player = playerStart;
    private static boolean tie = false;
    private static int turnCounter = 0;
    private static int gameSituation [][];

    /**
     * Creates the game window.
     * 
     * @param args Command line parameters. Not used.
     */
    public static void main(String [] args) {
        window = new main.util.MyWindow(windowSizeX, windowSizeY, windowTitle);
        window.setSymbols(playerSymbol, computerSymbol);
    }

    /**
     * Checks if given gamesize and winning condition are within limits.
     * 
     * Returning integers between 1 and 4 means that something wasn't right.
     * Returning integer 5 means that parameters were accepted.
     * 
     * @param gameSize Size of the gameboard.
     * @param winCondStr Number of consecutive positions to win.
     * @return Integer between 1 and 5.
     */
    public static int checkGameSettings(String gameSize, String winCondStr) {
        boardSizeMin = 3;
        winCondMin = 1;
        int check = main.util.MyCheck.checkGameSettings(gameSize, winCondStr,
                                    boardSizeMin, boardSizeMax, winCondMin);

        if (check > 2) {
            boardSize = Integer.parseInt(gameSize);
            if (boardSize >= 10) {winCondMin = 5;}
        }
        if (check > 4) {
            winCond = Integer.parseInt(winCondStr);
            createComputer();
            return 5;
        } else {
            return check;
        }
    }

    public static String [] getMessages() {
        return messages;
    }

    public static String getValueErrorBoardSize() {
        return valueError + boardSizeMin + " and " + boardSizeMax + "!";
    }

    public static String getValueErrorWinCond() {
        return valueError + winCondMin + " and " + boardSize + "!";
    }

    public static String getFormatError() {
        return formatError;
    }

    /**
     * Sets program to beginning state and sets buttons for the game.
     */
    public static void setGameButtons() {
        gameSituation = new int [boardSize][boardSize];
        resetGame();
        window.resetGame();
        window.setPlayer(playerStart);
        window.setSize(windowSizeX, newWindowSizeY);
        window.setLocationRelativeTo(null);
        window.createButtons(boardSize);
        if (!player) {
            computer.computerTurn(-1, 0);
        }
    }

    public static void createComputer() {
        computer = new main.util.Computer(boardSize, winCond, computerInt,
                                          playerInt);
    }

    /**
     * Takes an action from the computer or from the player.
     * 
     * Updates the gameSituation 2d-array.
     * Checks if the given action lead to victory.
     * If the gameboard is full, concludes a draw.
     * Gives turn to the other.
     * 
     * @param row Y-coordinate to 2d-array.
     * @param column X-coordinate to 2d-array.
     */
    public static void takeAction(int row, int column) {
        turnCounter++;

        if (player) {
            gameSituation[row][column] = playerInt;
        } else {
            gameSituation[row][column] = computerInt;
            window.setComputerMove(row, column);
        }
        if (main.util.MyCheck.ifWin(gameSituation, row, column, winCond)) {
            window.endGame(getVictoryText(), player, tie);
            return;
        }
        if (turnCounter >= boardSize * boardSize) {
            tie = true;
            window.endGame(getVictoryText(), player, tie);
            return;
        } 

        player = !player;

        if (!player) {
            computer.computerTurn(row, column);
        }
    }

    /**
     * Returns an ending message depending on the situation.
     * 
     * @return Game ending message.
     */
    public static String getVictoryText() {
        if (tie) {
            return "It's a draw!";
        }
        else if (player) {
            return "Player has won!";
        } else {
            return "Computer has won!";
        }
    }

    /**
     * Resets some variables to beginning state.
     */
    public static void resetGame() {
        player = playerStart;
        turnCounter = 0;
        tie = false;
    }
}