package paxos.participants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.*;

import paxos.messages.*;
import paxos.network.*;
import paxos.network.MessageQueue.ClientMessage;

/**
 * Represents the interface of a Paxos Participant.
 * 
 * A Paxos Participant is a node in the Paxos network that can send and receive messages.
 * This class provides the basic functionality for sending and receiving messages.
 */
public abstract class PaxosParticipant {
    protected NetworkServer server;
    protected Node serverNode;
    protected int participantID;
    protected DelayProfile delayProfile;
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
    public PaxosParticipant(Node serverNode, List<Node> nodes) {
        this.participantID = Integer.parseInt(serverNode.getNodeName().substring(1));
    }

    public PaxosParticipant(Node serverNode, List<Node> nodes, DelayProfile delayProfile) {
        this.delayProfile = delayProfile;
        this.participantID = Integer.parseInt(serverNode.getNodeName().substring(1));
    }

    /**
     * Constructor for PaxosParticipant. Mainly used for testing.
     * @param serverPort The port for receiving messages.
     * @param clientPort The port for sending messages.
     * @param nodes The list of nodes that this participant is connected to.
     * @param server The server for receiving messages.
     */
    public PaxosParticipant(int serverPort, List<Node> nodes, NetworkServer server, MessageQueue messageQueue) {
        this.server = server;
        // Message queue for receiving messages
        this.messageQueue = messageQueue;
        // Retain list of nodes that this participant is connected to
        this.nodes = nodes;
    }

    /**
     * Start a thread to process messages from the message queue.
     */
    public void startMessageProcessingThread() {
        messageProcessingThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // Wait for a message to be added to the queue
                    ClientMessage clientMessage = messageQueue.consumeMessage();
                    String message = clientMessage.getMessage();
                    // Socket clientSocket = clientMessage.getClientSocket();
                    // Parse the message
                    Optional<PaxosMessage> messageOpt = PaxosMessage.parseMessageFromString(message);
                    if (messageOpt.isPresent()) {
                        PaxosMessage paxosMessage = messageOpt.get();
                        // Process the message
                        this.receiveMessage(paxosMessage, paxosMessage.getParticipantID());
                    } else {
                        logger.warning("Failed to parse message: " + message);
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

    public void stop() {
        this.stopMessageProcessingThread();
        this.server.stop();
    }

    public abstract void receiveMessage(PaxosMessage message, String participantID);

    /**
     * Send message to another participant.
     * @param message The PaxosMessage to be sent.
     */
    public void sendMessage(PaxosMessage message, String host, int port) {
        // Simulate delay
        this.simulateDelay();
        NetworkClient.sendMessage(message.toString(), host, port);
    }

    public void simulateDelay() {
        switch (this.delayProfile) {
            case SMALL_DELAY:
                try {
                    logger.info("NODE " + serverNode.getNodeName() + ": " + "Simulating small delay.");
                    Thread.sleep(100); // 100 ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                break;
            case LARGE_DELAY:
                try {
                    logger.info("NODE " + serverNode.getNodeName() + ": " + "Simulating large delay.");
                    Thread.sleep(1000); // 1 s
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                break;
            case NO_RESPONSE:
                try {
                    logger.info("NODE " + serverNode.getNodeName() + ": " + "Simulating no response.");
                    Thread.sleep(100000000); // 100000000 ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                break;
            case IMMEDIATE_RESPONSE:
            default:
                // No delay
                break;
        }
    }

    // Use composite proposal number to ensure that the proposer with the highest proposal number is chosen
    protected boolean compareProposalNumbers(int proposalNumber1, int proposalNumber2, int ID1, int ID2) {
        // Compare the proposal numbers
        if (proposalNumber1 > proposalNumber2) {
            return true;
        } else if (proposalNumber1 == proposalNumber2) {
            // If the proposal numbers are equal, compare the proposer IDs
            return ID1 > ID2;
        } else {
            return false;
        }
    }

    public void setConnectedNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public String getServerNodeID() {
        return this.serverNode.getNodeName();
    }

    public Node findNodeByID(String nodeName) {
        for (Node node : this.nodes) {
            if (node.getNodeName().equals(nodeName)) {
                return node;
            }
        }

        return null;
    }

    // Node class to hold information about each node
    public static class Node {
        private String nodeName;
        private String host;
        private int acceptorPort;
        private int proposerPort;

        public Node(String nodeName, String host, int acceptorPort, int proposerPort) {
            this.nodeName = nodeName;
            this.host = host;
            this.acceptorPort = acceptorPort;
            this.proposerPort = proposerPort;
        }

        public String getHost() {
            return host;
        }

        public int getAcceptorPort() {
            return acceptorPort;
        }

        public int getProposerPort() {
            return proposerPort;
        }

        public String getNodeName() {
            return nodeName;
        }
    }

    public enum DelayProfile {
        IMMEDIATE_RESPONSE,
        SMALL_DELAY,
        LARGE_DELAY,
        NO_RESPONSE
    } 
}
