package com.cudailyschedule.ui.components;

import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.model.ScheduleStatus;
import com.cudailyschedule.model.Teacher;
import com.cudailyschedule.service.ScheduleTimingService;
import com.cudailyschedule.ui.theme.UITheme;
import com.cudailyschedule.util.TimeUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import java.time.LocalTime;
import java.util.List;

/**
 * Recreates cu-daily-schedule/src/components/teacher/TeacherProfileHeader.tsx.
 * Displays teacher avatar, credentials, and live countdown timer widget.
 */
public class TeacherProfileHeaderPanel extends JPanel {
    private final JLabel avatarLabel;
    private final JLabel nameLabel;
    private final JLabel idDeptLabel;
    private final JLabel roleLabel;

    private final JPanel timerWidget;
    private final JLabel timerTitleLabel;
    private final JLabel unit1ValLabel;
    private final JLabel unit1UnitLabel;
    private final JLabel unit2ValLabel;
    private final JLabel unit2UnitLabel;

    private final ScheduleTimingService timingService = new ScheduleTimingService();

    public TeacherProfileHeaderPanel() {
        setLayout(new BorderLayout(24, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JPanel card = new JPanel(new BorderLayout(24, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(UITheme.CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // Left: Avatar + Details
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setOpaque(false);

        // Circular red avatar
        JPanel avatarCircle = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatarCircle.setPreferredSize(new Dimension(64, 64));
        avatarCircle.setOpaque(false);
        avatarLabel = new JLabel("N", JLabel.CENTER);
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        avatarLabel.setForeground(Color.WHITE);
        avatarCircle.add(avatarLabel, BorderLayout.CENTER);

        JPanel infoBlock = new JPanel(new GridLayout(3, 1, 0, 2));
        infoBlock.setOpaque(false);

        nameLabel = new JLabel("Teacher Name");
        nameLabel.setFont(UITheme.FONT_SUBTITLE);
        nameLabel.setForeground(UITheme.NAVY);

        idDeptLabel = new JLabel("E19761 • CSE");
        idDeptLabel.setFont(UITheme.FONT_BODY);
        idDeptLabel.setForeground(UITheme.TEXT_MUTED);

        roleLabel = new JLabel("Assistant Professor");
        roleLabel.setFont(UITheme.FONT_SMALL);
        roleLabel.setForeground(UITheme.TEXT_MUTED);

        infoBlock.add(nameLabel);
        infoBlock.add(idDeptLabel);
        infoBlock.add(roleLabel);

        leftPanel.add(avatarCircle);
        leftPanel.add(infoBlock);

        // Right: Dynamic Countdown Widget
        timerWidget = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0)) {
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
        timerWidget.setOpaque(false);
        timerWidget.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        // Clock Icon badge
        JPanel clockBadge = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(UITheme.ACCENT_RING);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        clockBadge.setPreferredSize(new Dimension(40, 40));
        clockBadge.setOpaque(false);
        JLabel clockIcon = new JLabel("⏰", JLabel.CENTER);
        clockIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        clockBadge.add(clockIcon, BorderLayout.CENTER);

        JPanel timerText = new JPanel();
        timerText.setLayout(new GridLayout(2, 1, 0, 1));
        timerText.setOpaque(false);

        timerTitleLabel = new JLabel("CLASS ENDS IN");
        timerTitleLabel.setFont(UITheme.FONT_TINY_BOLD);
        timerTitleLabel.setForeground(UITheme.TEXT_MUTED);

        JPanel numbersRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        numbersRow.setOpaque(false);

        unit1ValLabel = new JLabel("45");
        unit1ValLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        unit1ValLabel.setForeground(UITheme.ACCENT);

        unit1UnitLabel = new JLabel("min");
        unit1UnitLabel.setFont(UITheme.FONT_TINY_BOLD);
        unit1UnitLabel.setForeground(UITheme.TEXT_MUTED);

        unit2ValLabel = new JLabel("20");
        unit2ValLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        unit2ValLabel.setForeground(UITheme.ACCENT);

        unit2UnitLabel = new JLabel("sec");
        unit2UnitLabel.setFont(UITheme.FONT_TINY_BOLD);
        unit2UnitLabel.setForeground(UITheme.TEXT_MUTED);

        numbersRow.add(unit1ValLabel);
        numbersRow.add(unit1UnitLabel);
        numbersRow.add(Box.createHorizontalStrut(4));
        numbersRow.add(unit2ValLabel);
        numbersRow.add(unit2UnitLabel);

        timerText.add(timerTitleLabel);
        timerText.add(numbersRow);

        timerWidget.add(clockBadge);
        timerWidget.add(timerText);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(timerWidget, BorderLayout.EAST);
        add(card, BorderLayout.CENTER);
    }

    public void updateTeacher(Teacher teacher, List<ScheduleEntry> todayEntries, LocalTime now) {
        if (teacher == null) return;
        avatarLabel.setText(teacher.getEmployeeName().substring(0, 1).toUpperCase());
        nameLabel.setText(teacher.getEmployeeName());
        idDeptLabel.setText(teacher.getEmployeeId() + " • " + (teacher.getDepartment() != null ? teacher.getDepartment() : "Department"));

        // Update Timer Widget
        ScheduleStatus status = timingService.calculateScheduleStatus(todayEntries, now);
        if (status.isFinished() || (status.getCurrentClass() == null && status.getNextClass() == null)) {
            timerWidget.setVisible(false);
        } else {
            timerWidget.setVisible(true);
            boolean isCurrent = (status.getCurrentClass() != null);
            timerTitleLabel.setText(isCurrent ? "CLASS ENDS IN" : "NEXT CLASS IN");
            TimeUtils.FormattedCountdown fmt = TimeUtils.formatDetailedCountdown(status.getRemainingSeconds());
            unit1ValLabel.setText(String.valueOf(fmt.unit1));
            unit1UnitLabel.setText(fmt.label1);
            unit2ValLabel.setText(String.valueOf(fmt.unit2));
            unit2UnitLabel.setText(fmt.label2);
        }
    }
}
