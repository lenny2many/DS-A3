package paxos.participants;

import paxos.messages.PaxosMessage;
import paxos.network.PaxosMessageQueue;

import java.util.logging.*;

/**
 * Represents the Paxos Proposer.
 */
public class PaxosProposer implements PaxosParticipant {
    public int id = 0;
    public MessageQueue messageQueue = null;
    
    private static final Logger logger = Logger.getLogger(PaxosProposer.class.getName());

    /**
     * Constructor for PaxosProposer.
     * @param id The id of the proposer.
     * @param messageQueue The message queue of the proposer.
     */
    public PaxosProposer(int id, PaxosMessageQueue messageQueue) {
        this.id = id;
        this.messageQueue = messageQueue;
    }

    /**
     * Begin the proposal process.
     * @param value The proposed value.
     */
    public void propose(Object value) { 
        logger.info("Proposer " + this.id + " proposing value " + value + ".");
        PaxosMessage message = new PaxosMessage("PREPARE", 0, value);
        this.send(message);
    }

    /**
     * Handle promise messages from acceptors.
     * @param message The received promise message.
     */
    public void handlePromise(PaxosMessage message) { }

    /**
     * Send accept request to acceptors.
     * @param proposalNumber The proposal number.
     * @param value The proposed value.
     */
    public void sendAcceptRequest(int proposalNumber, Object value) { }

    /**
     * Process incoming message.
     * @param message The received PaxosMessage.
     */
    public void onReceive(PaxosMessage message) { }

    /**
     * Send message to another participant.
     * @param message The PaxosMessage to be sent.
     */
    public void send(PaxosMessage message, int acceptorId) {
        logger.info("Proposer " + this.id + " sending message " + message.getType() + " to acceptor " + acceptorId + ".");
        // Send message to acceptor over network.
    }

    /**
     * Get the current proposal number.
     * @return The current proposal number.
     */
    public int getCurrentProposalNumber() { return 0; }

    /**
     * Get the current proposed value.
     * @return The current proposed value.
     */
    public Object getCurrentProposedValue() { return null; }

    /**
     * Get the received promise message.
     * @return The received promise message.
     */
    public PaxosMessage getReceivedPromiseMessage() { return null; }
}