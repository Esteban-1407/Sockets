package customer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MyCustomer {
    //Variables de instancia
    private Socket socket;
    private DataInputStream bufferDeEntrada = null;
    private DataOutputStream bufferDeSalida = null;
    Scanner teclado = new Scanner(System.in);
    final String COMANDO_TERMINACION = "salir()"; //Comando para finalizar la conexion

    //Metodo para establecer la conexion con el servidor
    public void establishConnection(String ip, int port) {
        try {
            // Create a socket with the specified IP address and port
            socket = new Socket(ip, port);
            displayText("Connected to: " + socket.getInetAddress().getHostName()); // Display successful connection message
        } catch (Exception e) {
            displayText("Exception while establishing connection: " + e.getMessage()); // Display exception message in case of error
            System.exit(0); // Exit the program
        }
    }

    public static void displayText(String s) {
        System.out.println(s);
    }

    // Method to open input and output streams of the socket
    public void openStreams() {
        try {
            // Create input and output streams of the created socket
            bufferDeEntrada = new DataInputStream(socket.getInputStream());
            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
            bufferDeSalida.flush();
        } catch (IOException e) {
            displayText("Error opening streams");
        }
    }

    public void send(String s) {
        // Method to send message to the server
        try {
            // Write the message to the output buffer and flush the buffer
            bufferDeSalida.writeUTF(s);
            bufferDeSalida.flush();
        } catch (IOException e) {
            displayText("IOException on send");
        }
    }

    // Method to close the connection and input and output streams
    public void closeConnection() {
        try {
            // Close the streams and the socket
            bufferDeEntrada.close();
            bufferDeEntrada.close();
            socket.close();
            displayText("Connection closed");
        } catch (IOException e) {
            displayText("IOException on closeConnection()");
        } finally {
            System.exit(0); // Exit the program
        }
    }

    // Method to execute the connection in a separate thread
    public void executeConnection(String ip, int port) {
        Thread thread = new Thread(() -> {
            try {
                establishConnection(ip, port); // Establish connection with the server
                openStreams(); // Open input and output streams
                receiveData(); // Receive data from the server
            } finally {
                closeConnection(); // Close the connection
            }
        });
        thread.start(); // Start the thread
    }

    // Method to receive messages from the server
    public void receiveData() {
        String message = "";
        try {
            // Loop to receive messages until receiving the termination command
            do {
                message = bufferDeEntrada.readUTF(); // Read the message from the server
                displayText("\n[Server] => " + message); // Display the received message
                System.out.print("\n[You] => "); // Indicate the user the input indicator
            } while (!message.equals(COMANDO_TERMINACION)); // Continue until receiving termination command
        } catch (IOException e) {
        }
    }

    // Method for the user to write and send messages to the server
    public void writeData() {
        String input = "";
        while (true) {
            System.out.print("[You] => "); // Display the input indicator to the user
            input = teclado.nextLine(); // Read user input
            if (input.length() > 0) // Check that the input is not empty
                send(input); // Send to the server
        }
    }

    public static void main(String[] args) {
        MyCustomer client = new MyCustomer(); // Instance of the client class
        Scanner scanner = new Scanner(System.in);
        displayText("Enter IP: [localhost by default] "); // Ask the user for the IP address
        String ip = scanner.nextLine(); // Read the IP address entered by the user
        if (ip.length() <= 0) ip = "localhost"; // Set "localhost" if no IP address is entered

        displayText("Port: [5050 by default] "); // Request the port from the user
        String port = scanner.nextLine(); // Read the port entered by the user
        if (port.length() <= 0) port = "5050"; // Set "5050" as the default port if no port is entered
        client.executeConnection(ip, Integer.parseInt(port)); // Execute the connection with the server
        client.writeData(); // Allow the user to enter messages to the server
    }
}

