package vsuet.app.gui;

import vsuet.app.components.ButtonEditor;
import vsuet.app.components.ButtonRenderer;
import vsuet.app.db.DatabaseManager;
import vsuet.app.utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FinancialDataManagerWindow {
    public static void show() {
        JFrame frame = new JFrame("Управление данными");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Добавить данные");
        topPanel.add(addButton);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Компания", "Активы", "Долговые обязательства", "Выручка", "Расходы", "Редактировать", "Удалить"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        frame.add(panel);
        frame.setVisible(true);

        DatabaseManager dbManager = new DatabaseManager();

        loadTableData(dbManager, tableModel);

        table.getColumn("Редактировать").setCellRenderer(new ButtonRenderer());
        table.getColumn("Редактировать").setCellEditor(new ButtonEditor(new JCheckBox(), dbManager, tableModel, "edit"));

        table.getColumn("Удалить").setCellRenderer(new ButtonRenderer());
        table.getColumn("Удалить").setCellEditor(new ButtonEditor(new JCheckBox(), dbManager, tableModel, "delete"));

        addButton.addActionListener(e -> addData(dbManager, tableModel));
    }

    private static void loadTableData(DatabaseManager dbManager, DefaultTableModel tableModel) {
        String query = "SELECT * FROM financial_data";
        tableModel.setRowCount(0);
        try (ResultSet rs = dbManager.executeQuery(query)) {
            while (rs != null && rs.next()) {
                Object[] rowData = {
                        rs.getInt("id"),
                        rs.getString("company_name"),
                        rs.getDouble("assets"),
                        rs.getDouble("liabilities"),
                        rs.getDouble("revenue"),
                        rs.getDouble("expenses"),
                        "Редактировать",
                        "Удалить"
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки данных из базы");
            e.printStackTrace();
        }
    }

    private static void addData(DatabaseManager dbManager, DefaultTableModel tableModel) {
        JFrame frame = new JFrame("Добавить данные");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Название компании:"), c);

        c.gridx = 1;
        JTextField companyNameField = new JTextField();
        panel.add(companyNameField, c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(new JLabel("Например: ООО Ромашка"), c);

        c.gridx = 0;
        c.gridy = 2;
        panel.add(new JLabel("Активы:"), c);

        c.gridx = 1;
        JTextField assetsField = new JTextField();
        panel.add(assetsField, c);

        c.gridx = 0;
        c.gridy = 3;
        panel.add(new JLabel("Сумма всех активов компании, например, 1000000"), c);

        c.gridx = 0;
        c.gridy = 4;
        panel.add(new JLabel("Долговые обязательства:"), c);

        c.gridx = 1;
        JTextField liabilitiesField = new JTextField();
        panel.add(liabilitiesField, c);

        c.gridx = 0;
        c.gridy = 5;
        panel.add(new JLabel("Сумма всех долгов компании, например, 500000"), c);

        c.gridx = 0;
        c.gridy = 6;
        panel.add(new JLabel("Выручка:"), c);

        c.gridx = 1;
        JTextField revenueField = new JTextField();
        panel.add(revenueField, c);

        c.gridx = 0;
        c.gridy = 7;
        panel.add(new JLabel("Доход компании за период, например, 2000000"), c);

        c.gridx = 0;
        c.gridy = 8;
        panel.add(new JLabel("Расходы:"), c);

        c.gridx = 1;
        JTextField expensesField = new JTextField();
        panel.add(expensesField, c);

        c.gridx = 0;
        c.gridy = 9;
        panel.add(new JLabel("Затраты компании за период, например, 1500000"), c);

        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        c.gridx = 0;
        c.gridy = 10;
        panel.add(saveButton, c);

        c.gridx = 1;
        panel.add(cancelButton, c);

        frame.add(panel);
        frame.setVisible(true);

        saveButton.addActionListener(e -> {
            String companyName = companyNameField.getText();
            String assets = assetsField.getText();
            String liabilities = liabilitiesField.getText();
            String revenue = revenueField.getText();
            String expenses = expensesField.getText();

            if (companyName.isEmpty() || assets.isEmpty() || liabilities.isEmpty() || revenue.isEmpty() || expenses.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Пожалуйста, заполните все поля");
                return;
            }

            if (!Utils.isNumeric(assets) || !Utils.isNumeric(liabilities) || !Utils.isNumeric(revenue) || !Utils.isNumeric(expenses)) {
                JOptionPane.showMessageDialog(frame, "Поля 'Активы', 'Долговые обязательства', 'Выручка' и 'Расходы' должны быть числами");
                return;
            }

            String query = "INSERT INTO financial_data (company_name, assets, liabilities, revenue, expenses) VALUES (?, ?, ?, ?, ?)";
            boolean success = dbManager.executeUpdate(query, companyName, Double.parseDouble(assets), Double.parseDouble(liabilities), Double.parseDouble(revenue), Double.parseDouble(expenses));

            if (success) {
                JOptionPane.showMessageDialog(frame, "Данные успешно добавлены");
                loadTableData(dbManager, tableModel);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Ошибка добавления данных");
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());
    }

}
