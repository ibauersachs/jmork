package mork;

/**
 * Represents a single group, which has an identifier and its content as String.
 * The content is in standard Mork format and can contain tables, rows,
 * dictionaries etc.
 * 
 * @author mhaller
 */
public class Group {

    /** A hex-encoded identifier */
    private final String transactionId;

    /** The unparsed Mork content of the group */
    private final String content;

    /**
     * Create a new Group with the given id and content
     * 
     * @param transactionId
     *            the id of the group, usually a number represented in hex
     * @param content
     *            the whole Mork content of the transaction group
     */
    public Group(String transactionId, String content) {
        this.transactionId = transactionId;
        this.content = content;
    }

    /**
     * Returns the ID of the transaction group as hexencoded string.
     * 
     * @return the id of the group
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Returns the unparsed content of the group
     * 
     * @return the unparsed content of the group
     */
    public String getContent() {
        return content;
    }
}
