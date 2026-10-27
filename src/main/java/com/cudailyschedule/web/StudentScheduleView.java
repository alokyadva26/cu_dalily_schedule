package com.cudailyschedule.web;

import com.cudailyschedule.model.ClassSession;
import com.cudailyschedule.service.DatabaseService;
import com.cudailyschedule.service.StudentService;
import com.cudailyschedule.util.TimeUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * StudentScheduleView provides the redesigned student dashboard matching
 * the university reference layout, typography, strict color palette,
 * dynamic IST countdown, and 2x2 overview statistics.
 */
@Route("student")
@PageTitle("Student Daily Timetable — CU Daily Schedule")
public class StudentScheduleView extends Div {

    private final StudentService studentService;

    // Header elements
    private final Span dateSpan = new Span();
    private final Span liveClockSpan = new Span();
    private final Span onlinePill = new Span();
    private final Span offlinePill = new Span();

    // Main section elements
    private final H1 headingTitle = new H1();
    private final Paragraph headingSubtitle = new Paragraph();
    private final Button viewTomorrowBtn = new Button();
    private final Div scheduleListContainer = new Div();
    private final Div currentLectureCard = new Div();
    private final Div overviewCard = new Div();

    // State
    private List<ClassSession> currentClasses;
    private ClassSession selectedSession;
    private boolean isViewingTomorrow = false;
    private Registration pollRegistration;

    public StudentScheduleView(StudentService studentService) {
        this.studentService = studentService;

        addClassName("cu-student-page");
        getStyle()
                .set("background-color", "#F3F4F6")
                .set("min-height", "100vh")
                .set("width", "100%")
                .set("margin", "0")
                .set("padding", "0")
                .set("box-sizing", "border-box")
                .set("font-family", "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif")
                .set("display", "flex")
                .set("flex-direction", "column");

        // Responsive stylesheet injection
        injectResponsiveStyles();

        // Top Header
        buildHeader();

        // Main 2-column container
        Div mainContent = new Div();
        mainContent.addClassName("cu-student-content");
        mainContent.getStyle()
                .set("max-width", "1440px")
                .set("width", "100%")
                .set("margin", "0 auto")
                .set("padding", "28px 40px 48px 40px")
                .set("display", "flex")
                .set("gap", "28px")
                .set("box-sizing", "border-box")
                .set("align-items", "flex-start");

        // Left Column (Schedule list ~68%)
        Div scheduleCol = new Div();
        scheduleCol.addClassName("cu-schedule-column");
        scheduleCol.getStyle()
                .set("flex", "68 1 0")
                .set("min-width", "0")
                .set("box-sizing", "border-box");

        Div scheduleHeader = new Div();
        scheduleHeader.addClassName("cu-schedule-header");
        scheduleHeader.getStyle()
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center")
                .set("margin-bottom", "20px");

        headingTitle.addClassName("cu-schedule-title");
        headingTitle.getStyle()
                .set("font-size", "28px")
                .set("font-weight", "800")
                .set("color", "#111827")
                .set("margin", "0")
                .set("line-height", "1.2");

        headingSubtitle.addClassName("cu-schedule-subtitle");
        headingSubtitle.getStyle()
                .set("font-size", "14px")
                .set("color", "#6B7280")
                .set("margin", "6px 0 0 0")
                .set("font-weight", "400");

        Div titlesBox = new Div(headingTitle, headingSubtitle);

        viewTomorrowBtn.addClassName("cu-btn-tomorrow");
        viewTomorrowBtn.getStyle()
                .set("background-color", "#FEE2E2")
                .set("color", "#E30613")
                .set("font-weight", "600")
                .set("font-size", "13px")
                .set("border-radius", "9999px")
                .set("padding", "8px 18px")
                .set("border", "none")
                .set("cursor", "pointer")
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "6px")
                .set("transition", "all 0.15s ease");

        viewTomorrowBtn.addClickListener(e -> {
            isViewingTomorrow = !isViewingTomorrow;
            refreshData();
        });

        scheduleHeader.add(titlesBox, viewTomorrowBtn);
        scheduleCol.add(scheduleHeader, scheduleListContainer);

        // Right Column (Current lecture & overview ~32%)
        Div sidebarCol = new Div();
        sidebarCol.addClassName("cu-sidebar-column");
        sidebarCol.getStyle()
                .set("flex", "32 1 0")
                .set("min-width", "320px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "20px")
                .set("box-sizing", "border-box")
                .set("position", "sticky")
                .set("top", "80px");

        currentLectureCard.addClassName("cu-dashboard-card");
        currentLectureCard.getStyle()
                .set("background", "#FFFFFF")
                .set("border", "1px solid #E5E7EB")
                .set("border-radius", "16px")
                .set("padding", "20px")
                .set("box-shadow", "0 1px 3px rgba(0, 0, 0, 0.03)")
                .set("box-sizing", "border-box");

        overviewCard.addClassName("cu-dashboard-card");
        overviewCard.getStyle()
                .set("background", "#FFFFFF")
                .set("border", "1px solid #E5E7EB")
                .set("border-radius", "16px")
                .set("padding", "20px")
                .set("box-shadow", "0 1px 3px rgba(0, 0, 0, 0.03)")
                .set("box-sizing", "border-box");

        sidebarCol.add(currentLectureCard, overviewCard);

        mainContent.add(scheduleCol, sidebarCol);
        
        // Floating Settings button at bottom-right corner
        FloatingSettingsButton floatingSettingsBtn = new FloatingSettingsButton();
        add(mainContent, floatingSettingsBtn);

        // Initial load
        refreshData();
    }

