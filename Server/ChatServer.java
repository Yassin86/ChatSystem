import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

/**
 * Class that starts the Server and
 * accepts client connections.
 */
public class ChatServer {

    private ServerSocket socket;
    //Stores all active threads
    private List<ServerConnection> list;
    private int noOfClients;
    private ServerGUI serverGUI;
    private boolean gui;

    /**
     * Constructor for the ChatServer class
     * @param port the port number to be used for the ServerSocket
     * @param gui true or false depending on the users input
     */
    public ChatServer(int port, boolean gui) {
        this.gui = gui;
        //If gui is true
        if (gui) {
            //Object of ServerGUI class created
            serverGUI = new ServerGUI(port,this);
        }
        else {
            setupServer(port);
        }
    }

    /**
     * Method which attempts to create a ServerSocket
     * @param port port number to be used
     */
    public void setupServer(int port) {
        try {
            //Attempt to create ServerSocket
            socket = new ServerSocket(port);
            print("Server online. Port: " + port + ".");
            //Concurrent data structure
            list = new CopyOnWriteArrayList<>();
            noOfClients = 0;
        }
        catch (Exception e) {
            //Exit if exception is thrown
            print("Invalid port number.");
            System.exit(0);
        }
        if (gui) {
            //Go method already called if !gui
            go();
        }
    }

    /**
     * Method which accepts clients and starts new threads
     */
    private void go() {
        print("Awaiting connections...");
        print("Number of clients online: "+list.size());
        try {
            while (true) {
                //Accept connection from clients
                Socket s = socket.accept();
                noOfClients++;
                print("Client " + noOfClients + " connected. Port: " + s.getPort() + ".");
                //Create thread for client
                ServerConnection serverConnection = new ServerConnection(s, this, noOfClients);
                new Thread(serverConnection).start();
                //Add this thread to the list of threads
                list.add(serverConnection);
                print("Number of clients online: " + list.size());
            }
        }
        catch (IOException e) {
            //Close server is exception is thrown
            exitServer();
        }
    }

    /**
     * Method which outputs a message to the user depending
     * on whether they are using the GUI or not
     * @param message message to be output
     */
    public synchronized void print(String message) {
        if (gui) {
            //Calls method in serverGUI
            serverGUI.print(message);
        }
        else {
            System.out.println(message);
        }
    }

    /**
     * Method which returns list of threads
     * @return the array list of threads
     */
    public synchronized List<ServerConnection> getList() {
        return list;
    }

    /**
     * Method which removes a thread from array list
     * @param thread thread to be removed
     */
    public synchronized void remove(ServerConnection thread) {
        list.remove(thread);
        print("Number of clients online: " + list.size());
    }

    /**
     * Method which scans the terminal input for the
     * exit command
     */
    private void handleExit() {
        Scanner sc = new Scanner(System.in);
        String input;
        try {
            while ((input = sc.nextLine()) != null) {
                if (input.equals("EXIT")) {
                    //Close server
                    exitServer();
                }
                else {
                    //Error message
                    System.out.println("Invalid command.");
                }
            }
        }
        catch (NoSuchElementException e) {
            //If null input, close server
            exitServer();
        }
    }

    /**
     * Method that cleanly terminates the server
     */
    public synchronized void exitServer() {
        System.out.println("Server shutdown.");
        //Close socket
        try {
            socket.close();
        } catch (Exception ignored) {
            /*
            Exception ignored as program will terminate
            anyway
             */
        }
        //Exit the program
        System.exit(0);
    }

    public static void main(String[] args) {
        //Default port number
        int port = 14001;

        //Checks for port number in the argument
        int x = -1;
        for (String in : args) {
            //Index of args
            x++;
            if (in.equals("-csp")) {
                try {
                    port = Integer.parseInt(args[x + 1]);
                    //Breaks when valid port found
                    break;
                }
                //If input is not an integer
                catch (NumberFormatException e) {
                    System.out.println("Invalid input.\n" +
                            "Default port will be used.");
                }
                //If nothing entered after flag
                catch (ArrayIndexOutOfBoundsException f) {
                    System.out.println("No port number detected.\n" +
                            "Default port will be used.");
                }
            }
        }

        //Scanner to check if user wants to load a GUI
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to load a GUI? \nEnter Y for yes.");
        try {
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.println("You have chosen GUI program.");
                //Load constructor with gui variable = true
                new ChatServer(port, true);
            }
            else {
                System.out.println("You have chosen command line program.");
                //Gui variable set to false
                ChatServer chatServer = new ChatServer(port, false);
                /*
                Using method references to run 'go' and
                'handleExit' in separate threads
                */
                new Thread(chatServer::go).start();
                new Thread(chatServer::handleExit).start();
            }
        }
        //If input = null
        catch (NoSuchElementException e) {
            System.out.println("Input cannot be null.\nClosing program.");
            System.exit(0);
        }
    }
}