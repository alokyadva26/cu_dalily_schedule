package com.cudailyschedule.web;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

/**
 * Kiosk Landing Page recreating the exact Smart Timetable Device portal.
 * Tap NFC card -> Navigates to Student Timetable
 * Scan QR code -> Navigates to Teacher Timetable
 */
@Route("")
@PageTitle("CU Daily Schedule — Smart Timetable Device")
public class LandingView extends VerticalLayout {

    public LandingView() {
        setSizeFull();
        setMargin(false);
        setPadding(false);
        setSpacing(false);
        addClassName("cu-kiosk-bg");
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Explicit inline styles to ensure the campus background is always beautifully rendered
        getStyle()
                .set("background-image", "linear-gradient(rgba(255, 255, 255, 0.68), rgba(255, 255, 255, 0.68)), url('/images/cu-bg.png')")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("background-repeat", "no-repeat")
                .set("background-attachment", "fixed")
                .set("min-height", "100vh")
                .set("height", "100vh")
                .set("width", "100%")
                .set("box-sizing", "border-box")
                .set("padding", "32px 20px 24px 20px");

        // ================= TOP HEADER SECTION =================
        VerticalLayout topSection = new VerticalLayout();
        topSection.setWidthFull();
        topSection.setSpacing(false);
        topSection.setPadding(false);
        topSection.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        Image cuLogo = new Image("/images/cu-logo.png", "Chandigarh University");
        cuLogo.setHeight("64px");
        cuLogo.getStyle().set("cursor", "pointer");
        cuLogo.addClickListener(e -> UI.getCurrent().navigate("student"));

        H1 mainHeading = new H1("DAILY SCHEDULE");
        mainHeading.getStyle()
                .set("color", "#1e293b")
                .set("font-weight", "900")
                .set("font-size", "2.85rem")
                .set("margin", "14px 0 6px 0")
                .set("letter-spacing", "0.5px");

        HorizontalLayout subtitleRow = new HorizontalLayout();
        subtitleRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        subtitleRow.setSpacing(false);

        Div lineLeft = new Div();
        lineLeft.getStyle()
                .set("width", "36px")
                .set("height", "1.5px")
                .set("background-color", "#c8102e");

        Span subtitleText = new Span("SMART TIMETABLE DEVICE");
        subtitleText.getStyle()
                .set("color", "#64748b")
                .set("font-weight", "700")
                .set("font-size", "0.85rem")
                .set("letter-spacing", "2px")
                .set("margin", "0 14px");

        Div lineRight = new Div();
        lineRight.getStyle()
                .set("width", "36px")
                .set("height", "1.5px")
                .set("background-color", "#c8102e");

        subtitleRow.add(lineLeft, subtitleText, lineRight);
        topSection.add(cuLogo, mainHeading, subtitleRow);

        // ================= MIDDLE CARDS SECTION =================
        HorizontalLayout cardsRow = new HorizontalLayout();
        cardsRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        cardsRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        cardsRow.getStyle()
                .set("gap", "48px")
                .set("margin", "auto 0")
                .set("flex-wrap", "wrap");

        // 1. NFC Card (Student Timetable)
        String nfcSvg = "<svg width=\"40\" height=\"40\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#1e293b\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\">"
                + "<rect x=\"2\" y=\"5\" width=\"13\" height=\"14\" rx=\"3\"></rect>"
                + "<rect x=\"5.5\" y=\"9\" width=\"3.5\" height=\"4.5\" rx=\"1\"></rect>"
                + "<path d=\"M18.5 8.5c1.2 1 1.8 2.2 1.8 3.5s-.6 2.5-1.8 3.5\"></path>"
                + "<path d=\"M21.5 6c2 1.8 2.8 3.8 2.8 6s-.8 4.2-2.8 6\"></path>"
                + "</svg>";

        RouterLink nfcCard = createKioskCard(
                nfcSvg,
                "TAP YOUR NFC CARD",
                "to view your timetable",
                "student",
                StudentScheduleView.class
        );

        // 2. Teacher QR Code (Teacher Timetable)
        String qrSvg = "<svg width=\"40\" height=\"40\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#1e293b\" stroke-width=\"2.2\" stroke-linecap=\"round\" stroke-linejoin=\"round\">"
                + "<path d=\"M3 8V5a2 2 0 0 1 2-2h3\"></path>"
                + "<path d=\"M16 3h3a2 2 0 0 1 2 2v3\"></path>"
                + "<path d=\"M21 16v3a2 2 0 0 1-2 2h-3\"></path>"
                + "<path d=\"M8 21H5a2 2 0 0 1-2-2v-3\"></path>"
                + "<rect x=\"7\" y=\"7\" width=\"3.5\" height=\"3.5\" rx=\"0.8\" fill=\"#1e293b\"></rect>"
                + "<rect x=\"13.5\" y=\"7\" width=\"3.5\" height=\"3.5\" rx=\"0.8\" fill=\"#1e293b\"></rect>"
                + "<rect x=\"7\" y=\"13.5\" width=\"3.5\" height=\"3.5\" rx=\"0.8\" fill=\"#1e293b\"></rect>"
                + "<rect x=\"13.5\" y=\"13.5\" width=\"3.5\" height=\"3.5\" rx=\"0.8\" fill=\"#1e293b\"></rect>"
                + "</svg>";

        RouterLink qrCard = createKioskCard(
                qrSvg,
                "SCAN TEACHER QR CODE",
                "to view teacher timetable",
                "teacher",
                TeacherSearchView.class
        );

        cardsRow.add(nfcCard, qrCard);

        // ================= BOTTOM FOOTER SECTION =================
        VerticalLayout bottomSection = new VerticalLayout();
        bottomSection.setWidthFull();
        bottomSection.setSpacing(false);
        bottomSection.setPadding(false);
        bottomSection.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        Span deviceReadyTitle = new Span("DEVICE READY");
        deviceReadyTitle.getStyle()
                .set("color", "#0f172a")
                .set("font-weight", "800")
                .set("font-size", "1.0rem")
                .set("letter-spacing", "1.5px")
                .set("margin-bottom", "4px");

        Span deviceReadySubtitle = new Span("Please tap your NFC card or scan QR code");
        deviceReadySubtitle.getStyle()
                .set("color", "#64748b")
                .set("font-size", "0.92rem")
                .set("font-weight", "500")
                .set("margin-bottom", "10px");

        Div redBar = new Div();
        redBar.getStyle()
                .set("width", "36px")
                .set("height", "3.5px")
                .set("background-color", "#c8102e")
                .set("border-radius", "2px");

        bottomSection.add(deviceReadyTitle, deviceReadySubtitle, redBar);

        // Add top, middle, bottom to view
        add(topSection, cardsRow, bottomSection);
    }

