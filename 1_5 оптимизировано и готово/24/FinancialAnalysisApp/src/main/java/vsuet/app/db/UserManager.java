package vsuet.app.db;

import java.sql.*;

public class UserManager {

    private Connection connection;

    public UserManager() {
        connection = new DatabaseManager().getConnection();
        if (connection == null) {
            System.err.println("Нет подключения к бд");
        }
    }

    public boolean executeUpdate(String query, Object... params) {
        if (connection == null) {
            System.err.println("Нет подключения к бд");
            return false;
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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

    public ResultSet executeQuery(String query, Object... params) {
        if (connection == null) {
            System.err.println("Нет подключения к бД");
            return null;
        }

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            // Отладка: выводим запрос перед выполнением
            System.out.println("Выполняется запрос: " + query);
            return stmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Ошибка выполнения запроса: " + query);
            e.printStackTrace();
            return null;
        }
    }

    public void createUser(String username, String password, String role) {
        String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        boolean success = executeUpdate(insertQuery, username, password, role);
        if (success) {
            System.out.println("Пользователь " + username + " успешно добавлен.");
        } else {
            System.err.println("Ошибка добавления пользователя " + username);
        }
    }

    public String getUserRole(String username) {
        String query = "SELECT role FROM users WHERE username = ?";
        try (ResultSet rs = executeQuery(query, username)) {
            if (rs != null && rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении роли пользователя: " + username);
            e.printStackTrace();
        }
        return null;
    }

    public void deleteUser(int userId) {
        String query = "DELETE FROM users WHERE id = ?";
        executeUpdate(query, userId);
    }

    public void updateUserRole(int userId, String newRole) {
        String query = "UPDATE users SET role = ? WHERE id = ?";
        executeUpdate(query, newRole, userId);
    }
}
