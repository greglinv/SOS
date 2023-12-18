package sos;

import javax.swing.*;

public class SOSGameMain {
	static int boardSize;

	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SOSGame();
        });
    }
}