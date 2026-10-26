package com.cudailyschedule.ui;

import com.cudailyschedule.model.Teacher;
import com.cudailyschedule.service.TeacherService;
import com.cudailyschedule.ui.theme.UITheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Recreates cu-daily-schedule/src/app/teacher/page.tsx.
 * Provides Employee ID search, sample quick-pick buttons, and simulated QR badge scanner.
 */
public class TeacherSearchScreen extends JPanel {
    private final TeacherService teacherService;
    private final Consumer<String> navigationHandler;
    private final BiConsumer<String, Teacher> onTeacherSelected;

    private final JTextField idField;

    public TeacherSearchScreen(TeacherService teacherService, Consumer<String> navigationHandler, BiConsumer<String, Teacher> onTeacherSelected) {
        this.teacherService = teacherService;
        this.navigationHandler = navigationHandler;
        this.onTeacherSelected = onTeacherSelected;

        setLayout(new BorderLayout());
        setOpaque(false);

        // TOP NAV
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 16));
        navBar.setOpaque(false);

        JButton backBtn = new JButton("← Back to Home") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(UITheme.CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        backBtn.setFont(UITheme.FONT_BODY_BOLD);
        backBtn.setForeground(UITheme.NAVY);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        backBtn.addActionListener(e -> navigationHandler.accept("LANDING"));
        navBar.add(backBtn);

        // CENTER SEARCH CARD
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
        centerWrapper.setOpaque(false);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.setColor(UITheme.CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.dispose();
            }
        };
        card.setPreferredSize(new Dimension(440, 520));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(32, 28, 32, 28));

        // Header Title
        JLabel titleLbl = new JLabel("CU DAILY SCHEDULE", JLabel.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLbl.setForeground(UITheme.NAVY);
        titleLbl.setAlignmentX(CENTER_ALIGNMENT);

        // Teacher Mode Badge
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.ACCENT_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        JLabel badgeLbl = new JLabel("TEACHER MODE");
        badgeLbl.setFont(UITheme.FONT_TINY_BOLD);
        badgeLbl.setForeground(UITheme.ACCENT);
        badge.add(badgeLbl);
        badge.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subLbl = new JLabel("View your teaching schedule", JLabel.CENTER);
        subLbl.setFont(UITheme.FONT_BODY);
        subLbl.setForeground(UITheme.TEXT_SECONDARY);
        subLbl.setAlignmentX(CENTER_ALIGNMENT);

        // Input Field
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(360, 48));
        idField.setMaximumSize(new Dimension(360, 48));
        idField.setFont(new Font("Segoe UI", Font.BOLD, 16));
        idField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.TIMELINE_LINE, 1, true),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        idField.setAlignmentX(CENTER_ALIGNMENT);

        // Submit Button
        JButton submitBtn = new JButton("View Schedule →") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(getModel().isRollover() ? new Color(0x24, 0x33, 0x56) : UITheme.NAVY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        submitBtn.setPreferredSize(new Dimension(360, 48));
        submitBtn.setMaximumSize(new Dimension(360, 48));
        submitBtn.setFont(UITheme.FONT_BODY_BOLD);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setContentAreaFilled(false);
        submitBtn.setBorderPainted(false);
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitBtn.setAlignmentX(CENTER_ALIGNMENT);
        submitBtn.addActionListener(e -> handleSearch(idField.getText()));

        // Divider
        JPanel divider = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        divider.setOpaque(false);
        JLabel orLbl = new JLabel("— OR —");
        orLbl.setFont(UITheme.FONT_TINY_BOLD);
        orLbl.setForeground(UITheme.TEXT_MUTED);
        divider.add(orLbl);
        divider.setAlignmentX(CENTER_ALIGNMENT);

        // Scan QR Button
        JButton qrBtn = new JButton("📷 Scan Employee QR") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(getModel().isRollover() ? UITheme.ACCENT_HOVER : UITheme.ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        qrBtn.setPreferredSize(new Dimension(360, 46));
        qrBtn.setMaximumSize(new Dimension(360, 46));
        qrBtn.setFont(UITheme.FONT_BODY_BOLD);
        qrBtn.setForeground(Color.WHITE);
        qrBtn.setContentAreaFilled(false);
        qrBtn.setBorderPainted(false);
        qrBtn.setFocusPainted(false);
        qrBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        qrBtn.setAlignmentX(CENTER_ALIGNMENT);
        qrBtn.addActionListener(e -> openQrScannerDialog());

        // Quick Pick Section
        JLabel quickLbl = new JLabel("Sample Faculty Profiles:", JLabel.CENTER);
        quickLbl.setFont(UITheme.FONT_SMALL_BOLD);
        quickLbl.setForeground(UITheme.TEXT_MUTED);
        quickLbl.setAlignmentX(CENTER_ALIGNMENT);

        JPanel quickRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        quickRow.setOpaque(false);

        quickRow.add(createSampleButton("E19761 (Neeraj)"));
        quickRow.add(createSampleButton("E16525 (Gitanjali)"));
        quickRow.add(createSampleButton("E10279 (Jaspreet)"));
        quickRow.setAlignmentX(CENTER_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(badge);
        card.add(Box.createVerticalStrut(8));
        card.add(subLbl);
        card.add(Box.createVerticalStrut(24));
        card.add(idField);
        card.add(Box.createVerticalStrut(12));
        card.add(submitBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(divider);
        card.add(Box.createVerticalStrut(14));
        card.add(qrBtn);
        card.add(Box.createVerticalStrut(20));
        card.add(quickLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(quickRow);

        centerWrapper.add(card);

        add(navBar, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private JButton createSampleButton(String label) {
        String id = label.split(" ")[0];
        JButton btn = new JButton(label);
        btn.setFont(UITheme.FONT_SMALL);
        btn.setForeground(UITheme.NAVY);
        btn.setBackground(new Color(0xF1, 0xF5, 0xF9));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            idField.setText(id);
            handleSearch(id);
        });
        return btn;
    }

    private void handleSearch(String inputId) {
        if (inputId == null || inputId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Employee ID (e.g. E19761)", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cleanId = inputId.trim().toUpperCase();
        Optional<Teacher> opt = teacherService.findTeacher(cleanId);
        if (opt.isPresent()) {
            onTeacherSelected.accept(cleanId, opt.get());
        } else {
            JOptionPane.showMessageDialog(this, "Teacher not found with Employee ID: " + cleanId + "\nPlease verify the ID or select one of the sample faculty members.", "Teacher Not Found", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openQrScannerDialog() {
        String[] options = {"E19761 - Neeraj (CSE)", "E16525 - Gitanjali (CSE)", "E10279 - Jaspreet Singh Baith (CSE)"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Simulating Faculty Badge QR Scanner...\nSelect a badge to scan:",
            "Faculty QR Badge Scanner",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice >= 0 && choice < options.length) {
            String selectedId = options[choice].split(" ")[0];
            idField.setText(selectedId);
            handleSearch(selectedId);
        }
    }
}
