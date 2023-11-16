package sos;

import sos.SOSGame; // Import your SOSGame class
import sos.SOSGame.GeneralSOSGameMode; // Import the GeneralSOSGameMode class within your SOSGame class
import org.junit.Before; // Import the Before annotation
import org.junit.Test; // Import the Test annotation
import static org.junit.Assert.*;

public class GeneralSOSGameModeTest {

    private GeneralSOSGameMode gameMode;

    @Before
    public void setUp() {
        // Initialize the game mode with the desired board size (e.g., 4x4)
        gameMode = new SOSGame().new GeneralSOSGameMode(4);
    }
    
    //AC 7.1
    @Test
    public void testCheckForSOSWithSOS() {
        char[][] board = new char[][] {
            {'S', 'O', 'S', ' '},
            {'S', 'O', 'S', 'O'},
            {'O', 'S', 'S', 'O'},
            {'O', 'S', 'O', 'S'}
        };
        boolean sosDetected = gameMode.checkForSOS(board);
        assertTrue(sosDetected);
    }

    //AC 7.2
    @Test
    public void testCheckForSOSWithNoSOS() {
        char[][] board = new char[][] {
            {'S', ' ', 'O', ' '},
            {'O', 'S', ' ', 'O'},
            {'O', 'O', 'O', ' '},
            {' ', ' ', 'O', 'O'}
        };
        boolean sosDetected = gameMode.checkForSOS(board);
        assertFalse(sosDetected);
    }

    
   
}
