package paxos.participants;

import paxos.messages.*;
import paxos.network.MessageQueue;
import paxos.network.NetworkServer;

import java.util.List;
import java.util.logging.*;



/**
 * Represents the Paxos Acceptor.
 * Acceptors are the "judges" of the Paxos algorithm. They receive proposals from proposers,
 * decide whether to accept them, and relay their decisions back to the proposers and learners.
 */
public class PaxosAcceptor extends PaxosParticipant {
    private int highestPrepareNumber = -1;
    private int acceptedProposalNumber = -1;
    private String acceptedValue = null;

    private static final Logger logger = Logger.getLogger(PaxosAcceptor.class.getName());

    /**
     * Constructor for PaxosAcceptor.
     * @param serverPort The port for receiving messages.
     * @param clientPort The port for sending messages.
     * @param nodes The list of nodes that this acceptor is connected to.
     */
    public PaxosAcceptor(Node serverNode, List<Node> nodes) {
        super(serverNode, nodes);
        // Server for receiving messages
        this.serverNode = serverNode;
        // Message queue for receiving messages
        this.messageQueue = new MessageQueue();
        // Server for receiving messages
        this.server = new NetworkServer(serverNode.getAcceptorPort(), this.messageQueue);
        // Retain list of nodes that this participant is connected to
        this.nodes = nodes;
    }

    public PaxosAcceptor(Node serverNode, List<Node> nodes, DelayProfile delayProfile) {
        super(serverNode, nodes, delayProfile);
        // Server for receiving messages
        this.serverNode = serverNode;
        // Message queue for receiving messages
        this.messageQueue = new MessageQueue();
        // Server for receiving messages
        this.server = new NetworkServer(serverNode.getAcceptorPort(), this.messageQueue);
        // Retain list of nodes that this participant is connected to
        this.nodes = nodes;
    }

    /**
     * Constructor for PaxosAcceptor. Mainly used for testing.
     * @param serverPort The port for receiving messages.
     * @param clientPort The port for sending messages.
     * @param nodes The list of nodes that this acceptor is connected to.
     * @param server The server for receiving messages.
     */
    public PaxosAcceptor(int serverPort, List<Node> nodes, NetworkServer server, MessageQueue messageQueue) {
        super(serverPort, nodes, server, messageQueue);
    }

    public void start() {
        // Start message processing thread
        this.server.startServer();
        // Start server thread
        this.startMessageProcessingThread();
    }
    
    /**
     * Processes a prepare request from a proposer. If the proposal number is higher than any seen before,
     * it promises not to accept any lower-numbered proposals.
     *
     * @param prepareMessage The prepare message received from a proposer.
     */
    public void onPrepareRequest(PaxosMessage prepareMessage, String participantID) {
        Node sender = this.findNodeByID(participantID);
        logger.info("NODE " + serverNode.getNodeName() + ": " + "Received prepare request with proposal number: " + prepareMessage.getProposalNumber() + " from proposer " + participantID);
        if (prepareMessage.getProposalNumber() > highestPrepareNumber) {
            highestPrepareNumber = prepareMessage.getProposalNumber();

            // Send a promise to not accept any lower-numbered proposals
            PaxosMessage promise = PaxosMessage.promiseMessage(prepareMessage.getProposalNumber(), acceptedValue, acceptedProposalNumber, this.getServerNodeID());
            sendMessage(promise, sender.getHost(), sender.getProposerPort());
            logger.info("NODE " + serverNode.getNodeName() + ": " + "Sent promise with proposal number: " + prepareMessage.getProposalNumber() + " to proposer " + participantID);
        }
    }
    

    /**
     * Processes an accept request from a proposer. If the accept request contains a proposal number
     * greater than or equal to the highest promised number, it accepts the proposal.
     *
     * @param acceptMessage The accept message received from a proposer.
     */
    public void onAcceptRequest(PaxosMessage acceptMessage, String participantID) {
        Node sender = this.findNodeByID(participantID);
        logger.info("NODE " + serverNode.getNodeName() + ": " + "Received accept request with proposal number: " + acceptMessage.getProposalNumber() + " and value: " + acceptMessage.getValue() + " from proposer " + participantID);
        if (acceptMessage.getProposalNumber() >= highestPrepareNumber) {
            acceptedProposalNumber = acceptMessage.getProposalNumber();
            acceptedValue = acceptMessage.getValue();
            
            // Send an accepted message to indicate the proposal has been accepted
            PaxosMessage accepted = PaxosMessage.acceptedMessage(acceptMessage.getProposalNumber(), acceptMessage.getValue(), participantID);
            // Broadcast the accepted message to all learners (or to the proposer, who will then inform the learners)
            sendMessage(accepted, sender.getHost(), sender.getProposerPort());
            logger.info("NODE " + serverNode.getNodeName() + ": " + "Sent accepted message with proposal number: " + acceptMessage.getProposalNumber() + " and value: " + acceptMessage.getValue() + " to proposer " + participantID);

            // This is simplified; in an actual implementation, you might send it to a specific set of nodes.
            // for (Node node : this.nodes) {
            //     sendMessage(accepted, sender.getHost(), sender.getAcceptorPort());
            // }
        }
    }

    @Override
    public void receiveMessage(PaxosMessage message, String participantID) {
        // Handle received Paxos messages
        switch (message.getType()) {
            case PREPARE:
                onPrepareRequest(message, participantID);
                break;
            case ACCEPT:
                onAcceptRequest(message, participantID);
                break;
            default:
                logger.warning("NODE " + serverNode.getNodeName() + ": " + "Received unsupported message type: " + message.getType());
        }
    }
    
    public String getAcceptedValue() {
        return acceptedValue;
    }
}
