package apps;

import java.sql.ResultSet;
import java.util.Random;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Register extends Stage {
	
	BorderPane bp;
	 GridPane gp;
	 VBox vb;
	 
	 MenuBar menuBar;
	 Menu menu;
	 MenuItem loginMenu, registerMenu;
	 
	 Label registerLbl, njuiceLbl, usernameLbl, passwordLbl, errorLbl;
	 TextField usernameField;
	 PasswordField passwordField;
	 
	 Button registerBtn;
	 
	 CheckBox tnc;
	 
	 ConnectionDB con = new ConnectionDB();
	 Vector<Customer> listUser = new Vector<>();
	 
	 login loginForm = new login();
	 Random rand = new Random();
	 
	 public void init() {
		  gp = new GridPane();
		  vb = new VBox();
		  bp = new BorderPane();
		  
		  menuBar = new MenuBar();
		  menu = new Menu("Dashboard");
		  loginMenu = new MenuItem("Login");
		  registerMenu = new MenuItem("Register");
		  
		  registerLbl = new Label("Register");
		  registerLbl.setStyle("-fx-font-size: 35; -fx-font-weight: bold;");
		  
		  njuiceLbl = new Label("NJuice");
		  usernameLbl = new Label("Username");
		  passwordLbl = new Label("Password");
		  errorLbl = new Label();
		  errorLbl.setStyle("-fx-text-fill: red;");
		  
		  usernameField = new TextField();
		  passwordField = new PasswordField();
		  
		  registerBtn = new Button("Register");
		  
		  tnc = new CheckBox("I agree to the terms and conditions of NJuice!");
	  
	 }

	 public void set() {
		 bp.setTop(menuBar);
		 bp.setCenter(vb);
		 
		 menuBar.getMenus().add(menu);
		 menu.getItems().addAll(loginMenu, registerMenu);
		  
		  //atur posisi komponen di gridpane
		  gp.add(usernameLbl, 0, 0);
		  gp.add(passwordLbl, 0, 2);
		  gp.add(tnc, 0, 4);
		  
		  gp.add(usernameField, 0, 1);
		  gp.add(passwordField, 0, 3);
		  gp.add(errorLbl, 0, 5);
		  
		  vb.getChildren().addAll(registerLbl, njuiceLbl, gp, registerBtn);
	  
	  
	 }
	 
	 public void position() {
	  gp.setPadding(new Insets(15));
	  gp.setHgap(30);
	  gp.setVgap(10);
	  
	  vb.setAlignment(Pos.CENTER);
	  gp.setAlignment(Pos.CENTER);
	 }
	 
	 public void readData() {
		listUser.clear();
		
		 try {
			 String query = "SELECT * FROM msuser";
			 ResultSet rs = con.execQuery(query);
			 
				while(rs.next()) {
					String username = rs.getString("Username");
					String password = rs.getString("Password");
					String role = rs.getString("Role");
					listUser.add(new Customer(username, password, role));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	 }
	 
	 public void refresh() {
		 readData();
		 ObservableList<Customer> regOBS 
		 	= FXCollections.observableArrayList(listUser);
	 }
	 
	 public void addData(String user, String pass, String role) {
		 try {
			String q = String.format("INSERT INTO msuser "
			 		+ "VALUES('%s', '%s', '%s')", user, pass, role);
			con.execUpdate(q);
			} catch (Exception e) {
				e.printStackTrace();
			}
	 }
	 
	 public void btnEvent() {
		 registerBtn.setOnAction(e ->{
			 String user = usernameField.getText();
			 String pass = passwordField.getText();
			 String role = "Customer";
		     boolean termsAccepted = tnc.isSelected();
	
		     if (user.isEmpty() || pass.isEmpty() || !termsAccepted) {
		    	 errorLbl.setText("Please input all the field!");
		     } else {
		    	 errorLbl.setText("");
		    	 
		    	 // check apakah data sudah ada di database
		    	 try {
		                con.openConnection();

		                String inputTaken = "SELECT * FROM msuser WHERE Username = '" + user + "'";
		                ResultSet rs = con.execQuery(inputTaken);

		                if (rs.next()) {
		                    errorLbl.setText("Username is already taken!");
		                } else {
		                    // If the username is not taken, add the data to the database
		                    addData(user, pass, role);
		                    refresh();

		                    Stage loginStage = new Stage();
		                    loginForm.start(loginStage);
		                    this.close(); 
		                }
		                con.closeConnection();
		            } catch (Exception e1) {
		                e1.printStackTrace();
		            }

		            usernameField.clear();
		            passwordField.clear();
		     }
		 });
		}
	 	 
	 public Register() {
		 init();
		 set();
		 position();
		 btnEvent();
		 
		 Scene registerScene = new Scene(bp, 800, 500);
		 this.setScene(registerScene);
		 this.show(); this.setTitle("NJuice");
		 
		// [ Menu ] Register -> Login
		 loginMenu.setOnAction(e -> {
			 try {
				 Stage loginStage = new Stage();
				 loginForm.start(loginStage);
//				 this.close();
			 } catch (Exception e1) {
				 e1.printStackTrace();
			 }
		});
	 }
}
	 
	

