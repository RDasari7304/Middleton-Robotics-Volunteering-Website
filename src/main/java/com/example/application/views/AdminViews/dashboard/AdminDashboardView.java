package com.example.application.views.AdminViews.dashboard;

import com.example.application.Components.CustomRadioButton;
import com.example.application.Components.CustomRadioGroup;
import com.example.application.Entities.Event.Event;
import com.example.application.Entities.User.userInEvent;
import com.example.application.Resources.Services.DataService;
import com.example.application.Resources.Extras.Util;
import com.example.application.Role;
import com.example.application.Components.Divider;
import com.example.application.Components.HDivider;
import com.example.application.views.AdminViews.Main.AdminMainView;
import com.vaadin.componentfactory.TooltipAlignment;
import com.vaadin.componentfactory.TooltipPosition;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route(value = "AdminHome", layout = AdminMainView.class)
@RouteAlias(value = "AdminHome", layout = AdminMainView.class)
@PageTitle("Main Events")
@CssImport(value = "styles/views/dashboard/dashView.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@JsModule("styles/shared-styles.js")
public class AdminDashboardView extends Div implements AfterNavigationObserver {

   private Grid<Event> grid = new Grid<>();
   private Grid.Column<Event> componentColumn1;
   private ListDataProvider<Event> dataProvider;

   // Filters
   private DatePicker picker;
   private TextField eventField;
   private ComboBox<String> eventType;

   DataService dataService = new DataService();

    public AdminDashboardView() {
        setId("dashboard-view");
        addClassName("dashboard-view");
        setSizeFull();

        ArrayList<String> roles = new ArrayList<>();
        roles.add("Camp Director");
        roles.add("Assistant Camp Director");
        roles.add("Program Direct");
        roles.add("Events Directors");
        roles.add("Mad STEM Science Director");
        roles.add("Beginner Robotics Director");
        roles.add("Advanced Robotics Director");
        roles.add("Volunteer");


        VerticalLayout filterLayout = configureFilter();
        ConfigureGrid();


        add(filterLayout , grid);
    }

    private void ConfigureGrid(){
        createGridComponent();
        configureComponentColumn();
        addFiltersToGrid();
    }

    private void createGridComponent(){
        grid = new Grid<>();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        reloadEvents();
    }

    private void reloadEvents(){
        List<Event> allEvents = dataService.getAllEvents();
        dataProvider = new ListDataProvider<>(allEvents);
        grid.setDataProvider(dataProvider);
    }

    private void configureComponentColumn(){
        componentColumn1 = grid.addComponentColumn(event -> createOutreachCard(event));
    }

    private void addFiltersToGrid(){
        eventField.setClearButtonVisible(true);
        picker.setClearButtonVisible(true);
        eventType.setClearButtonVisible(true);

        eventField.setValueChangeMode(ValueChangeMode.EAGER);
        eventField.addValueChangeListener(e -> dataProvider.addFilter(Event -> StringUtils.containsIgnoreCase(Event.getEventName() , eventField.getValue())));

        picker.addValueChangeListener(
                e -> dataProvider.addFilter(Event -> StringUtils.containsIgnoreCase(Event.getStartingDate() , Util.comparedString(Event , picker)))

        );


             /*   e -> dataProvider.addFilter(Event -> StringUtils.containsIgnoreCase(Event.getStartingTime() , Util.dateToString(picker.getValue())))
        */

        eventType.addValueChangeListener(e -> dataProvider.addFilter(Event -> isEventTypeEqual(Event , eventType.getValue())));
    }

    private boolean isEventTypeEqual(Event event, String eventType){
        if(eventType != null){
            if(eventType.equals("Both")) return true;
            return StringUtils.equals(event.getEventType() , eventType);
        }
        return true;
    }


    private HorizontalLayout createOutreachCard(Event event) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setWidth("800px");
        card.setMinHeight("195px");
        card.setMaxHeight("195px");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        boolean signUpWindowOpen = Util.datePassed(Util.stringToDate(event.getSignUpStartDate()), Util.stringToTime(event.getSignUpStartTime()))
                && !Util.datePassed(Util.stringToDate(event.getSignUpEndDate()), Util.stringToTime(event.getSignUpEndTime()));
        boolean signUpWindowPassed = Util.datePassed(Util.stringToDate(event.getSignUpEndDate()), Util.stringToTime(event.getSignUpEndTime()));

        System.out.println("Sign up window status: " + signUpWindowOpen);
        System.out.println("Sign up window passed: " + signUpWindowPassed);
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

        header.add(name, date , eventType, eventProgress);

        Span desc = new Span(event.getDescription());
        desc.addClassName("post");

        Icon infoIcon = new Icon(VaadinIcon.INFO_CIRCLE);
        infoIcon.setSize("30px");
        Button viewBtn = new Button("More Info", infoIcon);
        viewBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        viewBtn.setIconAfterText(true);
        viewBtn.setWidth("200px");

        Icon editIcon = new Icon(VaadinIcon.EDIT);
        editIcon.setSize("30px");
        Button editBtn = new Button("Edit Event", editIcon);
        editBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editBtn.setIconAfterText(true);
        editBtn.setWidth("200px");

        Icon viewSignUpsIcon = new Icon(VaadinIcon.PENCIL);
        Button viewSignUpsBtn = new Button("View SignUps");
        viewSignUpsBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        viewSignUpsBtn.setIconAfterText(true);
        viewSignUpsBtn.setWidth("200px");
        viewSignUpsBtn.getStyle().set("margin-left" , "auto");


        Icon deleteIcon = new Icon(VaadinIcon.DEL);
        Button DeleteEvent = new Button("Delete Event");
        DeleteEvent.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        DeleteEvent.setIconAfterText(true);
        DeleteEvent.setWidth("200px");
        DeleteEvent.getStyle().set("margin-left" , "auto");
        DeleteEvent.setIcon(deleteIcon);

        viewSignUpsIcon.setColor("white");
        editIcon.setColor("white");
        infoIcon.setColor("white");
        deleteIcon.setColor("white");

        viewBtn.getStyle().set("cursor" , "pointer");
        editBtn.getStyle().set("cursor" , "pointer");
        viewSignUpsBtn.getStyle().set("cursor" , "pointer");
        DeleteEvent.getStyle().set("cursor" , "pointer");

        viewBtn.addClickListener(buttonClickEvent -> {
                createInfoWindow(event);
        });

        editBtn.addClickListener(buttonClickEvent -> {
            openEditorWindow(event);

        });

        viewSignUpsBtn.addClickListener(buttonClickEvent -> {
            openSignUpsDialog(event);
        });

        Span timing = new Span("This event will give you an addition of " + event.getReward() + " " + event.getRewardType() + ".");
        timing.addClassName("post");

        viewBtn.getStyle().set("margin-left", "auto");
        editBtn.getStyle().set("margin-left", "auto");

        VerticalLayout btns = new VerticalLayout();
        //btns.addClassName();
        btns.add(viewBtn , editBtn , viewSignUpsBtn);

        description.add(header, desc, timing, signUpWindowStatus);
        //HorizontalLayout card = new HorizontalLayout();
        card.add(description, btns);
        card.setWidthFull();

        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }

    private void openEditorWindow(Event event){
        Dialog editorWindow = new Dialog();
        editorWindow.setHeightFull();
        editorWindow.setWidth("875px");
        editorWindow.setMaxWidth("875px");

        String initialEventName = event.getEventName();

        CustomRadioButton customRadioButton = new CustomRadioButton("1" , "Details" , "numberCircle");
        CustomRadioButton customRadioButton2 = new CustomRadioButton("2" , "Roles" , "numberCircle");
        CustomRadioButton customRadioButton3 = new CustomRadioButton("3" , "Finish" , "numberCircle");

        ArrayList<CustomRadioButton> buttons = new ArrayList<>();
        buttons.add(customRadioButton);
        buttons.add(customRadioButton2);
        buttons.add(customRadioButton3);

        CustomRadioGroup customRadioGroup = new CustomRadioGroup(buttons);
        customRadioGroup.selectButton(customRadioButton);

        customRadioButton3.setHorizontalBlueLineVisible(false);
        Dialog addEventWindow = new Dialog();
        addEventWindow.setHeightFull();
        addEventWindow.setWidth("875px");
        addEventWindow.setMaxWidth("875px");

        ArrayList<CustomRadioButton> disabledRadioButtons = new ArrayList<>();
        disabledRadioButtons.add(customRadioButton3);


        customRadioGroup.disableButtons(disabledRadioButtons);


        /*ArrayList<String> disabledRadioButtons = new ArrayList<>();
        disabledRadioButtons.add("Finish");

        RadioButtonGroup<String> currentSteps = new RadioButtonGroup<>();
        currentSteps.setItems("Event Details" , "Roles" , "Finish");
        currentSteps.getStyle().set("magin-left" , "auto");
        currentSteps.getStyle().set("magin-right" , "auto");

        currentSteps.setItemEnabledProvider(item -> !disabledRadioButtons.contains(item));
        currentSteps.setValue("Event Details");*/

        Label currentStep = new Label("Event Details");
        currentStep.getStyle().set("font-weight" , "bold");
        currentStep.getStyle().set("font-size" , "22px");
        Image firstStepImg = new Image("icons/firstStep.png" , "First Step");
        firstStepImg.setHeight("55px");

        Image secondStepImg = new Image("images/logos/secondStep.png"  , "Second Step");
        secondStepImg.setHeight("55px");

        HorizontalLayout TopLayout = new HorizontalLayout(firstStepImg , currentStep , customRadioGroup.getHorizontalButtongroup()/*currentSteps*/);
        TopLayout.setWidthFull();
        TopLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        TopLayout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        TopLayout.setMargin(false);
        TopLayout.getStyle().set("margin-top" , "auto");
        TopLayout.getStyle().set("margin-bottom" , "auto");

        TextField eventName = new TextField();
        eventName.setLabel("Event Name");
        eventName.setPlaceholder("Distinct name of event");
        eventName.setWidthFull();
        eventName.setRequired(true);
        eventName.setValueChangeMode(ValueChangeMode.EAGER);

        TextField Address = new TextField();
        Address.setLabel("Location");
        Address.setPlaceholder("Venue or address of event");
        Address.setWidthFull();
        Address.setRequired(true);
        Address.setValueChangeMode(ValueChangeMode.EAGER);

        Anchor userPreviousLocations = new Anchor();
        userPreviousLocations.setText("Previous Locations");
        userPreviousLocations.getStyle().set("font-size" , "15px");

        Anchor onlineEvent = new Anchor();
        onlineEvent.setText("Online Event");
        onlineEvent.getStyle().set("font-size" , "15px");

        Icon prevLocationIcon = new Icon(VaadinIcon.FOLDER_SEARCH);
        prevLocationIcon.setSize("20px");
        prevLocationIcon.getStyle().set("margin" , "0");
        prevLocationIcon.setColor("blue");

        Icon onlineEventIcon = new Icon(VaadinIcon.LOCATION_ARROW_CIRCLE_O);
        onlineEventIcon.setSize("20px");
        onlineEventIcon.getStyle().set("margin" , "0");
        onlineEventIcon.setColor("blue");

        DatePicker startingDate = new DatePicker();
        startingDate.setWidth("150px");

        TimePicker startingTime = new TimePicker();
        startingTime.setWidth("150px");

        DatePicker endingDate = new DatePicker();
        endingDate.setWidth("150px");

        TimePicker endingTime = new TimePicker();
        endingTime.setWidth("150px");


        DatePicker signUpStartDate = new DatePicker();
        signUpStartDate.setWidth("150px");

        TimePicker signUpStartTime = new TimePicker();
        signUpStartTime.setWidth("150px");

        DatePicker signUpEndDate = new DatePicker();
        signUpEndDate.setWidth("150px");

        TimePicker signUpEndTime = new TimePicker();
        signUpEndTime.setWidth("150px");

        Label starting = new Label("Starting");
        starting.getStyle().set("font-size" , "16px");
        Label ending = new Label("Ending");
        ending.getStyle().set("font-size" , "16px");
        Label signUpStart = new Label("Sign Up Window Start");
        signUpStart.getStyle().set("font-size" , "16px");
        Label signUpEnd = new Label("Sign Up Window End");
        signUpEnd.getStyle().set("font-size" , "16px");

        HorizontalLayout startingInfo = new HorizontalLayout(startingDate , startingTime);
        startingInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        startingInfo.setMargin(false);
        startingInfo.setPadding(false);

        VerticalLayout startingWithLabel = new VerticalLayout(starting , startingInfo);
        startingWithLabel.getStyle().set("margin" , "0");
        startingWithLabel.setSpacing(false);

        HorizontalLayout endingInfo = new HorizontalLayout(endingDate , endingTime);
        endingInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout endingWithLabel = new VerticalLayout(ending , endingInfo);
        endingWithLabel.getStyle().set("margin" , "0");
        endingWithLabel.setSpacing(false);


        HorizontalLayout signUpStartInfo = new HorizontalLayout(signUpStartDate , signUpStartTime);
        signUpStartInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        signUpStartInfo.setMargin(false);
        signUpStartInfo.setPadding(false);

        VerticalLayout signUpStartWithLabel = new VerticalLayout(signUpStart , signUpStartInfo);
        signUpStartWithLabel.getStyle().set("margin" , "0");
        signUpStartWithLabel.setSpacing(false);

        HorizontalLayout signUpEndInfo = new HorizontalLayout(signUpEndDate , signUpEndTime);
        signUpEndInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout signUpEndWithLabel = new VerticalLayout(signUpEnd, signUpEndInfo);
        signUpEndWithLabel.getStyle().set("margin" , "0");
        signUpEndWithLabel.setSpacing(false);

        HorizontalLayout fullInfo = new HorizontalLayout(startingWithLabel , endingWithLabel);
        fullInfo.setAlignItems(FlexComponent.Alignment.CENTER);


        HorizontalLayout fullSignUpInfo = new HorizontalLayout(signUpStartWithLabel , signUpEndWithLabel);
        fullSignUpInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout previousLocationAnchor = new HorizontalLayout(prevLocationIcon , userPreviousLocations);
        previousLocationAnchor.setAlignItems(FlexComponent.Alignment.CENTER);
        previousLocationAnchor.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        previousLocationAnchor.setPadding(false);
        previousLocationAnchor.setMargin(false);
        previousLocationAnchor.setSpacing(false);

        HorizontalLayout onlineEventAnchor = new HorizontalLayout(onlineEventIcon , onlineEvent);
        onlineEventAnchor.setAlignItems(FlexComponent.Alignment.CENTER);
        onlineEventAnchor.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        onlineEventAnchor.setPadding(false);
        onlineEventAnchor.setMargin(false);
        onlineEventAnchor.setSpacing(false);

        HorizontalLayout anchors = new HorizontalLayout(userPreviousLocations , onlineEvent);
        anchors.setMargin(false);
        anchors.setPadding(false);

        VerticalLayout location = new VerticalLayout(Address , anchors);
        location.setMargin(false);
        location.setSpacing(false);
        location.setPadding(false);

        ComboBox<String> eventType = new ComboBox<>();
        eventType.setLabel("Event Type");
        eventType.setItems("Outreach Event" , "Fundraising");
        eventType.setPlaceholder("Specify Event type for filtering");


        TextField reward = new TextField();
        reward.setLabel("Hours / Fundraising money");
        reward.setPlaceholder("amount of hours / money to earn");

        TextField slotsAvailable = new TextField();
        slotsAvailable.setLabel("Slots Available");
        slotsAvailable.setPlaceholder("int value");

        RichTextEditor descriptionBox = new RichTextEditor();
        descriptionBox.setWidthFull();


        Label descriptionLabel = new Label("Description");

        VerticalLayout descriptionLayout = new VerticalLayout();
        descriptionLayout.add(descriptionLabel , descriptionBox);
        descriptionLayout.setSpacing(false);
        descriptionLayout.setMargin(false);
        descriptionLayout.setPadding(false);
        descriptionLayout.getStyle().set("margin-top" , "10px");


        HorizontalLayout minorInfo = new HorizontalLayout(eventType , reward , slotsAvailable );
        minorInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        minorInfo.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        minorInfo.setMargin(false);

        Button detContinue = new Button("Save and Continue");
        detContinue.getStyle().set("margin-left" , "auto");
        detContinue.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        detContinue.addClickListener(buttonClickEvent -> {
            if(checkEverythingFilledEventDet(eventName , Address , eventType , reward , slotsAvailable ,
                    startingDate , startingTime , endingDate , endingTime, signUpStartDate, signUpStartTime,
                    signUpEndDate, signUpEndTime, descriptionBox
            )) {

                for (int i = 0; i < disabledRadioButtons.size() - 1; i++) {
                    if (disabledRadioButtons.get(i).equals(customRadioButton2)) {
                        disabledRadioButtons.remove(customRadioButton2);
                    }
                }
                customRadioGroup.enableButton(customRadioButton2);

                customRadioGroup.selectButton(customRadioButton2);

            }

        });

        List<Role> roles = dataService.getRolesForEvent(initialEventName);
        Grid<Role> rolesGrid = new Grid<>();
        rolesGrid.setItems(roles);
        rolesGrid.addComponentColumn(role -> rolesCard(role , roles , rolesGrid , false));
        rolesGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        rolesGrid.setWidthFull();

        TextField roleName = new TextField();
        roleName.setPlaceholder("Distinct name of role");
        roleName.setLabel("Role Name");

        TextField qualifications = new TextField();
        qualifications.setLabel("Qualifications");
        qualifications.setPlaceholder("Enter none in N/A");

        TextField numofSignUps = new TextField();
        numofSignUps.setLabel("# of signups allowed");
        numofSignUps.setPlaceholder("Maximum signups allowed");

        TextArea roleDescription = new TextArea();
        roleDescription.setLabel("Role description");
        roleDescription.setHeightFull();
        roleDescription.setWidthFull();

        HorizontalLayout roleTextFields = new HorizontalLayout(roleName , qualifications , numofSignUps);
        roleTextFields.setMargin(false);
        roleTextFields.setPadding(false);
        roleTextFields.setAlignItems(FlexComponent.Alignment.CENTER);

        Button addRole = new Button("Add Role");
        addRole.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button finishEdit = new Button("Finish edit");
        finishEdit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        finishEdit.setEnabled(false);

        Button roleContinue = new Button("Save and confirm edit");
        roleContinue.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        roleContinue.setEnabled(true);

        HorizontalLayout buttonLayAdd = new HorizontalLayout(addRole , finishEdit , roleContinue);

        addRole.addClickListener(buttonClickEvent -> {
            if(checkEverythingFilledRolesFields(roleName, qualifications , numofSignUps , roleDescription) && !finishEdit.isEnabled()){
                if(!eventName.isEmpty()) {
                    roles.add(new Role(roleName.getValue(), qualifications.getValue(), roleDescription.getValue(), eventName.getValue(),  Integer.parseInt(numofSignUps.getValue()) ));
                    rolesGrid.setItems(roles);
                    roleName.clear();
                    qualifications.clear();

                    numofSignUps.clear();
                    roleDescription.clear();
                }else Notification.show("Make sure you filled in the event name field before adding this role");

            }else{
                Notification.show("Check that all required fields are filled out or that you are not in edt mode");
            }

        });

        finishEdit.addClickListener(buttonClickEvent -> {
            if(finishEdit.isEnabled() && rolesGrid.getSelectedItems().size() != 0){
                Role savedRole = rolesGrid.getSelectionModel().getFirstSelectedItem().get();
                Role role = rolesGrid.getSelectionModel().getFirstSelectedItem().get();
                role.setRoleName(roleName.getValue());
                role.setPrereq(qualifications.getValue());
                role.setDescription(roleDescription.getValue());
                role.setAvailableSpots(Integer.parseInt(numofSignUps.getValue()));

                for(int i = 0 ; i < roles.size() - 1 ; i ++){
                    if(roles.get(i).equals(savedRole)){
                        roles.get(i).set(role);
                    }
                }

                if(roles.size() != 0 && roles.get(roles.size() - 1).equals(savedRole)){
                    roles.get(roles.size() - 1).set(role);
                }

                rolesGrid.setItems(roles);
                rolesGrid.deselectAll();

                roleName.clear();
                roleDescription.clear();
                numofSignUps.clear();
                qualifications.clear();

                finishEdit.setEnabled(false);
            }

        });

        rolesGrid.addSelectionListener(selectionEvent -> {
            if(rolesGrid.getSelectedItems().size() != 0){
                Role selectedRole = rolesGrid.getSelectionModel().getFirstSelectedItem().get();
                roleName.setValue(selectedRole.getRoleName());
                roleDescription.setValue(selectedRole.getDescription());
                numofSignUps.setValue(String.valueOf(selectedRole.getNumAvailableSpots()));
                finishEdit.setEnabled(true);
                addRole.setEnabled(false);
            }else{
                roleName.clear();
                roleDescription.clear();
                qualifications.clear();
                numofSignUps.clear();
                finishEdit.setEnabled(false);
                addRole.setEnabled(true);
            }

        });

        VerticalLayout fullTextfieldLayout = new VerticalLayout(roleTextFields , roleDescription , buttonLayAdd);
        fullTextfieldLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        fullTextfieldLayout.setMargin(false);
        fullTextfieldLayout.setSpacing(false);
        fullTextfieldLayout.setPadding(false);

        VerticalLayout fullRolesLayout = new VerticalLayout(rolesGrid , fullTextfieldLayout);
        fullRolesLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        fullRolesLayout.setVisible(false);

        VerticalLayout fullLayout = new VerticalLayout(eventName , location , minorInfo , fullInfo , fullSignUpInfo, descriptionLayout , detContinue);
        fullLayout.setSpacing(false);
        fullLayout.setPadding(false);
        fullLayout.setMargin(false);

        Map<String , Component> stepMap = new HashMap<>();
        stepMap.put("Details" , fullLayout);
        stepMap.put("Roles" , fullRolesLayout);
        Set<Component> pageShown = Stream.of(fullLayout).
                collect(Collectors.toSet());

        customRadioGroup.radioButtonGroup.addValueChangeListener(radioButtonGroupStringComponentValueChangeEvent -> {
            pageShown.forEach(component -> component.setVisible(false));
            pageShown.clear();
            if(customRadioGroup.getValueSelected().equals("Roles")){
                currentStep.setText("Event Roles");
                TopLayout.removeAll();
                TopLayout.add(secondStepImg , currentStep , customRadioGroup.getHorizontalButtongroup());
            }else if(customRadioGroup.getValueSelected().equals("Details")){
                currentStep.setText("Event Details");
                TopLayout.removeAll();
                TopLayout.add(firstStepImg , currentStep , customRadioGroup.getHorizontalButtongroup());
            }
            Component selectedPage = stepMap.get(customRadioGroup.getValueSelected());
            if(selectedPage != null)
            {selectedPage.setVisible(true);
                pageShown.add(selectedPage);
            }

        });

        /*currentSteps.addValueChangeListener(radioButtonGroupStringComponentValueChangeEvent -> {
            pageShown.forEach(component -> component.setVisible(false));
            pageShown.clear();
            if(currentSteps.getValue().equals("Roles")){
                currentStep.setText("Event Roles");
                TopLayout.removeAll();
                TopLayout.add(secondStepImg , currentStep , currentSteps);
            }else if(currentSteps.getValue().equals("Event Details")){
                currentStep.setText("Event Details");
                TopLayout.removeAll();
                TopLayout.add(firstStepImg , currentStep , currentSteps);
            }
            Component selectedPage = stepMap.get(currentSteps.getValue());
            if(selectedPage != null)
            {selectedPage.setVisible(true);
                pageShown.add(selectedPage);
            }

        });*/

        Dialog finishDialog = new Dialog();
        finishDialog.setVisible(false);

        roleContinue.addClickListener(buttonClickEvent -> {
            if(checkEverythingFilledEventDet(eventName , Address , eventType , reward , slotsAvailable ,
                    startingDate , startingTime , endingDate , endingTime, signUpStartDate, signUpStartTime,
                    signUpEndDate, signUpEndTime, descriptionBox
            )) {
                if(roles.size() >= 1) {
                    String description = descriptionBox.getHtmlValue();
                    description = description.replaceAll("\\<.*?\\>", "");/*
                        Database.updateEvent(initialEventName , new Event(roles , eventName.getValue() , Address.getValue() , eventType.getValue(),
                                Double.parseDouble(reward.getValue()) , Util.dateToString(startingDate.getValue()) , Util.timeToString(startingTime.getValue()) , Util.dateToString(endingDate.getValue()) , Util.timeToString(endingTime.getValue()) , Integer.parseInt(slotsAvailable.getValue()) , description , Integer.parseInt(slotsAvailable.getValue())));
*//*
                    grid.setItems(Database.getAllEvents());*/
                    ArrayList<Role> rolesAsList = new ArrayList<>();

                    for(Role r : roles){
                        rolesAsList.add(r);
                    }

                    dataService.updateEvent(initialEventName , new Event(rolesAsList , eventName.getValue() , Address.getValue() , eventType.getValue(),
                            Double.parseDouble(reward.getValue()) , Util.dateToString(startingDate.getValue()) , Util.timeToString(startingTime.getValue()) , Util.dateToString(endingDate.getValue()) , Util.timeToString(endingTime.getValue())
                            , Util.dateToString(signUpStartDate.getValue()) , Util.timeToString(signUpStartTime.getValue()) , Util.dateToString(signUpEndDate.getValue()) , Util.timeToString(signUpEndTime.getValue()), Integer.parseInt(slotsAvailable.getValue()) , description , Integer.parseInt(slotsAvailable.getValue())));
                    grid.setItems(dataService.getAllEvents());
                    editorWindow.close();
                }else Notification.show("Enter atleast one role before confirming the creation of this event");
            }
        });


        eventName.setValue(event.getEventName());
        Address.setValue(event.getLocation());
        eventType.setValue(event.getEventType());
        reward.setValue(Double.toString(event.getReward()));
        slotsAvailable.setValue(String.valueOf(event.getSlotsAvaliable()));
        startingDate.setValue(Util.stringToDate(event.getStartingDate()));
        startingTime.setValue(Util.stringToTime(event.getStartingTime()));
        endingDate.setValue(Util.stringToDate(event.getEndingDate()));
        endingTime.setValue(Util.stringToTime(event.getEndTime()));
        signUpStartDate.setValue(Util.stringToDate(event.getSignUpStartDate()));
        signUpStartTime.setValue(Util.stringToTime(event.getSignUpStartTime()));
        signUpEndDate.setValue(Util.stringToDate(event.getSignUpEndDate()));
        signUpEndTime.setValue(Util.stringToTime(event.getSignUpEndTime()));
        descriptionBox.setValue("[{\"insert\":\"" + event.getDescription() +"\\n\"}]");

        ArrayList<Role> rolesInEvent = event.getRoles();
        rolesGrid.setItems(rolesInEvent);


        editorWindow.add(TopLayout , new Hr(), fullLayout , fullRolesLayout , finishDialog);
        editorWindow.open();

    }

    public VerticalLayout configureFilter(){
        VerticalLayout filter = new VerticalLayout();
        filter.addClassName("filter");
        filter.setWidthFull();
        filter.setHeight("91px");
        filter.getStyle().set("box-shadow" ,"var(--lumo-box-shadow-xs)");

        HorizontalLayout filterHeader = new HorizontalLayout();

        Span filterLabel = new Span("Filters");
        filterLabel.addClassName("filterLabel");

        filterHeader.add(filterLabel);

        HorizontalLayout datePicker = new HorizontalLayout();

        picker = new DatePicker();
        picker.setPlaceholder("Pick a Date");

        Span pickerLabel = new Span("Date ");
        pickerLabel.addClassName("pickerLabel");

        datePicker.add(pickerLabel , picker);
        datePicker.setAlignItems(FlexComponent.Alignment.CENTER);
        datePicker.setHeight("40px");

        Span EventName = new Span("Event");
        EventName.addClassName("pickerLabel");

        eventField = new TextField();
        eventField.setPlaceholder("Enter Event Name");

        HorizontalLayout searchEvents = new HorizontalLayout(EventName , eventField);
        searchEvents.setAlignItems(FlexComponent.Alignment.CENTER);
        searchEvents.setHeight("40px");

        Icon addEvent = new Icon(VaadinIcon.PLUS_CIRCLE);
        addEvent.setSize("32px");
        addEvent.addClassName("addEvent");



        com.vaadin.componentfactory.Tooltip addEventToolTip = new com.vaadin.componentfactory.Tooltip();
        addEventToolTip.attachToComponent(addEvent);

        addEventToolTip.setPosition(TooltipPosition.BOTTOM);
        addEventToolTip.setAlignment(TooltipAlignment.CENTER);

        addEventToolTip.add(new Paragraph("Create Event"));

        addEventToolTip.getStyle().set("border-radius" , "5em");


        addEvent.addClickListener(iconClickEvent -> {
           createAddEventWindow();
        });

        addEvent.getStyle().set("margin-right" , "auto");

        eventType = new ComboBox<>();
        eventType.setPlaceholder("Outreach or Fundraising");
        eventType.setItems("Both" , "Outreach Event" , "Fundraising Event");

        Span eventTypeLabel = new Span("Type ");
        eventTypeLabel.addClassName("pickerLabel");

        HorizontalLayout pickType = new HorizontalLayout(eventTypeLabel , eventType);
        pickType.setAlignItems(FlexComponent.Alignment.CENTER);
        pickType.setHeight("40px");


        HorizontalLayout allItems = new HorizontalLayout(filterLabel , new Divider() , datePicker ,  searchEvents , pickType , addEvent, addEventToolTip);
        allItems.setAlignSelf(FlexComponent.Alignment.CENTER);
        allItems.setAlignItems(FlexComponent.Alignment.CENTER);
        filter.add(allItems);

        filter.setAlignItems(FlexComponent.Alignment.CENTER);
        filter.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        return filter;
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
        /*
        ArrayList<userInEvent> usersInEvent = Database.getAllRegisteredInEvent(event);*/
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


        HorizontalLayout placeTimingSlots = new HorizontalLayout();
        placeTimingSlots.add(availableSlots , emptySpace , mainInfo);
        placeTimingSlots.setWidthFull();
        placeTimingSlots.setAlignItems(FlexComponent.Alignment.CENTER);

        rolesCard.setWidthFull();

        rolesCard.getElement().getStyle().set("width" , "100%");
        rolesCard.getElement().getStyle().set("height" , "auto");
        rolesCard.getElement().getStyle().set("margin-top" , "auto");

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
        card.add(Header , lists);
        card.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        return card;

    }

    private void createAddEventWindow(){
        Dialog addEventWindow = new Dialog();
        addEventWindow.setHeightFull();
        addEventWindow.setWidth("875px");
        addEventWindow.setMaxWidth("875px");

        CustomRadioButton customRadioButton = new CustomRadioButton("1" , "Details" , "numberCircle");
        CustomRadioButton customRadioButton2 = new CustomRadioButton("2" , "Roles" , "numberCircle");
        CustomRadioButton customRadioButton3 = new CustomRadioButton("3" , "Finish" , "numberCircle");

        ArrayList<CustomRadioButton> buttons = new ArrayList<>();
        buttons.add(customRadioButton);
        buttons.add(customRadioButton2);
        buttons.add(customRadioButton3);

        CustomRadioGroup customRadioGroup = new CustomRadioGroup(buttons);
        customRadioGroup.selectButton(customRadioButton);

        customRadioButton3.setHorizontalBlueLineVisible(false);

        ArrayList<CustomRadioButton> disabledRadioButtons = new ArrayList<>();
        disabledRadioButtons.add(customRadioButton2);
        disabledRadioButtons.add(customRadioButton3);


        customRadioGroup.disableButtons(disabledRadioButtons);

        /*ArrayList<String> disabledRadioButtons = new ArrayList<>();
        disabledRadioButtons.add("Roles");
        disabledRadioButtons.add("Finish");*/

        /*RadioButtonGroup<String> currentSteps = new RadioButtonGroup<>();
        currentSteps.setItems("Event Details" , "Roles" , "Finish");
        currentSteps.getStyle().set("magin-left" , "auto");
        currentSteps.getStyle().set("magin-right" , "auto");

        currentSteps.setItemEnabledProvider(item -> !disabledRadioButtons.contains(item));
        currentSteps.setValue("Event Details");*/

        Label currentStep = new Label("Event Details");
        currentStep.getStyle().set("font-weight" , "bold");
        currentStep.getStyle().set("font-size" , "22px");
        Image firstStepImg = new Image("icons/firstStep.png" , "First Step");
        firstStepImg.setHeight("55px");

        Image secondStepImg = new Image("images/logos/secondStep.png"  , "Second Step");
        secondStepImg.setHeight("55px");

        HorizontalLayout TopLayout = new HorizontalLayout(firstStepImg , currentStep , customRadioGroup.getHorizontalButtongroup()/*currentSteps*/);
        TopLayout.setWidthFull();
        TopLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        TopLayout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        TopLayout.setMargin(false);
        TopLayout.getStyle().set("margin-top" , "auto");
        TopLayout.getStyle().set("margin-bottom" , "auto");

        TextField eventName = new TextField();
        eventName.setLabel("Event Name");
        eventName.setPlaceholder("Distinct name of event");
        eventName.setWidthFull();
        eventName.setRequired(true);
        eventName.setValueChangeMode(ValueChangeMode.EAGER);

        TextField Address = new TextField();
        Address.setLabel("Location");
        Address.setPlaceholder("Venue or address of event");
        Address.setWidthFull();
        Address.setRequired(true);
        Address.setValueChangeMode(ValueChangeMode.EAGER);

        Anchor userPreviousLocations = new Anchor();
        userPreviousLocations.setText("Previous Locations");
        userPreviousLocations.getStyle().set("font-size" , "15px");

        Anchor onlineEvent = new Anchor();
        onlineEvent.setText("Online Event");
        onlineEvent.getStyle().set("font-size" , "15px");

        Icon prevLocationIcon = new Icon(VaadinIcon.FOLDER_SEARCH);
        prevLocationIcon.setSize("20px");
        prevLocationIcon.getStyle().set("margin" , "0");
        prevLocationIcon.setColor("blue");

        Icon onlineEventIcon = new Icon(VaadinIcon.LOCATION_ARROW_CIRCLE_O);
        onlineEventIcon.setSize("20px");
        onlineEventIcon.getStyle().set("margin" , "0");
        onlineEventIcon.setColor("blue");

        DatePicker startingDate = new DatePicker();
        startingDate.setWidth("150px");

        TimePicker startingTime = new TimePicker();
        startingTime.setWidth("150px");

        DatePicker endingDate = new DatePicker();
        endingDate.setWidth("150px");

        TimePicker endingTime = new TimePicker();
        endingTime.setWidth("150px");

        DatePicker signUpStartDate = new DatePicker();
        signUpStartDate.setWidth("150px");

        TimePicker signUpStartTime = new TimePicker();
        signUpStartTime.setWidth("150px");

        DatePicker signUpEndDate = new DatePicker();
        signUpEndDate.setWidth("150px");

        TimePicker signUpEndTime = new TimePicker();
        signUpEndTime.setWidth("150px");

        Label starting = new Label("Starting");
        starting.getStyle().set("font-size" , "16px");
        Label ending = new Label("Ending");
        ending.getStyle().set("font-size" , "16px");
        Label signUpStart = new Label("Sign Up Window Start");
        signUpStart.getStyle().set("font-size" , "16px");
        Label signUpEnd = new Label("Sign Up Window End");
        signUpEnd.getStyle().set("font-size" , "16px");

        HorizontalLayout startingInfo = new HorizontalLayout(startingDate , startingTime);
        startingInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        startingInfo.setMargin(false);
        startingInfo.setPadding(false);

        VerticalLayout startingWithLabel = new VerticalLayout(starting , startingInfo);
        startingWithLabel.getStyle().set("margin" , "0");
        startingWithLabel.setSpacing(false);

        HorizontalLayout endingInfo = new HorizontalLayout(endingDate , endingTime);
        endingInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout endingWithLabel = new VerticalLayout(ending , endingInfo);
        endingWithLabel.getStyle().set("margin" , "0");
        endingWithLabel.setSpacing(false);

        HorizontalLayout signUpStartInfo = new HorizontalLayout(signUpStartDate , signUpStartTime);
        signUpStartInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        signUpStartInfo.setMargin(false);
        signUpStartInfo.setPadding(false);

        VerticalLayout signUpStartWithLabel = new VerticalLayout(signUpStart , signUpStartInfo);
        signUpStartWithLabel.getStyle().set("margin" , "0");
        signUpStartWithLabel.setSpacing(false);

        HorizontalLayout signUpEndInfo = new HorizontalLayout(signUpEndDate , signUpEndTime);
        signUpEndInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout signUpEndWithLabel = new VerticalLayout(signUpEnd, signUpEndInfo);
        signUpEndWithLabel.getStyle().set("margin" , "0");
        signUpEndWithLabel.setSpacing(false);

        HorizontalLayout fullInfo = new HorizontalLayout(startingWithLabel , endingWithLabel);
        fullInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout fullSignUpInfo = new HorizontalLayout(signUpStartWithLabel , signUpEndWithLabel);
        fullSignUpInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout previousLocationAnchor = new HorizontalLayout(prevLocationIcon , userPreviousLocations);
        previousLocationAnchor.setAlignItems(FlexComponent.Alignment.CENTER);
        previousLocationAnchor.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        previousLocationAnchor.setPadding(false);
        previousLocationAnchor.setMargin(false);
        previousLocationAnchor.setSpacing(false);

        HorizontalLayout onlineEventAnchor = new HorizontalLayout(onlineEventIcon , onlineEvent);
        onlineEventAnchor.setAlignItems(FlexComponent.Alignment.CENTER);
        onlineEventAnchor.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        onlineEventAnchor.setPadding(false);
        onlineEventAnchor.setMargin(false);
        onlineEventAnchor.setSpacing(false);

        HorizontalLayout anchors = new HorizontalLayout(userPreviousLocations , onlineEvent);
        anchors.setMargin(false);
        anchors.setPadding(false);

        VerticalLayout location = new VerticalLayout(Address , anchors);
        location.setMargin(false);
        location.setSpacing(false);
        location.setPadding(false);

        ComboBox<String> eventType = new ComboBox<>();
        eventType.setLabel("Event Type");
        eventType.setItems("Outreach Event" , "Fundraising");
        eventType.setPlaceholder("Specify Event type for filtering");


        TextField reward = new TextField();
        reward.setLabel("Hours / Fundraising money");
        reward.setPlaceholder("amount of hours / money to earn");

        TextField slotsAvailable = new TextField();
        slotsAvailable.setLabel("Slots Available");
        slotsAvailable.setPlaceholder("int value");

        RichTextEditor descriptionBox = new RichTextEditor();
        descriptionBox.setWidthFull();


        Label descriptionLabel = new Label("Description");

        VerticalLayout descriptionLayout = new VerticalLayout();
        descriptionLayout.add(descriptionLabel , descriptionBox);
        descriptionLayout.setSpacing(false);
        descriptionLayout.setMargin(false);
        descriptionLayout.setPadding(false);
        descriptionLayout.getStyle().set("margin-top" , "10px");


        HorizontalLayout minorInfo = new HorizontalLayout(eventType , reward , slotsAvailable );
        minorInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        minorInfo.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        minorInfo.setMargin(false);

        Button detContinue = new Button("Save and Continue");
        detContinue.getStyle().set("margin-left" , "auto");
        detContinue.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        detContinue.addClickListener(buttonClickEvent -> {
            if(checkEverythingFilledEventDet(eventName , Address , eventType , reward , slotsAvailable ,
                    startingDate , startingTime , endingDate , endingTime, signUpStartDate, signUpStartTime,
                    signUpEndDate, signUpEndTime, descriptionBox
            )) {

                for (int i = 0; i < disabledRadioButtons.size() - 1; i++) {
                    if (disabledRadioButtons.get(i).equals(customRadioButton2)) {
                        disabledRadioButtons.remove(customRadioButton2);
                    }
                }
                customRadioGroup.enableButton(customRadioButton2);

                customRadioGroup.selectButton(customRadioButton2);

            }else Notification.show("Please fill all required fields before proceeding");



        /*if(checkEverythingFilledEventDet(eventName , Address , eventType , reward , slotsAvailable ,
                    startingDate , startingTime , endingDate , endingTime , descriptionBox
                )) {

            for (int i = 0; i < disabledRadioButtons.size() - 1; i++) {
                if (disabledRadioButtons.get(i).equals("Roles")) {
                    disabledRadioButtons.remove(i);
                }
            }
            currentSteps.setItemEnabledProvider(item -> !disabledRadioButtons.contains(item));

            currentSteps.setValue("Roles");

            }else Notification.show("Please fill all required fields before proceeding");*/
        });

        ArrayList<Role> roles = new ArrayList<>();
        //roles.add(new com.example.application.Role("Camp Director" , "The camp director is responsible for makign sure everyone is on track to finish their summer camp videos" , 1.0));

        Grid<Role> rolesGrid = new Grid<>();
        //rolesGrid.setThemeName("no-padding" , true);
        //rolesGrid.setThemeName("no-padding");
        rolesGrid.setItems(roles);
        rolesGrid.addComponentColumn(role -> rolesCard(role , roles , rolesGrid , false));
        rolesGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        rolesGrid.setWidthFull();

        TextField roleName = new TextField();
        roleName.setPlaceholder("Distinct name of role");
        roleName.setLabel("Role Name");

        TextField qualifications = new TextField();
        qualifications.setLabel("Qualifications");
        qualifications.setPlaceholder("Enter none in N/A");

        TextField numofSignUps = new TextField();
        numofSignUps.setLabel("# of signups allowed");
        numofSignUps.setPlaceholder("Maximum signups allowed");

        TextArea roleDescription = new TextArea();
        roleDescription.setLabel("Role description");
        roleDescription.setHeightFull();
        roleDescription.setWidthFull();

        HorizontalLayout roleTextFields = new HorizontalLayout(roleName , qualifications , numofSignUps);
        roleTextFields.setMargin(false);
        roleTextFields.setPadding(false);
        roleTextFields.setAlignItems(FlexComponent.Alignment.CENTER);

        Button addRole = new Button("Add Role");
        addRole.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button finishEdit = new Button("Finish edit");
        finishEdit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        finishEdit.setEnabled(false);

        Button roleContinue = new Button("Save and Continue");
        roleContinue.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        roleContinue.setEnabled(true);

        HorizontalLayout buttonLayAdd = new HorizontalLayout(addRole , finishEdit , roleContinue);

        addRole.addClickListener(buttonClickEvent -> {
           if(checkEverythingFilledRolesFields(roleName, qualifications , numofSignUps , roleDescription) && !finishEdit.isEnabled()){
              if(!eventName.isEmpty()) {
                  roles.add(new Role(roleName.getValue(), qualifications.getValue(), roleDescription.getValue(), eventName.getValue(),  Integer.parseInt(numofSignUps.getValue()) ));
                  rolesGrid.setItems(roles);
                  roleName.clear();
                  qualifications.clear();

                  numofSignUps.clear();
                  roleDescription.clear();
              }else Notification.show("Make sure you filled int he event name field before adding this role");

           }else{
                Notification.show("Check that all required fields are filled out or that you are not in edt mode");
           }

        });

        finishEdit.addClickListener(buttonClickEvent -> {
           if(finishEdit.isEnabled() && rolesGrid.getSelectedItems().size() != 0){
               Role savedRole = rolesGrid.getSelectionModel().getFirstSelectedItem().get();
               Role role = rolesGrid.getSelectionModel().getFirstSelectedItem().get();
               role.setRoleName(roleName.getValue());
               role.setPrereq(qualifications.getValue());
               role.setDescription(roleDescription.getValue());
               role.setAvailableSpots(Integer.parseInt(numofSignUps.getValue()));

               for(int i = 0 ; i < roles.size() - 1 ; i ++){
                   if(roles.get(i).equals(savedRole)){
                       roles.get(i).set(role);
                   }
               }

               if(roles.size() != 0 && roles.get(roles.size() - 1).equals(savedRole)){
                   roles.get(roles.size() - 1).set(role);
               }

               rolesGrid.setItems(roles);
               rolesGrid.deselectAll();

               roleName.clear();
               roleDescription.clear();
               numofSignUps.clear();
               qualifications.clear();

                finishEdit.setEnabled(false);
           }

        });

        rolesGrid.addSelectionListener(selectionEvent -> {
           if(rolesGrid.getSelectedItems().size() != 0){
               Role selectedRole = rolesGrid.getSelectionModel().getFirstSelectedItem().get();
               roleName.setValue(selectedRole.getRoleName());
               roleDescription.setValue(selectedRole.getDescription());
               numofSignUps.setValue(String.valueOf(selectedRole.getNumAvailableSpots()));
               finishEdit.setEnabled(true);
               addRole.setEnabled(false);
           }else{
                roleName.clear();
                roleDescription.clear();
                qualifications.clear();
                numofSignUps.clear();
                finishEdit.setEnabled(false);
                addRole.setEnabled(true);
           }

        });

        VerticalLayout fullTextfieldLayout = new VerticalLayout(roleTextFields , roleDescription , buttonLayAdd);
        fullTextfieldLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        fullTextfieldLayout.setMargin(false);
        fullTextfieldLayout.setSpacing(false);
        fullTextfieldLayout.setPadding(false);

        VerticalLayout fullRolesLayout = new VerticalLayout(rolesGrid , fullTextfieldLayout);
        fullRolesLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        fullRolesLayout.setVisible(false);

        VerticalLayout fullLayout = new VerticalLayout(eventName , location , minorInfo , fullInfo, fullSignUpInfo , descriptionLayout , detContinue);
        fullLayout.setSpacing(false);
        fullLayout.setPadding(false);
        fullLayout.setMargin(false);

        Map<String , Component> stepMap = new HashMap<>();
        stepMap.put("Details" , fullLayout);
        stepMap.put("Roles" , fullRolesLayout);
        Set<Component> pageShown = Stream.of(fullLayout).
                collect(Collectors.toSet());

        customRadioGroup.radioButtonGroup.addValueChangeListener(radioButtonGroupStringComponentValueChangeEvent -> {
            pageShown.forEach(component -> component.setVisible(false));
            pageShown.clear();
            if(customRadioGroup.getValueSelected().equals("Roles")){
                currentStep.setText("Event Roles");
                TopLayout.removeAll();
                TopLayout.add(secondStepImg , currentStep , customRadioGroup.getHorizontalButtongroup());
            }else if(customRadioGroup.getValueSelected().equals("Details")){
                currentStep.setText("Event Details");
                TopLayout.removeAll();
                TopLayout.add(firstStepImg , currentStep , customRadioGroup.getHorizontalButtongroup());
            }
            Component selectedPage = stepMap.get(customRadioGroup.getValueSelected());
            if(selectedPage != null)
            {selectedPage.setVisible(true);
                pageShown.add(selectedPage);
            }

        });

        /*currentSteps.addValueChangeListener(radioButtonGroupStringComponentValueChangeEvent -> {
            pageShown.forEach(component -> component.setVisible(false));
            pageShown.clear();
            if(currentSteps.getValue().equals("Roles")){
                currentStep.setText("Event Roles");
                TopLayout.removeAll();
                TopLayout.add(secondStepImg , currentStep , currentSteps);
            }else if(currentSteps.getValue().equals("Event Details")){
                currentStep.setText("Event Details");
                TopLayout.removeAll();
                TopLayout.add(firstStepImg , currentStep , currentSteps);
            }
            Component selectedPage = stepMap.get(currentSteps.getValue());
            if(selectedPage != null)
            {selectedPage.setVisible(true);
                pageShown.add(selectedPage);
            }

        });*/

        Dialog finishDialog = new Dialog();
        finishDialog.setVisible(false);

        roleContinue.addClickListener(buttonClickEvent -> {
         if(checkEverythingFilledEventDet(eventName , Address , eventType , reward , slotsAvailable ,
                 startingDate , startingTime , endingDate , endingTime , signUpStartDate, signUpStartTime,
                 signUpEndDate, signUpEndTime, descriptionBox
         )) {
             if(roles.size() >= 1) {
                 configureFinishDialog(eventName , Address , eventType , reward , slotsAvailable ,
                         startingDate , startingTime , endingDate , endingTime , signUpStartDate, signUpStartTime, signUpEndDate, signUpEndTime, descriptionBox , roles ,finishDialog , addEventWindow);
                 finishDialog.setVisible(true);
                 finishDialog.open();
             }else Notification.show("Enter atleast one role before confirming the creation of this event");
         }
        });

        addEventWindow.add(TopLayout , new Hr(), fullLayout , fullRolesLayout , finishDialog);
        addEventWindow.open();
    }

    private void configureFinishDialog(TextField evtName , TextField address ,
                                       ComboBox<String> type , TextField reward , TextField slotsAvailable ,
                                       DatePicker startingDate , TimePicker startingTime , DatePicker endingDate , TimePicker endingTime ,
                                       DatePicker signUpStartingDate , TimePicker signUpStartingTime , DatePicker signUpEndingDate , TimePicker signUpEndingTime ,
                                       RichTextEditor descriptionBox , ArrayList<Role> roles ,
                                       Dialog finish , Dialog eventCreator){
        Label finishCreation = new Label("Finish creation of event , " + evtName.getValue() + " ?");
        finishCreation.getStyle().set("margin-left" , "auto");
        finishCreation.getStyle().set("margin-right" , "auto");
        Checkbox sendEmail = new Checkbox("Send notification email to all users");

        Button cancel = new Button("Cancel");
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancel.addClickListener(buttonClickEvent -> {
            finish.close();
        });

        Button confirm = new Button("Confirm");
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        confirm.addClickListener(buttonClickEvent -> {
           String eventName = evtName.getValue();
           String Location = address.getValue();
           String eventType = type.getValue();
           double Reward = Double.parseDouble(reward.getValue());
           int slotsLeft = Integer.parseInt(slotsAvailable.getValue());
           String starting_date = Util.dateToString(startingDate.getValue());
           String starting_time = Util.timeToString(startingTime.getValue());
           String ending_date = Util.dateToString(endingDate.getValue());
           String ending_time = Util.timeToString(endingTime.getValue());
           String signUpStartDate = Util.dateToString(signUpStartingDate.getValue());
           String signUpStartTime = Util.timeToString(signUpStartingTime.getValue());
           String signUpEndDate = Util.dateToString(signUpEndingDate.getValue());
           String signUpEndTime = Util.timeToString(signUpEndingTime.getValue());
           String description = descriptionBox.getHtmlValue();
           description = description.replaceAll("\\<.*?\\>", "");


           if(Util.validateSignUpWindow(signUpStartingDate.getValue(), signUpStartingTime.getValue(),
                   signUpEndingDate.getValue(), signUpEndingTime.getValue(), startingDate.getValue(), startingTime.getValue())) {
               Event evtToAdd = new Event(roles, eventName, Location, eventType, Reward, starting_date,
                       starting_time, ending_date, ending_time, signUpStartDate, signUpStartTime, signUpEndDate, signUpEndTime, slotsLeft, description, Integer.parseInt(slotsAvailable.getValue()));

               dataService.addEvent(evtToAdd);
           }else{
               Notification.show("Ensure that the Sign Up Window starting date is before the Sign Up Window ending date " +
                       "and that the sign up window ends before the event starts");
           }

           finish.close();
           eventCreator.close();

           /*grid.setItems(Database.getAllEvents());*/
            grid.setItems(dataService.getAllEvents());

        });

        HorizontalLayout buttonlay = new HorizontalLayout(cancel , confirm);
        buttonlay.getStyle().set("margin-left" , "auto");
        buttonlay.getStyle().set("margin-right" , "auto");

        VerticalLayout fullLayoutFinish = new VerticalLayout(finishCreation , new Hr() , sendEmail , buttonlay);
        fullLayoutFinish.setMargin(false);
        fullLayoutFinish.setPadding(false);

        finish.add(fullLayoutFinish);

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

    private boolean checkEverythingFilledRolesFields(TextField roleName , TextField qualificationField , TextField numOfSpots , TextArea description){
        if(roleName.isEmpty() || qualificationField.isEmpty() || numOfSpots.isEmpty() || description.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    private boolean checkEverythingFilledEventDet(TextField name , TextField location , ComboBox<String> type ,
                                                  TextField reward , TextField numOfSpotsLeft , DatePicker startDate ,
                                                  TimePicker startingTime , DatePicker endingDate , TimePicker endingTime, DatePicker signUpStartDate ,
                                                  TimePicker signUpStartTime , DatePicker signUpEndDate , TimePicker signUpEndTime , RichTextEditor description){

        if(name.isEmpty() || location.isEmpty() || type.isEmpty() || reward.isEmpty()
                || numOfSpotsLeft.isEmpty() || startDate.isEmpty() || startingTime.isEmpty() || endingDate.isEmpty()
                || endingTime.isEmpty() || signUpStartDate.isEmpty() || signUpStartTime.isEmpty() || signUpEndDate.isEmpty()
                || signUpEndTime.isEmpty() || description.isEmpty()){
            Notification.show("Please fill all required fields before proceeding");
            return false;
        }else{
            if(Util.validateSignUpWindow(signUpStartDate.getValue(), signUpStartTime.getValue(),
                    signUpEndDate.getValue(), signUpEndTime.getValue(), startDate.getValue(), startingTime.getValue())) {

                return true;

                }else{
                Notification.show("Ensure that the Sign Up Window starting date is before the Sign Up Window ending date " +
                        "and that the sign up window ends before the event starts");
                return false;
            }
        }

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

    private void openSignUpsDialog(Event event ){
        Dialog signUpsDialog = new Dialog();
        signUpsDialog.setWidthFull();
        signUpsDialog.setHeight("75%");

        H4 signUps = new H4(event.getEventName() + " : Sign ups");
        signUps.setWidthFull();
        signUps.getStyle().set("text-align" , "center");

        Tab pendingRequests = new Tab("Pending");
        Tab rejectedRequests = new Tab("Rejected");
        Tab approvedRequests = new Tab("Approved");

        Tabs gridsTabs = new Tabs(pendingRequests , rejectedRequests , approvedRequests);
        gridsTabs.setWidthFull();

        Grid<userInEvent> pendingGrid = getSignUpsGrid(event , "pending");
        Grid<userInEvent> rejectedGrid = getSignUpsGrid(event , "rejected");
        Grid<userInEvent> approvedGrid = getSignUpsGrid(event , "approved");


        Map<String , Component> tabChosenToGrid = new HashMap<>();
        tabChosenToGrid.put("Pending" , pendingGrid);
        tabChosenToGrid.put("Rejected" , rejectedGrid);
        tabChosenToGrid.put("Approved" , approvedGrid);

        Set<Component> pageShown = Stream.of(pendingGrid).
                collect(Collectors.toSet());

        gridsTabs.addSelectedChangeListener(e ->{
            pageShown.forEach(component -> component.setVisible(false));
            pageShown.clear();

            Component selectedPage = tabChosenToGrid.get(gridsTabs.getSelectedTab().getLabel());

            switch(gridsTabs.getSelectedTab().getLabel()){
                case "Pending":
                    pendingGrid.setItems(dataService.getAllRegisteredInEventWithStatus(event.getEventName() , "pending"));
                    break;
                case "Rejected":
                    rejectedGrid.setItems(dataService.getAllRegisteredInEventWithStatus(event.getEventName() , "rejected"));
                    break;
                case "Approved":
                    approvedGrid.setItems(dataService.getAllRegisteredInEventWithStatus(event.getEventName() , "approved"));
                    break;
            }
            selectedPage.setVisible(true);

            pageShown.add(selectedPage);
        });

        rejectedGrid.setVisible(false);
        approvedGrid.setVisible(false);

        signUpsDialog.add(signUps , new Hr() , gridsTabs , pendingGrid , rejectedGrid , approvedGrid);
        signUpsDialog.open();

    }

    public void gridSwitch(Grid<userInEvent> grid , Event event ,  String tabLabel){
        grid = getSignUpsGrid(event , "pending");
    }

    private Chart getNumberSignUpsGuageChart(double numSignUps , Event event){
        Chart scoreChart = new Chart(ChartType.SOLIDGAUGE);

        Configuration chartConfig = scoreChart.getConfiguration();

        chartConfig.setTitle("Sign Ups");

        Pane pane = chartConfig.getPane();
        pane.setStartAngle(-180);
        pane.setEndAngle(180);
        pane.setCenter(new String[] {"50%" , "50%"});

        Background paneBackground = new Background();
        paneBackground.setInnerRadius("60%");
        paneBackground.setOuterRadius("100%");
        paneBackground.setShape(BackgroundShape.ARC);
        pane.setBackground(paneBackground);

        YAxis yAxis = chartConfig.getyAxis();
        yAxis.setTickAmount(2);
        yAxis.getTitle().setY(-45);
        yAxis.getLabels().setY(16);
        yAxis.setMinorTickInterval("null");
        yAxis.setMin(0);
        yAxis.setMax(1);

        PlotOptionsSolidgauge plotOptionsSolidgauge = new PlotOptionsSolidgauge();

        DataLabels dataLabels = plotOptionsSolidgauge.getDataLabels();
        dataLabels.setY(5);
        dataLabels.setUseHTML(true);

        chartConfig.setPlotOptions(plotOptionsSolidgauge);

        DataSeries series = new DataSeries("Score");

        DataSeriesItem item = new DataSeriesItem();
        item.setY(0);
//        item.setColorIndex(2);
        item.setClassName("myClassName");
        DataLabels dataLabelsSeries = new DataLabels();
        dataLabelsSeries.setFormat("<div style=\"text-align:center\"><span style=\"font-size:25px;"
                + "color:black' + '\">{y}</span>"
                + "<span style=\"font-size:22px;color:#000\"> %</span></div>");

        item.setDataLabels(dataLabelsSeries);

        series.add(item);

        chartConfig.addSeries(series);



        return scoreChart;

    }

    private HorizontalLayout optionsLayoutPendingSignUpsGrid(Grid<userInEvent> grid , Event event, userInEvent user){
        Button rejectRequest = new Button("Reject");
        Button approveRequest = new Button("Approve");

        approveRequest.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        rejectRequest.getStyle().set("background-color" , "var(--lumo-error-color)");
        rejectRequest.getStyle().set("color" , "#fff");


        rejectRequest.addClickListener(buttonClickEvent -> {/*
           Database.updateJoinEventStatus(user , "rejected");*/

            dataService.updateJoinRequestStatus(user , "rejected");
            grid.setItems(dataService.getAllRegisteredInEventWithStatus(event.getEventName() , user.getStatus()));
            reloadEvents();
            /*
           Database.updateJoinEventStatus(user , "rejected");
           grid.setItems(Database.getPendingRequestsInEvent(event.getEventName()));*/
        });

        approveRequest.addClickListener(buttonClickEvent -> {/*
            Database.updateJoinEventStatus(user , "approved");*/

            dataService.updateJoinRequestStatus(user , "approved");
            grid.setItems(dataService.getAllRegisteredInEventWithStatus(event.getEventName() , user.getStatus()));
            reloadEvents();
            /*
            Database.updateJoinEventStatus(user , "approved");
            grid.setItems(Database.getPendingRequestsInEvent(event.getEventName()));*/
        });

        if(user.getStatus().equals("rejected")) rejectRequest.setVisible(false);
        else if(user.getStatus().equals("approved")) approveRequest.setVisible(false);

        HorizontalLayout optionsLayout = new HorizontalLayout(approveRequest , rejectRequest);

        return optionsLayout;

    }

    private Grid<userInEvent> getSignUpsGrid(Event event , String gridType){
        /*ArrayList<userInEvent> usersInEventsList = Database.getAllRegisteredInEventWithStatus(event , gridType);
*/

        List<userInEvent> usersInEventsList = dataService.getAllRegisteredInEventWithStatus(event.getEventName() , gridType);

        Grid<userInEvent> grid = new Grid(userInEvent.class);

        ListDataProvider<userInEvent> dataProvider = new ListDataProvider<>(usersInEventsList);

        grid.setDataProvider(dataProvider);

        grid.setSizeFull();
        grid.removeAllColumns();

        Grid.Column<userInEvent> FirstNameColumn;
        Grid.Column<userInEvent> LastNameColumn;
        Grid.Column<userInEvent> RoleColumn;
        Grid.Column<userInEvent> statusColumn;

        FirstNameColumn = grid.addColumn(userInEvent::getFirstName).setHeader("First Name");
        LastNameColumn = grid.addColumn(userInEvent::getLastName).setHeader("Last Name");
        RoleColumn = grid.addColumn(userInEvent::getRole).setHeader("Role");

        statusColumn = grid.addComponentColumn(userInEvent -> {
            Span span = new Span();
            span.setText(((userInEvent.getStatus())));

            if(userInEvent.getStatus().equals("pending")){
                span.getElement().setAttribute("theme", "badge");
            }else if(userInEvent.getStatus().equals("rejected")){
                span.getElement().setAttribute("theme", "badge error");
            }else{
                span.getElement().setAttribute("theme" , "badge success");
            }
            return span;
        }).setHeader("Status");

        addFiltersToSignUpGrid(event , grid , dataProvider , FirstNameColumn , LastNameColumn , RoleColumn);


        switch(gridType){
            case "pending":
                grid.addComponentColumn(user1 -> optionsLayoutPendingSignUpsGrid(grid , event , user1)).setHeader("Actions");
                break;
            case "rejected":
                grid.addComponentColumn(user1 -> optionsLayoutPendingSignUpsGrid(grid , event , user1)).setHeader("Actions");
                break;
            case "approved":
                grid.addComponentColumn(user1 -> optionsLayoutPendingSignUpsGrid(grid , event , user1)).setHeader("Actions");
                break;
            default :
                break;
        }

        return grid;

    }

    private void addFiltersToSignUpGrid(Event event , Grid<userInEvent> grid1 , ListDataProvider<userInEvent> dataProvider1,Grid.Column<userInEvent> FirstNameColumn , Grid.Column<userInEvent> LastNameColumn , Grid.Column<userInEvent> RoleColumn) {
        HeaderRow filterRow = grid1.appendHeaderRow();

        TextField firstNameFilter = new TextField();
        firstNameFilter.setPlaceholder("Filter");
        firstNameFilter.setClearButtonVisible(true);
        firstNameFilter.setWidth("100%");
        firstNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        firstNameFilter.addValueChangeListener(userInEvent -> dataProvider1.addFilter(
                userInEvent1 -> StringUtils.containsIgnoreCase(userInEvent1.getFirstName(), firstNameFilter.getValue())));
        filterRow.getCell(FirstNameColumn).setComponent(firstNameFilter);

        TextField lastNameFilter = new TextField();
        lastNameFilter.setPlaceholder("Filter");
        lastNameFilter.setClearButtonVisible(true);
        lastNameFilter.setWidth("100%");
        lastNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        lastNameFilter.addValueChangeListener(userInEvent -> dataProvider1.addFilter(
                userInEvent1 -> StringUtils.containsIgnoreCase(userInEvent1.getLastName(), lastNameFilter.getValue())));
        filterRow.getCell(LastNameColumn).setComponent(lastNameFilter);

        ArrayList<String> roleNames = new ArrayList<>();

        for(Role r : event.getRoles()){
            roleNames.add(r.getRoleName());
        }

        ComboBox<String> roleFilter = new ComboBox<>();
        roleFilter.setItems(roleNames);
        roleFilter.setPlaceholder("Filter");
        roleFilter.setClearButtonVisible(true);
        roleFilter.setWidth("100%");
        roleFilter.addValueChangeListener(
                userInEvent -> dataProvider1.addFilter(userInEvent1 -> isRoleEqual(userInEvent1.getRole() , roleFilter)));
        filterRow.getCell(RoleColumn).setComponent(roleFilter);

    }

    private boolean isRoleEqual(String role, ComboBox<String> roleFilter){
        if(roleFilter.isEmpty()){
            return true;
        }

        return StringUtils.equals(role , roleFilter.getValue());

    }




}

