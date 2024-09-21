package apps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionDB {
	Connection con;
	Statement st;

	public void openConnection() throws Exception{
		try { 
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/njuice", "root", "");
			st = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Connection Failed!");
		}
	}
	
	// untuk select
	public ResultSet execQuery(String query) throws Exception{
		return st.executeQuery(query);
		
	}
	
	// untuk insert, update, delete
	public void execUpdate(String query) throws Exception{
		st.executeUpdate(query);
	}
	
	public void closeConnection() throws Exception{
		st.close();
		con.close();
	}
	
	public PreparedStatement prepareStatement(String query) throws SQLException {
        return con.prepareStatement(query);
    }
}
