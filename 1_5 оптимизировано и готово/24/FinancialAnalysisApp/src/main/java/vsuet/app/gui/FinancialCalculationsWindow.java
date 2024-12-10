package vsuet.app.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.data.category.DefaultCategoryDataset;
import vsuet.app.db.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FinancialCalculationsWindow {
    public static void show() {
        JFrame frame = new JFrame("Финансовые расчёты");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1200, 800);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JButton reportButton = new JButton("Посмотреть отчёт");
        reportButton.addActionListener(e -> showReport());
        panel.add(reportButton, BorderLayout.SOUTH);

        JPanel chartPanel = createChartPanel();
        JScrollPane chartScrollPane = new JScrollPane(chartPanel);
        panel.add(chartScrollPane, BorderLayout.CENTER);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static JPanel createChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DatabaseManager dbManager = new DatabaseManager();

        String query = "SELECT * FROM financial_data";
        try (ResultSet rs = dbManager.executeQuery(query)) {
            while (rs != null && rs.next()) {
                String companyName = rs.getString("company_name");
                double assets = rs.getDouble("assets");
                double liabilities = rs.getDouble("liabilities");
                double revenue = rs.getDouble("revenue");
                double expenses = rs.getDouble("expenses");

                double roa = (revenue - expenses) / assets;
                double ros = revenue != 0 ? (revenue - expenses) / revenue : 0;

                dataset.addValue(roa, "Рентабельность активов (ROA)", companyName);
                dataset.addValue(ros, "Рентабельность продаж (ROS)", companyName);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка загрузки данных для графика");
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Финансовые показатели компаний",
                "Компания",
                "Показатель (в долях)",
                dataset
        );

        chart.getCategoryPlot().setNoDataMessage("Нет данных для отображения");
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.setBackgroundPaint(Color.WHITE);

        return new ChartPanel(chart);
    }

    private static void showReport() {
        StringBuilder report = new StringBuilder("<html><h2>Отчёт о финансовых показателях:</h2><br>");
        DatabaseManager dbManager = new DatabaseManager();

        String query = "SELECT * FROM financial_data";
        try (ResultSet rs = dbManager.executeQuery(query)) {
            while (rs != null && rs.next()) {
                String companyName = rs.getString("company_name");
                double assets = rs.getDouble("assets");
                double liabilities = rs.getDouble("liabilities");
                double revenue = rs.getDouble("revenue");
                double expenses = rs.getDouble("expenses");

                double roa = (revenue - expenses) / assets;
                double ros = revenue != 0 ? (revenue - expenses) / revenue : 0;

                report.append("<b>").append(companyName).append(":</b><br>");
                report.append("Рентабельность активов (ROA): ").append(String.format("%.2f", roa)).append("<br>");
                report.append("Рентабельность продаж (ROS): ").append(String.format("%.2f", ros)).append("<br>");

                report.append("<b>Вывод и рекомендации:</b><br>");

                if (roa < 0.05) {
                    report.append("ROA слишком низкая. Компания неэффективно использует свои активы. Рекомендуется оптимизировать использование активов и улучшить эффективность.<br>");
                } else if (roa >= 0.05 && roa < 0.1) {
                    report.append("ROA на уровне средней эффективности. Рекомендуется улучшить использование активов для повышения рентабельности.<br>");
                } else {
                    report.append("ROA высокая. Компания эффективно использует свои активы. Продолжайте в том же духе.<br>");
                }

                if (ros < 0.05) {
                    report.append("ROS низкая. Компания должна пересмотреть свою ценовую политику или снизить затраты, чтобы улучшить прибыльность.<br>");
                } else if (ros >= 0.05 && ros < 0.1) {
                    report.append("ROS на среднем уровне. Возможности для улучшения есть, возможно стоит снизить операционные расходы.<br>");
                } else {
                    report.append("ROS высокая. Компания показывает хорошую прибыльность на каждую проданную единицу. Рекомендуется поддерживать текущую стратегию.<br>");
                }

                double currentLiquidity = assets / liabilities;
                double autonomyRatio = (assets - liabilities) / assets;

                if (currentLiquidity < 1) {
                    report.append("Текущая ликвидность меньше 1. Рекомендуется улучшить управление оборотным капиталом.<br>");
                } else {
                    report.append("Текущая ликвидность хорошая. Компания в состоянии покрыть краткосрочные обязательства.<br>");
                }

                if (autonomyRatio < 0.2) {
                    report.append("Коэффициент автономии низкий. Компания зависит от внешнего финансирования. Рекомендуется увеличить собственный капитал.<br>");
                } else {
                    report.append("Коэффициент автономии в пределах нормы. Компания имеет хороший уровень финансовой независимости.<br>");
                }

                report.append("<br>");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке данных для отчёта");
        }

        report.append("</html>");

        JFrame reportFrame = new JFrame("Отчёт");
        reportFrame.setSize(400, 600);
        JTextPane reportText = new JTextPane();
        reportText.setContentType("text/html");
        reportText.setText(report.toString());
        reportText.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportText);
        reportFrame.add(scrollPane);
        reportFrame.setVisible(true);
    }

}
