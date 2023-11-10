import paxos.messages.PaxosMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.logging.*;

/**
 * Represents a Paxos Learner.
 */
public class PaxosLearner extends PaxosParticipant {
    // Keep track of the accepted values and the number of times they have been accepted.
    private Map<String, Integer> acceptedValuesCount = new HashMap<>();
    private String learnedValue = null; // The value that has been learned (chosen).

    public PaxosLearner(int serverPort, List<Node> nodes) {
        super(serverPort, nodes);
    }

    public PaxosLearner(int serverPort, List<Node> nodes, NetworkServer server, MessageQueue messageQueue) {
        super(serverPort, nodes, server, messageQueue);
    }

    /**
     * Processes an accepted message from an acceptor. If enough acceptors have accepted the same proposal, the learner
     * learns the value.
     *
     * @param acceptedMessage The accepted message received from an acceptor.
     */
    public void onAccepted(PaxosMessage acceptedMessage) {
        logger.info("Received accepted message with value: " + acceptedMessage.getValue());
        acceptedValuesCount.merge(acceptedMessage.getValue(), 1, Integer::sum);

        // Determine if a majority has been reached for a single value
        if (acceptedValuesCount.get(acceptedMessage.getValue()) > (nodes.size() / 2)) {
            learnedValue = acceptedMessage.getValue();
            logger.info("Consensus reached on value: " + learnedValue);

            // Further actions can be taken here, such as notifying other components of the system about the learned value
        }
    }

    @Override
    public void receiveMessage(PaxosMessage message) {
        // Handle received Paxos messages
        switch (message.getType()) {
            case ACCEPTED:
                onAccepted(message);
                break;
            default:
                logger.warning("Received unsupported message type: " + message.getType());
        }
    }

    /**
     * Retrieves the value that has been learned by the learner, if any.
     *
     * @return The learned value or null if no value has been learned yet.
     */
    public String getLearnedValue() {
        return learnedValue;
    }
}
