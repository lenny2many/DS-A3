package paxos.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The NetworkServer class is responsible for accepting TCP connections from clients on a specified port.
 * It implements the Runnable interface, allowing it to be run on a separate thread.
 */
public class NetworkServer implements Runnable {
    private int serverPort;
    private ServerSocket serverSocket = null;
    private MessageQueue messageQueue = null;
    private boolean isStopped = false;
    private Thread runningThread = null;

    public NetworkServer(int port, MessageQueue messageQueue) {
        this.serverPort = port;
        this.messageQueue = messageQueue;
    }

    /**
     * The entry point for the server thread. This method is called when the server thread is started.
     * It listens for incoming client connections and handles them using ClientHandler threads.
     */
    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    break;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
            new Thread(new ClientHandler(clientSocket, this.messageQueue)).start();
        }
        System.out.println("Server Stopped.");
    }

    /**
     * Starts the server thread. This method initialises a new thread to run the server.
     * It should only be called once for a given instance of NetworkServer.
     * 
     * @throws IllegalStateException if the server is already running.
     */
    public synchronized void startServer() {
        if (this.runningThread != null) {
            throw new IllegalStateException("Server is already running");
        }
        this.runningThread = new Thread(this);
        this.runningThread.start();
    }

    /**
     * Checks if the server has been stopped.
     *
     * @return true if the server is stopped, false otherwise.
     */
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    /**
     * Stops the server. This method closes the server socket and stops accepting new connections.
     */
    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
    
    /**
     * Opens the server socket to accept connections on the specified port.
     * 
     * @throws RuntimeException if the server socket cannot be opened.
     */
    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + serverPort, e);
        }
    }

    /**
     * The ClientHandler class is a helper class that handles communication with a single client socket.
     * It reads input from the client and prints it to the server's standard output.
     */
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private MessageQueue messageQueue;

        public ClientHandler(Socket socket, MessageQueue messageQueue) {
            this.clientSocket = socket;
            this.messageQueue = messageQueue;
        }

        /**
         * The entry point for the client handler thread. This method is called when the thread is started.
         * Adds messages received from the client to the message queue.
         */
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    try {
                        // Add message to message queue
                        messageQueue.produceMessage(inputLine);
                    } catch (InterruptedException e) {
                        // Thread was interrupted during wait
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (IOException e) {
                System.err.println("Exception caught when trying to listen on port or listening for a connection");
                System.err.println(e.getMessage());
            }
        }
    }
}
