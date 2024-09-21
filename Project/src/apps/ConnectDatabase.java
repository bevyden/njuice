package apps;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class ConnectDatabase {
	private final String USERNAME = "root";
	private final String PASSWORD = "";
	private final String DATABASE = "njuice";
	private final String HOST = "localhost:3306";
	private final String CONNECTION =
			String.format("jdbc:mysql://%s/%s", HOST, DATABASE);
	
		public ResultSet rs;
		
		public ResultSetMetaData rsm;
		
		private java.sql.Connection con;
		
		private Statement st;
		
		private static ConnectDatabase instance;
		
		public static ConnectDatabase getInstance() { 
			if(instance == null) {
				return new ConnectDatabase();
			}
			return instance;
		}
		
		public ConnectDatabase() {
			try { 
				Class.forName("com.mysql.cj.jdbc.Driver");
				con = DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD);
				st = con.createStatement();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Connection Failed!");
			}
		}
		
		public ResultSet executeQuery (String query) {
			try{
				rs = st.executeQuery(query);
				rsm = rs.getMetaData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return rs;
		}
		
		// method untuk eksekusi query yang tidak
		public void executeUpdate(String query) {
			try {
				st.executeUpdate(query);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void close(){
			try {
				st.close();
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
