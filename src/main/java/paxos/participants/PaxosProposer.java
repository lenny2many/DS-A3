package paxos.participants;

import paxos.messages.PaxosMessage;
import paxos.network.MessageQueue;
import paxos.network.NetworkServer;

import java.util.List;
import java.util.logging.*;

/**
 * Represents the Paxos Proposer.
 * The proposer is responsible for initiating the consensus process in the Paxos protocol.
 * It sends out propose requests to acceptors and responds to replies from them.
 */
public class PaxosProposer extends PaxosParticipant {
    // Track the number of promises received for a given proposal
    private int promisesReceived;
    // Track the number of acceptances received for a given proposal
    private int acceptancesReceived;
    // Track if prepare phase has reached a quorum
    private boolean acceptPhaseInitiated = false;
    // Track if accept phase has reached a quorum
    private boolean commitPhaseInitiated = false;
    // Track the highest proposal number that this proposer has seen
    private int highestProposalNumberSeen;
    // Proposal value to be accepted (may be updated based on promises received)
    private String proposedValue;
    private int lastProposalNumberUsed = 0;

    private static final Logger logger = Logger.getLogger(PaxosProposer.class.getName());

    /**
     * Constructor for PaxosProposer.
     * @param serverPort The port for receiving messages.
     * @param clientPort The port for sending messages.
     * @param nodes The list of nodes that this proposer is connected to.
     */
    public PaxosProposer(Node serverNode, List<Node> nodes) {
        super(serverNode, nodes);
        // Server for receiving messages
        this.serverNode = serverNode;
        // Message queue for receiving messages
        this.messageQueue = new MessageQueue();
        // Server for receiving messages
        this.server = new NetworkServer(serverNode.getProposerPort(), this.messageQueue);
        // Retain list of nodes that this participant is connected to
        this.nodes = nodes;
    }

    public PaxosProposer(Node serverNode, List<Node> nodes, DelayProfile delayProfile) {
        super(serverNode, nodes, delayProfile);
        // Server for receiving messages
        this.serverNode = serverNode;
        // Message queue for receiving messages
        this.messageQueue = new MessageQueue();
        // Server for receiving messages
        this.server = new NetworkServer(serverNode.getProposerPort(), this.messageQueue);
        // Retain list of nodes that this participant is connected to
        this.nodes = nodes;
    }

    public void startProposal(String proposedValue) {
        // Reset the number of promises received
        this.promisesReceived = 0;
        // Reset the number of acceptances received
        this.acceptancesReceived = 0;
        // Reset the proposed value
        this.proposedValue = proposedValue;
        // Increment the proposal number
        lastProposalNumberUsed++;
        // Reset the highest proposal number seen
        this.highestProposalNumberSeen = lastProposalNumberUsed;
        // Reset the acceptPhaseInitiated flag
        this.acceptPhaseInitiated = false;
        // Reset the commitPhaseInitiated flag
        this.commitPhaseInitiated = false;
        // Send prepare requests to all acceptors
        sendPrepareRequests(lastProposalNumberUsed);
    }

    public void start() {
        // Start message processing thread
        this.server.startServer();
        // Start server thread
        this.startMessageProcessingThread();
    }

    /**
     * Sends a prepare request to all acceptors.
     * The prepare request is the first phase of the Paxos protocol where the proposer
     * solicits acceptors to agree to consider a particular proposal identified by a unique generation number.
     */
    public void sendPrepareRequests(int proposalNumber) {
        // Create a prepare message
        PaxosMessage prepare = PaxosMessage.prepareMessage(proposalNumber, this.getServerNodeID());

        // Send prepare request to all acceptors
        for (Node node : nodes) {
            logger.info("NODE " + serverNode.getNodeName() + ": " + "Sending prepare request with proposal number " + proposalNumber + " to acceptor " + node.getNodeName());
            this.sendMessage(prepare, node.getHost(), node.getAcceptorPort());
        }
    }
    
