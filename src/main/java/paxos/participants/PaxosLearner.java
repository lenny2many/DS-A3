package paxos.participants;

/**
 * Represents the Paxos Learner.
 */
class PaxosLearner extends PaxosParticipant {
    
    /**
     * Handle accepted messages from acceptors.
     * @param message The received accepted message.
     */
    private void handleAccepted(PaxosMessage message) { }

    /**
     * Notify of consensus achievement.
     * @param value The consensus value.
     */
    private void notifyConsensus(Object value) { }
}