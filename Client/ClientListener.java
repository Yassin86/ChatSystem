import java.io.*;
import java.net.*;

/**
 * Class that handles clients listening for
 * messages from the server and outputting them
 * to the terminal in a thread by implementing
 * the Runnable interface.
 */
public class ClientListener implements Runnable {

    private Socket s;
    private ChatClient chatClient;

    /**
     * Constructor for ClientListener
     * @param s the client socket
     * @param chatClient the main client class
     */
    public ClientListener(Socket s, ChatClient chatClient) {
        this.s = s;
        this.chatClient = chatClient;
    }

    /**
     * Override of the run method in the
     * Runnable interface
     */
    @Override
    public void run() {
        try {
            //Get message from server
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String input;
            while ((input = serverIn.readLine()) != null) {
                //Output message to terminal
                chatClient.print(input);
            }
            //If input = null, disconnect from server
            chatClient.serverDisconnect();
        }
        catch (IOException e) {
            //Exception thrown when server disconnects
            chatClient.serverDisconnect();
        }
    }
}