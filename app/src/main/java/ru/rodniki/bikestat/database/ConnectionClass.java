package ru.rodniki.bikestat.database;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {
    String dbSource = "com.mysql.jdbc.Driver";

    String url = "jdbc:mysql://192.168.0.100/database";
    String user = "root";
    String password = "783";

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName(dbSource);
            conn = DriverManager.getConnection(url, user, password);
            conn = DriverManager.getConnection(ConnURL);

        } catch (SQLException | ClassNotFoundException se){
            Log.e("Error", se.getMessage());
        } catch (Exception e){
            Log.e("Error", e.getMessage());
        }
        return conn;
    }
}
