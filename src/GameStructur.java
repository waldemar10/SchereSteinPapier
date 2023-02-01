package src;

import javax.naming.event.ObjectChangeListener;

/**
 *  Enthält die Spielelogik
 */
public class GameStructur {

        private static int playerpoint;
        private static int enemypoint;


        public static int getPlayerpoint(){
                return playerpoint;
        }

        public static void setPlayerpoint(int playerPoints){
                playerpoint = playerPoints;
        }


        public static int getEnemypoint(){
                return enemypoint;
        }

        public static void setEnemypoint(int enemyPoints){
                enemypoint = enemyPoints;
        }


        /**
         * Ermittelt Gewinner bzw. Verlierer und vergibt Punkte
         * @param playerHand enthählt ein String mit der gewählten Form
         * @param enemyHand enthält ein String mit der vom computer generierten Form
         */
        public void Punkte(String playerHand, String enemyHand) {


                //Unentschieden
                if (playerHand.equals(enemyHand)) {
                        StartCodeMainWindow.textPlayer.setText("  UNENTSCHIEDEN!!!  Punktestand: " + GameStructur.getPlayerpoint() + " : " + GameStructur.getEnemypoint());

                        //Player gewinnt
                } else if (playerHand.equals("scissor") && enemyHand.equals("paper") ||
                           playerHand.equals("stone")  && enemyHand.equals("scissor") ||
                           playerHand.equals("paper") && enemyHand.equals("stone")) {

                        playerpoint++;
                        StartCodeMainWindow.textPlayer.setText("  GEWONNEN!!!  Punktestand: " + GameStructur.getPlayerpoint() + " : " + GameStructur.getEnemypoint());


                        //Computergegner gewinnt
                } else if (enemyHand.equals("scissor") && playerHand.equals("paper") ||
                           enemyHand.equals("stone")  && playerHand.equals("scissor") ||
                           enemyHand.equals("paper") && playerHand.equals("stone")) {


                        enemypoint++;
                        GameStructur.setEnemypoint(enemypoint);
                        StartCodeMainWindow.textPlayer.setText("  VERLOREN!!!  Punktestand: " + GameStructur.getPlayerpoint() + " : " + GameStructur.getEnemypoint());
                }


        }

}


