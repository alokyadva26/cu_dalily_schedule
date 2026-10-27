package com.cudailyschedule.web;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Reusable floating Settings button anchored to the bottom-right viewport.
 * Provides consistent access to database management & diagnostics across pages.
 */
public class FloatingSettingsButton extends Button {

    public FloatingSettingsButton() {
        Icon gearIcon = VaadinIcon.COG.create();
        gearIcon.setColor("#E30613");
        gearIcon.setSize("24px");
        setIcon(gearIcon);

        getElement().setAttribute("title", "Settings");
        getElement().setAttribute("aria-label", "Settings");
        addClassName("cu-floating-settings-btn");

        getStyle()
                .set("position", "fixed")
                .set("bottom", "32px")
                .set("right", "32px")
                .set("width", "58px")
                .set("height", "58px")
                .set("min-width", "58px")
                .set("border-radius", "50%")
                .set("background-color", "#FFFFFF")
                .set("border", "1px solid #E5E7EB")
                .set("box-shadow", "0 4px 14px rgba(0, 0, 0, 0.10), 0 2px 6px rgba(0, 0, 0, 0.06)")
                .set("cursor", "pointer")
                .set("z-index", "9999")
                .set("padding", "0")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("transition", "all 0.2s ease");

        addClickListener(e -> new AdminScheduleDialog().open());
    }
}
