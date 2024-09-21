package apps;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class CheckoutScene extends Stage {
	ConnectionDB con = new ConnectionDB();
	GridPane gridPane;
	
	Label cartLbl;
	Label priceLbl;
	
	Button cancelButton, checkoutButton, logoutBtn;
	RadioButton cashRadioButton, debitCardRadioButton, creditCardRadioButton;
	ToggleGroup paymentToggleGroup;
	
	MenuBar menuBar;
	Menu menu;
	
	Vector<YourCart> cartVector = new Vector<>();
	ObservableList<YourCart> juicesList;
	String username = UserSession.getInstance().getUsername();
	String payType;
	

    public CheckoutScene() {
	      menuBar = new MenuBar();
	      menu = new Menu();
	      logoutBtn = new Button("Logout");
	      menu.setGraphic(logoutBtn);
	     
	        Label titleLabel = new Label("Checkout");
	        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 60)); // Set font size and weight for the title
	        
	        priceLbl = new Label("Total Price:");
	        priceLbl.setFont(Font.font("Arial", 12));
	        Label typeLbl = new Label("Payment Type:");
	        typeLbl.setFont(Font.font("Arial", 14));
	        cartLbl = new Label("hello");
	        cartLbl.setStyle("-fx-font-family: Arial; -fx-font-size:12; -fx-line-spacing: 5;");
	
	        paymentToggleGroup = new ToggleGroup();
	        
	        RadioButton cashRadioButton = new RadioButton("Cash");
	        cashRadioButton.setFont(Font.font("Arial", 14));
	        cashRadioButton.setToggleGroup(paymentToggleGroup);
	        RadioButton debitCardRadioButton = new RadioButton("Debit");
	        debitCardRadioButton.setFont(Font.font("Arial", 14));
	        debitCardRadioButton.setToggleGroup(paymentToggleGroup);
	        RadioButton creditCardRadioButton = new RadioButton("Credit");
	        creditCardRadioButton.setFont(Font.font("Arial", 14));
	        creditCardRadioButton.setToggleGroup(paymentToggleGroup);
	        
	        cancelButton = new Button("Cancel");
	        cancelButton.setMinSize(40, 40);
	        checkoutButton = new Button("Checkout");
	        checkoutButton.setMinSize(40, 40);
	
	        BorderPane borderPane = new BorderPane();
	        borderPane.setTop(menuBar);
	        menuBar.getMenus().add(menu); 
	        
	        gridPane = new GridPane();
	        gridPane.setPadding(new Insets(20));
	        gridPane.setVgap(20);
	        gridPane.setHgap(10);
	        gridPane.setAlignment(Pos.CENTER); 
	
	        GridPane.setColumnSpan(titleLabel, 3);
	        gridPane.add(titleLabel, 0, 0);
	
	        gridPane.add(cartLbl, 0, 1);
	        gridPane.add(priceLbl, 0, 2);
	        gridPane.add(typeLbl, 0, 3);
	             
	        GridPane.setMargin(creditCardRadioButton, new Insets(0, 0, 0, 50));
	        
	        // FlowPane untuk radioButton
	        FlowPane radioBtnPane = new FlowPane();
	        radioBtnPane.getChildren().addAll(cashRadioButton, debitCardRadioButton, creditCardRadioButton);
	        FlowPane.setMargin(creditCardRadioButton, new Insets(0, 0, 0, 50));
	        FlowPane.setMargin(debitCardRadioButton, new Insets(0, 0, 0, 50));
	        gridPane.add(radioBtnPane, 0, 4, 3, 1);
	        
	        // HBox untuk Button
	        HBox buttonsBox = new HBox(20);
	        buttonsBox.setAlignment(Pos.CENTER);
	        buttonsBox.getChildren().addAll(cancelButton, checkoutButton);
	        gridPane.add(buttonsBox, 0, 5, 3, 1);
	        
	        getData();
	        btnEvent();
	        totalAllJuice();
	        borderPane.setCenter(gridPane);
	        Scene scene = new Scene(borderPane, 800, 500);
	        this.setScene(scene);
	        this.show(); this.setTitle("NJuice");
    }
    
    public void totalAllJuice() {
     	try {
    		    con.openConnection();
    		    int totalAllJuice=0;

    		    ResultSet rs = con.execQuery("SELECT c.Username, c.JuiceId, c.Quantity, j.Price " +
    		                                  "FROM cartdetail c " +
    		                                  "JOIN msjuice j ON c.JuiceId = j.JuiceId");
    		    while (rs.next()) {
    		        int quantity = rs.getInt("Quantity");
    		        int price = rs.getInt("Price");

    		        int itemTotal = quantity * price;
    		        totalAllJuice += itemTotal;
    		    }
    		    priceLbl.setText("Price: " + totalAllJuice);
    		    con.closeConnection();
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}

     }
    
    public void btnEvent() {
    	
    	// Logout
        logoutBtn.setOnAction(e -> {
  			 try {
  				UserSession.getInstance().clearSession();
  				 login loginForm = new login();
  				 Stage loginStage = new Stage();
  				 loginForm.start(loginStage);
  				 this.close();
  			 } catch (Exception e1) {
  				 e1.printStackTrace();
  			 }
  		 });
        
        
        // Cancel
        cancelButton.setOnAction(e ->{
        	CustomerHome custHome = new CustomerHome();
        	custHome.show();
        	this.close();
        });
        
        // CheckOut
        checkoutButton.setOnAction(event -> {
            if (paymentToggleGroup.getSelectedToggle() == null) {
                showAlertError("Error", "Error", "Please select payment type");
            } else {
                // Set the payType based on the selected radio button
                RadioButton selectedRadioButton = (RadioButton) paymentToggleGroup.getSelectedToggle();
                payType = selectedRadioButton.getText();
                
                int lastTransactionNumber = getLastTransactionNumber();
                String newTransactionId = generateTransactionId(lastTransactionNumber);
                insertTransaction(newTransactionId, username, payType);

                showAlertInfo("Message", "Message", "All items checked out successfully. Please proceed with your payment.");

                try {
                    con.openConnection();
                    con.execUpdate("DELETE FROM cartdetail WHERE Username='" + username + "'");
                    CustomerHome custHome = new CustomerHome();
                    custHome.refreshTable();
                    con.closeConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public int getLastTransactionNumber() {
    	int lastTransactionNumber = 0;
    	try {
	    	con.openConnection();
	        ResultSet resultSet = con.execQuery("SELECT MAX(CAST(SUBSTRING(TransactionId, 3) AS SIGNED)) FROM transactionheader");
	
	        lastTransactionNumber = 0;
	        if (resultSet.next()) {
	            lastTransactionNumber = resultSet.getInt(1);
	        }
	        con.closeConnection();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}finally {
            try {
                con.closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    	return lastTransactionNumber;
    }

    public String generateTransactionId(int lastTransactionNumber) {
        
    	// Increment transaction id
        lastTransactionNumber++;

        // transaction ID
        return String.format("TR%03d", lastTransactionNumber);
    }
    
    public void insertDataTransaction() {
        
    	try {
    	    con.openConnection();

    	    String insertDetailQuery = "INSERT INTO transactiondetail (TransactionId, JuiceId, Quantity) " +
					"SELECT newTransactionId, JuiceId, Quantity FROM cartdetail WHERE Username = '" + username + "'";
			con.execUpdate(insertDetailQuery);
    	    con.closeConnection();
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    }

    public void insertTransaction(String transactionId, String username, String paymentType) {
    	int lastTransactionNumber = getLastTransactionNumber();
    	String newTransactionId = generateTransactionId(lastTransactionNumber);
        try {
        	con.openConnection();
        	// Insert into transactionheader
        	String insertHeaderQuery = String.format("INSERT INTO transactionheader VALUES ('%s', '%s', '%s')", newTransactionId, username, paymentType);
			con.execUpdate(insertHeaderQuery);
			
			
			// Insert into transactiondetail 
			String insertDetailQuery = "INSERT INTO transactiondetail (TransactionId, JuiceId, Quantity) " +
					   "SELECT '" + newTransactionId + "', JuiceId, Quantity FROM cartdetail WHERE Username = '" + username + "'";
					con.execUpdate(insertDetailQuery);
			
			con.closeConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    	public void getData() {
    		StringBuilder cartBuilder = new StringBuilder();
    		 try {
    			 con.openConnection();
    	            String query = "SELECT cd.Username, cd.JuiceId, cd.Quantity, mj.JuiceName, mj.Price " +
    	                           "FROM cartdetail cd JOIN msjuice mj ON cd.JuiceId = mj.JuiceId WHERE Username='"+username+"'";

    	            ResultSet rsGetData = con.execQuery(query);

    	            while (rsGetData.next()) {
    	                int quantity = rsGetData.getInt("Quantity");
    	                String juiceName = rsGetData.getString("JuiceName");
    	                int price = rsGetData.getInt("Price");

    	                String cart = String.format("%dx %s \t[%dx Rp. %d,- = Rp.%d,-]\n", 
    	                		quantity, juiceName, quantity, price, quantity * price);
    	                
    	                cartBuilder.append(cart);
    	                System.out.println(cart);
    	            }
    	            cartLbl.setText(cartBuilder.toString());
    	            con.closeConnection();
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }
    	 }

    private void showAlertError(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
        this.close();
        CustomerHome custHome = new CustomerHome();
        custHome.show();
    }
    private void showAlertInfo(String title, String header, String message) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setTitle(title);
    	alert.setHeaderText(header);
    	alert.setContentText(message);
    	alert.showAndWait();
    	this.close();
    	CustomerHome custHome = new CustomerHome();
    	custHome.show();
    }
}