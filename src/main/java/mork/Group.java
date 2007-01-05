package mork;

public class Group {

	private final String transactionId;

	private final String content;

	public Group(String transactionId, String content) {
		this.transactionId = transactionId;
		this.content = content;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public String getContent() {
		return content;
	}
}
