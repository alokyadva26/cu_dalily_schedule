package com.cudailyschedule.ui;

import com.cudailyschedule.ui.theme.UITheme;
import com.cudailyschedule.util.ImageUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * Recreates the landing screen from cu-daily-schedule/src/app/page.tsx.
 * Features CU logo, background watermark, Student NFC & Teacher QR action cards.
 */
public class LandingScreen extends JPanel {
    private final Consumer<String> navigationHandler;

    public LandingScreen(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel mainContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.BACKGROUND);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle watermark background if cu-bg is present
                BufferedImage bg = ImageUtils.loadImage("/images/cu-bg.png");
                if (bg != null) {
                    g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.08f));
                    g2.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
                }
                g2.dispose();
            }
        };
        mainContainer.setLayout(new BorderLayout());

        // TOP HEADER
        JPanel topHeader = new JPanel();
        topHeader.setLayout(new BoxLayout(topHeader, BoxLayout.Y_AXIS));
        topHeader.setOpaque(false);
        topHeader.setBorder(BorderFactory.createEmptyBorder(28, 20, 10, 20));

        // CU Logo
        ImageIcon logoIcon = ImageUtils.loadScaledIcon("/images/cu-logo.png", 140, 56);
        JLabel logoLabel = (logoIcon != null) ? new JLabel(logoIcon, JLabel.CENTER) : new JLabel("CHANDIGARH UNIVERSITY", JLabel.CENTER);
        if (logoIcon == null) {
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
            logoLabel.setForeground(UITheme.ACCENT);
        }
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);

        // DAILY SCHEDULE Title
        JLabel mainTitle = new JLabel("DAILY SCHEDULE", JLabel.CENTER);
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 44));
        mainTitle.setForeground(UITheme.NAVY);
        mainTitle.setAlignmentX(CENTER_ALIGNMENT);

        // Subtitle row with red rule lines
        JPanel subRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        subRow.setOpaque(false);

        JPanel line1 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0xE3, 0x06, 0x13, 150));
                g.fillRect(0, 7, 48, 2);
            }
        };
        line1.setPreferredSize(new Dimension(48, 16));
        line1.setOpaque(false);

        JLabel subTitle = new JLabel("SMART TIMETABLE DEVICE");
        subTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subTitle.setForeground(new Color(0x4A, 0x55, 0x68));

        JPanel line2 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0xE3, 0x06, 0x13, 150));
                g.fillRect(0, 7, 48, 2);
            }
        };
        line2.setPreferredSize(new Dimension(48, 16));
        line2.setOpaque(false);

        subRow.add(line1);
        subRow.add(subTitle);
        subRow.add(line2);
        subRow.setAlignmentX(CENTER_ALIGNMENT);

        topHeader.add(logoLabel);
        topHeader.add(Box.createVerticalStrut(12));
        topHeader.add(mainTitle);
        topHeader.add(Box.createVerticalStrut(6));
        topHeader.add(subRow);

        // CENTER ACTION CARDS
        JPanel cardsWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 48, 40));
        cardsWrapper.setOpaque(false);

        // Card 1: Student NFC Card
        JPanel nfcCard = createInteractiveCard(
            "💳",
            "TAP YOUR NFC CARD",
            "to view your timetable",
            () -> navigationHandler.accept("STUDENT")
        );

        // Card 2: Teacher QR Card
        JPanel qrCard = createInteractiveCard(
            "📷",
            "SCAN TEACHER QR CODE",
            "to view teacher timetable",
            () -> navigationHandler.accept("TEACHER_SEARCH")
        );

        cardsWrapper.add(nfcCard);
        cardsWrapper.add(qrCard);

        // BOTTOM STATUS
        JPanel bottomStatus = new JPanel();
        bottomStatus.setLayout(new BoxLayout(bottomStatus, BoxLayout.Y_AXIS));
        bottomStatus.setOpaque(false);
        bottomStatus.setBorder(BorderFactory.createEmptyBorder(10, 20, 28, 20));

        JLabel readyTitle = new JLabel("DEVICE READY", JLabel.CENTER);
        readyTitle.setFont(UITheme.FONT_BODY_BOLD);
        readyTitle.setForeground(UITheme.NAVY);
        readyTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel readyDesc = new JLabel("Please tap your NFC card or scan QR code", JLabel.CENTER);
        readyDesc.setFont(UITheme.FONT_SMALL);
        readyDesc.setForeground(UITheme.TEXT_SECONDARY);
        readyDesc.setAlignmentX(CENTER_ALIGNMENT);

        JPanel redPill = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(UITheme.ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }
        };
        redPill.setPreferredSize(new Dimension(48, 4));
        redPill.setMaximumSize(new Dimension(48, 4));
        redPill.setOpaque(false);
        redPill.setAlignmentX(CENTER_ALIGNMENT);

        bottomStatus.add(readyTitle);
        bottomStatus.add(Box.createVerticalStrut(4));
        bottomStatus.add(readyDesc);
        bottomStatus.add(Box.createVerticalStrut(10));
        bottomStatus.add(redPill);

        mainContainer.add(topHeader, BorderLayout.NORTH);
        mainContainer.add(cardsWrapper, BorderLayout.CENTER);
        mainContainer.add(bottomStatus, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createInteractiveCard(String iconStr, String title, String subtitle, Runnable action) {
        final boolean[] hovered = {false};

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);

                int w = getWidth();
                int h = getHeight();

                // Drop shadow & background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w, h, 32, 32);

                // Border
                g2.setColor(hovered[0] ? UITheme.ACCENT : UITheme.CARD_BORDER);
                g2.setStroke(new BasicStroke(hovered[0] ? 2f : 1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 32, 32);

                g2.dispose();
            }
        };
        card.setPreferredSize(new Dimension(320, 320));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createEmptyBorder(36, 24, 36, 24));

        // Circle icon wrapper
        JPanel iconCircle = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(new Color(0xF8, 0xF9, 0xFA));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.CARD_BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        iconCircle.setPreferredSize(new Dimension(88, 88));
        iconCircle.setMaximumSize(new Dimension(88, 88));
        iconCircle.setOpaque(false);
        JLabel iconLbl = new JLabel(iconStr, JLabel.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconCircle.add(iconLbl, BorderLayout.CENTER);

        // Red separator notch
        JPanel redNotch = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                g2.setColor(new Color(0xE3, 0x06, 0x13, 140));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 2, 2);
                g2.dispose();
            }
        };
        redNotch.setPreferredSize(new Dimension(48, 3));
        redNotch.setMaximumSize(new Dimension(48, 3));
        redNotch.setOpaque(false);

        JLabel titleLbl = new JLabel(title, JLabel.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLbl.setForeground(UITheme.NAVY);

        JLabel subLbl = new JLabel(subtitle, JLabel.CENTER);
        subLbl.setFont(UITheme.FONT_BODY);
        subLbl.setForeground(UITheme.TEXT_SECONDARY);

        iconCircle.setAlignmentX(CENTER_ALIGNMENT);
        redNotch.setAlignmentX(CENTER_ALIGNMENT);
        titleLbl.setAlignmentX(CENTER_ALIGNMENT);
        subLbl.setAlignmentX(CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(iconCircle);
        card.add(Box.createVerticalStrut(20));
        card.add(redNotch);
        card.add(Box.createVerticalStrut(18));
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(subLbl);
        card.add(Box.createVerticalGlue());

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) action.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hovered[0] = true;
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered[0] = false;
                card.repaint();
            }
        });

        return card;
    }
}
