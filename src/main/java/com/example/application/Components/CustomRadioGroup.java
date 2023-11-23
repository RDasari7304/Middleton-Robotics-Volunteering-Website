package com.example.application.Components;


import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;

import java.util.ArrayList;

public class CustomRadioGroup {

    ArrayList<CustomRadioButton> radioButtons;
    String valueSelected = "";
    VerticalLayout buttonGroup;
    HorizontalLayout hButtongroup;

    public RadioButtonGroup<String> radioButtonGroup;

    CustomRadioButton buttonChosen;

    private boolean chooserMode = true;

    public CustomRadioGroup(){

    }

    public CustomRadioGroup(ArrayList<CustomRadioButton> radioButtons ){
        this.radioButtonGroup = new RadioButtonGroup<>();
        this.radioButtons = radioButtons;
        buttonGroup = new VerticalLayout();
        hButtongroup = new HorizontalLayout();

        buttonChosen = new CustomRadioButton("" , "" , "n");

        for(CustomRadioButton crb : radioButtons){
            radioButtonGroup.add(crb.getSelectValue());
        }

        for(CustomRadioButton radioButton : radioButtons){
            radioButton.setPartOfRadioGroup(true);

            radioButton.button.addClickListener(spanClickEvent -> {
                if(chooserMode) {
                    if (!radioButton.getValue().equals(valueSelected) && !(radioButton.button.getClassNames().contains("disabledButton"))) {
                        for (CustomRadioButton radioButton1 : radioButtons) {
                            radioButton1.unselect();
                        }

                        radioButton.select();
                        this.valueSelected = radioButton.getSelectValue();
                        this.buttonChosen = radioButton;
                        radioButtonGroup.setValue(radioButton.getSelectValue());
                    }
                }
            });

            radioButton.getComponent().addClickListener(horizontalLayoutClickEvent -> {
                if(chooserMode){
                if(!radioButton.getValue().equals(valueSelected) && !(radioButton.button.getClassNames().contains("disabledButton"))) {
                    for (CustomRadioButton radioButton1 : radioButtons) {
                        radioButton1.unselect();
                    }

                    radioButton.select();
                    this.valueSelected = radioButton.getSelectValue();
                    this.buttonChosen = radioButton;
                    radioButtonGroup.setValue(radioButton.getSelectValue());
                }
                }
            });

        }


            for(CustomRadioButton radioButton : radioButtons){
                HorizontalLayout button = radioButton.getFullLayout();
                button.setMargin(false);
                button.setWidthFull();

                if(radioButton.equals(radioButtons.get(radioButtons.size() - 1))){
                    radioButton.setHorizontalBlueLineVisible(false);
                }


                hButtongroup.add(button);
            }

            hButtongroup.addClassName("hButtongroup");

        buttonGroup.setSpacing(false);


    }

    public VerticalLayout getVerticalButtonGroup(){
        return buttonGroup;
    }

    public ArrayList<CustomRadioButton> getRadioButtons(){
        return radioButtons;
    }

    public HorizontalLayout getHorizontalButtongroup(){
        return hButtongroup;
    }

    public boolean isChoiceChosen(){
        for(CustomRadioButton customRadioButton : radioButtons){
            if(customRadioButton.isSelected()) return true;
        }

        return false;
    }

    public void turnOffChooser(){
        chooserMode = false;
    }

    public String getValueSelected(){
        return valueSelected;
    }

    public CustomRadioButton getButtonChosen(){
        return buttonChosen;
    }

    public void setButtonChosen(CustomRadioButton crb){
        this.buttonChosen = crb;
    }

    public ArrayList<CustomRadioButton> getButtonsNotChosen(){
        ArrayList<CustomRadioButton> buttonsNotChosen = new ArrayList<>();
        for(CustomRadioButton radioButton : radioButtons){
            if(radioButton != buttonChosen){
                buttonsNotChosen.add(radioButton);
            }
        }

        return buttonsNotChosen;
    }

    public void setValueSelected(String valueSelected){
        this.valueSelected = valueSelected;
    }

    public void selectButton(CustomRadioButton customRadioButton){
        for(CustomRadioButton crb : radioButtons){
            if(crb.equals(customRadioButton)){
                if (!crb.getValue().equals(valueSelected)) {
                    for (CustomRadioButton radioButton1 : radioButtons) {
                        radioButton1.unselect();
                    }

                    customRadioButton.select();
                    this.valueSelected = customRadioButton.getSelectValue();
                    this.buttonChosen = customRadioButton;
                    radioButtonGroup.setValue(customRadioButton.getSelectValue());
                }
            }
        }
    }

    public void disableButtons(ArrayList<CustomRadioButton> buttons){
        for(CustomRadioButton crb : buttons){
            for(CustomRadioButton crb2 : radioButtons){
                if(crb.equals(crb2)){
                    crb2.setEnabled(false);
                }
            }
        }
    }

    public void enableButton(CustomRadioButton crb){
        for(CustomRadioButton customRadioButton : radioButtons){
            if(crb.equals(customRadioButton)){
                customRadioButton.setEnabled(true);
            }
        }
    }



}


