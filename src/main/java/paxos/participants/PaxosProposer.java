package paxos.participants;

import paxos.messages.PaxosMessage;

import java.util.List;
import java.util.logging.*;

/**
 * Represents the Paxos Proposer.
 * The proposer is responsible for initiating the consensus process in the Paxos protocol.
 * It sends out propose requests to acceptors and responds to replies from them.
 */
public class PaxosProposer extends PaxosParticipant {
    public int id;
    // Track the number of promises received for a given proposal
    private int promisesReceived;
    // Track the highest proposal number that this proposer has seen
    private int highestProposalNumberSeen;
    // Proposal value to be accepted (may be updated based on promises received)
    private String proposedValue;

    private static final Logger logger = Logger.getLogger(PaxosProposer.class.getName());

    /**
     * Constructor for PaxosProposer.
     * @param serverPort The port for receiving messages.
     * @param clientPort The port for sending messages.
     * @param nodes The list of nodes that this proposer is connected to.
     */
    public PaxosProposer(int serverPort, int clientPort, List<Node> nodes) {
        super(serverPort, clientPort, nodes);
        id = clientPort; // Use the client port as the proposer ID
    }

    /**
     * Sends a prepare request to all acceptors.
     * The prepare request is the first phase of the Paxos protocol where the proposer
     * solicits acceptors to agree to consider a particular proposal identified by a unique generation number.
     */
    public void sendPrepareRequests(String value) {
        int proposalNumber = 0; // TODO: Generate a unique proposal number

        // Create a prepare message
        PaxosMessage prepare = new PaxosMessage("PREPARE", proposalNumber, value);

        // Send prepare request to all acceptors
        for (Node node : nodes) {
            this.sendMessage(prepare, node);
        }
    }
    
    /**
     * Handles responses to the prepare requests from acceptors.
     * This method processes the promises from acceptors to not accept any more proposals numbered less than the one sent.
     * @param promise The promise message from an acceptor.
     */
    public void onPrepareResponse(PaxosMessage promise) {
        // Check if the proposal number is greater than the highest proposal number seen so far
        if (promise.getProposalNumber() == highestProposalNumberSeen) {
            // Update the number of promises received
            this.promisesReceived++;

            logger.info("Received promise from acceptor " + promise.getSenderID());

            // Check if a promise contains a value (which means the acceptor has already accepted a proposal)
            if (!promise.getValue().isEmpty()) {
                // Set the proposed value to the highest-numbered proposal's value
                if (promise.getProposalNumber() > this.highestProposalNumberSeen) {
                    this.proposedValue = promise.getValue();
                    this.highestProposalNumberSeen = promise.getProposalNumber();
                }
            }

            // Check if the number of promises received has reached a quorum
            if (hasReachedQuorum(promisesReceived)) {
                // Proceed to the accept phase with the proposed value
                sendAcceptRequests(this.highestProposalNumber, this.proposedValue);
            }
        } else {
            // Handle the case where the promise received is for an outdated proposal number
            logger.info("Received outdated promise from acceptor " + promise.getSenderID() + " for proposal number " + promise.getProposalNumber());
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
        logger.info("Sending accept requests to all acceptors for proposal number " + proposalNumber + " with value: " + value);

        // Create the accept message
        PaxosMessage acceptMessage = new PaxosMessage();
        acceptMessage.setType(PaxosMessage.MessageType.ACCEPT);
        acceptMessage.setProposalNumber(proposalNumber);
        acceptMessage.setValue(value);

        // Send the accept message to all nodes (acceptors)
        for (Node node : this.nodes) {
            sendMessage(acceptMessage, node);
        }
    }
    
    /**
     * Handles responses to the accept requests from acceptors.
     * This method processes the acceptances of its proposal by the acceptors.
     * @param acceptance The acceptance message from an acceptor.
     */
    public void onAcceptResponse(PaxosMessage acceptedMessage) {
        // Check if the accepted message corresponds to the current proposal number
        if (acceptedMessage.getProposalNumber() == this.highestProposalNumber) {
            // Increment the count of acceptances received
            int acceptances = countAcceptance(acceptedMessage);

            // Log the receipt of the acceptance
            logger.info("Received acceptance from acceptor " + acceptedMessage.getSenderID() + " for proposal number " + highestProposalNumber);

            // If the acceptances have reached a quorum, the proposal is chosen
            if (hasReachedQuorum(acceptances)) {
                // The value is now chosen; notify all nodes and perform any additional logic required
                onProposalChosen(acceptedMessage.getValue());
            }
        } else {
            // Handle the case where the accept response is for an outdated proposal number
            logger.info("Received outdated acceptance from acceptor " + acceptedMessage.getSenderID() + " for proposal number " + acceptedMessage.getProposalNumber());
        }
    }

    // Method to count the number of acceptances.
    // You would have some logic here to track the number of acceptances received for the current proposal.
    private int countAcceptance(PaxosMessage acceptedMessage) {
        // Increment and return the count of acceptances received for the proposal
        // This would involve updating the state of the proposer to keep track of the acceptances
        return 0; // Placeholder return
    }

    // This method would be called when the proposal has been chosen.
    // You would implement logic here that should occur once a proposal has been accepted by a quorum.
    private void onProposalChosen(String value) {
        // Log the success of the proposal
        logger.info("Proposal with number " + highestProposalNumber + " and value " + value + " has been chosen.");

        // Implement logic to be performed once the value has been chosen
        // This could involve sending a message to learners or updating some state within the system
    }

    @Override
    public void receiveMessage(PaxosMessage message) {
        // Handle received Paxos messages
        switch (message.getType()) {
            case PROMISE:
                onPrepareResponse(message);
                break;
            case ACCEPTED:
                onAcceptResponse(message);
                break;
            default:
                logger.warning("Received unsupported message type: " + message.getType());
        }
    }
}