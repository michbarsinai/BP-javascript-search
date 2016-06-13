package bp.satellite.demo;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import bp.bprogram.BProgram;
import bp.events.BEvent;
import bp.satellite.demo.events.ObsAlert;
import bp.satellite.demo.events.StaticEvents;

/**
 * Class that implements the Graphical User Interface for the simulation
 */
@SuppressWarnings({"serial", "unused"})
public class GUI implements Serializable {

    public JFrame window = new JFrame("BP Satellite Simulation");
    public JButton startbutton = new JButton("Start Simulation");
    public JButton obsbutton = new JButton("Enter Obs");
    public ImageIcon picimage = new ImageIcon(getClass().getResource("takingpicture.jpg"));
    public ImageIcon satimage = new ImageIcon(getClass().getResource("satnoobs.jpg"));
    public ImageIcon satobsimage = new ImageIcon(getClass().getResource("satwithobs.jpg"));
    public ImageIcon satRTimage = new ImageIcon(getClass().getResource("satelliteRT.jpg"));
    public ImageIcon satLTimage = new ImageIcon(getClass().getResource("satelliteLT.jpg"));
    public JLabel picimagelable = new JLabel(picimage);
    public JLabel satimagelable = new JLabel(satimage);
    public JLabel satobsimagelable = new JLabel(satobsimage);
    public JLabel satRTimagelable = new JLabel(satRTimage);
    public JLabel satLTimagelable = new JLabel(satLTimage);
    public JLabel sattimelabel = new JLabel("Time :  ");
    public JLabel satposlabel = new JLabel("Position :  ");
    public JLabel satvellabel = new JLabel("Velocity :  ");
    public JLabel piclabel = new JLabel("Pictures Taken :   ");
    public JLabel starttimelable = new JLabel("Start");
    public JLabel endtimelable = new JLabel("End");
    public JLabel obslabel = new JLabel("                              ");
    public JTextField strattimetext = new JTextField("250", 5);
    public JTextField endtimetext = new JTextField("500", 5);
    public JTextField postext = new JTextField("300", 5);
    int obsposnum, obsstarttimenum, obsendtimenum, imagecount = 1;
    boolean buttonflag = true;
    public double pos = 0;
    public double vel = 1;
    public int time;

    private BProgram bp;
    JPanel board;

    /**
     * Constructor.
     */
    public GUI(BProgram bp) {
        this.bp = bp;

        // Create window
        window.setSize(650, 650);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());

        // The board
        JPanel board2 = new JPanel();
        board = new JPanel();
        JPanel obstaclepanel = new JPanel();
        JPanel timeobstaclepanel = new JPanel();
        JPanel posobstaclepanel = new JPanel();
        JPanel telpanel = new JPanel();

