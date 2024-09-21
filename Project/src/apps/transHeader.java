package apps;

public class transHeader {
	private String transactionId, paymentType, username;
	
	public transHeader(String transactionId, String paymentType, String username) {
		this.transactionId = transactionId;
		this.paymentType = paymentType;
		this.username = username;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
