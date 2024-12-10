package vsuet.app.gui;

import vsuet.app.db.UserManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminInterfaceWindow {

    private final UserManager userManager;

    public AdminInterfaceWindow() {
        userManager = new UserManager();
    }

    public void show() {
        JFrame frame = new JFrame("Управление пользователями");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);

        DefaultTableModel tableModel = new DefaultTableModel(
                new String[]{"ID", "Имя пользователя", "Роль"}, 0
        );
        JTable userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        JButton addUserButton = new JButton("Добавить пользователя");
        JButton deleteUserButton = new JButton("Удалить пользователя");
        JButton editRoleButton = new JButton("Изменить роль");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addUserButton);
        buttonPanel.add(deleteUserButton);
        buttonPanel.add(editRoleButton);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        loadUsers(tableModel);

        addUserButton.addActionListener(e -> showAddUserWindow(tableModel));
        deleteUserButton.addActionListener(e -> deleteUser(userTable, tableModel));
        editRoleButton.addActionListener(e -> editUserRole(userTable, tableModel));

        frame.setVisible(true);
    }

    private void loadUsers(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        try (ResultSet rs = userManager.executeQuery("SELECT id, username, role FROM users")) {
            while (rs != null && rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки пользователей.");
            e.printStackTrace();
        }
    }

    private void showAddUserWindow(DefaultTableModel tableModel) {
        JFrame addUserFrame = new JFrame("Добавить пользователя");
        addUserFrame.setSize(400, 300);

        JPanel panel = new JPanel(new GridLayout(4, 2));

        JLabel usernameLabel = new JLabel("Имя пользователя:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Пароль:");
        JPasswordField passwordField = new JPasswordField();

        JLabel roleLabel = new JLabel("Роль:");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Администратор", "Аналитик", "Просмотр"});

        JButton saveButton = new JButton("Сохранить");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(roleLabel);
        panel.add(roleComboBox);
        panel.add(saveButton);

        addUserFrame.add(panel);
        addUserFrame.setVisible(true);

        saveButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            userManager.createUser(username, password, role);
            loadUsers(tableModel);
            addUserFrame.dispose();
        });
    }

    private void deleteUser(JTable userTable, DefaultTableModel tableModel) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            userManager.deleteUser(userId);
            loadUsers(tableModel);
        } else {
            JOptionPane.showMessageDialog(null, "Выберите пользователя для удаления.");
        }
    }

    private void editUserRole(JTable userTable, DefaultTableModel tableModel) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            String currentRole = (String) tableModel.getValueAt(selectedRow, 2);

            JFrame editRoleFrame = new JFrame("Изменить роль");
            editRoleFrame.setSize(300, 200);

            JPanel panel = new JPanel(new GridLayout(2, 2));

            JLabel roleLabel = new JLabel("Новая роль:");
            JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Администратор", "Аналитик", "Просмотр"});
            roleComboBox.setSelectedItem(currentRole);

            JButton saveButton = new JButton("Сохранить");

            panel.add(roleLabel);
            panel.add(roleComboBox);
            panel.add(saveButton);

            editRoleFrame.add(panel);
            editRoleFrame.setVisible(true);

            saveButton.addActionListener(e -> {
                String newRole = (String) roleComboBox.getSelectedItem();
                userManager.updateUserRole(userId, newRole);
                loadUsers(tableModel);
                editRoleFrame.dispose();
            });
        } else {
            JOptionPane.showMessageDialog(null, "Выберите пользователя для изменения роли.");
        }
    }
}
