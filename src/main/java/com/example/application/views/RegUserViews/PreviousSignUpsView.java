package com.example.application.views.RegUserViews;

import com.example.application.Components.HDivider;
import com.example.application.Entities.Event.Event;
import com.example.application.Entities.Event.Request;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Route(value = "previous_sign_ups", layout = StudentMainView.class)
@PageTitle("Previous Sign Up Page")
@CssImport(value = "styles/views/dashboard/dashView.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class PreviousSignUpsView extends Div {
    Grid<Event> grid = new Grid<>();
    ArrayList<Event> eventList;
    byte[] imageBytes = null;

    DataService dataService = new DataService();
    public PreviousSignUpsView(){
        setId("dashboard-view");
        addClassName("dashboard-view");
        setSizeFull();

        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        eventList = new ArrayList<>();
        //eventList.addAll(DB.getAllEvents());
        eventList.addAll(dataService.getEventsByName(LoggedInUserDetails.getFirstName(),
                LoggedInUserDetails.getLastName()));
        grid.setItems(eventList);
        grid.addComponentColumn(this::createEventCard);

            add(grid);

    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    private Request isRequestPresent(String eventName){
        List<Request> requests = dataService.getAllRequests();

        for(Request request : requests){
            if(request.getEventAttended().toLowerCase().equals(eventName.toLowerCase())){
                return request;
            }
        }
        return null;
    }

    private HorizontalLayout createEventCard(Event event){
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setWidthFull();
        card.setMinHeight("195px");
        card.setMaxHeight("195px");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");


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


        userInEvent currentUser = dataService.getUserFromEvent(LoggedInUserDetails.getFirstName(),
                LoggedInUserDetails.getLastName(), event.getEventName());

        //ADD ROLE BUTTON THING TO DASHBOARD VIEW-EVENT CARDS SO IT LOOKS BETTER
        Button role = new Button("Role: " + (currentUser != null ? currentUser.getRole() : "Undefined"));
        role.setHeight("20px");
        role.getStyle().set("background-color" , "var(--lumo-primary-color)");
        role.getStyle().set("color" , "var(--lumo-base-color)");
        role.getStyle().set("opacity" , "0.75");
        role.getStyle().set("border-radius" , "1.0em");
        role.getStyle().set("font-size" , "10px");

        Button status = new Button("");
        status.setHeight("20px");
        status.getStyle().set("background-color" , "var(--lumo-primary-color)");
        status.getStyle().set("color" , "#fff");
        status.getStyle().set("opacity" , "0.75");
        status.getStyle().set("border-radius" , "1.0em");
        status.getStyle().set("font-size" , "10px");
        status.setVisible(false);

        Icon requestIcon = new Icon(VaadinIcon.PLUS);
        requestIcon.setSize("30px");
        requestIcon.setColor("white");

        Button requestBtn = new Button("Request");
        requestBtn.setIcon(requestIcon);
        requestBtn.setIconAfterText(true);
        requestBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        requestBtn.setWidth("200px");

        Request request = isRequestPresent(event.getEventName());

        if(request != null){
            status.setText("Status: " + request.getStatus());

            switch(request.getStatus().toLowerCase()) {
                case "approved":
                    status.getStyle().set("background-color", "var(--lumo-success-color)");
                    break;
                case "rejected":
                    status.getStyle().set("background-color", "var(--lumo-error-color)");
                    break;
                case "pending":
                    status.getStyle().set("background-color", "gold");
                    break;
            }

            status.getStyle().set("color", "black");
            status.setVisible(true);
            requestBtn.setText("Cancel Request");
            Icon cancelIcon = new Icon(VaadinIcon.CLOSE);
            cancelIcon.setColor("white");
            cancelIcon.setSize("30px");
            requestBtn.setIcon(cancelIcon);
            requestBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            //Utils.notify("Request made");
        }

        String eventProgressText = "";
        String eventProgressColor = "";

        boolean datePassedStarting = Util.datePassed(Util.stringToDate(event.getStartingDate()), Util.stringToTime(event.getStartingTime()));
        boolean datePassedEnd = Util.datePassed(Util.stringToDate(event.getEndingDate()), Util.stringToTime(event.getEndTime()));

        if(datePassedEnd) {
            eventProgressText = "Event has finished";
            eventProgressColor = "var(--lumo-success-color)";

            requestBtn.addClickListener(e -> {
                if (!status.isVisible()) {/*
                createAddEventWindow(event, status, requestBtn);*/


                    createRequestWindow(event,
                            currentUser,
                            status, requestBtn);
                } else {
                    if (dataService.deleteRequest(request)) {
                        status.setVisible(false);
                        requestBtn.setText("Request");
                        requestBtn.setIcon(requestIcon);
                        requestBtn.removeThemeVariants(ButtonVariant.LUMO_ERROR);
                        requestBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                    }
                }
            });
        }else{
            requestBtn.setEnabled(false);

            String buttonText = "";
            String color = "";
        }

        if(datePassedStarting && !datePassedEnd) {
            eventProgressText = "Event in progress";
            eventProgressColor = "orange";
        } else if (!datePassedStarting){
            eventProgressText = "Event has not started";
            eventProgressColor = "var(--lumo-error-color)";
        }

        Button eventProgress = new Button(eventProgressText);
        eventProgress.setHeight("20px");
        eventProgress.getStyle().set("background-color" , eventProgressColor);
        eventProgress.getStyle().set("color" , "var(--lumo-base-color)");
        eventProgress.getStyle().set("opacity" , "0.75");
        eventProgress.getStyle().set("border-radius" , "1.0em");
        eventProgress.getStyle().set("font-size" , "10px");

        header.add(name, date, role ,eventProgress, status);


        Span desc = new Span(event.getDescription());
        desc.addClassName("post");

        VerticalLayout info = new VerticalLayout(header, desc);

        Icon moreInfosetIcon = new Icon(VaadinIcon.INFO_CIRCLE);
        moreInfosetIcon.setSize("30px");
        moreInfosetIcon.setColor("white");

        Button moreInfoBtn = new Button("More Info");
        moreInfoBtn.setIcon(moreInfosetIcon);
        moreInfoBtn.setIconAfterText(true);
        moreInfoBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        moreInfoBtn.setWidth("200px");
        moreInfoBtn.addClickListener(e->createInfoWindow(event));

        Span viewPicBtn = new Span("View Request Proof");
        viewPicBtn.getStyle().set("font-weight", "bold");
        viewPicBtn.getStyle().set("text-decoration", "underline");
        viewPicBtn.getStyle().set("color", "blue");
        viewPicBtn.getStyle().set("font-size", "11px");
        viewPicBtn.addClickListener(e->{
            openProofWindow(isRequestPresent(event.getEventName()));
        });
        info.add(viewPicBtn);

        moreInfoBtn.getStyle().set("margin-left", "auto");
        requestBtn.getStyle().set("margin-left", "auto");


        VerticalLayout btns = new VerticalLayout(moreInfoBtn, requestBtn);

        btns.getStyle().set("margin-top", "auto");
        btns.getStyle().set("margin-bottom", "auto");

        card.add(info, btns);

        return card;
    }

    private void openProofWindow(Request request) {
        Dialog window = new Dialog();
        window.setCloseOnOutsideClick(true);
        if(request != null){
            window.add(request.getProofImage());
            window.open();
        }
        else Util.notify("Failed to show proof");
    }

    private void createInfoWindow(Event event){
        Dialog infoWindow = new Dialog();
        infoWindow.setMaxHeight("500px");
        infoWindow.setMaxWidth("1200px");


        Label windowLabel = new Label("Middleton Robotics Event : " + event.getEventName());
        HorizontalLayout labelLay = new HorizontalLayout(windowLabel);
        labelLay.setAlignItems(FlexComponent.Alignment.CENTER);
        labelLay.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        VerticalLayout windowLabellay = new VerticalLayout(labelLay , new HDivider());
        windowLabellay.setAlignItems(FlexComponent.Alignment.CENTER);
        windowLabellay.setMargin(false);
        windowLabellay.setPadding(false);

        Grid<userInEvent> grid = new Grid<>(userInEvent.class);
        List<userInEvent> usersInEvent = dataService.getAllRegisteredInEvent(event.getEventName());
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


        HorizontalLayout rolesCard = new HorizontalLayout(createRolesCard(event.getRoles()));
        rolesCard.setAlignItems(FlexComponent.Alignment.CENTER);


        HorizontalLayout placeTimingSlots = new HorizontalLayout();
        placeTimingSlots.add(availableSlots , emptySpace , mainInfo);
        placeTimingSlots.setWidthFull();
        placeTimingSlots.setAlignItems(FlexComponent.Alignment.CENTER);

        rolesCard.setWidthFull();

        rolesCard.getElement().getStyle().set("width" , "100%");
        rolesCard.getElement().getStyle().set("height" , "auto");

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

    private VerticalLayout createRolesCard(ArrayList<Role> roles){
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
            list.add(roles.get(i).getRoleName() + " , ");
        }

        list.add(roles.get(roles.size() - 1).getRoleName());

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

    private void createAddEventWindow(Event event,
                                      Button status, Button request){
        Dialog addEventWindow = new Dialog();
        addEventWindow.setDraggable(true);
        addEventWindow.setCloseOnOutsideClick(true);
        addEventWindow.setHeightFull();
        addEventWindow.setWidth("875px");
        addEventWindow.setMaxWidth("875px");

        RadioButtonGroup<String> currentSteps = new RadioButtonGroup<>();
        currentSteps.setItems("Details" , "Finish");
        currentSteps.setValue("Details");

        Label currentStep = new Label("Details");
        currentStep.getStyle().set("font-weight" , "bold");
        currentStep.getStyle().set("font-size" , "22px");
        Image firstStepImg = new Image("icons/firstStep.png" , "First Step");
        firstStepImg.setHeight("55px");

        Image secondStepImg = new Image("images/logos/secondStep.png"  , "Second Step");
        secondStepImg.setHeight("55px");

        HorizontalLayout TopLayout = new HorizontalLayout(firstStepImg , currentStep , currentSteps);
        TopLayout.setWidthFull();
        TopLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        TopLayout.getStyle().set("margin-top" , "auto");
        TopLayout.getStyle().set("margin-bottom" , "auto");

        TextField eventName = new TextField();
        eventName.setLabel("Event Name");
        eventName.setValue(event.getEventName());
        eventName.setEnabled(false);
        eventName.setWidthFull();
        eventName.setRequired(true);
        eventName.setValueChangeMode(ValueChangeMode.EAGER);

        TextField Address = new TextField();
        Address.setLabel("Location");
        Address.setValue(event.getLocation());
        Address.setEnabled(false);
        Address.setWidthFull();
        Address.setRequired(true);
        Address.setValueChangeMode(ValueChangeMode.EAGER);


        DatePicker startingDate = new DatePicker();
        startingDate.setWidth("150px");

        TimePicker startingTime = new TimePicker();
        startingTime.setWidth("150px");

        DatePicker endingDate = new DatePicker();
        endingDate.setWidth("150px");
        //endingDate.setValue(event.getEndingDate());

        TimePicker endingTime = new TimePicker();
        endingTime.setWidth("150px");

        Label starting = new Label("Started");
        starting.getStyle().set("font-size" , "16px");
        Label ending = new Label("Ended");
        ending.getStyle().set("font-size" , "16px");

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

        HorizontalLayout fullInfo = new HorizontalLayout(startingWithLabel , endingWithLabel);
        fullInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout location = new VerticalLayout(Address);
        location.setMargin(false);
        location.setSpacing(false);
        location.setPadding(false);

        ComboBox<String> eventType = new ComboBox<>();
        eventType.setLabel("Event Type");
        eventType.setItems("Outreach Event" , "Fundraising");
        eventType.setValue(event.getEventType());
        eventType.setEnabled(false);

        NumberField reward = new NumberField();
        String rewardLabel = eventType.getValue() == "Fundraising" ? "Money raised" : "Hours earned";
        reward.setValue(event.getReward());
        reward.setEnabled(false);
        reward.setLabel(rewardLabel);
        reward.setEnabled(false);

        TextField roleInEvent = new TextField();
        roleInEvent.setValue("Camp Director");
        roleInEvent.setLabel("Role in event");
        roleInEvent.setEnabled(false);

        RichTextEditor commentBox = new RichTextEditor();
        commentBox.setWidthFull();

        addEventWindow.setCloseOnOutsideClick(false);

        Label descriptionLabel = new Label("Comments");

        VerticalLayout descriptionLayout = new VerticalLayout();
        descriptionLayout.add(descriptionLabel , commentBox);
        descriptionLayout.setSpacing(false);
        descriptionLayout.setMargin(false);
        descriptionLayout.setPadding(false);
        descriptionLayout.getStyle().set("margin-top" , "10px");

        //byte[] image = null;
        MemoryBuffer buffer = new MemoryBuffer();
        Upload imageUpload = new Upload(buffer);
        imageUpload.setMaxFileSize(500000);
        imageUpload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        imageUpload.setMaxFiles(1);
        imageUpload.setDropAllowed(true);
        imageUpload.addSucceededListener(e->{
            try {
                byte[] image = IOUtils.toByteArray(buffer.getInputStream());
                setImageBytes(image);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        });

        HorizontalLayout minorInfo = new HorizontalLayout(eventType , reward , roleInEvent );
        minorInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        minorInfo.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        minorInfo.setMargin(false);

        Button submitBtn = new Button("Submit Request");
        submitBtn.setIcon(new Icon(VaadinIcon.PLUS));
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitBtn.setIconAfterText(true);
        submitBtn.addClickListener(e->{
            if(!roleInEvent.isEmpty() && !reward.isEmpty() &&
                    !commentBox.isEmpty()) {
                String comment = commentBox.getHtmlValue();
                comment = comment.replaceAll("\\<.*?\\>" , "");
                Request requestForm = new Request(LoggedInUserDetails.getFirstName(), LoggedInUserDetails.getLastName(),
                        LoggedInUserDetails.getEmail(), roleInEvent.getValue(), commentBox.getValue(), reward.getValue(),
                        "Request Pending", event.getEventName(), event.getEventType()
                        , null);
                dataService.addRequest(requestForm, imageBytes);
//                Utils.notify(imageUpload.getI18n().getAddFiles().getOne());
                //DB.addRequest();
                addEventWindow.close();
                status.setText("Status: Pending Approval");
                status.getStyle().set("background-color", "gold");//*"var(--lumo-primary-color)"*//*);
                status.getStyle().set("color", "black");
                status.setVisible(true);
                request.setText("Cancel Request");
                Icon cancelIcon = new Icon(VaadinIcon.CLOSE);
                cancelIcon.setColor("white");
                cancelIcon.setSize("30px");
                request.setIcon(cancelIcon);
                request.addThemeVariants(ButtonVariant.LUMO_ERROR);
                Util.notify("Request made");
            }
            else{
                Util.notify("Please enter all information");
            }
        });
        imageUpload.getStyle().set("margin-right","auto");
        HorizontalLayout bottom = new HorizontalLayout(imageUpload, submitBtn);
        VerticalLayout fullLayout = new VerticalLayout(eventName , location , minorInfo , fullInfo ,
                descriptionLayout , bottom);
        submitBtn.getStyle().set("margin-left", "auto");

        fullLayout.setSpacing(false);
        fullLayout.setPadding(false);
        fullLayout.setMargin(false);
        reward.setEnabled(true);

        imageUpload.setMaxFileSize(500000);
        imageUpload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        imageUpload.setMaxFiles(1);
        imageUpload.setDropAllowed(true);
        imageUpload.addSucceededListener(e->{
            try {
                byte[] image = IOUtils.toByteArray(buffer.getInputStream());
                setImageBytes(image);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        });



        Dialog finishDialog = new Dialog();
        finishDialog.setVisible(false);


        addEventWindow.add(TopLayout , new Hr(), fullLayout , finishDialog);
        addEventWindow.open();
    }

//(currentUser != null ? currentUser.getRole() : "Undefined"
    private void createRequestWindow(Event event, userInEvent userInEvent,
                                     Button status, Button request){

        Dialog window = new Dialog();
        window.setHeightFull();

        H2 addHeader = new H2("Add Your Request");

        TextField eventName = new TextField("Event Name:");
        eventName.setValue(event.getEventName());

        ComboBox<String> eventType = new ComboBox<>("Event Type: ");
        eventType.setClearButtonVisible(true);
        eventType.setItems("Fundraising Event", "Outreach Event");
        eventType.setValue(event.getEventType());


        TextField date = new TextField("Event Date: ");
        date.setValue(event.getStartingDate() + " - " + event.getEndingDate());
        date.setEnabled(false);


        HorizontalLayout eventInfo = new HorizontalLayout(eventName, eventType , date);
        eventInfo.setEnabled(false);

        TextField role = new TextField("Role at Event: ");
        role.setValue(userInEvent.getRole());

        NumberField amount = new NumberField("Money Raised/Hours Earned: ");
        amount.setPlaceholder("Enter the amount you earned at the event");
        HorizontalLayout moreInfo = new HorizontalLayout(role ,  amount);

        /*DatePicker startDatePicker = new DatePicker("Start Date: ");
        DatePicker endDatePicker = new DatePicker("End Date: ");

        HorizontalLayout datePickers = new HorizontalLayout(startDatePicker, endDatePicker);
*/
        RichTextEditor commentBox = new RichTextEditor();
        commentBox.setWidthFull();





        VerticalLayout info = new VerticalLayout(addHeader, eventInfo, moreInfo
                /*datePickers,*/, commentBox);
        info.setAlignItems(FlexComponent.Alignment.CENTER);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload imageUpload = new Upload(buffer);
        imageUpload.setMaxFileSize(500000);
        imageUpload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        imageUpload.setMaxFiles(1);
        imageUpload.setDropAllowed(true);
        imageUpload.addSucceededListener(e->{
            try {
                byte[] image = IOUtils.toByteArray(buffer.getInputStream());
                setImageBytes(image);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        });

        Button submitBtn = new Button("Submit Request");
        submitBtn.setIcon(new Icon(VaadinIcon.PLUS));
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitBtn.setIconAfterText(true);
        submitBtn.addClickListener(e->{
            if(!role.isEmpty() && !amount.isEmpty() && !commentBox.isEmpty()) {
                Request requestForm = new Request(LoggedInUserDetails.getFirstName(), LoggedInUserDetails.getLastName(),
                        LoggedInUserDetails.getEmail(), userInEvent.getRole(), commentBox.getValue() , amount.getValue(),
                        "pending", event.getEventName(), event.getEventType() , Util.getYearBound() , null);
                window.close();
                status.setText("Status: Pending Approval");
                status.getStyle().set("background-color", "gold"/*"var(--lumo-primary-color)"*/);
                status.getStyle().set("color", "black");
                status.setVisible(true);
                request.setText("Cancel Request");
                Icon cancelIcon = new Icon(VaadinIcon.CLOSE);
                cancelIcon.setColor("white");
                cancelIcon.setSize("30px");
                request.setIcon(cancelIcon);
                request.addThemeVariants(ButtonVariant.LUMO_ERROR);
                dataService.addRequest(requestForm , imageBytes);
            }
            else{
                Util.notify("Please enter all information");
            }
        });

        HorizontalLayout bottom = new HorizontalLayout(new Span(),submitBtn);
        submitBtn.getStyle().set("margin-left", "auto");


        window.add(info, imageUpload, bottom);
        window.open();
    }

}
