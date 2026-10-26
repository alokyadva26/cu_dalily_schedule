package com.cudailyschedule.ui.components;

import com.cudailyschedule.model.ClassSession;
import com.cudailyschedule.ui.theme.UITheme;
import com.cudailyschedule.util.SubjectRegistry;
import com.cudailyschedule.util.TimeUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.time.Duration;
import java.time.LocalTime;

/**
 * ClassDetailsPanel recreates cu-daily-schedule/src/components/ClassDetails.tsx.
 * Displays large subject overview, faculty, room badge, and live circular countdown ring.
 */
public class ClassDetailsPanel extends JPanel {
    private ClassSession currentSession;
    private final JPanel contentPanel;
    private final JLabel timePillLabel;
    private final JLabel categoryBadge;
    private final JLabel subjectTitle;
    private final JLabel facultyLabel;
    private final JLabel classroomLabel;
    private final CircularProgressIndicator progressRing;
    private final JLabel endsAtLabel;
    private final JPanel countdownContainer;
    private final JLabel emptyStateLabel;

    public ClassDetailsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Outer rounded card
        JPanel card = new JPanel(new BorderLayout()) {
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
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        emptyStateLabel = new JLabel("Select a class to view details", JLabel.CENTER);
        emptyStateLabel.setFont(UITheme.FONT_SUBTITLE);
        emptyStateLabel.setForeground(UITheme.TEXT_SECONDARY);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Top Time Pill
        JPanel pillWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pillWrapper.setOpaque(false);
        JPanel timePill = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.ACCENT_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        timePill.setOpaque(false);
        timePillLabel = new JLabel("🕒 09:30 AM - 10:20 AM");
        timePillLabel.setFont(UITheme.FONT_BODY_BOLD);
        timePillLabel.setForeground(UITheme.ACCENT);
        timePill.add(timePillLabel);
        pillWrapper.add(timePill);

        // Category Tag (e.g. CURRENT CLASS / CLASS)
        categoryBadge = new JLabel("📖 CLASS");
        categoryBadge.setFont(UITheme.FONT_TINY_BOLD);
        categoryBadge.setForeground(UITheme.ACCENT);

        // Subject Title
        subjectTitle = new JLabel("Subject Name");
        subjectTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        subjectTitle.setForeground(UITheme.TEXT_PRIMARY);

        // Separator
        JPanel separator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(UITheme.CARD_BORDER);
                g.fillRect(0, 0, 48, 2);
            }
        };
        separator.setPreferredSize(new Dimension(48, 2));
        separator.setMaximumSize(new Dimension(48, 2));
        separator.setOpaque(false);

        // Faculty Section
        JLabel facultyHeader = new JLabel("👤 FACULTY");
        facultyHeader.setFont(UITheme.FONT_TINY_BOLD);
        facultyHeader.setForeground(UITheme.TEXT_SECONDARY);
        facultyLabel = new JLabel("Faculty Name");
        facultyLabel.setFont(UITheme.FONT_HEADER);
        facultyLabel.setForeground(UITheme.TEXT_PRIMARY);

        // Classroom Section
        JLabel roomHeader = new JLabel("📍 CLASSROOM");
        roomHeader.setFont(UITheme.FONT_TINY_BOLD);
        roomHeader.setForeground(UITheme.TEXT_SECONDARY);
        classroomLabel = new JLabel("Block-B1-517");
        classroomLabel.setFont(UITheme.FONT_BODY_BOLD);
        classroomLabel.setForeground(UITheme.ACCENT);

        JPanel roomBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.ACCENT_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(UITheme.ACCENT_RING);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        roomBadge.setOpaque(false);
        roomBadge.add(classroomLabel);

        // Countdown Timer Container
        countdownContainer = new JPanel(new BorderLayout(16, 0));
        countdownContainer.setOpaque(false);
        countdownContainer.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        progressRing = new CircularProgressIndicator();

        JPanel endsAtCard = new JPanel(new GridLayout(2, 1, 0, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.ACCENT_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        endsAtCard.setOpaque(false);
        endsAtCard.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        JLabel endsAtHeader = new JLabel("Ends At");
        endsAtHeader.setFont(UITheme.FONT_TINY_BOLD);
        endsAtHeader.setForeground(UITheme.TEXT_SECONDARY);
        endsAtLabel = new JLabel("10:20 AM");
        endsAtLabel.setFont(UITheme.FONT_BODY_BOLD);
        endsAtLabel.setForeground(UITheme.ACCENT);
        endsAtCard.add(endsAtHeader);
        endsAtCard.add(endsAtLabel);

        countdownContainer.add(progressRing, BorderLayout.WEST);
        countdownContainer.add(endsAtCard, BorderLayout.CENTER);

        // Assemble content
        contentPanel.add(pillWrapper);
        contentPanel.add(Box.createVerticalStrut(16));
        contentPanel.add(categoryBadge);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(subjectTitle);
        contentPanel.add(Box.createVerticalStrut(14));
        contentPanel.add(separator);
        contentPanel.add(Box.createVerticalStrut(14));
        contentPanel.add(facultyHeader);
        contentPanel.add(Box.createVerticalStrut(2));
        contentPanel.add(facultyLabel);
        contentPanel.add(Box.createVerticalStrut(14));
        contentPanel.add(roomHeader);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(roomBadge);
        contentPanel.add(Box.createVerticalStrut(16));
        contentPanel.add(countdownContainer);

        card.add(emptyStateLabel, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }

    public void displaySession(ClassSession session, LocalTime currentTime) {
        this.currentSession = session;
        if (session == null) {
            emptyStateLabel.setVisible(true);
            contentPanel.setVisible(false);
            return;
        }

        emptyStateLabel.setVisible(false);
        removeAll();

        JPanel card = new JPanel(new BorderLayout()) {
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
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.add(contentPanel, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
        contentPanel.setVisible(true);

        timePillLabel.setText("🕒 " + session.getStartTime() + " - " + session.getEndTime());
        String friendlyName = session.isBreak() ? "Break" : SubjectRegistry.getSubjectName(session.getSubject());
        subjectTitle.setText("<html><body style='width: 260px;'>" + friendlyName + "</body></html>");
        facultyLabel.setText(session.getFaculty() != null ? session.getFaculty() : "Faculty Member");
        classroomLabel.setText(session.getRoom() != null ? session.getRoom() : "Classroom");

        // Calculate progress if active
        LocalTime start = TimeUtils.parseTime(session.getStartTime());
        LocalTime end = TimeUtils.parseTime(session.getEndTime());
        boolean isOngoing = !currentTime.isBefore(start) && currentTime.isBefore(end);

        if (isOngoing) {
            categoryBadge.setText("🔥 CURRENT CLASS");
            countdownContainer.setVisible(true);
            long totalSecs = Duration.between(start, end).getSeconds();
            long remSecs = Duration.between(currentTime, end).getSeconds();
            progressRing.setProgress(remSecs, totalSecs);
            endsAtLabel.setText(session.getEndTime());
        } else {
            categoryBadge.setText("📖 CLASS");
            countdownContainer.setVisible(false);
        }

        revalidate();
        repaint();
    }
}
