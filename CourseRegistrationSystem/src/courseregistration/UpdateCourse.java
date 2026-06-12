import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

public class UpdateCourse extends JFrame {
    private java.util.List<String> selectedCourses = new ArrayList<>();
    private JPanel confirmationPanel, chatBoxPanel;
    private JTextField chatInput;
    private JComboBox<String> batchDropdown, deptDropdown;
    private JScrollPane chatScroll;

    public UpdateCourse(String roll, String dept, String batch) {
        setTitle("Update Course");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBackground(new Color(40, 40, 40));
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(createSidebarButton("Register Course", roll, dept, batch));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(createSidebarButton("Additional Course", roll, dept, batch));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(createSidebarButton("Update Courses", roll, dept, batch));

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(60, 60, 60));

        JLabel heading = new JLabel("Update Registered Courses", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setForeground(Color.WHITE);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(heading);

        // Selective Course Panel
        JLabel selLabel = new JLabel("Select One Selective Course:");
        selLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        selLabel.setForeground(Color.WHITE);

        JComboBox<String> selectiveCourseDropdown = new JComboBox<>(getSelectiveCourses(dept));
        selectiveCourseDropdown.setMaximumSize(new Dimension(400, 30));
        selectiveCourseDropdown.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel selectivePanel = new JPanel();
        selectivePanel.setLayout(new BoxLayout(selectivePanel, BoxLayout.Y_AXIS));
        selectivePanel.setBackground(new Color(60, 60, 60));
        selectivePanel.add(selLabel);
        selectivePanel.add(Box.createVerticalStrut(5));
        selectivePanel.add(selectiveCourseDropdown);
        selectivePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        mainPanel.add(selectivePanel);

        // Update Button
        JButton updateButton = new JButton("Update Courses");
        updateButton.setFont(new Font("Arial", Font.BOLD, 14));
        updateButton.setBackground(new Color(60, 120, 180));
        updateButton.setForeground(Color.WHITE);
        updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateButton.setMaximumSize(new Dimension(200, 40));
        mainPanel.add(updateButton);

        // GPT Chat Panel
        JPanel gptPanel = new JPanel();
        gptPanel.setLayout(new BorderLayout());
        gptPanel.setBackground(new Color(60, 60, 60));
        gptPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel chatHeader = new JLabel("GPT Advisor Assistant");
        chatHeader.setFont(new Font("Arial", Font.BOLD, 14));
        chatHeader.setForeground(Color.CYAN);
        chatHeader.setHorizontalAlignment(SwingConstants.CENTER);
        gptPanel.add(chatHeader, BorderLayout.NORTH);

        chatBoxPanel = new JPanel();
        chatBoxPanel.setLayout(new BoxLayout(chatBoxPanel, BoxLayout.Y_AXIS));
        chatBoxPanel.setBackground(new Color(60, 60, 60));

        chatScroll = new JScrollPane(chatBoxPanel);
        chatScroll.setPreferredSize(new Dimension(600, 180));
        chatScroll.setBorder(null);
        gptPanel.add(chatScroll, BorderLayout.CENTER);

        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInput = new JTextField();
        chatInput.setFont(new Font("Arial", Font.PLAIN, 13));
        JButton sendButton = new JButton("Send");
        sendButton.setBackground(new Color(70, 130, 180));
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(e -> processChat());

        chatInputPanel.add(chatInput, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);
        chatInputPanel.setMaximumSize(new Dimension(600, 30));

        gptPanel.add(chatInputPanel, BorderLayout.SOUTH);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(gptPanel);

        // Confirmation Panel
        confirmationPanel = new JPanel();
        confirmationPanel.setLayout(new BoxLayout(confirmationPanel, BoxLayout.Y_AXIS));
        confirmationPanel.setBorder(BorderFactory.createTitledBorder("Updated Registration Summary"));
        confirmationPanel.setBackground(new Color(61, 61, 61));
        confirmationPanel.setForeground(Color.WHITE);
        confirmationPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmationPanel.setVisible(false);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(confirmationPanel);

        // Add panels
        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Logic
        updateButton.addActionListener(e -> {
            selectedCourses = new ArrayList<>(getCoursesByBatch(batch));
            String selectedSelective = (String) selectiveCourseDropdown.getSelectedItem();
            if (selectedSelective != null && !selectedCourses.contains(selectedSelective)) {
                selectedCourses.add(selectedSelective);
            }
            displayConfirmationTable(roll, dept, batch);
            confirmationPanel.setVisible(true);
        });

        addMessage("GPT: I'm here to help you choose electives. Type 'help me choose course' to get started.", false);
    }

