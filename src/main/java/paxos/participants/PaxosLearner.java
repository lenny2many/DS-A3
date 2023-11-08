package paxos.participants;

import paxos.messages.*;

import java.util.List;

/**
 * Represents the Paxos Learner.
 * Learners are the components of the Paxos algorithm responsible for completing the consensus process.
 * They learn the value that has been agreed upon by a quorum of acceptors.
 */
public class PaxosLearner extends PaxosParticipant {
    // Learner-specific fields such as records of acceptances
    
    /**
     * Constructor for PaxosLearner.
     * @param serverPort The port for receiving messages.
     * @param clientPort The port for sending messages.
     * @param nodes The list of nodes that this learner is connected to.
     */
    public PaxosLearner(int serverPort, int clientPort, List<Node> nodes) {
        super(serverPort, clientPort, nodes);
        // Initialize fields
    }
    
    /**
     * Learns the accepted value once it has been chosen by a quorum of acceptors.
     * @param accepted The accepted message from an acceptor or proposer.
     */
    public void learnValue(PaxosMessage accepted) {
        // Implementation details
    }

    @Override
    public void receiveMessage(PaxosMessage message) {
        // Handle received Paxos message and potentially call learnValue
    }
    
}
