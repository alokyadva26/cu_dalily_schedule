package com.cudailyschedule.ui.theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * Design system tokens, color constants, typography, and border styles.
 * Matches Tailwind CSS tokens from cu-daily-schedule/src/app/globals.css.
 */
public class UITheme {
    // Primary Color Palette
    public static final Color ACCENT = new Color(0xE3, 0x06, 0x13);            // #E30613 CU Red
    public static final Color ACCENT_HOVER = new Color(0xC0, 0x05, 0x10);
    public static final Color ACCENT_LIGHT = new Color(0xFF, 0xF5, 0xF5);      // #FFF5F5 Soft active tint
    public static final Color ACCENT_RING = new Color(0xE3, 0x06, 0x13, 50);

    public static final Color NAVY = new Color(0x1A, 0x2B, 0x4C);              // #1a2b4c CU Navy / Headings
    public static final Color BACKGROUND = new Color(0xF8, 0xF9, 0xFA);        // #f8f9fa Authentic page BG
    public static final Color CARD_BG = Color.WHITE;
    public static final Color CARD_BORDER = new Color(0xE5, 0xE7, 0xEB);       // #E5E7EB
    public static final Color TIMELINE_LINE = new Color(0xD1, 0xD5, 0xDB);     // #D1D5DB
    public static final Color TEXT_PRIMARY = new Color(0x11, 0x18, 0x27);      // #111827 Dark text
    public static final Color TEXT_SECONDARY = new Color(0x6B, 0x72, 0x80);    // #6B7280 Muted gray
    public static final Color TEXT_MUTED = new Color(0x86, 0x8E, 0x96);        // #868E96
    public static final Color SUCCESS_GREEN = new Color(0x28, 0xA7, 0x45);     // #28a745

    // Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TINY_BOLD = new Font("Segoe UI", Font.BOLD, 10);

    /**
     * Creates a rounded card border with optional subtle shadow effect.
     */
    public static Border createRoundedCardBorder(int radius, Color borderColor) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        );
    }

    /**
     * Enables high-quality anti-aliasing on Graphics2D.
     */
    public static void enableAntiAliasing(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }
    }
}
