package com.cudailyschedule.ui;

import com.cudailyschedule.model.ClassSession;
import com.cudailyschedule.service.ScheduleTimingService;
import com.cudailyschedule.service.ScheduleTimingService.SessionState;
import com.cudailyschedule.service.StudentService;
import com.cudailyschedule.ui.components.ClassDetailsPanel;
import com.cudailyschedule.ui.components.EmptyStatePanel;
import com.cudailyschedule.ui.components.EndOfDayPanel;
import com.cudailyschedule.ui.components.FooterPanel;
import com.cudailyschedule.ui.components.HeaderPanel;
import com.cudailyschedule.ui.components.ScheduleCardPanel;
import com.cudailyschedule.ui.theme.UITheme;
import com.cudailyschedule.util.TimeUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
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
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Recreates cu-daily-schedule/src/app/student/page.tsx.
 * Manages daily student timetable, real-time active class countdown, end-of-day transitions, and tomorrow preview.
 */
public class StudentScheduleScreen extends JPanel {
    private final StudentService studentService;
    private final ScheduleTimingService timingService = new ScheduleTimingService();
    private final Consumer<String> navigationHandler;

    private final HeaderPanel headerPanel;
    private final JPanel timelineContainer;
    private final ClassDetailsPanel detailsPanel;
    private final FooterPanel footerPanel;

    private List<ClassSession> currentClasses;
    private ClassSession selectedSession;
    private boolean isViewingTomorrow = false;
    private Timer clockTimer;

