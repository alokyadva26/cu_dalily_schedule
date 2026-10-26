package com.cudailyschedule.ui.components;

import com.cudailyschedule.ui.theme.UITheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Recreates cu-daily-schedule/src/components/EndOfDayState.tsx.
 */
public class EndOfDayPanel extends JPanel {

    public EndOfDayPanel(Runnable onViewTomorrow) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = new JPanel() {
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
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(36, 24, 36, 24));

        JPanel iconCircle = new JPanel(new BorderLayout()) {
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
        iconCircle.setPreferredSize(new Dimension(64, 64));
        iconCircle.setMaximumSize(new Dimension(64, 64));
        iconCircle.setOpaque(false);
        JLabel icon = new JLabel("🎉", JLabel.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        iconCircle.add(icon, BorderLayout.CENTER);

        JLabel title = new JLabel("Classes are Over!", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel desc = new JLabel("<html><center>Have a great evening.<br>See you tomorrow.</center></html>", JLabel.CENTER);
        desc.setFont(UITheme.FONT_BODY);
        desc.setForeground(UITheme.TEXT_SECONDARY);

        JButton tomorrowBtn = new JButton("View Tomorrow's Schedule →") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(getModel().isRollover() ? UITheme.ACCENT_HOVER : UITheme.ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tomorrowBtn.setFont(UITheme.FONT_BODY_BOLD);
        tomorrowBtn.setForeground(Color.WHITE);
        tomorrowBtn.setContentAreaFilled(false);
        tomorrowBtn.setBorderPainted(false);
        tomorrowBtn.setFocusPainted(false);
        tomorrowBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tomorrowBtn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        tomorrowBtn.addActionListener(e -> {
            if (onViewTomorrow != null) onViewTomorrow.run();
        });

        iconCircle.setAlignmentX(CENTER_ALIGNMENT);
        title.setAlignmentX(CENTER_ALIGNMENT);
        desc.setAlignmentX(CENTER_ALIGNMENT);
        tomorrowBtn.setAlignmentX(CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(iconCircle);
        card.add(Box.createVerticalStrut(16));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(desc);
        card.add(Box.createVerticalStrut(20));
        card.add(tomorrowBtn);
        card.add(Box.createVerticalGlue());

        add(card, BorderLayout.CENTER);
    }
}
