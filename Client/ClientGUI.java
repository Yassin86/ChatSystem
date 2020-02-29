import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class that creates a graphical user interface
 * to run the client side of the chat system.
 */
public class ClientGUI extends JFrame {

    int port;
    String address;
    private ChatClient chatClient;
    private JTextArea jTextArea;
    private JTextField inputText;

    /**
     * Constructor for ClientGUI
     * @param port port to be used
     * @param address address to be user
     * @param chatClient the main client class
     */
    public ClientGUI(int port, String address, ChatClient chatClient) {
        this.port = port;
        this.address = address;
        this.chatClient = chatClient;

        /*
        Method reference to run the createWindow method in the event
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
        setTitle("Chat Client GUI");
        setVisible(true);
        setResizable(false);
        this.jTextArea = new JTextArea();

        setSize(new Dimension(800,800));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Panel for establishing connection
        JPanel connectPanel = new JPanel();
        connectPanel.setBorder(new TitledBorder("ChatClient"));

        //Panel for client input area
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(new TitledBorder("Client Input"));

        //Panel for client output area
        JPanel outputPanel = new JPanel();
        outputPanel.setBorder(new TitledBorder("Client Terminal"));

        createStart(connectPanel);
        createInputArea(inputPanel);
        createOutputArea(outputPanel);

        //Add panels to frame using border layouts
        getContentPane().add(connectPanel, BorderLayout.NORTH);
        getContentPane().add(inputPanel, BorderLayout.SOUTH);
        getContentPane().add(outputPanel,BorderLayout.CENTER);

        pack();
    }

    /**
     * Method which adds components to the client connection
     * panel.
     * @param panel the panel to be altered
     */
    private void createStart(JPanel panel) {
        JLabel labelPort = new JLabel();
        labelPort.setText("Port: ");

        //Default text field set to current port
        JTextField portNum = new JTextField(Integer.toString(port));
        portNum.setPreferredSize(new Dimension(100,30));

        JLabel labelAddress = new JLabel();
        labelAddress.setText("Address: ");

        JTextField addressField = new JTextField(address);
        addressField.setPreferredSize(new Dimension(100,30));

        JButton buttonConnect = new JButton("CONNECT");
        //Lambda expression to create action listener for connect button
        buttonConnect.addActionListener(e  -> {
            checkPort(portNum);
            checkAddress(addressField);
            //Connection buttons disappear once connection established
            labelPort.setVisible(false);
            portNum.setVisible(false);
            labelAddress.setVisible(false);
            addressField.setVisible(false);
            buttonConnect.setVisible(false);
            chatClient.startClient(port,address);
        });

        JButton buttonExit = new JButton("QUIT");
        buttonExit.addActionListener(e -> chatClient.quit());

        //Add components to panel
        panel.add(labelPort);
        panel.add(portNum);
        panel.add(labelAddress);
        panel.add(addressField);
        panel.add(buttonConnect);
        panel.add(buttonExit);
    }

    /**
     * Method which adds components to the
     * input area panel.
     * @param panel the panel to be altered
     */
    private void createInputArea(JPanel panel) {
        inputText = new JTextField();
        //Allows message to be sent when user hits Enter key
        inputText.addActionListener(new InputListener());

        //Second way to send messages - via a button
        JButton buttonSend = new JButton("SEND");
        buttonSend.addActionListener(new InputListener());

        JScrollPane jScrollPane = new JScrollPane(inputText);
        jScrollPane.setPreferredSize(new Dimension(610,55));

        buttonSend.setPreferredSize(new Dimension(75,54));

        panel.add(jScrollPane);
        panel.add(buttonSend);
    }

    /**
     * Method which adds components to the
     * output area panel.
     * @param panel the panel to be altered
     */
    private void createOutputArea(JPanel panel) {
        jTextArea.setEditable(false);
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);

        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        jScrollPane.setPreferredSize(new Dimension(700,315));

        panel.add(jScrollPane);
    }

    /**
     * Private class that implements the ActionListener interface
     * to allow components to share an action listener.
     */
    private class InputListener implements ActionListener {
        /**
         * Override of the actionPerformed method in the
         * ActionListener interface
         * @param e action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = inputText.getText();
            //Checks if input is empty
            if (!input.isEmpty()) {
                //Send message to main client class
                chatClient.guiMessage(input);
            }
            else {
                //Error message box
                JOptionPane.showMessageDialog(null,"Can't have an empty input.");
            }
            //Set text field back to empty
            inputText.setText("");
        }
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
        //If input not an integer
        catch (NumberFormatException e) {
            //Error message + uses default port
            JOptionPane.showMessageDialog(null,"Must be a valid port number.\nCurrent" +
                    " port will be used.");
            portNum.setText(Integer.toString(this.port));
        }
    }

    /**
     * Method which checks if the address text field
     * has been updated.
     * @param addressField the text field to read from
     */
    private void checkAddress(JTextField addressField) {
        this.address = addressField.getText();
    }
}