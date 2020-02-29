import java.net.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Class that connects to a server given an address
 * and a port - client side of the chat system.
 */
public class ChatClient {

    private Socket s;
    private boolean gui;
    private ClientGUI clientGUI;
    private boolean messageR;
    private String message;

    /**
     * Constructor for ChatClient
     * @param port port of socket
     * @param address address of socket
     * @param gui true or false depending on the users input
     */
    public ChatClient(int port, String address, boolean gui) {
        this.gui = gui;
        this.messageR = false;
        this.message = "";
        if (gui) {
            //Object of ClientGUI class created
            clientGUI = new ClientGUI(port,address,this);
        }
        else {
            startClient(port,address);
        }
    }

    /**
     * Method which attempts to connect to the server using
     * a socket
     * @param port server port
     * @param address server address
     */
    public void startClient(int port, String address) {
        try {
            s = new Socket(address,port);
            print("Connection successful. Address: "+address+". Port: "+port);
        }
        catch (Exception e) {
            //Exit program if unable to connect to socket
            System.out.println("Unable to establish connection to server.");
            System.exit(0);
        }
        if (gui) {
            go();
        }
    }

    /**
     * Method which starts the listener and writer classes
     * in separate threads
     */
    private void go() {
        new Thread(new ClientListener(s, this)).start();
        new Thread(new ClientWriter(s, this, gui)).start();
    }

    /**
     * Get method which returns state of
     * messageR
     * @return boolean messageR
     */
    public synchronized boolean isMessageR() {
        return this.messageR;
    }

    /**
     * Method that is called when the user
     * enters a message on the GUI
     * @param input the input from the user
     */
    public void guiMessage(String input) {
        //Message read variable set to true
        this.messageR = true;
        message = input;
    }

    /**
     * Get method for message variable
     * @return String message
     */
    public String getMessage() {
        this.messageR = false;
        return message;
    }

    /**
     * Method which outputs a message to the user depending
     * on whether they are using the GUI or not
     * @param message message to be printed
     */
    public synchronized void print(String message) {
        if (gui) {
            //Calls print method in GUI class
            clientGUI.print(message);
        }
        else {
            System.out.println(message);
        }
    }

    /**
     * Method which calls quit method if the server
     * has disconnected
     */
    public synchronized void serverDisconnect() {
        //Output error message
        System.out.println("\nSERVER DISCONNECTED");
        quit();
    }

    /**
     * Method which quits the program cleanly
     */
    public synchronized void quit() {
        System.out.println("CLOSED");
        //Close the socket
        try {
            s.close();
        }
        catch (Exception ignored) {
            /*
            Exception ignored as program will be
            closed regardless
             */
        }
        //Exit the program
        System.exit(0);
    }

    public static void main(String[] args) {
        //Default port number and address
        int port = 14001;
        String address = "localhost";

        //Checks for port number and/or address in the argument
        int x = -1;
        //Boolean variables for port and address
        boolean ccp = false;
        boolean cca = false;
        for (String in : args) {
            //Index of args
            x++;
            if (in.equals("-ccp") && !ccp) {
                try {
                    port = Integer.parseInt(args[x + 1]);
                    /*
                    Boolean set to true so command is not looked
                    for again
                     */
                    ccp = true;
                }
                catch (NumberFormatException e) {
                    System.out.println("Invalid input.\n" +
                            "Default port will be used.");
                }
                catch (ArrayIndexOutOfBoundsException f) {
                    System.out.println("No port number detected.\n" +
                            "Default port will be used.");
                }
            }
            else if (in.equals("-cca") && !cca) {
                try {
                    address = args[x + 1];
                    cca = true;
                }
                catch (ArrayIndexOutOfBoundsException g) {
                    System.out.println("No address detected.\n" +
                            "Default address will be used.");
                }
            }
        }

        //Scanner to check if user wants to load a GUI
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to load a GUI? \nEnter Y for Yes.");
        try {
            if (scanner.nextLine().equalsIgnoreCase("Y")) {
                System.out.println("You have chosen GUI program.");
                //Load constructor with gui variable set to true
                new ChatClient(port, address, true);
            }
            else {
                System.out.println("You have chosen command line program.");
                //Starts the go method directly with gui variable = false
                new ChatClient(port, address, false).go();
            }
        }
        //If input = null
        catch (NoSuchElementException e) {
            System.out.println("Input cannot be null.\nClosing program.");
            System.exit(0);
        }
    }
}