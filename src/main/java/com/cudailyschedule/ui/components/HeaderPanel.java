package com.cudailyschedule.ui.components;

import com.cudailyschedule.ui.theme.UITheme;
import com.cudailyschedule.util.TimeUtils;

import javax.swing.BorderFactory;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Top Header component matching cu-daily-schedule/src/components/Header.tsx.
 */
public class HeaderPanel extends JPanel {
    private final JLabel dayLabel;
    private final JLabel dateLabel;

    public HeaderPanel() {
        setLayout(new BorderLayout(20, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // Background container panel
        JPanel card = new JPanel(new BorderLayout(20, 0)) {
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
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Left: Icon + Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        leftPanel.setOpaque(false);

        // Red badge
        JPanel iconBadge = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        iconBadge.setPreferredSize(new Dimension(48, 48));
        iconBadge.setOpaque(false);
        JLabel calIcon = new JLabel("📅", JLabel.CENTER);
        calIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        calIcon.setForeground(Color.WHITE);
        iconBadge.add(calIcon, BorderLayout.CENTER);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel title = new JLabel("CU Daily Schedule");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.ACCENT);

        JLabel subtitle = new JLabel("Manage your daily classes effortlessly");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(title);
        titleBlock.add(subtitle);

        leftPanel.add(iconBadge);
        leftPanel.add(titleBlock);

        // Right: Day + Date
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        rightPanel.setOpaque(false);

        dayLabel = new JLabel("", JLabel.RIGHT);
        dayLabel.setFont(UITheme.FONT_SUBTITLE);
        dayLabel.setForeground(UITheme.TEXT_PRIMARY);

        dateLabel = new JLabel("", JLabel.RIGHT);
        dateLabel.setFont(UITheme.FONT_BODY_BOLD);
        dateLabel.setForeground(UITheme.ACCENT);

        rightPanel.add(dayLabel);
        rightPanel.add(dateLabel);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.EAST);
        add(card, BorderLayout.CENTER);

        refreshClock();
    }

    public void refreshClock() {
        ZonedDateTime now = TimeUtils.getNowInKolkata();
        String day = now.format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH));
        String date = now.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH));
        dayLabel.setText(day);
        dateLabel.setText(date);
    }
}
