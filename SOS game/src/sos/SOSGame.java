// Refactored SOS Game Code

package sos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

// SOS Game Class
class SOSGame extends JFrame {

    private static final long serialVersionUID = 1L;
    private Board board;
    private Player player1, player2;
    private boolean isPlayer1Turn;
    private boolean gameEnded;
    private GameMode gameMode;
    private JLabel turnLabel;
    private JTextField boardSizeField;
    private JCheckBox recordGameCheckbox;
    private List<Move> moveList;
    private char currentSymbol; // Tracks whether the player is placing 'S' or 'O'
    private JPanel modePanel; // Panel for selecting game mode

    public SOSGame() {
        setTitle("SOS Game");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initializeGame();
        createUI();
        setVisible(true);
    }

    // Initialize Game Components
    private void initializeGame() {
        int initialBoardSize = 3;
        this.board = new Board(initialBoardSize);
        this.player1 = new Player("Red", Color.RED);
        this.player2 = new Player("Blue", Color.BLUE);
        this.isPlayer1Turn = true;
        this.gameEnded = false;
        this.gameMode = new SimpleGameMode(board);
        this.moveList = new ArrayList<>();
        this.currentSymbol = 'S'; // Default symbol
    }

    // Create Game UI
    private void createUI() {
        setLayout(new BorderLayout());

        // Create Turn Label
        turnLabel = new JLabel("Red Player's Turn");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(turnLabel, BorderLayout.NORTH);

        // Create Board UI
        JPanel boardPanel = new JPanel(new GridLayout(board.getSize(), board.getSize()));
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                final int row = i; // Ensure effective finality for lambdas
                final int col = j;
                JButton button = new JButton();
                button.setFont(new Font("Arial", Font.BOLD, 20));
                button.addActionListener(e -> handleMove(row, col, button));
                board.addButton(i, j, button);
                boardPanel.add(button);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        // Bottom Panel for Controls
        JPanel controlPanel = new JPanel(new FlowLayout());
        boardSizeField = new JTextField("3", 5);
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> resetGame());
        recordGameCheckbox = new JCheckBox("Record Game");

        // Add Symbol Selection
        JRadioButton sButton = new JRadioButton("S", true);
        JRadioButton oButton = new JRadioButton("O");
        ButtonGroup symbolGroup = new ButtonGroup();
        symbolGroup.add(sButton);
        symbolGroup.add(oButton);
        
        // Add listeners for symbol selection
        sButton.addActionListener(e -> currentSymbol = 'S');
        oButton.addActionListener(e -> currentSymbol = 'O');

        // Add Game Mode Selection
        JRadioButton simpleModeButton = new JRadioButton("Simple Mode", true);
        JRadioButton generalModeButton = new JRadioButton("General Mode");
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(simpleModeButton);
        modeGroup.add(generalModeButton);

        // Add listeners for game mode selection
        simpleModeButton.addActionListener(e -> gameMode = new SimpleGameMode(board));
        generalModeButton.addActionListener(e -> gameMode = new GeneralGameMode(board));

        modePanel = new JPanel(new FlowLayout());
        modePanel.add(new JLabel("Game Mode:"));
        modePanel.add(simpleModeButton);
        modePanel.add(generalModeButton);
        controlPanel.add(modePanel);

        controlPanel.add(new JLabel("Symbol:"));
        controlPanel.add(sButton);
        controlPanel.add(oButton);
        controlPanel.add(new JLabel("Board Size:"));
        controlPanel.add(boardSizeField);
        controlPanel.add(newGameButton);
        controlPanel.add(recordGameCheckbox);

