package sos;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
    
    
    //Create the Board with set size
    public SOSGame(int boardSize) {
        this.boardSize = boardSize;
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
                        		}
                        		else {
                        			symbol = 'O';
                        		}
                        	}
                        	else {
                        		if (player2RadioButtonSisSelected) {
                        			symbol = 'S';
                        		}
                        		else {
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
}