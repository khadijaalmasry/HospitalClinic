package com.mycompany.hospitalclinic;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class for managing Oracle Database connections.
 * Connection details can be overridden in src/main/resources/db.properties.
 */
public class DBConnection {
    private static String url = "jdbc:oracle:thin:@localhost:1522:XE";
    private static String user = "SYSTEM";
    private static String password = "oracle";

    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                url = prop.getProperty("db.url", url);
                user = prop.getProperty("db.user", user);
                password = prop.getProperty("db.password", password);
            }
        } catch (Exception e) {
            // Fallback to default values
        }
    }

    /**
     * Obtains a new database Connection.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