    /**
     * Handles responses to the prepare requests from acceptors.
     * This method processes the promises from acceptors to not accept any more proposals numbered less than the one sent.
     * @param promise The promise message from an acceptor.
     */
    public void onPrepareResponse(PaxosMessage promise, String participantID) {
        Node sender = this.findNodeByID(participantID);
        // Check if the proposal number is greater than the highest proposal number seen so far
        if (promise.getProposalNumber() == lastProposalNumberUsed && !acceptPhaseInitiated && !commitPhaseInitiated) {
            // Update the number of promises received
            this.promisesReceived++;

            logger.info("NODE " + serverNode.getNodeName() + ": " + "Received promise from acceptor " + sender.getNodeName() + " for proposal number " + promise.getProposalNumber());

            // Check if a promise contains a value (which means the acceptor has already accepted a proposal)
            if (!promise.getValue().isEmpty()) {
                // Set the proposed value to the highest-numbered proposal's value
                if (promise.getProposalNumber() > this.highestProposalNumberSeen) {
                    logger.info("NODE " + serverNode.getNodeName() + ": " + "Received promise with value: " + promise.getValue() + " from acceptor " + sender.getNodeName() + " for proposal number " + promise.getProposalNumber());
                    this.proposedValue = promise.getValue();
                    this.highestProposalNumberSeen = promise.getProposalNumber();
                }
            }

            // Check if the number of promises received has reached a quorum
            if (hasReachedQuorum(promisesReceived)) {
                logger.info("NODE " + serverNode.getNodeName() + ": " + "Received a quorum of promises for proposal number " + promise.getProposalNumber() + ". Entering accept phase.");
                // Set the accept phase initiated flag
                this.acceptPhaseInitiated = true;
                // Proceed to the accept phase with the proposed value
                sendAcceptRequests(this.highestProposalNumberSeen, this.proposedValue);
            }
        } else {
            // Handle the case where the promise received is for an outdated proposal number
            logger.info("NODE " + serverNode.getNodeName() + ": " + "Received outdated promise from acceptor " + sender.getNodeName() + " for proposal number " + promise.getProposalNumber());
        }
    }
    
    /**
     * Helper method which checks if the count has reached a quorum.
     * @param count The count to check.
     * @return True if the count has reached a quorum, false otherwise.
     */
    private boolean hasReachedQuorum(int count) {
        // Determine if the count reaches a quorum. This could be a simple majority, for example.
        return count > (this.nodes.size() / 2);
    }

    /**
     * Sends an accept request to all acceptors.
     * After receiving promises from a majority of acceptors, the proposer sends an accept request to each of them
     * for a proposal with a number no less than the one specified in the promises, and with a value of its choice.
     */
    private void sendAcceptRequests(int proposalNumber, String value) {
        // Log the action of sending accept requests
        logger.info("NODE " + serverNode.getNodeName() + ": " + "Sending accept requests to all acceptors for proposal number " + proposalNumber + " with value: " + value);

        // Create the accept message
        PaxosMessage acceptMessage = PaxosMessage.acceptRequestMessage(proposalNumber, value, this.getServerNodeID());

        // Send the accept message to all nodes (acceptors)
        for (Node node : this.nodes) {
            sendMessage(acceptMessage, node.getHost(), node.getAcceptorPort());
        }
    }
    
    /**
     * Handles responses to the accept requests from acceptors.
     * This method processes the acceptances of its proposal by the acceptors.
     * @param acceptance The acceptance message from an acceptor.
     */
    public void onAcceptResponse(PaxosMessage acceptedMessage, String participantID) {
        Node sender = this.findNodeByID(participantID);
        // Check if the accepted message corresponds to the current proposal number
        if (acceptedMessage.getProposalNumber() == this.highestProposalNumberSeen && !commitPhaseInitiated && acceptPhaseInitiated) {
            // Increment the count of acceptances received
            int acceptances = this.acceptancesReceived++;

            // Log the receipt of the acceptance
            logger.info("NODE " + serverNode.getNodeName() + ": " + "Received acceptance from acceptor " + sender.getNodeName() + " for proposal number " + this.highestProposalNumberSeen);

            // If the acceptances have reached a quorum, the proposal is chosen
            if (hasReachedQuorum(acceptances)) {
                logger.info("NODE " + serverNode.getNodeName() + ": " + "Received a quorum of acceptances for proposal number " + this.highestProposalNumberSeen + ". Proposal chosen.");
                // Set the commit phase initiated flag
                this.commitPhaseInitiated = true;
                // The value is now chosen; notify all nodes and perform any additional logic required
                onProposalChosen(acceptedMessage.getValue());
            }
        } else {
            // Handle the case where the accept response is for an outdated proposal number
            logger.info("NODE " + serverNode.getNodeName() + ": " + "Received outdated acceptance from acceptor " + sender.getNodeName() + " for proposal number " + acceptedMessage.getProposalNumber());
        }
    }

    // This method would be called when the proposal has been chosen.
    // You would implement logic here that should occur once a proposal has been accepted by a quorum.
    private void onProposalChosen(String value) {
        // Log the success of the proposal
        logger.info("NODE " + serverNode.getNodeName() + ": " + "Proposal with number " + this.highestProposalNumberSeen + " and value " + value + " has been chosen.");

        // Implement logic to be performed once the value has been chosen
        // This could involve sending a message to learners or updating some state within the system
    }

    @Override
    public void receiveMessage(PaxosMessage message, String participantID) {
        // Handle received Paxos messages
        switch (message.getType()) {
            case PROMISE:
                onPrepareResponse(message, participantID);
                break;
            case ACCEPTED:
                onAcceptResponse(message, participantID);
                break;
            default:
                logger.warning("NODE " + serverNode.getNodeName() + ": " + "Received unsupported message type: " + message.getType());
        }
    }
}