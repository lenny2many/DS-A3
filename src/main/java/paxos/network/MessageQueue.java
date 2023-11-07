package paxos.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents message queue for producers and consumers.
 * 
 * This class is thread-safe.
 */
public class MessageQueue {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    /**
     * Add a message to the queue.
     * @param message The message to be added.
     * @throws InterruptedException
     */
    public void produceMessage(String message) throws InterruptedException {
        queue.put(message); // This may block if the queue has a capacity limit
    }

    /**
     * Remove a message from the queue.
     * @return The message removed from the queue.
     * @throws InterruptedException
     */
    public String consumeMessage() throws InterruptedException {
        return queue.take(); // This will block until a message is available
    }
}
