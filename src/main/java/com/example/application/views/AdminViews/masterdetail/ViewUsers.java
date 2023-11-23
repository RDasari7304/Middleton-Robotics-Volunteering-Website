package com.example.application.views.AdminViews.masterdetail;

import com.example.application.Entities.User.User;
import com.example.application.Entities.User.requestsUser;
import com.example.application.Resources.Services.DataService;
import com.example.application.views.AdminViews.Main.AdminMainView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER;

@Route(value = "UsersPage", layout = AdminMainView.class)
@PageTitle("View-Users")
@CssImport("styles/views/masterdetail/master-detail-view.css")
public class ViewUsers extends Div {

    private Grid<User> grid;
    private Grid<requestsUser> requestGrid;

    private TextField firstName = new TextField();
    private TextField lastName = new TextField();
    private TextField userName = new TextField();
    private TextField email = new TextField();
    private TextField password = new TextField();
    private Span numOfrequests;

    private TextField firstNameReq = new TextField();
    private TextField lastNameReq = new TextField();
    private TextField emailReq = new TextField();
    private TextField passwordReq = new TextField();
    private TextField usernameReq = new TextField();
    private TextField roleReq = new TextField();

    private Button viewStat = new Button("View Stats");
    private Button Delete = new Button("Delete");

    private Button grantAccess = new Button("Accept");
    private Button denyAcces = new Button("Deny");

    private DataService dataService = new DataService();

    public ViewUsers() {
        setId("master-detail-view");
        addClassName("viewUserPage");
        // Configure Grid
        grid = new Grid<>(User.class);
        requestGrid = new Grid<>(requestsUser.class);

        configureUserGrid();
        configureUserReqGrid();

        Delete.addClickListener(e -> grid.asSingleSelect().clear());

        configurePages();
        configureFunction();
        configureButtonFunctions();

    }

    private void configureFunction(){
        grid.addSelectionListener(e ->{
            if(grid.getSelectedItems().size() != 0) {
                User user = grid.getSelectionModel().getFirstSelectedItem().get();
                String FirstName = user.getFirstName();
                String LastName = user.getLastName();
                String Username = user.getUsername();
                String Password = user.getPassword();
                String Email = user.getEmail();
                String role = user.getRole();

                firstName.setValue(FirstName);
                lastName.setValue(LastName);
                userName.setValue(Username);
                password.setValue(Password);
                email.setValue(Email);
                roleReq.setValue(role);
            }
        });

        requestGrid.addSelectionListener(e ->{
            if(requestGrid.getSelectedItems().size() != 0){
            requestsUser user = requestGrid.getSelectionModel().getFirstSelectedItem().get();
            String FirstName = user.getFirstName();
            String LastName = user.getLastName();
            String Username = user.getUsername();
            String Password = user.getPassword();
            String email = user.getEmail();
            String role = user.getRole();

            firstNameReq.setValue(FirstName);
            lastNameReq.setValue(LastName);
            usernameReq.setValue(Username);
            passwordReq.setValue(Password);
            emailReq.setValue(email);
            roleReq.setValue(role);
        }}
        );
    }

    private boolean isRequestGridSelected(){
        if(firstNameReq.getValue() != ""){
            return true;
        }else{
            Notification.show("Please make sure a User has been selected and try again!");
            return false;
        }
    }

    private boolean isUserGridSelected(){
        if(firstName.getValue() != " "){
            return true;
        }else{
            Notification.show("Please make sure a User has been selected and try again!");
            return false;
        }
    }

