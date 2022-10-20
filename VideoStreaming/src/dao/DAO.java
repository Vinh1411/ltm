package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DAO {
    public static Connection con;

    public DAO() {
        if (con == null) {
            String dbUrl = "jdbc:mysql://localhost:3306/ltm?autoReconnect=true&useSSL=false";
            String dbClass = "com.mysql.cj.jdbc.Driver";
            try {
                //Khoi tao trinh dieu khien JDBC theo duong dan da chi dinh
                Class.forName(dbClass);
                //Tạo một kết nối
                con = DriverManager.getConnection(dbUrl, "root", "vinhcoi123");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
