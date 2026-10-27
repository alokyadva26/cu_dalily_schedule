package com.cudailyschedule.web;

import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.model.ScheduleStatus;
import com.cudailyschedule.model.Teacher;
import com.cudailyschedule.service.ScheduleTimingService;
import com.cudailyschedule.service.TeacherService;
import com.cudailyschedule.util.TimeUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

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
 * TeacherScheduleView provides a modern, clean, minimal Faculty Schedule dashboard
 * matching the university brand guidelines and design specification.
 */
@Route("teacher-schedule")
@PageTitle("Faculty Schedule — CU Daily Schedule")
public class TeacherScheduleView extends Div implements HasUrlParameter<String> {

    private final TeacherService teacherService;
    private final ScheduleTimingService timingService = new ScheduleTimingService();

    private Teacher currentTeacher;
    private List<ScheduleEntry> todayEntries = new ArrayList<>();
    private Registration pollRegistration;

    // Layout containers
    private final Div profileCard = new Div();
    private final Div scheduleContainer = new Div();
    private final Div statusCard = new Div();

    public TeacherScheduleView(TeacherService teacherService) {
        this.teacherService = teacherService;

        // Root container: light gray background, full viewport, no navbars
        setSizeFull();
        addClassName("cu-teacher-schedule-page");
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

        // Responsive style sheet injection
        injectResponsiveStyles();

        // Main content wrapper
        Div contentWrapper = new Div();
        contentWrapper.addClassName("cu-teacher-schedule-content");
        contentWrapper.getStyle()
                .set("max-width", "1440px")
                .set("width", "100%")
                .set("margin", "0 auto")
                .set("padding", "32px 40px 48px 40px")
                .set("box-sizing", "border-box")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "28px");

        // Top Teacher Profile Card
        profileCard.addClassName("cu-teacher-profile-card");
        profileCard.getStyle()
                .set("background-color", "#FFFFFF")
                .set("border", "1px solid #E5E7EB")
                .set("border-radius", "16px")
                .set("padding", "20px 28px")
                .set("box-shadow", "0 1px 3px rgba(0, 0, 0, 0.03)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "space-between")
                .set("flex-wrap", "wrap")
                .set("gap", "16px")
                .set("width", "100%")
                .set("box-sizing", "border-box");

        // Two-column Main Section
        Div mainCols = new Div();
        mainCols.addClassName("cu-teacher-main-cols");
        mainCols.getStyle()
                .set("display", "flex")
                .set("gap", "28px")
                .set("align-items", "flex-start")
                .set("width", "100%")
                .set("box-sizing", "border-box");

        // Left Column (Teaching Schedule ~68%)
        Div scheduleCol = new Div();
        scheduleCol.addClassName("cu-teacher-schedule-col");
        scheduleCol.getStyle()
                .set("flex", "68 1 0")
                .set("min-width", "0")
                .set("box-sizing", "border-box");

        Div scheduleHeader = new Div();
        scheduleHeader.getStyle().set("margin-bottom", "18px");

        H1 headingTitle = new H1();
        headingTitle.getStyle()
                .set("font-size", "28px")
                .set("font-weight", "800")
                .set("margin", "0 0 4px 0")
                .set("line-height", "1.2");

        Span todaysSpan = new Span("Today's ");
        todaysSpan.getStyle().set("color", "#111827");

        Span teachingScheduleSpan = new Span("Teaching Schedule");
        teachingScheduleSpan.getStyle().set("color", "#E30613");

        headingTitle.add(todaysSpan, teachingScheduleSpan);

        Paragraph subtitle = new Paragraph("Here are your classes for today.");
        subtitle.getStyle()
                .set("color", "#64748B")
                .set("font-size", "14px")
                .set("margin", "0");

        scheduleHeader.add(headingTitle, subtitle);
        scheduleCol.add(scheduleHeader, scheduleContainer);

        // Right Column (Current Status ~32%)
        Div statusCol = new Div();
        statusCol.addClassName("cu-teacher-status-col");
        statusCol.getStyle()
                .set("flex", "32 1 0")
                .set("min-width", "320px")
                .set("box-sizing", "border-box")
                .set("position", "sticky")
                .set("top", "32px");

        statusCard.addClassName("cu-teacher-status-card");
        statusCard.getStyle()
                .set("background-color", "#FFFFFF")
                .set("border", "1px solid #E5E7EB")
                .set("border-radius", "16px")
                .set("padding", "24px")
                .set("box-shadow", "0 1px 3px rgba(0, 0, 0, 0.03)")
                .set("box-sizing", "border-box");

        statusCol.add(statusCard);
        mainCols.add(scheduleCol, statusCol);

        contentWrapper.add(profileCard, mainCols);

        // Floating Settings button in bottom-right corner
        FloatingSettingsButton floatingSettingsBtn = new FloatingSettingsButton();

        add(contentWrapper, floatingSettingsBtn);
    }

