package apps;

import java.sql.ResultSet;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CustomerHome extends Stage {
	GridPane gp;
	VBox vb;
	BorderPane bp;
	HBox hb;
	
	MenuBar menuBar;
	Menu menu;
	
	Label yourCartLbl, emptyLbl, greetingLbl;
	Button newBtn, deleteBtn, checkoutBtn, logoutBtn;

	// Cart Detail List View
	Vector<YourCart> cartVector = new Vector<>();
    ObservableList<YourCart> juicesList;
    ListView<YourCart> cartLV;
	
	ConnectionDB con = new ConnectionDB();
	login loginForm = new login();
	AddNewItem addScene;
	CheckoutScene checkout;
	
	String username = UserSession.getInstance().getUsername();
	String id;
 
 public void init() {
	 gp = new GridPane();
	 vb = new VBox();
	 hb = new HBox();
	 bp = new BorderPane();
	 
	 menuBar = new MenuBar();
	 menu = new Menu();
	 
	 yourCartLbl = new Label("Your Cart");
	 yourCartLbl.setStyle("-fx-font-size: 35; -fx-font-weight: bold;");
	 
	 emptyLbl = new Label("Your cart is empty, try adding new items!");
	 newBtn = new Button("Add new Item to Cart");
	 deleteBtn = new Button("Delete Item From Cart");
	 
	 checkoutBtn = new Button("Checkout");	 
	 logoutBtn = new Button("Logout");
	 menu.setGraphic(logoutBtn);
	 
  
	 getData(); // panggil method getData dari cartdetail
	 juicesList = FXCollections.observableArrayList(cartVector);
	 cartLV = new ListView<>(juicesList);
	 cartLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
 }
 
 
 public void set() {
	 bp.setTop(menuBar);
	 bp.setCenter(vb);
	 
	 menuBar.getMenus().add(menu);
	 
	 cartLV.setMaxSize(500, 250);
	 getData();
	 
	 //atur posisi komponen di gridpane
	 gp.add(newBtn, 0, 0);
	 gp.add(deleteBtn, 1, 0);
	 gp.add(checkoutBtn, 2, 0);
	 newBtn.setMinSize(100, 50);
	 deleteBtn.setMinSize(100, 50);
	 checkoutBtn.setMinSize(100, 50);
 }
 
 private void position() {
	 gp.setPadding(new Insets(15));
	 gp.setHgap(30);
	 gp.setVgap(10);
	 
	 vb.setAlignment(Pos.CENTER);
	 gp.setAlignment(Pos.CENTER);
 }

 public void checkCartIsEmpty() {
	 try {
		 con.openConnection();
		ResultSet rsCheckIsEmpty = con.execQuery("SELECT * FROM cartdetail WHERE Username='"+username+"'");
		
		if (rsCheckIsEmpty.next()) {
			vb.getChildren().addAll(yourCartLbl, cartLV, emptyLbl, gp);
			emptyLbl.setText("Total Price: ");
			checkoutBtn.setOnAction(e -> {
                CheckoutScene coScene = new CheckoutScene();
                coScene.show();
               this.close();
            });
		} else {
			vb.getChildren().addAll(yourCartLbl, emptyLbl, gp);
			showCheckoutAlert();
		}
		con.closeConnection();
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
    }
 }
 
 private void showCheckoutAlert() {
	 checkoutBtn.setOnAction(e -> {
		 Alert alert = new Alert(Alert.AlertType.ERROR);
		 alert.setTitle("Error");
		 alert.setHeaderText("Error");
		 alert.setContentText("Cannot checkout. Cart is empty.");
		 alert.showAndWait();	
	 });
     
 }
 
 
 public void showDeleteAlert() {
	 YourCart selectedJuice = cartLV.getSelectionModel().getSelectedItem();
	 System.out.println(selectedJuice);
	 if (selectedJuice == null) {
		 Alert alert = new Alert(AlertType.ERROR);
		 alert.setTitle("Error");
		 alert.setContentText("Please choose which juice to delete");
		 alert.showAndWait();
	 }else {    
		 ObservableList<YourCart> items = cartLV.getItems();
		 items.remove(selectedJuice);
		 
		 // DELETE DARI CART
		 try {
			con.openConnection();
			String selectedJuiceName = selectedJuice.getJuiceName();
			ResultSet rsJuiceId = con.execQuery("SELECT JuiceId FROM msjuice WHERE JuiceName = '" + selectedJuiceName + "'");

	        if (rsJuiceId.next()) {
	            String juiceIdToDelete = rsJuiceId.getString("juiceId");

	            con.execUpdate("DELETE FROM cartdetail WHERE JuiceId = '" + juiceIdToDelete + "'");
	            refreshTable();
	            con.closeConnection();
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
}
 
 public void refreshTable() {
     getData();
     ObservableList<YourCart> regObs = FXCollections.observableArrayList(cartVector);
     cartLV.setItems(regObs);
 }
 
 public void getData() {
     try {
    	 con.openConnection();
    	 cartVector.clear();
    	 ResultSet rsCart = con.execQuery("SELECT * FROM cartdetail cd JOIN msjuice mj ON cd.JuiceId = mj.JuiceId WHERE cd.Username='"+username+"'");
    	 
		while (rsCart.next()) {
		     int quantity = rsCart.getInt("Quantity");
		     String juiceName = rsCart.getString("JuiceName");
		     int price = rsCart.getInt("Price");
		     int totalPrice = price * quantity;

		     cartVector.add(new YourCart(quantity, juiceName, totalPrice));
		     totalAllJuice();
		 }
		con.closeConnection();
	} catch (Exception e) {
		e.printStackTrace();
	}
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
		    emptyLbl.setText("Price: " + totalAllJuice);
		    con.closeConnection();
		} catch (Exception e) {
		    e.printStackTrace();
		}
 }
 
 public void btnEvent() {
	 // Add New Item Button ( Pop-up )
	 newBtn.setOnAction(e -> {
		 addScene = new AddNewItem();
		 addScene.AddNewItem();
		 this.close();
	 });
	 
	 // Delete Item Button ( Alert )
	 deleteBtn.setOnAction(e -> showDeleteAlert());
	 
	 // Logout Menu 
	 logoutBtn.setOnAction(e -> {
		 try {
			 UserSession.getInstance().clearSession();
			 System.out.println(UserSession.getInstance().getUsername());
			 Stage loginStage = new Stage();
			 loginForm.start(loginStage);
			 this.close();
		 } catch (Exception e1) {
			 e1.printStackTrace();
		 }
	 });
	 
 }
 
 
 public CustomerHome() {
	 init();
	 set();
	 position();
	 checkCartIsEmpty();
	 btnEvent();
	 refreshTable();
	 getData();
	 
	 Scene chScene = new Scene(bp, 800, 500);
	 this.setScene(chScene);
	 this.show(); this.setTitle("NJuice");
 }
}