        add(controlPanel, BorderLayout.SOUTH);
    }

    // Handle Player Move
    private void handleMove(int row, int col, JButton button) {
        if (gameEnded || !board.isValidMove(row, col)) return;

        Player currentPlayer = isPlayer1Turn ? player1 : player2;
        board.placeSymbol(row, col, currentSymbol);
        button.setText(String.valueOf(currentSymbol));
        button.setBackground(currentPlayer.getColor()); // Set background color to match the player
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setEnabled(false);

        moveList.add(new Move(currentPlayer.getName(), row, col, currentSymbol));

        if (checkForSOS()) {
            endGameWithWinner(currentPlayer);
            return;
        }

        if (board.isFull()) {
            endGame();
        } else {
            isPlayer1Turn = !isPlayer1Turn;
            updateTurnLabel();
        }

        // Disable game mode selection after the first move
        modePanel.setVisible(false);
    }

    // Full-Window Scan for SOS detection
    private boolean checkForSOS() {
        char[][] grid = board.getGrid();
        int size = board.getSize();
        String target = "SOS";

        // Check rows and columns
        for (int i = 0; i < size; i++) {
            for (int j = 0; j <= size - 3; j++) {
                // Check rows
                if (grid[i][j] == 'S' && grid[i][j + 1] == 'O' && grid[i][j + 2] == 'S') {
                    return true;
                }
                // Check columns
                if (grid[j][i] == 'S' && grid[j + 1][i] == 'O' && grid[j + 2][i] == 'S') {
                    return true;
                }
            }
        }

        // Check diagonals
        for (int i = 0; i <= size - 3; i++) {
            for (int j = 0; j <= size - 3; j++) {
                // Top-left to bottom-right
                if (grid[i][j] == 'S' && grid[i + 1][j + 1] == 'O' && grid[i + 2][j + 2] == 'S') {
                    return true;
                }
                // Top-right to bottom-left
                if (grid[i][j + 2] == 'S' && grid[i + 1][j + 1] == 'O' && grid[i + 2][j] == 'S') {
                    return true;
                }
            }
        }

        return false;
    }

    // Update Turn Label
    private void updateTurnLabel() {
        Player currentPlayer = isPlayer1Turn ? player1 : player2;
        turnLabel.setText(currentPlayer.getName() + " Player's Turn");
    }

    // End Game with Winner
    private void endGameWithWinner(Player winner) {
        gameEnded = true;
        JOptionPane.showMessageDialog(this, winner.getName() + " Player Wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    // End Game Logic
    private void endGame() {
        gameEnded = true;
        JOptionPane.showMessageDialog(this, "It's a Tie!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    // Reset Game
    private void resetGame() {
        try {
            int newSize = Integer.parseInt(boardSizeField.getText());
            if (newSize < 3) throw new NumberFormatException();
            board = new Board(newSize);
            initializeGame();
            getContentPane().removeAll();
            createUI();
            revalidate();
            repaint();

            // Re-enable game mode selection
            modePanel.setVisible(true);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid board size. Please enter an integer >= 3.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// Board Class
class Board {
    private final int size;
    private final char[][] grid;
    private final JButton[][] buttons;

    public Board(int size) {
        this.size = size;
        this.grid = new char[size][size];
        this.buttons = new JButton[size][size];
        for (char[] row : grid) Arrays.fill(row, ' ');
    }

    public int getSize() {
        return size;
    }

    public boolean isValidMove(int row, int col) {
        return grid[row][col] == ' ';
    }

    public void placeSymbol(int row, int col, char symbol) {
        grid[row][col] = symbol;
    }

    public boolean isFull() {
        for (char[] row : grid) {
            for (char cell : row) {
                if (cell == ' ') return false;
            }
        }
        return true;
    }

    public void addButton(int row, int col, JButton button) {
        buttons[row][col] = button;
    }

    public char[][] getGrid() {
        return grid;
    }
}

// Player Class
class Player {
    private final String name;
    private final Color color;
    private int score;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        score++;
    }
}

// GameMode Interface
interface GameMode {
    boolean checkForSOS(int row, int col);
}

// Simple Game Mode Implementation
class SimpleGameMode implements GameMode {
    private final Board board;

    public SimpleGameMode(Board board) {
        this.board = board;
    }

    @Override
    public boolean checkForSOS(int row, int col) {
        // This is overridden by the full-window scan in the main game logic.
        return false;
    }
}

// General Game Mode Implementation
class GeneralGameMode implements GameMode {
    private final Board board;

    public GeneralGameMode(Board board) {
        this.board = board;
    }

    @Override
    public boolean checkForSOS(int row, int col) {
        // This is overridden by the full-window scan in the main game logic.
        return false;
    }
}

// Move Class for Recording
class Move {
    private final String playerName;
    private final int row, col;
    private final char symbol;

    public Move(String playerName, int row, int col, char symbol) {
        this.playerName = playerName;
        this.row = row;
        this.col = col;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return String.format("%s played '%c' at (%d, %d)", playerName, symbol, row, col);
    }
}
