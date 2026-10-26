package com.cudailyschedule.web;

import com.cudailyschedule.model.ClassSession;
import com.cudailyschedule.service.ScheduleTimingService;
import com.cudailyschedule.service.StudentService;
import com.cudailyschedule.util.TimeUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * StudentScheduleView provides student timetable timeline, real-time countdown widget,
 * active lecture tracking, and tomorrow preview in Chrome.
 */
@Route(value = "student", layout = MainLayout.class)
@PageTitle("Student Daily Timetable — CU Daily Schedule")
public class StudentScheduleView extends VerticalLayout {

    private final StudentService studentService;

    private final Span liveClockSpan = new Span();
    private final Span dateSpan = new Span();
    private final VerticalLayout timelineLayout = new VerticalLayout();
    private final Div detailsPanel = new Div();

    private List<ClassSession> currentClasses;
    private ClassSession selectedSession;
    private boolean isViewingTomorrow = false;

    public StudentScheduleView(StudentService studentService) {
        this.studentService = studentService;

        setSizeFull();
        setSpacing(false);
        setPadding(false);
        addClassName("cu-watermark-container");

        // Top Navigation Bar
        HorizontalLayout navBar = new HorizontalLayout();
        navBar.setWidthFull();
        navBar.setPadding(true);
        navBar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        navBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Button backBtn = new Button("← Back to Home", e -> UI.getCurrent().navigate(""));
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backBtn.getStyle()
                .set("color", "var(--cu-navy)")
                .set("font-weight", "700")
                .set("background-color", "#FFFFFF")
                .set("border", "1px solid var(--cu-border)")
                .set("border-radius", "12px");

        // Date & Clock Badges
        HorizontalLayout dateClock = new HorizontalLayout(dateSpan, liveClockSpan);
        dateClock.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        dateClock.setSpacing(true);

        dateSpan.getStyle().set("color", "var(--cu-text-muted)").set("font-weight", "600");
        liveClockSpan.getStyle()
                .set("background-color", "var(--cu-navy)")
                .set("color", "#FFFFFF")
                .set("font-weight", "700")
                .set("padding", "4px 12px")
                .set("border-radius", "8px")
                .set("font-family", "monospace")
                .set("font-size", "0.95rem");

        navBar.add(backBtn, dateClock);
        add(navBar);

        // Main 2-Column Split Content
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.getStyle()
                .set("padding", "0 24px 24px 24px")
                .set("gap", "24px")
                .set("flex-wrap", "wrap")
                .set("align-items", "flex-start");

        // Left Timeline Column
        timelineLayout.getStyle()
                .set("flex", "1 1 540px")
                .set("min-width", "320px");
        timelineLayout.setPadding(false);
        timelineLayout.setSpacing(true);

        // Right Details Column (Sticky)
        detailsPanel.addClassName("cu-card");
        detailsPanel.getStyle()
                .set("flex", "0 0 380px")
                .set("min-width", "320px")
                .set("padding", "24px")
                .set("position", "sticky")
                .set("top", "24px");

        mainContent.add(timelineLayout, detailsPanel);
        add(mainContent);

        // Initial Data Load
        refreshData();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        attachEvent.getUI().setPollInterval(1000);
        attachEvent.getUI().addPollListener(e -> updateClockAndTimers());
        updateClockAndTimers();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if (detachEvent.getUI() != null) {
            detachEvent.getUI().setPollInterval(-1);
        }
    }

    public void refreshData() {
        ZonedDateTime kolkataNow = TimeUtils.getNowInKolkata();
        if (isViewingTomorrow) {
            ZonedDateTime tomorrow = kolkataNow.plusDays(1);
            String tomorrowDay = tomorrow.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
            currentClasses = studentService.getClassesForDay(tomorrowDay);
        } else {
            currentClasses = studentService.getTodayClasses();
        }

        // Auto-select active or next session
        if (currentClasses != null && !currentClasses.isEmpty()) {
            LocalTime nowTime = kolkataNow.toLocalTime();
            selectedSession = currentClasses.stream()
                    .filter(s -> {
                        LocalTime st = TimeUtils.parseTime(s.getStartTime());
                        LocalTime et = TimeUtils.parseTime(s.getEndTime());
                        return st != null && et != null && !nowTime.isBefore(st) && nowTime.isBefore(et);
                    })
                    .findFirst()
                    .orElse(currentClasses.get(0));
        } else {
            selectedSession = null;
        }

        renderTimeline();
        renderDetailsPanel();
    }

