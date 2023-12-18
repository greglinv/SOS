package sos;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import sos.SOSGame.ComputerPlayer;

public class ComputerPlayerTest {

    private char[][] emptyBoard;
    SOSGame sosGameInstance = new SOSGame();
    private ComputerPlayer computerPlayer = sosGameInstance.new ComputerPlayer();
    
    


    @Before
    public void setUp() {
        emptyBoard = new char[][]{
                {' ', ' ', ' '},
                {' ', ' ', ' '},
                {' ', ' ', ' '}
        };
        
        
    }

    @Test
    public void testMakeMove() {
        computerPlayer.makeMove(emptyBoard);

        // Verify that the computer made a valid move
        boolean validMove = false;
        for (int i = 0; i < emptyBoard.length; i++) {
            for (int j = 0; j < emptyBoard[0].length; j++) {
                if (emptyBoard[i][j] != ' ') {
                    validMove = true;
                    break;
                }
            }
        }
        assertTrue(validMove);
    }
}