package main.util;

/**
 * The class contains the "decision" making of the computer opponent.
 * 
 * @author Artur Haavisto
 * @version 2020.1214
 * @since 15.0.1
 */
public class Computer {

    /**
     * Game states and settings. 
     */ 
    private int boardSize;
    private int winCond;
    private int computer;
    private int player;

    /**
     * This array contains the given moves of computer and player.
     * It also contains information of every spot that is next to 
     * either computer, player or them both.
     */
    private int [][] possibleWin;
    
    /**
     * Variables that are used to calculate some turns away.
     */
    private final int possibleCom = 3;
    private final int possiblePla = 4;
    private final int possibleBoth = 5;
    private final int possibleTurn = 6;
    private int storedInt1 = 0;
    private int storedInt2 = 0;
    private int storedInt3 = 0;
    private int storedInt4 = 0;
    private boolean turn3 = false;
    private boolean turn4 = false;

    /**
     * Class constructor.
     * 
     * 
     * @param getBoardSize Size of the gameboard.
     * @param getWinCond Number of consecutive marks for the win.
     * @param computerInteger Integer that is used to indicate computer.
     * @param playerInteger Integer that is used to indicate player.
     */
    public Computer(int getBoardSize, int getWinCond, int computerInteger,
                    int playerInteger) {

        boardSize = getBoardSize;
        winCond = getWinCond;
        //gameSituation = new int [boardSize][boardSize];
        possibleWin = new int [boardSize][boardSize];
        computer = computerInteger;
        player = playerInteger;
    }

    /**
     * Determines the best move for computer.
     * 
     * Takes player's move as parameters and updates those to 2d-array.
     * Goes through different calculations in order of importance.
     * 
     * @param row Y-coordinate.
     * @param column X-coordinate.
     */
    public void computerTurn(int row, int column) {
        if (row != -1) {
            updatePossibleWin(row, column, player, false, false);

            if (checkWin(possibleCom, computer, false, 0)) {return;}
            if (checkWin(possiblePla, player, false, 0)) {return;}

            if (checkCertainWin2(possibleCom, computer, 2)) {return;}
            if (checkCertainWin2(possiblePla, player, 2)) {return;}

            if (boardSize != 3) {
                if (checkCertainWin3(possibleCom, computer, 4)) {return;}
                if (checkCertainWin3(possiblePla, player, 4)) {return;}

                if (checkCertainWin4(possibleCom, computer, 4)) {return;}
                if (checkCertainWin4(possiblePla, player, 4)) {return;}

                if (checkCertainWin5(possibleCom, computer, 4)) {return;}
                if (checkCertainWin5(possiblePla, player, 4)) {return;}
            }
        }
        makeRandomMove();

    }


    /**
     * Updates the possibleWin 2d-array.
     * 
     * It either adds or takes away integers.
     * 
     * @param row Y-coordinate to the 2d-array.
     * @param column X-coordinate to the 2d-array.
     * @param value Integer that represents computer or player.
     * @param secTurn Tells if this is test update or real update.
     * @param restore Tells if 2d-array is to be set to a previous state.
     */
    public void updatePossibleWin(int row, int column, int value,
                                  boolean secTurn, boolean restore) {

        boolean isComputer = false;
        int possibleValue = possibleCom;
        int possibleValue2 = possiblePla;

        if (!restore) {
            if (computer == value) {
                isComputer = true;
                possibleWin[row][column] = computer;
            }
            if (!isComputer) {
                possibleValue = possiblePla;
                possibleValue2 = possibleCom;
                possibleWin[row][column] = player;
            } 
        } 
        else if (storedInt2 == 0 && storedInt1 == 0 && storedInt3 == 0) {
            possibleWin[row][column] = storedInt4;
        }
        else if (storedInt1 == 0 && storedInt2 == 0) {
            possibleWin[row][column] = storedInt3;
        } 
        else if (storedInt1 == 0) {
            possibleWin[row][column] = storedInt2;
        } else {
            possibleWin[row][column] = storedInt1;
        }

        int minX = column - 1;
        int maxX = column + 1;
        if (minX < 0) {minX = 0;}
        if (maxX > boardSize - 1) {maxX = boardSize - 1;}

        int minY = row - 1;
        int maxY = row + 1;
        if (minY < 0) {minY = 0;}
        if (maxY > boardSize - 1) {maxY = boardSize - 1;}

        for (int i = minY; i <= maxY; i++) {
            for (int j = minX; j <= maxX; j++) {
                int number = possibleWin[i][j];
                if (number == 0 && !secTurn && !restore) {
                    possibleWin[i][j] = possibleValue;
                }
                else if (number ==  possibleValue2) {
                    possibleWin[i][j] = possibleBoth;
                }
                else if (number == 0 && secTurn) {
                    possibleWin[i][j] = possibleTurn;
                }
                else if (number == possibleTurn && restore) {
                    possibleWin[i][j] = 0;
                }
            }
        }
    }