    private void injectResponsiveStyles() {
        Span styleContainer = new Span();
        styleContainer.getElement().setProperty("innerHTML",
                "<style>" +
                        "@media (max-width: 1024px) {" +
                        "  .cu-teacher-schedule-content { padding: 20px !important; gap: 20px !important; }" +
                        "  .cu-teacher-main-cols { flex-direction: column !important; }" +
                        "  .cu-teacher-schedule-col, .cu-teacher-status-col { width: 100% !important; flex: none !important; position: static !important; }" +
                        "}" +
                        "@media (max-width: 640px) {" +
                        "  .cu-teacher-profile-card { padding: 16px 20px !important; }" +
                        "  .cu-teacher-lecture-card { flex-direction: column !important; align-items: flex-start !important; gap: 12px !important; padding: 16px !important; }" +
                        "  .cu-teacher-lecture-middle { padding: 0 !important; width: 100% !important; }" +
                        "}" +
                        "</style>");
        add(styleContainer);
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
        pollRegistration = attachEvent.getUI().addPollListener(e -> renderDynamicCard());
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
        profileCard.removeAll();
        if (currentTeacher == null) return;

        // Left section: Avatar + Name + Department
        Div left = new Div();
        left.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "16px");

        Div avatar = new Div();
        avatar.addClassName("cu-teacher-avatar");
        avatar.getStyle()
                .set("width", "56px")
                .set("height", "56px")
                .set("border-radius", "50%")
                .set("background-color", "#E30613")
                .set("color", "#FFFFFF")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("font-size", "1.6rem")
                .set("font-weight", "800")
                .set("flex-shrink", "0");

        String initial = currentTeacher.getEmployeeName() != null && !currentTeacher.getEmployeeName().isEmpty()
                ? currentTeacher.getEmployeeName().substring(0, 1).toUpperCase() : "T";
        avatar.setText(initial);

        Div textInfo = new Div();
        textInfo.getStyle().set("display", "flex").set("flex-direction", "column");

        H2 name = new H2(currentTeacher.getEmployeeName());
        name.getStyle()
                .set("font-size", "1.4rem")
                .set("font-weight", "800")
                .set("color", "#111827")
                .set("margin", "0 0 2px 0")
                .set("line-height", "1.2");

        String deptStr = currentTeacher.getDepartment() != null ? currentTeacher.getDepartment() : "CSE";
        Span dept = new Span("Department: " + deptStr);
        dept.getStyle()
                .set("color", "#64748B")
                .set("font-size", "0.92rem")
                .set("font-weight", "500");

        textInfo.add(name, dept);
        left.add(avatar, textInfo);

