import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

public class AdditionalCourse extends JFrame {
    private static final List<String> batches = Arrays.asList("2024F", "2023F", "2022F", "2021F");
    private static final List<String> departments = Arrays.asList("Software Engineering", "Computer Science", "Civil Engineering");

    private JPanel coursePanel;
    private JButton confirmButton, refreshButton;
    private JLabel selectedCoursesLabel;
    private JTable confirmationTable;
    private JScrollPane tableScrollPane;

    private List<JCheckBox> courseCheckBoxes = new ArrayList<>();
    private List<String> selectedCourses = new ArrayList<>();
    private Map<String, String> courseToBatch = new HashMap<>();
    private Map<String, Integer> courseSeats = new HashMap<>();
    private Map<String, String> courseStatus = new HashMap<>();
    private int refreshCount = 0;

    public AdditionalCourse(String roll, String dept, String userBatch) {
        setTitle("Additional Course Selection");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 650));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 40, 40));

        JLabel rollLabel = createInfoLabel("Roll Number: " + roll);
        JLabel deptLabel = createInfoLabel("Department: " + dept);
        JLabel batchLabel = createInfoLabel("Batch: " + userBatch);

        JButton registerCourseBtn = createSidebarButton("Register Course");
        JButton additionalCourseBtn = createSidebarButton("Additional Course");
        JButton updateCourseBtn = createSidebarButton("Update Courses");

        sidebar.add(Box.createVerticalStrut(60));
        sidebar.add(rollLabel);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(deptLabel);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(batchLabel);
        sidebar.add(Box.createVerticalStrut(80));
        sidebar.add(registerCourseBtn);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(additionalCourseBtn);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(updateCourseBtn);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(60, 60, 60));

        JLabel heading = new JLabel("Select Additional Courses");
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setForeground(Color.WHITE);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(heading);

        // Course Panel
        coursePanel = new JPanel();
        coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.Y_AXIS));
        coursePanel.setBackground(new Color(60, 60, 60));

        JScrollPane scrollPane = new JScrollPane(coursePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(new Color(60, 60, 60));
        scrollPane.setBackground(new Color(60, 60, 60));
        scrollPane.setPreferredSize(new Dimension(700, 250));

        JPanel scrollContainer = new JPanel(new BorderLayout());
        scrollContainer.setBackground(new Color(60, 60, 60));
        scrollContainer.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(scrollContainer);

        // Buttons
        confirmButton = new JButton("Confirm Courses");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.setBackground(new Color(60, 120, 180));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.setMaximumSize(new Dimension(200, 40));

        refreshButton = new JButton("Refresh Status");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(100, 180, 100));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshButton.setMaximumSize(new Dimension(200, 40));

        selectedCoursesLabel = new JLabel("Selected Courses: []");
        selectedCoursesLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        selectedCoursesLabel.setForeground(Color.WHITE);
        selectedCoursesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmationTable = new JTable();
        confirmationTable.setFillsViewportHeight(true);
        tableScrollPane = new JScrollPane(confirmationTable);
        tableScrollPane.setVisible(false);
        tableScrollPane.setPreferredSize(new Dimension(600, 120));

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(confirmButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(refreshButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(selectedCoursesLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(tableScrollPane);
        mainPanel.add(Box.createVerticalStrut(20));

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Button Logic
        confirmButton.addActionListener(e -> {
            selectedCourses.clear();
            courseStatus.clear();
            Set<String> selectedBatches = new HashSet<>();

            for (JCheckBox cb : courseCheckBoxes) {
                if (cb.isSelected()) {
                    String course = cb.getText();
                    String batch = courseToBatch.get(course);
                    if (selectedBatches.contains(batch)) {
                        JOptionPane.showMessageDialog(this, "Cannot select more than one course from the same batch: " + batch);
                        return;
                    }
                    selectedBatches.add(batch);
                    selectedCourses.add(course);
                    courseStatus.put(course, userBatch.equals(batch) ? "Confirmed" : "Waitlisted");
                }
            }

            if (selectedCourses.size() > 5) {
                JOptionPane.showMessageDialog(this, "You can select up to 5 courses only.");
                return;
            }

            selectedCoursesLabel.setText("Selected Courses: " + selectedCourses);
            showConfirmationTable();
            refreshCount = 0;
        });

        refreshButton.addActionListener(e -> {
            refreshCount++;
            for (String course : selectedCourses) {
                String status = courseStatus.get(course);
                if (!"Confirmed".equals(status)) {
                    if (refreshCount == 1) courseStatus.put(course, "Processing");
                    else if (refreshCount >= 4) courseStatus.put(course, "Allocated");
                }
            }
            showConfirmationTable();
        });

        registerCourseBtn.addActionListener(e -> {
            new RegisterCourse(roll, dept, userBatch).setVisible(true);
            dispose();
        });

        additionalCourseBtn.addActionListener(e -> {
            new AdditionalCourse(roll, dept, userBatch).setVisible(true);
            dispose();
        });

        updateCourseBtn.addActionListener(e -> {
            new UpdateCourse(roll, dept, userBatch).setVisible(true);
            dispose();
        });

        loadCourses(dept, userBatch);
    }

    private void loadCourses(String dept, String userBatch) {
        coursePanel.removeAll();
        courseCheckBoxes.clear();
        courseToBatch.clear();

        int userIndex = batches.indexOf(userBatch);
        List<String> allowedBatches = batches.subList(userIndex + 1, batches.size());

        for (String batch : allowedBatches) {
            List<String> courses = getCoursesByDeptAndBatch(dept, batch);
            for (String course : courses) {
                String fullText = "[" + batch + "] " + course;
                JCheckBox cb = new JCheckBox(fullText);
                cb.setBackground(new Color(60, 60, 60));
                cb.setForeground(Color.WHITE);
                cb.setFont(new Font("Arial", Font.PLAIN, 14));
                courseCheckBoxes.add(cb);
                coursePanel.add(cb);
                courseToBatch.put(fullText, batch);
                courseSeats.put(fullText, 3);
            }
        }

        coursePanel.revalidate();
        coursePanel.repaint();
        tableScrollPane.setVisible(false);
    }

    private void showConfirmationTable() {
        String[] columnNames = {"Course Name", "Credit Hours", "Status"};
        String[][] data = new String[selectedCourses.size()][3];
        for (int i = 0; i < selectedCourses.size(); i++) {
            String course = selectedCourses.get(i);
            data[i][0] = course;
            data[i][1] = "3";
            data[i][2] = courseStatus.get(course);
        }
        confirmationTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        tableScrollPane.setVisible(true);
    }

    private List<String> getCoursesByDeptAndBatch(String dept, String batch) {
        if (dept.equals("Software Engineering") || dept.equals("Computer Science")) {
            return switch (batch) {
                case "2024F" -> List.of("Linear Algebra", "Introduction To Computing", "Islamic Studies", "Programming Fundamentals");
                case "2023F" -> List.of("Object Oriented Programming", "Pakistan Studies", "Calculus & Analytical Geometry", "Applied Physics", "Introduction To Software Engineering");
                case "2022F" -> List.of("Discrete Mathematics", "Communication Skills", "Software Requirement Engineering", "Data Structures And Algorithm", "Computer Organization & Architecture");
                case "2021F" -> List.of("Software Design & Architecture", "Introduction To Database Systems", "Operating System", "Probability And Statistics", "Technical Writing");
                default -> new ArrayList<>();
            };
        } else if (dept.equals("Civil Engineering")) {
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
        SwingUtilities.invokeLater(() -> new AdditionalCourse("22F-1234", "Civil Engineering", "2023F").setVisible(true));
    }
}

