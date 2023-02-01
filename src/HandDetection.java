package src;

import org.opencv.core.Core;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.core.Point;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;
import org.opencv.videoio.VideoCapture;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



import static org.opencv.imgproc.Imgproc.*;
import static src.StartCodeMainWindow.window;

/**
 * Erkennung der Handform
 * @author Waldemar Justus
 *
 */
public class HandDetection implements Runnable {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    AnimationGroups a = new AnimationGroups();
    // O ist die Standardkamera. 1 wäre eine andere Kamera
    VideoCapture camera = new VideoCapture(0);
    public Graphics bufferGraphics;
    static public Image offscreen;
    static public int largestContourIndex = -1;
    static public MatOfPoint largestContour;

    Size size = new Size(25, 25);
    Point pointAnchor = new Point(-1, -1);
    static Mat frame = new Mat(), gray = new Mat(), thresh = new Mat(),dst = new Mat();
    BufferedImage bufferFrame = null, bufferThresh = null, bufImgSchere = null, bufImgPapier = null,
            bufImgStein = null;
    public static JFrame windowKalibrierung = new JFrame();

    public static JPanel handPanel = new JPanel();

    private static int thresholdValue = 0;
    private int indexLine = 0;
    protected static String playerhand ="";
    protected static String computerhand ="";
    public String[] enemyHandWert = {"scissor","stone","paper"};
    public static  int lock = 0;
    public static boolean markerPapier = false;
    public static boolean markerSchere = false;
    public static boolean markerStein = false;
    public static boolean checkTimer = false;
    Timer timerPapier = new Timer(1500,null);
    Timer timerStein = new Timer(1500,null);
    Timer timerSchere = new Timer(1500,null);
    static HandDetection handdetection = new HandDetection();
    static Thread thread = new Thread(handdetection);
    GameStructur checkWinner = new GameStructur();

    public static void main(String[] args){
        thread.start();
        createWindow();
    }

