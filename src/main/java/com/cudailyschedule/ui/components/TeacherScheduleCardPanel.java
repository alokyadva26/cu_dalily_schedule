package com.cudailyschedule.ui.components;

import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.service.ScheduleTimingService.SessionState;
import com.cudailyschedule.ui.theme.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

/**
 * Recreates cu-daily-schedule/src/components/teacher/TeacherScheduleCard.tsx.
 */
public class TeacherScheduleCardPanel extends JPanel {
    private final ScheduleEntry entry;
    private final SessionState state;
    private final boolean isLast;

    public TeacherScheduleCardPanel(ScheduleEntry entry, SessionState state, boolean isLast) {
        this.entry = entry;
        this.state = state;
        this.isLast = isLast;

        setLayout(new BorderLayout(16, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        boolean isHighlighted = (state == SessionState.ACTIVE || state == SessionState.NEXT);
        boolean isCompleted = (state == SessionState.COMPLETED);

        // 1. Timeline Column
        JPanel timelineColumn = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);

                int width = getWidth();
                int height = getHeight();
                int lineX = width - 12;

                // Vertical connecting line
                if (!isLast) {
                    g2.setColor(UITheme.TIMELINE_LINE);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawLine(lineX, 26, lineX, height + 10);
                }

                // Node
                int nodeY = 14;
                int r = 7;
                if (isHighlighted) {
                    g2.setColor(UITheme.ACCENT);
                    g2.fillOval(lineX - r, nodeY, r * 2, r * 2);
                } else if (isCompleted) {
                    g2.setColor(UITheme.SUCCESS_GREEN);
                    g2.fillOval(lineX - r, nodeY, r * 2, r * 2);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawLine(lineX - 3, nodeY + 7, lineX - 1, nodeY + 10);
                    g2.drawLine(lineX - 1, nodeY + 10, lineX + 4, nodeY + 4);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(lineX - r, nodeY, r * 2, r * 2);
                    g2.setColor(UITheme.TIMELINE_LINE);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(lineX - r, nodeY, r * 2, r * 2);
                }

                g2.dispose();
            }
        };
        timelineColumn.setPreferredSize(new Dimension(84, 80));
        timelineColumn.setLayout(new GridLayout(2, 1, 0, 2));
        timelineColumn.setOpaque(false);

        JLabel startLabel = new JLabel(entry.getStartTime(), JLabel.RIGHT);
        startLabel.setFont(isHighlighted ? UITheme.FONT_SMALL_BOLD : UITheme.FONT_SMALL);
        startLabel.setForeground(UITheme.NAVY);

        JLabel endLabel = new JLabel(entry.getEndTime(), JLabel.RIGHT);
        endLabel.setFont(UITheme.FONT_SMALL);
        endLabel.setForeground(UITheme.TEXT_MUTED);

        timelineColumn.add(startLabel);
        timelineColumn.add(endLabel);

        // 2. Card Content
        JPanel card = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(isHighlighted ? UITheme.ACCENT : UITheme.CARD_BORDER);
                g2.setStroke(new BasicStroke(isHighlighted ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        // Subject Icon Badge
        JPanel iconBadge = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.ACCENT_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.ACCENT_RING);
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        iconBadge.setPreferredSize(new Dimension(46, 46));
        iconBadge.setOpaque(false);
        JLabel iconLbl = new JLabel("📖", JLabel.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconBadge.add(iconLbl, BorderLayout.CENTER);

        // Center Details
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 0, 3));
        centerPanel.setOpaque(false);

        // Status Label (e.g. CURRENT CLASS / NEXT CLASS)
        if (state == SessionState.ACTIVE) {
            JLabel statusLbl = new JLabel("CURRENT CLASS");
            statusLbl.setFont(UITheme.FONT_TINY_BOLD);
            statusLbl.setForeground(UITheme.ACCENT);
            centerPanel.add(statusLbl);
        } else if (state == SessionState.NEXT) {
            JLabel statusLbl = new JLabel("NEXT CLASS");
            statusLbl.setFont(UITheme.FONT_TINY_BOLD);
            statusLbl.setForeground(UITheme.TEXT_MUTED);
            centerPanel.add(statusLbl);
        }

        JLabel titleLbl = new JLabel(entry.getCourseName());
        titleLbl.setFont(UITheme.FONT_BODY_BOLD);
        titleLbl.setForeground(UITheme.NAVY);

        String codeType = (entry.getCourseCode() != null ? entry.getCourseCode() : "") +
                (entry.getCourseType() != null ? " • " + entry.getCourseType() : "");
        JLabel codeTypeLbl = new JLabel(codeType);
        codeTypeLbl.setFont(UITheme.FONT_SMALL_BOLD);
        codeTypeLbl.setForeground(UITheme.ACCENT);

        // Meta tags: Location, Group, Section
        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        metaRow.setOpaque(false);

        JLabel locLbl = new JLabel("📍 " + entry.getFormattedRoom());
        locLbl.setFont(UITheme.FONT_SMALL);
        locLbl.setForeground(UITheme.TEXT_MUTED);
        metaRow.add(locLbl);

        if (entry.getStudentGroup() != null && !entry.getStudentGroup().equalsIgnoreCase("empty")) {
            JLabel groupLbl = new JLabel("👥 Group " + entry.getStudentGroup());
            groupLbl.setFont(UITheme.FONT_SMALL);
            groupLbl.setForeground(UITheme.TEXT_MUTED);
            metaRow.add(groupLbl);
        }

        if (entry.getSection() != null) {
            JLabel secLbl = new JLabel("# " + entry.getSection());
            secLbl.setFont(UITheme.FONT_SMALL);
            secLbl.setForeground(UITheme.TEXT_MUTED);
            metaRow.add(secLbl);
        }

        centerPanel.add(titleLbl);
        centerPanel.add(codeTypeLbl);

        JPanel fullCenter = new JPanel(new BorderLayout(0, 6));
        fullCenter.setOpaque(false);
        fullCenter.add(centerPanel, BorderLayout.CENTER);
        fullCenter.add(metaRow, BorderLayout.SOUTH);

        card.add(iconBadge, BorderLayout.WEST);
        card.add(fullCenter, BorderLayout.CENTER);

        add(timelineColumn, BorderLayout.WEST);
        add(card, BorderLayout.CENTER);
    }
}
