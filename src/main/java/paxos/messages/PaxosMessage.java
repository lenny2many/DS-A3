package paxos.messages;

import java.util.Optional;
import java.util.logging.*;

/**
 * Represents a Paxos message passed between participants.
 */
public class PaxosMessage {
    private Type type;
    private int proposalNumber;
    private String value;
    private int senderID;

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

    /**
     * Constructor for PaxosMessage.
     * @param type The type of the message.
     * @param proposalNumber The proposal number.
     * @param value The proposed value.
     */
    public PaxosMessage(String type, int proposalNumber, String value) {
        this.type = Type.valueOf(type);
        this.proposalNumber = proposalNumber;
        this.value = value;
    }

    /**
     * Convert a string representation of a message into a PaxosMessage object.
     * The expected message format is: <type>;<proposalNumber>;<value>
     * For example: "PROPOSE;123;SomeValue"
     * 
     * @param messageString The string representation of the message.
     * @return The string representation of the message.
     */
    public static Optional<PaxosMessage> parseMessageFromString(String messageString) {
        // Ensure message is not empty or null
        if (messageString == null || messageString.isEmpty()) {
            LOGGER.warning("Message cannot be empty.");
            return Optional.empty();
        }

        String[] parts = messageString.split(";");
        if (parts.length != 3) {
            LOGGER.warning("Invalid message format. Expected format: <type>;<proposalNumber>;<value>");
            return Optional.empty();
        }
    
        String typeStr = parts[0];
        if (!isValidType(typeStr)) {
            LOGGER.warning("Invalid message type: " + typeStr);
            return Optional.empty();
        }
    
        int proposalNumber;
        try {
            proposalNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            LOGGER.warning("Proposal number must be an integer. Found: " + parts[1]);
            return Optional.empty();
        }
    
        String value = parts[2].trim();
        if (value.isEmpty()) {
            LOGGER.warning("Value cannot be empty.");
            return Optional.empty();
        }
    
        return Optional.of(new PaxosMessage(typeStr, proposalNumber, value));
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
     * @return The string representation of the message.
     */
    public String toString() {
        return type + ";" + proposalNumber + ";" + value;
    }

    /**
     * Get the type of the message.
     * @return The type of the message.
     */
    public String getType() {
        return type.toString();
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

    /**
     * Get the sender ID.
     * @return The sender ID.
     */
    public int getSenderID() {
        return senderID;
    }
}