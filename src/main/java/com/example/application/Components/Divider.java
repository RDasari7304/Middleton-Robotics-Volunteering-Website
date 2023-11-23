package com.example.application.Components;

import com.vaadin.flow.component.html.Span;

public class Divider extends Span {

    public Divider(){
        getStyle().set("background-color", "gray");
        getStyle().set("flex", "1px 0 0");
        getStyle().set("align-self", "stretch");
    }


}
