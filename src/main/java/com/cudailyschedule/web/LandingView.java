package com.cudailyschedule.web;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * LandingView recreates the smart kiosk landing hub with Student NFC and Teacher QR cards.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("CU Daily Schedule — Smart Timetable Hub")
public class LandingView extends VerticalLayout {

    public LandingView() {
        setSizeFull();
        setSpacing(false);
        setPadding(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        addClassName("cu-watermark-container");

        // Main content wrapper
        VerticalLayout content = new VerticalLayout();
        content.setMaxWidth("1000px");
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        content.getStyle().set("padding", "40px 20px");

        // Hero Logo
        Image cuLogo = new Image("/images/cu-logo.png", "Chandigarh University");
        cuLogo.setHeight("64px");
        cuLogo.getStyle().set("margin-bottom", "12px");

        // Title
        H1 mainTitle = new H1("DAILY SCHEDULE");
        mainTitle.getStyle()
                .set("color", "var(--cu-navy)")
                .set("font-weight", "900")
                .set("font-size", "2.8rem")
                .set("margin", "0 0 6px 0")
                .set("letter-spacing", "-0.5px");

        // Subtitle row with red accent line rules
        HorizontalLayout subtitleRow = new HorizontalLayout();
        subtitleRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        subtitleRow.setSpacing(true);

        Div lineLeft = createRuleLine();
        Span subtitle = new Span("SMART TIMETABLE DEVICE");
        subtitle.getStyle()
                .set("color", "var(--cu-text-muted)")
                .set("font-weight", "700")
                .set("font-size", "0.95rem")
                .set("letter-spacing", "2px");
        Div lineRight = createRuleLine();

        subtitleRow.add(lineLeft, subtitle, lineRight);

        // Action Cards Grid
        HorizontalLayout cardsGrid = new HorizontalLayout();
        cardsGrid.setWidthFull();
        cardsGrid.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        cardsGrid.getStyle()
                .set("margin-top", "44px")
                .set("flex-wrap", "wrap")
                .set("gap", "28px");

        // Card 1: Student NFC Card
        Div studentCard = createActionCard(
                VaadinIcon.CREDIT_CARD,
                "STUDENT SMART CARD",
                "Tap your student NFC card or click below to view real-time class schedule, ongoing lectures, and daily timetable.",
                "Tap Smart Card →",
                "student",
                "var(--cu-red)"
        );

        // Card 2: Teacher QR Card
        Div teacherCard = createActionCard(
                VaadinIcon.QRCODE,
                "TEACHER QR SCAN",
                "Scan teacher QR badge or search by employee ID to inspect faculty lecture schedules, room assignments, and availability.",
                "Scan Teacher QR Code →",
                "teacher",
                "var(--cu-navy)"
        );

        cardsGrid.add(studentCard, teacherCard);

        // Kiosk footer caption
        Paragraph footerNote = new Paragraph("Chandigarh University • Smart Campus Daily Schedule System");
        footerNote.getStyle()
                .set("color", "var(--cu-text-muted)")
                .set("font-size", "0.85rem")
                .set("margin-top", "50px");

        content.add(cuLogo, mainTitle, subtitleRow, cardsGrid, footerNote);
        add(content);
    }

    private Div createRuleLine() {
        Div line = new Div();
        line.setWidth("48px");
        line.setHeight("2px");
        line.getStyle().set("background-color", "var(--cu-red)");
        return line;
    }

    private Div createActionCard(VaadinIcon iconType, String title, String description, String buttonText, String targetRoute, String accentColor) {
        Div card = new Div();
        card.addClassName("cu-card");
        card.addClassName("cu-card-interactive");
        card.getStyle()
                .set("width", "360px")
                .set("min-width", "300px")
                .set("padding", "36px 28px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("text-align", "center")
                .set("border-top", "4px solid " + accentColor);

        // Icon circle
        Div iconCircle = new Div();
        iconCircle.getStyle()
                .set("width", "72px")
                .set("height", "72px")
                .set("border-radius", "50%")
                .set("background-color", "var(--cu-bg)")
                .set("border", "1px solid var(--cu-border)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("margin-bottom", "20px");

        Icon icon = iconType.create();
        icon.setSize("32px");
        icon.setColor(accentColor);
        iconCircle.add(icon);

        H2 cardTitle = new H2(title);
        cardTitle.getStyle()
                .set("color", "var(--cu-navy)")
                .set("font-size", "1.3rem")
                .set("font-weight", "800")
                .set("margin", "0 0 10px 0");

        Paragraph cardDesc = new Paragraph(description);
        cardDesc.getStyle()
                .set("color", "var(--cu-text-muted)")
                .set("font-size", "0.9rem")
                .set("line-height", "1.5")
                .set("margin", "0 0 24px 0");

        Button actionBtn = new Button(buttonText);
        actionBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        actionBtn.getStyle()
                .set("background-color", accentColor)
                .set("border-radius", "12px")
                .set("padding", "12px 24px")
                .set("font-weight", "700")
                .set("cursor", "pointer")
                .set("width", "100%");
        actionBtn.addClickListener(e -> UI.getCurrent().navigate(targetRoute));

        // Click whole card to navigate
        card.addClickListener(e -> UI.getCurrent().navigate(targetRoute));

        card.add(iconCircle, cardTitle, cardDesc, actionBtn);
        return card;
    }
}
