package vsuet.app.components;

import vsuet.app.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String label;
    private DatabaseManager dbManager;
    private DefaultTableModel tableModel;
    private String actionType;

    public ButtonEditor(JCheckBox checkBox, DatabaseManager dbManager, DefaultTableModel tableModel, String actionType) {
        super(checkBox);
        this.dbManager = dbManager;
        this.tableModel = tableModel;
        this.actionType = actionType;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        label = (value == null) ? "" : value.toString();
        button.setText(label);

        if ("edit".equals(actionType)) {
            int id = (int) tableModel.getValueAt(row, 0);
            editData(row, id, tableModel);
        } else if ("delete".equals(actionType)) {
            int id = (int) tableModel.getValueAt(row, 0);
            deleteData(row, id, tableModel);
        }

        return button;
    }

    private void editData(int row, int id, DefaultTableModel tableModel) {
        JTextField companyNameField = new JTextField((String) tableModel.getValueAt(row, 1));
        JTextField assetsField = new JTextField(tableModel.getValueAt(row, 2).toString());
        JTextField liabilitiesField = new JTextField(tableModel.getValueAt(row, 3).toString());
        JTextField revenueField = new JTextField(tableModel.getValueAt(row, 4).toString());
        JTextField expensesField = new JTextField(tableModel.getValueAt(row, 5).toString());

        JPanel editPanel = new JPanel(new GridLayout(6, 2));
        editPanel.add(new JLabel("Название компании:"));
        editPanel.add(companyNameField);
        editPanel.add(new JLabel("Активы:"));
        editPanel.add(assetsField);
        editPanel.add(new JLabel("Обязательства:"));
        editPanel.add(liabilitiesField);
        editPanel.add(new JLabel("Выручка:"));
        editPanel.add(revenueField);
        editPanel.add(new JLabel("Расходы:"));
        editPanel.add(expensesField);

        int result = JOptionPane.showConfirmDialog(null, editPanel, "Редактировать данные", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String updateQuery = "UPDATE financial_data SET company_name = ?, assets = ?, liabilities = ?, revenue = ?, expenses = ? WHERE id = ?";
            boolean success = dbManager.executeUpdate(updateQuery, companyNameField.getText(), Double.parseDouble(assetsField.getText()), Double.parseDouble(liabilitiesField.getText()), Double.parseDouble(revenueField.getText()), Double.parseDouble(expensesField.getText()), id);

            if (success) {
                tableModel.setValueAt(companyNameField.getText(), row, 1);
                tableModel.setValueAt(Double.parseDouble(assetsField.getText()), row, 2);
                tableModel.setValueAt(Double.parseDouble(liabilitiesField.getText()), row, 3);
                tableModel.setValueAt(Double.parseDouble(revenueField.getText()), row, 4);
                tableModel.setValueAt(Double.parseDouble(expensesField.getText()), row, 5);
                JOptionPane.showMessageDialog(null, "Данные успешно обновлены");
            } else {
                JOptionPane.showMessageDialog(null, "Ошибка обновления данных");
            }
        }
    }

    private void deleteData(int row, int id, DefaultTableModel tableModel) {
        String deleteQuery = "DELETE FROM financial_data WHERE id = ?";
        boolean success = dbManager.executeUpdate(deleteQuery, id);

        if (success) {
            tableModel.removeRow(row);
            JOptionPane.showMessageDialog(null, "Запись успешно удалена");
        } else {
            JOptionPane.showMessageDialog(null, "Ошибка удаления записи");
        }
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
