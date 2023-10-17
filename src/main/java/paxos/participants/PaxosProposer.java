/**
 * Represents the Paxos Proposer.
 */
class PaxosProposer extends PaxosParticipant {
    
    /**
     * Begin the proposal process.
     * @param value The proposed value.
     */
    public void propose(Object value) { }

    /**
     * Handle promise messages from acceptors.
     * @param message The received promise message.
     */
    private void handlePromise(PaxosMessage message) { }

    /**
     * Send accept request to acceptors.
     * @param proposalNumber The proposal number.
     * @param value The proposed value.
     */
    private void sendAcceptRequest(int proposalNumber, Object value) { }
}