    private void configureButtonFunctions(){
        grantAccess.addClickListener(e ->{
           if(isRequestGridSelected()){
                String Username = usernameReq.getValue();
                String Email = emailReq.getValue();/*
                Database.transferToUser(Username , Email);*/
                dataService.transferToUser(Username , roleReq.getValue() , firstNameReq.getValue() , lastNameReq.getValue() , Email);
                clearReqFields();
                updateGrids();
                Notification.show("The User with username, " + Username + " has been accepted.");
                reloadNumOfRequests();
           }
        });

        denyAcces.addClickListener(e ->{
           if(isRequestGridSelected()){
               String Username = usernameReq.getValue();/*
               Database.deleteRegisterAcc(Username);*/
               dataService.deleteRegisterAcc(Username);
               clearReqFields();
               updateGrids();
               Notification.show("The User with username, " + Username + " has been denied.");
               reloadNumOfRequests();
           }
        });

        Delete.addClickListener(e -> {
            if(isUserGridSelected()){
                String Username = userName.getValue();/*
                Database.deleteUser(Username);*/
                dataService.deleteUser(Username);
                clearUserFields();
                updateGrids();
                Notification.show("The User with username, " + Username + " has been deleted.");
            }
        });
    }

    private void configurePages(){

        Span currentUsers = new Span("Current Users");
/*
        numOfrequests = new Span("" + Database.getNumOfRequests());*/
        numOfrequests = new Span("" + dataService.getNumRequests());
        numOfrequests.addClassName("requestsNotif");
        Span requests = new Span("Requests " + " " + " " + " " + " ");

        Tab currentUsersTab = new Tab(currentUsers);
        Tab requestsTab = new Tab(requests);

        currentUsersTab.addClassName("usersTab");
        requestsTab.addClassName("usersTab");

        requestsTab.add(numOfrequests);

        Div currentUserDiv = new Div();
        Div requestsDiv = new Div();
        requestsDiv.setVisible(false);

        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(currentUsersTab , currentUserDiv);
        tabsToPages.put(requestsTab , requestsDiv);
        Tabs adminTabs = new Tabs(currentUsersTab , requestsTab);
        Div adminTabPages = new Div(currentUserDiv , requestsDiv);
        Set<Component> pageShown = Stream.of(currentUserDiv).
                collect(Collectors.toSet());

        HorizontalLayout tabsLayout = new HorizontalLayout(adminTabs);
        tabsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        SplitLayout currUserSplit = new SplitLayout();
        currUserSplit.setSizeFull();

        createGridLayout(currUserSplit);
        createEditorLayout(currUserSplit);

        currentUserDiv.add(currUserSplit);

        SplitLayout requestsSplit = new SplitLayout();
        requestsSplit.setSizeFull();

        createReqGridLayout(requestsSplit);
        createReqEditorLayout(requestsSplit);

        requestsDiv.add(requestsSplit);

        adminTabs.addSelectedChangeListener(e ->{
            pageShown.forEach(component -> component.setVisible(false));
            pageShown.clear();
            Component selectedPage = tabsToPages.get(adminTabs.getSelectedTab());
            selectedPage.setVisible(true);
            pageShown.add(selectedPage);
        });

        add(tabsLayout);
        add(adminTabPages);
    }

    private void configureUserGrid(){
        grid.addClassName("users-grid");
        grid.removeAllColumns();
/*
        ArrayList<User> usersArray = Database.getAllUsers();*/
        List<User> usersArray = dataService.getAllUsers();

        grid.setItems(usersArray);

        grid.addColumn(User::getFirstName).setHeader("First Name");
        grid.addColumn(User::getLastName).setHeader("Last Name");
        grid.addColumn(User::getUsername).setHeader("Username");
        grid.addColumn(User::getPassword).setHeader("Password");
        grid.addColumn(User::getTeam).setHeader("Team");
        grid.addColumn(User::getEmail).setHeader("Email Address");

        grid.setHeightByRows(true);
        grid.setMaxHeight("325px");
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorDiv = new Div();
        editorDiv.setId("editor-layout");
        FormLayout formLayout = new FormLayout();
        addFormItem(editorDiv, formLayout, firstName, "First Name");
        addFormItem(editorDiv, formLayout, lastName, "Last Name");
        addFormItem(editorDiv, formLayout, userName, "Username");
        addFormItem(editorDiv, formLayout, password, "Password");
        addFormItem(editorDiv, formLayout, email, "Email Address");
        createButtonLayout(editorDiv);
        splitLayout.addToSecondary(editorDiv);
    }

