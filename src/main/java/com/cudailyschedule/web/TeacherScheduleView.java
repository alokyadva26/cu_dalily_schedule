package com.cudailyschedule.web;

import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.model.ScheduleStatus;
import com.cudailyschedule.model.Teacher;
import com.cudailyschedule.service.ScheduleTimingService;
import com.cudailyschedule.service.TeacherService;
import com.cudailyschedule.util.TimeUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TeacherScheduleView displays teacher profile header, daily lecture timeline, and live countdowns in Chrome.
 */
@Route(value = "teacher-schedule", layout = MainLayout.class)
@PageTitle("Faculty Schedule — CU Daily Schedule")
public class TeacherScheduleView extends VerticalLayout implements HasUrlParameter<String> {

    private final TeacherService teacherService;
    private final ScheduleTimingService timingService = new ScheduleTimingService();

    private Teacher currentTeacher;
    private List<ScheduleEntry> todayEntries = new ArrayList<>();

    private final Div profileHeader = new Div();
    private final VerticalLayout timelineLayout = new VerticalLayout();
    private final Div dynamicCard = new Div();

    public TeacherScheduleView(TeacherService teacherService) {
        this.teacherService = teacherService;

        setSizeFull();
        setSpacing(false);
        setPadding(false);
        addClassName("cu-watermark-container");

        // Top Navigation Bar
        HorizontalLayout navBar = new HorizontalLayout();
        navBar.setWidthFull();
        navBar.setPadding(true);

        Button backBtn = new Button("← Back to Faculty Search", e -> UI.getCurrent().navigate("teacher"));
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backBtn.getStyle()
                .set("color", "var(--cu-navy)")
                .set("font-weight", "700")
                .set("background-color", "#FFFFFF")
                .set("border", "1px solid var(--cu-border)")
                .set("border-radius", "12px");
        navBar.add(backBtn);
        add(navBar);

        // Main content wrapper
        VerticalLayout content = new VerticalLayout();
        content.setMaxWidth("1200px");
        content.setWidthFull();
        content.getStyle().set("padding", "0 24px 32px 24px");
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        // Profile Header Card
        profileHeader.addClassName("cu-card");
        profileHeader.setWidthFull();
        profileHeader.getStyle()
                .set("padding", "24px 32px")
                .set("margin-bottom", "24px");

        // Split Layout (Timeline & Dynamic Status)
        HorizontalLayout splitLayout = new HorizontalLayout();
        splitLayout.setWidthFull();
        splitLayout.getStyle()
                .set("gap", "24px")
                .set("flex-wrap", "wrap")
                .set("align-items", "flex-start");

        timelineLayout.getStyle().set("flex", "1 1 540px").set("min-width", "320px");
        timelineLayout.setPadding(false);

        dynamicCard.addClassName("cu-card");
        dynamicCard.getStyle()
                .set("flex", "0 0 360px")
                .set("min-width", "320px")
                .set("padding", "24px")
                .set("position", "sticky")
                .set("top", "24px");

        splitLayout.add(timelineLayout, dynamicCard);
        content.add(profileHeader, splitLayout);
        add(content);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        if (parameter != null && !parameter.trim().isEmpty()) {
            Optional<Teacher> teacherOpt = teacherService.findTeacher(parameter.trim());
            if (teacherOpt.isPresent()) {
                this.currentTeacher = teacherOpt.get();
                loadTeacherSchedule();
                renderAll();
                return;
            }
        }
        UI.getCurrent().navigate("teacher");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        attachEvent.getUI().setPollInterval(1000);
        attachEvent.getUI().addPollListener(e -> renderDynamicCard());
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if (detachEvent.getUI() != null) {
            detachEvent.getUI().setPollInterval(-1);
        }
    }

    private void loadTeacherSchedule() {
        if (currentTeacher == null) return;
        List<ScheduleEntry> allEntries = teacherService.getTeacherSchedule(currentTeacher);
        ZonedDateTime now = TimeUtils.getNowInKolkata();
        String todayName = now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        this.todayEntries = allEntries.stream()
                .filter(e -> todayName.equalsIgnoreCase(e.getDayOfWeek()))
                .sorted(Comparator.comparing(e -> {
                    LocalTime t = TimeUtils.parseTime(e.getStartTime());
                    return t != null ? t : LocalTime.MIN;
                }))
                .collect(Collectors.toList());
    }

