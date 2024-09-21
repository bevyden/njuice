package apps;

	import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
	import javafx.geometry.Insets;
	import javafx.geometry.Pos;
	import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
	import javafx.scene.control.Label;
	import javafx.scene.control.Spinner;
	import javafx.scene.layout.BorderPane;
	import javafx.scene.layout.FlowPane;
	import javafx.scene.layout.StackPane;
	import javafx.scene.layout.VBox;
	import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

	public class AddNewItem{
		
		ConnectionDB con = new ConnectionDB();
		
		Rectangle box = new Rectangle(400, 30);
		Label title = new Label("Add new item");
		
		//main scene
		Stage s;
		Scene sc;
	 	BorderPane bpAddItem;
	    FlowPane fpAddItem;
	    VBox vbAddItem;
	    StackPane sp;
	 
	    //komponen
	    Label juiceLbl;
	    Label priceLbl;
	    Label descLbl;
	    Label qtyLbl;
	    Label totPriceLbl;
	    ComboBox<String> productBox;
	    Spinner<Integer> qtySpinner;
	    Button addItemBtn;
	    
	    Integer price;
	    String id;
	    String username = UserSession.getInstance().getUsername();
	    CustomerHome custHome;
	    
	    public void init() {
		     bpAddItem = new BorderPane();
		     fpAddItem = new FlowPane();
		     vbAddItem = new VBox();
		     sp = new StackPane();
		     
		     juiceLbl = new Label("Juice:");
		     priceLbl = new Label("JuicePrice: ");
		     descLbl = new Label ("Description: ");
		     descLbl.setWrapText(true);
		     qtyLbl = new Label("Quantity: ");
		     totPriceLbl = new Label("Total Price: ");
		     productBox = new ComboBox<>();
		     qtySpinner = new Spinner<>(1, 100, 1);
		     
		     addItemBtn = new Button("Add Item");
	     
	    }
	    
	    public void set() {
		    bpAddItem.setTop(sp);
		    bpAddItem.setCenter(vbAddItem);
		    bpAddItem.setStyle("-fx-background-color: white;");
		    
		    // Add new item label style
		    title.setStyle("-fx-font-size:15; -fx-text-fill: white;");
		    title.setAlignment(Pos.CENTER);
		    sp.getChildren().addAll(box, title);
		    
		     //combobox
		     productBox.getItems().addAll("Avocado Avalanches", "Apple Adventure", "Berry Blast", "Mango Tango", "Citrus Crush", "Watermelon Wave");
		     
		     fpAddItem.getChildren().addAll(productBox, priceLbl);
		     vbAddItem.getChildren().addAll(juiceLbl, fpAddItem, descLbl, qtyLbl, qtySpinner, totPriceLbl, addItemBtn);
		     // Set the font size for all text in the VBox
		     vbAddItem.setStyle("-fx-font-size: 15;");
	    }
	    
	    public void position() {
	    	vbAddItem.setAlignment(Pos.CENTER);
		    fpAddItem.setAlignment(Pos.CENTER);
	    	
		     vbAddItem.setPadding(new Insets(15));
		     fpAddItem.setHgap(10);
		     vbAddItem.setSpacing(10);
	    }
	    
	    // Price Label, Description Label
	    public void setLabel() {
	    	productBox.setOnAction(e -> {
	    		String selectedOption = productBox.getValue();
	    		totalPrice();
	    		try {
	    			con.openConnection();
	    			ResultSet rsProduct = con.execQuery("SELECT * FROM msjuice WHERE JuiceName='"+selectedOption+"'");
		            if (rsProduct.next()) {
		            	String juiceDesc = rsProduct.getString("JuiceDescription");
		            	price = rsProduct.getInt("Price");
		            	id = rsProduct.getString("JuiceId");
						
		            	if (selectedOption.equals("Avocado Avalanches")) {
		            		priceLbl.setText("Price: 23500");
		            		descLbl.setText(juiceDesc);
		            		
		            	} else if(selectedOption.equals("Apple Adventure")){
		            		priceLbl.setText("Price: 17400");
		            		descLbl.setText(juiceDesc);
		            		
		            	}else if(selectedOption.equals("Berry Blast")){
		            		priceLbl.setText("Price: 24500");
		            		descLbl.setText(juiceDesc);
		            		
		            	}else if(selectedOption.equals("Mango Tango")){
		            		priceLbl.setText("Price: 20400");
		            		descLbl.setText(juiceDesc);
		            		
		            	}else if(selectedOption.equals("Citrus Crush")){
		            		priceLbl.setText("Price: 21900");
		            		descLbl.setText(juiceDesc);
		            		
		            	}else if(selectedOption.equals("Watermelon Wave")){
		            		priceLbl.setText("Price: 15400");
		            		descLbl.setText(juiceDesc);
		            	}
		            }
		            } catch (Exception e1) {
		            	e1.printStackTrace();
		            }
	    		});
	    	
	    	// Qty Spinner untuk total Price
	    	totalPrice();
	    	qtySpinner.setOnMouseClicked(e -> totalPrice());
	    }
	    
	    // Total Price
	    public void totalPrice() {
	    		String selectedOption = productBox.getValue();
	    		Integer qty = qtySpinner.getValue();

	    		try {
	    			con.openConnection();
	    			ResultSet rsProduct = con.execQuery("SELECT * FROM msjuice WHERE JuiceName='"+selectedOption+"'");
		            
	    			if (rsProduct.next()) {
		            	Integer price = rsProduct.getInt("Price");
		            	Integer total = price * qty;
		            	totPriceLbl.setText("Total Price: " + total.toString());
		            }
		         } catch (Exception e1) {
		            	e1.printStackTrace();
		         }
	    }
	    
	    
	    
	    // INSERT INTO cartdetail VALUES (username, JuiceId, Quantity)
	    public void addData(String user, String id, Integer qty) {
			try {
				con.openConnection();
				String query = 
						String.format("INSERT INTO cartdetail VALUES('%s', '%s', %d)", 
								user, id, qty);
				con.execUpdate(query);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	    
	    // Add New Item Button
	    public void addBtnEvent(){
	    	addItemBtn.setOnAction(e -> {
	    		if (validateBoxNull()) {
	    			Integer qty = Integer.valueOf(qtySpinner.getValue());
	    			
	    			try {
	    	        	con.openConnection();
	    	        	String q = String.format("SELECT * FROM cartdetail WHERE Username ='%s' AND JuiceId='%s'", username, id);
	    				ResultSet rsValidate = con.execQuery(q);
	    				
	    				if (rsValidate.next()) {
	    			        // Product sudah dalam cart, quantity akan ditambah dengan qtySpinner
	    			        int currentQty = rsValidate.getInt("Quantity");
	    			        int newQty = currentQty + qty;

	    			        // Update quantity di database
	    			        con.execUpdate("UPDATE cartdetail SET Quantity = " + newQty + " WHERE Username = '" + username + "' AND JuiceId = '" + id + "'");
	    				}
	    				con.closeConnection();
	    			} catch (Exception e4) {
	    				e4.printStackTrace();
	    			}
	    			addData(username, id, qty);
	    			custHome = new CustomerHome();
	    			custHome.refreshTable();
	    			s.close();
				}
	    	});
	    }
	    
	    private boolean validateBoxNull() {
	        String juiceName = productBox.getValue();
	        
	        if (juiceName == null || juiceName.isEmpty()) {
	            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a juice!", ButtonType.OK);
		        alert.showAndWait();
	            return false;
	        }
	        return true;
	    }
	    
	    
	    public void AddNewItem() {
	    	init();
	    	set();
	    	position();
	    	setLabel();
	    	addBtnEvent();
	    	
	    	s = new Stage();
	    	Scene addItemScene = new Scene(bpAddItem, 400, 400);
	    	s.setScene(addItemScene);
	    	s.show(); s.setTitle("NJuice");
	    	
	    }
	}


