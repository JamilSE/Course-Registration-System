import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class AssessmentResult extends JFrame {
    private static final Map<String, int[]> latestMarks = new HashMap<>();

    public AssessmentResult(String roll, String dept, String batch) {
        if (batch.equals("2023F") || batch.equals("2024F")) {
            showMessageWindow();
        } else {
            showResultWindow(roll, dept, batch);
        }
    }

    private void showMessageWindow() {
        JFrame messageFrame = new JFrame("Assessment Result Notice");
        messageFrame.setSize(500, 250);
        messageFrame.setLocationRelativeTo(null);
        messageFrame.setLayout(new BorderLayout());

        JLabel message = new JLabel("<html><div style='text-align:center;padding:20px;'>"
                + "<h2>🧑‍🎓 Notice for New Batches</h2>"
                + "Dear Student,<br><br>Your assessment results are currently under review.<br>"
                + "Please check back later or contact your department coordinator."
                + "</div></html>", JLabel.CENTER);
        message.setFont(new Font("Arial", Font.PLAIN, 14));
        messageFrame.add(message, BorderLayout.CENTER);

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> messageFrame.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(okBtn);
        messageFrame.add(btnPanel, BorderLayout.SOUTH);

        messageFrame.setVisible(true);
    }

    private void showResultWindow(String roll, String dept, String batch) {
        setTitle("Assessment Result");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel heading = new JLabel("Assessment Result", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(heading, BorderLayout.NORTH);

        List<String> subjects = getSubjects(dept, batch);
        Object[][] data = new Object[subjects.size()][6];

        latestMarks.clear(); // clear previous data

        // Store first subject's marks for pie chart
        int qaPie = 0, midPie = 0, finalPie = 0;

        for (int i = 0; i < subjects.size(); i++) {
            int qa = 10 + (int)(Math.random() * 11);       // Q/A: 10–20
            int mid = 20 + (int)(Math.random() * 11);      // Midterm: 20–30
            int fin = 35 + (int)(Math.random() * 16);      // Final: 35–50
            int total = qa + mid + fin;
            String grade = getGrade(total);

            data[i][0] = subjects.get(i);
            data[i][1] = qa;
            data[i][2] = mid;
            data[i][3] = fin;
            data[i][4] = total;
            data[i][5] = grade;

            // Save midterm and final only
            latestMarks.put(subjects.get(i), new int[]{mid, fin});

            if (i == 0) {
                qaPie = qa;
                midPie = mid;
                finalPie = fin;
            }
        }

        String[] columns = {"Subject", "Q/A (20)", "Midterm (30)", "Final (50)", "Total (100)", "Grade"};
        JTable table = new JTable(data, columns);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        // Grade color rendering
        DefaultTableCellRenderer gradeRenderer = new DefaultTableCellRenderer() {
            public void setValue(Object value) {
                setText(value.toString());
                setHorizontalAlignment(CENTER);
                switch (value.toString()) {
                    case "A+", "A" -> setForeground(Color.GREEN.darker());
                    case "B+", "B", "C" -> setForeground(new Color(255, 140, 0));
                    case "F" -> setForeground(Color.RED);
                    default -> setForeground(Color.BLACK);
                }
            }
        };
        table.getColumnModel().getColumn(5).setCellRenderer(gradeRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // ==== PIE CHART ====
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Q/A (20)", qaPie);
        dataset.setValue("Midterm (30)", midPie);
        dataset.setValue("Final (50)", finalPie);

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Marks Breakdown", dataset, true, true, false
        );
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(400, 300));

        JPanel chartWrapper = new JPanel(new BorderLayout());
        chartWrapper.add(chartPanel, BorderLayout.CENTER);

        // Combine table and chart in center
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.add(scroll);
        centerPanel.add(chartWrapper);
        add(centerPanel, BorderLayout.CENTER);

        // ==== Bottom Panel with Print Button Centered ====
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JButton printBtn = new JButton("Print Result");
        printBtn.setFont(new Font("Arial", Font.BOLD, 14));
        printBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Sending result to printer...", "Print", JOptionPane.INFORMATION_MESSAGE)
        );

        JPanel btnPanel = new JPanel(); // default FlowLayout centers
        btnPanel.add(printBtn);

        bottomPanel.add(btnPanel, BorderLayout.NORTH);

        JLabel footer = new JLabel("Roll: " + roll + "   |   Department: " + dept + "   |   Batch: " + batch);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setFont(new Font("Arial", Font.PLAIN, 13));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        bottomPanel.add(footer, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private String getGrade(int total) {
        if (total >= 90) return "A+";
        if (total >= 80) return "A";
        if (total >= 70) return "B+";
        if (total >= 60) return "B";
        if (total >= 50) return "C";
        return "F";
    }

    private List<String> getSubjects(String dept, String batch) {
        if (dept.equalsIgnoreCase("software engineering") || dept.equalsIgnoreCase("computer science")) {
            return switch (batch) {
                case "2022F" -> List.of("Discrete Mathematics", "Communication Skills", "Software Requirement Engineering", "Data Structures And Algorithm", "Computer Organization & Architecture");
                case "2021F" -> List.of("Software Design & Architecture", "Introduction To Database Systems", "Operating System", "Probability And Statistics", "Technical Writing");
                default -> new ArrayList<>();
            };
        } else if (dept.equalsIgnoreCase("civil engineering")) {
            return switch (batch) {
                case "2022F" -> List.of("Numerical Analysis", "Engineering Geology & Ecoinformatics", "Surveying II", "Strength Of Materials", "Social Science");
                case "2021F" -> List.of("Probability Methods In Engineering", "Civil Engineering Materials", "Communication Skills", "Structural Analysis-I", "Fluid Mechanics");
                default -> new ArrayList<>();
            };
        }
        return new ArrayList<>();
    }

    // ✅ Synced getter method for ReportCard
    public static Map<String, int[]> getLatestMarks(String roll, String dept, String batch) {
        return latestMarks;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AssessmentResult("22F-1234", "Software Engineering", "2022F").setVisible(true);
        });
    }
}
