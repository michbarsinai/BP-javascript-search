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
import bp.satellite.demo.events.PosUpdate;
import bp.satellite.demo.events.StaticEvents;

/**
 * Class that implements the Graphical User Interface for the simulation
 */
@SuppressWarnings({"serial", "unused"})
public class GUI implements Serializable {

    public JFrame window = new JFrame("BP Satellite Simulation");
    public JButton startbutton = new JButton("Start Simulation");
    public ImageIcon picimage = new ImageIcon(getClass().getResource("takingpicture.jpg"));
    public ImageIcon satimage = new ImageIcon(getClass().getResource("satnoobs.jpg"));
    public JLabel picimagelable = new JLabel(picimage);
    public JLabel satimagelable = new JLabel(satimage);
    public JLabel sattimelabel = new JLabel("Time :  ");
    public JLabel satposlabel = new JLabel("Position :  ");
    public JLabel satvellabel = new JLabel("Velocity :  ");
    public JLabel piclabel = new JLabel("Pictures Taken :   ");
    int imagecount = 1;
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
       JPanel telpanel = new JPanel();

        // The message label
        satimagelable.setHorizontalAlignment(JLabel.CENTER);
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



        TitledBorder telborder = new TitledBorder("Sat telemetry");
        telpanel.setBorder(telborder);

        board.add(telpanel);
        board.setLayout(new GridLayout(2, 1));

        // Add the boards and the message component to the window
        window.add(board2, BorderLayout.NORTH);
        window.add(board);
        window.add(satimagelable, BorderLayout.SOUTH);

        // Make the window visible
        window.setVisible(true);
    }

    static double oldpos = 0;

    static int flag = 0;

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
}
