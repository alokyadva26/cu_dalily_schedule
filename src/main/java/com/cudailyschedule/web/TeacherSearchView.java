package com.cudailyschedule.web;

import com.cudailyschedule.model.Teacher;
import com.cudailyschedule.service.TeacherService;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Optional;

/**
 * TeacherSearchView provides a minimal, clean, centered Teacher Schedule portal
 * matching the university brand guidelines and reference design.
 */
@Route("teacher")
@PageTitle("Teacher Schedule — CU Daily Schedule")
public class TeacherSearchView extends Div {

    private final TeacherService teacherService;
    private final TextField idField;

    public TeacherSearchView(TeacherService teacherService) {
        this.teacherService = teacherService;

        // Viewport-wide centered container: clean light gray background (#F9FAFB)
        setSizeFull();
        addClassName("cu-teacher-page");
        getStyle()
                .set("min-height", "100vh")
                .set("width", "100%")
                .set("background-color", "#F9FAFB")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("box-sizing", "border-box")
                .set("padding", "24px 16px")
                .set("margin", "0");

        // Central Teacher Schedule Card
        Div card = new Div();
        card.addClassName("cu-teacher-card");
        card.getStyle()
                .set("background-color", "#FFFFFF")
                .set("width", "100%")
                .set("max-width", "600px")
                .set("border-radius", "18px")
                .set("border", "1px solid #E5E7EB")
                .set("box-shadow", "0 10px 30px -5px rgba(0, 0, 0, 0.05), 0 4px 6px -2px rgba(0, 0, 0, 0.02)")
                .set("padding", "44px 44px 40px 44px")
                .set("box-sizing", "border-box")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "stretch");

        // 1. Circular graduation-cap icon with subtle light-red background
        Div iconCircle = new Div();
        iconCircle.addClassName("cu-teacher-icon-circle");
        iconCircle.getStyle()
                .set("width", "64px")
                .set("height", "64px")
                .set("border-radius", "50%")
                .set("background-color", "#FEE2E2")
                .set("margin", "0 auto 18px auto")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        Icon capIcon = VaadinIcon.ACADEMY_CAP.create();
        capIcon.setColor("#E30613");
        capIcon.setSize("32px");
        iconCircle.add(capIcon);

        // 2. Heading: "TEACHER SCHEDULE"
        H1 title = new H1();
        title.addClassName("cu-teacher-title");
        title.getStyle()
                .set("text-align", "center")
                .set("margin", "0 0 8px 0")
                .set("font-size", "1.85rem")
                .set("font-weight", "900")
                .set("letter-spacing", "0.5px")
                .set("line-height", "1.2");

        Span teacherSpan = new Span("TEACHER");
        teacherSpan.addClassName("cu-teacher-title-dark");
        teacherSpan.getStyle().set("color", "#111827");

        Span spaceSpan = new Span(" ");

        Span scheduleSpan = new Span("SCHEDULE");
        scheduleSpan.addClassName("cu-teacher-title-red");
        scheduleSpan.getStyle().set("color", "#E30613");

        title.add(teacherSpan, spaceSpan, scheduleSpan);

        // 3. Subtitle
        Paragraph subtitle = new Paragraph("Search faculty schedule by Employee ID or simulate QR badge scanning");
        subtitle.addClassName("cu-teacher-subtitle");
        subtitle.getStyle()
                .set("text-align", "center")
                .set("color", "#6B7280")
                .set("font-size", "0.92rem")
                .set("line-height", "1.45")
                .set("max-width", "440px")
                .set("margin", "0 auto 28px auto");

        // 4. Label: "Employee ID"
        Span idLabel = new Span("Employee ID");
        idLabel.addClassName("cu-teacher-label");
        idLabel.getStyle()
                .set("display", "block")
                .set("font-size", "0.88rem")
                .set("font-weight", "700")
                .set("color", "#111827")
                .set("margin-bottom", "8px")
                .set("text-align", "left");

        // 5. Employee ID input field with user icon and placeholder "e.g. E19761"
        idField = new TextField();
        idField.addClassName("cu-teacher-input");
        idField.setPlaceholder("e.g. E19761");
        idField.setClearButtonVisible(true);
        idField.setWidthFull();
        idField.getStyle().set("margin-bottom", "18px");

        Icon userIcon = VaadinIcon.USER.create();
        userIcon.setColor("#9CA3AF");
        userIcon.setSize("18px");
        userIcon.getStyle().set("margin-left", "4px").set("margin-right", "4px");
        idField.setPrefixComponent(userIcon);
        idField.addKeyPressListener(Key.ENTER, e -> performSearch(idField.getValue()));

        // 6. Primary red button: "Search Faculty Schedule"
        Button searchBtn = new Button();
        searchBtn.addClassName("cu-btn-search");
        searchBtn.setWidthFull();
        searchBtn.getStyle()
                .set("min-height", "48px")
                .set("background-color", "#E30613")
                .set("color", "#FFFFFF")
                .set("border", "none")
                .set("border-radius", "12px")
                .set("font-weight", "700")
                .set("font-size", "0.98rem")
                .set("cursor", "pointer")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("margin-bottom", "20px");

        Icon searchIcon = VaadinIcon.SEARCH.create();
        searchIcon.setColor("#FFFFFF");
        searchIcon.setSize("16px");
        searchIcon.getStyle().set("margin-right", "8px");
        searchBtn.setIcon(searchIcon);
        searchBtn.setText("Search Faculty Schedule");
        searchBtn.addClickListener(e -> performSearch(idField.getValue()));

