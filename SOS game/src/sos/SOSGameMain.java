package sos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SOSGameMain {
    public static void main(String[] args) {
        int boardSize = Integer.parseInt(JOptionPane.showInputDialog("Enter board size (e.g., 3):"));
        javax.swing.SwingUtilities.invokeLater(() -> new SOSGame(boardSize));
    }
}