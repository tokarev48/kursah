package vsuet.app.gui;

import vsuet.app.db.DatabaseManager;
import vsuet.app.models.User;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;

public class LoginWindow {
    public static void show() {
        JFrame frame = new JFrame("Вход");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel usernameLabel = new JLabel("Имя пользователя:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Пароль:");
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Войти");
        JButton registerButton = new JButton("Регистрация");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);

        frame.add(panel);
        frame.setVisible(true);

        DatabaseManager dbManager = new DatabaseManager();

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (ResultSet rs = dbManager.executeQuery(query, username, password)) {
                if (rs != null && rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                    frame.dispose();
                    MainAppWindow.show(user);
                } else {
                    JOptionPane.showMessageDialog(frame, "Неверные данные");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Ошибка входа");
                ex.printStackTrace();
            }
        });

        registerButton.addActionListener(e -> {
            RegistrationWindow.show();
        });
    }
}
