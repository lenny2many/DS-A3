package paxos.messages;

/**
 * Represents a Paxos message passed between participants.
 */
public class PaxosMessage {
    private Type type;
    private int proposalNumber;
    private Object value;

    /**
     * Enum for PaxosMessage types.
     */
    public enum Type {
        PREPARE,
        PROMISE,
        ACCEPT_REQUEST,
        ACCEPTED
    }

    /**
     * Constructor for PaxosMessage.
     * @param type The type of the message.
     * @param proposalNumber The proposal number.
     * @param value The proposed value.
     */
    public PaxosMessage(String type, int proposalNumber, Object value) {
        this.type = Type.valueOf(type);
        this.proposalNumber = proposalNumber;
        this.value = value;
    }

    /**
     * Convert a string representation of a message into a PaxosMessage object.
     * @return The string representation of the message.
     */
    public static PaxosMessage parseMessageFromString(String messageString) {
        // Split the string by a delimiter to extract the different parts
        // This is just an example; you'll need to adapt it based on your message format
        String[] parts = messageString.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid message format");
        }

        String type = parts[0];
        int proposalNumber = Integer.parseInt(parts[1]);
        Object value = parts[2];

        return new PaxosMessage(type, proposalNumber, value);
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
    public Object getValue() {
        return value;
    }
}