package sos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.BooleanSupplier;

//Create the Main Game Class
public class SOSGame extends JFrame {
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
	private boolean simpleModeButtonisSelected;
	private boolean player1RadioButtonSisSelected = true;
	private boolean player2RadioButtonSisSelected = true;
	private JLabel turnLabel;
	private JLabel modeLabel;
	private JTextField boardSizeField;
	private JButton applyBoardSizeButton;

	public SOSGame() {
		this.boardSize = 3; // Default board size is 3x3
		this.board = new char[boardSize][boardSize];
		this.player1Turn = true;
		this.buttons = new JButton[boardSize][boardSize];
		this.currentPlayerSymbol = 'S';
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
		//Button Groups
		ButtonGroup player1ButtonGroup = new ButtonGroup();
		ButtonGroup player2ButtonGroup = new ButtonGroup();
		player1ButtonGroup.add(player1RadioButtonS);
		player1ButtonGroup.add(player1RadioButtonO);
		player2ButtonGroup.add(player2RadioButtonS);
		player2ButtonGroup.add(player2RadioButtonO);
		//Player Labels and Buttons
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
							board[row][col] = symbol;
							buttons[row][col].setText(Character.toString(symbol));
							currentPlayerSymbol = symbol;
							player1Turn = !player1Turn;
							updateTurnLabel();
						}
					}
				});
				boardPanel.add(buttons[i][j]);
			}
		}
		simpleModeRadioButton = new JRadioButton("Simple Mode", true);
		complexModeRadioButton = new JRadioButton("Complex Mode");
		simpleModeRadioButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// Update the boolean variable based on the radio button's state
				simpleModeButtonisSelected = (e.getStateChange() == ItemEvent.SELECTED);
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
		//Set and Display Mode Label
		modeLabel = new JLabel("Simple Mode");
		modeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		modeLabel.setFont(new Font("Arial", Font.BOLD, 16));
		add(modeLabel, BorderLayout.SOUTH);
		// Turn indicator label
		turnLabel = new JLabel("Player 1's Turn");
		turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
		turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
		add(turnLabel, BorderLayout.NORTH);
		setVisible(true);

		// Add a JTextField for changing the board size
		boardSizeField = new JTextField(5);
		applyBoardSizeButton = new JButton("Apply Size");
		applyBoardSizeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int newSize = Integer.parseInt(boardSizeField.getText());
					if (newSize > 3) {
						setBoardSize(newSize);
						initializeBoard();
						recreateBoard();
					} else {
						// Handle invalid board size (less than or equal to 3)
						JOptionPane.showMessageDialog(SOSGame.this, "Board size must be larger than 3x3.");
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
		boardSizePanel.add(applyBoardSizeButton);

		// Add the board size input panel to the UI
		add(boardSizePanel, BorderLayout.NORTH);
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
		if (simpleModeButtonisSelected) {
			modeLabel.setText("Simple Mode");
		} else {
			modeLabel.setText("Complex Mode");
		}
	}

//Functions for tests
	public boolean isMoveValid(int row, int col) {
		if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
			return false;
// Check if the row and column are within the board bounds
		}
		return board[row][col] == ' ';
	}

	public char[][] getBoard() {
		return board;
	}

	public boolean makeMove(int row, int col, char symbol) {
		if (!isMoveValid(row, col)) {
			return false; // Invalid move
		}
		board[row][col] = symbol;
		return true;
	}

	public void togglePlayer() {
		if (player1Turn) {
			player1Turn = false;
		} else if (!player1Turn) {
			player1Turn = true;
		}
	}

	public boolean getCurrentPlayer() {
		return player1Turn;
	}

	public boolean isComplexMode() {
		return simpleModeButtonisSelected;
	}
}
