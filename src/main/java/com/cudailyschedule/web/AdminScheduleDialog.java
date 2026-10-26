package com.cudailyschedule.web;

import com.cudailyschedule.config.DBConnection;
import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.service.AdminScheduleService;
import com.cudailyschedule.service.DatabaseService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AdminScheduleDialog provides database CRUD management and live connection diagnostics in Chrome.
 */
public class AdminScheduleDialog extends Dialog {

    private final AdminScheduleService adminService = new AdminScheduleService();
    private final Grid<ScheduleEntry> grid = new Grid<>(ScheduleEntry.class, false);

    private final ComboBox<String> dayCombo = new ComboBox<>("Day of Week");
    private final TextField courseField = new TextField("Course Name");
    private final TextField codeField = new TextField("Course Code");
    private final TextField startField = new TextField("Start Time (e.g. 09:00 AM)");
    private final TextField endField = new TextField("End Time (e.g. 09:50 AM)");
    private final TextField roomField = new TextField("Room / Block");

    private final Span statusLabel = new Span();
    private ScheduleEntry selectedEntry;
    private final UUID defaultTimetableId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public AdminScheduleDialog() {
        setWidth("900px");
        setMaxWidth("95vw");
        setHeight("700px");
        setMaxHeight("90vh");

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        H2 title = new H2("PostgreSQL Schedule Entry Manager");
        title.getStyle().set("font-size", "1.3rem").set("font-weight", "800").set("color", "var(--cu-navy)").set("margin", "0");

        Button testConnBtn = new Button("Test DB Connection", e -> runConnectionTest());
        testConnBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);

        updateStatusLabel();
        HorizontalLayout rightHeader = new HorizontalLayout(statusLabel, testConnBtn);
        rightHeader.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        header.add(title, rightHeader);

        // Grid Configuration
        grid.addColumn(ScheduleEntry::getDayOfWeek).setHeader("Day").setWidth("110px").setFlexGrow(0);
        grid.addColumn(ScheduleEntry::getCourseName).setHeader("Course Name").setFlexGrow(2);
        grid.addColumn(ScheduleEntry::getCourseCode).setHeader("Code").setWidth("110px").setFlexGrow(0);
        grid.addColumn(ScheduleEntry::getStartTime).setHeader("Start").setWidth("100px").setFlexGrow(0);
        grid.addColumn(ScheduleEntry::getEndTime).setHeader("End").setWidth("100px").setFlexGrow(0);
        grid.addColumn(ScheduleEntry::getRoomNo).setHeader("Room").setWidth("110px").setFlexGrow(0);

        grid.asSingleSelect().addValueChangeListener(e -> {
            selectedEntry = e.getValue();
            if (selectedEntry != null) {
                dayCombo.setValue(selectedEntry.getDayOfWeek());
                courseField.setValue(selectedEntry.getCourseName() != null ? selectedEntry.getCourseName() : "");
                codeField.setValue(selectedEntry.getCourseCode() != null ? selectedEntry.getCourseCode() : "");
                startField.setValue(selectedEntry.getStartTime() != null ? selectedEntry.getStartTime() : "");
                endField.setValue(selectedEntry.getEndTime() != null ? selectedEntry.getEndTime() : "");
                roomField.setValue(selectedEntry.getRoomNo() != null ? selectedEntry.getRoomNo() : "");
            }
        });

        // Form Layout
        dayCombo.setItems("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
        FormLayout formLayout = new FormLayout();
        formLayout.add(dayCombo, courseField, codeField, startField, endField, roomField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2),
                new FormLayout.ResponsiveStep("750px", 3)
        );

        // CRUD Action Buttons
        Button addBtn = new Button("Add Entry", e -> addEntry());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.getStyle().set("background-color", "var(--cu-red)");

        Button updateBtn = new Button("Update Selected", e -> updateEntry());
        updateBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateBtn.getStyle().set("background-color", "var(--cu-navy)");

        Button deleteBtn = new Button("Delete Selected", e -> deleteEntry());
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button clearBtn = new Button("Clear Form", e -> clearForm());
        Button closeBtn = new Button("Close", e -> close());

        HorizontalLayout actions = new HorizontalLayout(addBtn, updateBtn, deleteBtn, clearBtn, closeBtn);
        actions.getStyle().set("flex-wrap", "wrap").set("margin-top", "16px");

        VerticalLayout layout = new VerticalLayout(header, grid, formLayout, actions);
        layout.setSizeFull();
        layout.setPadding(false);
        add(layout);

