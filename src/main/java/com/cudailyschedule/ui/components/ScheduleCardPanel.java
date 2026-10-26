package com.cudailyschedule.ui.components;

import com.cudailyschedule.model.ClassSession;
import com.cudailyschedule.service.ScheduleTimingService.SessionState;
import com.cudailyschedule.ui.theme.UITheme;
import com.cudailyschedule.util.SubjectRegistry;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * ScheduleCardPanel recreates cu-daily-schedule/src/components/ScheduleCard.tsx.
 * Renders an interactive timeline entry with status nodes, time bounds, and active badges.
 */
public class ScheduleCardPanel extends JPanel {
    private final ClassSession session;
    private final SessionState state;
    private final String countdown;
    private final boolean isLast;
    private boolean isSelected;
    private boolean isHovered;

    public ScheduleCardPanel(ClassSession session, SessionState state, String countdown, boolean isLast, boolean isSelected, Consumer<ClassSession> onClick) {
        this.session = session;
        this.state = state;
        this.countdown = countdown;
        this.isLast = isLast;
        this.isSelected = isSelected;

        setLayout(new BorderLayout(16, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        // 1. Timeline Column on Left
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
                    g2.setColor(state == SessionState.ACTIVE ? UITheme.ACCENT : UITheme.TIMELINE_LINE);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawLine(lineX, 28, lineX, height + 10);
                }

                // Timeline Node
                int nodeY = 16;
                int nodeRadius = (state == SessionState.ACTIVE) ? 8 : 6;
                if (state == SessionState.ACTIVE) {
                    g2.setColor(UITheme.ACCENT_RING);
                    g2.fillOval(lineX - nodeRadius - 4, nodeY - 4, (nodeRadius + 4) * 2, (nodeRadius + 4) * 2);
                    g2.setColor(UITheme.ACCENT);
                    g2.fillOval(lineX - nodeRadius, nodeY, nodeRadius * 2, nodeRadius * 2);
                } else if (state == SessionState.COMPLETED) {
                    g2.setColor(UITheme.SUCCESS_GREEN);
                    g2.fillOval(lineX - nodeRadius, nodeY, nodeRadius * 2, nodeRadius * 2);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawLine(lineX - 3, nodeY + 6, lineX - 1, nodeY + 9);
                    g2.drawLine(lineX - 1, nodeY + 9, lineX + 4, nodeY + 3);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(lineX - nodeRadius, nodeY, nodeRadius * 2, nodeRadius * 2);
                    g2.setColor(UITheme.TIMELINE_LINE);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(lineX - nodeRadius, nodeY, nodeRadius * 2, nodeRadius * 2);
                }

                g2.dispose();
            }
        };
        timelineColumn.setPreferredSize(new Dimension(84, 80));
        timelineColumn.setLayout(new GridLayout(2, 1, 0, 2));
        timelineColumn.setOpaque(false);

        JLabel startLabel = new JLabel(session.getStartTime(), JLabel.RIGHT);
        startLabel.setFont(state == SessionState.ACTIVE ? UITheme.FONT_SMALL_BOLD : UITheme.FONT_SMALL);
        startLabel.setForeground(state == SessionState.ACTIVE ? UITheme.ACCENT : UITheme.TEXT_PRIMARY);

        JLabel endLabel = new JLabel(session.getEndTime(), JLabel.RIGHT);
        endLabel.setFont(UITheme.FONT_SMALL);
        endLabel.setForeground(UITheme.TEXT_SECONDARY);

        timelineColumn.add(startLabel);
        timelineColumn.add(endLabel);