    private RouterLink createKioskCard(String svgIcon, String titleText, String subtitleText, String targetRoute, Class<? extends com.vaadin.flow.component.Component> targetClass) {
        RouterLink card = new RouterLink();
        card.setRoute(targetClass);
        card.addClassName("cu-kiosk-card");
        card.getStyle()
                .set("width", "360px")
                .set("height", "370px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("text-align", "center")
                .set("padding", "36px 24px")
                .set("box-sizing", "border-box")
                .set("background", "#ffffff")
                .set("border-radius", "36px")
                .set("border", "1px solid rgba(226, 232, 240, 0.8)")
                .set("box-shadow", "0 16px 40px rgba(0, 0, 0, 0.06), 0 2px 8px rgba(0, 0, 0, 0.02)")
                .set("text-decoration", "none")
                .set("color", "inherit")
                .set("cursor", "pointer")
                .set("transition", "transform 0.25s cubic-bezier(0.16, 1, 0.3, 1), box-shadow 0.25s cubic-bezier(0.16, 1, 0.3, 1)");

        // Circular Icon Outline
        Div iconCircle = new Div();
        iconCircle.getStyle()
                .set("width", "84px")
                .set("height", "84px")
                .set("border-radius", "50%")
                .set("border", "1.5px solid #e2e8f0")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("margin-bottom", "18px");

        // Direct innerHTML injection for guaranteed SVG rendering
        Div iconSvgWrapper = new Div();
        iconSvgWrapper.getElement().setProperty("innerHTML", svgIcon);
        iconSvgWrapper.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        iconCircle.add(iconSvgWrapper);

        // Small Red Accent Dash
        Div redDash = new Div();
        redDash.getStyle()
                .set("width", "30px")
                .set("height", "2px")
                .set("background-color", "#c8102e")
                .set("border-radius", "1px")
                .set("margin-bottom", "18px");

        // Title
        H2 title = new H2(titleText);
        title.getStyle()
                .set("color", "#1e293b")
                .set("font-size", "1.25rem")
                .set("font-weight", "800")
                .set("margin", "0 0 8px 0")
                .set("letter-spacing", "0.3px");

        // Subtitle
        Paragraph subtitle = new Paragraph(subtitleText);
        subtitle.getStyle()
                .set("color", "#64748b")
                .set("font-size", "0.95rem")
                .set("font-weight", "500")
                .set("margin", "0");

        card.add(iconCircle, redDash, title, subtitle);

        // Also add element click listener for instant navigation fallback
        card.getElement().addEventListener("click", e -> UI.getCurrent().navigate(targetRoute));

        return card;
    }
}
