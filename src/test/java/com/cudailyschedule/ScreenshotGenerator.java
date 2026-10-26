package com.cudailyschedule;

import com.cudailyschedule.model.Teacher;
import com.cudailyschedule.service.TeacherService;
import com.cudailyschedule.ui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;

/**
 * Utility to generate screenshots of all working screens for college submission report.
 */
public class ScreenshotGenerator {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "false");
        FlatLightLaf.setup();

        File dir = new File("screenshots");
        if (!dir.exists()) dir.mkdirs();

        SwingUtilities.invokeAndWait(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setSize(1240, 840);
                frame.setVisible(true);
                Thread.sleep(800);

                // 1. Capture Landing Screen
                frame.navigateTo("LANDING");
                Thread.sleep(400);
                captureScreen(frame, new File(dir, "01_landing_screen.png"));

                // 2. Capture Student Schedule Screen
                frame.navigateTo("STUDENT");
                Thread.sleep(400);
                captureScreen(frame, new File(dir, "02_student_schedule_screen.png"));

                // 3. Capture Teacher Search Screen
                frame.navigateTo("TEACHER_SEARCH");
                Thread.sleep(400);
                captureScreen(frame, new File(dir, "03_teacher_search_screen.png"));

                // 4. Capture Teacher Schedule Screen (Neeraj)
                TeacherService teacherService = new TeacherService();
                Optional<Teacher> neeraj = teacherService.findTeacher("E19761");
                neeraj.ifPresent(teacher -> frame.showTeacherSchedule("E19761", teacher));
                Thread.sleep(400);
                captureScreen(frame, new File(dir, "04_teacher_schedule_screen.png"));

                System.out.println("Screenshots generated successfully in " + dir.getAbsolutePath());
                frame.dispose();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static void captureScreen(MainFrame frame, File targetFile) {
        try {
            int w = frame.getWidth();
            int h = frame.getHeight();
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            frame.printAll(g2);
            g2.dispose();
            ImageIO.write(image, "PNG", targetFile);
            System.out.println("Saved: " + targetFile.getName() + " (" + targetFile.length() + " bytes)");
        } catch (Exception e) {
            System.err.println("Failed to capture: " + targetFile.getName() + " - " + e.getMessage());
        }
    }
}