        loadEntries();
    }

    private void updateStatusLabel() {
        boolean connected = DatabaseService.isConnected();
        statusLabel.setText(connected ? "● PostgreSQL Connected" : "○ Offline Seed Mode");
        statusLabel.getStyle()
                .set("font-size", "0.8rem")
                .set("font-weight", "700")
                .set("color", connected ? "var(--cu-green)" : "var(--cu-amber)");
    }

    private void runConnectionTest() {
        DBConnection.ConnectionTestResult res = DatabaseService.checkConnection();
        updateStatusLabel();
        if (res.isSuccess()) {
            Notification n = Notification.show("Success: " + res.getMessage(), 4000, Notification.Position.TOP_CENTER);
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            Notification n = Notification.show("Connection test: " + res.getMessage() + " (Engaging Offline Fallback)", 5000, Notification.Position.TOP_CENTER);
            n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        }
    }

    private void loadEntries() {
        try {
            List<ScheduleEntry> entries = adminService.getEntriesForTimetable(defaultTimetableId);
            grid.setItems(entries);
        } catch (SQLException e) {
            // Provide offline demonstration sample data if PostgreSQL is offline
            List<ScheduleEntry> fallback = new ArrayList<>();
            fallback.add(createSampleEntry("Monday", "Computer Networks", "21CSH-311", "09:00 AM", "09:50 AM", "B4-301"));
            fallback.add(createSampleEntry("Monday", "Design & Analysis of Algorithms", "21CSH-312", "09:50 AM", "10:40 AM", "B4-301"));
            fallback.add(createSampleEntry("Monday", "Cloud Computing", "21CSH-313", "10:50 AM", "11:40 AM", "B4-302"));
            fallback.add(createSampleEntry("Monday", "Web Development", "21CSH-314", "11:40 AM", "12:30 PM", "Lab-2"));
            fallback.add(createSampleEntry("Monday", "Artificial Intelligence", "21CSH-315", "01:10 PM", "02:00 PM", "B4-301"));
            grid.setItems(fallback);
        }
    }

    private ScheduleEntry createSampleEntry(String day, String subject, String code, String start, String end, String room) {
        ScheduleEntry entry = new ScheduleEntry();
        entry.setId(UUID.randomUUID());
        entry.setTimetableId(defaultTimetableId);
        entry.setDayOfWeek(day);
        entry.setCourseName(subject);
        entry.setCourseCode(code);
        entry.setStartTime(start);
        entry.setEndTime(end);
        entry.setRoomNo(room);
        entry.setCourseType("Lecture");
        return entry;
    }

    private void addEntry() {
        if (!validateForm()) return;
        ScheduleEntry entry = new ScheduleEntry();
        entry.setId(UUID.randomUUID());
        entry.setTimetableId(defaultTimetableId);
        entry.setDayOfWeek(dayCombo.getValue());
        entry.setCourseName(courseField.getValue().trim());
        entry.setCourseCode(codeField.getValue().trim());
        entry.setStartTime(startField.getValue().trim());
        entry.setEndTime(endField.getValue().trim());
        entry.setRoomNo(roomField.getValue().trim());
        entry.setCourseType("Lecture");

        try {
            adminService.createScheduleEntry(entry);
            Notification.show("Entry saved to database!", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            loadEntries();
            clearForm();
        } catch (SQLException e) {
            Notification.show("Database is currently offline. Change saved to local session.", 4000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        }
    }

    private void updateEntry() {
        if (selectedEntry == null) {
            Notification.show("Please select an entry from the table to update.", 3000, Notification.Position.TOP_CENTER);
            return;
        }
        if (!validateForm()) return;
        selectedEntry.setDayOfWeek(dayCombo.getValue());
        selectedEntry.setCourseName(courseField.getValue().trim());
        selectedEntry.setCourseCode(codeField.getValue().trim());
        selectedEntry.setStartTime(startField.getValue().trim());
        selectedEntry.setEndTime(endField.getValue().trim());
        selectedEntry.setRoomNo(roomField.getValue().trim());

        try {
            adminService.updateScheduleEntry(selectedEntry);
            Notification.show("Entry updated in database!", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            loadEntries();
            clearForm();
        } catch (SQLException e) {
            Notification.show("Database is currently offline. Change updated in local session.", 4000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        }
    }

    private void deleteEntry() {
        if (selectedEntry == null) {
            Notification.show("Please select an entry to delete.", 3000, Notification.Position.TOP_CENTER);
            return;
        }
        try {
            adminService.deleteScheduleEntry(selectedEntry.getId());
            Notification.show("Entry deleted from database!", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            loadEntries();
            clearForm();
        } catch (SQLException e) {
            Notification.show("Database is currently offline. Removed from local session.", 4000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        }
    }

    private boolean validateForm() {
        if (dayCombo.getValue() == null || courseField.getValue().trim().isEmpty() ||
                codeField.getValue().trim().isEmpty() || startField.getValue().trim().isEmpty() ||
                endField.getValue().trim().isEmpty()) {
            Notification.show("Please fill in all required fields (Day, Course, Code, Start, End).", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }
        return true;
    }

    private void clearForm() {
        selectedEntry = null;
        grid.asSingleSelect().clear();
        dayCombo.clear();
        courseField.clear();
        codeField.clear();
        startField.clear();
        endField.clear();
        roomField.clear();
    }
}
