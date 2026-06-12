import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SignInPage extends JFrame {
    private Font arial = new Font("Arial", Font.PLAIN, 14);
    private Color darkBg = new Color(45, 45, 45);
    private JLabel imageLabel;
    private int currentImageIndex = 0;
    private final String[] imagePaths = {
        "/images/logo2.png", "/images/1.jpg", "/images/2.jpg", "/images/3.jpg"
    };

    public SignInPage() {
        setTitle("Course Registration - Sign In");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel container = new JPanel(new GridLayout(1, 2)); // split into two equal halves

        // Left Panel
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(darkBg);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel logoLabel;
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/logo2.png"));
            Image scaled = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaled));
        } catch (Exception ex) {
            logoLabel = new JLabel("Logo Not Found", JLabel.CENTER);
            logoLabel.setFont(arial);
            logoLabel.setForeground(Color.WHITE);
        }
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(logoLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel rollLabel = createLabel("Roll Number:");
        leftPanel.add(rollLabel, gbc);

        gbc.gridx = 1;
        JTextField rollField = createTextField();
        leftPanel.add(rollField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passLabel = createLabel("Password:");
        leftPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = createPasswordField();
        leftPanel.add(passwordField, gbc);

        gbc.gridy++;
        JCheckBox showPass = new JCheckBox("Show Password");
        showPass.setOpaque(false);
        showPass.setForeground(Color.WHITE);
        leftPanel.add(showPass, gbc);

        showPass.addItemListener(e -> {
            passwordField.setEchoChar(showPass.isSelected() ? (char) 0 : '\u2022');
        });

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel deptLabel = createLabel("Department:");
        leftPanel.add(deptLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> departmentCombo = createComboBox(new String[]{
                "Software Engineering", "Computer Science", "Civil Engineering"
        });
        leftPanel.add(departmentCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel batchLabel = createLabel("Batch:");
        leftPanel.add(batchLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> batchCombo = createComboBox(new String[]{
                "2021F", "2022F", "2023F", "2024F"
        });
        leftPanel.add(batchCombo, gbc);

        // CAPTCHA Setup
        Random rand = new Random();
        int a = rand.nextInt(10), b = rand.nextInt(10);
        String question = "CAPTCHA: " + a + " + " + b + " =";

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel captchaLabel = createLabel(question);
        leftPanel.add(captchaLabel, gbc);

        gbc.gridx = 1;
        JTextField captchaField = createTextField();
        captchaField.setPreferredSize(new Dimension(100, 35));
        leftPanel.add(captchaField, gbc);

        // Sign In Button
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton signInButton = createButton("Sign In");
        leftPanel.add(signInButton, gbc);

        // Focus flow with Enter key
        rollField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> departmentCombo.requestFocus());
        departmentCombo.addActionListener(e -> batchCombo.requestFocus());
        batchCombo.addActionListener(e -> captchaField.requestFocus());
        captchaField.addActionListener(e -> signInButton.doClick());

        // Sign In Logic
        signInButton.addActionListener(e -> {
            String roll = rollField.getText();
            String dept = (String) departmentCombo.getSelectedItem();
            String batch = (String) batchCombo.getSelectedItem();
            String answer = captchaField.getText().trim();

            if (roll.isEmpty() || passwordField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }
            if (!answer.equals(String.valueOf(a + b))) {
                JOptionPane.showMessageDialog(this, "Incorrect CAPTCHA answer.");
                return;
            }

            dispose();
            new Dashboard(roll, dept, batch).setVisible(true);
        });

        // Right Panel: Image carousel with arrows and auto-scroll
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        loadImage(currentImageIndex);

        // Auto-scroll image every 3 seconds
        Timer autoScrollTimer = new Timer(3000, e -> {
            currentImageIndex = (currentImageIndex + 1) % imagePaths.length;
            loadImage(currentImageIndex);
        });
        autoScrollTimer.start();

        rightPanel.add(imageLabel, BorderLayout.CENTER);

        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        prevButton.setFocusable(false);
        nextButton.setFocusable(false);

        prevButton.addActionListener(e -> {
            currentImageIndex = (currentImageIndex - 1 + imagePaths.length) % imagePaths.length;
            loadImage(currentImageIndex);
        });

        nextButton.addActionListener(e -> {
            currentImageIndex = (currentImageIndex + 1) % imagePaths.length;
            loadImage(currentImageIndex);
        });

        JPanel arrowPanel = new JPanel();
        arrowPanel.setBackground(Color.WHITE);
        arrowPanel.add(prevButton);
        arrowPanel.add(nextButton);

        rightPanel.add(arrowPanel, BorderLayout.SOUTH);

        container.add(leftPanel);
        container.add(rightPanel);
        add(container);
    }

    private void loadImage(int index) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePaths[index]));
            Image scaled = icon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
            imageLabel.setText(null);
        } catch (Exception e) {
            imageLabel.setIcon(null);
            imageLabel.setText("Image not found");
            imageLabel.setFont(arial);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(arial);
        label.setForeground(Color.WHITE);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(arial);
        field.setPreferredSize(new Dimension(300, 35));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(arial);
        field.setPreferredSize(new Dimension(300, 35));
        field.setEchoChar('\u2022');
        return field;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(arial);
        combo.setPreferredSize(new Dimension(300, 35));
        return combo;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(arial.deriveFont(Font.BOLD, 14));
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(300, 40));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignInPage().setVisible(true));
    }
}