        // The message label
        satimagelable.setHorizontalAlignment(JLabel.CENTER);
        satRTimagelable.setHorizontalAlignment(JLabel.CENTER);
        // Create buttons
        board2.add(startbutton);
        startbutton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent a) {
                if (buttonflag) {
                    System.out.println("bp=" + bp);
                    bp.enqueueExternalEvent( StaticEvents.StartSimulation );
                }
                buttonflag = false;
            }
        });
        telpanel.add(sattimelabel);
        telpanel.add(satposlabel);
        telpanel.add(satvellabel);
        telpanel.add(piclabel);
        telpanel.setLayout(new GridLayout(4, 1));

        timeobstaclepanel.add(starttimelable);
        timeobstaclepanel.add(strattimetext);
        timeobstaclepanel.add(endtimelable);
        timeobstaclepanel.add(endtimetext);
        timeobstaclepanel.setLayout(new GridLayout(2, 1));
        obstaclepanel.setLayout(new GridBagLayout());
        GridBagConstraints obstaclepanelconst = new GridBagConstraints();
        obstaclepanelconst.gridx = 0;
        obstaclepanelconst.gridy = 0;
        obstaclepanelconst.weightx = 1;
        obstaclepanelconst.fill = GridBagConstraints.BOTH;
        obstaclepanelconst.anchor = GridBagConstraints.CENTER;
        obstaclepanel.add(timeobstaclepanel, obstaclepanelconst);
        posobstaclepanel.add(postext);
        obstaclepanelconst.gridx = 0;
        obstaclepanelconst.gridy = 1;
        obstaclepanelconst.weightx = 1;
        obstaclepanelconst.fill = GridBagConstraints.BOTH;
        obstaclepanelconst.anchor = GridBagConstraints.CENTER;
        obstaclepanel.add(posobstaclepanel, obstaclepanelconst);
        obstaclepanelconst.gridx = 1;
        obstaclepanelconst.gridy = 0;
        obstaclepanelconst.weightx = 6;
        obstaclepanelconst.fill = GridBagConstraints.HORIZONTAL;
        obstaclepanelconst.anchor = GridBagConstraints.CENTER;
        obstaclepanel.add(obsbutton, obstaclepanelconst);
        obstaclepanelconst.gridx = 1;
        obstaclepanelconst.gridy = 1;
        obstaclepanelconst.fill = GridBagConstraints.VERTICAL;
        obstaclepanelconst.anchor = GridBagConstraints.CENTER;
        obstaclepanel.add(obslabel, obstaclepanelconst);

        obsbutton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent b) {
                try {
                    obsposnum = Integer.parseInt(postext.getText());
                    obsstarttimenum = Integer.parseInt(strattimetext.getText());
                    obsendtimenum = Integer.parseInt(endtimetext.getText());

                        ObsAlert obsalert = new ObsAlert(obsstarttimenum, obsendtimenum, obsposnum);
                        bp.enqueueExternalEvent(obsalert);
                        
                } catch (NumberFormatException ee) {
                    JOptionPane.showMessageDialog(obstaclepanel, "Please Enter Numbers Only!");
                }

            }
        });

        TitledBorder obsborder = new TitledBorder("Obstacle Properties (In the Satellite Orbit)");
        obstaclepanel.setBorder(obsborder);
        TitledBorder timeobsborder = new TitledBorder("Time");
        timeobstaclepanel.setBorder(timeobsborder);
        TitledBorder posobsborder = new TitledBorder("Position");
        posobstaclepanel.setBorder(posobsborder);
        TitledBorder telborder = new TitledBorder("Sat telemetry");
        telpanel.setBorder(telborder);

        board.add(telpanel);
        board.add(obstaclepanel);
        board.setLayout(new GridLayout(3, 1));

        // Add the boards and the message component to the window
        window.add(board2, BorderLayout.NORTH);
        window.add(board);
        window.add(satimagelable, BorderLayout.SOUTH);

        // Make the window visible
        window.setVisible(true);
    }

    public void takePicture() {

        piclabel.setText("Pictures taken : " + imagecount++);
        board.add(picimagelable, BorderLayout.SOUTH);
        board.repaint();

    }

    public void fireTimeTick() {
        bp.enqueueExternalEvent(new BEvent("Tick"));
        board.remove(picimagelable);
        window.add(satimagelable, BorderLayout.SOUTH);
        board.repaint();
        window.repaint();
    }
    public void updateguitele() {
        sattimelabel.setText("Time : " + time);
        satposlabel.setText("Position : " + Math.floor(pos * 1000) / 1000);
        satvellabel.setText("Velocity : " + Math.floor(vel * 100) / 100);
    }


    void obsavoided() {
        obslabel.setText("The obstacle is out of sat orbit!");   
        window.remove(satobsimagelable);
        window.remove(satRTimagelable);
        window.add(satimagelable, BorderLayout.SOUTH);
        window.repaint();
    }

    void obsdetected() {
       obslabel.setText("Obstacle detected!");     
        window.remove(satimagelable);
        window.add(satobsimagelable, BorderLayout.SOUTH);
        board.repaint();
        window.repaint();
    }

    void RTfire() {
        obslabel.setText("Collision detected! Maneuver in progress.");    
        window.remove(satobsimagelable);
        window.remove(satimagelable);
        window.add(satRTimagelable, BorderLayout.SOUTH);
        window.repaint();
    }
}
