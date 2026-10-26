package com.cudailyschedule.ui.components;

import com.cudailyschedule.ui.theme.UITheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Recreates cu-daily-schedule/src/components/EmptyState.tsx.
 */
public class EmptyStatePanel extends JPanel {

    public EmptyStatePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(48, 24, 48, 24));

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
        iconCircle.setPreferredSize(new Dimension(80, 80));
        iconCircle.setMaximumSize(new Dimension(80, 80));
        iconCircle.setOpaque(false);
        JLabel icon = new JLabel("🎉", JLabel.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconCircle.add(icon, BorderLayout.CENTER);

        JLabel title = new JLabel("No Classes Today!", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel desc = new JLabel("Enjoy your day off! Relax, recharge, or catch up on some personal projects.", JLabel.CENTER);
        desc.setFont(UITheme.FONT_BODY);
        desc.setForeground(UITheme.TEXT_SECONDARY);

        iconCircle.setAlignmentX(CENTER_ALIGNMENT);
        title.setAlignmentX(CENTER_ALIGNMENT);
        desc.setAlignmentX(CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(iconCircle);
        card.add(Box.createVerticalStrut(20));
        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(desc);
        card.add(Box.createVerticalGlue());

        add(card, BorderLayout.CENTER);
    }
}
