package vsuet.app.gui;

import vsuet.app.models.User;

import javax.swing.*;
import java.awt.*;

public class MainAppWindow {
    public static void show(User user) {
        JFrame frame = new JFrame("Система оценки финансовой стабильности");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JLabel welcomeLabel = new JLabel("Добро пожаловать, " + user.getUsername() + " (" + user.getRole() + ")");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(welcomeLabel, BorderLayout.NORTH);

        if ("Администратор".equals(user.getRole())) {
            JButton manageUsersButton = new JButton("Управление пользователями");
            manageUsersButton.addActionListener(e -> {
                AdminInterfaceWindow adminInterfaceWindow = new AdminInterfaceWindow();
                adminInterfaceWindow.show();
            });
            panel.add(manageUsersButton, BorderLayout.CENTER);
        } else if ("Аналитик".equals(user.getRole()) || "Просмотр".equals(user.getRole())) {
            JPanel userPanel = new JPanel();
            userPanel.setLayout(new GridLayout(2, 1));

            JButton manageDataButton = new JButton("Управление данными");
            manageDataButton.addActionListener(e -> FinancialDataManagerWindow.show());
            if ("Просмотр".equals(user.getRole())) {
                manageDataButton.setEnabled(false);
            }
            userPanel.add(manageDataButton);

            JButton calculationsButton = new JButton("Просмотр отчетов");
            calculationsButton.addActionListener(e -> FinancialCalculationsWindow.show());
            if ("Просмотр".equals(user.getRole())) {
                calculationsButton.setEnabled(true);
            }
            userPanel.add(calculationsButton);

            panel.add(userPanel, BorderLayout.CENTER);
        }

        frame.add(panel);
        frame.setVisible(true);
    }
}
