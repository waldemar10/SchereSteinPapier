package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


/**
 * Java class for handling menuPane events
 *
 * @author Darius Schippritt
 * @version 27.10.2021
 *
 */

public class MenuEventHandler implements ActionListener {
    static public boolean check =false;
    static public boolean start =false;
    AnimationGroups a = new AnimationGroups();

    GameStructur points = new GameStructur();

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == StartCodeMainWindow.buttonStart) {
            // Handle event
            check = false;
            StartCodeMainWindow.textPlayer.setText("");
            HandDetection.checkTimer = true;
            HandDetection.markerStein = false;
            HandDetection.markerPapier = false;
            HandDetection.markerSchere = false;
            HandDetection.lock = 0;
            StartCodeMainWindow.buttonQuit.setVisible(false);
            StartCodeMainWindow.buttonStart.setVisible(false);
            StartCodeMainWindow.buttonKalibrierung.setVisible(false);
            StartCodeMainWindow.buttonRules.setVisible(false);
            StartCodeMainWindow.buttonReset.setVisible(true);
            StartCodeMainWindow.buttonRestart.setVisible(true);
            StartCodeMainWindow.buttonBack.setVisible(true);
            start = true;
            HandDetection.playerhand = "";
            HandDetection.computerhand = "";
            a.reset();

        } else if (e.getSource() == StartCodeMainWindow.buttonQuit) {
            System.exit(0);

        } else if (e.getSource() == StartCodeMainWindow.buttonKalibrierung) {
            check = true;
            HandDetection.windowKalibrierung.setVisible(true);
            StartCodeMainWindow.window.setVisible(false);

        } else if (e.getSource() == StartCodeMainWindow.buttonRules) {

            Rules.createWindow();
        } else if (e.getSource() == StartCodeMainWindow.buttonReset) {
            GameStructur.setPlayerpoint(0);
            GameStructur.setEnemypoint(0);
            System.out.println(GameStructur.getEnemypoint());
            check = false;
            HandDetection.checkTimer = true;
            HandDetection.markerStein = false;
            HandDetection.markerPapier = false;
            HandDetection.markerSchere = false;
            HandDetection.lock = 0;
            StartCodeMainWindow.textPlayer.setText("   Spielpunktestand zurückgesetzt !  ");

        } else if (e.getSource() == StartCodeMainWindow.buttonRestart) {
            check = false;
            HandDetection.checkTimer = true;
            HandDetection.markerStein = false;
            HandDetection.markerPapier = false;
            HandDetection.markerSchere = false;
            HandDetection.lock = 0;
            StartCodeMainWindow.textPlayer.setText("   Neue Runde!!!  Punktestand: " + GameStructur.getPlayerpoint() + " : " + GameStructur.getEnemypoint());
            HandDetection.playerhand = "";
            HandDetection.computerhand = "";
            a.reset();

        } else if (e.getSource() == StartCodeMainWindow.buttonBack) {
            HandDetection.checkTimer = false;
            StartCodeMainWindow.buttonQuit.setVisible(true);
            StartCodeMainWindow.buttonStart.setVisible(true);
            StartCodeMainWindow.buttonKalibrierung.setVisible(true);
            StartCodeMainWindow.buttonRules.setVisible(true);
            StartCodeMainWindow.buttonReset.setVisible(false);
            StartCodeMainWindow.buttonRestart.setVisible(false);
            StartCodeMainWindow.buttonBack.setVisible(false);
            start = false;
            StartCodeMainWindow.textPlayer.setText("  Willkommen bei Schere Stein Papier Ultimate!");

        }
    }
}