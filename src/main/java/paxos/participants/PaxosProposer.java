package paxos.participants;

import paxos.messages.PaxosMessage;
import paxos.messages.PaxosMessageQueue;

import java.util.logging.*;

/**
 * Represents the Paxos Proposer.
 */
public class PaxosProposer implements PaxosParticipant {
    
    private static final Logger logger = Logger.getLogger(PaxosProposer.class.getName());

    /**
     * Constructor for PaxosProposer.
     * @param id The id of the proposer.
     * @param messageQueue The message queue of the proposer.
     */
    public PaxosProposer(int id, PaxosMessageQueue messageQueue) { }

    /**
     * Initialise the proposal process.
     */
    public void initialiseProposal() { 
        
    }

    /**
     * Begin the proposal process.
     * @param value The proposed value.
     */
    public void propose(Object value) { }

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
    public void send(PaxosMessage message) { }

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