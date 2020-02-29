import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Class that creates a graphical user interface
 * for the server side of the chat system.
 */
public class ServerGUI extends JFrame {

    int port;
    private ChatServer chatServer;
    private JTextArea jTextArea;

    /**
     * Constructor for ServerGUI
     * @param port port to establish server socket
     * @param chatServer the main server class
     */
    public ServerGUI(int port, ChatServer chatServer) {
        this.port = port;
        this.chatServer = chatServer;

        /*
        Method reference to run createWindow in the event
        dispatch thread.
         */
        SwingUtilities.invokeLater(this::createWindow);
    }

    /**
     * Method which outputs a sting to the JTextArea
     * @param message message to be printed
     */
    public void print(String message) {
        jTextArea.append(message + "\n");
    }

    /**
     * Method which creates the GUI by adding
     * elements to the JFrame.
     */
    private void createWindow() {
        setTitle("Chat Server GUI");
        setVisible(true);
        setResizable(false);
        setSize(new Dimension(800,800));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.jTextArea = new JTextArea();

        /*
        Panel to handle starting the server and specifying the port as well
        as the EXIT button
         */
        JPanel connectPanel = new JPanel();
        connectPanel.setBorder(new TitledBorder("ChatServer"));

        /*
        Panel which handles everything that is printed to the server
        terminal
         */
        JPanel outputPanel = new JPanel();
        outputPanel.setBorder(new TitledBorder("Server Terminal"));

        createStart(connectPanel);
        createOutputArea(outputPanel);

        //Add panels to JFrame using border layouts to position panels
        getContentPane().add(connectPanel, BorderLayout.NORTH);
        getContentPane().add(outputPanel,BorderLayout.SOUTH);

        pack();
    }

    /**
     * Method which adds components to the server connection
     * panel.
     * @param panel the panel to be altered
     */
    private void createStart(JPanel panel) {
        JLabel labelPort = new JLabel();
        labelPort.setText("Port: ");

        //Current port is the default text in the JTextField
        JTextField portNum = new JTextField(Integer.toString(port));
        portNum.setPreferredSize(new Dimension(100,30));

        JButton buttonStart = new JButton("START");
        //Lambda expression to create action listener for button in a new thread
        buttonStart.addActionListener(e -> new Thread(() -> {
            checkPort(portNum);
            //Connection buttons disappear once connection established
            labelPort.setVisible(false);
            portNum.setVisible(false);
            buttonStart.setVisible(false);
            chatServer.setupServer(port);
        }).start());

        JButton buttonExit = new JButton("EXIT");
        buttonExit.addActionListener(e -> chatServer.exitServer());

        //Add components to panel
        panel.add(labelPort);
        panel.add(portNum);
        panel.add(buttonStart);
        panel.add(buttonExit);
    }

    /**
     * Method which adds components to the output area
     * panel.
     * @param panel the panel to be altered
     */
    private void createOutputArea(JPanel panel) {
        jTextArea.setEditable(false);
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);

        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        jScrollPane.setPreferredSize(new Dimension(700,405));

        panel.add(jScrollPane);
    }

    /**
     * Method which checks if the port number has been
     * updated.
     * @param portNum the text field to read from
     */
    private void checkPort(JTextField portNum) {
        try {
            this.port = Integer.parseInt(portNum.getText());
        }
        //If input if not an integer
        catch (NumberFormatException e) {
            //Default port will be used
            JOptionPane.showMessageDialog(null,"Must be a valid port number.\nCurrent" +
                    " port will be used.");
            portNum.setText(Integer.toString(this.port));
        }
    }
}