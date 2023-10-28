package paxos.participants;

/**
 * Represents the Paxos Acceptor.
 */
class PaxosAcceptor extends PaxosParticipant {
    
    /**
     * Handle prepare messages from proposers.
     * @param message The received prepare message.
     */
    private void handlePrepare(PaxosMessage message) { }

    /**
     * Send promise message to proposer.
     * @param proposalNumber The proposal number.
     */
    private void sendPromise(int proposalNumber) { }

    /**
     * Handle accept request messages from proposers.
     * @param message The received accept request message.
     */
    private void handleAcceptRequest(PaxosMessage message) { }

    /**
     * Send accepted message to learner.
     * @param proposalNumber The proposal number.
     * @param value The accepted value.
     */
    private void sendAccepted(int proposalNumber, Object value) { }
}