        // 7. Subtle horizontal divider with "OR" in the center
        Div divider = new Div();
        divider.addClassName("cu-divider-or");
        divider.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("width", "100%")
                .set("margin-bottom", "20px");

        Div lineLeft = new Div();
        lineLeft.addClassName("cu-divider-line");
        lineLeft.getStyle().set("flex", "1").set("height", "1px").set("background-color", "#E5E7EB");

        Span orText = new Span("OR");
        orText.addClassName("cu-divider-text");
        orText.getStyle()
                .set("padding", "0 14px")
                .set("color", "#9CA3AF")
                .set("font-size", "0.78rem")
                .set("font-weight", "600")
                .set("letter-spacing", "1px");

        Div lineRight = new Div();
        lineRight.addClassName("cu-divider-line");
        lineRight.getStyle().set("flex", "1").set("height", "1px").set("background-color", "#E5E7EB");

        divider.add(lineLeft, orText, lineRight);

        // 8. Secondary button: "Simulate QR Badge Scan (E19761)"
        Button qrScanBtn = new Button();
        qrScanBtn.addClassName("cu-btn-qr");
        qrScanBtn.setWidthFull();
        qrScanBtn.getStyle()
                .set("min-height", "48px")
                .set("background-color", "#FFF5F5")
                .set("border", "1px solid #FECACA")
                .set("color", "#E30613")
                .set("border-radius", "12px")
                .set("font-weight", "700")
                .set("font-size", "0.95rem")
                .set("cursor", "pointer")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("margin-bottom", "26px");

        Icon cameraIcon = VaadinIcon.CAMERA.create();
        cameraIcon.setColor("#E30613");
        cameraIcon.setSize("18px");
        cameraIcon.getStyle().set("margin-right", "8px");
        qrScanBtn.setIcon(cameraIcon);
        qrScanBtn.setText("Simulate QR Badge Scan (E19761)");
        qrScanBtn.addClickListener(e -> {
            idField.setValue("E19761");
            performSearch("E19761");
        });

        // 9. Label: "Sample Faculty IDs:"
        Span sampleLabel = new Span("Sample Faculty IDs:");
        sampleLabel.addClassName("cu-sample-label");
        sampleLabel.getStyle()
                .set("display", "block")
                .set("text-align", "center")
                .set("width", "100%")
                .set("color", "#6B7280")
                .set("font-size", "0.82rem")
                .set("font-weight", "600")
                .set("margin-bottom", "10px");

        // 10. Two sample ID buttons: E19761 (Dr. Hardeep) & E16525 (Er. Paramjit)
        HorizontalLayout sampleRow = new HorizontalLayout();
        sampleRow.setWidthFull();
        sampleRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        sampleRow.setSpacing(true);
        sampleRow.getStyle()
                .set("gap", "12px")
                .set("flex-wrap", "wrap");

        Button sample1 = createSampleButton("E19761 (Dr. Hardeep)", "E19761");
        Button sample2 = createSampleButton("E16525 (Er. Paramjit)", "E16525");
        sampleRow.add(sample1, sample2);

        // Add all elements in the exact required order
        card.add(
                iconCircle,
                title,
                subtitle,
                idLabel,
                idField,
                searchBtn,
                divider,
                qrScanBtn,
                sampleLabel,
                sampleRow
        );

        // Direct CSS injection for input-field part styling
        Html inputStyle = new Html("<style>"
                + ".cu-teacher-input::part(input-field) { background-color: #F9FAFB !important; border: 1px solid #E5E7EB !important; border-radius: 12px !important; min-height: 48px !important; padding: 0 12px !important; box-sizing: border-box !important; }"
                + ".cu-teacher-input::part(input-field):focus-within { border-color: #E30613 !important; box-shadow: 0 0 0 2px rgba(227, 6, 19, 0.15) !important; background-color: #FFFFFF !important; }"
                + ".cu-teacher-input::part(value) { color: #111827 !important; font-weight: 500 !important; font-size: 0.95rem !important; }"
                + "</style>");

        add(card, inputStyle);
    }

    private Button createSampleButton(String label, String id) {
        Button btn = new Button(label);
        btn.addClassName("cu-sample-btn");
        btn.getStyle()
                .set("background-color", "#FFFFFF")
                .set("border", "1px solid #E5E7EB")
                .set("border-radius", "10px")
                .set("color", "#111827")
                .set("font-size", "0.82rem")
                .set("font-weight", "600")
                .set("padding", "8px 16px")
                .set("height", "38px")
                .set("cursor", "pointer")
                .set("box-shadow", "0 1px 2px rgba(0, 0, 0, 0.03)");
        btn.addClickListener(e -> {
            idField.setValue(id);
            performSearch(id);
        });
        return btn;
    }

    private void performSearch(String rawId) {
        if (rawId == null || rawId.trim().isEmpty()) {
            Notification notification = Notification.show("Please enter an Employee ID (e.g. E19761)", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        String searchId = rawId.trim().toUpperCase();
        Optional<Teacher> teacherOpt = teacherService.findTeacher(searchId);

        if (teacherOpt.isPresent()) {
            UI.getCurrent().navigate(TeacherScheduleView.class, searchId);
        } else {
            Notification notification = Notification.show("No faculty member found with ID: " + searchId, 4000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        }
    }
}
