import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class RegisterCourse extends JFrame {
    private String selectedBatch;
    private String selectedDept;
    private String rollNo;
    private JPanel availableCoursesPanel;
    private JLabel timerLabel;
    private JProgressBar progressBar;
    private javax.swing.Timer countdownTimer;
    private int timeRemaining = 5 * 60;

    private List<String> selectedCourses = new ArrayList<>();
    private HashMap<String, JCheckBox> selectedCheckBoxes = new HashMap<>();
    private HashMap<String, Integer> seatMap = new HashMap<>();
    private HashMap<String, List<String>> waitlistMap = new HashMap<>();
    private Random random = new Random();
    private JPanel confirmationPanel;

    private List<String> sortedCoursesForDisplay = new ArrayList<>();
    private Map<String, Integer> courseDurations = new HashMap<>();
    private Map<String, Integer> courseCredits = new HashMap<>();
    private Map<String, Integer> courseLoadPercent = new HashMap<>();
    private String schedulingType = "SJF";

    public RegisterCourse(String roll, String dept, String batch) {
        this.rollNo = roll;
        this.selectedDept = dept;
        this.selectedBatch = batch;

        setTitle("Register Course");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeCourses();

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 600));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 40, 40));

        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(createInfoLabel("Roll Number: " + roll));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createInfoLabel("Department: " + dept));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createInfoLabel("Batch: " + batch));
        sidebar.add(Box.createVerticalStrut(40));

        sidebar.add(createSidebarButton("Home", roll, dept, batch));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(createSidebarButton("Register Course", roll, dept, batch));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(createSidebarButton("Additional Course", roll, dept, batch));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(createSidebarButton("Update Courses", roll, dept, batch));

        timerLabel = new JLabel("Time left to register: 05:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setForeground(Color.RED);

        progressBar = new JProgressBar(0, 5 * 60);
        progressBar.setValue(timeRemaining);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 153, 76));

        JLabel headerLabel = new JLabel("Courses for Batch: " + batch);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        availableCoursesPanel = new JPanel();
        availableCoursesPanel.setLayout(new BoxLayout(availableCoursesPanel, BoxLayout.Y_AXIS));
        JScrollPane availableScroll = new JScrollPane(availableCoursesPanel);
        availableScroll.setPreferredSize(new Dimension(600, 300));

        confirmationPanel = new JPanel();
        confirmationPanel.setLayout(new BoxLayout(confirmationPanel, BoxLayout.Y_AXIS));
        confirmationPanel.setBorder(BorderFactory.createTitledBorder("Registration Summary"));
        confirmationPanel.setVisible(false);

        JButton confirmBtn = new JButton("Confirm Courses");
        confirmBtn.setBackground(new Color(60, 60, 60));
        confirmBtn.setForeground(Color.WHITE);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(60, 60, 60));
        centerPanel.add(headerLabel);
        centerPanel.add(availableScroll);
        centerPanel.add(timerLabel);
        centerPanel.add(progressBar);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(confirmBtn);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(confirmationPanel);

        add(sidebar, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

        displayAvailableCourses(selectedDept, selectedBatch);
        startCountdown();

        confirmBtn.addActionListener(e -> {
            selectedCourses.clear();
            selectedCheckBoxes.clear();
            confirmationPanel.removeAll();
            boolean waitlisted = false;

            for (Component comp : availableCoursesPanel.getComponents()) {
                if (comp instanceof JPanel rowPanel) {
                    for (Component rowComp : rowPanel.getComponents()) {
                        if (rowComp instanceof JCheckBox checkBox && checkBox.isSelected()) {
                            String courseText = checkBox.getText();
                            String cleanName = courseText.replaceAll("\\s*\\(\\d+h\\)", "").trim();
                            int seats = seatMap.get(courseText);
                            if (seats > 0 && selectedCourses.size() < 5 && !selectedCourses.contains(cleanName)) {
                                seatMap.put(courseText, seats - 1);
                                selectedCourses.add(cleanName);
                            } else if (seats == 0) {
                                waitlistMap.computeIfAbsent(courseText, k -> new ArrayList<>()).add(rollNo);
                                waitlisted = true;
                            }
                        }
                    }
                }
            }

            displayAvailableCourses(selectedDept, selectedBatch);
            displayConfirmationTable(waitlisted);
            confirmationPanel.setVisible(true);

            if (selectedCourses.size() < 5) {
                JOptionPane.showMessageDialog(this,
                        "You have selected fewer courses.\nYour application will be processed in about 3 hours.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "You have selected all 5 courses.\nYour application will take longer to process.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void initializeCourses() {
        List<String> allCourses = new ArrayList<>(getCoursesByDeptAndBatch(selectedDept, selectedBatch));
        courseDurations.clear();
        courseCredits.clear();
        sortedCoursesForDisplay.clear();

        for (String course : allCourses) {
            int duration = random.nextInt(3) + 1;
            courseDurations.put(course, duration);
            int credits = 2 + random.nextInt(2);
            courseCredits.put(course, credits);
            courseLoadPercent.put(course, 40 + random.nextInt(61));
        }

        if (schedulingType.equals("SJF")) {
            allCourses.sort(Comparator.comparingInt(courseDurations::get));
        }

        for (String course : allCourses) {
            String labeledCourse = course + " (" + courseDurations.get(course) + "h)";
            sortedCoursesForDisplay.add(labeledCourse);
            seatMap.put(labeledCourse, 50 + random.nextInt(71));
        }
    }

    private void displayAvailableCourses(String dept, String batch) {
        availableCoursesPanel.removeAll();
        List<String> courses = sortedCoursesForDisplay.isEmpty()
                ? getCoursesByDeptAndBatch(dept, batch)
                : sortedCoursesForDisplay;

        for (String course : courses) {
    int seats = seatMap.getOrDefault(course, 0);
    int load = courseLoadPercent.getOrDefault(course.replaceAll("\\s*\\(\\d+h\\)", "").trim(), 50);

    JCheckBox checkBox = new JCheckBox(course);
    checkBox.setFont(new Font("Arial", Font.BOLD, 14));
    checkBox.setBackground(new Color(60, 60, 60));
    checkBox.setForeground(Color.WHITE);

    JLabel seatsLabel = new JLabel(seats > 0 ? "Seats Available: " + seats : "Full (Waitlist)");
    seatsLabel.setFont(new Font("Arial", Font.PLAIN, 13));
    seatsLabel.setForeground(seats > 0 ? new Color(255, 0, 0) : Color.RED);

    JProgressBar meter = new JProgressBar(0, 100);
    meter.setValue(load);
    meter.setStringPainted(true);
    meter.setPreferredSize(new Dimension(150, 20));

    JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    rowPanel.setBackground(new Color(60, 60, 60)); // 🟢 THIS FIXES IT
    rowPanel.add(checkBox);
    rowPanel.add(seatsLabel);
    rowPanel.add(meter);
    rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    availableCoursesPanel.add(rowPanel);
}

        availableCoursesPanel.revalidate();
        availableCoursesPanel.repaint();
    }

    private void displayConfirmationTable(boolean waitlisted) {
        confirmationPanel.removeAll();

        JLabel info = new JLabel("Roll Number: " + rollNo + "   |   Department: " + selectedDept + "   |   Batch: " + selectedBatch);
        info.setFont(new Font("Arial", Font.BOLD, 14));
        confirmationPanel.add(info);
        confirmationPanel.add(Box.createVerticalStrut(10));

        List<Map.Entry<String, Integer>> courseCreditPairs = new ArrayList<>();
        for (String courseName : selectedCourses) {
            int credits = courseCredits.getOrDefault(courseName, 3);
            courseCreditPairs.add(new AbstractMap.SimpleEntry<>(courseName, credits));
        }

        courseCreditPairs.sort(Comparator.comparingInt(Map.Entry::getValue));

        Object[][] data = new Object[courseCreditPairs.size()][3];
        int totalCredits = 0;
        for (int i = 0; i < courseCreditPairs.size(); i++) {
            String courseName = courseCreditPairs.get(i).getKey();
            int credits = courseCreditPairs.get(i).getValue();
            int duration = courseDurations.getOrDefault(courseName, 0);

            data[i][0] = courseName;
            data[i][1] = credits;
            data[i][2] = duration + "h";
            totalCredits += credits;
        }

        String[] columnNames = {"Subject", "Credit Hours", "Duration"};

        JTable table = new JTable(data, columnNames);
        table.setEnabled(false);
        table.setRowHeight(25);
        JScrollPane tableScroll = new JScrollPane(table);
        confirmationPanel.add(tableScroll);

        JLabel totalLabel = new JLabel("Total Credit Hours: " + totalCredits);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 13));
        confirmationPanel.add(Box.createVerticalStrut(10));
        confirmationPanel.add(totalLabel);

        if (waitlisted) {
            JLabel waitNote = new JLabel("Note: Some courses were full. You have been added to the waitlist.");
            waitNote.setForeground(Color.RED);
            waitNote.setFont(new Font("Arial", Font.ITALIC, 12));
            confirmationPanel.add(Box.createVerticalStrut(5));
            confirmationPanel.add(waitNote);
        }

        confirmationPanel.revalidate();
        confirmationPanel.repaint();
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

    private void startCountdown() {
        countdownTimer = new javax.swing.Timer(1000, e -> {
            if (timeRemaining <= 0) {
                countdownTimer.stop();
                timerLabel.setText("Time is up! Registration closed.");
                disableCourseSelection();
                progressBar.setForeground(Color.RED);
                progressBar.setValue(0);
                JOptionPane.showMessageDialog(this, "5 minutes are over. You can't register courses anymore.", "Time Up", JOptionPane.WARNING_MESSAGE);
            } else {
                int minutes = timeRemaining / 60;
                int seconds = timeRemaining % 60;
                timerLabel.setText(String.format("Time left to register: %02d:%02d", minutes, seconds));
                progressBar.setValue(timeRemaining);
                if (timeRemaining > 180) {
                    progressBar.setForeground(new Color(0, 153, 76));
                } else if (timeRemaining > 90) {
                    progressBar.setForeground(new Color(255, 204, 0));
                } else {
                    progressBar.setForeground(new Color(255, 102, 0));
                }
                timeRemaining--;
            }
        });
        countdownTimer.start();
    }

    private void disableCourseSelection() {
        for (Component comp : availableCoursesPanel.getComponents()) {
            if (comp instanceof JPanel panel) {
                for (Component child : panel.getComponents()) {
                    if (child instanceof JCheckBox box) {
                        box.setEnabled(false);
                    }
                }
            }
        }
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton createSidebarButton(String text, String roll, String dept, String batch) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> {
            dispose();
            switch (text) {
                case "Home" -> new Dashboard(roll, dept, batch).setVisible(true);
                case "Register Course" -> new RegisterCourse(roll, dept, batch).setVisible(true);
                case "Additional Course" -> new AdditionalCourse(roll, dept, batch).setVisible(true);
                case "Update Courses" -> new UpdateCourse(roll, dept, batch).setVisible(true);
            }
        });
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterCourse("22F-1234", "Civil Engineering", "2022F").setVisible(true));
    }
}