    public static void createWindow(){
        windowKalibrierung.setSize(1000,700);
        windowKalibrierung.setTitle("Kalibrierung der Hand");
        windowKalibrierung.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        windowKalibrierung.setLocationRelativeTo(null);
        addComponentsToPane(windowKalibrierung.getContentPane());
        Image icon = Toolkit.getDefaultToolkit().getImage(".//resources//PapierBild.jpg");
        windowKalibrierung.setIconImage(icon);
        windowKalibrierung.setVisible(false);

        handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.PAGE_AXIS));

    }

    /**
     * 'Erkennung' der Handformen
     * @param graphics
     */
    public void paint (Graphics graphics) {
        //Image 'offscreen' an Kalibrierung anpassen
        if(MenuEventHandler.check==true) {
            offscreen = windowKalibrierung.createImage(1300, 800);
            if (offscreen != null) {
                bufferGraphics = offscreen.getGraphics();
            }
        }
        //Image 'offscreen' an MainWindow anpassen
        if(MenuEventHandler.check==false) {
            offscreen = window.createImage(384,288);
            if (offscreen != null) {
                bufferGraphics = offscreen.getGraphics();
            }
        }
        if (camera.isOpened()) {
            double arc, arcHull = 0;
            double circularity = 0;
            double cntArea = 0;
            camera.read(frame);
            if (frame.empty()) {
                return;
            }
            //Skalieren des Bildes von 640x480 auf 384x288 (Faktor 0.6)
            resize(frame,frame,new Size(384,288));

            cvtColor(frame, gray, COLOR_BGR2GRAY);
            GaussianBlur(gray, gray, size, 0);
            threshold(gray, thresh, thresholdValue, 255, THRESH_BINARY);
            Canny(thresh, dst, 250, 255, 3, false);
            dilate(dst, dst, new Mat(), pointAnchor, 2);
            List<MatOfPoint> kontur = new ArrayList<>();
            Mat hierarchy = new Mat();
            findContours(dst, kontur, hierarchy, RETR_EXTERNAL,
                    CHAIN_APPROX_SIMPLE);

            MatOfInt convexHullMatOfInt = new MatOfInt();
            List<Point> convexHullPointArrayList = new ArrayList<>();
            MatOfPoint convexHullMatOfPoint = new MatOfPoint();
            List<MatOfPoint> convexHullMatOfPointArrayList = new ArrayList<>();
            int[] hullInt = null;

            //Eine Hull um die Finger (Rote Linie)
            if (kontur.size() > 0) {
                cntArea = contourArea(getLargestContour(kontur));

                convexHull(getLargestContour(kontur), convexHullMatOfInt, true);
                //hullInt speichert die Anzahl der MatOfInt in einem int Array ab
                hullInt = convexHullMatOfInt.toArray();
                for (int j = 0; j < convexHullMatOfInt.toList().size(); j++) {
                    //Speichern der Punkte in einer Liste
                    convexHullPointArrayList.add(getLargestContour(kontur).toList().get(convexHullMatOfInt.toList().get(j)));
                }
                //Die Punkte aus der Liste nehmen, die neuen MatOfPoint in eine Liste packen
                convexHullMatOfPoint.fromList(convexHullPointArrayList);
                convexHullMatOfPointArrayList.add(convexHullMatOfPoint);
            }


            //Darstellung der Kontur ab einer bestimmten Größe; hier >2000
            if (cntArea > 2000) {

                if (kontur.size() > 0 && hullInt.length > 3) {
                    List<Integer> intList;
                    List<MatOfInt4> convDef = new ArrayList<>();
                    for (int i = 0; i < kontur.size(); i++) {
                        //Die Liste 'convDef' nimmt pro Durchgang ein neues MatOfInt4 auf
                        convDef.add(new MatOfInt4());
                        convexityDefects(getLargestContour(kontur), convexHullMatOfInt, convDef.get(i));
                        Mat nativeMat = convDef.get(i);
                        if (nativeMat.rows() > 0) {
                            indexLine = 0;
                            intList = convDef.get(i).toList();
                            Point[] data = getLargestContour(kontur).toArray();
                            for (int j = 0; j < intList.size(); j = j + 4) {

                                //Point start = data[intList.get(j)];
                                Point end = data[intList.get(j + 1)];
                                //Point defect = data[intList.get(j + 2)];
                                //Point depth = data[intList.get(j + 3) / 256];

                                //Mittelpunkt der Kontur berechnen
                                Moments m = moments(dst);
                                int xAchse = (int) Math.round(m.m10 / m.m00);
                                int yAchse = (int) Math.round(m.m01 / m.m00);
                                Point pMitte = new Point(xAchse, yAchse);

                                //Anzahl der Kreise reduzieren (Point end)
                                if (intList.get(j + 3) / 256 > 20) {
                                    //zeichnen
                                    indexLine++;
                                    circle(frame, end, 10, new Scalar(255, 0, 0), 2);
                                    circle(frame, pMitte, 10, new Scalar(255, 255, 255), -1);
                                    //Numerierung der Punkte
                                    putText(frame, String.valueOf(indexLine), end, 2, 1,
                                            new Scalar(255, 200, 0), 1);
                                    //Line von Punkt zum Mittelpunkt
                                    line(frame, end, pMitte, new Scalar(255, 0, 255), 2);
                                }
                            }
                        }
                    }
                }

                if (kontur.size() > 0) {
                    //Zeichne die Kontur um die Hand
                    drawContours(frame, kontur, -1, new Scalar(124, 252, 0), 3);
                    //Zeichne die Hull um die Hand
                    drawContours(frame, convexHullMatOfPointArrayList, -1,
                            new Scalar(0, 0, 255), 2);
                }

                //Position für die Wörter: Stein, Schere und Papier
                Point pStein = new Point(50, 25);
                Point pSchere = new Point(140, 25);
                Point pPapier = new Point(250, 25);

                if (kontur.size() > 0) {
                    for (int i = 0; i < kontur.size(); i++) {

                        //Kontur und die Hull in MatOfPoint2f umwandeln
                        MatOfPoint2f kontur2f = new MatOfPoint2f(getLargestContour(kontur).toArray());
                        MatOfPoint2f hull2f = new MatOfPoint2f(convexHullMatOfPoint.toArray());
                        arcHull = arcLength(hull2f, true);
                        arc = arcLength(kontur2f, true);

                        //Kreisförmigkeit berechnen
                        circularity = (4 * Math.PI * cntArea) / (arc * arc);

                        //Timer stoppen, wenn man nach dem Start des Spiels auf 'zurück' klickt
                        if(checkTimer==false){
                            timerPapier.stop();
                            timerSchere.stop();
                            timerStein.stop();
                        }
                        //Erkennung der Geste 'Stein'
                        //Stein muss mind. 1 Linie & max. 3 Linien haben & die Hull muss kleiner kleiner 640 sein
                        if ((indexLine > 0 && indexLine < 4) && arcHull < 640 &&circularity>0.028) {





                            //Hebt Rot "Stein" hervor
                            putText(frame, "Stein", pStein, 2, 1,
                                    new Scalar(0, 0, 255), 1);
                            //Setzt die anderen auf die ursprüngliche blaue Farbe zurück
                            putText(frame, "Schere", pSchere, 2, 1,
                                    new Scalar(255, 200, 0), 1);
                            putText(frame, "Papier", pPapier, 2, 1,
                                    new Scalar(255, 200, 0), 1);
                            //3 Sekunden Countdown, um die richtige Geste zu übernehmen
                            if (checkTimer == true) {
                                if (!markerStein) {
                                    markerStein = true;
                                    markerPapier = false;
                                    markerSchere = false;
                                    timerStein.restart();
                                    ActionListener actionListener = new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            if (lock < 1){
                                                lock += 1;
                                                computerhand = enemyHandWert[(int)(Math.random() * 3)];
                                                playerhand = "stone";
                                                a.giveHands(playerhand, computerhand);
                                                checkWinner.Punkte(playerhand,computerhand);
                                            }

                                        }
                                    };
                                    timerStein.addActionListener(actionListener);
                                    timerPapier.stop();
                                    timerSchere.stop();
                                }
                            }

                        }
                        //Erkennung der Geste 'Schere'
                        // Es müssen 4 Linien erkannt werden & das Maß der Rundheit muss einen bestimmten Wert haben
                        else if ((indexLine ==4) && circularity > 0.0095 && circularity < 0.02) {

                            //Hebt Rot "Schere" hervor
                            putText(frame, "Schere", pSchere, 2, 1,
                                    new Scalar(0, 0, 255), 1);
                            //Setzt die anderen auf die ursprüngliche blaue Farbe zurück
                            putText(frame, "Stein", pStein, 2, 1,
                                    new Scalar(255, 200, 0), 1);
                            putText(frame, "Papier", pPapier, 2, 1,
                                    new Scalar(255, 200, 0), 1);
                            //3 Sekunden Countdown, um die richtige Geste zu übernehmen
                            if (checkTimer== true) {
                                if (!markerSchere) {
                                    markerStein = false;
                                    markerSchere = true;
                                    markerPapier = false;
                                    timerSchere.restart();
                                    ActionListener actionListener = new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            if (lock < 1){
                                                lock += 1;
                                                computerhand = enemyHandWert[(int)(Math.random() * 3)];
                                                playerhand = "scissor";
                                                a.giveHands(playerhand, computerhand);
                                                checkWinner.Punkte(playerhand,computerhand );
                                            }
                                        }
                                    };
                                    timerSchere.addActionListener(actionListener);
                                    timerPapier.stop();
                                    timerStein.stop();
                                }
                            }
                        }
                        //Erkennung der Geste 'Papier'. Es hat als einzige Handform, zwei Optionen der Erkennung
                        //Mehr als 4 Linien und weniger als 9 Linien, um eine normal geöffnete Hand zu erkennen
                        //Weniger als 4 Linien und die Hull muss größer als 650 sein, um die flache Hand zu erkennen
                        else if (((indexLine > 4 && indexLine < 9)) ||
                                (indexLine < 4 && arcHull > 650 && circularity<0.025)) {

                            //Hebt Rot "Papier" hervor
                            putText(frame, "Papier", pPapier, 2, 1,
                                    new Scalar(0, 0, 255), 1);
                            //Setzt die anderen auf die ursprüngliche blaue Farbe zurück
                            putText(frame, "Stein", pStein, 2, 1,
                                    new Scalar(255, 200, 0), 1);
                            putText(frame, "Schere", pSchere, 2, 1,
                                    new Scalar(255, 200, 0), 1);
                            //3 Sekunden Countdown, um die richtige Geste zu übernehmen
                            if (checkTimer == true) {
                                if (!markerPapier) {
                                    markerPapier = true;
                                    markerStein = false;
                                    markerSchere = false;
                                    timerPapier.restart();
                                    ActionListener actionListener = new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            if (lock < 1){
                                                lock += 1;
                                                computerhand = enemyHandWert[(int)(Math.random() * 3)];
                                                playerhand = "paper";
                                                a.giveHands(playerhand, computerhand);
                                                checkWinner.Punkte(playerhand,computerhand);
                                            }

                                        }
                                    };
                                    timerPapier.addActionListener(actionListener);
                                    timerSchere.stop();
                                    timerStein.stop();
                                }
                            }
                        }
                        //Falls nichts erkannt wird, werden die Timer gestoppt
                        else {
                            markerStein=false;
                            markerPapier=false;
                            markerSchere=false;
                            timerPapier.stop();
                            timerSchere.stop();
                            timerStein.stop();
                        }
                    }
                }
            }
            //Falls die Kontur eine gewisse Größe nicht erreicht hat, wird der Timer nicht gestartet
            if (cntArea < 2000) {
                markerStein=false;
                markerPapier=false;
                markerSchere=false;
                timerPapier.stop();
                timerSchere.stop();
                timerStein.stop();
            }

            bufferFrame = Mat2BufferedImage(frame);
            bufferThresh = Mat2BufferedImage(thresh);

            //Lesen der Bilder
            try {
                bufImgSchere = ImageIO.read(new File(".//resources//SchereBild.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bufImgPapier = ImageIO.read(new File(".//resources//PapierBild.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bufImgStein = ImageIO.read(new File(".//resources//SteinBild.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //BufferGraphics für das Fenster 'Kalibrierung'
            if(MenuEventHandler.check ==true&&bufferGraphics!=null&&offscreen!=null) {
                Font myFont = new Font("Arial", Font.BOLD, 20);
                bufferGraphics.setFont(myFont);
                bufferGraphics.setColor(Color.white);
                //bufferGraphics.fillRect(0, 0, 5000, 5000);
                //Webcam und Threshold Ausgabe zeichnen
                bufferGraphics.drawImage(bufferFrame, 10, 0, null);
                bufferGraphics.drawImage(bufferThresh, 408, 0, null);
                //Bilder zeichnen
                bufferGraphics.drawImage(bufImgSchere,825,30,null);
                bufferGraphics.drawImage(bufImgStein,825,180,null);
                bufferGraphics.drawImage(bufImgPapier,825,330,null);
                bufferGraphics.setColor(Color.black);
                //Beschriftung
                bufferGraphics.drawString("Schere", 840, 20);
                bufferGraphics.drawString("Stein", 850, 165);
                bufferGraphics.drawString("Papier", 845, 315);
                bufferGraphics.drawString("Kreisförmigkeit: " + circularity, 10, 350);
                bufferGraphics.drawString("arcHull: " + arcHull, 10, 400);
                graphics.drawImage(offscreen, 0, 250, null);
            }
            //BufferGraphics für das Fenster 'MainWindow'
            if(MenuEventHandler.check==false&&bufferGraphics!=null&&offscreen!=null){
                Font myFont = new Font("Arial", Font.BOLD, 20);
                bufferGraphics.setFont(myFont);
                bufferGraphics.setColor(Color.white);
                //bufferGraphics.fillRect(0, 0, 5000, 5000);
                bufferGraphics.drawImage(bufferFrame, 0, 0, null);
                graphics.drawImage(offscreen, 1011, 468, null);
            }
        }
    }

    /**
     Eine Methode, um die größte Kontur im Bild zu finden
     **/
    public static MatOfPoint getLargestContour(List<MatOfPoint> kontur) {
        if(kontur.size()>0) {
            largestContourIndex =0;
            double bigErg = 0;
            int contourNumber = kontur.size();
            for (int i = 0; i < contourNumber; i++) {
                MatOfPoint2f kontur2f = new MatOfPoint2f(kontur.get(i).toArray());
                double erg = Imgproc.arcLength(kontur2f, false);
                if(erg>bigErg){
                    bigErg = erg;
                    largestContourIndex = i;
                }
            }
            largestContour = kontur.get(largestContourIndex);
        }
        return largestContour;
    }

    /**
     * Methode, um die Matrize in ein BufferedImage umzuwandeln.
     * @param src Die Matrize, die als BufferedImage umgewandelt werden soll.
     * @return Als return wird das BufferedImage zurückgegeben.
     */
    public static BufferedImage Mat2BufferedImage(Mat src) {

        //Umwandeln in Bytes
        MatOfByte matByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", src, matByte);
        byte[] byteArray = matByte.toArray();
        //Inputstream liest den byteArray aus
        InputStream input = new ByteArrayInputStream(byteArray);
        if(input.equals(null)){
            System.out.println("in = null");
            return null;
        }
        BufferedImage buffImage = null;
        try {
            buffImage = ImageIO.read(input);
        } catch (IOException e) {
            System.out.println("Fehler beim Mat2BufferedImage");
        }
        return buffImage;
    }


    /**
     * Der neue Threshold value wird übergeben.
     */
    private static void update() {
        threshold(gray, thresh, thresholdValue, 255, THRESH_BINARY);
    }

    private static void addComponentsToPane(Container pane) {
        //Button, um die Kalibrierung zu bestätigen
        JButton buttonOkay = new JButton("Okay");
        buttonOkay.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                windowKalibrierung.setVisible(false);
                MenuEventHandler.check=false;
                window.setVisible(true);
            }
        });
        buttonOkay.setBounds(800,80,100,20);

        JPanel sliderPanel = new JPanel();
        JLabel anweisung = new JLabel("<html><body>Das ist die Kalibrierung.<br>" +
                "Der Hintergrund sollte weiß sein und nur die Hand sollte zu sehen sein. " +
                "Benutze den Threshold, um die Kontur der Hand vollständig zu erfassen. " +
                "<br>Danach klicke auf 'okay'.</body></html>");

        Font fontInstruction= new Font("Arial",Font.PLAIN,18);
        anweisung.setFont(fontInstruction);
        anweisung.setBounds(30,0,400,200);
        //Slider um den Threshold anzupassen
        sliderPanel.add(new JLabel("Threshold Value"));
        JSlider sliderThreshValue = new JSlider(0, 255, 0);
        sliderPanel.setBounds(450,50,300,300);
        sliderThreshValue.setMajorTickSpacing(50);
        sliderThreshValue.setMinorTickSpacing(10);
        sliderThreshValue.setPaintTicks(true);
        sliderThreshValue.setPaintLabels(true);
        sliderPanel.add(sliderThreshValue);
        sliderThreshValue.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                thresholdValue = source.getValue();
                update();
            }
        });
        pane.setLayout(null);
        pane.add(anweisung);
        pane.add(buttonOkay);
        pane.add(sliderPanel);
    }

    @Override
    public void run() {
        if (camera.isOpened()) {
            while (true) {
                Graphics graphicsStart = window.getGraphics();
                Graphics graphics = windowKalibrierung.getGraphics();
                if (MenuEventHandler.check == true) {
                    paint(graphics);
                }else {
                    paint(graphicsStart);
                }
                if(thread.isInterrupted()){
                    return;
                }
            }
        }
    }
}
