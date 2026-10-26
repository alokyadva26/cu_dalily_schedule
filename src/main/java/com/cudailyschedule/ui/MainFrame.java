package com.cudailyschedule.ui;

import com.cudailyschedule.model.Teacher;
import com.cudailyschedule.service.DatabaseService;
import com.cudailyschedule.service.StudentService;
import com.cudailyschedule.service.TeacherService;
import com.cudailyschedule.ui.theme.UITheme;
import com.cudailyschedule.util.ImageUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Main application window and navigation controller.
 * Hosts CardLayout switching between Landing, Student, Teacher Search, and Teacher Schedule views.
 */
public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardsPanel = new JPanel(cardLayout);

    private final StudentService studentService = new StudentService();
    private final TeacherService teacherService = new TeacherService();

    private final LandingScreen landingScreen;
    private final StudentScheduleScreen studentScreen;
    private final TeacherSearchScreen teacherSearchScreen;
    private final TeacherScheduleScreen teacherScheduleScreen;

    private final JLabel dbStatusBadge;

    public MainFrame() {
        super("CU Daily Schedule - Smart Timetable Application (Java Swing + PostgreSQL JDBC)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 840);
        setMinimumSize(new Dimension(1024, 720));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BACKGROUND);

        // App Icon
        if (ImageUtils.loadImage("/images/cu-logo.png") != null) {
            setIconImage(ImageUtils.loadImage("/images/cu-logo.png"));
        }

        // Top Navigation & Diagnostic Bar
        JPanel topBar = createTopBar();

        // Screen Initializations
        landingScreen = new LandingScreen(this::navigateTo);
        studentScreen = new StudentScheduleScreen(studentService, this::navigateTo);
        teacherSearchScreen = new TeacherSearchScreen(teacherService, this::navigateTo, this::showTeacherSchedule);
        teacherScheduleScreen = new TeacherScheduleScreen(teacherService, this::navigateTo);

        cardsPanel.setOpaque(false);
        cardsPanel.add(landingScreen, "LANDING");
        cardsPanel.add(studentScreen, "STUDENT");
        cardsPanel.add(teacherSearchScreen, "TEACHER_SEARCH");
        cardsPanel.add(teacherScheduleScreen, "TEACHER_SCHEDULE");

        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);

        // Status update
        dbStatusBadge = new JLabel();
        updateDbStatusBadge();
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.CARD_BORDER),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        // Brand on left
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brand.setOpaque(false);
        JLabel logo = new JLabel("🎓");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        JLabel title = new JLabel("CU Daily Schedule");
        title.setFont(UITheme.FONT_BODY_BOLD);
        title.setForeground(UITheme.NAVY);
        brand.add(logo);
        brand.add(title);

        // Quick Navigation Pills
        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        navButtons.setOpaque(false);

        navButtons.add(createNavButton("🏠 Home", "LANDING"));
        navButtons.add(createNavButton("👨‍🎓 Student Timetable", "STUDENT"));
        navButtons.add(createNavButton("👨‍🏫 Teacher Mode", "TEACHER_SEARCH"));

        // Admin Dialog Button
        JButton adminBtn = new JButton("⚙️ DB CRUD Admin");
        adminBtn.setFont(UITheme.FONT_SMALL_BOLD);
        adminBtn.setForeground(UITheme.NAVY);
        adminBtn.setBackground(new Color(0xF1, 0xF5, 0xF9));
        adminBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        adminBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        adminBtn.addActionListener(e -> new AdminScheduleDialog(this).setVisible(true));
        navButtons.add(adminBtn);

        // Database status on right
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JPanel statusPill = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                boolean connected = DatabaseService.isConnected();
                g2.setColor(connected ? new Color(0xDC, 0xFC, 0xE7) : new Color(0xFE, 0xF3, 0xC7));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        statusPill.setOpaque(false);
        statusPill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        statusPill.setToolTipText("Click to view database connection status");

        boolean connected = DatabaseService.isConnected();
        JLabel statusDot = new JLabel(connected ? "● Online (PostgreSQL)" : "○ Offline (Local Seed)");
        statusDot.setFont(UITheme.FONT_TINY_BOLD);
        statusDot.setForeground(connected ? new Color(0x16, 0x65, 0x34) : new Color(0x92, 0x40, 0x0E));
        statusPill.add(statusDot);

        statusPill.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new AdminScheduleDialog(MainFrame.this).setVisible(true);
            }
        });

        rightPanel.add(statusPill);

        bar.add(brand, BorderLayout.WEST);
        bar.add(navButtons, BorderLayout.CENTER);
        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }

    private JButton createNavButton(String text, String targetCard) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_SMALL_BOLD);
        btn.setForeground(UITheme.NAVY);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> navigateTo(targetCard));
        return btn;
    }

    public void navigateTo(String cardName) {
        if ("STUDENT".equals(cardName)) {
            studentScreen.refreshData();
        }
        cardLayout.show(cardsPanel, cardName);
    }

    public void showTeacherSchedule(String employeeId, Teacher teacher) {
        teacherScheduleScreen.loadTeacher(teacher);
        cardLayout.show(cardsPanel, "TEACHER_SCHEDULE");
    }

    private void updateDbStatusBadge() {
        boolean connected = DatabaseService.isConnected();
        if (connected) {
            dbStatusBadge.setText("● PostgreSQL JDBC Connected");
            dbStatusBadge.setForeground(UITheme.SUCCESS_GREEN);
        } else {
            dbStatusBadge.setText("○ Offline / Fallback Seed Mode");
            dbStatusBadge.setForeground(new Color(0xD9, 0x77, 0x06));
        }
    }
}
