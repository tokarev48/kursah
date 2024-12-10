package vsuet.app.gui;

import vsuet.app.db.DatabaseManager;

import javax.swing.*;
import java.awt.*;

public class RegistrationWindow {
    public static void show() {
        JFrame frame = new JFrame("Регистрация");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        JLabel usernameLabel = new JLabel("Имя пользователя:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Пароль:");
        JPasswordField passwordField = new JPasswordField();

        JLabel roleLabel = new JLabel("Роль:");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Администратор", "Аналитик", "Просмотр"});

        JButton registerButton = new JButton("Зарегистрироваться");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(roleLabel);
        panel.add(roleComboBox);
        panel.add(registerButton);

        frame.add(panel);
        frame.setVisible(true);

        DatabaseManager dbManager = new DatabaseManager();

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            boolean success = dbManager.executeUpdate(query, username, password, role);

            if (success) {
                JOptionPane.showMessageDialog(frame, "Регистрация успешна");
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Ошибка регистрации");
            }
        });
    }
}
