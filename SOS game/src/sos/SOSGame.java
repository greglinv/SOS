package sos;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import javax.swing.border.LineBorder;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import javax.swing.JOptionPane;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

//Create the Main Game Class
public class SOSGame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int boardSize;
	private int player1Score;
	private int player2Score;
	private char[][] board;
	private boolean player1Turn;
	private JButton[][] buttons;
	public char currentPlayerSymbol;
	private boolean gameEnded;

	private ComputerPlayer computerPlayer;
	private boolean player1Computer;
	private boolean player2Computer;

	private JRadioButton player1RadioButtonS;
	private JRadioButton player1RadioButtonO;
	private JRadioButton player2RadioButtonS;
	private JRadioButton player2RadioButtonO;
	private JRadioButton simpleModeRadioButton;
	private JRadioButton complexModeRadioButton;

	private JRadioButton player1HumanButton;
	private JRadioButton player1ComputerButton;
	private JRadioButton player2HumanButton;
	private JRadioButton player2ComputerButton;

	private boolean simpleMode;
	private boolean player1RadioButtonSisSelected = true;
	private boolean player2RadioButtonSisSelected = true;
	private JLabel turnLabel;
	private JLabel modeLabel;
	JTextField boardSizeField;
	JButton newGameButton;
	JButton replayGameButton;
	JCheckBox recordGameCheckbox = new JCheckBox("Record Game");
	

	private Set<String> player1CompletedSequences;
	private Set<String> player2CompletedSequences;

	private List<Move> moveList;

	public SOSGame() {
		// Default board size is 3x3
		this.boardSize = 3;
		this.player1Computer = false;
		this.player2Computer = false;
		this.board = new char[boardSize][boardSize];
		this.player1Turn = true;
		this.buttons = new JButton[boardSize][boardSize];
		this.currentPlayerSymbol = 'S';
		this.computerPlayer = new ComputerPlayer();
		this.simpleMode = true;

		moveList = new ArrayList<>();

		initializeBoard();
		createUI();
	}

	public SOSGameMode getGameMode() {
		return simpleMode ? new SimpleSOSGameMode(boardSize) : new GeneralSOSGameMode(boardSize);
	}

	// Create board based on board Size
	public void initializeBoard() {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = ' ';
			}
		}
		gameEnded = false;
		player1Computer = false;
		player2Computer = false;
		player1Score = 0;
		player2Score = 0;
		recordGameCheckbox.setEnabled(true);

	}

	// Create a Move Class for Recordings
	public class Move {
		private int player;
		private int row;
		private int col;
		private char symbol;

		public Move(int player, int row, int col, char symbol) {
			this.player = player;
			this.row = row;
			this.col = col;
			this.symbol = symbol;
		}

		// Add getters if needed

		@Override
		public String toString() {
			return String.format("Player %d played '%c' at (%d, %d)", player, symbol, row, col);
		}
	}

	// Record the move
	private void recordMove(int row, int col, char symbol) {
		int player = player1Turn ? 1 : 2;
		Move move = new Move(player, row, col, symbol);
		moveList.add(move);
	}

	// Save the moves to a text file
	public void saveMovesToFile(String filePath) {
		
	    try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
	    	writer.print("");
	        for (Move move : moveList) {
	            writer.println(move.toString());
	        }
	        writer.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void replayGame(String filePath) {
	    initializeBoard();
	    moveList.clear(); // Clear the move list

	    try (Scanner scanner = new Scanner(new File(filePath))) {
	        while (scanner.hasNextLine()) {
	            String line = scanner.nextLine();
	            String[] parts = line.split("\\s+");
	            int player = Integer.parseInt(parts[1]);
	            String symbolString = parts[3].replaceAll("[^a-zA-Z]", "");
	            char symbol = symbolString.charAt(0);
	            int row = Integer.parseInt(parts[5].replaceAll("[^0-9]", ""));
	            int col = Integer.parseInt(parts[6].replaceAll("[^0-9]", ""));

	            SOSGameMode gameMode = simpleMode ? new SimpleSOSGameMode(boardSize) : new GeneralSOSGameMode(boardSize);
	            gameMode.makeMove(row, col, symbol, board);
	            togglePlayer();
	            updateTurnLabel();
	            moveList.add(new Move(player, row, col, symbol));
	        }
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	}

	// Disable buttons after clicked
	private void disableButton(int row, int col) {
		buttons[row][col].setEnabled(false);
	}

	// Define the abstract base class for SOS game modes
	abstract class SOSGameMode {
		protected char[][] board;
		protected int boardSize;

		public SOSGameMode(int boardSize) {
			this.boardSize = boardSize;
			this.board = new char[boardSize][boardSize];
			initializeBoard();

		}

		public static boolean searchInDirection(char[][] board, int row, int col, String target, int rowDir,
				int colDir) {
			int numRows = board.length;
			int numCols = board[0].length;
			int targetIndex = 0;
			int currentRow = row;
			int currentCol = col;

			while (targetIndex < target.length()) {
				if (currentRow < 0 || currentRow >= numRows || currentCol < 0 || currentCol >= numCols
						|| board[currentRow][currentCol] != target.charAt(targetIndex)) {
					return false;
				}
				currentRow += rowDir;
				currentCol += colDir;
				targetIndex++;
			}

			return true;
		}

		protected void initializeBoard() {
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					board[i][j] = ' ';
				}
			}
		}

		public abstract void makeMove(int row, int col, char symbol, char[][] board);

		public abstract int getWinner();

		protected abstract void announceWinner();
	}

	// Implement Simple SOS Game Mode
	class SimpleSOSGameMode extends SOSGameMode {

		private int gameWinner;

		public SimpleSOSGameMode(int boardSize) {
			super(boardSize);
			gameEnded = false;
			gameWinner = 0;
			player1CompletedSequences = new HashSet<>();
			player2CompletedSequences = new HashSet<>();
		}

		// Detect SOS sequences
		public boolean checkForSOS(char[][] board) {
			int numRows = board.length;
			int numCols = board[0].length;
			String target = "SOS";

			for (int i = 0; i < numRows; i++) {
				for (int j = 0; j < numCols; j++) {
					if (searchInDirection(board, i, j, target, 0, 1) || // Horizontal
							searchInDirection(board, i, j, target, 1, 0) || // Vertical
							searchInDirection(board, i, j, target, 1, 1) || // Diagonal (bottom-right)
							searchInDirection(board, i, j, target, 1, -1)) { // Diagonal (bottom-left)
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public void makeMove(int row, int col, char symbol, char[][] board) {
			if (!gameEnded && board[row][col] == ' ') {
				board[row][col] = symbol;
				buttons[row][col].setText(Character.toString(symbol));
				

				if (recordGameCheckbox.isSelected()) {
					recordMove(row, col, symbol);
				}
				
				if (checkForSOS(board)) {
					// An SOS sequence is found; declare the winner and end the game
					if (player1Turn) {
						gameWinner = 1;
						player1CompletedSequences.add(row + "," + col);
					} else {
						gameWinner = 2;
						player2CompletedSequences.add(row + "," + col);
					}
					announceWinner();
				} else if (isGameOver()) {
					announceWinner();
				} else {
					currentPlayerSymbol = (symbol == 'S') ? 'O' : 'S';

					// Disable the button after placing the symbol
					disableButton(row, col);
				}
			}
		}

		// When game is finished announce winner
		protected void announceWinner() {
			// Change the button color after SOS is completed
			if (gameWinner == 1) {
				for (String sequence : player1CompletedSequences) {
					String[] coords = sequence.split(",");
					int row = Integer.parseInt(coords[0]);
					int col = Integer.parseInt(coords[1]);
					buttons[row][col].setBackground(Color.RED);
				}
			} else if (gameWinner == 2) {
				for (String sequence : player2CompletedSequences) {
					String[] coords = sequence.split(",");
					int row = Integer.parseInt(coords[0]);
					int col = Integer.parseInt(coords[1]);
					buttons[row][col].setBackground(Color.BLUE);
				}
			}

			saveMovesToFile("moves.txt");
			String message;
			if (gameWinner == 1) {
				message = "Red Player has won the game!";
			} else if (gameWinner == 2) {
				message = "Blue Player has won the game!";
			} else {
				message = "Tie Game";
			}
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					disableButton(i, j);
				}
			}

			JOptionPane.showMessageDialog(null, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
			gameEnded = true;
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					if (board[i][j] == 'S' || board[i][j] == 'O') {
						buttons[i][j].setText(Character.toString(board[i][j]));
						buttons[i][j].setBorder(new LineBorder(Color.BLACK)); // Add a border for visibility
						buttons[i][j].setEnabled(false);
					}
				}
			}
		}

		@Override
		public int getWinner() {
			return -1;
		}
	}

	// Implement General SOS Game Mode
	class GeneralSOSGameMode extends SOSGameMode {

		private int lastSquareRow;
		private int lastSquareCol;
		Set<String> completedSequences = new HashSet<String>();

		public GeneralSOSGameMode(int boardSize) {
			super(boardSize);
			player1CompletedSequences = new HashSet<>();
			player2CompletedSequences = new HashSet<>();
			gameEnded = false;
		}

		@Override
		public void makeMove(int row, int col, char symbol, char[][] board) {
			if (!gameEnded && board[row][col] == ' ') {
				board[row][col] = symbol;
				buttons[row][col].setText(Character.toString(symbol));

				checkForSOS(board);

				// Continue the game, switching players
				currentPlayerSymbol = (symbol == 'S') ? 'O' : 'S';
				disableButton(row, col);

				if (recordGameCheckbox.isSelected()) {
					// Record the move to a file
					recordMove(row, col, symbol);
				}

				if (isGameOver()) {
					try {
						announceWinner();
					} catch (Exception e1) {
						System.out.println("announce winner broke");
					}

				}
			}
		}

		@Override
		public int getWinner() {
			if (player1Score > player2Score) {
				return 1; // Player 1 wins
			} else if (player2Score > player1Score) {
				return 2; // Player 2 wins
			} else {
				return 0; // It's a draw
			}
		}

		protected void announceWinner() {
			saveMovesToFile("moves.txt");
			String message;
			if (getWinner() == 1) {
				message = "Red Player has won the game!";
			} else if (getWinner() == 2) {
				message = "Blue Player has won the game!";
			} else {
				message = "Tie Game";
			}
			JOptionPane.showMessageDialog(null, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
			gameEnded = true;
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					if (board[i][j] == 'S' || board[i][j] == 'O') {
						buttons[i][j].setText(Character.toString(board[i][j]));
						buttons[i][j].setBorder(new LineBorder(Color.BLACK)); // Add a border for visibility
						buttons[i][j].setEnabled(false);
					}
				}
			}
		}

		// Search the Board for new SOS
		public void checkForSOS(char[][] board) {
			int numRows = board.length;
			int numCols = board[0].length;
			String target = "SOS";
			String position;

			for (int i = 0; i < numRows; i++) {
				for (int j = 0; j < numCols; j++) {
					if (searchInDirection(board, i, j, target, 0, 1)) {
						position = i + "," + j + "," + i + "," + (j + 2);
						if (!completedSequences.contains(position)) {
							if (player1Turn) {
								player1Score++;
							} else {
								player2Score++;
							}
							lastSquareRow = i;
							lastSquareCol = j;
							updateLastSquareColor();
							completedSequences.add(position);
						}
					} else if (searchInDirection(board, i, j, target, 1, 0)) {
						position = i + "," + j + (i + 2) + "," + j;
						if (!completedSequences.contains(position)) {
							if (player1Turn) {
								player1Score++;
							} else {
								player2Score++;
							}
							lastSquareRow = i;
							lastSquareCol = j;
							updateLastSquareColor();
							completedSequences.add(position);
						}
					} else if (searchInDirection(board, i, j, target, 1, 1)) {
						position = i + "," + j + (i + 2) + "," + (j + 2);
						if (!completedSequences.contains(position)) {
							if (player1Turn) {
								player1Score++;
							} else {
								player2Score++;
							}
							lastSquareRow = i;
							lastSquareCol = j;
							updateLastSquareColor();
							completedSequences.add(position);
						}
					} else if (searchInDirection(board, i, j, target, 1, -1)) { // Diagonal (bottom-left)
						position = i + "," + j + (i + 2) + "," + (j - 2);
						if (!completedSequences.contains(position)) {
							if (player1Turn) {
								player1Score++;
							} else {
								player2Score++;
							}
							completedSequences.add(position);
						}

					}
				}
			}
		}

		private void updateLastSquareColor() {
			if (player1Turn) {
				buttons[lastSquareRow][lastSquareCol].setBackground(Color.RED);
			} else {
				buttons[lastSquareRow][lastSquareCol].setBackground(Color.BLUE);
			}
		}
	}

	public class ComputerPlayer {
		private Random random;

		public ComputerPlayer() {
			this.random = new Random();
		}

		public void makeMove(char[][] board) {
			int row, col;

			do {
				row = random.nextInt(board.length);
				col = random.nextInt(board[0].length);
			} while (board[row][col] != ' ');

			char symbol = (random.nextBoolean()) ? 'S' : 'O';

			// Make the move
			if (simpleMode) {
				SOSGameMode gameMode = new SimpleSOSGameMode(board.length);
				gameMode.makeMove(row, col, symbol, board);

			} else if (!simpleMode) {
				SOSGameMode gameMode = new GeneralSOSGameMode(board.length);
				gameMode.makeMove(row, col, symbol, board);

			}
		}
	}

	public void computerTurn() {
		if (!gameEnded) {
			computerPlayer.makeMove(board);
			togglePlayer();
			updateTurnLabel();
			Timer timer = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					
					if (!gameEnded && player1Turn && player1Computer) {
						computerTurn();
					} else if (!gameEnded && !player1Turn && player2Computer) {
						computerTurn();
					}
				}
			});

			// Start the timer
			timer.setRepeats(false);
			timer.start();
		}
	}



	// Change the Board Size
	public void setBoardSize(int newSize) {
		this.boardSize = newSize;
		this.board = new char[boardSize][boardSize];
		this.buttons = new JButton[boardSize][boardSize];
	}

	private void recreateBoard(char currentPlayerSymbol) {
		// Remove the old board panel
		getContentPane().removeAll();

		currentPlayerSymbol = 'S';

		// Recreate the UI with the new board size
		createUI();

		revalidate();
		repaint();
	}

	public int getBoardSize() {
		return boardSize;
	}

	// Add a method to handle the computer's turn
	private boolean isGameOver() {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] == ' ') {
					return false;
				}
			}
		}
		return true;
	}

	private void updateTurnLabel() {
		if (player1Turn) {
			turnLabel.setText("Red Player's Turn");
		} else {
			turnLabel.setText("Blue Player's Turn");
		}
	}

	public JLabel getTurnLabel() {
		return turnLabel;
	}

	private void updateModeLabel(JLabel statusLabel) {
		if (simpleMode) {
			modeLabel.setText("Simple Mode");
		} else {
			modeLabel.setText("General Mode");
		}
	}

	// Check if the row and column are within the board bounds
	public boolean isMoveValid(int row, int col) {
		if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
			return false;

		}
		return board[row][col] == ' ';
	}

	public char[][] getBoard() {
		return board;
	}

	public boolean getCurrentPlayer() {
		return player1Turn;
	}

	public boolean isComplexMode() {
		return !simpleMode;
	}

	public void togglePlayer() {
		player1Turn = !player1Turn;
	}

	private void createUI() {
		setTitle("SOS Game");
		setSize(1600, 900);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel playerPanel = new JPanel(new GridLayout(2, 1));

		// Red Player Section
		JPanel redPlayerPanel = new JPanel(new GridLayout(2, 2));
		JLabel player1Label = new JLabel("Red Player:");
		player1RadioButtonS = new JRadioButton("S", true);
		player1RadioButtonO = new JRadioButton("O");
		player1HumanButton = new JRadioButton("Human", true);
		player1ComputerButton = new JRadioButton("Computer");

		// Button Groups
		ButtonGroup player1ButtonGroup = new ButtonGroup();
		ButtonGroup player1ComputerGroup = new ButtonGroup();
		player1ButtonGroup.add(player1RadioButtonS);
		player1ButtonGroup.add(player1RadioButtonO);
		player1ComputerGroup.add(player1HumanButton);
		player1ComputerGroup.add(player1ComputerButton);

		// Add components for Red Player
		redPlayerPanel.add(player1Label);
		redPlayerPanel.add(player1RadioButtonS);
		redPlayerPanel.add(player1RadioButtonO);
		redPlayerPanel.add(player1HumanButton);
		redPlayerPanel.add(player1ComputerButton);

		// Blue Player Section
		JPanel bluePlayerPanel = new JPanel(new GridLayout(2, 2));
		JLabel player2Label = new JLabel("Blue Player:");
		player2RadioButtonS = new JRadioButton("S", true);
		player2RadioButtonO = new JRadioButton("O");
		player2HumanButton = new JRadioButton("Human", true);
		player2ComputerButton = new JRadioButton("Computer");

		// Button Groups
		ButtonGroup player2ButtonGroup = new ButtonGroup();
		ButtonGroup player2ComputerGroup = new ButtonGroup();
		player2ButtonGroup.add(player2RadioButtonS);
		player2ButtonGroup.add(player2RadioButtonO);
		player2ComputerGroup.add(player2HumanButton);
		player2ComputerGroup.add(player2ComputerButton);

		// Add components for Blue Player
		bluePlayerPanel.add(player2Label);
		bluePlayerPanel.add(player2RadioButtonS);
		bluePlayerPanel.add(player2RadioButtonO);
		bluePlayerPanel.add(player2HumanButton);
		bluePlayerPanel.add(player2ComputerButton);

		// Add Red and Blue Player sections to the main player panel
		playerPanel.add(redPlayerPanel);
		playerPanel.add(bluePlayerPanel);

		// Add the main player panel to the main layout
		add(playerPanel, BorderLayout.WEST);

		JPanel boardPanel = new JPanel(new GridLayout(boardSize, boardSize));
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				final int row = i;
				final int col = j;
				buttons[i][j] = new JButton("");
				buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 24));
				buttons[i][j].setPreferredSize(new Dimension(100, 100));
				buttons[i][j].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (board[row][col] == ' ' && !isGameOver()) {

							if (player1Turn) {
								if (player1RadioButtonSisSelected) {
									currentPlayerSymbol = 'S';
								} else {
									currentPlayerSymbol = 'O';
								}
							} else {
								if (player2RadioButtonSisSelected) {
									currentPlayerSymbol = 'S';
								} else {
									currentPlayerSymbol = 'O';
								}
							}

							SOSGameMode gameMode = simpleMode ? new SimpleSOSGameMode(boardSize)
									: new GeneralSOSGameMode(boardSize);
							gameMode.makeMove(row, col, currentPlayerSymbol, board);

							togglePlayer();
							updateTurnLabel();

							// Trigger computer's turn if vsComputer mode is enabled
							if (player1Computer && player1Turn) {
								try {
									computerTurn();
								} catch (Exception e2) {
									System.out.println("Computer Turn Broke");
								}

							} else if (player2Computer && !player1Turn) {
								try {
									computerTurn();
								} catch (Exception e2) {
									System.out.println("Computer Turn Broke");
								}
							}

							simpleModeRadioButton.setEnabled(false);
							complexModeRadioButton.setEnabled(false);
							
							recordGameCheckbox.setEnabled(false);

							player1HumanButton.setEnabled(false);
							player2HumanButton.setEnabled(false);
							player1ComputerButton.setEnabled(false);
							player2ComputerButton.setEnabled(false);
						}

					}

				});
				boardPanel.add(buttons[i][j]);
			}
		}
		simpleModeRadioButton = new JRadioButton("Simple Mode", true);
		complexModeRadioButton = new JRadioButton("General Mode");

		// Add action listener to Buttons
		simpleModeRadioButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// Update the boolean variable based on the radio button's state
				simpleMode = (e.getStateChange() == ItemEvent.SELECTED);
				updateModeLabel(modeLabel);
			}
		});
		player1RadioButtonS.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent d) {
				player1RadioButtonSisSelected = (d.getStateChange() == ItemEvent.SELECTED);
			}
		});
		player2RadioButtonS.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent f) {
				player2RadioButtonSisSelected = (f.getStateChange() == ItemEvent.SELECTED);
			}
		});

		player1HumanButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent f) {
				player1Computer = !(f.getStateChange() == ItemEvent.SELECTED);
				setPlayer1Computer(true);
			}
		});

		player2HumanButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent f) {
				player2Computer = !(f.getStateChange() == ItemEvent.SELECTED);
			}
		});

		add(boardPanel, BorderLayout.CENTER);

		// Game mode selection
		JPanel modePanel = new JPanel(new GridLayout(4, 1));
		ButtonGroup modeButtonGroup = new ButtonGroup();
		modeButtonGroup.add(simpleModeRadioButton);
		modeButtonGroup.add(complexModeRadioButton);
		modePanel.add(simpleModeRadioButton);
		modePanel.add(complexModeRadioButton);
		modePanel.add(recordGameCheckbox);
		replayGameButton = new JButton("Replay Game");
		replayGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SOSGame sosGame = new SOSGame();
				sosGame.replayGame("moves.txt");
			}
		});

		modePanel.add(replayGameButton);
		
		add(modePanel, BorderLayout.EAST);

		// Set and Display Mode Label
		modeLabel = new JLabel("Simple Mode");
		modeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		modeLabel.setFont(new Font("Arial", Font.BOLD, 16));
		add(modeLabel, BorderLayout.SOUTH);

		// Turn indicator label
		turnLabel = new JLabel("Red Player's Turn");
		turnLabel.setFont(new Font("Arial", Font.BOLD, 16));

		// Add the turn label to the top right
		JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topRightPanel.add(turnLabel); // Add turnLabel to topRightPanel
		add(topRightPanel, BorderLayout.NORTH);

		// Add a JTextField for changing the board size
		boardSizeField = new JTextField(5);
		newGameButton = new JButton("New Game");
		newGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentPlayerSymbol = 'S';
				player1RadioButtonSisSelected = true;
				player2RadioButtonSisSelected = true;
				player1Turn = true;
				
				try {
					int newSize = Integer.parseInt(boardSizeField.getText());
					if (newSize > 2) {
						setBoardSize(newSize);
						initializeBoard();
						recreateBoard(currentPlayerSymbol);
					} else {
						// Handle invalid board size (less than or equal to 3)
						JOptionPane.showMessageDialog(SOSGame.this, "Board size must be larger than 2x2.");
					}
				} catch (NumberFormatException ex) {
					// Handle invalid input (non-integer)
					JOptionPane.showMessageDialog(SOSGame.this, "Invalid input. Please enter a valid integer.");
				}
			}
		});

		// Add components for changing board size
		JPanel boardSizePanel = new JPanel();
		boardSizePanel.add(new JLabel("Size:"));
		boardSizePanel.add(boardSizeField);
		boardSizePanel.add(newGameButton);
		boardSizePanel.add(turnLabel);

		// Add the board size input panel to the UI
		add(boardSizePanel, BorderLayout.NORTH);

		setVisible(true);
	}

	public void setGameEnded(boolean value) {
		this.gameEnded = value;
	}

	public void setPlayer1Turn(boolean value) {
		this.player1Turn = value;
	}

	public void setPlayer1Computer(boolean value) {
		this.player1Computer = value;

		// If player 1 is set to computer, switch to player 2 and update the label
		if (value) {
			player1Turn = false;
			updateTurnLabel();

			// Trigger computer's turn for player 2
			if (player2Computer) {
				computerTurn();
			}
		}
	}

	public void setPlayer2Computer(boolean value) {
		this.player2Computer = value;
	}
}