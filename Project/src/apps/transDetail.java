package apps;

public class transDetail {
	private String transactionId, juiceId, juiceName;
	private Integer quantity;

	public transDetail(String transactionId, String juiceId, String juiceName, Integer quantity) {
		this.transactionId = transactionId;
		this.juiceId = juiceId;
		this.juiceName = juiceName;
		this.quantity = quantity;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getJuiceId() {
		return juiceId;
	}

	public void setJuiceId(String juiceId) {
		this.juiceId = juiceId;
	}

	public String getJuiceName() {
		return juiceName;
	}

	public void setJuiceName(String juiceName) {
		this.juiceName = juiceName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
}
