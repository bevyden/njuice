package apps;

import java.sql.ResultSet;
import java.util.Vector;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewTrans extends Stage{
	Scene scene;
	BorderPane root;
	VBox vb;
	
	MenuBar menuBar;
	Menu menu, menu2;
	MenuItem viewMenu, manageMenu, logoutMenu;
	
	Label viewLbl, placeholderLabel;
			
	ListView<String> productLV;
			
	TableView<transHeader> transHeaderTable;
	TableView<transDetail> transDetailTable;
	
	 ConnectionDB con = new ConnectionDB();
	 
	 Vector<transHeader> thList = new Vector<>();
	 Vector<transDetail> tdList = new Vector<>();
	 
	 login loginForm = new login();
	
	public void init() {
		root = new BorderPane();
		vb = new VBox();
		
		// Menu
		menuBar = new MenuBar();
		menu = new Menu("Admins' Dashboard");
		menu2 = new Menu("Logout");
		viewMenu = new MenuItem("View Transaction");
		manageMenu = new MenuItem("Manage Products");
		logoutMenu = new MenuItem("Logout from Admin");
		
		placeholderLabel = new Label("Click on a transaction header to view the transaction detail");
		
		viewLbl = new Label("View Transaction");
		viewLbl.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
		
		transHeaderTable = new TableView<>();
		transDetailTable = new TableView<>();
		
		if (transDetailTable.getItems().isEmpty()) {
			transDetailTable.setPlaceholder(placeholderLabel);
		}
	}


	public void set() {
		root.setCenter(vb);
		root.setTop(menuBar);
		
		// menu
		menuBar.getMenus().add(menu);
		menuBar.getMenus().add(menu2);
		menu.getItems().addAll(viewMenu, manageMenu);
		menu2.getItems().addAll(logoutMenu);
		
		//  Set TableView Size
		transHeaderTable.setMaxSize(300, 220);
		transDetailTable.setMaxSize(450, 200);
		
		// Add komponen di VBox
		vb.getChildren().addAll(viewLbl, transHeaderTable, transDetailTable);
		
	}
	
	public void position() {
		vb.setPadding(new Insets(50));
		
		vb.setSpacing(20);
        vb.setAlignment(Pos.CENTER);
		
	}
	

	public void setTransHeaderTable() {
		// kasih nama column 
		// nama, address
		TableColumn<transHeader, String> transIdColumn = new TableColumn<transHeader, String>("TransactionId");
		transIdColumn.setCellValueFactory(new PropertyValueFactory<transHeader, String>("TransactionId"));
		transIdColumn.setMinWidth(300 / 3);
		
		TableColumn<transHeader, String> paymentTypeColumn = new TableColumn<transHeader, String>("PaymentType"); 
		paymentTypeColumn.setCellValueFactory(new PropertyValueFactory<transHeader, String>("PaymentType"));
		paymentTypeColumn.setMinWidth(300 / 3);
		
		TableColumn<transHeader, String> usernameColumn = new TableColumn<transHeader, String>("Username"); 
		usernameColumn.setCellValueFactory(new PropertyValueFactory<transHeader, String>("Username"));
		usernameColumn.setMinWidth(300 / 3);
		
		transHeaderTable.getColumns().addAll(transIdColumn, paymentTypeColumn, usernameColumn);
	
	}
	
	public void setTransDetailTable() {
		// kasih nama column 
		// nama, address
		TableColumn<transDetail, String> transIdColumn2 = new TableColumn<transDetail, String>("TransactionId");
		transIdColumn2.setCellValueFactory(new PropertyValueFactory<transDetail, String>("TransactionId"));
		transIdColumn2.setMinWidth(400 / 4);
		
		TableColumn<transDetail, String> juiceIdColumn = new TableColumn<transDetail, String>("JuiceId");
		juiceIdColumn.setCellValueFactory(new PropertyValueFactory<transDetail, String>("JuiceId"));
		juiceIdColumn.setMinWidth(400 / 4);

		TableColumn<transDetail, String> juiceNameColumn = new TableColumn<transDetail, String>("JuiceName");
		juiceNameColumn.setCellValueFactory(new PropertyValueFactory<transDetail, String>("JuiceName"));
		juiceNameColumn.setMinWidth(400 / 4);

		TableColumn<transDetail, Integer> qtyColumn = new TableColumn<transDetail, Integer>("Quantity");
		qtyColumn.setCellValueFactory(new PropertyValueFactory<transDetail, Integer>("Quantity"));
		qtyColumn.setMinWidth(400 / 4);
		
		
		transDetailTable.getColumns().addAll(transIdColumn2, juiceIdColumn, juiceNameColumn, qtyColumn);
		
	}
	
	public void getDataTH() {
		  thList.clear();
		  try {
			  String query = "SELECT * FROM transactionHeader ";
			  con.openConnection();
			  ResultSet rs = con.execQuery(query);
			  while (rs.next()) {
				  String trId = rs.getString("TransactionId");
				  String payType = rs.getString("PaymentType");
				  String username = rs.getString("Username");
				  
				  thList.add(new transHeader(trId, payType, username));
			  }
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
	}
		 
		public void refreshTableTH() {
			getDataTH();
			ObservableList<transHeader> regObs = FXCollections.observableArrayList(thList);
			transHeaderTable.setItems(regObs);
		}
		
		 public void showTransDetail() {
			 System.out.println("Entering showTransDetail method");
			   TableSelectionModel<transHeader> selectionModel = transHeaderTable.getSelectionModel();
			   selectionModel.setSelectionMode(SelectionMode.SINGLE);
			   
			   transHeaderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
		            if (newSelection != null) {
		                // Ambil value dari item yang selected
		                String selectedTrId = newSelection.getTransactionId();
		                
		                tdList.clear(); 
		                try {
							con.openConnection();
							ResultSet rsTrDetail = con.execQuery("SELECT * FROM transactionDetail JOIN msjuice WHERE TransactionId='"+selectedTrId+"'");
							
							while (rsTrDetail.next()) {
								  String trId = rsTrDetail.getString("TransactionId");
								  String juiceId = rsTrDetail.getString("JuiceId");
								  String juiceName = rsTrDetail.getString("JuiceName");
								  Integer qty = rsTrDetail.getInt("Quantity");
								  
								  tdList.add(new transDetail(trId, juiceId, juiceName, qty));
							}
		                }catch (Exception e1) {
							e1.printStackTrace();
						}finally {
			                try {
			                	con.closeConnection();
			                } catch (Exception e3) {
			                    e3.printStackTrace();
			                }
						}
		                ObservableList<transDetail> regObs2 = FXCollections.observableArrayList(tdList);
		    			transDetailTable.setItems(regObs2);
		            // kalau pada transHeader, juice blm selected
			        }else {
						transDetailTable.setPlaceholder(placeholderLabel);
					}
			   });
//		  });
		 }
		 
		 
		 
	public void navigation() {
		// Admin Dashboard -> login
		logoutMenu.setOnAction(e -> {
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
		
		// Admin Dashboard [ update ]
		manageMenu.setOnAction(e->{
			try {
				UpdateTrans update = new UpdateTrans();
				update.show();
				this.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
	}

	
	public void ViewTrans() {
		init(); set();
		position();
		
		setTransHeaderTable(); 
		setTransDetailTable(); 
		
		refreshTableTH();
		showTransDetail(); 
		
		navigation();
		
		Scene adminHome = new Scene(root, 800, 500);
		this.setScene(adminHome);
		this.show(); this.setTitle("NJuice");
		
	}
}


