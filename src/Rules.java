package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *  Enthält die Spielregeln zum anschauen
 */
public class Rules  {



    public static void main(String[] args) throws IOException {

        createWindow();
    }

    /**
     * Erstellt ein neues Fenster, in dem die Regeln angezeigt werden
     */
    public static void createWindow() {

        JFrame windowRules = new JFrame();
        JPanel rulePanel = new JPanel();
        JButton goBack = new JButton();


        windowRules.setSize(400,700);
        windowRules.setTitle("Regelwerk");
        windowRules.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        windowRules.setResizable(false);
        windowRules.setLocationRelativeTo(null);

        rulePanel.setBackground(Color.RED);
        rulePanel.setLayout(new BoxLayout(rulePanel, BoxLayout.PAGE_AXIS));

        try {
         BufferedImage myPicture = ImageIO.read(new File(".//resources//RULES.jpg"));
         JLabel ruleImg = new JLabel(new ImageIcon(myPicture));
         ruleImg.setAlignmentX(JComponent.CENTER_ALIGNMENT);

         rulePanel.add(ruleImg);

        }catch (Exception e){

        }

        goBack.setText("Back");
        goBack.setMaximumSize(new Dimension(150,50));
        goBack.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        goBack.setBorderPainted(false);
        goBack.setFocusPainted(false);
        goBack.setBackground(Color.BLACK);
        goBack.setFont(new Font("SansSerif",Font.BOLD,16));
        goBack.setForeground(Color.white);
        goBack.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                windowRules.setVisible(false);
                StartCodeMainWindow.window.setVisible(true);
            }
        });

        rulePanel.add(Box.createRigidArea(new Dimension(0,6)));
        rulePanel.add(goBack);
        windowRules.add(rulePanel);
        Image icon = Toolkit.getDefaultToolkit().getImage(".//resources//PapierBild.jpg");
        windowRules.setIconImage(icon);
        windowRules.setVisible(true);

    }

}