    private void processChat() {
        String input = chatInput.getText().trim();
        if (input.isEmpty()) return;

        addMessage("You: " + input, true);

        if (input.toLowerCase().contains("help")) {
            batchDropdown = new JComboBox<>(new String[]{"2021F", "2022F", "2023F", "2024F"});
            deptDropdown = new JComboBox<>(new String[]{"Civil Engineering", "Software Engineering", "Computer Science"});

            int result = JOptionPane.showConfirmDialog(this, new Object[]{
                    "Select Department:", deptDropdown,
                    "Select Batch:", batchDropdown
            }, "GPT Advisor Input", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String selectedDept = (String) deptDropdown.getSelectedItem();
                String selectedBatch = (String) batchDropdown.getSelectedItem();

                java.util.List<String> electives = new ArrayList<>(getSelectiveListByDept(selectedDept));
                Collections.shuffle(electives);
                java.util.List<String> suggestions = electives.subList(0, Math.min(3, electives.size()));

                StringBuilder reply = new StringBuilder("Based on your input (Dept: " + selectedDept + ", Batch: " + selectedBatch + "), here are some recommended electives:\n");
                for (String course : suggestions) {
                    reply.append("- ").append(course).append("\n");
                }
                addMessage("GPT: " + reply.toString().trim(), false);
            } else {
                addMessage("GPT: Advisor cancelled. Please type 'help me choose course' again if needed.", false);
            }
        } else {
            addMessage("GPT: Please type 'help me choose course' to receive course suggestions.", false);
        }

        chatInput.setText("");
    }

    private void addMessage(String text, boolean isUser) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setOpaque(false);

        JTextArea messageLabel = new JTextArea(text);
        messageLabel.setEditable(false);
        messageLabel.setLineWrap(true);
        messageLabel.setWrapStyleWord(true);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        messageLabel.setBackground(isUser ? new Color(80, 80, 80) : new Color(30, 100, 160));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JPanel bubble = new JPanel();
        bubble.setLayout(new BorderLayout());
        bubble.setBackground(messageLabel.getBackground());
        bubble.add(messageLabel, BorderLayout.CENTER);
        bubble.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        if (isUser) {
            messagePanel.add(bubble, BorderLayout.EAST);
        } else {
            messagePanel.add(bubble, BorderLayout.WEST);
        }

        chatBoxPanel.add(messagePanel);
        chatBoxPanel.revalidate();
        SwingUtilities.invokeLater(() ->
                chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum()));
    }

    private java.util.List<String> getCoursesByBatch(String batch) {
        return switch (batch) {
            case "2024F" -> java.util.List.of("Linear Algebra", "Introduction To Computing", "Islamic Studies", "Programming Fundamentals");
            case "2023F" -> java.util.List.of("Object Oriented Programming", "Pakistan Studies", "Calculus & Analytical Geometry", "Applied Physics", "Introduction To Software Engineering");
            case "2022F" -> java.util.List.of("Discrete Mathematics", "Communication Skills", "Software Requirement Engineering", "Data Structures And Algorithm", "Computer Organization & Architecture");
            case "2021F" -> java.util.List.of("Software Design & Architecture", "Introduction To Database Systems", "Operating System", "Probability And Statistics", "Technical Writing");
            default -> new ArrayList<>();
        };
    }

    private String[] getSelectiveCourses(String dept) {
        return getSelectiveListByDept(dept).toArray(new String[0]);
    }

    private java.util.List<String> getSelectiveListByDept(String dept) {
        return switch (dept) {
            case "Civil Engineering" -> java.util.List.of(
                    "Fire Safety Engineering", "Building Information Modeling (BIM)", "Construction Contract Management",
                    "Hazard & Disaster Management", "Entrepreneurship", "Advance Fluid Mechanics", "Soil Improvement");
            case "Software Engineering", "Computer Science" -> java.util.List.of(
                    "Software Quality Assurance & Testing", "Web Mining", "Design Pattern", "Human Computer Interaction",
                    "Semantic Web", "Social Computing", "Social Network Analysis", "Big Data Analytics",
                    "Agent Based Software Engineering", "Computer Graphics");
            default -> new ArrayList<>();
        };
    }

    private void displayConfirmationTable(String rollNo, String dept, String batch) {
        confirmationPanel.removeAll();
        JLabel info = new JLabel("Roll Number: " + rollNo + "   |   Department: " + dept + "   |   Batch: " + batch);
        info.setFont(new Font("Arial", Font.BOLD, 13));
        info.setForeground(Color.WHITE);
        confirmationPanel.add(info);
        confirmationPanel.add(Box.createVerticalStrut(10));

        String[] columns = {"Subject", "Credit Hours"};
        Object[][] data = new Object[selectedCourses.size()][2];
        int totalCredits = 0;
        for (int i = 0; i < selectedCourses.size(); i++) {
            data[i][0] = selectedCourses.get(i);
            data[i][1] = 3;
            totalCredits += 3;
        }

        JTable table = new JTable(data, columns);
        table.setEnabled(false);
        table.setRowHeight(25);
        JScrollPane tableScroll = new JScrollPane(table);
        confirmationPanel.add(tableScroll);

        JLabel totalLabel = new JLabel("Total Credit Hours: " + totalCredits);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        totalLabel.setForeground(Color.WHITE);
        confirmationPanel.add(Box.createVerticalStrut(10));
        confirmationPanel.add(totalLabel);

        confirmationPanel.revalidate();
        confirmationPanel.repaint();
    }

    private JButton createSidebarButton(String text, String roll, String dept, String batch) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> {
            dispose();
            switch (text) {
                case "Register Course" -> new RegisterCourse(roll, dept, batch).setVisible(true);
                case "Additional Course" -> new AdditionalCourse(roll, dept, batch).setVisible(true);
                case "Update Courses" -> new UpdateCourse(roll, dept, batch).setVisible(true);
            }
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new UpdateCourse("22F-1234", "Civil Engineering", "2022F").setVisible(true)
        );
    }
}

