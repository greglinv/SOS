package sos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.BooleanSupplier;
import java.util.ArrayList;
import javax.swing.JOptionPane;

//Create the Main Game Class
public class SOSGame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int boardSize;
	private char[][] board;
	private boolean player1Turn;
	private JButton[][] buttons;
	private char currentPlayerSymbol;
	private JRadioButton player1RadioButtonS;
	private JRadioButton player1RadioButtonO;
	private JRadioButton player2RadioButtonS;
	private JRadioButton player2RadioButtonO;
	private JRadioButton simpleModeRadioButton;
	private JRadioButton complexModeRadioButton;
	private boolean simpleMode;
	private boolean player1RadioButtonSisSelected = true;
	private boolean player2RadioButtonSisSelected = true;
	private JLabel turnLabel;
	private JLabel modeLabel;
	private JTextField boardSizeField;
	private JButton newGameButton;

	public SOSGame() {
		// Default board size is 3x3
		this.boardSize = 3;
		this.board = new char[boardSize][boardSize];
		this.player1Turn = true;
		this.buttons = new JButton[boardSize][boardSize];
		this.currentPlayerSymbol = 'S';

		// Set simpleMode to true by default
		this.simpleMode = true;
		
		
		initializeBoard();
		createUI();
	}

	private void initializeBoard() {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = ' ';
			}
		}
	}

	// To disable buttons after clicked
	private void disableButton(int row, int col) {
		buttons[row][col].setEnabled(false);
	}

	// Define the abstract base class for SOS game modes
	abstract class SOSGameMode {
		protected char[][] board;
		protected int boardSize;
		protected char currentPlayerSymbol;

		public SOSGameMode(int boardSize) {
			this.boardSize = boardSize;
			this.board = new char[boardSize][boardSize];
			initializeBoard();
			currentPlayerSymbol = 'S';
		}

		protected void initializeBoard() {
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					board[i][j] = ' ';
				}
			}
		}

		public abstract void makeMove(int row, int col, char symbol, char[][] board);
		
		public abstract boolean checkForSOS(char[][] board);

		public abstract int getWinner(); // For General mode, return the player with the most sequences

		// Add other common methods and attributes as needed
	}

	// Implement Simple SOS Game Mode
	class SimpleSOSGameMode extends SOSGameMode {
	    private boolean gameEnded;
	    private int gameWinner;

	    public SimpleSOSGameMode(int boardSize) {
	        super(boardSize);
	        gameEnded = false;
	        gameWinner = 0;
	    }

	    public boolean checkForSOS(char[][] board) {
	    	int numRows = board.length;
	        int numCols = board[0].length;
	        String target = "SOS";

	        for (int i = 0; i < numRows; i++) {
	            for (int j = 0; j < numCols; j++) {
	                if (searchInDirection(board, i, j, target, 0, 1) ||  // Horizontal
	                    searchInDirection(board, i, j, target, 1, 0) ||  // Vertical
	                    searchInDirection(board, i, j, target, 1, 1) ||  // Diagonal (bottom-right)
	                    searchInDirection(board, i, j, target, 1, -1)) {  // Diagonal (bottom-left)
	                    return true;
	                }
	            }
	        }
	        return false;
	    }
	    
	    public static boolean searchInDirection(char[][] board, int row, int col, String target, int rowDir, int colDir) {
	        int numRows = board.length;
	        int numCols = board[0].length;
	        int targetIndex = 0;
	        int currentRow = row;
	        int currentCol = col;

	        while (targetIndex < target.length()) {
	            if (currentRow < 0 || currentRow >= numRows || currentCol < 0 || currentCol >= numCols || board[currentRow][currentCol] != target.charAt(targetIndex)) {
	                return false;
	            }
	            currentRow += rowDir;
	            currentCol += colDir;
	            targetIndex++;
	        }

	        return true;
	    }
	    


	    @Override
	    public void makeMove(int row, int col, char symbol, char[][] board) {
	    	int numRows = board.length; // Number of rows
	        int numCols = board[0].length;
	        if (!gameEnded && board[row][col] == ' ') {
	            board[row][col] = symbol;
	            buttons[row][col].setText(Character.toString(symbol));

	            if (checkForSOS(board)) {
	                // An SOS sequence is found; declare the winner and end the game
	            	if(player1Turn) {
	            		gameWinner = 1;
	            	}
	            	else {
	            		gameWinner = 2;
	            	}
	                announceWinner();
	            }
	            else if (isGameOver()) {
	            	announceWinner();
	            }
	            
	            else {
	                currentPlayerSymbol = (symbol == 'S') ? 'O' : 'S';

	                // Disable the button after placing the symbol
	                disableButton(row, col);
	            }
	        }
	    }

	    private void announceWinner() {
	    	String message;
	    	if (gameWinner == 1) {
	    		message = "Player 1 has won the game!";
	    	}
	    	else if (gameWinner == 2) {
	    		message = "Player 2 has won the game!";
	    	}
	    	else {
	    		message = "Tie Game";
	    	}
	        JOptionPane.showMessageDialog(null, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
	        gameEnded = true;
	        for (int i = 0; i < boardSize; i++) {
	            for (int j = 0; j < boardSize; j++) {
	            	disableButton(i, j);
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
		public GeneralSOSGameMode(int boardSize) {
			super(boardSize);
		}

		@Override
		public void makeMove(int row, int col, char symbol, char[][] board) {
			// Implement move logic for General mode
			// Update the board and switch players
		}

		@Override
		public int getWinner() {
			// Implement logic to determine the winner in General mode
			return 0; // Return player 1 or player 2 based on the game outcome
		}

		@Override
		public boolean checkForSOS(char[][] board) {
			// TODO Auto-generated method stub
			return false;
		}

		// Add other methods specific to General mode
	}

	private void setBoardSize(int newSize) {
		this.boardSize = newSize;
		this.board = new char[boardSize][boardSize];
		this.buttons = new JButton[boardSize][boardSize]; // Recreate buttons array
	}

	private void recreateBoard() {
		// Remove the old board panel
		getContentPane().removeAll();

		// Recreate the UI with the new board size
		createUI();

		revalidate();
		repaint();
	}

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
			turnLabel.setText("Player 1's Turn");
		} else {
			turnLabel.setText("Player 2's Turn");
		}
	}

	private void updateModeLabel(JLabel statusLabel) {
		if (simpleMode) {
			modeLabel.setText("Simple Mode");
		} else {
			modeLabel.setText("Complex Mode");
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
		setSize(600, 450);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel playerPanel = new JPanel(new GridLayout(2, 2));
		JLabel player1Label = new JLabel("Player 1:");
		JLabel player2Label = new JLabel("Player 2:");
		player1RadioButtonS = new JRadioButton("S", true);
		player1RadioButtonO = new JRadioButton("O");
		player2RadioButtonS = new JRadioButton("S", true);
		player2RadioButtonO = new JRadioButton("O");
		// Button Groups
		ButtonGroup player1ButtonGroup = new ButtonGroup();
		ButtonGroup player2ButtonGroup = new ButtonGroup();
		player1ButtonGroup.add(player1RadioButtonS);
		player1ButtonGroup.add(player1RadioButtonO);
		player2ButtonGroup.add(player2RadioButtonS);
		player2ButtonGroup.add(player2RadioButtonO);
		// Player Labels and Buttons
		playerPanel.add(player1Label);
		playerPanel.add(player1RadioButtonS);
		playerPanel.add(player1RadioButtonO);
		playerPanel.add(player2Label);
		playerPanel.add(player2RadioButtonS);
		playerPanel.add(player2RadioButtonO);
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
					        char symbol = 'S';
					        if (player1Turn) {
					            if (player1RadioButtonSisSelected) {
					                symbol = 'S';
					            } else {
					                symbol = 'O';
					            }
					        } else {
					            if (player2RadioButtonSisSelected) {
					                symbol = 'S';
					            } else {
					                symbol = 'O';
					            }
					        }

					        SOSGameMode gameMode = simpleMode ? new SimpleSOSGameMode(boardSize)
					                : new GeneralSOSGameMode(boardSize);
					        gameMode.makeMove(row, col, symbol, board);
					        
					        
					        togglePlayer();
					        

					        updateTurnLabel(); // Add this line to update the turn label
					    }
					}
				});
				boardPanel.add(buttons[i][j]);
			}
		}
		simpleModeRadioButton = new JRadioButton("Simple Mode", true);
		complexModeRadioButton = new JRadioButton("Complex Mode");

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
		add(boardPanel, BorderLayout.CENTER);

		// Game mode selection
		JPanel modePanel = new JPanel(new GridLayout(2, 1));
		ButtonGroup modeButtonGroup = new ButtonGroup();
		modeButtonGroup.add(simpleModeRadioButton);
		modeButtonGroup.add(complexModeRadioButton);
		modePanel.add(simpleModeRadioButton);
		modePanel.add(complexModeRadioButton);
		add(modePanel, BorderLayout.EAST);

		// Set and Display Mode Label
		modeLabel = new JLabel("Simple Mode");
		modeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		modeLabel.setFont(new Font("Arial", Font.BOLD, 16));
		add(modeLabel, BorderLayout.SOUTH);

		// Turn indicator label
		turnLabel = new JLabel("Player 1's Turn");
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
				try {
					int newSize = Integer.parseInt(boardSizeField.getText());
					if (newSize > 2) {
						setBoardSize(newSize);
						initializeBoard();
						recreateBoard();
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
}
