package src; /**
 * Copyright 2012-2013 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */



import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * (Wird nicht mehr genutzt)
 * Container class of the graphics application.
 * Creates a Window (JFrame) where the OpenGL canvas is displayed in.
 * Starts an animation loop, which triggers the renderer.
 *
 * Displays a triangle using the fixed function pipeline.
 *
 * Based on a tutorial by Chua Hock-Chuan
 * http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html
 *
 * and on an example by Xerxes Rånby
 * http://jogamp.org/git/?p=jogl-demos.git;a=blob;f=src/demos/es2/RawGL2ES2demo.java;hb=HEAD
 *
 * and based on predefined code from Karsten Lehn and Darius Schippritt
 * @author Waldemar Justus (changes since 2022)
 * @version 26.8.2015, 16.9.2015, 10.9.2017, 17.9.2018, 19.9.2018, 27.10.2021
 *
 */
public class StartCodeMainWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private static String FRAME_TITLE = "Schere Stein Papier Ultimate";

    private static final int WINDOW_WIDTH = 1408;
    private static final int WINDOW_HEIGHT = 768;
    private static final int FRAME_RATE = 60; // target frames per seconds

    public static JFrame window = new JFrame();
    public static JButton buttonStart = null;
    public static JButton buttonKalibrierung = null;
    public static JButton buttonRules = null;
    public static JButton buttonQuit= null;
    public static JButton buttonBack = null;
    public static JButton buttonRestart = null;
    public static JButton buttonReset = null;
    public static JLabel textPlayer = null;
    public static Color buttonFontColor = new Color(250,250,250);
    public static Color buttonBackground = new Color(10,10,10);
    /**
     * Standard constructor generating a Java Swing window for displaying an OpenGL canvas.
     */
    public StartCodeMainWindow() {
        // Setup an OpenGL context for the GLCanvas
        // Using the JOGL-Profile GL2
        // GL2: Compatibility profile, OpenGL Versions 1.0 to 3.0
        GLProfile profile = GLProfile.get(GLProfile.GL3);
        GLCapabilities capabilities = new GLCapabilities(profile);

        // Create the OpenGL Canvas for rendering content
        GLCanvas canvas = new StartRendererPP(capabilities);
        //canvas.setPreferredSize(new Dimension(GLCANVAS_WIDTH, GLCANVAS_HEIGHT));
        //canvas.setSize(new Dimension(GLCANVAS_WIDTH, GLCANVAS_HEIGHT));

        // Create an animator object for calling the display method of the GLCanvas
        // at the defined frame rate.
        final FPSAnimator animator = new FPSAnimator(canvas, FRAME_RATE, true);

        // Create the window container
        window.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        // Create and add split pane to window

        //Color
        Color c = Color.getHSBColor(1.0f,0.8f,0.80f);
        // Create and add button to menu pane
        buttonStart = new JButton("Start");
        buttonStart.setBounds(1134,30,150,50);
        buttonStart.setBackground(buttonBackground);
        buttonStart.setForeground(buttonFontColor);
        buttonStart.setFont(new Font("SansSerif",Font.BOLD,18));
        buttonStart.setHorizontalAlignment(JLabel.CENTER);
        buttonStart.setBorderPainted(false);
        buttonStart.setFocusPainted(false);
        buttonStart.addActionListener(new MenuEventHandler());

        buttonKalibrierung = new JButton("Kalibrierung");
        buttonKalibrierung.setBounds(1134,110,150,50);
        buttonKalibrierung.setBackground(buttonBackground);
        buttonKalibrierung.setForeground(buttonFontColor);
        buttonKalibrierung.setFont(new Font("SansSerif",Font.BOLD,18));
        buttonKalibrierung.setHorizontalAlignment(JLabel.CENTER);
        buttonKalibrierung.setBorderPainted(false);
        buttonKalibrierung.setFocusPainted(false);
        buttonKalibrierung.addActionListener(new MenuEventHandler());

        buttonRules = new JButton("Spielregeln");
        buttonRules.setBounds(1134,190,150,50);
        buttonRules.setBackground(buttonBackground);
        buttonRules.setForeground(buttonFontColor);
        buttonRules.setFont(new Font("SansSerif",Font.BOLD,18));
        buttonRules.setHorizontalAlignment(JLabel.CENTER);
        buttonRules.setBorderPainted(false);
        buttonRules.setFocusPainted(false);
        buttonRules.addActionListener(new MenuEventHandler());

        buttonQuit = new JButton("Beenden");
        buttonQuit.setBounds(1134,318,150,80);
        buttonQuit.setBackground(buttonBackground);
        buttonQuit.setForeground(buttonFontColor);
        buttonQuit.setFont(new Font("SansSerif",Font.BOLD,18));
        buttonQuit.setHorizontalAlignment(JLabel.CENTER);
        buttonQuit.setBorderPainted(false);
        buttonQuit.setFocusPainted(false);
        buttonQuit.addActionListener(new MenuEventHandler());

        buttonReset = new JButton("Punkte zurücksetzen");
        buttonReset.setBounds(1084,50,250,80);
        buttonReset.setBackground(buttonBackground);
        buttonReset.setForeground(buttonFontColor);
        buttonReset.setFont(new Font("SansSerif",Font.BOLD,18));
        buttonReset.setHorizontalAlignment(JLabel.CENTER);
        buttonReset.setBorderPainted(false);
        buttonReset.setFocusPainted(false);
        buttonReset.addActionListener(new MenuEventHandler());
        buttonReset.setVisible(false);

        buttonRestart = new JButton("Neue Runde!");
        buttonRestart.setBounds(1134,190,150,80);
        buttonRestart.setBackground(buttonBackground);
        buttonRestart.setForeground(buttonFontColor);
        buttonRestart.setFont(new Font("SansSerif",Font.BOLD,18));
        buttonRestart.setHorizontalAlignment(JLabel.CENTER);
        buttonRestart.setBorderPainted(false);
        buttonRestart.setFocusPainted(false);
        buttonRestart.addActionListener(new MenuEventHandler());
        buttonRestart.setVisible(false);

        buttonBack = new JButton("Zurück");
        buttonBack.setBounds(1134,318,150,80);
        buttonBack.setBackground(buttonBackground);
        buttonBack.setForeground(buttonFontColor);
        buttonBack.setFont(new Font("SansSerif",Font.BOLD,18));
        buttonBack.setHorizontalAlignment(JLabel.CENTER);
        buttonBack.setBorderPainted(false);
        buttonBack.setFocusPainted(false);
        buttonBack.addActionListener(new MenuEventHandler());
        buttonBack.setVisible(false);

        textPlayer = new JLabel();
        textPlayer.setBounds(10,10,500,50);
        textPlayer.setOpaque(true);
        textPlayer.setBackground(c);
        textPlayer.setFont(new Font("SansSerif",Font.BOLD,16));
        textPlayer.setForeground(Color.white);
        textPlayer.setText("  Willkommen bei Schere Stein Papier Ultimate!");
        JPanel menuBackground = new JPanel();
        menuBackground.setBounds(1000,0,408,768);
        menuBackground.setBackground(c);

        // Create and add glpanel to right side of split pane
        JPanel glPanel = new JPanel();
        canvas.setSize(999,768);
        glPanel.add(canvas);

        canvas.requestFocusInWindow();
        window.add(buttonStart);
        window.add(buttonKalibrierung);
        window.add(buttonRules);
        window.add(buttonQuit);
        window.add(buttonReset);
        window.add(buttonRestart);
        window.add(buttonBack);
        window.add(textPlayer);
        window.add(canvas);
        HandDetection.main(null);
        window.add(menuBackground);
        //DrawFrame.main(null);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Thread to stop the animator
                // before the program exits
                new Thread() {
                    @Override
                    public void run() {
                        if (animator.isStarted()) animator.stop();
                        System.exit(0);

                    }
                }.start();
            }
        });
        // Add split pane to window

        Image icon = Toolkit.getDefaultToolkit().getImage(".//resources//PapierBild.jpg");
        window.setIconImage(icon);
        window.setResizable(false);
        window.setTitle(FRAME_TITLE);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        animator.start();

    }

    /**
     * Creates the main window and starts the program
     * @param args The arguments are not used
     */
    public static void main(String[] args) {
        // Ensure thread safety
        SwingUtilities.invokeLater(new Runnable() {
                                       @Override
                                       public void run() {
                                           new StartCodeMainWindow();
                                       }
                                   }
        );
    }
}
