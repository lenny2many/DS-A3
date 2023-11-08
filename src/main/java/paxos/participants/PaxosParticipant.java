package paxos.participants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.*;

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
    protected MessageQueue messageQueue;
    private Thread messageProcessingThread;
    
    private static final Logger logger = Logger.getLogger(PaxosParticipant.class.getName());

    /**
     * Constructor for PaxosParticipant.
     * @param serverPort The port for receiving messages.
     * @param clientPort The port for sending messages.
     * @param nodes The list of nodes that this participant is connected to.
     */
    public PaxosParticipant(int serverPort, int clientPort, List<Node> nodes) {
        // Message queue for receiving messages
        this.messageQueue = new MessageQueue();
        // Server for receiving messages
        this.server = new NetworkServer(serverPort, this.messageQueue);
        // Retain list of nodes that this participant is connected to
        this.nodes = nodes;
        // start server and message processing thread
        this.start();
    }

    /**
     * Constructor for PaxosParticipant. Mainly used for testing.
     * @param serverPort The port for receiving messages.
     * @param clientPort The port for sending messages.
     * @param nodes The list of nodes that this participant is connected to.
     * @param server The server for receiving messages.
     */
    public PaxosParticipant(int serverPort, int clientPort, List<Node> nodes, NetworkServer server, MessageQueue messageQueue) {
        this.server = server;
        // Message queue for receiving messages
        this.messageQueue = messageQueue;
        // Retain list of nodes that this participant is connected to
        this.nodes = nodes;
    }

    public void start() {
        // Start message processing thread
        this.server.startServer();
        // Start server thread
        this.startMessageProcessingThread();
    }

    /**
     * Start a thread to process messages from the message queue.
     */
    public void startMessageProcessingThread() {
        messageProcessingThread =    new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // Wait for a message to be added to the queue
                    String messageString = this.messageQueue.consumeMessage();
                    // Parse the message
                    Optional<PaxosMessage> messageOpt = PaxosMessage.parseMessageFromString(messageString);
                    if (messageOpt.isPresent()) {
                        PaxosMessage paxosMessage = messageOpt.get();
                        // Process the message
                        this.receiveMessage(paxosMessage);
                    } else {
                        logger.warning("Failed to parse message: " + messageString);
                    }
                }
            } catch (InterruptedException e) {
                this.stopMessageProcessingThread();
            }
        });

        messageProcessingThread.start();
    }

    public void stopMessageProcessingThread() {
        if (messageProcessingThread != null) {
            messageProcessingThread.interrupt(); // Interrupt the thread

            try {
                messageProcessingThread.join(1000); // Wait for the thread to finish execution
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Set the interrupt flag again if the current thread is interrupted
                // Handle the interruption, for example by logging it or by throwing a runtime exception if this is unexpected
            }
        }
    }

    public abstract void receiveMessage(PaxosMessage message);

    /**
     * Send message to another participant.
     * @param message The PaxosMessage to be sent.
     */
    public void sendMessage(PaxosMessage message, Node node) {
        String host = node.getHost();
        int port = node.getPort();
        NetworkClient.sendMessage(message.toString(), host, port);
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
