package com.example.application.Components;

import com.example.application.Entities.Event.Event;
import com.vaadin.flow.component.grid.Grid;
import org.springframework.web.util.HtmlUtils;

public class eventsGrid extends Grid<Event> {
    public eventsGrid(){
        setSizeFull();

        Column<Event> column = addColumn(event -> twoRowCell(event.getStartingDate() , event.getEndingDate()));

        Column<Event> eventColumn = addColumn(event -> twoRowCell(event.getEventName() , event.getDescription()));

    }

    private static String twoRowCell(String header, String content) {
        return "<div class=\"header\">" + HtmlUtils.htmlEscape(header) + "</div><div class=\"content\">"
                + HtmlUtils.htmlEscape(content) + "</div>";
    }


}