    /**
     * Goes through possible coordinates and checks if they bring victory.
     * 
     * Goes through the possibleWin array and if there is right integer there, 
     * the value is inserted in its place and the array is sended for
     * a victory check. If it comes back true, move will be done to those
     * coordinates which caused the victory.
     * 
     * @param possibleValue A value which represents a possible move.
     * @param value A value which represents either computer or player.
     * @param secTurn Tells if this is a win check for more than one turn away.
     * @param howMany Tells how many different winning positions there must be.
     * @return True if victory move was found, false if not.
     */
    public boolean checkWin(int possibleValue,int value, boolean secTurn,
                            int howMany) {

        int numOfWins = 0;
        for (int i=0; i < boardSize; i++) {
            for (int j=0; j < boardSize; j++) {
                if (possibleWin[i][j] == possibleValue
                        || possibleWin[i][j] == possibleBoth
                        || possibleWin[i][j] == possibleTurn) {

                    int number = possibleValue;
                    if (possibleWin[i][j] == possibleBoth) {
                        number = possibleBoth;
                    }
                    else if (possibleWin[i][j] == possibleTurn) {
                        number = possibleTurn;
                    }
                    possibleWin[i][j] = value;
                    if (MyCheck.ifWin(possibleWin, i, j, winCond)) {
                        numOfWins++;
                    }
                    if (numOfWins > 0 && !secTurn) {
                        makeMove(i, j);
                        return true;
                    }
                    possibleWin[i][j] = number;
                } 
            }
        }
        if (numOfWins >= howMany && secTurn) {
            return true;
        }
        return false;
    }

    /**
     * Checks if there is a certain victory in two turns.
     * 
     * Goes through every integer in possibleWin and if it is not a made move
     * or empty, the value is updated to it. Then the checkWin() funcion is 
     * called and checked that if the added value brings atleast howMany 
     * amount of wins. PossibleWin array is being restored back to its
     * previous state after that.
     * 
     * @param possibleValue A value which represents a possible move.
     * @param value A value which represents either computer or player.
     * @param howMany Tells how many different winning positions there must be.
     * @return True if there is a spot which brings victory, false if not.
     */
    public boolean checkCertainWin2(int possibleValue, int value, int howMany) {
        for (int i=0; i < boardSize; i++) {
            for (int j=0; j < boardSize; j++) {
                storedInt1 = 0;
                storedInt1 += possibleWin[i][j];
                if (storedInt1 != 0 && storedInt1 != computer
                        && storedInt1 != player) {

                    updatePossibleWin(i, j, value, true, false);
                    if (checkWin(possibleValue, value, true, howMany)) {
                        if (howMany == 4) {
                            updatePossibleWin(i, j, value, false, true);
                            return true;
                            
                        } else {
                            updatePossibleWin(i, j, value, false, true);
                            makeMove(i, j);
                            return true;
                        }
                    }
                    updatePossibleWin(i, j, value, false, true);
                }
                storedInt1 = 0;
            }
        }
        return false;
    }

    /**
     * Checks if there is a certain victory in three turns.
     * 
     * Goes through every integer in possibleWin and if it is not a made move
     * or empty, the value is updated to it. It calls checkCertainWin2()
     * function which will check victory to another two turns away.
     * PossibleWin array is restored to its previous state after.
     * 
     * 
     * @param possibleValue A value which represents a possible move.
     * @param value A value which represents either computer or player.
     * @param howMany Tells how many different winning positions there must be.
     * @return True if there is a spot which brings victory, false if not.
     */
    public boolean checkCertainWin3(int possibleValue, int value, int howMany) {
        for (int i=0; i < boardSize; i++) {
            for (int j=0; j < boardSize; j++) {
                storedInt2 = 0;
                storedInt2 += possibleWin[i][j];
                if (storedInt2 != 0 && storedInt2 != computer
                        && storedInt2 != player) {

                    updatePossibleWin(i, j, value, true, false);
                    if (checkCertainWin2(possibleValue, value, howMany)) {
                        if (turn3) {
                            updatePossibleWin(i, j, value, false, true);
                            return true;
                        } else {
                            updatePossibleWin(i, j, value, false, true);
                            makeMove(i, j);
                            return true;
                        }
                    }
                    updatePossibleWin(i, j, value, false, true);
                }
                storedInt2 = 0;
            }
        }
        return false;
    }

