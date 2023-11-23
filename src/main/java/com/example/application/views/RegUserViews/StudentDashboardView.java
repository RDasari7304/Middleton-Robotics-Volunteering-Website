package com.example.application.views.RegUserViews;

import com.example.application.Components.Divider;
import com.example.application.Components.HDivider;
import com.example.application.Entities.Event.Event;
import com.example.application.Entities.User.userInEvent;
import com.example.application.Resources.Services.DataService;
import com.example.application.Resources.Extras.Util;
import com.example.application.Role;
import com.example.application.Security.LoggedInUserDetails;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSingleSelectionModel;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "events", layout = StudentMainView.class)
@PageTitle("Events Page")
@CssImport(value = "styles/views/dashboard/dashView.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class StudentDashboardView extends Div implements AfterNavigationObserver {

    Grid<Event> grid = new Grid<>();
    ArrayList<Event> permList;


    DataService dataService = new DataService();

    public StudentDashboardView() {
        setId("dashboard-view");
        addClassName("dashboard-view");
        setSizeFull();
        grid.setHeightFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addClassName("grid");
        permList = new ArrayList<>();
        permList.addAll(dataService.getAllEvents());
        grid.setItems(permList);
        grid.addComponentColumn(this::createEventCard);
        add(configureFilter(), grid);
    }

    private void filterEventsByName(String nameVal){
        if(nameVal == null || nameVal.isEmpty()){
            grid.setItems(permList);
            //Utils.notify("No filtering input found");
        }
        else{
            ArrayList<Event> filteredList = new ArrayList<>();
            for(Event event : permList){
                if(event.getEventName().toLowerCase().contains(nameVal.toLowerCase())){
                    filteredList.add(event);
                }
            }
            if(filteredList.isEmpty()) Util.notify("No results found!");
            grid.setItems(filteredList);
            //Utils.notify("U should see filtered list now");
        }
    }

    private void filterEventsByType(String typeVal){
        if(typeVal == null || typeVal.isEmpty()){
            grid.setItems(permList);
            //Utils.notify("No filtering input found");
        }
        else{
            ArrayList<Event> filteredList = new ArrayList<>();
            for(Event event : permList){
                String type = event.getEventType();
                if(type.toLowerCase().equals(typeVal.toLowerCase())){
                    filteredList.add(event);
                }
            }
            if(filteredList.isEmpty()) Util.notify("No results found!");
            grid.setItems(filteredList);
            //Utils.notify("U should see filtered list now");
        }
    }

    private HorizontalLayout createEventCard(Event event){
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setWidth("800px");
        card.setMinHeight("195px");
        card.setMaxHeight("195px");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        double amount = event.getReward();

        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span name = new Span(event.getEventName());
        name.addClassName("name");
        Span date;
        if(event.getStartingDate().equals(event.getEndingDate())){
            date = new Span(event.getStartingDate());
        }else{
            date = new Span(event.getStartingDate() + " - " + event.getEndingDate());
        }
        date.addClassName("date");

        String eventTypeText = event.getEventType() == "Fundraising" ? "Fundraising Event" : event.getEventType();

        Button eventType = new Button("Event Type : " + eventTypeText);
        eventType.setHeight("20px");
        eventType.getStyle().set("background-color" , "var(--lumo-success-color)");
        eventType.getStyle().set("color" , "var(--lumo-base-color)");
        eventType.getStyle().set("opacity" , "0.65");
        eventType.getStyle().set("border-radius" , "1.0em");
        eventType.getStyle().set("font-size" , "10px");


        Span desc = new Span(event.getDescription());
        desc.addClassName("post");

        List<userInEvent> usersInEvent = dataService.getAllRegisteredInEvent(event.getEventName());


        boolean userFound = false;
        userInEvent userSignUpInfo = new userInEvent();

        for(userInEvent user : usersInEvent){
            if(user.getFirstName().toLowerCase().equals(LoggedInUserDetails.getFirstName().toLowerCase())
                    && user.getLastName().toLowerCase().equals(LoggedInUserDetails.getLastName().toLowerCase())){
                userSignUpInfo = user;
                userFound = true;
                break;
            }
        }

        Icon infoIcon = new Icon(VaadinIcon.INFO_CIRCLE);
        infoIcon.setColor("white");
        infoIcon.setSize("30px");
        Button viewBtn = new Button("More Info", infoIcon);
        viewBtn.addClickListener(e -> createInfoWindow(event));
        viewBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        viewBtn.setIconAfterText(true);
        viewBtn.setWidth("200px");

        Icon signInIcon = new Icon(VaadinIcon.FLAG);
        signInIcon.setColor("white");
        signInIcon.setSize("30px");
        Button signUpBtn = new Button("Sign Up", signInIcon);
        signUpBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        signUpBtn.setIconAfterText(true);
        signUpBtn.setWidth("200px");

        Button role1 = new Button("Role: " + userSignUpInfo.getRole());
        role1.setHeight("20px");
        role1.getStyle().set("background-color" , "var(--lumo-primary-color)");
        role1.getStyle().set("color" , "var(--lumo-base-color)");
        role1.getStyle().set("opacity" , "0.75");
        role1.getStyle().set("border-radius" , "1.0em");
        role1.getStyle().set("font-size" , "10px");
        role1.setVisible(false);

        boolean signUpWindowOpen = Util.datePassed(Util.stringToDate(event.getSignUpStartDate()), Util.stringToTime(event.getSignUpStartTime()))
                && !Util.datePassed(Util.stringToDate(event.getSignUpEndDate()), Util.stringToTime(event.getSignUpEndTime()));

        boolean signUpWindowPassed = Util.datePassed(Util.stringToDate(event.getSignUpEndDate()), Util.stringToTime(event.getSignUpEndTime()));

        String signUpWindowStatusText = "";
        String signUpWindowStatusColor = "";

        if(!signUpWindowOpen && !signUpWindowPassed){
            signUpWindowStatusColor = "orange";
            signUpWindowStatusText = "Sign Up Window not open yet";
        }else if(!signUpWindowOpen && signUpWindowPassed){
            signUpWindowStatusColor = "var(--lumo-error-color)";
            signUpWindowStatusText = "Sign Up Window passed";
        }else if(signUpWindowOpen){
            signUpWindowStatusColor = "var(--lumo-success-color)";
            signUpWindowStatusText = "Sign Up Window open";
        }

        Button signUpWindowStatus = new Button(signUpWindowStatusText);
        signUpWindowStatus.setHeight("20px");
        signUpWindowStatus.getStyle().set("background-color" , signUpWindowStatusColor);
        signUpWindowStatus.getStyle().set("color" , "var(--lumo-base-color)");
        signUpWindowStatus.getStyle().set("opacity" , "0.65");
        signUpWindowStatus.getStyle().set("border-radius" , "1.0em");
        signUpWindowStatus.getStyle().set("font-size" , "10px");

        boolean datePassedStarting = Util.datePassed(Util.stringToDate(event.getStartingDate()), Util.stringToTime(event.getStartingTime()));
        boolean datePassedEnd = Util.datePassed(Util.stringToDate(event.getEndingDate()), Util.stringToTime(event.getEndTime()));

        String eventProgressText = "";
        String eventProgressColor = "";

        if(datePassedStarting && !datePassedEnd) {
            eventProgressText = "Event in progress";
            eventProgressColor = "var(--lumo-success-color)";
        } else if (!datePassedStarting){
            eventProgressText = "Event not started";
            eventProgressColor = "orange";
        }else if (datePassedEnd){
            eventProgressText = "Event finished";
            eventProgressColor = "var(--lumo-error-color)";
        }

        Button eventProgress = new Button(eventProgressText);
        eventProgress.setHeight("20px");
        eventProgress.getStyle().set("background-color" , eventProgressColor);
        eventProgress.getStyle().set("color" , "var(--lumo-base-color)");
        eventProgress.getStyle().set("opacity" , "0.75");
        eventProgress.getStyle().set("border-radius" , "1.0em");
        eventProgress.getStyle().set("font-size" , "10px");

        header.add(name, date , eventType, eventProgress);

        System.out.println("user found: " + userFound);
        if(userFound){
            signUpBtn.setText("Cancel Sign Up");
            signUpBtn.getStyle().set("background-color","red");
            signUpBtn.getStyle().set("font-color","white");
            signUpBtn.getStyle().set("margin-left","auto");
            Icon checkIcon = new Icon(VaadinIcon.CLOSE);
            checkIcon.setSize("30px");
            checkIcon.setColor("white");
            signUpBtn.setIcon(checkIcon);
        }

        if(signUpWindowOpen) {
            signUpBtn.addClickListener(e -> {
                userInEvent finalUserFound = isUserFoundInEvent(event);
                if (finalUserFound != null) {
                    cancelSignUp(event, finalUserFound.getRole());
                    signUpBtn.setText("Sign Up");
                    Icon flagIcon = new Icon(VaadinIcon.FLAG);
                    flagIcon.setColor("white");
                    flagIcon.setSize("30px");
                    role1.setVisible(false);
                    signUpBtn.setIcon(flagIcon);
                    signUpBtn.getElement().getStyle().set("background-color", "var(--lumo-primary-color)"/*ButtonVariant.LUMO_PRIMARY.getVariantName()*/);
                    //signUpBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);nets
                    signUpBtn.setIconAfterText(true);
                    signUpBtn.setWidth("200px");
                    signUpBtn.getStyle().set("margin-right", "0");

                /*signUpInfoDialog.close();
                btns.replace(viewSignUpButton , signUpBtn);*/
                } else openSignUpWindow(event, signUpBtn, role1);
            });
        }else{
            signUpBtn.setEnabled(false);
            signUpBtn.getStyle().set("opacity", "50%");
        }

        String amountDesc = eventTypeText.toLowerCase().equals("outreach event") ? amount + " hours." : "$" + amount + ".";
        Span timing = new Span("This event will give you an addition of " + amountDesc);
        timing.addClassName("post");

        Span roleDesc = new Span();
        roleDesc.addClassName("role");

        viewBtn.getStyle().set("margin-left", "auto");
        signUpBtn.getStyle().set("margin-left", "auto");

        VerticalLayout btns = new VerticalLayout();
        //btns.addClassName();
        btns.add(viewBtn, signUpBtn);
        btns.getStyle().set("margin-top", "auto");
        btns.getStyle().set("margin-bottom", "auto");

        description.add(header, desc, timing, signUpWindowStatus/*,roleDesc*/);
        //HorizontalLayout card = new HorizontalLayout();
        card.add(description, btns);
        card.setWidthFull();


        header.add(role1);



        return card;
    }

    private userInEvent isUserFoundInEvent(Event event){
        List<userInEvent> usersInEvent = dataService.getAllRegisteredInEvent(event.getEventName());
        boolean userFound = false;
        userInEvent currUser = null;
        String role = "";
        for(userInEvent user : usersInEvent){
            if(user.getFirstName().toLowerCase().equals(LoggedInUserDetails.getFirstName().toLowerCase())
                    && user.getLastName().toLowerCase().equals(LoggedInUserDetails.getLastName().toLowerCase())){
                role = user.getRole();
                currUser = user;
                userFound = true;
                break;
            }
        }
        return currUser;
    }

    private void createInfoWindow(Event event){
        Dialog infoWindow = new Dialog();
        infoWindow.setWidthFull();
        infoWindow.setHeight("75%");


        Label windowLabel = new Label("Middleton Robotics Event : " + event.getEventName());
        HorizontalLayout labelLay = new HorizontalLayout(windowLabel);
        labelLay.setAlignItems(FlexComponent.Alignment.CENTER);
        labelLay.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        VerticalLayout windowLabellay = new VerticalLayout(labelLay , new HDivider());
        windowLabellay.setAlignItems(FlexComponent.Alignment.CENTER);
        windowLabellay.setMargin(false);
        windowLabellay.setPadding(false);

        Grid<userInEvent> grid = new Grid<>(userInEvent.class);
        List<userInEvent> usersInEvent = dataService.getAllRegisteredInEventWithStatus(event.getEventName(), "approved");
        for(userInEvent user : usersInEvent){
            if(user.getFirstName().toLowerCase().equals(LoggedInUserDetails.getFirstName().toLowerCase())
            && user.getLastName().toLowerCase().equals(LoggedInUserDetails.getLastName().toLowerCase())){

                grid.select(user);
                GridSingleSelectionModel<userInEvent> singleSelect =
                        (GridSingleSelectionModel<userInEvent>) grid.getSelectionModel();
                singleSelect.setDeselectAllowed(false);
            }
        }
        configureInfoGrid(grid , usersInEvent);

        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);


        VerticalLayout information = new VerticalLayout();
        Label description = new Label("Description : " + event.getDescription());
        Label availableSlots = new Label("Slots Available : " + event.getSlotsAvaliable());

        Label Timings = new Label("Timings : " + event.getStartingTime() + " - " + event.getEndTime());
        Label Address = new Label("Address : " + event.getLocation() );

        Label emptySpace = new Label();
        emptySpace.setVisible(false);
        emptySpace.setWidth("65px");

        HorizontalLayout AddressLay = new HorizontalLayout(Address);
        AddressLay.setAlignItems(FlexComponent.Alignment.CENTER);
        AddressLay.setSpacing(true);

        HorizontalLayout availableSlotsLay = new HorizontalLayout(availableSlots);
        AddressLay.setAlignItems(FlexComponent.Alignment.CENTER);
        availableSlotsLay.setSpacing(true);

        HorizontalLayout timingsLay = new HorizontalLayout(Timings);
        AddressLay.setAlignItems(FlexComponent.Alignment.CENTER);
        timingsLay.setSpacing(true);

        HorizontalLayout mainInfo = new HorizontalLayout(Address , emptySpace , Timings);
        //HorizontalLayout mainInfo = new HorizontalLayout(AddressLay , timingsLay);
        mainInfo.setAlignItems(FlexComponent.Alignment.CENTER);


        HorizontalLayout rolesCard = new HorizontalLayout(createRolesCard(event.getEventName() ,event.getRoles()));
        rolesCard.setAlignItems(FlexComponent.Alignment.CENTER);

        rolesCard.setWidthFull();

        rolesCard.getElement().getStyle().set("width" , "100%");
        rolesCard.getElement().getStyle().set("height" , "auto");
        rolesCard.getElement().getStyle().set("margin-top" , "auto");



        HorizontalLayout placeTimingSlots = new HorizontalLayout();
        placeTimingSlots.add(availableSlots , emptySpace , mainInfo);
        placeTimingSlots.setWidthFull();
        placeTimingSlots.setAlignItems(FlexComponent.Alignment.CENTER);


        information.add(placeTimingSlots , description,  rolesCard );

        HorizontalLayout gridWithInfo = new HorizontalLayout(grid , information);
        gridWithInfo.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);


        VerticalLayout labelAndGrid = new VerticalLayout(windowLabellay , gridWithInfo);
        labelAndGrid.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        HorizontalLayout layoutAligner = new HorizontalLayout(labelAndGrid);
        layoutAligner.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        layoutAligner.getStyle().set("margin", "auto");
        layoutAligner.getStyle().set("width", "auto");
        layoutAligner.getStyle().set("display", "block");

        infoWindow.add(layoutAligner);
        infoWindow.open();
    }





    private void configureInfoGrid(Grid<userInEvent> grid , List<userInEvent> arrayList){
        grid.removeAllColumns();

        grid.setHeight("375px");
        grid.setWidth("850px");
        grid.setItems(arrayList);

        grid.addColumn(userInEvent::getFirstName).setHeader("First Name");
        grid.addColumn(userInEvent::getLastName).setHeader("Last Name");
        grid.addColumn(userInEvent::getRole).setHeader("Role");

    }

    private void cancelSignUp(Event event, String roleName){
        dataService.deleteUserFromEvent(new userInEvent(LoggedInUserDetails.getFirstName() , LoggedInUserDetails.getLastName() ,
                roleName , event.getEventName() , null , dataService.getUserFromEvent(LoggedInUserDetails.getFirstName(), LoggedInUserDetails.getLastName(), event.getEventName()).getStatus()));
    }

    private void openSignUpWindow(Event event, Button btn, Button roleBtn){
        Dialog form = new Dialog();
        form.setCloseOnOutsideClick(false);
        Label formLabel = new Label(event.getEventName() + " Sign Up");
        ArrayList<String> roleNames = new ArrayList<>();

        for(Role role : event.getRoles()){
            roleNames.add(role.getRoleName());
        }

        ComboBox<String> roleOptions = new ComboBox<>("Role");
        roleOptions.setClearButtonVisible(true);
        roleOptions.setItems(roleNames);
        roleOptions.setErrorMessage("Please enter in your role");

        TextField commentField = new TextField("Comment (Optional)");
        commentField.setMaxLength(200);

        Icon signUpIcon = new Icon(VaadinIcon.FLAG);
        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setIcon(signUpIcon);
        signUpBtn.setIconAfterText(true);
        signUpBtn.addClickListener(e->{
            if(roleOptions.isEmpty()){
                Notification.show("Please enter in all information");
            }
            else{/*
                DB.addUserToEvent(LoggedInUserDetails.loggedInUserDetails, event, roleOptions.getValue());*/
                userInEvent userInEvent = new userInEvent(LoggedInUserDetails.getFirstName() , LoggedInUserDetails.getLastName() ,
                        roleOptions.getValue() , event.getEventName() , commentField.getValue() , "pending");
                dataService.signUpUserForEvent(userInEvent);
                form.close();
                Util.notify("You have successfully signed up for " + event.getEventName() + " as " + roleOptions.getValue());
                roleBtn.setText("Role Sign Up for: " + roleOptions.getValue());
                roleBtn.setVisible(true);
                btn.setText("Cancel Sign Up");
                btn.getStyle().set("background-color","red");
                Icon checkIcon = new Icon(VaadinIcon.CLOSE);
                checkIcon.setSize("30px");
                checkIcon.setColor("white");
                btn.setIcon(checkIcon);
            }
        });

        signUpBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Icon cancelIcon = new Icon(VaadinIcon.SIGN_OUT);
        Button cancelBtn = new Button("Close", cancelIcon);
        cancelBtn.getElement().getStyle().set("background-color", "red");
        cancelBtn.getElement().getStyle().set("color", "white");
        cancelBtn.setIconAfterText(true);
        cancelBtn.addClickListener(e-> form.close());

        HorizontalLayout btnLayout = new HorizontalLayout(cancelBtn, signUpBtn);
        VerticalLayout layout = new VerticalLayout(formLabel, roleOptions, commentField, btnLayout);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        form.add(layout);
        add(form);
        form.open();
    }

    private VerticalLayout configureFilter(){
        VerticalLayout filter = new VerticalLayout();
        filter.addClassName("filter");
        filter.setWidthFull();
        filter.setHeight("91px");

        HorizontalLayout filterHeader = new HorizontalLayout();

        Span filterLabel = new Span("Filters");
        filterLabel.addClassName("filterLabel");

        filterHeader.add(filterLabel);

        HorizontalLayout datePicker = new HorizontalLayout();

        DatePicker picker = new DatePicker();
        picker.setPlaceholder("Pick a Date");

        Span pickerLabel = new Span("Date ");
        pickerLabel.addClassName("pickerLabel");

        datePicker.add(pickerLabel , picker);
        datePicker.setAlignItems(FlexComponent.Alignment.CENTER);
        datePicker.setHeight("40px");
        //datePicker.;

        Span EventName = new Span("Event");
        EventName.addClassName("pickerLabel");

        TextField eventField = new TextField();
        eventField.setPlaceholder("Enter Event Name");
        eventField.setClearButtonVisible(true);
        eventField.setValueChangeMode(ValueChangeMode.LAZY);
        eventField.addValueChangeListener(
                e->{
                    filterEventsByName(eventField.getValue());
                    //Utils.notify("Filtering...");
                });

        HorizontalLayout searchEvents = new HorizontalLayout(EventName , eventField);
        searchEvents.setAlignItems(FlexComponent.Alignment.CENTER);
        searchEvents.setHeight("40px");

        ComboBox<String> eventType = new ComboBox<>();
        eventType.setClearButtonVisible(true);
        eventType.addValueChangeListener(e->filterEventsByType(eventType.getValue()));
        eventType.setPlaceholder("Outreach or Fundraising");
        eventType.setItems("Both" , "Outreach Event" , "Fundraising Event");

        Span eventTypeLabel = new Span("Type ");
        eventType.addClassName("pickerLabel");

        HorizontalLayout pickType = new HorizontalLayout(eventTypeLabel , eventType);
        pickType.setAlignItems(FlexComponent.Alignment.CENTER);
        pickType.setHeight("40px");

        HorizontalLayout allItems = new HorizontalLayout(filterLabel , new Divider() , datePicker ,  searchEvents , pickType);
        allItems.setAlignSelf(FlexComponent.Alignment.CENTER);
        allItems.setAlignItems(FlexComponent.Alignment.CENTER);
        filter.add(allItems);
        filter.setAlignItems(FlexComponent.Alignment.CENTER);
        filter.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        return filter;
    }


    private void openRolesDetailsGrid(String eventName){
        Dialog rolesGridDialog = new Dialog();
        rolesGridDialog.setWidth("50%");
        rolesGridDialog.setHeight("75%");

        H4 roleHeader = new H4(eventName + " : Roles");
        roleHeader.getStyle().set("margin-right" , "auto");
        roleHeader.getStyle().set("margin-left" , "auto");

/*
        ArrayList<Role> roles = Database.getAllEventRoles(eventName);*/
        //roles.add(new com.example.application.Role("Camp Director" , "The camp director is responsible for makign sure everyone is on track to finish their summer camp videos" , 1.0));

        List<Role> roles = dataService.getRolesForEvent(eventName);
        Grid<Role> rolesGrid = new Grid<>();
        rolesGrid.setItems(roles);
        rolesGrid.addComponentColumn(role -> rolesCard(role , roles , rolesGrid , true));
        rolesGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        rolesGrid.setWidthFull();

        VerticalLayout fulllayout = new VerticalLayout(roleHeader , new Hr() , rolesGrid);
        fulllayout.setSizeFull();

        rolesGridDialog.add(fulllayout);

        rolesGridDialog.open();


    }

    private HorizontalLayout rolesCard(Role role , List<Role> roles , Grid<Role> grid , boolean viewMode){
        HorizontalLayout card = new HorizontalLayout();
        card.getStyle().set("background-color" , "var(--lumo-contrast-10pct)");
        card.getStyle().set("border-radius" , "var(--lumo-border-radius)");
        card.getStyle().set("box-shadow" , "var(--lumo-box-shadow-xs)");
        card.getStyle().set("padding" , "calc(var(--lumo-space-s) * 1.5) var(--lumo-space-m)");
        card.addClassName("card");
        card.setWidthFull();
        card.setSpacing(false);

        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span roleName = new Span(role.getRoleName());
        roleName.getStyle().set("font-size","var(--lumo-font-size-s");
        roleName.getStyle().set("font-weight","bold");

        Span slotsAvailable = new Span("Slots Available : " + role.getNumAvailableSpots());
        slotsAvailable.getStyle().set("color" , "var(--lumo-tertiary-text-color)");
        slotsAvailable.getStyle().set("font-size" , "var(--lumo-font-size-xs)");
        header.add(roleName, slotsAvailable);

        Span desc = new Span(role.getDescription());
        desc.addClassName("post");
        desc.getStyle().set("color" , "var(--lumo-secondary-text-color)");
        desc.getStyle().set("font-size" , "var(--lumo-font-size-s)");
        desc.getStyle().set("margin-bottom" , "var(--lumo-space-s)");
        desc.getStyle().set("white-space" , "normal");

        Button delete = new Button("Delete");
        delete.getStyle().set("margin-left" , "auto");
        delete.getStyle().set("margin-top" , "auto");
        delete.getStyle().set("margin-bottom" , "auto");
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.setWidth("150px");

        if(viewMode) delete.setVisible(false);

        Span prereqs = new Span("Prereq : " + role.getPrereq());
        prereqs.addClassName("rolePrereqsSpan");
        prereqs.getStyle().set("color" , "var(--lumo-secondary-text-color)");
        prereqs.getStyle().set("font-size" , "var(--lumo-font-size-s)");
        prereqs.getStyle().set("margin-bottom" , "var(--lumo-space-s)");
        prereqs.getStyle().set("white-space" , "normal");

        delete.addClickListener(buttonClickEvent -> {
            if(!viewMode) {
                for (int i = 0; i < roles.size(); i++) {
                    if (roles.get(i).getRoleName().equals(role.getRoleName())) {
                        roles.remove(i);
                    }
                }

                if (roles.size() != 0 && roles.get(roles.size() - 1).getRoleName().equals(role.getRoleName())) {
                    roles.remove(roles.size() - 1);
                }

                grid.setItems(roles);

            }
        });

        header.setAlignItems(FlexComponent.Alignment.CENTER);


        description.add(header, desc , prereqs);
        //HorizontalLayout card = new HorizontalLayout();
        card.add(description, delete);
        card.setWidthFull();
        return card;
    }

    private VerticalLayout createRolesCard(String event , ArrayList<Role> roles){
        VerticalLayout card = new VerticalLayout();

        card.setSpacing(false);

        Span cardLabel = new Span("Roles");
        cardLabel.getStyle().set("font-weight", "bold");
        HDivider HR = new HDivider();

        VerticalLayout Header = new VerticalLayout(cardLabel , HR);
        Header.setAlignItems(FlexComponent.Alignment.CENTER);
        Header.setSpacing(false);
        Header.setPadding(false);

        OrderedList list = new OrderedList();

        for(int i = 0 ; i < roles.size() - 1 ; i ++){
            list.add(getRoleDescriptionCard(event ,roles.get(i).getRoleName() , true));
        }

        if(roles.size() != 0) {
            list.add(getRoleDescriptionCard(event, roles.get(roles.size() - 1).getRoleName(), false));
        }

        Button viewRolesAsGrid = new Button("View Roles Grid");
        viewRolesAsGrid.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        viewRolesAsGrid.setIconAfterText(true);
        viewRolesAsGrid.getStyle().set("margin-left" , "20px");
        viewRolesAsGrid.getStyle().set("cursor" , "pointer");

        viewRolesAsGrid.setIcon(new Icon(VaadinIcon.INFO));

        viewRolesAsGrid.addClickListener(buttonClickEvent -> {
            openRolesDetailsGrid(event);
        });

        list.add(viewRolesAsGrid);


        list.getElement().getStyle().set("text-align" , "center");
        list.getStyle().set("margin-left" , "auto");
        list.getStyle().set("margin-right" , "auto");

        HorizontalLayout lists = new HorizontalLayout(list);

        VerticalLayout headerWithRoles = new VerticalLayout(Header , lists);
        //headerWithRoles.setSpacing(false);
        headerWithRoles.setPadding(false);
        headerWithRoles.setAlignItems(FlexComponent.Alignment.CENTER);
        headerWithRoles.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        card.add(Header , lists);
        card.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        return card;

    }

    private Span getRoleDescriptionCard(String eventName , String role , boolean addComma){
        /*Role roleDetails = Database.getRoleDetails(eventName , role);*/

        Role roleDetails = dataService.getRoleDetails(eventName , role);
        Span roleDescCard = new Span(role);
        if(addComma) roleDescCard.add(" , ");

        if(roleDetails.getNumAvailableSpots() == 0 ){
            roleDescCard.getStyle().set("text-decoration" , "line-through");
            roleDescCard.getStyle().set("cursor" , "no-drop");
        }else{
            roleDescCard.addClassName("roleDescCard");
        }

        roleDescCard.addClickListener(spanClickEvent -> {
            if(roleDetails.getNumAvailableSpots() != 0) {
                openRoleInfoDialog(roleDetails);
            }

        });

        return roleDescCard;
    }

    private void openRoleInfoDialog(Role role){
        Dialog roleInfoDialog = new Dialog();

        roleInfoDialog.setWidth("35%");

        H4 roleHeader = new H4(role.getEventName() + " : " + role.getRoleName());
        roleHeader.addClassName("roleHeader");

        Hr horizontalRule = new Hr();
        horizontalRule.setWidthFull();

        Span roleDesc = new Span("Description : " + role.getDescription());
        roleDesc.addClassName("roleDescSpan");

        Span rolePrereq = new Span("Prerequisite : " + role.getPrereq());
        rolePrereq.addClassName("rolePreReqSpan");

        Span slotsAvailable = new Span("Slots available : " + role.getNumAvailableSpots());
        slotsAvailable.addClassName("slotsAvailableSpan");

        VerticalLayout fullLayout = new VerticalLayout();
        fullLayout.add(roleHeader , horizontalRule , roleDesc , rolePrereq , slotsAvailable);
        fullLayout.addClassName("openRoleInfoDialogLayout");

        roleInfoDialog.add(fullLayout);

        roleInfoDialog.open();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        // Set some data when this view is displayed.

                /*,
        createPerson("https://randomuser.me/api/portraits/women/42.jpg", "Abagail Libbie", "May 3",
                "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document without relying on meaningful content (also called greeking).",
                "1K", "500", "20"),
        createPerson("https://randomuser.me/api/portraits/men/24.jpg", "Alberto Raya", "May 3",

                "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document without relying on meaningful content (also called greeking).",
                "1K", "500", "20"),
        createPerson("https://randomuser.me/api/portraits/women/24.jpg", "Emmy Elsner", "Apr 22",

                "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document without relying on meaningful content (also called greeking).",
                "1K", "500", "20"),
        createPerson("https://randomuser.me/api/portraits/men/76.jpg", "Alf Huncoot", "Apr 21",

                "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document without relying on meaningful content (also called greeking).",
                "1K", "500", "20"),
        createPerson("https://randomuser.me/api/portraits/women/76.jpg", "Lidmila Vilensky", "Apr 17",

                "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document without relying on meaningful content (also called greeking).",
                "1K", "500", "20"),
        createPerson("https://randomuser.me/api/portraits/men/94.jpg", "Jarrett Cawsey", "Apr 17",
                "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document without relying on meaningful content (also called greeking).",
                "1K", "500", "20"),
        createPerson("https://randomuser.me/api/portraits/women/94.jpg", "Tania Perfilyeva", "Mar 8",

                "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document without relying on meaningful content (also called greeking).",
                "1K", "500", "20"),
        createPerson("https://randomuser.me/api/portraits/men/16.jpg", "Ivan Polo", "Mar 5",

                "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document without relying on meaningful content (also called greeking).",
                "1K", "500", "20"),
        createPerson("https://randomuser.me/api/portraits/women/16.jpg", "Emelda Scandroot", "Mar 5",

                "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document without relying on meaningful content (also called greeking).",
                "1K", "500", "20"),
        createPerson("https://randomuser.me/api/portraits/men/67.jpg", "Marcos Sá", "Mar 4",

                "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document without relying on meaningful content (also called greeking).",
                "1K", "500", "20"),
        createPerson("https://randomuser.me/api/portraits/women/67.jpg", "Jacqueline Asong", "Mar 2",

                "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document without relying on meaningful content (also called greeking).",
                "1K", "500", "20")*/
    }
}
