package vsuet.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:company_financials.db";

    private Connection connection;

    public DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(URL);
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("Не найдены дрова SQLite");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к бд");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() {
        createUsersTable();
        createFinancialDataTable();
    }

    private void createUsersTable() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
        } catch (SQLException e) {
            System.err.println("Ошибка создания users");
            e.printStackTrace();
        }
    }

    private void createFinancialDataTable() {
        String createFinancialDataTable = "CREATE TABLE IF NOT EXISTS financial_data (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "company_name TEXT NOT NULL," +
                "assets REAL NOT NULL," +
                "liabilities REAL NOT NULL," +
                "revenue REAL NOT NULL," +
                "expenses REAL NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createFinancialDataTable);
        } catch (SQLException e) {
            System.err.println("Ошибка создания таблы financial_data");
            e.printStackTrace();
        }
    }

    public boolean executeUpdate(String query, Object... params) {
        if (connection == null) {
            System.err.println("Нет подключения к бд");
            return false;
        }
        try (var stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка выполнения запроса: " + query);
            e.printStackTrace();
            return false;
        }
    }

    public java.sql.ResultSet executeQuery(String query, Object... params) {
        if (connection == null) {
            System.err.println("Нет подключения к бд");
            return null;
        }
        try {
            var stmt = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Ошибка выполнения запроса: " + query);
            e.printStackTrace();
            return null;
        }
    }
}
