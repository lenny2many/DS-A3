package paxos.network;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.logging.*;

/**
 * The NetworkClient class is responsible for sending messages to a server on a specified port.
 */
public class NetworkClient {

    private static final Logger logger = Logger.getLogger(NetworkClient.class.getName());

    public static void sendMessage(String message, String host, int port) {
        try (Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
            // logger.info("Sent message: " + message);
        } catch (UnknownHostException e) {
            logger.severe("Don't know about host " + host);
            System.exit(1);
        } catch (IOException e) {
            logger.severe("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }
    }
}
