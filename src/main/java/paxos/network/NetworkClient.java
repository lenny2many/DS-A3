package paxos.network;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The NetworkClient class is responsible for sending messages to a server on a specified port.
 */
public class NetworkClient {
    public static void sendMessage(String message, String host, int port) {
        try (Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
            System.out.println("Client sent: " + message);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }
    }
}
