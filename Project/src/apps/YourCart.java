package apps;

import java.sql.ResultSet;
import java.util.Vector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class YourCart {
	private int quantity;
    private String juiceName;
    private int price;
    private int totalPrice;
    
    public YourCart(int quantity, String juiceName, int price) {
		super();
		this.quantity = quantity;
		this.juiceName = juiceName;
		this.price = price;
	}
    
    @Override
    public String toString() {
        return quantity + "x " + juiceName + " - Rp. " + price;
    }
    
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getJuiceName() {
		return juiceName;
	}
	public void setJuiceName(String juiceName) {
		this.juiceName = juiceName;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	
}
