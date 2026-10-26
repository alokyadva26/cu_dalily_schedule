package com.cudailyschedule.ui.components;

import com.cudailyschedule.ui.theme.UITheme;
import com.cudailyschedule.util.TimeUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Recreates cu-daily-schedule/src/components/BottomCard.tsx.
 */
public class FooterPanel extends JPanel {
    private final JLabel timeLabel;

    public FooterPanel(Runnable onRefresh) {
        setLayout(new BorderLayout(16, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 24, 16, 24));

        JPanel card = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(UITheme.CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        // Left: Clock + Auto-update status
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JPanel iconBadge = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.ACCENT_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        iconBadge.setPreferredSize(new Dimension(36, 36));
        iconBadge.setOpaque(false);
        JLabel icon = new JLabel("⏰", JLabel.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconBadge.add(icon, BorderLayout.CENTER);

        JPanel textBlock = new JPanel(new GridLayout(2, 1, 0, 1));
        textBlock.setOpaque(false);
        JLabel title = new JLabel("Schedule updates automatically");
        title.setFont(UITheme.FONT_BODY_BOLD);
        title.setForeground(UITheme.TEXT_PRIMARY);

        timeLabel = new JLabel("Last updated: -");
        timeLabel.setFont(UITheme.FONT_SMALL);
        timeLabel.setForeground(UITheme.TEXT_SECONDARY);

        textBlock.add(title);
        textBlock.add(timeLabel);

        left.add(iconBadge);
        left.add(textBlock);

        // Right: Refresh button
        JButton refreshBtn = new JButton("↻ Refresh") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(getModel().isRollover() ? UITheme.ACCENT_LIGHT : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(UITheme.ACCENT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        refreshBtn.setFont(UITheme.FONT_BODY_BOLD);
        refreshBtn.setForeground(UITheme.ACCENT);
        refreshBtn.setContentAreaFilled(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        refreshBtn.addActionListener(e -> {
            updateTimestamp();
            if (onRefresh != null) onRefresh.run();
        });

        card.add(left, BorderLayout.WEST);
        card.add(refreshBtn, BorderLayout.EAST);
        add(card, BorderLayout.CENTER);

        updateTimestamp();
    }

    public void updateTimestamp() {
        String t = TimeUtils.getNowInKolkata().format(DateTimeFormatter.ofPattern("h:mm:ss a", Locale.ENGLISH));
        timeLabel.setText("Last updated: " + t);
    }
}
