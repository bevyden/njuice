package apps;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionDatabase {

    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private final String DATABASE = "njuice";
    private final String HOST = "localhost:3306";
    private final String CONNECTION = String.format("jdbc:mysql://%s/%s", HOST, DATABASE);

    private Connection con;
    private static ConnectionDatabase connect;

    private ConnectionDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConnectionDatabase getInstance() {
        if (connect == null) connect = new ConnectionDatabase();
        return connect;
    }

    public List<MsJuice> execQuery(String query) {
        List<MsJuice> result = new ArrayList<>();

        try (Connection con = getConnection(); Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String JuiceId = rs.getString("JuiceId");
                String JuiceName = rs.getString("JuiceName");
                Integer Price = rs.getInt("Price");
                String JuiceDescription = rs.getString("JuiceDescription");

                result.add(new MsJuice(JuiceId, JuiceName, Price, JuiceDescription));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void execUpdate(String query) {
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}