    private void createButtonLayout(Div editorDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        Delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        viewStat.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(viewStat);
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        buttonLayout.setJustifyContentMode(CENTER);
        editorDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();
        wrapper.add( grid);
        splitLayout.addToPrimary(wrapper);
    }


    private void addFormItem(Div wrapper, FormLayout formLayout, AbstractField field, String fieldName) {
        formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
    }

    private void addReqFormItem(Div wrapper, FormLayout formLayout, AbstractField field, String fieldName) {
        formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
    }

    private void configureUserReqGrid(){
        requestGrid.addClassName("requests-grid");
        requestGrid.removeAllColumns();
/*
        ArrayList<requestsUser> usersArray = Database.getAllRegisterUsers();*/

        List<requestsUser> usersArray = dataService.getAllRegisterUsers();

        requestGrid.setItems(usersArray);

        requestGrid.addColumn(requestsUser::getFirstName).setHeader("First Name");
        requestGrid.addColumn(requestsUser::getLastName).setHeader("Last Name");
        requestGrid.addColumn(requestsUser::getUsername).setHeader("Username");
        requestGrid.addColumn(requestsUser::getPassword).setHeader("Password");
        requestGrid.addColumn(requestsUser::getTeam).setHeader("Team");
        requestGrid.addColumn(requestsUser::getEmail).setHeader("Email Address");

        requestGrid.setHeightByRows(true);
    }

    private void createReqGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(requestGrid);
    }

    private void createReqEditorLayout(SplitLayout splitLayout) {
        Div editorDiv = new Div();
        editorDiv.setId("editor-layout");
        FormLayout formLayout = new FormLayout();
        addReqFormItem(editorDiv, formLayout, firstNameReq, "First Name");
        addReqFormItem(editorDiv, formLayout, lastNameReq, "Last Name");
        addReqFormItem(editorDiv, formLayout, usernameReq, "Username");
        addReqFormItem(editorDiv, formLayout, passwordReq, "Password");
        addReqFormItem(editorDiv, formLayout, emailReq, "Email Address");
        createReqButtonLayout(editorDiv);
        splitLayout.addToSecondary(editorDiv);
    }

    private void createReqButtonLayout(Div editorDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        grantAccess.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        denyAcces.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(grantAccess, denyAcces);
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        buttonLayout.setJustifyContentMode(CENTER);
        editorDiv.add(buttonLayout);
    }

    private void updateGrids(){/*
        ArrayList<User> allUsers = Database.getAllUsers();
        ArrayList<requestsUser> allreqUsers = Database.getAllRegisterUsers();*/


        List<User> allUsers = dataService.getAllUsers();
        List<requestsUser> allreqUsers = dataService.getAllRegisterUsers();

        grid.setItems(allUsers);
        requestGrid.setItems(allreqUsers);
    }

    private void clearUserFields(){
        userName.setValue("");
        password.setValue("");
        email.setValue("");
        firstName.setValue("");
        lastName.setValue("");
        roleReq.setValue("");
    }

    private void clearReqFields(){
        usernameReq.setValue("");
        passwordReq.setValue("");
        emailReq.setValue("");
        firstNameReq.setValue("");
        lastNameReq.setValue("");
        roleReq.setValue("");
    }

    private void setNumOfrequests(int numOfrequests){
        this.numOfrequests.setText("" + numOfrequests);
    }

    private void reloadNumOfRequests(){/*
        int numOfRequests = Database.getNumOfRequests();*/
        int numOfRequests = dataService.getNumRequests();
        setNumOfrequests(numOfRequests);
    }


}
