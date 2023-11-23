package com.example.application.Components;

import com.vaadin.flow.component.html.Span;

public class HDivider extends Span {

    public HDivider(){
        getStyle().set("background-color", "gray");
        getStyle().set("flex", "0 0 1px");
        getStyle().set("align-self", "stretch");
    }

}
