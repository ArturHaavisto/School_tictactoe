package main.util;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

/**
 * The game window which the user will be interacting with.
 * 
 * @author Artur Haavisto
 * @version 2020.1214
 * @since 15.0.1
 */
public class MyWindow extends JFrame implements ActionListener,
                      ComponentListener, KeyListener, MouseListener {

    private MyWindow window;

    private JLayeredPane lpane;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JPanel sideLeft;
    private JPanel sideBottom;
    private JPanel sideRight;
    private JPanel mapBottom;
    private JPanel mapTop;
    private JPanel victory;

    /**
     * Fields and labels on panel1. 
     */
    private JTextField gameSize;
    private JTextField winCond;
    private JLabel sizeLabel;
    private JLabel condLabel;
    private JLabel valueError;
    private JLabel formatError;
    private JLabel victoryLabel;
    private KeyListener keyListener;

    /**
     * Gamesetting bounds on panel1. 
     */ 
    private static int sizeX = 200;
    private static int sizeY = 30;
    private static int firstY = 10;
    private static int secondY = 40;
    private static int thirdY = 65;

    /**
     * Victory panel colors.
     */ 
    private Color victoryPlayer = Color.GREEN;
    private Color victoryComputer = Color.RED;
    private Color victoryTie = Color.ORANGE;

    /**
     * Game states and settings.
     */ 
    private JButton [][] buttons;
    private int boardSize;
    private int sizeOfOne;
    private boolean showMap = false;
    private boolean isMap = false;
    private JButton start;
    private static char symbol1;
    private static char symbol2;
    private boolean player;
    private boolean gameCreated = false;
    private boolean gameEnded = false;

    /**
     * Variables that are used to navigate the board. 
     */ 
    private boolean ifRun = false;
    private Thread thread1;
    private Thread thread2;
    private Thread thread3;
    private Thread thread4;
    private Thread buttonLocChange;
    private boolean pressedW = false;
    private boolean pressedS = false;
    private boolean pressedA = false;
    private boolean pressedD = false;
    private boolean threadLoc = false;
    private final int timeDelay = 50;
    private final int multiplier = 2;
    private int currentMultiplier = 1;
    private int changeX = 0;
    private int changeY = 0;
    private int keysPressed = 0;
    private final int change = 5;
    private int counter = 0;

    /**
     * Constructor
     * 
     * Adds the game-creating panel and it's components.
     * 
     * @param x Width of the window.
     * @param y Height of the window.
     * @param title Title of the window.
     */
    public MyWindow(int x, int y, String title) {
        window = this;
        setTitle(title);
        setSize(x, y);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(x,y));
        lpane = new JLayeredPane();
        add(lpane, BorderLayout.CENTER);
        createPanel1();
        createPanel2();
        createSidePanels();
        labels();
        textFields();
        startButton();
        addMouseListener(this);
        setVisible(true);

        addComponentListener(new ComponentAdapter() {

            /**
             * If the size of the window is changed, the components on
             * that will move and change dynamically.
             */
            public void componentResized(ComponentEvent componentEvent) {

                panel1.setLocation(getPanel1LocX(), 0);
                setPanel2Bounds();
                sideLeft.setBounds(0, 0, 20, window.getHeight());
                sideBottom.setBounds(0, window.getHeight() - 60,
                                     window.getWidth(), 60);
                sideRight.setBounds(window.getWidth() - 36, 0, 20,
                                    window.getHeight());

                if (panel3 != null) {
                    panel3.setBounds(20, 120, window.getWidth() - 56,
                                     window.getHeight() - 176);
                    setMapTopBounds();
                    setMapBottomLocation();
                }
                if (buttons != null) {
                    setButtonBounds(false);
                }
                if (gameEnded) {
                    victory.setLocation(getPanel1LocX(), thirdY + 5);
                }
            }
        });
    }

    /**
     * Resets the window to it's default state.
     */
    public void resetGame() {
        if (gameCreated) {
            lpane.remove(panel3);
            lpane.remove(mapBottom);
            lpane.remove(mapTop);
            if (threadLoc) {
                buttonLocChange.stop();
            }
            if (gameEnded) {
                lpane.remove(victory);
                gameEnded = false;
            }
            window.revalidate();
            window.repaint();
        }

        threadLoc = false;
        showMap = false;
        isMap = false;
        ifRun = false;
        pressedA = false;
        pressedD = false;
        pressedS = false;
        pressedW = false;
        currentMultiplier = 1;
        changeX = 0;
        changeY = 0;
        keysPressed = 0;
        counter = 0;
    } 

    /**
     * Brings the window to it's victory state.
     * 
     * @param text Victory text.
     * @param playerWon Which side won.
     * @param tie If it was a draw.
     */
    public void endGame(String text, boolean playerWon, boolean tie) {

        if (tie) {
            createVictoryPanel(text, playerWon, tie);
        } else {
            pressButtons();
            createVictoryPanel(text, playerWon, tie);
        }
        gameEnded = true;
    }

    /**
     * Sets all remaining buttons to pressed state.
     */
    public void pressButtons() {
        for (int i=0; i < buttons.length; i++) {
            for (int j=0; j < buttons[i].length; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    public void resizeWindow(int x, int y) {
        this.setSize(x, y);
    }

    /**
     * Sets symbols to represent computer and player.
     * 
     * @param symb1 Character that represents player.
     * @param symb2 Character that represents computer.
     */
    public void setSymbols(char symb1, char symb2) {
        symbol1 = symb1;
        symbol2 = symb2;
    }

    /**
     * Sets state if it's player's turn or not.
     * 
     * @param b Player's turn status.
     */
    public void setPlayer(boolean b) {
        player = b;
    }

    /**
     * Creates the game making panel.
     */
    public void createPanel1() {
        panel1 = new JPanel();
        panel1.setLayout(null);
        panel1.setBounds(0, 0, 800, 100);
        panel1.setBackground(Color.GRAY);
        lpane.add(panel1, new Integer(3), 0);
        keyListener = new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    start.doClick();
                }
            }
    
            public void keyReleased(KeyEvent e) {}

            public void keyTyped(KeyEvent e) {}
        };
    }

    /**
     * Creates the background panel.
     */
    public void createPanel2() {
        panel2 = new JPanel();
        setPanel2Bounds();
        panel2.setBackground(Color.CYAN);
        lpane.add(panel2, new Integer(0), 0);
    }

    /**
     * Creates the panel where the game buttons are.
     */
    public void createPanel3() {
        panel3 = new JPanel();
        panel3.setLayout(null);
        panel3.setBounds(20, 120, this.getWidth() - 56, this.getHeight() - 176);
        panel3.setBackground(Color.CYAN);
        lpane.add(panel3, new Integer(1), 0);
        addKeyListener(this);
    }

    /**
     * Creates the side panels around the buttons panel.
     */
    public void createSidePanels() {
        sideLeft = new JPanel();
        sideLeft.setBounds(0, 0, 20, this.getHeight());
        sideLeft.setBackground(Color.CYAN);
        lpane.add(sideLeft, new Integer(2), 0);

        sideBottom = new JPanel();
        sideBottom.setBounds(0, this.getHeight() - 60, this.getWidth(), 60);
        sideBottom.setBackground(Color.CYAN);
        lpane.add(sideBottom, new Integer(2), 0);

        sideRight = new JPanel();
        sideRight.setBounds(this.getWidth() - 36, 0, 20, this.getHeight());
        sideRight.setBackground(Color.CYAN);
        lpane.add(sideRight, new Integer(2), 0);
    }

    /**
     * Creates the minimap panels.
     */
    public void createMapPanels() {
        mapBottom = new JPanel();
        mapBottom.setBackground(new Color(0,0,0,40));
        mapBottom.setBounds(this.getWidth() - 186, this.getHeight() - 210,
                            150, 150);

        mapTop = new JPanel();
        mapTop.setBackground(new Color(0,0,0,70));
        setMapTopBounds();
    }

    /**
     * Creates the victory panel.
     * 
     * @param text Game ending text.
     * @param playerWon Tells if the player or computer won.
     * @param tie Tells if it was a draw.
     */
    public void createVictoryPanel(String text, boolean playerWon,
                                   boolean tie) {
        victory = new JPanel();
        if (tie) {
            victory.setBackground(victoryTie);
        }
        else if (playerWon) {
            victory.setBackground(victoryPlayer);
        } else {
            victory.setBackground(victoryComputer);
        }
        victory.setBounds(getPanel1LocX(), thirdY + 5, 800, 50);
        lpane.add(victory, new Integer(5), 0);
        setVictoryLabel(text);
    }

    public int getPanel1LocX() {
        return this.getWidth() / 2 - 408;
    }

    public void setPanel2Bounds() {
        panel2.setBounds(0, 0, this.getWidth(), this.getHeight());
    }

    /**
     * Sets top of the map's bounds.
     */
    public void setMapTopBounds() {
        int boardLength = boardSize * sizeOfOne;
        int xLoc = this.getWidth() - 186 + (int) ((double)
                   (panel3.getX() - 8 - buttons[0][0].getX())
                   / (boardSize * sizeOfOne) * 150);
        int yLoc = this.getHeight() - 210 + (int) ((double)
                   (panel3.getY() - 120 - buttons[0][0].getY())
                   / (boardSize * sizeOfOne) * 150);
        int xSize = (int) ((double) panel3.getWidth() / boardLength * 150);
        int ySize = (int) ((double) panel3.getHeight() / boardLength * 150);
        if (xSize > 150) {xSize = 150;}
        if (ySize > 150) {ySize = 150;}
        if (xLoc < this.getWidth() - 186) {xLoc = this.getWidth() - 186;}
        mapTop.setBounds(xLoc, yLoc, xSize, ySize);
    }

    /**
     * Sets a new location to bottom of the map.
     */
    public void setMapBottomLocation() {
        mapBottom.setLocation(this.getWidth() - 186, this.getHeight() - 210);
    }

    /**
     * Sets top of the map's location.
     */
    public void setMapTopLocation() {
        int boardLength = boardSize * sizeOfOne;
        int xLoc = this.getWidth() - 186 + (int) ((double)
                   (panel3.getX() - 8 -  buttons[0][0].getX())
                   / (boardSize * sizeOfOne) * 150);
        int yLoc = this.getHeight() - 210 + (int) ((double) 
                   (panel3.getY() - 120 - buttons[0][0].getY())
                   / (boardSize * sizeOfOne) * 150);
        if (xLoc < this.getWidth() - 186) {xLoc = this.getWidth() - 186;}
        mapTop.setLocation(xLoc, yLoc);
    }
    
    /**
     * Creates the labels on panel1.
     */
    public void labels() {
    
        String [] messages = main.TicTacToe.getMessages();
        sizeLabel = new JLabel(messages[0]);
        panel1.add(sizeLabel);
        sizeLabel.setBounds(getLeftX(), firstY, sizeX, sizeY);

        condLabel = new JLabel(messages[1]);
        panel1.add(condLabel);
        condLabel.setBounds(getMiddleX(), firstY, sizeX, sizeY);

        valueError = new JLabel();
        valueError.setForeground(Color.RED);
        panel1.add(valueError);

        formatError = new JLabel();
        formatError.setForeground(Color.RED);
        panel1.add(formatError);

    }

    /**
     * Creates the victory label on victory panel.
     * 
     * @param text Game ending text.
     */
    public void setVictoryLabel(String text) {
        victoryLabel = new JLabel(text, SwingConstants.CENTER);
        victoryLabel.setVerticalAlignment(SwingConstants.CENTER);
        victoryLabel.setBounds(0, 0, 800, 100);
        victoryLabel.setFont(new Font("serif", Font.BOLD, 30));
        victory.add(victoryLabel);

    }

    /**
     * Creates the text fields on panel1.
     */
    public void textFields() {
        gameSize = new JTextField();
        gameSize.setBounds(getLeftX(), secondY, sizeX, sizeY);
        panel1.add(gameSize);
        gameSize.addKeyListener(keyListener);

        winCond = new JTextField();
        winCond.setBounds(getMiddleX(), secondY, sizeX, sizeY);
        panel1.add(winCond);
        winCond.addKeyListener(keyListener);
    }

    /**
     * Creates the game creation button on panel1.
     */
    public void startButton() {
        start = new JButton("Create Game");
        panel1.add(start);
        start.setBounds(getRightX(), secondY, sizeX, sizeY);
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (gameEnded) {
                    lpane.remove(victory);
                    window.revalidate();
                    window.repaint();
                    gameEnded = false;
                }

                valueError.setText("");
                formatError.setText("");
                int check = main.TicTacToe.checkGameSettings(gameSize.getText(),
                                                             winCond.getText());
                if (check == 1) {
                    valueError.setText(main.TicTacToe.getValueErrorBoardSize());
                    valueError.setBounds(getLeftX(), thirdY, sizeX, sizeY);
                    gameSize.requestFocus();
                }
                else if (check == 2) {
                    formatError.setText(main.TicTacToe.getFormatError());
                    formatError.setBounds(getLeftX(), thirdY, sizeX, sizeY);
                    gameSize.requestFocus();
                }
                else if (check == 3) {
                    valueError.setText(main.TicTacToe.getValueErrorWinCond());
                    valueError.setBounds(getMiddleX(), thirdY, sizeX, sizeY);
                    winCond.requestFocus();
                }
                else if (check == 4) {
                    formatError.setText(main.TicTacToe.getFormatError());
                    formatError.setBounds(getMiddleX(), thirdY, sizeX, sizeY);
                    winCond.requestFocus();
                }
                else {
                    requestFocusInWindow();
                    main.TicTacToe.setGameButtons();
                }
            }
        });
    }

    /**
     * Confirms player's move.
     * 
     * Sets player's symbol to button and sends the move to main program.
     */
    public void actionPerformed(ActionEvent e) {
        if (!player) {return;}
        int y = 0;
        int x = 0;
        boolean exit = false;
        for (int i=0; i<buttons.length && !exit; i++) {
            for (int j=0; j<buttons.length; j++) {
                if (e.getSource() == buttons[i][j]) {
                    y = i;
                    x = j;
                    exit = true;
                    break;
                }
            }
        }
        
        buttons[y][x].setText("" + symbol1);
        player = !player;
        buttons[y][x].setEnabled(false); 
        window.revalidate();
        window.repaint();
        requestFocusInWindow();
        main.TicTacToe.takeAction(y, x);
    }

    /**
     * Sets computer's move to the button.
     * 
     * @param row Y-coordinate of the move.
     * @param column X-coordinate of the move.
     */
    public void setComputerMove(int row, int column) {
        buttons[row][column].setText("" + symbol2);
        buttons[row][column].setEnabled(false);
        requestFocusInWindow();
        player = !player;
    }
    
    /**
     * Gives an X-coordinate to the left side components of panel1.
     * 
     * @return X-coordinate.
     */
    public int getLeftX() {
        return panel1.getSize().width / 2 - 325;
    }

    /**
     * Gives an X-coordinate to the middle components of panel1.
     * 
     * @return X-coordinate.
     */
    public int getMiddleX() {
        return panel1.getSize().width / 2 - 100;
    }

    /**
     * Gives an X-coordinate to the right side components of panel1.
     * 
     * @return X-coordinate.
     */
    public int getRightX() {
        return panel1.getSize().width / 2 + 125;
    }

    /**
     * Creates the buttons for the game.
     * 
     * @param getBoardSize Size of the gameboard.
     */
    public void createButtons(int getBoardSize) {
        boardSize = getBoardSize;
        buttons = new JButton[boardSize][boardSize];
        createPanel3();

        
        setButtonBounds(true);

        int minWindowY = getHeight();
        if (minWindowY > 176 + boardSize * 40) {
            minWindowY = 176 + boardSize * 40;
        }
        setMinimumSize(new Dimension(800, minWindowY));
        setVisible(true);
        gameCreated = true;
    }

    /**
     * Sets locations to each button.
     */
    public void setButtonLocations() {
        int panelSizeX = panel3.getSize().width;
        int panelSizeY = panel3.getSize().height;
        int boardLength =  sizeOfOne * boardSize;
        int overX = 0;
        if (panelSizeX > boardLength) {
            overX = (panelSizeX - boardLength) / 2;
        }
        int overY = 0;
        int locX = buttons[0][0].getX();
        int locY = buttons[0][0].getY();
        int newZeroLocX = locX + changeX * currentMultiplier;
        int newZeroLocY = locY + changeY * currentMultiplier;
        if (newZeroLocX < panelSizeX - boardLength - overX) {
            newZeroLocX = panelSizeX - boardLength - overX;
        }
        else if (newZeroLocX > overX) {
            newZeroLocX = overX;
        }

        if (newZeroLocY < panelSizeY - boardLength - overY) {
            newZeroLocY = panelSizeY - boardLength - overY;
        }
        else if (locY + changeY > overY) {
            newZeroLocY = overY;
        }

        for (int row = 0; row < buttons.length; row++) {
            for (int column = 0; column < buttons[row].length; column++) {
                buttons[row][column].setLocation(newZeroLocX 
                + column * sizeOfOne, newZeroLocY + row * sizeOfOne);
            }
        }
        setMapTopLocation();
    }

    /**
     * Sets the bounds of the buttons.
     * 
     * @param createButtons If the buttons are to be created.
     */
    public void setButtonBounds(boolean createButtons) {

        int overX = 0;
        int overY = 0;

        int min = panel3.getSize().width;
        if (min > panel3.getSize().height) {
            min = panel3.getSize().height;
        }
        sizeOfOne = min / boardSize;
        if (sizeOfOne <= 40) {
            sizeOfOne = 40;
            overY = (panel3.getSize().height - sizeOfOne * boardSize) / 2;
            showMap = true;
        } else {showMap = false;}
        overX = (panel3.getSize().width - sizeOfOne * boardSize) / 2;

        
        for (int row=0; row < boardSize; row++) {
            for (int column=0; column < boardSize; column++) {
                if (createButtons) {
                    buttons[row][column] = new JButton();
                    panel3.add(buttons[row][column]);
                    buttons[row][column].addActionListener(this::actionPerformed);

                    buttons[row][column].setMargin(new Insets(5, 5, 5, 5));
                }
                buttons[row][column].setFont(new Font("serif", Font.BOLD,
                                             sizeOfOne / 2));
                buttons[row][column].setBounds(overX + sizeOfOne * column,
                overY + sizeOfOne * row, sizeOfOne, sizeOfOne);
            }
        }

        if (createButtons) {
            createMapPanels();
        }
        if (showMap && !isMap && (overX < 0 || overY < 0)) {
            lpane.add(mapBottom, new Integer(4), 0);
            lpane.add(mapTop, new Integer(5), 0);
            isMap = true;
        }
        else if (!showMap && isMap) {
            lpane.remove(mapBottom);
            lpane.remove(mapTop);
            isMap = false;
        }
    }

    /**
     * The keyboard commands which moves the gameboard.
     */
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S
        || e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {

            if (!threadLoc) {
                setButtonLocThread();
            }
            if (e.getKeyCode() == KeyEvent.VK_W && !pressedW) {
                changeY += change;
                keysPressed++;
                pressedW = true;
            } 
            else if (e.getKeyCode() == KeyEvent.VK_S && !pressedS) {
                changeY -= change;
                keysPressed++;
                pressedS = true;
            }
            else if (e.getKeyCode() == KeyEvent.VK_A && !pressedA) {
                changeX += change;
                keysPressed++;
                pressedA = true;
            }
            else if (e.getKeyCode() == KeyEvent.VK_D && !pressedD) {
                changeX -= change;
                keysPressed++;
                pressedD = true;
            }
            
        } 
        else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            if (keysPressed > 0) {
                currentMultiplier = multiplier;
            }
        }
    }

    /**
     * Updates buttons locations.
     */
    public void setButtonLocThread() {
        if (threadLoc) {return;}
        buttonLocChange = new Thread(new Runnable() {
            public void run()  {
                while (true) {
                    setButtonLocations();
                    try {
                        TimeUnit.MILLISECONDS.sleep(timeDelay);
                    } catch (Exception e) {}
                }
            }
        });
        threadLoc = true;
        buttonLocChange.start();
    }

    /**
     * Stop the keyboard commands.
     */
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S
        || e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {

            if (e.getKeyCode() == KeyEvent.VK_W && pressedW) {
                changeY -= change;
                keysPressed--;
                pressedW = false;
            } 
            else if (e.getKeyCode() == KeyEvent.VK_S && pressedS) {
                changeY += change;
                keysPressed--;
                pressedS = false;
            }

            else if (e.getKeyCode() == KeyEvent.VK_A && pressedA) {
                changeX -= change;
                keysPressed--;
                pressedA = false;
            }

            else if (e.getKeyCode() == KeyEvent.VK_D && pressedD) {
                changeX += change;
                keysPressed--;
                pressedD = false;
            }
            
        } 
        else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            currentMultiplier = 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            keysPressed = 0;
            pressedA = false;
            pressedD = false;
            pressedS = false;
            pressedW = false;
        }
        if (keysPressed == 0 && threadLoc) {
            buttonLocChange.stop();
            threadLoc = false;
            changeX = 0;
            changeY = 0;
        }
    }

    public void keyTyped(KeyEvent e) {}

    public void mouseClicked(MouseEvent e) {
        requestFocusInWindow();
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}


    public void componentHidden (ComponentEvent e) {}

    public void componentMoved (ComponentEvent e) {}

    public void componentResized (ComponentEvent e) {}

    public void componentShown (ComponentEvent e) {}
}