    private void renderAll() {
        renderProfileHeader();
        renderTimeline();
        renderDynamicCard();
    }

    private void renderProfileHeader() {
        profileHeader.removeAll();
        if (currentTeacher == null) return;

        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.getStyle().set("flex-wrap", "wrap").set("gap", "16px");

        // Avatar + info
        HorizontalLayout left = new HorizontalLayout();
        left.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        left.setSpacing(true);

        Div avatar = new Div();
        avatar.getStyle()
                .set("width", "64px")
                .set("height", "64px")
                .set("border-radius", "50%")
                .set("background-color", "var(--cu-red)")
                .set("color", "#FFFFFF")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("font-size", "1.6rem")
                .set("font-weight", "800");
        String initial = currentTeacher.getEmployeeName() != null && !currentTeacher.getEmployeeName().isEmpty()
                ? currentTeacher.getEmployeeName().substring(0, 1) : "T";
        avatar.setText(initial);

        VerticalLayout info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);

        H1 name = new H1(currentTeacher.getEmployeeName());
        name.getStyle().set("font-size", "1.5rem").set("font-weight", "800").set("color", "var(--cu-navy)").set("margin", "0 0 4px 0");

        Span dept = new Span("Department: " + currentTeacher.getDepartment());
        dept.getStyle().set("color", "var(--cu-text-muted)").set("font-size", "0.9rem");

        info.add(name, dept);
        left.add(avatar, info);

        // Employee ID Badge on right
        Span idBadge = new Span("ID: " + currentTeacher.getEmployeeId());
        idBadge.getStyle()
                .set("background-color", "var(--cu-navy)")
                .set("color", "#FFFFFF")
                .set("font-weight", "700")
                .set("padding", "6px 14px")
                .set("border-radius", "8px")
                .set("font-size", "0.9rem");

