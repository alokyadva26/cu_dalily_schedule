package com.cudailyschedule.ui.components;

import com.cudailyschedule.ui.theme.UITheme;
import com.cudailyschedule.util.TimeUtils;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Custom Swing component rendering the circular countdown progress ring.
 * Recreates the SVG progress indicator from cu-daily-schedule/src/components/ClassDetails.tsx.
 */
public class CircularProgressIndicator extends JComponent {
    private long totalSeconds = 3000;
    private long remainingSeconds = 1500;
    private int strokeWidth = 8;

    public CircularProgressIndicator() {
        setPreferredSize(new Dimension(130, 130));
        setMinimumSize(new Dimension(100, 100));
        setOpaque(false);
    }

    public void setProgress(long remainingSeconds, long totalSeconds) {
        this.remainingSeconds = Math.max(0, remainingSeconds);
        this.totalSeconds = Math.max(1, totalSeconds);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.enableAntiAliasing(g2);

        int size = Math.min(getWidth(), getHeight()) - strokeWidth * 2;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        // Background track circle
        g2.setColor(new Color(0xF3, 0xF4, 0xF6));
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawOval(x, y, size, size);

        // Progress arc in Red
        double progress = Math.min(1.0, Math.max(0.0, (double) remainingSeconds / totalSeconds));
        int angle = (int) Math.round(progress * 360.0);

        if (angle > 0) {
            g2.setColor(UITheme.ACCENT);
            // Swing arc angles: 0 is 3 o'clock, 90 is 12 o'clock. We start at top (90) and go clockwise (-angle)
            g2.drawArc(x, y, size, size, 90, -angle);
        }

        // Center Time Text (e.g. "25:40")
        String timeText = TimeUtils.formatRemainingSeconds(remainingSeconds);
        g2.setColor(UITheme.TEXT_PRIMARY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        FontMetrics fmTime = g2.getFontMetrics();
        int timeX = (getWidth() - fmTime.stringWidth(timeText)) / 2;
        int timeY = (getHeight() / 2) + 2;
        g2.drawString(timeText, timeX, timeY);

        // Subtitle "REMAINING"
        String label = "REMAINING";
        g2.setColor(UITheme.ACCENT);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        FontMetrics fmLabel = g2.getFontMetrics();
        int labelX = (getWidth() - fmLabel.stringWidth(label)) / 2;
        int labelY = timeY + 16;
        g2.drawString(label, labelX, labelY);

        g2.dispose();
    }
}
