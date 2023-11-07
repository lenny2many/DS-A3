package paxos.participants;

import java.util.ArrayList;
import java.util.List;

import paxos.messages.*;
import paxos.network.*;

/**
 * Represents the interface of a Paxos Participant.
 * 
 * A Paxos Participant is a node in the Paxos network that can send and receive messages.
 * This class provides the basic functionality for sending and receiving messages.
 */
public abstract class PaxosParticipant {
    protected NetworkServer server;
    protected List<Node> nodes = new ArrayList<>();
    protected MessageQueue messageQueue = new MessageQueue();
    
    /**
     * Constructor for PaxosParticipant.
     * @param host The host of the participant.
     * @param serverPort The port for receiving messages.
     * @param clientPort The port for sending messages.
     */
    public PaxosParticipant(int serverPort, int clientPort, List<Node> nodes) {
        // Server for receiving messages
        this.server = new NetworkServer(serverPort, this.messageQueue);
        this.server.startServer(); // Start server thread

        // Retain list of nodes that this participant is connected to
        this.nodes = nodes;

        // Start message processing thread
        this.startMessageProcessingThread();
    }

    /**
     * Start a thread to process messages from the message queue.
     */
    private void startMessageProcessingThread() {
        new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // Wait for a message to be added to the queue
                    String messageString = this.messageQueue.consumeMessage();
                    // Parse the message string into a PaxosMessage object
                    PaxosMessage message = PaxosMessage.parseMessageFromString(messageString);
                    // Process the message
                    this.receiveMessage(message);
                }
            } catch (InterruptedException e) {
                // Thread was interrupted during wait
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public abstract void receiveMessage(PaxosMessage message);

    /**
     * Send message to another participant.
     * @param message The PaxosMessage to be sent.
     */
    public void sendMessage(String message, Node node) {
        String host = node.getHost();
        int port = node.getPort();
        NetworkClient.sendMessage(message, host, port);
    }

    // Node class to hold information about each node
    public static class Node {
        private String host;
        private int port;

        public Node(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
    }
}
