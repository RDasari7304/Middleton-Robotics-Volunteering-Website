package com.example.application.views.LoginViews;

import com.example.application.Entities.User.User;
import com.example.application.Resources.Services.DataService;
import com.example.application.Resources.Services.Database;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.xml.crypto.Data;
import java.util.Collections;

@Route("login")
@RouteAlias("")
@PageTitle("Middleton Robotics | Login")
@CssImport("styles/views/authenticationViews/login.css")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    public LoginForm login = new LoginForm();
    public LoginI18n loginChange = LoginI18n.createDefault();
    Anchor signUp = new Anchor();
    VerticalLayout registerLayout = new VerticalLayout(new TextField("Hello"));


    TextField userName;
    PasswordField password;
    boolean first = false;
    boolean filled = true;

    private DataService dataService;

    public LoginView(){//regiterPanel.setEnabled(false);
         setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        addClassName("login");

        configureLoginForm();
    }

    public void configureLoginForm(){
        registerLayout.addComponentAsFirst(new TextField("Hello"));

        loginChange.getForm().setTitle("       Login");

        login.setAction("login");

        signUp.getElement().addEventListener("click", e ->{
            UI.getCurrent().navigate("Register");
            //openRegisterWindow();
        });


        login.addForgotPasswordListener(event -> {
            UI.getCurrent().navigate("forgot_password");
        });

        UI.getCurrent().getPage().executeJs("document.getElementById(\"vaadinLoginUsername\").focus();");

        signUp.setText("Don't have an Account? Sign up here!");
        signUp.getStyle().set("cursor" , "pointer");

        login.setI18n(loginChange);

        loginChange.setAdditionalInformation("");

        H2 loginLabel = new H2("Middleton Robotics Outreach");
        loginLabel.addClassName("middletonLabel");

        signUp.addClassName("signUpLabel");

        add(
                loginLabel,
                login,
                signUp
        );
    }

    private void openRegisterWindow(){

            Database.connect();
            Dialog testDialog = new Dialog();

            TextField userName = new TextField("Username: ");
            PasswordField password = new PasswordField("Password: ");

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


                if(filled && Database.hasSameUserName(userName.getValue())){
                }
                else if(filled && !Database.hasSameUserName(userName.getValue())){
                    userName.setInvalid(false);
                    password.setInvalid(false);
                    register.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                    Notification.show("Account created, wait for approval from Admin");

                    Database.addUserToRegister(new User(firstName.getValue() , lastName.getValue() , userName.getValue() , password.getValue()
                            , Email.getValue() , teams.getValue() , roles.getValue() , 0 , 0 , 0));

                    first = false;
                    filled = true;

                }

                filled = true;

            });

            register.addClickShortcut(Key.ENTER);
            addClassName("centered-content");

            H3 Header = new H3("Register Page");
            Header.addClassName("RegisterHeader");




            Button back = new Button("Cancel", e->
            {
                first = false;
                filled = true;
                testDialog.close();
            });


            back.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            back.addClickShortcut(Key.ESCAPE);

            HorizontalLayout navigatorLayouts = new HorizontalLayout(
                    back ,register
            );

            back.getStyle().set("margin-left" , "auto");
            register.getStyle().set("margin-right" , "auto");

            navigatorLayouts.addClassName("navigatorLayout");


            testDialog.setWidth("47.5");
            testDialog.setHeight("62vh");

            testDialog.add(Header, credentials, personalInfo,
                    schoolInfo, roles, navigatorLayouts);

            testDialog.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
            if(!beforeEnterEvent.getLocation()
                .getQueryParameters()
                    .getParameters()
                    .getOrDefault("error" , Collections.emptyList())
                    .isEmpty()){

                                login.setError(true);

            }


    }



}
