import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

public class Dashboard extends JFrame {
    private Color sidebarBg = new Color(35, 35, 35);
    private Color activeBtnBg = new Color(70, 130, 180);
    private Color defaultBtnBg = new Color(60, 60, 60);

    public Dashboard(String roll, String dept, String batch) {
        setTitle("Course Registration - Dashboard");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ==== Header Panel ====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(60, 60, 60));
        header.setPreferredSize(new Dimension(1200, 50));

        JLabel appName = new JLabel("Course Registration System", JLabel.LEFT);
        appName.setFont(new Font("Arial", Font.BOLD, 16));
        appName.setForeground(Color.WHITE);
        appName.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        header.add(appName, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setForeground(new Color(70, 130, 180));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setPreferredSize(new Dimension(100, 30));
        logoutBtn.addActionListener(e -> {
            dispose();
            new SignInPage().setVisible(true);
        });
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutBtn);
        header.add(logoutPanel, BorderLayout.EAST);

        // ==== Sidebar ====
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 700));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(sidebarBg);

        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/logo2.png"));
            Image scaled = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel logo = new JLabel(new ImageIcon(scaled));
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
            sidebar.add(logo);
        } catch (Exception e) {
            JLabel fallback = new JLabel("University");
            fallback.setForeground(Color.WHITE);
            fallback.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
            sidebar.add(fallback);
        }

        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        sidebar.add(createInfoLabel("Roll Number: " + roll));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createInfoLabel("Department: " + dept));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createInfoLabel("Batch: " + batch));
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        sidebar.add(new JSeparator());
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton homeBtn = createSidebarButton("Home");
        JButton registerCourseBtn = createSidebarButton("Register Course");
        JButton additionalCourseBtn = createSidebarButton("Additional Course");
        JButton updateCourseBtn = createSidebarButton("Update Courses");
        JButton attendanceBtn = createSidebarButton("Attendance");

        JButton examBtn = createSidebarButton("Exam ▼");
        JButton admitCardBtn = createSidebarButton("Print Admit Card");
        JButton assessmentBtn = createSidebarButton("Assessment Result");
        JButton reportCardBtn = createSidebarButton("View Report Card");

        // Hide exam submenu by default
        admitCardBtn.setVisible(false);
        assessmentBtn.setVisible(false);
        reportCardBtn.setVisible(false);

        examBtn.addActionListener(e -> {
            boolean visible = !admitCardBtn.isVisible();
            admitCardBtn.setVisible(visible);
            assessmentBtn.setVisible(visible);
            reportCardBtn.setVisible(visible);
        });

        sidebar.add(homeBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(registerCourseBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(additionalCourseBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(updateCourseBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(attendanceBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(examBtn);
        sidebar.add(admitCardBtn);
        sidebar.add(assessmentBtn);
        sidebar.add(reportCardBtn);

        // ==== Main Panel ====
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 245, 245));

        JLabel placeholderLabel = new JLabel("Welcome to your Student Dashboard. Use the left menu to get started.", JLabel.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.BOLD, 18));
        placeholderLabel.setForeground(new Color(60, 60, 60));
        placeholderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(placeholderLabel);
        mainPanel.add(Box.createVerticalGlue());

        // ==== Footer ====
        JPanel footer = new JPanel();
        footer.setPreferredSize(new Dimension(1000, 30));
        footer.setBackground(new Color(230, 230, 230));
        JLabel version = new JLabel("Version 1.0 | Developed by JAMIL, PARVEZ, HUR AND ABIDULLAH | Privacy Policy | About");
        version.setFont(new Font("Arial", Font.PLAIN, 11));
        footer.add(version);

        // ==== Add to Frame ====
        add(header, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        // ==== Button Actions ====
        homeBtn.addActionListener(e -> {
            // Optional: reset main panel
        });

        registerCourseBtn.addActionListener(e -> {
            new RegisterCourse(roll, dept, batch).setVisible(true);
            dispose();
        });

        additionalCourseBtn.addActionListener(e -> {
            new AdditionalCourse(roll, dept, batch).setVisible(true);
            dispose();
        });

        updateCourseBtn.addActionListener(e -> {
            new UpdateCourse(roll, dept, batch).setVisible(true);
            dispose();
        });

        attendanceBtn.addActionListener(e -> {
            new AttendanceUI(roll, dept, batch).setVisible(true);
            dispose();
        });

        // ✅ Admit Card Button
        admitCardBtn.addActionListener(e -> {
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
                List<String> courses = AdmitCard.getCourses(dept, batch);
                new AdmitCard(roll, dept, batch, courses).setVisible(true);
            }
        });

        // ✅ Assessment Result Button
        assessmentBtn.addActionListener(e -> {
            new AssessmentResult(roll, dept, batch).setVisible(true);
        });

        // ✅ Report Card Button (🆕 synced marks)
        reportCardBtn.addActionListener(e -> {
            Map<String, int[]> syncedMarks = AssessmentResult.getLatestMarks(roll, dept, batch);
            new ReportCard(roll, dept, batch, syncedMarks).setVisible(true);
        });
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
        button.setBackground(defaultBtnBg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(activeBtnBg);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultBtnBg);
            }
        });
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new Dashboard("22F-1234", "Software Engineering", "2022F").setVisible(true)
        );
    }
}
