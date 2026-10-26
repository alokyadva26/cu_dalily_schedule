package com.cudailyschedule.ui;

import com.cudailyschedule.config.DBConnection;
import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.model.Teacher;
import com.cudailyschedule.model.Timetable;
import com.cudailyschedule.service.AdminScheduleService;
import com.cudailyschedule.service.DatabaseService;
import com.cudailyschedule.ui.theme.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AdminScheduleDialog provides database CRUD management for college CSE evaluation.
 * Demonstrates JTable, TableModel, SwingWorker multithreading, and JDBC PreparedStatement operations.
 */
public class AdminScheduleDialog extends JDialog {
    private final AdminScheduleService adminService = new AdminScheduleService();
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;
    private final JComboBox<String> dayCombo;
    private final JTextField courseField;
    private final JTextField codeField;
    private final JTextField startField;
    private final JTextField endField;
    private final JTextField roomField;

    private List<ScheduleEntry> currentEntries = new ArrayList<>();
    private UUID defaultTimetableId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public AdminScheduleDialog(JFrame parent) {
        super(parent, "Database Management & Live PostgreSQL CRUD", true);
        setSize(960, 620);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(16, 16));
        getContentPane().setBackground(UITheme.BACKGROUND);

        // Header Panel
        JPanel topPanel = new JPanel(new BorderLayout(16, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.CARD_BORDER),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        JLabel title = new JLabel("PostgreSQL Schedule Entry Manager");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.NAVY);

        statusLabel = new JLabel(DatabaseService.getStatusMessage());
        statusLabel.setFont(UITheme.FONT_SMALL_BOLD);
        statusLabel.setForeground(DatabaseService.isConnected() ? UITheme.SUCCESS_GREEN : new Color(0xD9, 0x77, 0x06));

        JButton testConnBtn = new JButton("Test DB Connection");
        testConnBtn.setFont(UITheme.FONT_SMALL_BOLD);
        testConnBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        testConnBtn.addActionListener(e -> runConnectionTest());

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightTop.setOpaque(false);
        rightTop.add(statusLabel);
        rightTop.add(testConnBtn);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(rightTop, BorderLayout.EAST);

        // Center Table
        String[] columnNames = {"ID", "Day", "Course Name", "Course Code", "Start Time", "End Time", "Room / Block"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(UITheme.FONT_BODY);
        table.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(new Color(0xF1, 0xF5, 0xF9));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Bottom Form for Insertion
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.CARD_BORDER),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        dayCombo = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        courseField = new JTextField(14);
        codeField = new JTextField(8);
        startField = new JTextField(7);
        endField = new JTextField(7);
        roomField = new JTextField(8);

        // Prepopulate with sample
        courseField.setText("Database Systems Lab");
        codeField.setText("24CSP-304");
        startField.setText("09:30 AM");
        endField.setText("10:20 AM");
        roomField.setText("407");

        gbc.gridx = 0; gbc.gridy = 0; formGrid.add(new JLabel("Day:"), gbc);
        gbc.gridx = 1; formGrid.add(dayCombo, gbc);
        gbc.gridx = 2; formGrid.add(new JLabel("Course Name:"), gbc);
        gbc.gridx = 3; formGrid.add(courseField, gbc);
        gbc.gridx = 4; formGrid.add(new JLabel("Code:"), gbc);
        gbc.gridx = 5; formGrid.add(codeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formGrid.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1; formGrid.add(startField, gbc);
        gbc.gridx = 2; formGrid.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 3; formGrid.add(endField, gbc);
        gbc.gridx = 4; formGrid.add(new JLabel("Room:"), gbc);
        gbc.gridx = 5; formGrid.add(roomField, gbc);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton addBtn = new JButton("➕ Insert Entry");
        addBtn.setBackground(UITheme.ACCENT);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(UITheme.FONT_BODY_BOLD);
        addBtn.addActionListener(e -> executeInsert());

        JButton deleteBtn = new JButton("🗑️ Delete Selected");
        deleteBtn.setFont(UITheme.FONT_BODY_BOLD);
        deleteBtn.setForeground(new Color(0xDC, 0x26, 0x26));
        deleteBtn.addActionListener(e -> executeDelete());

        JButton refreshBtn = new JButton("🔄 Refresh Data");
        refreshBtn.setFont(UITheme.FONT_BODY);
        refreshBtn.addActionListener(e -> loadEntries());

        btnRow.add(deleteBtn);
        btnRow.add(refreshBtn);
        btnRow.add(addBtn);

        bottomPanel.add(formGrid, BorderLayout.CENTER);
        bottomPanel.add(btnRow, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadEntries();
    }

    private void runConnectionTest() {
        new SwingWorker<DBConnection.ConnectionTestResult, Void>() {
            @Override
            protected DBConnection.ConnectionTestResult doInBackground() {
                return DatabaseService.checkConnection();
            }

            @Override
            protected void done() {
                try {
                    DBConnection.ConnectionTestResult res = get();
                    statusLabel.setText(DatabaseService.getStatusMessage());
                    statusLabel.setForeground(res.isSuccess() ? UITheme.SUCCESS_GREEN : new Color(0xD9, 0x77, 0x06));
                    JOptionPane.showMessageDialog(AdminScheduleDialog.this, res.getMessage(),
                        res.isSuccess() ? "Connection Success" : "Connection Notice",
                        res.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private void loadEntries() {
        new SwingWorker<List<ScheduleEntry>, Void>() {
            @Override
            protected List<ScheduleEntry> doInBackground() {
                try {
                    return adminService.getEntriesForTimetable(defaultTimetableId);
                } catch (SQLException e) {
                    return new ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    currentEntries = get();
                    tableModel.setRowCount(0);
                    for (ScheduleEntry e : currentEntries) {
                        tableModel.addRow(new Object[]{
                            e.getId().toString().substring(0, 8) + "...",
                            e.getDayOfWeek(),
                            e.getCourseName(),
                            e.getCourseCode(),
                            e.getStartTime(),
                            e.getEndTime(),
                            e.getFormattedRoom()
                        });
                    }
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private void executeInsert() {
        ScheduleEntry entry = new ScheduleEntry(
            UUID.randomUUID(),
            defaultTimetableId,
            (String) dayCombo.getSelectedItem(),
            courseField.getText().trim(),
            codeField.getText().trim(),
            startField.getText().trim(),
            endField.getText().trim(),
            "24BCS-101",
            "Lecture",
            "A",
            "Block-B1",
            roomField.getText().trim(),
            "empty"
        );

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                adminService.createScheduleEntry(entry);
                return true;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(AdminScheduleDialog.this, "Entry saved successfully to PostgreSQL database!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadEntries();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminScheduleDialog.this, "Could not save to live database (offline mode):\n" + ex.getMessage(), "Database Notice", JOptionPane.WARNING_MESSAGE);
                }
            }
        }.execute();
    }

    private void executeDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= currentEntries.size()) {
            JOptionPane.showMessageDialog(this, "Please select an entry in the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ScheduleEntry target = currentEntries.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete entry: " + target.getCourseName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return adminService.deleteScheduleEntry(target.getId());
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(AdminScheduleDialog.this, "Entry deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadEntries();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminScheduleDialog.this, "Failed to delete entry:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
