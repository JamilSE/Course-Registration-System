import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class AdmitCard extends JFrame {
    private String rollNo;
    private String department;
    private String batch;
    private List<String> courses;

    public AdmitCard(String rollNo, String department, String batch, List<String> courses) {
        this.rollNo = rollNo;
        this.department = department;
        this.batch = batch;
        this.courses = courses;

        setTitle("Admit Card");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLUE);
                g.drawRect(20, 20, getWidth() - 40, getHeight() - 100);
            }
        };
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("University Examination Admit Card", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setBounds(180, 30, 350, 30);
        mainPanel.add(title);

        JLabel rollLabel = new JLabel("Roll Number: " + rollNo);
        rollLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        rollLabel.setBounds(50, 80, 300, 25);
        mainPanel.add(rollLabel);

        JLabel deptLabel = new JLabel("Department: " + department);
        deptLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        deptLabel.setBounds(50, 110, 300, 25);
        mainPanel.add(deptLabel);

        JLabel batchLabel = new JLabel("Batch: " + batch);
        batchLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        batchLabel.setBounds(50, 140, 300, 25);
        mainPanel.add(batchLabel);

        JLabel coursesLabel = new JLabel("Registered Courses:");
        coursesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        coursesLabel.setBounds(50, 180, 300, 25);
        mainPanel.add(coursesLabel);

        // Table
        String[] columnNames = {"S.No", "Course Name"};
        String[][] data = new String[courses.size()][2];
        for (int i = 0; i < courses.size(); i++) {
            data[i][0] = String.valueOf(i + 1);
            data[i][1] = courses.get(i);
        }

        JTable table = new JTable(data, columnNames);
        table.setEnabled(false);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 210, 580, 150);
        mainPanel.add(scrollPane);

        JLabel examNote = new JLabel("Note: Please bring this admit card to each exam.");
        examNote.setFont(new Font("Arial", Font.ITALIC, 14));
        examNote.setBounds(50, 380, 400, 25);
        mainPanel.add(examNote);

        JLabel fakeDate = new JLabel("Issue Date: 1st July 2025");
        fakeDate.setFont(new Font("Arial", Font.PLAIN, 14));
        fakeDate.setBounds(50, 410, 300, 25);
        mainPanel.add(fakeDate);

        JLabel signLabel = new JLabel("Signature");
        signLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        signLabel.setBounds(500, 460, 150, 25);
        mainPanel.add(signLabel);

        JButton downloadBtn = new JButton("Download as Image");
        downloadBtn.setBounds(250, 500, 200, 35);
        downloadBtn.setBackground(Color.BLUE);
        downloadBtn.setForeground(Color.WHITE);
        downloadBtn.setFocusPainted(false);

        downloadBtn.addActionListener(e -> savePanelAsImage(mainPanel));
        mainPanel.add(downloadBtn);

        add(mainPanel);
    }

    private void savePanelAsImage(JPanel panel) {
        try {
            BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            panel.paint(g2);
            g2.dispose();

            String filename = "AdmitCard_" + rollNo + ".png";
            File file = new File(filename);
            ImageIO.write(image, "png", file);
            JOptionPane.showMessageDialog(this, "Admit card saved as " + filename);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage());
        }
    }

    public static List<String> getCourses(String dept, String batch) {
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
        } else {
            return new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String roll = "22F-1234";
            String dept = "Software Engineering";
            String batch = "2022F";

            if (batch.equals("2023F") || batch.equals("2024F")) {
                JFrame noticeFrame = new JFrame("Admit Card Notice");
                noticeFrame.setSize(500, 200);
                noticeFrame.setLocationRelativeTo(null);
                noticeFrame.setLayout(new BorderLayout());

                JLabel message = new JLabel("<html><div style='text-align:center;padding:20px;'>Admit cards for the selected batch are not yet available.<br>Please contact the examination office or try again later.</div></html>", JLabel.CENTER);
                message.setFont(new Font("Arial", Font.PLAIN, 14));
                noticeFrame.add(message, BorderLayout.CENTER);

                JButton okBtn = new JButton("OK");
                okBtn.addActionListener(ev -> noticeFrame.dispose());
                JPanel btnPanel = new JPanel();
                btnPanel.add(okBtn);
                noticeFrame.add(btnPanel, BorderLayout.SOUTH);

                noticeFrame.setVisible(true);
            } else {
                List<String> courses = getCourses(dept, batch);
                new AdmitCard(roll, dept, batch, courses).setVisible(true);
            }
        });
    }
}