        // 2. Card Body
        JPanel cardBody = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);

                int w = getWidth();
                int h = getHeight();

                // Fill background
                if (state == SessionState.ACTIVE) {
                    g2.setColor(UITheme.ACCENT_LIGHT);
                } else {
                    g2.setColor(UITheme.CARD_BG);
                }
                g2.fillRoundRect(0, 0, w, h, 16, 16);

                // Border
                if (state == SessionState.ACTIVE || isSelected) {
                    g2.setColor(UITheme.ACCENT);
                    g2.setStroke(new BasicStroke(2f));
                } else if (isHovered) {
                    g2.setColor(UITheme.TIMELINE_LINE);
                    g2.setStroke(new BasicStroke(1.5f));
                } else {
                    g2.setColor(UITheme.CARD_BORDER);
                    g2.setStroke(new BasicStroke(1f));
                }
                g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

                // Left active accent indicator stripe
                if (state == SessionState.ACTIVE) {
                    g2.setColor(UITheme.ACCENT);
                    g2.fillRoundRect(0, 0, 6, h, 6, 6);
                }

                g2.dispose();
            }
        };
        cardBody.setOpaque(false);
        cardBody.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        cardBody.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Icon container
        JPanel iconPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(state == SessionState.ACTIVE ? UITheme.CARD_BG : new Color(0xF3, 0xF4, 0xF6));
                g2.fillOval(0, 0, getWidth(), getHeight());
                if (state == SessionState.ACTIVE) {
                    g2.setColor(UITheme.ACCENT_RING);
                    g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                }
                g2.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(42, 42));
        iconPanel.setOpaque(false);
        JLabel iconLbl = new JLabel(session.isBreak() ? "☕" : "📖", JLabel.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconPanel.add(iconLbl, BorderLayout.CENTER);

        // Center Details
        JPanel centerDetails = new JPanel(new GridLayout(3, 1, 0, 2));
        centerDetails.setOpaque(false);

        String titleText = session.isBreak() ? "Break" : SubjectRegistry.getSubjectName(session.getSubject());
        JLabel subjectLabel = new JLabel(titleText);
        subjectLabel.setFont(UITheme.FONT_BODY_BOLD);
        subjectLabel.setForeground(UITheme.TEXT_PRIMARY);

        JLabel facultyLabel = new JLabel("👤 " + (session.getFaculty() != null ? session.getFaculty() : "Faculty"));
        facultyLabel.setFont(UITheme.FONT_SMALL);
        facultyLabel.setForeground(UITheme.TEXT_SECONDARY);

        JLabel roomLabel = new JLabel("📍 " + (session.getRoom() != null ? session.getRoom() : "Classroom"));
        roomLabel.setFont(UITheme.FONT_SMALL);
        roomLabel.setForeground(UITheme.TEXT_SECONDARY);

        centerDetails.add(subjectLabel);
        if (!session.isBreak()) {
            centerDetails.add(facultyLabel);
            centerDetails.add(roomLabel);
        }

        // Right Badges (NOW / Ends In)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        rightPanel.setOpaque(false);

        if (state == SessionState.ACTIVE) {
            JPanel activeBadge = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    UITheme.enableAntiAliasing(g2);
                    g2.setColor(UITheme.ACCENT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.dispose();
                }
            };
            activeBadge.setOpaque(false);
            activeBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            JLabel activeLbl = new JLabel("NOW");
            activeLbl.setFont(UITheme.FONT_TINY_BOLD);
            activeLbl.setForeground(Color.WHITE);
            activeBadge.add(activeLbl, BorderLayout.CENTER);
            rightPanel.add(activeBadge);

            if (countdown != null && !countdown.isEmpty()) {
                JLabel countdownLbl = new JLabel(countdown);
                countdownLbl.setFont(UITheme.FONT_SMALL_BOLD);
                countdownLbl.setForeground(UITheme.ACCENT);
                rightPanel.add(countdownLbl);
            }
        } else if (state == SessionState.COMPLETED) {
            JLabel compLbl = new JLabel("✓");
            compLbl.setFont(UITheme.FONT_HEADER);
            compLbl.setForeground(UITheme.SUCCESS_GREEN);
            rightPanel.add(compLbl);
        }

        cardBody.add(iconPanel, BorderLayout.WEST);
        cardBody.add(centerDetails, BorderLayout.CENTER);
        cardBody.add(rightPanel, BorderLayout.EAST);

        // Interaction
        cardBody.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.accept(session);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                cardBody.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                cardBody.repaint();
            }
        });

        add(timelineColumn, BorderLayout.WEST);
        add(cardBody, BorderLayout.CENTER);
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        repaint();
    }
}