        row.add(left, idBadge);
        profileHeader.add(row);
    }

    private void renderTimeline() {
        timelineLayout.removeAll();

        H2 title = new H2("Today's Teaching Schedule");
        title.getStyle().set("font-size", "1.3rem").set("font-weight", "800").set("color", "var(--cu-navy)").set("margin", "0 0 16px 0");
        timelineLayout.add(title);

        if (todayEntries.isEmpty()) {
            Div empty = new Div();
            empty.addClassName("cu-card");
            empty.getStyle().set("padding", "28px").set("text-align", "center");
            empty.add(new H3("No Lectures Scheduled Today"));
            empty.add(new Paragraph("This faculty member has no teaching sessions scheduled for today."));
            timelineLayout.add(empty);
            return;
        }

        LocalTime now = TimeUtils.getNowInKolkata().toLocalTime();

        for (ScheduleEntry entry : todayEntries) {
            LocalTime st = TimeUtils.parseTime(entry.getStartTime());
            LocalTime et = TimeUtils.parseTime(entry.getEndTime());

            boolean isActive = st != null && et != null && !now.isBefore(st) && now.isBefore(et);
            boolean isCompleted = et != null && now.isAfter(et);

            Div card = new Div();
            card.addClassName("cu-card");
            card.getStyle()
                    .set("padding", "16px 20px")
                    .set("margin-bottom", "12px")
                    .set("display", "flex")
                    .set("justify-content", "space-between")
                    .set("align-items", "center")
                    .set("border-left", isActive ? "6px solid var(--cu-red)" : "1px solid var(--cu-border)");

            VerticalLayout details = new VerticalLayout();
            details.setPadding(false);
            details.setSpacing(false);

            Span timeSpan = new Span(entry.getStartTime() + " - " + entry.getEndTime());
            timeSpan.getStyle().set("font-size", "0.8rem").set("font-weight", "700").set("color", isActive ? "var(--cu-red)" : "var(--cu-text-muted)");

            String subName = entry.getCourseName() != null ? entry.getCourseName() : entry.getCourseCode();
            Span sub = new Span(subName);
            sub.getStyle().set("font-size", "1.05rem").set("font-weight", "700").set("color", "var(--cu-navy)");

            Span room = new Span("📍 Room: " + entry.getRoomNo() + " • Section: " + entry.getSection());
            room.getStyle().set("font-size", "0.85rem").set("color", "var(--cu-text-muted)");

            details.add(timeSpan, sub, room);

            Span statusPill = new Span(isActive ? "● ONGOING" : (isCompleted ? "COMPLETED" : "UPCOMING"));
            statusPill.getStyle()
                    .set("font-size", "0.75rem")
                    .set("font-weight", "700")
                    .set("padding", "4px 10px")
                    .set("border-radius", "9999px")
                    .set("background-color", isActive ? "var(--cu-red-light)" : (isCompleted ? "#F1F5F9" : "#E2E8F0"))
                    .set("color", isActive ? "var(--cu-red)" : (isCompleted ? "#94A3B8" : "var(--cu-navy)"));

            card.add(details, statusPill);
            timelineLayout.add(card);
        }
    }

    private void renderDynamicCard() {
        dynamicCard.removeAll();
        LocalTime now = TimeUtils.getNowInKolkata().toLocalTime();
        ScheduleStatus status = timingService.calculateScheduleStatus(todayEntries, now);

        H3 title = new H3("CURRENT STATUS");
        title.getStyle().set("color", "var(--cu-navy)").set("font-size", "1.1rem").set("font-weight", "800").set("margin", "0 0 16px 0");

        Div statusBox = new Div();
        statusBox.getStyle().set("background-color", "var(--cu-bg)").set("border-radius", "16px").set("padding", "20px").set("text-align", "center");

        if (status.getCurrentClass() != null) {
            ScheduleEntry active = status.getCurrentClass();
            H2 countdown = new H2(TimeUtils.formatRemainingSeconds(status.getRemainingSeconds()));
            countdown.getStyle().set("font-size", "2.2rem").set("font-weight", "900").set("color", "var(--cu-red)").set("margin", "0 0 4px 0").set("font-family", "monospace");
            Span label = new Span("Remaining in Active Lecture");
            label.getStyle().set("font-size", "0.8rem").set("color", "var(--cu-text-muted)");
            statusBox.add(countdown, label);

            Paragraph actSub = new Paragraph("Conducting: " + active.getCourseName());
            actSub.getStyle().set("font-weight", "700").set("margin-top", "16px").set("color", "var(--cu-navy)");
            Paragraph actRoom = new Paragraph("Room: " + active.getRoomNo());
            actRoom.getStyle().set("color", "var(--cu-text-muted)").set("margin", "0");
            dynamicCard.add(title, statusBox, actSub, actRoom);
        } else if (status.getNextClass() != null) {
            ScheduleEntry next = status.getNextClass();
            H2 countdown = new H2(TimeUtils.formatRemainingSeconds(status.getRemainingSeconds()));
            countdown.getStyle().set("font-size", "2.2rem").set("font-weight", "900").set("color", "var(--cu-navy)").set("margin", "0 0 4px 0").set("font-family", "monospace");
            Span label = new Span("Until Next Lecture");
            label.getStyle().set("font-size", "0.8rem").set("color", "var(--cu-text-muted)");
            statusBox.add(countdown, label);

            Paragraph nxtSub = new Paragraph("Next Up: " + next.getCourseName());
            nxtSub.getStyle().set("font-weight", "700").set("margin-top", "16px").set("color", "var(--cu-navy)");
            Paragraph nxtRoom = new Paragraph("Room: " + next.getRoomNo());
            nxtRoom.getStyle().set("color", "var(--cu-text-muted)").set("margin", "0");
            dynamicCard.add(title, statusBox, nxtSub, nxtRoom);
        } else {
            H2 freeText = new H2("Available");
            freeText.getStyle().set("font-size", "1.8rem").set("font-weight", "700").set("color", "var(--cu-green)").set("margin", "0");
            statusBox.add(freeText);
            Paragraph note = new Paragraph("No active or upcoming lectures at this time.");
            note.getStyle().set("color", "var(--cu-text-muted)").set("margin-top", "12px");
            dynamicCard.add(title, statusBox, note);
        }
    }
}
