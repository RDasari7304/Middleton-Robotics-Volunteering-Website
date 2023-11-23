package com.example.application.views.LoginViews;

import com.example.application.Entities.User.User;
import com.example.application.Resources.Services.DataService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("Register")

@CssImport("styles/views/authenticationViews/Register-page.css")
public class RegisterPage extends VerticalLayout {

    TextField userName;
    PasswordField password;
    boolean first = false;
    boolean filled = true;

    @Autowired
    DataService userService;


    public RegisterPage(){
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        addClassName("RegPage");


        setSizeFull();
        setHeightFull();
        setWidthFull();

        userName = new TextField("Username: ");
        password = new PasswordField("Password: ");



        ComboBox<String> roles = new ComboBox<>();
        roles.setLabel("Role:");
        roles.setItems("Student","Adult");
        roles.setClearButtonVisible(true);

        roles.addClassName("fieldLayout");


        TextField firstName = new TextField("First Name: ");
        TextField lastName = new TextField("Last Name: ");
        HorizontalLayout credentials = new HorizontalLayout(userName, password);
        credentials.addClassName("fieldLayout");
        HorizontalLayout personalInfo = new HorizontalLayout(
                firstName, lastName
        );
        personalInfo.addClassName("fieldLayout");
        ComboBox<String> teams = new ComboBox<>("Team: ");
        teams.setItems("Maelstrom", "Masquerade", "Mercury", "Minerva", "Minotaur");
        teams.setClearButtonVisible(true);
        EmailField Email = new EmailField("Email Address: ");
        HorizontalLayout schoolInfo = new HorizontalLayout(
                Email, teams
        );

        schoolInfo.addClassName("fieldLayout");


        userName.setId("username");
        firstName.setId("firstName");
        lastName.setId("last");
        password.setId("pass");
        teams.setId("Teams");
        Email.setId("email");
        roles.setId("role");

        Button register = new Button("Register Account");
        register.getStyle().set("margin-left" , "auto");


        register.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        register.addClickListener(e -> {
                    Paragraph output = new Paragraph("");

            if(userName.isEmpty()) {
                register.addThemeVariants(ButtonVariant.LUMO_ERROR);
                userName.setInvalid(true);
                UI.getCurrent().getPage().executeJs("document.getElementById(\"username\").focus();");
                first = true;
                Notification.show("Please Enter a Username");
                filled = false;
            }
            if (password.isEmpty()){
                register.addThemeVariants(ButtonVariant.LUMO_ERROR);
                password.setInvalid(true);
                if(first != true){
                    first = true;
                    UI.getCurrent().getPage().executeJs("document.getElementById(\"pass\").focus();");

                }
                Notification.show("Please Enter a Password");
                filled = false;
            }
            if(teams.isEmpty()){
                register.addThemeVariants(ButtonVariant.LUMO_ERROR);
                teams.setInvalid(true);
                if(first != true){
                    first = true;
                    UI.getCurrent().getPage().executeJs("document.getElementById(\"Teams\").focus();");

                }
                Notification.show("Please pick a team");
                filled = false;
            }
            if(Email.isEmpty()){
                register.addThemeVariants(ButtonVariant.LUMO_ERROR);
                Email.setInvalid(true);
                if(first != true){
                    first = true;
                    UI.getCurrent().getPage().executeJs("document.getElementById(\"grade\").focus();");

                }
                Notification.show("Please Enter your grade level");
                filled = false;
            }
            if(firstName.isEmpty()){
                register.addThemeVariants(ButtonVariant.LUMO_ERROR);
                firstName.setInvalid(true);
                if(first != true){
                    first = true;
                    UI.getCurrent().getPage().executeJs("document.getElementById(\"firstName\").focus();");

                }
                Notification.show("Please Enter your first name");
                filled = false;

            }
            if(lastName.isEmpty()){
                register.addThemeVariants(ButtonVariant.LUMO_ERROR);
                lastName.setInvalid(true);
                if(first != true){
                    first = true;
                    UI.getCurrent().getPage().executeJs("document.getElementById(\"last\").focus();");

                }
                Notification.show("Please Enter your last name");
                filled = false;
            }
            if(roles.isEmpty()){
                register.addThemeVariants(ButtonVariant.LUMO_ERROR);
                roles.setInvalid(true);
                if(first != true){
                    first = true;
                    UI.getCurrent().getPage().executeJs("document.getElementById(\"last\").focus();");

                }
                Notification.show("Please choose a role");
                filled = false;
            }


            /*if(filled && Database.hasSameUserName(userName.getValue())){
            }
            else if(filled && !Database.hasSameUserName(userName.getValue())){
                userName.setInvalid(false);
                password.setInvalid(false);
                register.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                Notification.show("Account created, wait for approval from Admin");

                Database.addUser(firstName.getValue() , lastName.getValue() , userName.getValue() , password.getValue()
                                                    , Email.getValue() , teams.getValue() , roles.getValue() , true);

                getUI().ifPresent(ui -> ui.navigate(""));

            }*/

            if(filled && !userService.isUserNameAvailable(userName.getValue())){
            }
            else if(filled && userService.isUserNameAvailable(userName.getValue())){
                userName.setInvalid(false);
                password.setInvalid(false);
                register.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                Notification.show("Account created, wait for approval from Admin");

                userService.registerUser(new User(firstName.getValue() , lastName.getValue() , userName.getValue() , password.getValue() , teams.getValue() , Email.getValue() , roles.getValue() , 0 , 0 , 0));
/*
                Database.addUser(firstName.getValue() , lastName.getValue() , userName.getValue() , password.getValue()
                        , Email.getValue() , teams.getValue() , roles.getValue() , true);*/

                getUI().ifPresent(ui -> ui.navigate(""));

            }


            filled = true;

        });

        register.addClickShortcut(Key.ENTER);
        addClassName("centered-content");

        H3 Header = new H3("Register Account");
        Header.addClassName("RegisterHeader");


        Button back = new Button("Back To Login", e->
        {
            getUI().ifPresent(ui -> ui.navigate("Login"));
        });

        back.getStyle().set("margin-right" , "auto");

        back.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        back.addClickShortcut(Key.ESCAPE);

        HorizontalLayout navigatorLayouts = new HorizontalLayout(
          register, back
        );

        navigatorLayouts.addClassName("navigationLayouts");

        userName.addThemeName("boldlabel");
        password.addThemeName("boldlabel");
        firstName.addThemeName("boldlabel");
        lastName.addThemeName("boldlabel");
        Email.addThemeName("boldlabel");

        VerticalLayout fullRegisterLayout = new VerticalLayout(Header, credentials, personalInfo,
                schoolInfo, roles, navigatorLayouts);
        fullRegisterLayout.addClassName("fullRegisterLayout");

        add(fullRegisterLayout);
    }




    }




