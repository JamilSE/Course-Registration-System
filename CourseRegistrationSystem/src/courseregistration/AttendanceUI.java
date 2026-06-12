import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AttendanceUI extends JFrame {
    private String rollNo, dept, batch;
    private JTable table;
    private JPanel chartPanel;

    public AttendanceUI(String rollNo, String dept, String batch) {
        this.rollNo = rollNo;
        this.dept = dept;
        this.batch = batch;

        setTitle("Attendance Overview");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 600));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 40, 40));

        sidebar.add(Box.createVerticalStrut(40));
        sidebar.add(createInfoLabel("Roll Number: " + rollNo));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createInfoLabel("Department: " + dept));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createInfoLabel("Batch: " + batch));
        sidebar.add(Box.createVerticalStrut(50));

        JButton backBtn = createSidebarButton("Back to Home");
        backBtn.addActionListener(e -> {
            new Dashboard(rollNo, dept, batch).setVisible(true);
            dispose();
        });
        sidebar.add(backBtn);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Attendance Summary", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(title, BorderLayout.NORTH);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 150));

        chartPanel = new JPanel();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(chartPanel);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        loadAttendance();
    }

    private void loadAttendance() {
        String targetBatch = getAttendanceBatch(batch);

        if (targetBatch.isEmpty()) {
            // Clear table and setup chartPanel with image above and text below centered
            table.setModel(new javax.swing.table.DefaultTableModel());  // clear table
            chartPanel.removeAll();
            chartPanel.setLayout(new GridBagLayout());

            JPanel messageBox = new JPanel();
            messageBox.setLayout(new BoxLayout(messageBox, BoxLayout.Y_AXIS));
            messageBox.setOpaque(false);
            messageBox.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                    BorderFactory.createEmptyBorder(20, 40, 20, 40)
            ));

            ImageIcon icon = null;
            try {
                icon = new ImageIcon(getClass().getResource("/images/server.png"));
                if (icon != null && icon.getIconWidth() > 0) {
                    Image img = icon.getImage();
                    Image scaled = img.getScaledInstance(150, -1, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaled);
                } else {
                    icon = null;
                }
            } catch (Exception e) {
                icon = null;
            }

            JLabel imageLabel = new JLabel();
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            if (icon != null) {
                imageLabel.setIcon(icon);
            } else {
                imageLabel.setText("Image not available");
                imageLabel.setForeground(Color.RED);
            }

            JLabel message = new JLabel("<html><div style='text-align: center;'>"
                    + "Attendance data for your current batch is not available at this time.<br>"
                    + "Please check back later or contact the administration for more information."
                    + "</div></html>", JLabel.CENTER);
            message.setFont(new Font("Arial", Font.BOLD, 16));
            message.setForeground(new Color(70, 70, 70));
            message.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
            message.setAlignmentX(Component.CENTER_ALIGNMENT);

            messageBox.add(imageLabel);
            messageBox.add(message);

            chartPanel.add(messageBox);
            chartPanel.revalidate();
            chartPanel.repaint();
            return;
        }

        List<String> subjects = getCoursesByDeptAndBatch(dept, targetBatch);
        String[] columns = {"Subject", "Total Classes", "Classes Attended", "Attendance %"};
        Object[][] data = new Object[subjects.size()][4];

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < subjects.size(); i++) {
            int total = 20 + new Random().nextInt(11);
            int attended = 10 + new Random().nextInt(total - 9);
            int percent = (int) ((attended / (double) total) * 100);

            data[i][0] = subjects.get(i);
            data[i][1] = total;
            data[i][2] = attended;
            data[i][3] = percent + "%";

            dataset.addValue(percent, "Attendance", subjects.get(i));
        }

        table.setModel(new javax.swing.table.DefaultTableModel(data, columns));
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String val = value.toString().replace("%", "");
                int perc = Integer.parseInt(val);
                if (perc >= 75) c.setForeground(new Color(0, 128, 0));
                else if (perc >= 60) c.setForeground(new Color(255, 165, 0));
                else c.setForeground(Color.RED);
                return c;
            }
        });

        JFreeChart barChart = ChartFactory.createBarChart(
                "Attendance % by Subject", "Subject", "Percentage",
                dataset
        );

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setRangeGridlinePaint(Color.BLACK);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(72, 118, 255));

        ChartPanel chart = new ChartPanel(barChart);
        chart.setPreferredSize(new Dimension(700, 300));

        chartPanel.removeAll();
        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(chart, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private String getAttendanceBatch(String userBatch) {
        return switch (userBatch.toUpperCase()) {
            case "2024F", "2023F" -> "";
            case "2022F" -> "2021F";
            case "2021F" -> "2022F";
            default -> "";
        };
    }

    private List<String> getCoursesByDeptAndBatch(String dept, String batch) {
        dept = dept.trim().toLowerCase();
        batch = batch.trim().toUpperCase();

        if (dept.equals("software engineering") || dept.equals("computer science")) {
            return switch (batch) {
                case "2024F" -> List.of("Linear Algebra", "Introduction To Computing", "Islamic Studies", "Programming Fundamentals");
                case "2023F" -> List.of("Object Oriented Programming", "Pakistan Studies", "Calculus & Analytical Geometry", "Applied Physics", "Introduction To Software Engineering");
                case "2022F" -> List.of("Discrete Mathematics", "Communication Skills", "Software Requirement Engineering", "Data Structures And Algorithm", "Computer Organization & Architecture");
                case "2021F" -> List.of("Software Design & Architecture", "Introduction To Database Systems", "Operating System", "Probability And Statistics", "Technical Writing");
                default -> new ArrayList<>();
            };
        } else if (dept.equals("civil engineering")) {
            return switch (batch) {
                case "2024F" -> List.of("Pakistan Studies", "Applied Calculus", "Functional English", "Basic Electro-Mechanical Engineering", "Engineering Drawing");
                case "2023F" -> List.of("Computer Fundamentals & Programming", "Islamic Studies", "Differential Equations", "Surveying", "Engineering Mechanics");
                case "2022F" -> List.of("Numerical Analysis", "Engineering Geology & Ecoinformatics", "Surveying II", "Strength Of Materials", "Social Science");
                case "2021F" -> List.of("Probability Methods In Engineering", "Civil Engineering Materials", "Communication Skills", "Structural Analysis-I", "Fluid Mechanics");
                default -> new ArrayList<>();
            };
        } else {
            return new ArrayList<>();
        }
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AttendanceUI("22F-1234", "Software Engineering", "2022F").setVisible(true));
    }
}
