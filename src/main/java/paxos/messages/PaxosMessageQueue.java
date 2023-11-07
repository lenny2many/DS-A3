package paxos.messages;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a queue for Paxos messages, allowing participants to send and receive messages.
 */
public class PaxosMessageQueue {
    private List<PaxosMessage> messageQueue;
    
    /**
     * Constructor for PaxosMessageQueue.
     */
    public PaxosMessageQueue() { 
        messageQueue = new ArrayList<>();
    }

    /**
     * Add a message to the queue.
     * @param message The message to be added.
     */
    public void addMessage(PaxosMessage message) {
        messageQueue.add(message);
    }

    /**
     * Get the next message from the queue.
     * @return The next message from the queue.
     */
    public PaxosMessage getNextMessage() { return messageQueue.remove(0); }
}