    private void injectResponsiveStyles() {
        Span styleContainer = new Span();
        styleContainer.getElement().setProperty("innerHTML",
                "<style>" +
                        "@media (max-width: 1024px) {" +
                        "  .cu-student-content { flex-direction: column !important; padding: 20px !important; }" +
                        "  .cu-schedule-column, .cu-sidebar-column { width: 100% !important; flex: none !important; position: static !important; }" +
                        "}" +
                        "@media (max-width: 640px) {" +
                        "  .cu-student-header { padding: 12px 16px !important; flex-wrap: wrap !important; gap: 12px !important; }" +
                        "  .cu-student-header-right { gap: 8px !important; flex-wrap: wrap !important; }" +
                        "  .cu-student-header-divider { display: none !important; }" +
                        "  .cu-lecture-card { flex-direction: column !important; align-items: flex-start !important; gap: 12px !important; padding: 16px !important; }" +
                        "  .cu-lecture-info { padding: 0 !important; width: 100% !important; }" +
                        "}" +
                        "</style>");
        add(styleContainer);
    }

    private void buildHeader() {
        Div header = new Div();
        header.addClassName("cu-student-header");
        header.getStyle()
                .set("background-color", "#FFFFFF")
                .set("border-bottom", "1px solid #E5E7EB")
                .set("padding", "14px 40px")
                .set("width", "100%")
                .set("box-sizing", "border-box")
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center")
                .set("box-shadow", "0 1px 3px rgba(0, 0, 0, 0.03)")
                .set("position", "sticky")
                .set("top", "0")
                .set("z-index", "50");

        // Left: University Logo (Discover. Learn. Empower.)
        Image logo = new Image("/images/cu-logo.png", "Chandigarh University");
        logo.addClassName("cu-student-header-logo");
        logo.setHeight("48px");
        logo.setMaxHeight("48px");
        logo.getStyle()
                .set("height", "48px")
                .set("max-height", "48px")
                .set("width", "auto")
                .set("object-fit", "contain")
                .set("cursor", "pointer")
                .set("display", "block");
        logo.addClickListener(e -> UI.getCurrent().navigate(""));

        // Right items
        Div rightContainer = new Div();
        rightContainer.addClassName("cu-student-header-right");
        rightContainer.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "16px");

