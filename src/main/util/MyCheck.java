package main.util;

/**
 * The class contains methods which check something and returs the answer.
 * 
 * @author Artur Haavisto
 * @version 2020.1214
 * @since 15.0.1
 */
public class MyCheck {

    /**
     * Checks that given values are within bounds.
     * 
     * Returning integers between 1 and 4 means that something wasn't right.
     * Returning integer 5 means that parameters were accepted.
     * 
     * @param gameSize Size of the gameboard.
     * @param winCond Number of consecutive positions to win.
     * @param sizeMin Minimum size of the gameboard.
     * @param sizeMax Maximum size of the gameboard.
     * @param minCond Minimum winning condition.
     * @return Integer between 1 and 5 depending on results.
     */
    public static int checkGameSettings(String gameSize, String winCond,
                                        int sizeMin, int sizeMax, int minCond) {

        int gameSizeInt;

        try {
            gameSizeInt = Integer.parseInt(gameSize);
            if (gameSizeInt < sizeMin || gameSizeInt > sizeMax) {
                return 1;
            }
        } catch (NumberFormatException e) {
            return 2;
        }
        if (gameSizeInt >= 10) {minCond = 5;}

        try {
            int winCondInt = Integer.parseInt(winCond);
            if (winCondInt < minCond || winCondInt > gameSizeInt) {
                return 3;
            }
        } catch (NumberFormatException e) {
            return 4;
        }
        return 5;
    }

    /**
     * Checks if there is enough consecutive marks for the win.
     * 
     * Checks only if the given coordinates bring victory.
     * Checking is done horizontally, vertically and both ways diagonally.
     * 
     * @param gameSituation 2d-array containing integers.
     * @param latestRow Y-coordinate to the 2d-array.
     * @param latestColumn X-coordinate to the 2d-array.
     * @param winCond Number of consecutive marks for the win.
     * @return True if winning condition was met, false if not.
     */
    public static boolean ifWin(int [][] gameSituation, int latestRow,
                                int latestColumn, int winCond) {
        int streak = 0;
        int size = gameSituation.length;
        int mark = gameSituation[latestRow][latestColumn];
        int minX = latestColumn - winCond + 1;
        int maxX = latestColumn + winCond - 1;
        if (minX < 0) {minX = 0;}
        if (maxX > size - 1) {maxX = size - 1;}

        for (int i = minX; i <= maxX; i++) {
            if (gameSituation[latestRow][i] == mark) {
                streak++;
            } else {
                streak = 0;
            }
            if (streak == winCond) {
                return true;
            }
        }
        streak = 0;
        int minY = latestRow - winCond + 1;
        int maxY = latestRow + winCond - 1;
        if (minY < 0) {minY = 0;}
        if (maxY > size - 1) {maxY = size - 1;}

        for (int i = minY; i <= maxY; i++) {
            if (gameSituation[i][latestColumn] == mark) {
                streak++;
            } else {
                streak = 0;
            }
            if (streak == winCond) {
                return true;
            }
        }
        streak = 0;
        int min = minX;
        int max = maxX;
        if (latestRow - minY < latestColumn - minX) {
            min = latestColumn - (latestRow - minY);
        }
        if (maxY - latestRow < maxX - latestColumn) {
            max = latestColumn + (maxY - latestRow);
        }
        int Ycounter = latestRow - (latestColumn - min);

        for (int i = min; i <= max; i++) {
            
            if (gameSituation[Ycounter][i] == mark) {
                streak++;
            } else {
                streak = 0;
            }
            if (streak == winCond) {
                return true;
            }
            Ycounter++;
        }
        
        streak = 0;
        min = minX;
        max = maxX;
        if (maxY - latestRow < latestColumn - minX) {
            min = latestColumn - (maxY - latestRow);
        }
        if (latestRow - minY < maxX - latestColumn) {
            max = latestColumn + (latestRow - minY);
        }
        Ycounter = latestRow + (latestColumn - min);

        for (int i = min; i <= max; i++) {
            if (gameSituation[Ycounter][i] == mark) {
                streak++;
            } else {
                streak = 0;
            }
            if (streak == winCond) {
                return true;
            }
            Ycounter--;
        }
        return false;
    }
} 

