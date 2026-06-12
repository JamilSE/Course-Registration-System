import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.util.List;
import java.util.Map;

public class ReportCard extends JFrame {
    private double gpaTotal = 0.0;

    public ReportCard(String roll, String dept, String batch, Map<String, int[]> subjectMarks) {
        setTitle("Report Card");
        setSize(700, 1000);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==== Outer Panel with Black and Gold Border ====
        JPanel outerBorderPanel = new JPanel(new BorderLayout());
        outerBorderPanel.setBackground(Color.BLACK);
        outerBorderPanel.setBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, Color.BLACK));

        JPanel goldInnerBorder = new JPanel(new BorderLayout());
        goldInnerBorder.setBackground(new Color(212, 175, 55)); // Gold color
        goldInnerBorder.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, new Color(212, 175, 55)));

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        cardPanel.setBackground(Color.WHITE);

        // University Logo
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/logo2.png"));
            Image scaledLogo = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardPanel.add(logoLabel);
        } catch (Exception e) {
            JLabel fallback = new JLabel("University Logo");
            fallback.setFont(new Font("Arial", Font.BOLD, 16));
            fallback.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardPanel.add(fallback);
        }

        // Title
        JLabel title = new JLabel("Official Report Card", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        cardPanel.add(title);

        // Student Info
        cardPanel.add(infoLabel("Name: ____________________________"));
        cardPanel.add(infoLabel("Roll No: " + roll));
        cardPanel.add(infoLabel("Department: " + dept));
        cardPanel.add(infoLabel("Batch: " + batch));
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Table Header
        JPanel headerRow = new JPanel(new GridLayout(1, 5));
        headerRow.setMaximumSize(new Dimension(600, 30));
        headerRow.add(boldCell("Subject"));
        headerRow.add(boldCell("Midterm (30)"));
        headerRow.add(boldCell("Final (50)"));
        headerRow.add(boldCell("Total (100)"));
        headerRow.add(boldCell("Grade"));
        cardPanel.add(headerRow);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Table Data
        List<String> subjects = getStaticSubjects(dept, batch);
        for (String subject : subjects) {
            int[] marks = subjectMarks.get(subject);
            int mid = (marks != null && marks.length >= 1) ? marks[0] : 0;
            int fin = (marks != null && marks.length >= 2) ? marks[1] : 0;
            int total = mid + fin;
            String grade = getGrade(total);
            double point = getGradePoint(grade);
            gpaTotal += point;

            JPanel row = new JPanel(new GridLayout(1, 5));
            row.setMaximumSize(new Dimension(600, 25));
            row.add(normalCell(subject));
            row.add(normalCell(String.valueOf(mid)));
            row.add(normalCell(String.valueOf(fin)));
            row.add(normalCell(String.valueOf(total)));
            row.add(gradeCell(grade));
            cardPanel.add(row);
        }

        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        cardPanel.add(new JSeparator());

        // GPA
        double gpa = Math.round((gpaTotal / subjects.size()) * 100.0) / 100.0;
        JLabel gpaLabel = new JLabel("Semester GPA: " + gpa, JLabel.CENTER);
        gpaLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gpaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gpaLabel.setForeground(new Color(0, 102, 0));
        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        cardPanel.add(gpaLabel);

        JLabel gpaNote = new JLabel("Note: GPA is calculated based on subject grade points", JLabel.CENTER);
        gpaNote.setFont(new Font("Arial", Font.ITALIC, 12));
        gpaNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(gpaNote);

        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Signature
        try {
            ImageIcon signIcon = new ImageIcon(getClass().getResource("/images/signature.png"));
            Image scaled = signIcon.getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);
            JLabel signLabel = new JLabel(new ImageIcon(scaled));
            signLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardPanel.add(signLabel);
        } catch (Exception e) {
            JLabel fallback = new JLabel("Signature");
            fallback.setFont(new Font("Arial", Font.ITALIC, 14));
            fallback.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardPanel.add(fallback);
        }

        JLabel signedBy = new JLabel("Controller of Examination", JLabel.CENTER);
        signedBy.setFont(new Font("Arial", Font.PLAIN, 14));
        signedBy.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(signedBy);

        // Print Button
        JButton printBtn = new JButton("Print Report Card");
        printBtn.setFont(new Font("Arial", Font.BOLD, 14));
        printBtn.setBackground(new Color(70, 130, 180));
        printBtn.setForeground(Color.WHITE);
        printBtn.setFocusPainted(false);
        printBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        printBtn.setMaximumSize(new Dimension(200, 40));
        printBtn.addActionListener(e -> printComponent(cardPanel));

        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        cardPanel.add(printBtn);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        goldInnerBorder.add(cardPanel, BorderLayout.CENTER);
        outerBorderPanel.add(goldInnerBorder, BorderLayout.CENTER);
        add(new JScrollPane(outerBorderPanel), BorderLayout.CENTER);

        setVisible(true);
    }

    private JLabel infoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        return label;
    }

    private JLabel boldCell(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JLabel normalCell(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JLabel gradeCell(String grade) {
        JLabel label = new JLabel(grade);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        switch (grade) {
            case "A+", "A" -> label.setForeground(new Color(0, 128, 0));
            case "B+", "B", "C" -> label.setForeground(new Color(255, 140, 0));
            case "F" -> label.setForeground(Color.RED);
            default -> label.setForeground(Color.BLACK);
        }
        return label;
    }

    private String getGrade(int total) {
        if (total >= 85) return "A+";
        if (total >= 75) return "A";
        if (total >= 65) return "B+";
        if (total >= 55) return "B";
        if (total >= 50) return "C";
        return "F";
    }

    private double getGradePoint(String grade) {
        return switch (grade) {
            case "A+" -> 4.0;
            case "A" -> 3.7;
            case "B+" -> 3.3;
            case "B" -> 3.0;
            case "C" -> 2.5;
            default -> 0.0;
        };
    }

    public static List<String> getStaticSubjects(String dept, String batch) {
        if (dept.equalsIgnoreCase("software engineering") || dept.equalsIgnoreCase("computer science")) {
            return switch (batch) {
                case "2022F" -> List.of("Discrete Mathematics", "Communication Skills", "Software Requirement Engineering", "Data Structures And Algorithm", "Computer Organization & Architecture");
                case "2021F" -> List.of("Software Design & Architecture", "Introduction To Database Systems", "Operating System", "Probability And Statistics", "Technical Writing");
                default -> List.of("Subject A", "Subject B", "Subject C");
            };
        } else if (dept.equalsIgnoreCase("civil engineering")) {
            return switch (batch) {
                case "2022F" -> List.of("Numerical Analysis", "Engineering Geology & Ecoinformatics", "Surveying II", "Strength Of Materials", "Social Science");
                case "2021F" -> List.of("Probability Methods In Engineering", "Civil Engineering Materials", "Communication Skills", "Structural Analysis-I", "Fluid Mechanics");
                default -> List.of("Subject X", "Subject Y", "Subject Z");
            };
        }
        return List.of("Subject X", "Subject Y", "Subject Z");
    }

    private void printComponent(Component component) {
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setJobName("Print Report Card");

        pj.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            component.printAll(g2);

            return Printable.PAGE_EXISTS;
        });

        if (pj.printDialog()) {
            try {
                pj.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to Print: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Map<String, int[]> marks = Map.of(
            "Discrete Mathematics", new int[]{25, 45},
            "Communication Skills", new int[]{22, 40},
            "Software Requirement Engineering", new int[]{28, 43},
            "Data Structures And Algorithm", new int[]{24, 48},
            "Computer Organization & Architecture", new int[]{26, 42}
        );

        SwingUtilities.invokeLater(() ->
            new ReportCard("22F-1234", "Software Engineering", "2022F", marks)
        );
    }
}
