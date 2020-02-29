import java.io.*;
import java.net.*;

/**
 * Class that handles sending messages to
 * the server in a thread by implementing
 * the Runnable interface.
 */
public class ClientWriter implements Runnable {

    private Socket s;
    private ChatClient chatClient;
    private boolean gui;
    PrintWriter serverOut;

    /**
     * Constructor for the ClientWriter thread
     * @param s the client socket
     * @param chatClient the main client class
     * @param gui true or false depending on the users input
     */
    public ClientWriter(Socket s, ChatClient chatClient, boolean gui) {
        this.s = s;
        this.chatClient = chatClient;
        this.gui = gui;
    }

    /**
     * Override of the run method in the
     * Runnable interface
     */
    @Override
    public void run() {
        try {
            //Initialise PrintWriter from the socket's output stream
            serverOut = new PrintWriter(s.getOutputStream(),true);
            //Checks if gui is true and loads corresponding method
            if (gui) {
                guiOut();
            }
            else {
                cliOut();
            }
        }
        catch (IOException e) {
            //Disconnect client if exception thrown
            chatClient.serverDisconnect();
        }
    }

    /**
     * Method which outputs user input to the PrintWriter if
     * the command line interface is being used.
     */
    private void cliOut() {
        try {
            //Get input from terminal
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            String input;
            while ((input = userIn.readLine()) != null ) {
                //Close if user enters "QUIT"
                if (input.equals("QUIT")) {
                    //Exits the program
                    chatClient.quit();
                }
                serverOut.println(input);
            }
            //If input = null, disconnect from server
            chatClient.serverDisconnect();
        }
        catch (IOException e) {
            chatClient.serverDisconnect();
        }
    }

    /**
     * Method which outputs user input to the PrintWriter if
     * the gui is being used.
     */
    private void guiOut() {
        while (true) {
            //Checks if message has been received by the main class
            if (chatClient.isMessageR()) {
                serverOut.println(chatClient.getMessage());
            }
            //Sleep for 50ms
            try {
                Thread.sleep(50);
            }
            catch (InterruptedException ignored) {
                //Continue with the loop if exception thrown
            }
        }
    }
}