    public StudentScheduleScreen(StudentService studentService, Consumer<String> navigationHandler) {
        this.studentService = studentService;
        this.navigationHandler = navigationHandler;

        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // TOP BAR with Home Button & Header
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);

        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 12));
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

        headerPanel = new HeaderPanel();
        topContainer.add(navBar, BorderLayout.NORTH);
        topContainer.add(headerPanel, BorderLayout.CENTER);

        // CENTER SPLIT: Left Timeline (7 cols) + Right Details (5 cols)
        JPanel centerGrid = new JPanel(new BorderLayout(24, 0));
        centerGrid.setOpaque(false);
        centerGrid.setBorder(BorderFactory.createEmptyBorder(0, 24, 12, 24));

        // Left Timeline
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

        // Right Details + Footer
        JPanel rightSide = new JPanel(new BorderLayout(0, 12));
        rightSide.setPreferredSize(new Dimension(420, 500));
        rightSide.setOpaque(false);

        detailsPanel = new ClassDetailsPanel();
        footerPanel = new FooterPanel(this::refreshData);

        rightSide.add(detailsPanel, BorderLayout.CENTER);
        rightSide.add(footerPanel, BorderLayout.SOUTH);

        centerGrid.add(leftSide, BorderLayout.CENTER);
        centerGrid.add(rightSide, BorderLayout.EAST);

        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(centerGrid, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        initTimer();
        refreshData();
    }

    private void initTimer() {
        clockTimer = new Timer(1000, e -> updateClockTick());
        clockTimer.start();
    }

    public void refreshData() {
        headerPanel.refreshClock();
        ZonedDateTime now = TimeUtils.getNowInKolkata();
        String dayOfWeek = now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        if (isViewingTomorrow) {
            ZonedDateTime tom = now.plusDays(1);
            String tomDay = tom.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            currentClasses = studentService.getClassesForDay(tomDay);
        } else {
            currentClasses = studentService.getClassesForDay(dayOfWeek);
        }

        rebuildTimeline();
    }

    private void updateClockTick() {
        if (!isViewingTomorrow) {
            LocalTime nowTime = TimeUtils.getNowInKolkata().toLocalTime();
            if (selectedSession != null) {
                detailsPanel.displaySession(selectedSession, nowTime);
            }
        }
    }

    private void rebuildTimeline() {
        timelineContainer.removeAll();
        ZonedDateTime now = TimeUtils.getNowInKolkata();
        String dayOfWeek = now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        LocalTime nowTime = now.toLocalTime();

        // Check weekend
        if (!isViewingTomorrow && (dayOfWeek.equalsIgnoreCase("Saturday") || dayOfWeek.equalsIgnoreCase("Sunday"))) {
            timelineContainer.add(new EmptyStatePanel());
            detailsPanel.displaySession(null, nowTime);
            revalidate();
            repaint();
            return;
        }

        if (currentClasses == null || currentClasses.isEmpty()) {
            timelineContainer.add(new EmptyStatePanel());
            detailsPanel.displaySession(null, nowTime);
            revalidate();
            repaint();
            return;
        }

        // Section header
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(BorderFactory.createEmptyBorder(6, 6, 12, 6));

        JLabel titleLbl = new JLabel(isViewingTomorrow ? "Tomorrow's Schedule" : "📅 Today's Schedule");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(UITheme.NAVY);
        headerRow.add(titleLbl, BorderLayout.WEST);

        if (isViewingTomorrow) {
            JButton backToTodayBtn = new JButton("← Back to Today");
            backToTodayBtn.setFont(UITheme.FONT_SMALL_BOLD);
            backToTodayBtn.setForeground(UITheme.ACCENT);
            backToTodayBtn.setContentAreaFilled(false);
            backToTodayBtn.setBorderPainted(false);
            backToTodayBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            backToTodayBtn.addActionListener(e -> {
                isViewingTomorrow = false;
                refreshData();
            });
            headerRow.add(backToTodayBtn, BorderLayout.EAST);
        }

        timelineContainer.add(headerRow);

        // Find active and next classes
        ClassSession activeSession = null;
        ClassSession nextSession = null;
        boolean allFinished = true;

        for (ClassSession s : currentClasses) {
            LocalTime start = TimeUtils.parseTime(s.getStartTime());
            LocalTime end = TimeUtils.parseTime(s.getEndTime());
            if (!nowTime.isBefore(start) && nowTime.isBefore(end)) {
                activeSession = s;
                allFinished = false;
            } else if (nowTime.isBefore(start)) {
                if (nextSession == null) nextSession = s;
                allFinished = false;
            }
        }

        // Show End of Day state if all finished
        if (!isViewingTomorrow && allFinished) {
            timelineContainer.add(new EndOfDayPanel(() -> {
                isViewingTomorrow = true;
                refreshData();
            }));
            timelineContainer.add(Box.createVerticalStrut(16));
        }

        // Select default session if not set
        if (selectedSession == null || !currentClasses.contains(selectedSession)) {
            if (activeSession != null) {
                selectedSession = activeSession;
            } else if (nextSession != null) {
                selectedSession = nextSession;
            } else if (!currentClasses.isEmpty()) {
                selectedSession = currentClasses.get(0);
            }
        }

        // Build cards
        for (int i = 0; i < currentClasses.size(); i++) {
            ClassSession session = currentClasses.get(i);
            boolean isLast = (i == currentClasses.size() - 1);
            boolean isSelected = (session == selectedSession);

            SessionState state = SessionState.NORMAL;
            String countdown = null;

            if (!isViewingTomorrow) {
                LocalTime start = TimeUtils.parseTime(session.getStartTime());
                LocalTime end = TimeUtils.parseTime(session.getEndTime());

                if (session == activeSession) {
                    state = SessionState.ACTIVE;
                    countdown = TimeUtils.getCountdownString(end, nowTime);
                } else if (session == nextSession) {
                    state = SessionState.NEXT;
                    countdown = TimeUtils.getCountdownString(start, nowTime);
                } else if (!nowTime.isBefore(end)) {
                    state = SessionState.COMPLETED;
                }
            }

            ScheduleCardPanel card = new ScheduleCardPanel(session, state, countdown, isLast, isSelected, s -> {
                selectedSession = s;
                detailsPanel.displaySession(s, nowTime);
                rebuildTimeline();
            });

            timelineContainer.add(card);
        }

        if (selectedSession != null) {
            detailsPanel.displaySession(selectedSession, nowTime);
        }

        revalidate();
        repaint();
    }
}
