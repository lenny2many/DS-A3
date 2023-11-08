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
    public PaxosAcceptor(int serverPort, int clientPort, List<Node> nodes) {
        super(serverPort, clientPort, nodes);
    }

    /**
     * Constructor for PaxosAcceptor. Mainly used for testing.
     * @param serverPort The port for receiving messages.
     * @param clientPort The port for sending messages.
     * @param nodes The list of nodes that this acceptor is connected to.
     * @param server The server for receiving messages.
     */
    public PaxosAcceptor(int serverPort, int clientPort, List<Node> nodes, NetworkServer server, MessageQueue messageQueue) {
        super(serverPort, clientPort, nodes, server, messageQueue);
    }
    
    /**
     * Processes a prepare request from a proposer. If the proposal number is higher than any seen before,
     * it promises not to accept any lower-numbered proposals.
     *
     * @param prepareMessage The prepare message received from a proposer.
     */
    public void onPrepareRequest(PaxosMessage prepareMessage) {
        logger.info("Received prepare request with proposal number: " + prepareMessage.getProposalNumber());
        if (prepareMessage.getProposalNumber() > highestPrepareNumber) {
            highestPrepareNumber = prepareMessage.getProposalNumber();

            // Send a promise to not accept any lower-numbered proposals
            PaxosMessage promise = new PaxosMessage(PaxosMessage.MessageType.PROMISE, prepareMessage.getProposalNumber(), this.acceptedProposalNumber, this.acceptedValue);
            sendMessage(promise, new Node(prepareMessage.getSenderHost(), prepareMessage.getSenderPort()));
        }
    }
    

    /**
     * Processes an accept request from a proposer. If the accept request contains a proposal number
     * greater than or equal to the highest promised number, it accepts the proposal.
     *
     * @param acceptMessage The accept message received from a proposer.
     */
    public void onAcceptRequest(PaxosMessage acceptMessage) {
        logger.info("Received accept request with proposal number: " + acceptMessage.getProposalNumber() + " and value: " + acceptMessage.getValue());
        if (acceptMessage.getProposalNumber() >= highestPrepareNumber) {
            acceptedProposalNumber = acceptMessage.getProposalNumber();
            acceptedValue = acceptMessage.getValue();

            // Send an accepted message to indicate the proposal has been accepted
            PaxosMessage accepted = new PaxosMessage(PaxosMessage.MessageType.ACCEPTED, acceptMessage.getProposalNumber(), acceptedValue);
            // Broadcast the accepted message to all learners (or to the proposer, who will then inform the learners)
            // This is simplified; in an actual implementation, you might send it to a specific set of nodes.
            for (Node node : this.nodes) {
                sendMessage(accepted, node);
            }
        }
    }

    @Override
    public void receiveMessage(PaxosMessage message) {
        // Handle received Paxos messages
        switch (message.getType()) {
            case PREPARE:
                onPrepareRequest(message);
                break;
            case ACCEPT:
                onAcceptRequest(message);
                break;
            default:
                logger.warning("Received unsupported message type: " + message.getType());
        }
    }
    
}
