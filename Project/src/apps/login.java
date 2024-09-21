package apps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class login extends Application {
	Stage st; 
	Scene scene;
	 
	 BorderPane bp;
	 GridPane gp;
	 FlowPane fp;
	 VBox vb;
	 
	 MenuBar menuBar;
	 Menu menu;
	 MenuItem loginMenu;
	 MenuItem registerMenu;
	 
	 Label loginLbl, njuiceLbl, usernameLbl, passwordLbl, errorLbl;
	 TextField usernameField;
	 PasswordField passwordField;
	 
	 ConnectionDB con = new ConnectionDB();
 
	 Button loginBtn;
	 
	 public void init() {
		 bp = new BorderPane();
		 gp = new GridPane();
		 vb = new VBox();
		  
		  menuBar = new MenuBar();
		  menu = new Menu("Dashboard");
		  loginMenu = new MenuItem("Login");
		  registerMenu = new MenuItem("Register");
		  errorLbl = new Label();
		  errorLbl.setStyle("-fx-text-fill: red;");	
		  
		  loginLbl = new Label("Login");
		  loginLbl.setStyle("-fx-font-size: 35; -fx-font-weight: bold;");
		  
		  njuiceLbl = new Label("NJuice");
		  usernameLbl = new Label("Username");
		  passwordLbl = new Label("Password");
		  
		  usernameField = new TextField();
		  passwordField = new PasswordField();
		  
		  loginBtn = new Button("Login");
	  
	 }
	
	 public void set() {
		  bp.setCenter(vb);
		  bp.setTop(menuBar);
		  
		  menuBar.getMenus().add(menu);
		  menu.getItems().addAll(loginMenu, registerMenu);
		  
		  //atur posisi komponen di gridpane
		  gp.add(usernameLbl, 0, 0);
		  gp.add(passwordLbl, 0, 2);
		  
		  gp.add(usernameField, 0, 1);
		  gp.add(passwordField, 0, 3);
		  gp.add(errorLbl, 0, 4);
		  
		  vb.getChildren().addAll(loginLbl, njuiceLbl, gp, loginBtn);
	 }
	 
	 public void position() {
		  gp.setPadding(new Insets(15));
		  gp.setHgap(30);
		  gp.setVgap(10);
		  
		  vb.setAlignment(Pos.CENTER);
		  gp.setAlignment(Pos.CENTER);
	 }
	 
	 public void validate() {
		 loginBtn.setOnAction(e -> {
		     String user = usernameField.getText();
		     String pass = passwordField.getText();
		     
		     // Input ada yang kosong
		     if (user.isEmpty() || pass.isEmpty()) {
		    	 errorLbl.setText("Please input all the field!");
		    	 	    	 
		     } else {
		    	 errorLbl.setText("");
		    	try {
		    		 con.openConnection();
		    		 ResultSet rs = con.execQuery("Select * from msuser where Username = '" + user + "' AND Password = '" + pass + "'");
		    		 
		    		 // Check data match sama database
		    		 if (rs.next()) {
		    			checkRole();
		    		 }else {
		    			 errorLbl.setText("Credentials Failed!");
		    		 }
		    		    con.closeConnection();
		    	} catch (Exception e1) {
		    		    e1.printStackTrace();
		    	}
		     }
		 });
	 }
	 
	 public void checkRole() {
		 try {
			con.openConnection();
			
			ResultSet rsRole  
				= con.execQuery("SELECT Role FROM msuser WHERE Username='"+usernameField.getText()+"' and Password = '"+passwordField.getText()+"'");
			
			if (rsRole.next()) {
	            String roleCust = rsRole.getString("Role");

	            if ("Customer".equals(roleCust)) {
	            	UserSession.getInstance().setUser(usernameField.getText(), roleCust);
	            	System.out.println(UserSession.getInstance().getUsername());
	            	CustomerHome custHome = new CustomerHome();
	            	custHome.show();
	            	st.close();
	            } else if("Admin".equals(roleCust)){
	            	UserSession.getInstance().setUser(usernameField.getText(), roleCust);
	            	System.out.println(UserSession.getInstance().getUsername());
	            	ViewTrans adminHome = new ViewTrans();
	            	adminHome.ViewTrans();
	            	st.close();
	            }
	        } else {
	            errorLbl.setText("Credentials Failed!");
	        }
			con.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	 
	 public static void main(String[] args) {
			launch(args);
			
	 }
	 
	 @Override
	 public void start(Stage st) throws Exception {
		 this.st = st;
		 init();
		 set();
		 position();
		 validate();
		 
		 Scene sc = new Scene(bp, 800, 500);
		 st.setTitle("NJuice");
		 st.setScene(sc);
		 st.show(); st.setTitle("NJuice");
			
		 // [ Menu ] Login -> Register
		 registerMenu.setOnAction(e -> {
			 st.close();
			 System.out.println("Help");
		     Register regist = new Register();
		     regist.show(); 
		 });
	 }
}