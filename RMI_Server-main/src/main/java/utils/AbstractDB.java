package utils;

import java.sql.*;

public class AbstractDB{
    public static Connection getConnection(){
        try {
            String url = "jdbc:sqlite:D:\\CUOIKY\\RMI_Server-main\\db\\qlvt.db";
            return DriverManager.getConnection(url);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

}
