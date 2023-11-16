package sos;

import sos.SOSGame;
import sos.SOSGame.SimpleSOSGameMode;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static org.junit.Assert.*;

public class SimpleSOSGameModeTest {
	private SOSGame game;
	private SimpleSOSGameMode gameMode;

	@Before
	public void setUp() {
		// Initialize the game mode with the desired board size (e.g., 3x3)
		game = new SOSGame();
		gameMode = new SOSGame().new SimpleSOSGameMode(3);
	}

	//AC 1.1
	@Test
    public void testValidBoardSize() {
        char[][] board = gameMode.board; 
        int initialWinner = gameMode.getWinner();

        gameMode.makeMove(0, 0, 'S', board);
        gameMode.makeMove(0, 1, 'O', board);

        // Perform assertions
        assertEquals('S', board[0][0]);
        assertEquals('O', board[0][1]);
        int finalWinner = gameMode.getWinner();
        assertEquals(initialWinner, finalWinner);
    }
	
	//AC 1.4
	@Test
    public void testChangeBoardSize() {
        // Set the new board size
        int newBoardSize = 4;

        // Simulate user interaction: Enter a new number and press the new game button
        game.boardSizeField.setText(String.valueOf(newBoardSize)); // Set the new board size
        game.newGameButton.doClick(); // Simulate the button click
        assertEquals(newBoardSize, game.getBoardSize());
    }
	
	//AC 5.1
	@Test
	public void testCheckForSOSWithSOS() {
		char[][] board = new char[][] { { 'S', 'O', 'S' }, { 'S', 'O', ' ' }, { 'O', 'S', 'O' } };
		boolean sosDetected = gameMode.checkForSOS(board);
		assertTrue(sosDetected);
	} 
	
	//AC 5.2
	@Test
	public void testCheckForSOSWithNoSOS() {
		char[][] board = new char[][] { { 'S', ' ', 'O' }, { 'O', 'S', ' ' }, { ' ', 'O', 'S' } };
		boolean sosDetected = gameMode.checkForSOS(board);
		assertFalse(sosDetected);
	}

	

}