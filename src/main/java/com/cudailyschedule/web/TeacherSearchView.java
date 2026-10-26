package com.cudailyschedule.web;

import com.cudailyschedule.model.Teacher;
import com.cudailyschedule.service.TeacherService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Optional;

/**
 * TeacherSearchView provides teacher employee ID search and simulated QR badge scanner in Chrome.
 */
@Route(value = "teacher", layout = MainLayout.class)
@PageTitle("Teacher Mode — CU Daily Schedule")
public class TeacherSearchView extends VerticalLayout {

    private final TeacherService teacherService;
    private final TextField idField;

    public TeacherSearchView(TeacherService teacherService) {
        this.teacherService = teacherService;

        setSizeFull();
        setSpacing(false);
        setPadding(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        addClassName("cu-watermark-container");

        // Top Navigation Bar
        HorizontalLayout navBar = new HorizontalLayout();
        navBar.setWidthFull();
        navBar.setPadding(true);
        Button backBtn = new Button("← Back to Home", e -> UI.getCurrent().navigate(""));
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backBtn.getStyle()
                .set("color", "var(--cu-navy)")
                .set("font-weight", "700")
                .set("background-color", "#FFFFFF")
                .set("border", "1px solid var(--cu-border)")
                .set("border-radius", "12px");
        navBar.add(backBtn);
        add(navBar);

        // Center Card Container
        Div card = new Div();
        card.addClassName("cu-card");
        card.getStyle()
                .set("width", "440px")
                .set("max-width", "90vw")
                .set("padding", "36px 32px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("text-align", "center")
                .set("margin-top", "20px");

        // Icon Header
        Div iconCircle = new Div();
        iconCircle.getStyle()
                .set("width", "64px")
                .set("height", "64px")
                .set("border-radius", "50%")
                .set("background-color", "var(--cu-bg)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("margin-bottom", "16px");

        Icon userIcon = VaadinIcon.ACADEMY_CAP.create();
        userIcon.setSize("28px");
        userIcon.setColor("var(--cu-navy)");
        iconCircle.add(userIcon);

        H1 title = new H1("TEACHER SCHEDULE");
        title.getStyle()
                .set("font-size", "1.6rem")
                .set("font-weight", "900")
                .set("color", "var(--cu-navy)")
                .set("margin", "0 0 6px 0");

        Paragraph subtitle = new Paragraph("Search faculty schedule by Employee ID or simulate QR badge scanning");
        subtitle.getStyle()
                .set("color", "var(--cu-text-muted)")
                .set("font-size", "0.9rem")
                .set("margin", "0 0 24px 0");

        // Input Field
        idField = new TextField("Employee ID");
        idField.setPlaceholder("e.g. E19761");
        idField.setClearButtonVisible(true);
        idField.setWidthFull();
        idField.setPrefixComponent(VaadinIcon.USER.create());
        idField.getStyle().set("margin-bottom", "16px");

        // Search Button
        Button searchBtn = new Button("Search Faculty Schedule", VaadinIcon.SEARCH.create());
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchBtn.setWidthFull();
        searchBtn.getStyle()
                .set("background-color", "var(--cu-red)")
                .set("border-radius", "12px")
                .set("padding", "12px")
                .set("font-weight", "700")
                .set("cursor", "pointer")
                .set("margin-bottom", "16px");
        searchBtn.addClickListener(e -> performSearch(idField.getValue()));

        // QR Badge Scanner Simulator Button
        Button qrScanBtn = new Button("📷 Simulate QR Badge Scan (E19761)");
        qrScanBtn.setWidthFull();
        qrScanBtn.getStyle()
                .set("background-color", "#F1F5F9")
                .set("color", "var(--cu-navy)")
                .set("border", "1px dashed var(--cu-border)")
                .set("border-radius", "12px")
                .set("font-weight", "700")
                .set("cursor", "pointer")
                .set("margin-bottom", "24px");
        qrScanBtn.addClickListener(e -> {
            idField.setValue("E19761");
            performSearch("E19761");
        });

        // Sample Quick Pick Row
        Span quickPickLabel = new Span("Sample Faculty IDs:");
        quickPickLabel.getStyle().set("font-size", "0.8rem").set("color", "var(--cu-text-muted)").set("font-weight", "600");

        HorizontalLayout samplePills = new HorizontalLayout();
        samplePills.setSpacing(true);
        samplePills.getStyle().set("margin-top", "8px");

        Button sample1 = createSamplePill("E19761 (Dr. Hardeep)", "E19761");
        Button sample2 = createSamplePill("E16525 (Er. Paramjit)", "E16525");
        samplePills.add(sample1, sample2);

        card.add(iconCircle, title, subtitle, idField, searchBtn, qrScanBtn, quickPickLabel, samplePills);
        add(card);
    }

    private Button createSamplePill(String label, String id) {
        Button btn = new Button(label);
        btn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        btn.getStyle()
                .set("background-color", "var(--cu-bg)")
                .set("color", "var(--cu-navy)")
                .set("border", "1px solid var(--cu-border)")
                .set("border-radius", "8px")
                .set("font-size", "0.75rem")
                .set("cursor", "pointer");
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