        // 1. Date with red calendar icon
        Div dateItem = new Div();
        dateItem.addClassName("cu-student-date-badge");
        dateItem.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "8px")
                .set("color", "#111827")
                .set("font-size", "14px")
                .set("font-weight", "500");

        Span calIcon = new Span();
        calIcon.getElement().setProperty("innerHTML",
                "<svg width=\"18\" height=\"18\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#E30613\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"vertical-align:middle; flex-shrink:0;\">" +
                        "<rect x=\"3\" y=\"4\" width=\"18\" height=\"18\" rx=\"2\" ry=\"2\"></rect>" +
                        "<line x1=\"16\" y1=\"2\" x2=\"16\" y2=\"6\"></line>" +
                        "<line x1=\"8\" y1=\"2\" x2=\"8\" y2=\"6\"></line>" +
                        "<line x1=\"3\" y1=\"10\" x2=\"21\" y2=\"10\"></line>" +
                        "</svg>");
        dateItem.add(calIcon, dateSpan);

        // 2. Live IST clock pill (dark charcoal background)
        Div clockItem = new Div();
        clockItem.addClassName("cu-student-clock-pill");
        clockItem.getStyle()
                .set("background-color", "#111827")
                .set("color", "#FFFFFF")
                .set("border-radius", "10px")
                .set("padding", "7px 14px")
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "8px")
                .set("font-size", "13px")
                .set("font-weight", "600")
                .set("font-family", "monospace")
                .set("letter-spacing", "0.3px")
                .set("box-shadow", "0 1px 2px rgba(0, 0, 0, 0.1)");

        Span clockIcon = new Span();
        clockIcon.getElement().setProperty("innerHTML",
                "<svg width=\"15\" height=\"15\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#FFFFFF\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"vertical-align:middle; flex-shrink:0;\">" +
                        "<circle cx=\"12\" cy=\"12\" r=\"10\"></circle>" +
                        "<polyline points=\"12 6 12 12 16 14\"></polyline>" +
                        "</svg>");
        clockItem.add(clockIcon, liveClockSpan);

        // 3. Subtle vertical divider
        Div divider = new Div();
        divider.addClassName("cu-student-header-divider");
        divider.getStyle()
                .set("width", "1px")
                .set("height", "22px")
                .set("background-color", "#E5E7EB")
                .set("margin", "0 4px");

        // 4. Online status pill
        onlinePill.addClassName("cu-pill-online");
        onlinePill.getStyle()
                .set("background-color", "#DCFCE7")
                .set("color", "#16A34A")
                .set("border-radius", "9999px")
                .set("padding", "6px 14px")
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "8px")
                .set("font-size", "13px")
                .set("font-weight", "600")
                .set("cursor", "pointer")
                .set("transition", "opacity 0.2s ease");
        onlinePill.getElement().setProperty("innerHTML",
                "<span style=\"width:8px; height:8px; border-radius:50%; background-color:#16A34A; display:inline-block;\"></span><span>Online</span>");
        onlinePill.getElement().setAttribute("title", "Database Status: Online (Click for Diagnostics)");
        onlinePill.addClickListener(e -> new AdminScheduleDialog().open());

        // 5. Offline status pill
        offlinePill.addClassName("cu-pill-offline");
        offlinePill.getStyle()
                .set("background-color", "#FEE2E2")
                .set("color", "#E30613")
                .set("border-radius", "9999px")
                .set("padding", "6px 14px")
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "8px")
                .set("font-size", "13px")
                .set("font-weight", "600")
                .set("cursor", "pointer")
                .set("transition", "opacity 0.2s ease");
        offlinePill.getElement().setProperty("innerHTML",
                "<span style=\"width:8px; height:8px; border-radius:50%; background-color:#E30613; display:inline-block;\"></span><span>Offline</span>");
        offlinePill.getElement().setAttribute("title", "Database Status: Offline (Click for Diagnostics)");
        offlinePill.addClickListener(e -> new AdminScheduleDialog().open());

        rightContainer.add(dateItem, clockItem, divider, onlinePill, offlinePill);

        header.add(logo, rightContainer);
        add(header);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
        ui.setPollInterval(1000);
        pollRegistration = ui.addPollListener(e -> updateClockAndTimers());
        updateClockAndTimers();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if (pollRegistration != null) {
            pollRegistration.remove();
            pollRegistration = null;
        }
        if (detachEvent.getUI() != null) {
            detachEvent.getUI().setPollInterval(-1);
        }
    }

    public void refreshData() {
        ZonedDateTime kolkataNow = TimeUtils.getNowInKolkata();
        if (isViewingTomorrow) {
            ZonedDateTime tomorrow = kolkataNow.plusDays(1);
            String tomorrowDay = tomorrow.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            currentClasses = studentService.getClassesForDay(tomorrowDay);

            headingTitle.getElement().setProperty("innerHTML", "Tomorrow's <span style=\"color:#E30613;\">Schedule</span>");
            headingSubtitle.setText("Here are your classes for tomorrow.");
            viewTomorrowBtn.getElement().setProperty("innerHTML", "← View Today");
        } else {
            currentClasses = studentService.getTodayClasses();

            headingTitle.getElement().setProperty("innerHTML", "Today's <span style=\"color:#E30613;\">Schedule</span>");
            headingSubtitle.setText("Here are your classes for today.");
            viewTomorrowBtn.getElement().setProperty("innerHTML", "View Tomorrow <span style=\"font-size:15px; margin-left:4px;\">→</span>");
        }

        // Selection: default to ongoing session if exists, otherwise next upcoming or first
        LocalTime now = kolkataNow.toLocalTime();
        ClassSession ongoing = (!isViewingTomorrow) ? findOngoingSession(now) : null;
        if (ongoing != null) {
            selectedSession = ongoing;
        } else {
            ClassSession next = (!isViewingTomorrow) ? findNextSession(now) : null;
            if (next != null) {
                selectedSession = next;
            } else if (currentClasses != null && !currentClasses.isEmpty()) {
                selectedSession = currentClasses.get(0);
            } else {
                selectedSession = null;
            }
        }

        renderScheduleList();
        renderCurrentLectureCard();
        renderOverviewCard();
        updateConnectivityPills();
    }

    private void updateClockAndTimers() {
        ZonedDateTime now = TimeUtils.getNowInKolkata();
        String dateStr = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH));
        String timeStr = now.format(DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH)) + " IST";

        dateSpan.setText(dateStr);
        liveClockSpan.setText(timeStr);

        updateConnectivityPills();

        // Refresh dynamic countdown and cards without rebuilding entire DOM
        renderCurrentLectureCard();
        renderScheduleList();
        renderOverviewCard();
    }

    private void updateConnectivityPills() {
        boolean connected = DatabaseService.isConnected();
        if (connected) {
            onlinePill.getStyle().set("opacity", "1.0");
            offlinePill.getStyle().set("opacity", "0.4");
        } else {
            onlinePill.getStyle().set("opacity", "0.4");
            offlinePill.getStyle().set("opacity", "1.0");
        }
    }

    private ClassSession findOngoingSession(LocalTime now) {
        if (currentClasses == null) return null;
        for (ClassSession s : currentClasses) {
            LocalTime st = TimeUtils.parseTime(s.getStartTime());
            LocalTime et = TimeUtils.parseTime(s.getEndTime());
            if (st != null && et != null && !now.isBefore(st) && now.isBefore(et)) {
                return s;
            }
        }
        return null;
    }

    private ClassSession findNextSession(LocalTime now) {
        if (currentClasses == null) return null;
        for (ClassSession s : currentClasses) {
            LocalTime st = TimeUtils.parseTime(s.getStartTime());
            if (st != null && now.isBefore(st)) {
                return s;
            }
        }
        return null;
    }

    private String formatFaculty(String faculty) {
        if (faculty == null) return "N/A";
        return faculty.replaceAll("(?<=[^\\s])\\(", " (");
    }

    private void renderScheduleList() {
        scheduleListContainer.removeAll();

        if (currentClasses == null || currentClasses.isEmpty()) {
            Div emptyCard = new Div();
            emptyCard.addClassName("cu-dashboard-card");
            emptyCard.getStyle()
                    .set("padding", "40px")
                    .set("text-align", "center")
                    .set("color", "#6B7280")
                    .set("background", "#FFFFFF")
                    .set("border", "1px solid #E5E7EB")
                    .set("border-radius", "16px");
            emptyCard.getElement().setProperty("innerHTML",
                    "<h3 style=\"font-size:18px; font-weight:700; color:#111827; margin:0 0 8px 0;\">No Classes Scheduled</h3>" +
                            "<p style=\"font-size:14px; margin:0;\">There are no classes scheduled for this day. Enjoy your free time!</p>");
            scheduleListContainer.add(emptyCard);
            return;
        }

        LocalTime now = TimeUtils.getNowInKolkata().toLocalTime();

        for (ClassSession session : currentClasses) {
            LocalTime st = TimeUtils.parseTime(session.getStartTime());
            LocalTime et = TimeUtils.parseTime(session.getEndTime());

            boolean isActive = (!isViewingTomorrow) && st != null && et != null && !now.isBefore(st) && now.isBefore(et);
            boolean isCompleted = (!isViewingTomorrow) && et != null && now.isAfter(et);

            Div card = new Div();
            card.addClassName("cu-lecture-card");
            card.getStyle()
                    .set("background", isActive ? "#FFF5F5" : "#FFFFFF")
                    .set("border", isActive ? "1px solid #FECACA" : "1px solid #E5E7EB")
                    .set("border-left", isActive ? "6px solid #E30613" : (isCompleted ? "6px solid #16A34A" : "6px solid #374151"))
                    .set("border-radius", "16px")
                    .set("padding", "18px 24px")
                    .set("margin-bottom", "14px")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "space-between")
                    .set("box-shadow", isActive ? "0 2px 8px rgba(227, 6, 19, 0.08)" : "0 1px 3px rgba(0, 0, 0, 0.03)")
                    .set("cursor", "pointer")
                    .set("box-sizing", "border-box")
                    .set("width", "100%")
                    .set("transition", "transform 0.15s ease, box-shadow 0.15s ease");

            if (isActive) {
                card.addClassName("cu-lecture-card-ongoing");
            } else if (isCompleted) {
                card.addClassName("cu-lecture-card-completed");
            } else {
                card.addClassName("cu-lecture-card-upcoming");
            }

            // Left: Time Block
            Div timeBlock = new Div();
            timeBlock.addClassName("cu-time-block");
            timeBlock.getStyle()
                    .set("width", "110px")
                    .set("flex-shrink", "0")
                    .set("display", "flex")
                    .set("flex-direction", "column");

            Span startSpan = new Span(session.getStartTime());
            startSpan.addClassName("cu-time-start");
            startSpan.getStyle()
                    .set("font-size", "16px")
                    .set("font-weight", "800")
                    .set("color", isActive ? "#E30613" : "#111827")
                    .set("margin-bottom", "3px")
                    .set("line-height", "1.2");

            if (isActive) {
                startSpan.addClassName("cu-time-start-ongoing");
            }

            Span endSpan = new Span(session.getEndTime());
            endSpan.addClassName("cu-time-end");
            endSpan.getStyle()
                    .set("font-size", "13px")
                    .set("font-weight", "500")
                    .set("color", "#6B7280")
                    .set("line-height", "1.2");

            timeBlock.add(startSpan, endSpan);

            // Middle: Lecture Info
            Div info = new Div();
            info.addClassName("cu-lecture-info");
            info.getStyle()
                    .set("flex", "1")
                    .set("padding", "0 24px")
                    .set("min-width", "0");

            Div courseTitle = new Div();
            courseTitle.addClassName("cu-lecture-course-code");
            courseTitle.getStyle()
                    .set("font-size", "17px")
                    .set("font-weight", "700")
                    .set("color", "#111827")
                    .set("margin-bottom", "8px")
                    .set("letter-spacing", "-0.2px");
            courseTitle.setText(session.getSubject());

            Div metaRow = new Div();
            metaRow.addClassName("cu-lecture-meta-row");
            metaRow.getStyle()
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("gap", "24px")
                    .set("flex-wrap", "wrap");

            // Location
            Span roomItem = new Span();
            roomItem.addClassName("cu-meta-item");
            roomItem.getStyle()
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("gap", "6px")
                    .set("font-size", "13px")
                    .set("color", "#4B5563");
            roomItem.getElement().setProperty("innerHTML",
                    "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"#E30613\" style=\"flex-shrink:0;\">" +
                            "<path d=\"M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z\"/>" +
                            "</svg> <span>Room: " + (session.getRoom() != null ? session.getRoom() : "N/A") + "</span>");

            // Instructor
            Span instructorItem = new Span();
            instructorItem.addClassName("cu-meta-item");
            instructorItem.getStyle()
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("gap", "6px")
                    .set("font-size", "13px")
                    .set("color", "#4B5563");
            instructorItem.getElement().setProperty("innerHTML",
                    "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"#374151\" style=\"flex-shrink:0;\">" +
                            "<path d=\"M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z\"/>" +
                            "</svg> <span>" + formatFaculty(session.getFaculty()) + "</span>");

            metaRow.add(roomItem, instructorItem);
            info.add(courseTitle, metaRow);

            // Right: Status Badge
            Span statusBadge = new Span();
            statusBadge.getStyle()
                    .set("font-size", "11px")
                    .set("font-weight", "700")
                    .set("padding", "6px 14px")
                    .set("border-radius", "9999px")
                    .set("display", "inline-flex")
                    .set("align-items", "center")
                    .set("gap", "6px")
                    .set("letter-spacing", "0.5px")
                    .set("flex-shrink", "0");

            if (isActive) {
                statusBadge.addClassName("cu-badge-ongoing");
                statusBadge.getStyle()
                        .set("background-color", "#FEE2E2")
                        .set("color", "#E30613");
                statusBadge.getElement().setProperty("innerHTML",
                        "<span style=\"width:8px; height:8px; border-radius:50%; background-color:#E30613; display:inline-block;\"></span> <span>ONGOING</span>");
            } else if (isCompleted) {
                statusBadge.addClassName("cu-badge-completed");
                statusBadge.getStyle()
                        .set("background-color", "#DCFCE7")
                        .set("color", "#16A34A");
                statusBadge.getElement().setProperty("innerHTML",
                        "<svg width=\"15\" height=\"15\" viewBox=\"0 0 24 24\" fill=\"#16A34A\" style=\"vertical-align:middle; flex-shrink:0;\">" +
                                "<path d=\"M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z\"/>" +
                                "</svg> <span>COMPLETED</span>");
            } else {
                statusBadge.addClassName("cu-badge-upcoming");
                statusBadge.getStyle()
                        .set("background-color", "#F3F4F6")
                        .set("color", "#374151");
                statusBadge.getElement().setProperty("innerHTML",
                        "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#374151\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"vertical-align:middle; flex-shrink:0;\">" +
                                "<circle cx=\"12\" cy=\"12\" r=\"10\"></circle>" +
                                "<polyline points=\"12 6 12 12 16 14\"></polyline>" +
                                "</svg> <span>UPCOMING</span>");
            }

            card.add(timeBlock, info, statusBadge);

            // Clicking a card allows user to inspect it in the right card
            card.addClickListener(e -> {
                selectedSession = session;
                renderCurrentLectureCard();
            });

            scheduleListContainer.add(card);
        }
    }

    private void renderCurrentLectureCard() {
        currentLectureCard.removeAll();

        // Title Header with Red Accent Bar
        Div cardHeader = new Div();
        cardHeader.addClassName("cu-card-header");
        cardHeader.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "8px")
                .set("margin-bottom", "16px");

        Div accentBar = new Div();
        accentBar.addClassName("cu-card-accent-bar");
        accentBar.getStyle()
                .set("width", "4px")
                .set("height", "20px")
                .set("background-color", "#E30613")
                .set("border-radius", "2px")
                .set("flex-shrink", "0");

        H2 cardTitle = new H2("Current Lecture");
        cardTitle.addClassName("cu-card-title");
        cardTitle.getStyle()
                .set("font-size", "18px")
                .set("font-weight", "700")
                .set("color", "#111827")
                .set("margin", "0")
                .set("line-height", "1.2");

        cardHeader.add(accentBar, cardTitle);

        LocalTime now = TimeUtils.getNowInKolkata().toLocalTime();
        ClassSession ongoing = (!isViewingTomorrow) ? findOngoingSession(now) : null;
        ClassSession nextSession = (!isViewingTomorrow) ? findNextSession(now) : null;

        // Choose session to display: ongoing has top priority, then selected, then next
        ClassSession targetSession;
        if (ongoing != null) {
            targetSession = ongoing;
        } else if (selectedSession != null) {
            targetSession = selectedSession;
        } else if (nextSession != null) {
            targetSession = nextSession;
        } else if (currentClasses != null && !currentClasses.isEmpty()) {
            targetSession = currentClasses.get(0);
        } else {
            targetSession = null;
        }

        if (targetSession == null) {
            Div emptyState = new Div();
            emptyState.getStyle()
                    .set("padding", "32px")
                    .set("text-align", "center")
                    .set("color", "#6B7280");
            emptyState.getElement().setProperty("innerHTML",
                    "<h3 style=\"font-size:16px; font-weight:700; color:#111827; margin:0 0 6px 0;\">No lecture in progress</h3>" +
                            "<p style=\"font-size:13px; margin:0;\">There are no lectures scheduled at this moment.</p>");
            currentLectureCard.add(cardHeader, emptyState);
            return;
        }

        LocalTime st = TimeUtils.parseTime(targetSession.getStartTime());
        LocalTime et = TimeUtils.parseTime(targetSession.getEndTime());

        boolean isOngoing = (!isViewingTomorrow) && st != null && et != null && !now.isBefore(st) && now.isBefore(et);
        boolean isUpcoming = isViewingTomorrow || (st != null && now.isBefore(st));

        // Countdown Box
        Div countdownBox = new Div();
        countdownBox.addClassName("cu-countdown-box");
        countdownBox.getStyle()
                .set("background-color", "#FFF7F7")
                .set("border-radius", "16px")
                .set("padding", "16px 20px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("gap", "20px")
                .set("margin-bottom", "16px");

        // Calculate countdown values and circle ring
        String countdownText;
        String countdownLabel;
        double progressRatio = 0.0;

        if (isOngoing && et != null) {
            long totalSec = Math.max(1, Duration.between(st, et).getSeconds());
            long remainingSec = Math.max(0, Duration.between(now, et).getSeconds());
            countdownText = TimeUtils.formatRemainingSeconds(remainingSec);
            countdownLabel = "Remaining in Lecture";
            progressRatio = (double) remainingSec / totalSec;
        } else if (isUpcoming && st != null && !isViewingTomorrow) {
            long remainingSec = Math.max(0, Duration.between(now, st).getSeconds());
            countdownText = TimeUtils.formatRemainingSeconds(remainingSec);
            countdownLabel = "Starts In";
            progressRatio = 1.0;
        } else if (isViewingTomorrow) {
            countdownText = targetSession.getStartTime();
            countdownLabel = "Starts Tomorrow";
            progressRatio = 1.0;
        } else {
            countdownText = "00:00";
            countdownLabel = "Concluded";
            progressRatio = 0.0;
        }

        // SVG Circular Progress Ring (84x84, radius 34)
        double circumference = 2.0 * Math.PI * 34.0; // ~213.63
        double dashOffset = circumference * (1.0 - Math.min(1.0, Math.max(0.0, progressRatio)));

        Span circleWidget = new Span();
        circleWidget.getElement().setProperty("innerHTML",
                "<svg width=\"84\" height=\"84\" viewBox=\"0 0 84 84\" style=\"flex-shrink:0;\">" +
                        "  <!-- Background track circle -->" +
                        "  <circle cx=\"42\" cy=\"42\" r=\"34\" stroke=\"#FEE2E2\" stroke-width=\"6\" fill=\"none\" />" +
                        "  <!-- Progress ring arc -->" +
                        "  <circle cx=\"42\" cy=\"42\" r=\"34\" stroke=\"#E30613\" stroke-width=\"6\" fill=\"none\" stroke-linecap=\"round\"" +
                        "          stroke-dasharray=\"" + String.format(Locale.ENGLISH, "%.2f", circumference) + "\" " +
                        "          stroke-dashoffset=\"" + String.format(Locale.ENGLISH, "%.2f", dashOffset) + "\" " +
                        "          transform=\"rotate(-90 42 42)\" />" +
                        "  <!-- Graduation Cap Icon in Center -->" +
                        "  <path d=\"M12 3L1 9l11 6 9-4.91V17h2V9L12 3zm0 8.47L4.54 9 12 4.93 19.46 9 12 11.47zM5 13.18v4L12 21l7-3.82v-4L12 17l-7-3.82z\" " +
                        "        fill=\"#E30613\" transform=\"translate(29, 29) scale(1.1)\" />" +
                        "</svg>");

        Div timerInfoBox = new Div();
        timerInfoBox.getStyle().set("display", "flex").set("flex-direction", "column");

        Div timerLabelDiv = new Div();
        timerLabelDiv.addClassName("cu-timer-remaining-label");
        timerLabelDiv.getStyle()
                .set("font-size", "13px")
                .set("color", "#6B7280")
                .set("font-weight", "500")
                .set("margin-bottom", "4px");
        timerLabelDiv.setText(countdownLabel);

        Div timerValueDiv = new Div();
        timerValueDiv.addClassName("cu-timer-value");
        timerValueDiv.getStyle()
                .set("font-size", "34px")
                .set("font-weight", "800")
                .set("color", "#E30613")
                .set("line-height", "1")
                .set("letter-spacing", "-0.5px")
                .set("font-family", "monospace");
        timerValueDiv.setText(countdownText);

        timerInfoBox.add(timerLabelDiv, timerValueDiv);
        countdownBox.add(circleWidget, timerInfoBox);

        // Course code & Session badge row
        Div courseRow = new Div();
        courseRow.addClassName("cu-current-course-row");
        courseRow.getStyle()
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center")
                .set("margin-bottom", "14px");

        Span courseCode = new Span(targetSession.getSubject());
        courseCode.addClassName("cu-current-course-code");
        courseCode.getStyle()
                .set("font-size", "17px")
                .set("font-weight", "700")
                .set("color", "#111827");

        Span sessionBadge = new Span(targetSession.isBreak() ? "RECESS" : "SESSION");
        sessionBadge.addClassName("cu-badge-session");
        sessionBadge.getStyle()
                .set("background-color", "#FEE2E2")
                .set("color", "#E30613")
                .set("font-size", "10px")
                .set("font-weight", "700")
                .set("padding", "3px 8px")
                .set("border-radius", "6px")
                .set("letter-spacing", "0.5px");

        courseRow.add(courseCode, sessionBadge);

        // 4 Detail rows with red icons
        Div detailsContainer = new Div();
        detailsContainer.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "2px");

        detailsContainer.add(createDetailRow(
                "<svg width=\"15\" height=\"15\" viewBox=\"0 0 24 24\" fill=\"#E30613\" style=\"flex-shrink:0;\">" +
                        "<path d=\"M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z\"/>" +
                        "</svg>",
                "Room / Block",
                targetSession.getRoom() != null ? targetSession.getRoom() : "N/A"
        ));

        detailsContainer.add(createDetailRow(
                "<svg width=\"15\" height=\"15\" viewBox=\"0 0 24 24\" fill=\"#E30613\" style=\"flex-shrink:0;\">" +
                        "<path d=\"M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z\"/>" +
                        "</svg>",
                "Instructor",
                formatFaculty(targetSession.getFaculty())
        ));

        detailsContainer.add(createDetailRow(
                "<svg width=\"15\" height=\"15\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#E30613\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"flex-shrink:0;\">" +
                        "<circle cx=\"12\" cy=\"12\" r=\"10\"></circle>" +
                        "<polyline points=\"12 6 12 12 16 14\"></polyline>" +
                        "</svg>",
                "Timing",
                targetSession.getStartTime() + " - " + targetSession.getEndTime()
        ));

        detailsContainer.add(createDetailRow(
                "<svg width=\"15\" height=\"15\" viewBox=\"0 0 24 24\" fill=\"#E30613\" style=\"flex-shrink:0;\">" +
                        "<path d=\"M18 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zM6 4h5v8l-2.5-1.5L6 12V4z\"/>" +
                        "</svg>",
                "Session Type",
                deriveSessionType(targetSession)
        ));

        currentLectureCard.add(cardHeader, countdownBox, courseRow, detailsContainer);
    }

    private Div createDetailRow(String iconSvg, String label, String value) {
        Div row = new Div();
        row.addClassName("cu-detail-row");
        row.getStyle()
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center")
                .set("padding", "6px 0");

        Div left = new Div();
        left.addClassName("cu-detail-label");
        left.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "8px")
                .set("color", "#6B7280")
                .set("font-size", "13px")
                .set("font-weight", "400");

        Span iconSpan = new Span();
        iconSpan.getElement().setProperty("innerHTML", iconSvg);
        Span labelSpan = new Span(label);
        left.add(iconSpan, labelSpan);

        Span valSpan = new Span(value);
        valSpan.addClassName("cu-detail-value");
        valSpan.getStyle()
                .set("font-weight", "700")
                .set("color", "#111827")
                .set("font-size", "13px")
                .set("text-align", "right");

        row.add(left, valSpan);
        return row;
    }

    private void renderOverviewCard() {
        overviewCard.removeAll();

        Div cardHeader = new Div();
        cardHeader.addClassName("cu-card-header");
        cardHeader.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "8px")
                .set("margin-bottom", "16px");

        Div accentBar = new Div();
        accentBar.addClassName("cu-card-accent-bar");
        accentBar.getStyle()
                .set("width", "4px")
                .set("height", "20px")
                .set("background-color", "#E30613")
                .set("border-radius", "2px")
                .set("flex-shrink", "0");

        H2 cardTitle = new H2(isViewingTomorrow ? "Tomorrow's Overview" : "Today's Overview");
        cardTitle.addClassName("cu-card-title");
        cardTitle.getStyle()
                .set("font-size", "18px")
                .set("font-weight", "700")
                .set("color", "#111827")
                .set("margin", "0")
                .set("line-height", "1.2");

        cardHeader.add(accentBar, cardTitle);

        // Calculate statistics dynamically
        int total = currentClasses != null ? currentClasses.size() : 0;
        int completed = 0;
        int ongoing = 0;
        int upcoming = 0;

        if (isViewingTomorrow) {
            upcoming = total;
        } else if (currentClasses != null) {
            LocalTime now = TimeUtils.getNowInKolkata().toLocalTime();
            for (ClassSession s : currentClasses) {
                LocalTime st = TimeUtils.parseTime(s.getStartTime());
                LocalTime et = TimeUtils.parseTime(s.getEndTime());
                if (et != null && now.isAfter(et)) {
                    completed++;
                } else if (st != null && et != null && !now.isBefore(st) && now.isBefore(et)) {
                    ongoing++;
                } else {
                    upcoming++;
                }
            }
        }

        Div grid = new Div();
        grid.addClassName("cu-overview-grid");
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "1fr 1fr")
                .set("gap", "12px");

        // 1. Completed (green styling)
        Div tileCompleted = new Div();
        tileCompleted.addClassNames("cu-stat-tile", "cu-stat-completed");
        tileCompleted.getStyle()
                .set("background-color", "#DCFCE7")
                .set("border-radius", "12px")
                .set("padding", "12px 14px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "12px")
                .set("box-sizing", "border-box");

        Span checkIcon = new Span();
        checkIcon.getElement().setProperty("innerHTML",
                "<div style=\"width:32px; height:32px; border-radius:50%; background:#16A34A; display:flex; align-items:center; justify-content:center; flex-shrink:0;\">" +
                        "<svg width=\"18\" height=\"18\" viewBox=\"0 0 24 24\" fill=\"#FFFFFF\"><path d=\"M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z\"/></svg>" +
                        "</div>");
        Div textCompleted = new Div();
        Div countCompleted = new Div();
        countCompleted.addClassName("cu-stat-count");
        countCompleted.getStyle().set("color", "#111827").set("font-size", "20px").set("font-weight", "800").set("line-height", "1.1");
        countCompleted.setText(String.valueOf(completed));
        Div labelCompleted = new Div();
        labelCompleted.addClassName("cu-stat-label");
        labelCompleted.getStyle().set("color", "#16A34A").set("font-size", "12px").set("font-weight", "600");
        labelCompleted.setText("Completed");
        textCompleted.add(countCompleted, labelCompleted);
        tileCompleted.add(checkIcon, textCompleted);

        // 2. Ongoing (red styling)
        Div tileOngoing = new Div();
        tileOngoing.addClassNames("cu-stat-tile", "cu-stat-ongoing");
        tileOngoing.getStyle()
                .set("background-color", "#FEE2E2")
                .set("border-radius", "12px")
                .set("padding", "12px 14px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "12px")
                .set("box-sizing", "border-box");

        Span playIcon = new Span();
        playIcon.getElement().setProperty("innerHTML",
                "<div style=\"width:32px; height:32px; border-radius:50%; background:#E30613; display:flex; align-items:center; justify-content:center; flex-shrink:0;\">" +
                        "<svg width=\"16\" height=\"16\" viewBox=\"0 0 24 24\" fill=\"#FFFFFF\"><path d=\"M8 5v14l11-7z\"/></svg>" +
                        "</div>");
        Div textOngoing = new Div();
        Div countOngoing = new Div();
        countOngoing.addClassName("cu-stat-count");
        countOngoing.getStyle().set("color", "#E30613").set("font-size", "20px").set("font-weight", "800").set("line-height", "1.1");
        countOngoing.setText(String.valueOf(ongoing));
        Div labelOngoing = new Div();
        labelOngoing.addClassName("cu-stat-label");
        labelOngoing.getStyle().set("color", "#E30613").set("font-size", "12px").set("font-weight", "600");
        labelOngoing.setText("Ongoing");
        textOngoing.add(countOngoing, labelOngoing);
        tileOngoing.add(playIcon, textOngoing);

        // 3. Upcoming (neutral gray styling)
        Div tileUpcoming = new Div();
        tileUpcoming.addClassNames("cu-stat-tile", "cu-stat-upcoming");
        tileUpcoming.getStyle()
                .set("background-color", "#F3F4F6")
                .set("border-radius", "12px")
                .set("padding", "12px 14px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "12px")
                .set("box-sizing", "border-box");

        Span upClockIcon = new Span();
        upClockIcon.getElement().setProperty("innerHTML",
                "<div style=\"width:32px; height:32px; border-radius:50%; background:#E5E7EB; display:flex; align-items:center; justify-content:center; flex-shrink:0;\">" +
                        "<svg width=\"18\" height=\"18\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#374151\" stroke-width=\"2.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><circle cx=\"12\" cy=\"12\" r=\"10\"></circle><polyline points=\"12 6 12 12 16 14\"></polyline></svg>" +
                        "</div>");
        Div textUpcoming = new Div();
        Div countUpcoming = new Div();
        countUpcoming.addClassName("cu-stat-count");
        countUpcoming.getStyle().set("color", "#111827").set("font-size", "20px").set("font-weight", "800").set("line-height", "1.1");
        countUpcoming.setText(String.valueOf(upcoming));
        Div labelUpcoming = new Div();
        labelUpcoming.addClassName("cu-stat-label");
        labelUpcoming.getStyle().set("color", "#4B5563").set("font-size", "12px").set("font-weight", "600");
        labelUpcoming.setText("Upcoming");
        textUpcoming.add(countUpcoming, labelUpcoming);
        tileUpcoming.add(upClockIcon, textUpcoming);

        // 4. Total Lectures (red/charcoal styling)
        Div tileTotal = new Div();
        tileTotal.addClassNames("cu-stat-tile", "cu-stat-total");
        tileTotal.getStyle()
                .set("background-color", "#FEE2E2")
                .set("border-radius", "12px")
                .set("padding", "12px 14px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "12px")
                .set("box-sizing", "border-box");

        Span totalIcon = new Span();
        totalIcon.getElement().setProperty("innerHTML",
                "<div style=\"width:32px; height:32px; border-radius:50%; background:#FEE2E2; border:1px solid #FECACA; display:flex; align-items:center; justify-content:center; flex-shrink:0;\">" +
                        "<svg width=\"18\" height=\"18\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#E30613\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><rect x=\"3\" y=\"4\" width=\"18\" height=\"18\" rx=\"2\" ry=\"2\"></rect><line x1=\"16\" y1=\"2\" x2=\"16\" y2=\"6\"></line><line x1=\"8\" y1=\"2\" x2=\"8\" y2=\"6\"></line><line x1=\"3\" y1=\"10\" x2=\"21\" y2=\"10\"></line></svg>" +
                        "</div>");
        Div textTotal = new Div();
        Div countTotal = new Div();
        countTotal.addClassName("cu-stat-count");
        countTotal.getStyle().set("color", "#E30613").set("font-size", "20px").set("font-weight", "800").set("line-height", "1.1");
        countTotal.setText(String.valueOf(total));
        Div labelTotal = new Div();
        labelTotal.addClassName("cu-stat-label");
        labelTotal.getStyle().set("color", "#E30613").set("font-size", "12px").set("font-weight", "600");
        labelTotal.setText("Total Lectures");
        textTotal.add(countTotal, labelTotal);
        tileTotal.add(totalIcon, textTotal);

        grid.add(tileCompleted, tileOngoing, tileUpcoming, tileTotal);
        overviewCard.add(cardHeader, grid);
    }

    private String deriveSessionType(ClassSession session) {
        if (session == null) return "Lecture";
        if (session.isBreak()) return "Break";
        String subject = session.getSubject();
        if (subject == null) return "Lecture";
        if (subject.contains("..P::") || subject.contains(".P::") || (subject.contains("-305") && subject.contains("GP-A"))) {
            return "Practical";
        }
        if (subject.contains("..T::") || subject.contains(".T::")) {
            return "Tutorial";
        }
        if (subject.contains("..L::") || subject.contains(".L::")) {
            return "Lecture";
        }
        return "Lecture";
    }
}
