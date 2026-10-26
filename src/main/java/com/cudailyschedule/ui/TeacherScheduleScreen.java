package com.cudailyschedule.ui;

import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.model.ScheduleStatus;
import com.cudailyschedule.model.Teacher;
import com.cudailyschedule.service.ScheduleTimingService;
import com.cudailyschedule.service.ScheduleTimingService.SessionState;
import com.cudailyschedule.service.TeacherService;
import com.cudailyschedule.ui.components.EmptyStatePanel;
import com.cudailyschedule.ui.components.TeacherDynamicPanel;
import com.cudailyschedule.ui.components.TeacherProfileHeaderPanel;
import com.cudailyschedule.ui.components.TeacherScheduleCardPanel;
import com.cudailyschedule.ui.theme.UITheme;
import com.cudailyschedule.util.TimeUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Recreates cu-daily-schedule/src/app/teacher/[employeeId]/page.tsx.
 * Provides teacher timetable timeline, profile header, real-time status card, and countdowns.
 */
public class TeacherScheduleScreen extends JPanel {
    private final TeacherService teacherService;
    private final ScheduleTimingService timingService = new ScheduleTimingService();
    private final Consumer<String> navigationHandler;

    private Teacher currentTeacher;
    private List<ScheduleEntry> allEntries = new ArrayList<>();
    private List<ScheduleEntry> todayEntries = new ArrayList<>();

    private final TeacherProfileHeaderPanel headerPanel;
    private final JPanel timelineContainer;
    private final TeacherDynamicPanel dynamicPanel;
    private Timer clockTimer;

    public TeacherScheduleScreen(TeacherService teacherService, Consumer<String> navigationHandler) {
        this.teacherService = teacherService;
        this.navigationHandler = navigationHandler;

        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // TOP NAV BAR
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 12));
        navBar.setOpaque(false);

        JButton backBtn = new JButton("← Back to Search") {
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
        backBtn.addActionListener(e -> navigationHandler.accept("TEACHER_SEARCH"));
        navBar.add(backBtn);

        // HEADER
        headerPanel = new TeacherProfileHeaderPanel();

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(navBar, BorderLayout.NORTH);
        topContainer.add(headerPanel, BorderLayout.CENTER);

        // TWO-COLUMN MAIN CONTENT
        JPanel centerGrid = new JPanel(new BorderLayout(24, 0));
        centerGrid.setOpaque(false);
        centerGrid.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));

        // Left Timeline Column
        JPanel leftSide = new JPanel(new BorderLayout(0, 10));
        leftSide.setOpaque(false);

        timelineContainer = new JPanel();
        timelineContainer.setLayout(new BoxLayout(timelineContainer, BoxLayout.Y_AXIS));
        timelineContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(timelineContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        leftSide.add(scrollPane, BorderLayout.CENTER);

        // Right Dynamic Panel
        dynamicPanel = new TeacherDynamicPanel();
        dynamicPanel.setPreferredSize(new Dimension(380, 500));

        centerGrid.add(leftSide, BorderLayout.CENTER);
        centerGrid.add(dynamicPanel, BorderLayout.EAST);

        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(centerGrid, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        initTimer();
    }

    private void initTimer() {
        clockTimer = new Timer(1000, e -> updateClockTick());
        clockTimer.start();
    }

    public void loadTeacher(Teacher teacher) {
        this.currentTeacher = teacher;
        this.allEntries = teacherService.getTeacherSchedule(teacher);

        ZonedDateTime now = TimeUtils.getNowInKolkata();
        String currentDay = now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        this.todayEntries = allEntries.stream()
                .filter(e -> currentDay.equalsIgnoreCase(e.getDayOfWeek()))
                .sorted(Comparator.comparing(e -> TimeUtils.parseTime(e.getStartTime())))
                .collect(Collectors.toList());

        rebuildView();
    }

    private void updateClockTick() {
        if (currentTeacher != null) {
            LocalTime nowTime = TimeUtils.getNowInKolkata().toLocalTime();
            headerPanel.updateTeacher(currentTeacher, todayEntries, nowTime);
            ScheduleStatus status = timingService.calculateScheduleStatus(todayEntries, nowTime);
            dynamicPanel.updateStatus(status, currentTeacher.getEmployeeName(), currentTeacher.getEmployeeId());
        }
    }

    private void rebuildView() {
        timelineContainer.removeAll();
        LocalTime nowTime = TimeUtils.getNowInKolkata().toLocalTime();

        headerPanel.updateTeacher(currentTeacher, todayEntries, nowTime);

        // Section Title
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(BorderFactory.createEmptyBorder(6, 6, 12, 6));

        JLabel titleLbl = new JLabel("📅 Today's Schedule");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(UITheme.NAVY);
        headerRow.add(titleLbl, BorderLayout.WEST);
        timelineContainer.add(headerRow);

        if (todayEntries == null || todayEntries.isEmpty()) {
            timelineContainer.add(new EmptyStatePanel());
            dynamicPanel.updateStatus(ScheduleStatus.finished(), currentTeacher.getEmployeeName(), currentTeacher.getEmployeeId());
            revalidate();
            repaint();
            return;
        }

        ScheduleStatus status = timingService.calculateScheduleStatus(todayEntries, nowTime);
        dynamicPanel.updateStatus(status, currentTeacher.getEmployeeName(), currentTeacher.getEmployeeId());

        // Check if all classes completed
        if (status.isFinished()) {
            JPanel doneCard = new JPanel(new BorderLayout(0, 6)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    UITheme.enableAntiAliasing(g2);
                    g2.setColor(new Color(0xF8, 0xF9, 0xFA));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    g2.setColor(UITheme.CARD_BORDER);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                    g2.dispose();
                }
            };
            doneCard.setOpaque(false);
            doneCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            JLabel doneTitle = new JLabel("DAY COMPLETE", JLabel.CENTER);
            doneTitle.setFont(UITheme.FONT_SUBTITLE);
            doneTitle.setForeground(UITheme.NAVY);
            JLabel doneSub = new JLabel("All classes are finished for today.", JLabel.CENTER);
            doneSub.setFont(UITheme.FONT_BODY);
            doneSub.setForeground(UITheme.TEXT_SECONDARY);
            doneCard.add(doneTitle, BorderLayout.NORTH);
            doneCard.add(doneSub, BorderLayout.CENTER);
            timelineContainer.add(doneCard);
            timelineContainer.add(Box.createVerticalStrut(14));
        }

        // Build schedule cards
        for (int i = 0; i < todayEntries.size(); i++) {
            ScheduleEntry entry = todayEntries.get(i);
            boolean isLast = (i == todayEntries.size() - 1);

            SessionState sessionState = SessionState.NORMAL;
            if (status.getCurrentClass() != null && entry.getId().equals(status.getCurrentClass().getId())) {
                sessionState = SessionState.ACTIVE;
            } else if (status.getNextClass() != null && entry.getId().equals(status.getNextClass().getId())) {
                sessionState = SessionState.NEXT;
            } else {
                LocalTime end = TimeUtils.parseTime(entry.getEndTime());
                if (!nowTime.isBefore(end)) {
                    sessionState = SessionState.COMPLETED;
                }
            }

            TeacherScheduleCardPanel card = new TeacherScheduleCardPanel(entry, sessionState, isLast);
            timelineContainer.add(card);
        }

        revalidate();
        repaint();
    }
}
