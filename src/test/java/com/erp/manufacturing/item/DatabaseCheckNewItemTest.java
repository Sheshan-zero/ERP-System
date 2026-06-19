package com.erp.manufacturing.item;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseCheckNewItemTest {
    @Test
    public void testCheckNewItemCreatedDate() throws Exception {
        String dbUrl = "jdbc:oracle:thin:@company_high?TNS_ADMIN=C:/Users/ISHARA/Documents/GitHub/ERP-SystemADBMS/ERP-System-ADBMS/wallet";
        String username = "ADMIN";
        String password = "Himansa@123M";
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {
            System.out.println("Connection successful!");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT item_id, item_name, created_date FROM item WHERE item_id = 21")) {
                if (rs.next()) {
                    long id = rs.getLong("item_id");
                    String name = rs.getString("item_name");
                    java.sql.Timestamp createdDate = rs.getTimestamp("created_date");
                    System.out.println("Item #" + id + ": " + name + " -> Database Created Date: " + createdDate);
                } else {
                    System.out.println("Item #21 not found in database.");
                }
            }
        }
    }
}
