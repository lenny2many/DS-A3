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