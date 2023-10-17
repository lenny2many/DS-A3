/**
 * Represents the abstract Paxos Participant.
 */
abstract class PaxosParticipant {
    protected int id;
    protected PaxosMessageQueue messageQueue;

    /**
     * Process incoming message.
     * @param message The received PaxosMessage.
     */
    abstract void onReceive(PaxosMessage message);

    /**
     * Send message to another participant.
     * @param message The PaxosMessage to be sent.
     */
    abstract void send(PaxosMessage message);
}