        // Right section: Light-red pill with ID badge icon
        Div idBadge = new Div();
        idBadge.addClassName("cu-teacher-id-badge");
        idBadge.getStyle()
                .set("background-color", "#FFF5F5")
                .set("border", "1px solid #FEE2E2")
                .set("border-radius", "10px")
                .set("padding", "8px 16px")
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "8px");

        Html idIconSvg = new Html(
                "<svg width=\"18\" height=\"18\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#E30613\" stroke-width=\"2.2\" stroke-linecap=\"round\" stroke-linejoin=\"round\">"
                + "<rect x=\"3\" y=\"4\" width=\"18\" height=\"16\" rx=\"2\"></rect>"
                + "<circle cx=\"9\" cy=\"10\" r=\"2\"></circle>"
                + "<line x1=\"15\" y1=\"8\" x2=\"17\" y2=\"8\"></line>"
                + "<line x1=\"15\" y1=\"12\" x2=\"17\" y2=\"12\"></line>"
                + "<line x1=\"7\" y1=\"16\" x2=\"17\" y2=\"16\"></line>"
                + "</svg>"
        );

        Span idText = new Span("ID: " + currentTeacher.getEmployeeId());
        idText.getStyle()
                .set("color", "#111827")
                .set("font-weight", "800")
                .set("font-size", "0.92rem");

        idBadge.add(idIconSvg, idText);

        profileCard.add(left, idBadge);
    }

    private void renderTimeline() {
        scheduleContainer.removeAll();

        if (todayEntries.isEmpty()) {
            Div empty = new Div();
            empty.getStyle()
                    .set("background-color", "#FFFFFF")
                    .set("border", "1px solid #E5E7EB")
                    .set("border-radius", "14px")
                    .set("padding", "32px")
                    .set("text-align", "center")
                    .set("color", "#64748B");
            empty.add(new Paragraph("No lectures scheduled for today."));
            scheduleContainer.add(empty);
            return;
        }

        LocalTime now = TimeUtils.getNowInKolkata().toLocalTime();

        for (ScheduleEntry entry : todayEntries) {
            LocalTime st = TimeUtils.parseTime(entry.getStartTime());
            LocalTime et = TimeUtils.parseTime(entry.getEndTime());

            boolean isActive = st != null && et != null && !now.isBefore(st) && now.isBefore(et);
            boolean isCompleted = et != null && now.isAfter(et);

            Div card = new Div();
            card.addClassName("cu-teacher-lecture-card");
            card.getStyle()
                    .set("background-color", isActive ? "#FFF5F5" : "#FFFFFF")
                    .set("border", isActive ? "1px solid #FECACA" : "1px solid #E5E7EB")
                    .set("border-left", isActive ? "5px solid #E30613" : (isCompleted ? "5px solid #16A34A" : "5px solid #111827"))
                    .set("border-radius", "14px")
                    .set("padding", "18px 24px")
                    .set("margin-bottom", "14px")
                    .set("box-shadow", "0 1px 3px rgba(0, 0, 0, 0.03)")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "space-between")
                    .set("box-sizing", "border-box")
                    .set("transition", "transform 0.15s ease, box-shadow 0.15s ease");

            // Left Part: Timing
            Div timeBox = new Div();
            timeBox.getStyle()
                    .set("display", "flex")
                    .set("flex-direction", "column")
                    .set("min-width", "95px")
                    .set("flex-shrink", "0");

            Span startTime = new Span(entry.getStartTime());
            startTime.getStyle()
                    .set("font-size", "15px")
                    .set("font-weight", "800")
                    .set("color", isActive ? "#E30613" : "#111827")
                    .set("line-height", "1.2");

            Span endTime = new Span(entry.getEndTime());
            endTime.getStyle()
                    .set("font-size", "13px")
                    .set("font-weight", "500")
                    .set("color", "#64748B")
                    .set("margin-top", "2px");

            timeBox.add(startTime, endTime);

            // Middle Part: Subject Name, Room, Section
            Div middleBox = new Div();
            middleBox.addClassName("cu-teacher-lecture-middle");
            middleBox.getStyle()
                    .set("flex", "1")
                    .set("padding", "0 24px")
                    .set("display", "flex")
                    .set("flex-direction", "column")
                    .set("gap", "4px");

            String subjectName = entry.getCourseName() != null ? entry.getCourseName() : entry.getCourseCode();
            Span subjectSpan = new Span(subjectName);
            subjectSpan.getStyle()
                    .set("font-size", "16px")
                    .set("font-weight", "800")
                    .set("color", "#111827");

            Div metaRow = new Div();
            metaRow.getStyle()
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("flex-wrap", "wrap")
                    .set("gap", "6px")
                    .set("font-size", "13px")
                    .set("color", "#64748B");

            Html pinSvg = new Html(
                    "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"#E30613\" style=\"margin-right: 4px; vertical-align: -2px;\">"
                    + "<path d=\"M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5a2.5 2.5 0 0 1 0-5 2.5 2.5 0 0 1 0 5z\"/>"
                    + "</svg>"
            );
            Span roomSpan = new Span("Room: " + (entry.getRoomNo() != null ? entry.getRoomNo() : "N/A"));
            roomSpan.getStyle().set("font-weight", "500");

            Div roomContainer = new Div(pinSvg, roomSpan);
            roomContainer.getStyle().set("display", "inline-flex").set("align-items", "center");

            Span dividerSpan = new Span("|");
            dividerSpan.getStyle().set("color", "#E5E7EB").set("margin", "0 6px");

            Html sectionSvg = new Html(
                    "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#E30613\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"margin-right: 4px; vertical-align: -2px;\">"
                    + "<path d=\"M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2\"></path>"
                    + "<circle cx=\"9\" cy=\"7\" r=\"4\"></circle>"
                    + "<path d=\"M23 21v-2a4 4 0 0 0-3-3.87\"></path>"
                    + "<path d=\"M16 3.13a4 4 0 0 1 0 7.75\"></path>"
                    + "</svg>"
            );
            Span secSpan = new Span("Section: " + (entry.getSection() != null ? entry.getSection() : "N/A"));
            secSpan.getStyle().set("font-weight", "500");

            Div secContainer = new Div(sectionSvg, secSpan);
            secContainer.getStyle().set("display", "inline-flex").set("align-items", "center");

            metaRow.add(roomContainer, dividerSpan, secContainer);
            middleBox.add(subjectSpan, metaRow);

            // Right Part: Status Badge
            Div badge = new Div();
            badge.getStyle()
                    .set("display", "inline-flex")
                    .set("align-items", "center")
                    .set("padding", "6px 14px")
                    .set("border-radius", "9999px")
                    .set("font-size", "11px")
                    .set("font-weight", "800")
                    .set("letter-spacing", "0.5px")
                    .set("flex-shrink", "0");

            if (isActive) {
                badge.getStyle().set("background-color", "#FEE2E2").set("color", "#E30613");
                Html ongoingDot = new Html("<svg width=\"10\" height=\"10\" viewBox=\"0 0 24 24\" fill=\"#E30613\" style=\"margin-right: 6px;\"><circle cx=\"12\" cy=\"12\" r=\"10\"/></svg>");
                badge.add(ongoingDot, new Span("ONGOING"));
            } else if (isCompleted) {
                badge.getStyle().set("background-color", "#DCFCE7").set("color", "#16A34A");
                Html checkSvg = new Html("<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"#16A34A\" style=\"margin-right: 6px;\"><path d=\"M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z\"/></svg>");
                badge.add(checkSvg, new Span("COMPLETED"));
            } else {
                badge.getStyle().set("background-color", "#F3F4F6").set("color", "#111827");
                Html clockSvg = new Html("<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#111827\" stroke-width=\"2.2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"margin-right: 6px;\"><circle cx=\"12\" cy=\"12\" r=\"10\"></circle><polyline points=\"12 6 12 12 16 14\"></polyline></svg>");
                badge.add(clockSvg, new Span("UPCOMING"));
            }

            card.add(timeBox, middleBox, badge);
            scheduleContainer.add(card);
        }
    }

    private void renderDynamicCard() {
        statusCard.removeAll();
        LocalTime now = TimeUtils.getNowInKolkata().toLocalTime();
        ScheduleStatus status = timingService.calculateScheduleStatus(todayEntries, now);

        // Header: Red vertical bar + CURRENT STATUS
        Div headerRow = new Div();
        headerRow.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("margin-bottom", "18px");

        Div redBar = new Div();
        redBar.getStyle()
                .set("width", "4px")
                .set("height", "18px")
                .set("background-color", "#E30613")
                .set("border-radius", "2px")
                .set("margin-right", "8px");

        Span title = new Span("CURRENT STATUS");
        title.getStyle()
                .set("font-size", "15px")
                .set("font-weight", "800")
                .set("color", "#111827")
                .set("letter-spacing", "0.5px");

        headerRow.add(redBar, title);

        // Availability / Activity status box
        Div statusBox = new Div();
        statusBox.getStyle()
                .set("border-radius", "14px")
                .set("padding", "22px 20px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("gap", "10px")
                .set("margin-bottom", "18px");

        Div msgRow = new Div();
        msgRow.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("gap", "8px")
                .set("color", "#64748B")
                .set("font-size", "13px")
                .set("text-align", "center");

        Html calendarIcon = new Html(
                "<svg width=\"16\" height=\"16\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#64748B\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\">"
                + "<rect x=\"3\" y=\"4\" width=\"18\" height=\"18\" rx=\"2\" ry=\"2\"></rect>"
                + "<line x1=\"16\" y1=\"2\" x2=\"16\" y2=\"6\"></line>"
                + "<line x1=\"8\" y1=\"2\" x2=\"8\" y2=\"6\"></line>"
                + "<line x1=\"3\" y1=\"10\" x2=\"21\" y2=\"10\"></line>"
                + "</svg>"
        );

        if (status.getCurrentClass() != null) {
            // Teacher is currently conducting a lecture
            ScheduleEntry active = status.getCurrentClass();
            statusBox.getStyle()
                    .set("background-color", "#FFF5F5")
                    .set("border", "1px solid #FECACA");

            Html redDot = new Html("<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"#E30613\"><circle cx=\"12\" cy=\"12\" r=\"10\"/></svg>");
            Span inLectureSpan = new Span("In Lecture");
            inLectureSpan.getStyle()
                    .set("color", "#E30613")
                    .set("font-size", "24px")
                    .set("font-weight", "800");

            statusBox.add(redDot, inLectureSpan);

            String timeRem = TimeUtils.formatRemainingSeconds(status.getRemainingSeconds());
            String actMsg = "Conducting " + (active.getCourseName() != null ? active.getCourseName() : active.getCourseCode())
                    + " in Room " + (active.getRoomNo() != null ? active.getRoomNo() : "N/A") + " (" + timeRem + " remaining)";
            Span msgText = new Span(actMsg);
            msgRow.add(calendarIcon, msgText);

        } else if (status.getNextClass() != null) {
            // Teacher is currently Available, but has an upcoming lecture later today
            ScheduleEntry next = status.getNextClass();
            statusBox.getStyle()
                    .set("background-color", "#F0FDF4")
                    .set("border", "1px solid #DCFCE7");

            Html greenDot = new Html("<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"#16A34A\"><circle cx=\"12\" cy=\"12\" r=\"10\"/></svg>");
            Span availSpan = new Span("Available");
            availSpan.getStyle()
                    .set("color", "#16A34A")
                    .set("font-size", "24px")
                    .set("font-weight", "800");

            statusBox.add(greenDot, availSpan);

            String nxtMsg = "Next class at " + next.getStartTime() + " in Room " + (next.getRoomNo() != null ? next.getRoomNo() : "N/A") + ".";
            Span msgText = new Span(nxtMsg);
            msgRow.add(calendarIcon, msgText);

        } else {
            // Teacher is Available and no active/upcoming lectures remain today
            statusBox.getStyle()
                    .set("background-color", "#F0FDF4")
                    .set("border", "1px solid #DCFCE7");

            Html greenDot = new Html("<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"#16A34A\"><circle cx=\"12\" cy=\"12\" r=\"10\"/></svg>");
            Span availSpan = new Span("Available");
            availSpan.getStyle()
                    .set("color", "#16A34A")
                    .set("font-size", "24px")
                    .set("font-weight", "800");

            statusBox.add(greenDot, availSpan);

            Span msgText = new Span("No active or upcoming lectures at this time.");
            msgRow.add(calendarIcon, msgText);
        }

        statusCard.add(headerRow, statusBox, msgRow);
    }
}
