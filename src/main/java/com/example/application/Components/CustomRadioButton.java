package com.example.application.Components;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class CustomRadioButton {

    public Span button;
    private Span choice;

    private String value;
    private boolean partOfRadioGroup;

    private String selectValue;

    public VerticalLayout buttonLayout;

    public HorizontalLayout fullLayout;

    private Span horizontalBlueLine;

    public CustomRadioButton(String value , String selectValue , String buttonClass){
        button = new Span(value);
        choice = new Span(selectValue);
        this.value = value;
        this.selectValue = selectValue;
        button.addClassName(buttonClass);
        button.addClassName("enabledButton");
        partOfRadioGroup = false;

        choice.setWidthFull();
        choice.getStyle().set("margin-bottom" , "auto");
        choice.addClassName("choice");

        buttonLayout = new VerticalLayout(button , choice);

        buttonLayout.addClassName("fullChoiceLayout");

        horizontalBlueLine = new Span("");
        horizontalBlueLine.addClassName("horizontalBlueLine");

        fullLayout = new HorizontalLayout(buttonLayout , horizontalBlueLine);
        fullLayout.addClassName("fullLayout");

        fullLayout.setSpacing(false);
        buttonLayout.addClickListener(spanClickEvent -> {
            if(!partOfRadioGroup){
                select();
            }
        });

        button.addClickListener(spanClickEvent -> {
            if(!partOfRadioGroup){
                select();
            }
        });
    }

    public Span button(){
        return this.button;
    }

    public void unselect(){
        if(button.getElement().getClassList().contains("choiceClicked")){
            button.getElement().getClassList().remove("choiceClicked");
        }
    }

    public void select(){
        if(!button.getElement().getClassList().contains("choiceClicked")){
            button.getElement().getClassList().add("choiceClicked");
        }
    }

    public boolean isSelected(){
        return button.getElement().getClassList().contains("choiceClicked") ? true : false;
    }

    public String getValue(){
        return this.value;
    }

    public VerticalLayout getComponent(){
        return this.buttonLayout;
    }

    public HorizontalLayout getFullLayout(){return this.fullLayout;}

    public HorizontalLayout addCorrectAnswerArrow(){
        HorizontalLayout correctAnswerArrow = new HorizontalLayout();
        correctAnswerArrow.addClassName("correctAnswerArrow");

        Icon arrowIcon = new Icon(VaadinIcon.ARROW_LONG_LEFT);
        arrowIcon.addClassName("arrowIcon");

        Span correctAnswerLabel = new Span("Correct Answer");
        correctAnswerLabel.addClassName("correctAnswerLabel");


        correctAnswerArrow.add(arrowIcon);

        return correctAnswerArrow;
    }


    public void setPartOfRadioGroup(boolean partOfRadioGroup){
        this.partOfRadioGroup = partOfRadioGroup;
    }

    public String getSelectValue(){return selectValue;}

    public void setHorizontalBlueLineVisible(boolean visible){
        this.horizontalBlueLine.setVisible(visible);
    }

    public void setEnabled(boolean enabled){
        if(!enabled) {
            if(button.getClassNames().contains("enabledButton")) button.getClassNames().remove("enabledButton");
            if(!button.getClassNames().contains("disabledButton")) button.addClassName("disabledButton");
        }else{
            if(!button.getClassNames().contains("enabledButton")) button.addClassName("enabledButton");
            if(button.getClassNames().contains("disabledButton")){
                Notification.show("contain disabled");
                button.removeClassName("disabledButton");
            }
        }
    }


}

