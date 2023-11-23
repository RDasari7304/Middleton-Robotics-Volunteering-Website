package com.example.application.views.LoginViews;

import com.example.application.Resources.Services.Database;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("forgot_password")
@CssImport("styles/views/authenticationViews/forgotPassPage.css")
public class ForgotPasswordView extends VerticalLayout {

    public ForgotPasswordView(){

        setAlignItems(Alignment.CENTER);
        setSizeFull();

        Database.connect();
        init();

        addClassName("forgot_password");
        setJustifyContentMode(JustifyContentMode.CENTER);

    }


    public void init(){
        Label formLabel = new Label("Please enter the following info to receive your password : ");
        Label Username = new Label("Username :");
        Label Email = new Label("Email Address:");

        TextField usernameField = new TextField();
        usernameField.setPlaceholder("Enter Username");

        TextField emailAdressField = new TextField();
        emailAdressField.setPlaceholder("Enter Email Address");


        HorizontalLayout UsernameLayout = new HorizontalLayout(Username , usernameField);
        HorizontalLayout EmailLayout = new HorizontalLayout(Email , emailAdressField);

        UsernameLayout.setAlignItems(Alignment.CENTER);
        EmailLayout.setAlignItems(Alignment.CENTER);


        VerticalLayout allItems = new VerticalLayout(formLabel ,UsernameLayout , EmailLayout);
        allItems.setAlignItems(Alignment.CENTER);

        Button returnToLogin = new Button("Back To Login");
        Button submit = new Button("Submit");


        submit.addClickListener(event -> {
           boolean isThere = Database.dbHasUsernameAndEmail(usernameField.getValue() , emailAdressField.getValue());

           if(isThere){
                Database.sendPasswordEmail(usernameField.getValue() , emailAdressField.getValue());
                Notification.show("The password to user, " + usernameField.getValue() + ", has been sent to your email!");
                UI.getCurrent().navigate("");

           }else if(!isThere){
               Notification.show("The specified username is not associated with the email, check the info and please try again");
           }

        });

        returnToLogin.addClickListener(event -> {
           UI.getCurrent().navigate("");
        });

        returnToLogin.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttons = new HorizontalLayout(submit , returnToLogin);
        buttons.setAlignItems(Alignment.CENTER);


        allItems.add(buttons);


        add(allItems);
    }


}
