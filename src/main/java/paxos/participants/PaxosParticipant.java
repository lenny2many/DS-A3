package paxos.participants;

import paxos.messages.*;

/**
 * Represents the interface of a Paxos Participant.
 */
interface PaxosParticipant {
    public int id = 0;
    public PaxosMessageQueue messageQueue = null;

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