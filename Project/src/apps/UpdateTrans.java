package apps;

import java.sql.*;
import java.sql.Connection;
import javafx.beans.property.SimpleStringProperty;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class UpdateTrans extends Stage {
	

    TableView<MsJuice> msJuiceTable;
    List<MsJuice> msJuiceList;

    GridPane gp, tableContainer, titleContainer;
    BorderPane root, bp;

    Label titleLb1, mngLbID, mngLbPrice, mngLbPName;
    TextField mngPNameTf;
    TextArea mngDescTf;
    
    MenuBar menuBar;
	Menu menu, menu2;
	MenuItem viewMenu, manageMenu, logoutMenu;

    Spinner<Integer> priceSpinner;

    Button insertBtn, updateBtn, removeBtn;

    ComboBox<String> idBox;

    private String tempId = null;
    
    ConnectionDatabase connect  = ConnectionDatabase.getInstance();;  

    login loginForm;

    public void init() {
    	
        gp = new GridPane();
        tableContainer = new GridPane();
        titleContainer = new GridPane();
        root = new BorderPane();
        bp = new BorderPane();

     	menuBar = new MenuBar();
     	menu = new Menu("Admins' Dashboard");
     	menu2 = new Menu("Logout");
     	viewMenu = new MenuItem("View Transaction");
     	manageMenu = new MenuItem("Manage Products");
     	logoutMenu = new MenuItem("Logout from Admin");
        
        titleLb1 = new Label("Manage Products");
        titleLb1.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        mngLbID = new Label("ProductID\n" + "to delete/remove");
        mngLbPrice = new Label("Price");
        mngLbPName = new Label("Product Name");

        idBox = new ComboBox<String>();

        priceSpinner = new Spinner<>(1000, 1000000, 1000, 100);

        mngPNameTf = new TextField();
        mngPNameTf.setPromptText("insert product name to be created");
        mngDescTf = new TextArea();
        mngDescTf.setPromptText("insert the new product text description, min 10 and max 100");

        insertBtn = new Button("Insert");
        updateBtn = new Button("Update");
        removeBtn = new Button("Remove");

        msJuiceTable = new TableView<>();
        msJuiceList = new Vector<>();
    }

    public void setTable() {
        TableColumn<MsJuice, String> juiceIdColumn = new TableColumn<>("Juice Id");
        juiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("JuiceId"));
        juiceIdColumn.setMinWidth(500 / 4);

        TableColumn<MsJuice, String> juiceNameColumn = new TableColumn<>("Juice Name");
        juiceNameColumn.setCellValueFactory(new PropertyValueFactory<>("JuiceName"));
        juiceNameColumn.setMinWidth(500 / 4);

        TableColumn<MsJuice, Integer> juicePriceColumn = new TableColumn<>("Price");
        juicePriceColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));
        juicePriceColumn.setMinWidth(500 / 4);

        TableColumn<MsJuice, String> juiceDescColumn = new TableColumn<>("Juice Description");
        juiceDescColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getJuiceDesc()));
        juiceDescColumn.setMinWidth(500 / 4);

        msJuiceTable.getColumns().addAll(juiceIdColumn, juiceNameColumn, juicePriceColumn, juiceDescColumn);
        
        List<String> juiceIds = getJuiceIdsFromDatabase();
        idBox.getItems().addAll(juiceIds);
    }
    private List<String> getJuiceIdsFromDatabase() {
        List<String> juiceIds = new ArrayList<>();

        try (Connection con = connect.getConnection()) {
            String query = "SELECT JuiceId FROM MsJuice";
            PreparedStatement st = con.prepareStatement(query);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                juiceIds.add(rs.getString("JuiceId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return juiceIds;
    }

    
    public void getData() {
        ConnectionDatabase connect = ConnectionDatabase.getInstance();
        msJuiceList = connect.execQuery("SELECT * FROM MsJuice");
    }

    public void refreshTable() {
        getData();
        ObservableList<MsJuice> juiceObs = FXCollections.observableArrayList(msJuiceList);
        msJuiceTable.setItems(juiceObs);
    }

    public void addData(String JuiceId, String JuiceName, Integer Price, String JuiceDescription) {
        String query = String.format("INSERT INTO MsJuice VALUES('%s', '%s', %d, '%s')", JuiceId, JuiceName, Price,
                JuiceDescription);
        connect.execUpdate(query);
    }

    public void updateData(String JuiceId, String JuiceName, Integer Price, String JuiceDescription) {
        String query = "UPDATE MsJuice SET JuiceName = ?, Price = ?, JuiceDescription = ? WHERE JuiceId = ?";

        try (Connection con = connect.getConnection()) {
        	PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, JuiceName);
            ps.setInt(2, Price);
            ps.setString(3, JuiceDescription);
            ps.setString(4, JuiceId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void deleteData(String JuiceId) {
        String query = String.format("DELETE FROM MsJuice\n" + "WHERE JuiceId = '%s'", JuiceId);
        connect.execUpdate(query);
    }

    private String getLatestJuiceIdFromDatabase() {
        String latestJuiceId = null;

        try (Connection con = connect.getConnection()) {
            String query = "SELECT JuiceId FROM MsJuice ORDER BY JuiceId DESC LIMIT 1";
            PreparedStatement st = con.prepareStatement(query);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                latestJuiceId = rs.getString("JuiceId");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return latestJuiceId;
    }

    private String generateNextJuiceId(String latestJuiceId) {
        String newJuiceId = null;

        if (latestJuiceId != null && latestJuiceId.matches("JU\\d{3}")) {
            String numericPart = latestJuiceId.substring(2); 
            int numericValue = Integer.parseInt(numericPart);
            numericValue++; 

            
            newJuiceId = "JU" + String.format("%03d", numericValue);
        }

        return newJuiceId;
    }
     
    
    private String generateJuiceId() {
        String latestJuiceId = getLatestJuiceIdFromDatabase();
        String newJuiceId = null;

        if (latestJuiceId != null) {
            newJuiceId = generateNextJuiceId(latestJuiceId);
        }

        return newJuiceId;
    }

	public void setEvent() {
        insertBtn.setOnAction(e -> {
            String JuiceId = generateJuiceId();
            String JuiceName = mngPNameTf.getText();
            Integer Price = Integer.valueOf(priceSpinner.getValue());
            String JuiceDescription = mngDescTf.getText();

            if (JuiceName.isEmpty() || JuiceDescription.length() < 10) {
                showAlertError("Error", "Error", "Please fill all the fields");
            } else {
                addData(JuiceId, JuiceName, Price, JuiceDescription);
                refreshTable();

                mngPNameTf.clear();
                priceSpinner.getValueFactory().setValue(1000);
                mngDescTf.clear();
            }
        });

        msJuiceTable.setOnMouseClicked(e -> {
            TableSelectionModel<MsJuice> selectModel = msJuiceTable.getSelectionModel();
            selectModel.setSelectionMode(SelectionMode.SINGLE);

            MsJuice select = selectModel.getSelectedItem();

            idBox.setPromptText(select.getJuiceId());
            mngPNameTf.setText(select.getJuiceName());
            priceSpinner.getValueFactory().setValue(select.getPrice());
            mngDescTf.setText(select.getJuiceDesc());
            

            tempId = select.getJuiceId();
        });

        updateBtn.setOnAction(e -> {
            String JuiceName = mngPNameTf.getText();
            Integer Price = Integer.valueOf(priceSpinner.getValue());
            String juiceDescription = mngDescTf.getText();

            updateData(tempId, JuiceName, Price, juiceDescription);
            refreshTable();
            mngPNameTf.clear();
            idBox.getSelectionModel().clearSelection();
            idBox.getEditor().clear();
            priceSpinner.getValueFactory().setValue(1000);
            mngDescTf.clear();
        });

        removeBtn.setOnAction(e -> {
            deleteData(tempId);
            refreshTable();
        });

        priceSpinner.getValueFactory().setValue(1000);
    }

	private void showAlertError(String title, String header, String message) {
	    Alert alert = new Alert(Alert.AlertType.ERROR);
	    alert.setTitle(title);
	    alert.setHeaderText(header);
	    alert.setContentText(message);
	    alert.showAndWait();
	}

    public void set() {
    	bp.setTop(menuBar);
    	bp.setCenter(root);
        root.setTop(titleContainer);
        root.setCenter(tableContainer);
        root.setBottom(gp);
        
        menuBar.getMenus().add(menu);
		menuBar.getMenus().add(menu2);
		menu.getItems().addAll(viewMenu, manageMenu);
		menu2.getItems().addAll(logoutMenu);

        gp.add(mngLbID, 0, 0);
        gp.add(mngLbPrice, 0, 1);
        gp.add(mngLbPName, 0, 2);

        gp.add(idBox, 1, 0);
        gp.add(priceSpinner, 1, 1);
        gp.add(mngPNameTf, 1, 2);
        gp.add(mngDescTf, 1, 3);

        gp.add(insertBtn, 2, 0);
        gp.add(updateBtn, 2, 1);
        gp.add(removeBtn, 2, 2);
        tableContainer.add(msJuiceTable, 0, 5, 3, 1);
        titleContainer.add(titleLb1, 0, 0);

        msJuiceTable.setMaxSize(500, 300);
    }

    public void position() {
        titleContainer.setAlignment(Pos.TOP_CENTER);
        tableContainer.setAlignment(Pos.CENTER);
        tableContainer.setPadding(new Insets(20));
        gp.setAlignment(Pos.BOTTOM_CENTER);
        gp.setVgap(15);
        gp.setHgap(10);
        root.setPadding(new Insets(20, 0, 20, 0));
    }
    
    public void navigation() {
		// Admin Dashboard -> login
		logoutMenu.setOnAction(e -> {
			try {
				UserSession.getInstance().clearSession();
				System.out.println(UserSession.getInstance().getUsername());
				 Stage loginStage = new Stage();
				 loginForm = new login();
				 loginForm.start(loginStage);
				 this.close();
			 } catch (Exception e1) {
				 e1.printStackTrace();
			 }
		});
		
		// Admin Dashboard [ view ]
		viewMenu.setOnAction(e->{
			try {
				UpdateTrans update = new UpdateTrans();
				update.show();
				this.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
	}

    public UpdateTrans() {
        init();
        set();
        setTable();
        position();
        setEvent();
        refreshTable();
        Scene scn = new Scene(bp, 800, 600);
        this.setScene(scn); this.setTitle("NJuice");
        this.show();
    }
}
