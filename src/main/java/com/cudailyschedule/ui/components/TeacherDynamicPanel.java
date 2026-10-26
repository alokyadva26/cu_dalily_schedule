package com.cudailyschedule.ui.components;

import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.model.ScheduleStatus;
import com.cudailyschedule.ui.theme.UITheme;

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

/**
 * Recreates the right sticky dynamic panel from cu-daily-schedule/src/components/teacher/TeacherScheduleView.tsx.
 */
public class TeacherDynamicPanel extends JPanel {
    private final JPanel containerCard;
    private final JLabel headerTagLabel;
    private final JLabel courseNameLabel;
    private final JLabel courseBadgeLabel;

    private final JLabel facultyValLabel;
    private final JLabel classroomValLabel;
    private final JLabel groupValLabel;
    private final JLabel sectionValLabel;
    private final JLabel timeValLabel;

    private final JPanel dayCompletePanel;
    private final JPanel activeContentPanel;

    public TeacherDynamicPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        containerCard = new JPanel(new BorderLayout()) {
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
        containerCard.setOpaque(false);
        containerCard.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // 1. Day Complete View
        dayCompletePanel = new JPanel();
        dayCompletePanel.setLayout(new BoxLayout(dayCompletePanel, BoxLayout.Y_AXIS));
        dayCompletePanel.setOpaque(false);

        JPanel checkCircle = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(new Color(0xF8, 0xF9, 0xFA));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.CARD_BORDER);
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        checkCircle.setPreferredSize(new Dimension(64, 64));
        checkCircle.setMaximumSize(new Dimension(64, 64));
        checkCircle.setOpaque(false);
        JLabel checkIcon = new JLabel("✓", JLabel.CENTER);
        checkIcon.setFont(new Font("Segoe UI", Font.BOLD, 32));
        checkIcon.setForeground(UITheme.SUCCESS_GREEN);
        checkCircle.add(checkIcon, BorderLayout.CENTER);

        JLabel completeTitle = new JLabel("DAY COMPLETE", JLabel.CENTER);
        completeTitle.setFont(UITheme.FONT_SUBTITLE);
        completeTitle.setForeground(UITheme.NAVY);
        completeTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel completeDesc = new JLabel("All classes are finished for today.", JLabel.CENTER);
        completeDesc.setFont(UITheme.FONT_BODY);
        completeDesc.setForeground(UITheme.TEXT_SECONDARY);
        completeDesc.setAlignmentX(CENTER_ALIGNMENT);

        checkCircle.setAlignmentX(CENTER_ALIGNMENT);
        dayCompletePanel.add(Box.createVerticalGlue());
        dayCompletePanel.add(checkCircle);
        dayCompletePanel.add(Box.createVerticalStrut(16));
        dayCompletePanel.add(completeTitle);
        dayCompletePanel.add(Box.createVerticalStrut(6));
        dayCompletePanel.add(completeDesc);
        dayCompletePanel.add(Box.createVerticalGlue());

        // 2. Active Class View
        activeContentPanel = new JPanel();
        activeContentPanel.setLayout(new BoxLayout(activeContentPanel, BoxLayout.Y_AXIS));
        activeContentPanel.setOpaque(false);

        headerTagLabel = new JLabel("📖 CURRENT CLASS");
        headerTagLabel.setFont(UITheme.FONT_TINY_BOLD);
        headerTagLabel.setForeground(UITheme.ACCENT);

        courseNameLabel = new JLabel("Course Name");
        courseNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        courseNameLabel.setForeground(UITheme.NAVY);

        courseBadgeLabel = new JLabel("24CSP-305 • Practical");
        courseBadgeLabel.setFont(UITheme.FONT_SMALL_BOLD);
        courseBadgeLabel.setForeground(UITheme.ACCENT);
        JPanel badgePill = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.ACCENT_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        badgePill.setOpaque(false);
        badgePill.add(courseBadgeLabel);

        // Metadata key-value table
        JPanel metaTable = new JPanel(new GridLayout(5, 1, 0, 8));
        metaTable.setOpaque(false);
        metaTable.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        facultyValLabel = new JLabel("-", JLabel.RIGHT);
        classroomValLabel = new JLabel("-", JLabel.RIGHT);
        groupValLabel = new JLabel("-", JLabel.RIGHT);
        sectionValLabel = new JLabel("-", JLabel.RIGHT);
        timeValLabel = new JLabel("-", JLabel.RIGHT);

        classroomValLabel.setForeground(UITheme.ACCENT);
        classroomValLabel.setFont(UITheme.FONT_BODY_BOLD);

        metaTable.add(createRow("👤 Faculty", facultyValLabel));
        metaTable.add(createRow("📍 Classroom", classroomValLabel));
        metaTable.add(createRow("👥 Group", groupValLabel));
        metaTable.add(createRow("# Section", sectionValLabel));
        metaTable.add(createRow("⏰ Time", timeValLabel));

        activeContentPanel.add(headerTagLabel);
        activeContentPanel.add(Box.createVerticalStrut(8));
        activeContentPanel.add(courseNameLabel);
        activeContentPanel.add(Box.createVerticalStrut(8));
        activeContentPanel.add(badgePill);
        activeContentPanel.add(Box.createVerticalStrut(12));
        activeContentPanel.add(metaTable);

        containerCard.add(activeContentPanel, BorderLayout.CENTER);
        add(containerCard, BorderLayout.CENTER);
    }

    private JPanel createRow(String label, JLabel valLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF1, 0xF5, 0xF9)));
        JLabel key = new JLabel(label);
        key.setFont(UITheme.FONT_SMALL);
        key.setForeground(UITheme.TEXT_MUTED);

        valLabel.setFont(UITheme.FONT_BODY_BOLD);
        valLabel.setForeground(UITheme.NAVY);

        row.add(key, BorderLayout.WEST);
        row.add(valLabel, BorderLayout.EAST);
        return row;
    }

    public void updateStatus(ScheduleStatus status, String teacherName, String teacherId) {
        if (status.isFinished()) {
            containerCard.removeAll();
            containerCard.add(dayCompletePanel, BorderLayout.CENTER);
        } else {
            ScheduleEntry target = (status.getCurrentClass() != null) ? status.getCurrentClass() : status.getNextClass();
            if (target == null) {
                containerCard.removeAll();
                containerCard.add(dayCompletePanel, BorderLayout.CENTER);
            } else {
                containerCard.removeAll();
                containerCard.add(activeContentPanel, BorderLayout.CENTER);

                boolean isCurrent = (status.getCurrentClass() != null);
                headerTagLabel.setText(isCurrent ? "📖 CURRENT CLASS" : "📖 NEXT CLASS");
                courseNameLabel.setText("<html><body style='width: 240px;'>" + target.getCourseName() + "</body></html>");
                courseBadgeLabel.setText(target.getCourseCode() + " • " + target.getCourseType());

                facultyValLabel.setText(teacherName != null ? teacherName + " (" + teacherId + ")" : "-");
                classroomValLabel.setText(target.getFormattedRoom());
                groupValLabel.setText(target.getStudentGroup() != null && !target.getStudentGroup().equals("empty") ? "Group " + target.getStudentGroup() : target.getSection());
                sectionValLabel.setText(target.getSection() != null ? target.getSection() : "-");
                timeValLabel.setText(target.getStartTime() + " - " + target.getEndTime());
            }
        }
        containerCard.revalidate();
        containerCard.repaint();
    }
}