    /**
     * Checks if there is a certain victory in four turns.
     * 
     * Goes through every integer in possibleWin and if it is not a made move
     * or empty, the value is updated to it. It calls checkCertainWin3()
     * function which will check victory to another three turns away.
     * PossibleWin array is restored to its previous state after.
     * 
     * 
     * @param possibleValue A value which represents a possible move.
     * @param value A value which represents either computer or player.
     * @param howMany Tells how many different winning positions there must be.
     * @return True if there is a spot which brings victory, false if not.
     */
    public boolean checkCertainWin4(int possibleValue, int value, int howMany) {
        turn3 = true;
        for (int i=0; i < boardSize; i++) {
            for (int j=0; j < boardSize; j++) {
                storedInt3 = 0;
                storedInt3 += possibleWin[i][j];
                if (storedInt3 != 0 && storedInt3 != computer
                        && storedInt3 != player) {

                    updatePossibleWin(i, j, value, true, false);
                    if (checkCertainWin3(possibleValue, value, howMany)) {
                        if (turn4) {
                            updatePossibleWin(i, j, value, false, true);
                            turn3 = false;
                            return true;
                        } else {
                            updatePossibleWin(i, j, value, false, true);
                            makeMove(i, j);
                            turn3 = false;
                            return true;
                        }
                    }
                    updatePossibleWin(i, j, value, false, true);
                }
                storedInt3 = 0;
            }
        }
        turn3 = false;
        return false;
    }

    /**
     * Checks if there is a certain victory in five turns.
     * 
     * Goes through every integer in possibleWin and if it is not a made move
     * or empty, the value is updated to it. It calls checkCertainWin4()
     * function which will check victory to another four turns away.
     * PossibleWin array is restored to its previous state after.
     * 
     * 
     * @param possibleValue A value which represents a possible move.
     * @param value A value which represents either computer or player.
     * @param howMany Tells how many different winning positions there must be.
     * @return True if there is a spot which brings victory, false if not.
     */
    public boolean checkCertainWin5(int possibleValue, int value, int howMany) {
        turn4 = true;
        for (int i=0; i < boardSize; i++) {
            for (int j=0; j < boardSize; j++) {
                storedInt4 = 0;
                storedInt4 += possibleWin[i][j];
                if (storedInt4 != 0 && storedInt4 != computer
                        && storedInt4 != player) {

                    updatePossibleWin(i, j, value, true, false);
                    if (checkCertainWin4(possibleValue, value, howMany)) {
                        updatePossibleWin(i, j, value, false, true);
                        makeMove(i, j);
                        turn4 = false;
                        return true;
                    }
                    updatePossibleWin(i, j, value, false, true);
                }
                storedInt4 = 0;
            }
        }
        turn4 = false;
        return false;
    }

    /**
     * Goes through every possible coordinates and selects one in random.
     * 
     * If there has not been made any moves yet, the random coordinate is
     * selected from the whole gameboard.
     */
    public void makeRandomMove() {
        int size = 0;
        for (int i=0; i < boardSize; i++) {
            for (int j=0; j < boardSize; j++) {
                if (possibleWin[i][j] != 0 && possibleWin[i][j] != computer
                        && possibleWin[i][j] != player) {

                    size++;
                }
            }
        }

        if (size == 0) {size = boardSize * boardSize;}
        int [][] random = new int [size][2];

        int counter = 0;
        for (int i=0; i < boardSize; i++) {
            for (int j=0; j < boardSize; j++) {
                if ((possibleWin[i][j] != 0 && possibleWin[i][j] != computer
                        && possibleWin[i][j] != player)
                        || size == boardSize * boardSize) {

                    random[counter][0] = i;
                    random[counter][1] = j;
                    counter++;
                }
            }
        }
        int randomNum = (int) (Math.random() * size);
        makeMove(random[randomNum][0], random[randomNum][1]);
    }

    /**
     * Makes the selected move.
     * 
     * Updates the selected move to possibleWin array.
     * Sends selected coordinates to main program.
     * 
     * @param row Selected Y-coordinate.
     * @param column Selected X-coordinate.
     */
    public void makeMove(int row, int column) {
        updatePossibleWin(row, column, computer, false, false);
        main.TicTacToe.takeAction(row, column);
    }
}
