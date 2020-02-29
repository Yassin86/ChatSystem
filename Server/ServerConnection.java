import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Class that handles client connections and messages
 * in a thread by implementing the Runnable interface.
 */
public class ServerConnection implements Runnable {

    private Socket clientSocket;
    private ChatServer server;
    private PrintWriter clientOut;
    private List<ServerConnection> threadList;
    private int noOfClients;

    /**
     * Constructor for the ServerThread
     * @param clientSocket socket of the client connected
     * @param server main server class
     * @param counter number of clients connected
     */
    public ServerConnection(Socket clientSocket, ChatServer server, int counter) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.noOfClients = counter;
        threadList = server.getList();
    }

    /**
     * Override of the run method in the
     * Runnable interface.
     */
    @Override
    public void run() {
        try {
            //Get output stream from socket
            OutputStream outStream = clientSocket.getOutputStream();
            clientOut = new PrintWriter(outStream, true);
            //Get input stream from socket
            BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String userInput;
            while ((userInput = clientIn.readLine()) != null) {
                //Output message to server
                server.print("Client " + noOfClients + ": " + userInput);
                //Send to all clients
                sendToAll(userInput);
            }
            //If userInput = null, close the socket
            tryDisconnect();
        }
        catch (IOException e) {
            //Exception thrown when client disconnects
            tryDisconnect();
        }
    }

    /**
     * Method which outputs a string to all connected
     * clients.
     * @param userInput message to be sent
     */
    private void sendToAll(String userInput) {
        //Loop through all connected clients
        for (ServerConnection sC : threadList) {
            //If client sends message to itself
            if (sC == this) {
                sC.outputMessage("You: " + userInput);
            }
            else {
                sC.outputMessage("Client " + noOfClients + ": " + userInput);
            }
        }
    }

    /**
     * Method which prints a string to the PrintWriter
     * @param userInput message to be sent
     */
    private void outputMessage(String userInput) {
        clientOut.println(userInput);
    }

    /**
     * Method which disconnects a client by closing the socket
     */
    private void tryDisconnect() {
        try {
            clientSocket.close();
        }
        catch (IOException ignored) {
            /*
            Exception ignored as client will
            be removed from the list regardless.
             */
        }
        server.print("Client " + noOfClients + " disconnected.");
        //Remove client from list of connected clients
        server.remove(this);
        sendToAll("DISCONNECTED");
    }
}