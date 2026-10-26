package com.cudailyschedule.web;

import com.cudailyschedule.service.DatabaseService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

/**
 * MainLayout provides the global navigation bar, brand header, live DB status badge,
 * and responsive container for all Vaadin views.
 */
public class MainLayout extends AppLayout {

    private final Span statusBadge;

    public MainLayout() {
        setPrimarySection(Section.NAVBAR);

        // Drawer toggle for mobile screens
        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle().set("color", "var(--cu-navy)");

        // Brand logo and title
        HorizontalLayout brand = new HorizontalLayout();
        brand.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        brand.setSpacing(true);

        Image logo = new Image("/images/cu-logo.png", "Chandigarh University");
        logo.setHeight("36px");
        logo.getStyle().set("cursor", "pointer");
        logo.addClickListener(e -> UI.getCurrent().navigate(""));

        Span brandTitle = new Span("CU Daily Schedule");
        brandTitle.getStyle()
                .set("font-weight", "700")
                .set("font-size", "1.15rem")
                .set("color", "var(--cu-navy)")
                .set("cursor", "pointer");
        brandTitle.addClickListener(e -> UI.getCurrent().navigate(""));

        brand.add(logo, brandTitle);

        // Desktop Navigation Links
        HorizontalLayout navLinks = new HorizontalLayout();
        navLinks.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        navLinks.getStyle().set("margin-left", "20px");
        navLinks.addClassName("desktop-nav");

        Button homeBtn = createNavButton("🏠 Home", "");
        Button studentBtn = createNavButton("👨‍🎓 Student Timetable", "student");
        Button teacherBtn = createNavButton("👨‍🏫 Teacher Mode", "teacher");

        // Admin CRUD Button
        Button adminBtn = new Button("⚙️ DB CRUD Admin", VaadinIcon.COG_O.create());
        adminBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        adminBtn.getStyle()
                .set("background-color", "#F1F5F9")
                .set("color", "var(--cu-navy)")
                .set("font-weight", "600")
                .set("border-radius", "8px");
        adminBtn.addClickListener(e -> new AdminScheduleDialog().open());

        navLinks.add(homeBtn, studentBtn, teacherBtn, adminBtn);

        // Database status pill
        statusBadge = new Span();
        updateStatusBadge();
        statusBadge.getStyle().set("cursor", "pointer");
        statusBadge.getElement().setAttribute("title", "Click to view database diagnostics");
        statusBadge.addClickListener(e -> new AdminScheduleDialog().open());

        // Header container
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.addClassName("cu-navbar");

        HorizontalLayout left = new HorizontalLayout(toggle, brand, navLinks);
        left.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        left.setSpacing(true);

        header.add(left);
        header.add(statusBadge);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        addToNavbar(header);

        // Mobile Drawer navigation
        createMobileDrawer();
    }

    private Button createNavButton(String text, String route) {
        Button btn = new Button(text);
        btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btn.getStyle()
                .set("color", "var(--cu-navy)")
                .set("font-weight", "600")
                .set("font-size", "0.95rem");
        btn.addClickListener(e -> UI.getCurrent().navigate(route));
        return btn;
    }

    private void createMobileDrawer() {
        VerticalLayout drawerLayout = new VerticalLayout();
        drawerLayout.setSpacing(true);
        drawerLayout.setPadding(true);

        H1 drawerTitle = new H1("Navigation");
        drawerTitle.getStyle().set("font-size", "1.2rem").set("color", "var(--cu-navy)");

        RouterLink homeLink = new RouterLink("🏠 Home", LandingView.class);
        RouterLink studentLink = new RouterLink("👨‍🎓 Student Timetable", StudentScheduleView.class);
        RouterLink teacherLink = new RouterLink("👨‍🏫 Teacher Mode", TeacherSearchView.class);

        Button adminDrawerBtn = new Button("⚙️ Database Admin", e -> new AdminScheduleDialog().open());
        adminDrawerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        adminDrawerBtn.setWidthFull();

        drawerLayout.add(drawerTitle, homeLink, studentLink, teacherLink, adminDrawerBtn);
        addToDrawer(drawerLayout);
    }

    private void updateStatusBadge() {
        boolean connected = DatabaseService.isConnected();
        if (connected) {
            statusBadge.setText("● Online (PostgreSQL)");
            statusBadge.setClassName("cu-badge-online");
        } else {
            statusBadge.setText("○ Offline (Local Seed)");
            statusBadge.setClassName("cu-badge-offline");
        }
    }
}
