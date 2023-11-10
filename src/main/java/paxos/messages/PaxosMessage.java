package paxos.messages;

import java.util.Optional;
import java.util.logging.*;

/**
 * Represents a Paxos message passed between participants.
 */
public class PaxosMessage {
    private Type type;
    private String value;
    private int proposalNumber;
    private String participantID;

    private static final Logger LOGGER = Logger.getLogger(PaxosMessage.class.getName());

    /**
     * Enum for PaxosMessage types.
     */
    public enum Type {
        PREPARE,
        PROMISE,
        ACCEPT,
        ACCEPTED
    }

    private PaxosMessage(Type type, int proposalNumber, String value, String participantID) {
        this.type = type;
        this.proposalNumber = proposalNumber;
        this.value = value;
        this.participantID = participantID;
    }

    // Static factory method for PREPARE message
    public static PaxosMessage prepareMessage(int proposalNumber, String participantID) {
        return new PaxosMessage(Type.PREPARE, proposalNumber, null, participantID);
    }

    // Static factory method for PROMISE message
    public static PaxosMessage promiseMessage(int proposalNumber, String promisedValue, int lastAcceptedProposalNumber, String participantID) {
        String combinedValue = promisedValue + ":" + lastAcceptedProposalNumber;
        return new PaxosMessage(Type.PROMISE, proposalNumber, combinedValue, participantID);
    }

    // Static factory method for ACCEPT message
    public static PaxosMessage acceptRequestMessage(int proposalNumber, String value, String participantID) {
        return new PaxosMessage(Type.ACCEPT, proposalNumber, value, participantID);
    }

    // Static factory method for ACCEPTED message
    public static PaxosMessage acceptedMessage(int proposalNumber, String acceptedValue, String participantID) {
        return new PaxosMessage(Type.ACCEPTED, proposalNumber, acceptedValue, participantID);
    }

    /**
     * Convert a string representation of a message into a PaxosMessage object.
     * The expected message format is: <type>;<proposalNumber>;<participantID>;<value>
     * For example: "PREPARE;123;M1;SomeValue"
     * 
     * @param messageString The string representation of the message.
     * @return An Optional containing the PaxosMessage object, or empty if the format is invalid.
     */
    public static Optional<PaxosMessage> parseMessageFromString(String messageString) {
        // Ensure message is not empty or null
        if (messageString == null || messageString.isEmpty()) {
            LOGGER.warning("Message cannot be empty.");
            return Optional.empty();
        }

        String[] parts = messageString.split(";");
        if (parts.length != 4) {
            LOGGER.warning("Invalid message format. Expected format: <type>;<proposalNumber>;<participantID>;<value>");
            return Optional.empty();
        }

        String typeStr = parts[0];
        if (!isValidType(typeStr)) {
            LOGGER.warning("Invalid message type: " + typeStr);
            return Optional.empty();
        }
        Type type = Type.valueOf(typeStr);

        int proposalNumber;
        try {
            proposalNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            LOGGER.warning("Proposal number must be an integer. Found: " + parts[1]);
            return Optional.empty();
        }
        
        String participantID = parts[2];
        String value = parts[3];
        int lastAcceptedProposalNumber = -1; // Default value

        if (type == Type.PROMISE) {
            String[] valueParts = value.split(":");
            if (valueParts.length != 2) {
                LOGGER.warning("Invalid value format for PROMISE message: " + value);
                return Optional.empty();
            }
            value = valueParts[0];
            try {
                lastAcceptedProposalNumber = Integer.parseInt(valueParts[1]);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid last accepted proposal number: " + valueParts[1]);
                return Optional.empty();
            }
        }

        // Use the factory methods to create the message
        switch (type) {
            case PREPARE:
                return Optional.of(PaxosMessage.prepareMessage(proposalNumber, participantID));
            case PROMISE:
                return Optional.of(PaxosMessage.promiseMessage(proposalNumber, value, lastAcceptedProposalNumber, participantID));
            case ACCEPT:
                return Optional.of(PaxosMessage.acceptRequestMessage(proposalNumber, value, participantID));
            case ACCEPTED:
                return Optional.of(PaxosMessage.acceptedMessage(proposalNumber, value, participantID));
            default:
                LOGGER.warning("Unsupported message type: " + type);
                return Optional.empty();
        }
    }

    private static boolean isValidType(String typeStr) {
        for (Type type : Type.values()) {
            if (type.name().equals(typeStr)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert the message to a string representation.
     * This will include sender information if available.
     * @return The string representation of the message.
     */
    @Override
    public String toString() {
        return this.type + ";" + this.proposalNumber + ";" + this.participantID + ";" + this.value;
    }

    /**
     * Get the type of the message.
     * @return The type of the message.
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the proposal number.
     * @return The proposal number.
     */
    public int getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Get the proposed value.
     * @return The proposed value.
     */
    public String getValue() {
        return value;
    }

    public String getParticipantID() {
        return participantID;
    }
}