    private void updateClockAndTimers() {
        ZonedDateTime now = TimeUtils.getNowInKolkata();
        String dateStr = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        String timeStr = now.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));

        dateSpan.setText(dateStr);
        liveClockSpan.setText("🕒 " + timeStr + " IST");

        // Re-render details countdown
        renderDetailsPanel();
    }

    private void renderTimeline() {
        timelineLayout.removeAll();

        // Timeline Header Title
        HorizontalLayout titleRow = new HorizontalLayout();
        titleRow.setWidthFull();
        titleRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        titleRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        H2 headerTitle = new H2(isViewingTomorrow ? "Tomorrow's Schedule" : "Today's Schedule");
        headerTitle.getStyle()
                .set("font-size", "1.5rem")
                .set("font-weight", "800")
                .set("color", "var(--cu-navy)")
                .set("margin", "0");

        Button toggleTomorrowBtn = new Button(isViewingTomorrow ? "← View Today" : "View Tomorrow →");
        toggleTomorrowBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        toggleTomorrowBtn.getStyle()
                .set("color", "var(--cu-red)")
                .set("font-weight", "700");
        toggleTomorrowBtn.addClickListener(e -> {
            isViewingTomorrow = !isViewingTomorrow;
            refreshData();
        });

        titleRow.add(headerTitle, toggleTomorrowBtn);
        timelineLayout.add(titleRow);

        if (currentClasses == null || currentClasses.isEmpty()) {
            Div emptyCard = new Div();
            emptyCard.addClassName("cu-card");
            emptyCard.getStyle()
                    .set("padding", "32px")
                    .set("text-align", "center")
                    .set("width", "100%");
            emptyCard.add(new H3("🎉 No Classes Scheduled"));
            emptyCard.add(new Paragraph("There are no classes scheduled for this day. Enjoy your free time!"));
            timelineLayout.add(emptyCard);
            return;
        }

        LocalTime now = TimeUtils.getNowInKolkata().toLocalTime();

        for (ClassSession session : currentClasses) {
            LocalTime st = TimeUtils.parseTime(session.getStartTime());
            LocalTime et = TimeUtils.parseTime(session.getEndTime());

            boolean isActive = st != null && et != null && !now.isBefore(st) && now.isBefore(et);
            boolean isCompleted = et != null && now.isAfter(et);
            boolean isSelected = selectedSession != null && selectedSession.getSubject().equals(session.getSubject()) && selectedSession.getStartTime().equals(session.getStartTime());

            Div card = new Div();
            card.addClassName("cu-card");
            card.addClassName("cu-card-interactive");
            card.getStyle()
                    .set("padding", "16px 20px")
                    .set("margin-bottom", "12px")
                    .set("display", "flex")
                    .set("justify-content", "space-between")
                    .set("align-items", "center")
                    .set("border-left", isActive ? "6px solid var(--cu-red)" : (isSelected ? "6px solid var(--cu-navy)" : "1px solid var(--cu-border)"));

            // Left details
            VerticalLayout cardDetails = new VerticalLayout();
            cardDetails.setPadding(false);
            cardDetails.setSpacing(false);

            Span timeBadge = new Span(session.getStartTime() + " - " + session.getEndTime());
            timeBadge.getStyle()
                    .set("font-size", "0.8rem")
                    .set("font-weight", "700")
                    .set("color", isActive ? "var(--cu-red)" : "var(--cu-text-muted)");

            Span courseName = new Span(session.getSubject());
            courseName.getStyle()
                    .set("font-size", "1.05rem")
                    .set("font-weight", "700")
                    .set("color", "var(--cu-navy)");

            Span meta = new Span("📍 Room: " + session.getRoom() + " • 👨‍🏫 " + session.getFaculty());
            meta.getStyle().set("font-size", "0.85rem").set("color", "var(--cu-text-muted)");

            cardDetails.add(timeBadge, courseName, meta);

            // Right status pill
            Span statusPill = new Span();
            statusPill.getStyle()
                    .set("font-size", "0.75rem")
                    .set("font-weight", "700")
                    .set("padding", "4px 10px")
                    .set("border-radius", "9999px");

            if (isActive) {
                statusPill.setText("● ONGOING");
                statusPill.getStyle().set("background-color", "var(--cu-red-light)").set("color", "var(--cu-red)");
            } else if (isCompleted) {
                statusPill.setText("COMPLETED");
                statusPill.getStyle().set("background-color", "#F1F5F9").set("color", "#94A3B8");
            } else {
                statusPill.setText("UPCOMING");
                statusPill.getStyle().set("background-color", "#E2E8F0").set("color", "var(--cu-navy)");
            }

            card.add(cardDetails, statusPill);
            card.addClickListener(e -> {
                selectedSession = session;
                renderTimeline();
                renderDetailsPanel();
            });

            timelineLayout.add(card);
        }
    }

    private void renderDetailsPanel() {
        detailsPanel.removeAll();

        if (selectedSession == null) {
            detailsPanel.add(new H3("Select a Class"));
            detailsPanel.add(new Paragraph("Click any lecture card on the left to inspect detailed specifications and countdown."));
            return;
        }

        LocalTime now = TimeUtils.getNowInKolkata().toLocalTime();
        LocalTime st = TimeUtils.parseTime(selectedSession.getStartTime());
        LocalTime et = TimeUtils.parseTime(selectedSession.getEndTime());

        boolean isActive = st != null && et != null && !now.isBefore(st) && now.isBefore(et);
        boolean isUpcoming = st != null && now.isBefore(st);

        H3 panelHeader = new H3(isActive ? "CURRENT LECTURE" : (isUpcoming ? "UPCOMING LECTURE" : "LECTURE DETAILS"));
        panelHeader.getStyle()
                .set("color", isActive ? "var(--cu-red)" : "var(--cu-navy)")
                .set("font-size", "1.1rem")
                .set("font-weight", "800")
                .set("margin", "0 0 16px 0")
                .set("letter-spacing", "1px");

        // Countdown Circle Widget
        Div countdownWidget = new Div();
        countdownWidget.getStyle()
                .set("background-color", "var(--cu-bg)")
                .set("border-radius", "16px")
                .set("padding", "20px")
                .set("text-align", "center")
                .set("margin-bottom", "20px");

        if (isActive && et != null) {
            long remainingSeconds = java.time.Duration.between(now, et).getSeconds();
            String remainingFormatted = TimeUtils.formatRemainingSeconds(remainingSeconds);

            H2 timerText = new H2(remainingFormatted);
            timerText.getStyle()
                    .set("font-size", "2.2rem")
                    .set("font-weight", "900")
                    .set("color", "var(--cu-red)")
                    .set("margin", "0 0 4px 0")
                    .set("font-family", "monospace");

            Span timerSub = new Span("Remaining in Lecture");
            timerSub.getStyle().set("font-size", "0.8rem").set("color", "var(--cu-text-muted)").set("font-weight", "600");

            countdownWidget.add(timerText, timerSub);
        } else if (isUpcoming && st != null) {
            long untilSeconds = java.time.Duration.between(now, st).getSeconds();
            String untilFormatted = TimeUtils.formatRemainingSeconds(untilSeconds);

            H2 timerText = new H2(untilFormatted);
            timerText.getStyle()
                    .set("font-size", "2.2rem")
                    .set("font-weight", "900")
                    .set("color", "var(--cu-navy)")
                    .set("margin", "0 0 4px 0")
                    .set("font-family", "monospace");

            Span timerSub = new Span("Starts In");
            timerSub.getStyle().set("font-size", "0.8rem").set("color", "var(--cu-text-muted)").set("font-weight", "600");

            countdownWidget.add(timerText, timerSub);
        } else {
            H2 timerText = new H2("Concluded");
            timerText.getStyle()
                    .set("font-size", "1.8rem")
                    .set("font-weight", "700")
                    .set("color", "var(--cu-text-muted)")
                    .set("margin", "0");
            countdownWidget.add(timerText);
        }

        // Subject & Code
        H2 subTitle = new H2(selectedSession.getSubject());
        subTitle.getStyle()
                .set("font-size", "1.3rem")
                .set("font-weight", "800")
                .set("color", "var(--cu-navy)")
                .set("margin", "0 0 8px 0");

        Span codeBadge = new Span(selectedSession.isBreak() ? "RECESS" : "SESSION");
        codeBadge.getStyle()
                .set("background-color", "var(--cu-navy)")
                .set("color", "#FFFFFF")
                .set("font-size", "0.75rem")
                .set("font-weight", "700")
                .set("padding", "3px 8px")
                .set("border-radius", "6px");

        // Specifications List
        VerticalLayout specs = new VerticalLayout();
        specs.setPadding(false);
        specs.setSpacing(true);
        specs.getStyle().set("margin-top", "16px");

        specs.add(createSpecRow("📍 Room / Block", selectedSession.getRoom()));
        specs.add(createSpecRow("👨‍🏫 Instructor", selectedSession.getFaculty()));
        specs.add(createSpecRow("🕒 Timing", selectedSession.getStartTime() + " - " + selectedSession.getEndTime()));
        specs.add(createSpecRow("📚 Session Type", selectedSession.isBreak() ? "Break" : "Lecture"));

        detailsPanel.add(panelHeader, countdownWidget, subTitle, codeBadge, specs);
    }

    private HorizontalLayout createSpecRow(String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("color", "var(--cu-text-muted)").set("font-size", "0.85rem");

        Span valSpan = new Span(value != null ? value : "N/A");
        valSpan.getStyle().set("font-weight", "700").set("color", "var(--cu-navy)").set("font-size", "0.85rem");

        row.add(labelSpan, valSpan);
        return row;
    }
}
