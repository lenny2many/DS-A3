package paxos.network;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents message queue for producers and consumers.
 * 
 * This class is thread-safe.
 */
public class MessageQueue {
    private final BlockingQueue<ClientMessage> queue = new LinkedBlockingQueue<>();

    /**
     * Add a message to the queue.
     * @param message The message to be added.
     * @throws InterruptedException
     */
    public void produceMessage(ClientMessage message) throws InterruptedException {
        queue.put(message); // This may block if the queue has a capacity limit
    }

    /**
     * Remove a message from the queue.
     * @return The message removed from the queue.
     * @throws InterruptedException
     */
    public ClientMessage consumeMessage() throws InterruptedException {
        return queue.take(); // This will block until a message is available
    }

    public static class ClientMessage {
        private String message;
        private Socket clientSocket;

        public ClientMessage(String message, Socket clientSocket) {
            this.message = message;
            this.clientSocket = clientSocket;
        }

        public String getMessage() {
            return message;
        }

        public Socket getClientSocket() {
            return clientSocket;
        }
    }
}
