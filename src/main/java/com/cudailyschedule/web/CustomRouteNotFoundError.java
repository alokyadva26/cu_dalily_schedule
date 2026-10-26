package com.cudailyschedule.web;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.ParentLayout;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles 404 Route Not Found errors gracefully.
 * Automatically redirects requests for 'offline-stub.html' to the root LandingView.
 */
@ParentLayout(MainLayout.class)
public class CustomRouteNotFoundError extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    private final Paragraph errorMsg;

    public CustomRouteNotFoundError() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("padding", "60px 20px");

        H1 heading = new H1("404 — Page Not Found");
        heading.getStyle().set("color", "#c8102e").set("margin-bottom", "10px");

        errorMsg = new Paragraph("The requested page does not exist.");
        errorMsg.getStyle().set("color", "#9ca3af").set("font-size", "15px");

        Button homeBtn = new Button("Return to Landing Page", e -> getUI().ifPresent(ui -> ui.navigate("")));
        homeBtn.getStyle()
                .set("background-color", "#c8102e")
                .set("color", "white")
                .set("font-weight", "600")
                .set("border-radius", "8px")
                .set("margin-top", "20px")
                .set("cursor", "pointer");

        add(heading, errorMsg, homeBtn);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        String path = event.getLocation().getPath();
        if ("offline-stub.html".equals(path) || path.endsWith("offline-stub.html")) {
            event.forwardTo("");
            return HttpServletResponse.SC_MOVED_TEMPORARILY;
        }
        errorMsg.setText("The path '" + path + "' could not be found.");
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
