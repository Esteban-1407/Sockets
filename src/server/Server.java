package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    // Instance variables
    private Socket socket;
    private ServerSocket serverSocket;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    Scanner scanner = new Scanner(System.in);
    final String TERMINATION_COMMAND = "exit()"; // Command to terminate the connection

    // Method to establish server connection on a specific port
    public void establishConnection(int port) {
        try {
            serverSocket = new ServerSocket(port); // Create a ServerSocket on the specified port
            displayText("Waiting for incoming connection on port " + String.valueOf(port) + "..."); // Display connection waiting message
            socket = serverSocket.accept();
            displayText("Connection established with: " + socket.getInetAddress().getHostName() + "\n\n\n"); // Display connection established message
        } catch (Exception e) {
            displayText("Error in establishConnection(): " + e.getMessage()); // Display error message in case of failure
            System.exit(0); // Exit the program
        }
    }

    // Method to open input and output streams of the socket
    public void openStreams() {
        try {
            inputStream = new DataInputStream(socket.getInputStream()); // Create input stream associated with the socket
            outputStream = new DataOutputStream(socket.getOutputStream()); // Create output stream associated with the socket
            outputStream.flush(); // Clear the output buffer
            // Buffer: Temporary memory region used to temporarily store data while transferring from one place to another
        } catch (IOException e) {
            displayText("Error opening streams");
        }
    }

    // Method to receive messages from the client
    public void receiveData() {
        String message = "";
        try {
            // Loop to receive messages until receiving the termination command
            do {
                message = inputStream.readUTF(); // Read client message
                displayText("\n[Client] => " + message); // Display received message
                System.out.print("\n[You] => "); // Display user input indicator
            } while (!message.equals(TERMINATION_COMMAND)); // Continue until receiving termination command
        } catch (IOException e) {
            closeConnection();
        }
    }

    // Method to send a message to the client
    public void send(String s) {
        try {
            outputStream.writeUTF(s); // Write the message to the output buffer
            outputStream.flush(); // Clear the buffer
        } catch (IOException e) {
            displayText("Error in send(): " + e.getMessage());
        }
    }

    // Method to display text on console
    public static void displayText(String s) {
        System.out.print(s);
    }

    // Method for the server to write and send messages to the client
    public void writeData() {
        while (true) {
            System.out.print("[You] => "); // Display user input indicator
            send(scanner.nextLine()); // Send message to the client
        }
    }

    // Method to close the connection and input and output streams
    public void closeConnection() {
        try {
            // Close the streams and the socket
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            displayText("Exception in closeConnection(): " + e.getMessage());
        } finally {
            displayText("Conversation ended....");
            System.exit(0);
        }
    }

    // Method to execute server connection in a separate thread
    public void executeConnection(int port) {
        Thread thread = new Thread(() -> {
            // Designing this infinite loop to keep the server waiting for new incoming client connections
            while (true) {
                try {
                    establishConnection(port); // Establish server connection on the specified port
                    openStreams(); // Open input and output streams
                    receiveData(); // Receive data from the client
                } finally {
                    closeConnection(); // Close the connection
                }
            }
        });
        thread.start(); // Start the thread
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(); // Instance of the Server class
        Scanner scanner = new Scanner(System.in);

        displayText("Enter the port [5050 by default]: "); // Request port from user
        String port = scanner.nextLine();
        if (port.length() <= 0) port = "5050"; // Default port
        server.executeConnection(Integer.parseInt(port)); // Server connection on the specified port
        server.writeData(); // Allow the server to send messages to the client
    }
}
