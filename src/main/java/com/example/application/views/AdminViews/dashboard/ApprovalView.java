package com.example.application.views.AdminViews.dashboard;

import com.example.application.Entities.Event.Event;
import com.example.application.Entities.Event.Request;
import com.example.application.Entities.User.userInEvent;
import com.example.application.Resources.Extras.MessageRunnable;
import com.example.application.Resources.Services.DataService;
import com.example.application.Resources.Extras.Util;
import com.example.application.Role;
import com.example.application.Components.HDivider;
import com.example.application.views.AdminViews.Main.AdminMainView;
import com.vaadin.annotations.HtmlImport;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "pending_approvals" , layout = AdminMainView.class)
@CssImport(value = "styles/views/requests.css", include = "lumo-badge")
@PageTitle("Approvals Pending")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@HtmlImport(value = "styles/dialogpadding.html")
public class ApprovalView extends VerticalLayout {

    private Grid<Request> requestGrid;

    private Tabs requestsTabs;

    private Map<String , String> tabLabelToStatus;

    ArrayList<Request> pendingApprovals;
    ArrayList<Request> rejected;

    private Span numPendingApproval;
    private Span numApproved;
    private Span numRejected;

    DataService dataService = new DataService();



    public ApprovalView(){
        addClassName("fullPage");
        setSizeFull();

        requestGrid = new Grid<>();
        requestGrid.setSizeFull();
        getStyle().set("background" , "#fff");
        requestGrid.getStyle().set("background" , "#fff");


        Span noRequests = new Span();
        noRequests.addClassName("noRequestsSpan");



        tabLabelToStatus = new HashMap<>();
        tabLabelToStatus.put("Pending Approval" , "pending");
        tabLabelToStatus.put("Rejected" , "rejected");
        tabLabelToStatus.put("Approved" , "approved");


        configureTabs();
/*
        requestGrid.setItems(Database.getAllRequestsWithStatus("pending"));*/
        requestGrid.setItems(dataService.getAllRequestsWithStatus("pending"));
        requestGrid.addComponentColumn(request -> {
            return createRequestCard(request);
        });

        add(requestsTabs , requestGrid);


    }

    private void configureTabs(){
        requestsTabs = new Tabs();
        requestsTabs.setWidthFull();

        for(Tab tab : getAllNavTabs()){
            if(tab.getLabel().equals("PendingApprovals")){
                requestsTabs.setSelectedTab(tab);
            }
            requestsTabs.add(tab);
        }

        requestsTabs.addSelectedChangeListener(selectedChangeEvent -> {
            requestGrid.setItems(dataService.getAllRequestsWithStatus(tabLabelToStatus.get(requestsTabs.getSelectedTab().getLabel())));
/*
            requestGrid.setItems(Database.getAllRequestsWithStatus(tabLabelToStatus.get(requestsTabs.getSelectedTab().getLabel())));*/
        });
    }

    private ArrayList<Tab> getAllNavTabs(){
        Tab PendingApprovals = new Tab("Pending Approval");
        Tab Rejected = new Tab("Rejected");
        Tab Approved = new Tab("Approved");

/*
        numPendingApproval = new Span(String.valueOf(Database.getAllRequestsWithStatus("pending").size()));
        numApproved = new Span(String.valueOf(Database.getAllRequestsWithStatus("approved").size()));
        numRejected = new Span(String.valueOf(Database.getAllRequestsWithStatus("rejected").size()));*/

        numPendingApproval = new Span(String.valueOf(dataService.getAllRequestsWithStatus("pending").size()));
        numApproved = new Span(String.valueOf(dataService.getAllRequestsWithStatus("approved").size()));
        numRejected = new Span(String.valueOf(dataService.getAllRequestsWithStatus("rejected").size()));

        numPendingApproval.addClassName("requestsNotif");
        numApproved.addClassName("requestsNotif");
        numRejected.addClassName("requestsNotif");

        PendingApprovals.add(numPendingApproval);
        Rejected.add(numRejected);
        Approved.add(numApproved);

        ArrayList<Tab> tabs = new ArrayList<>();
        tabs.add(PendingApprovals);
        tabs.add(Rejected);
        tabs.add(Approved);

        return tabs;
    }

    private HorizontalLayout createRequestCard(Request request){
        request.setYearBound(Util.getYearBound());

        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setWidthFull();/*
        card.setMinHeight("222px");
        card.setMaxHeight("222px");*/
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        Button pendingApproval = new Button("Status : " + request.getStatus());
        pendingApproval.setHeight("20px");

        switch(request.getStatus()){
            case "Pending Approval":
                pendingApproval.getStyle().set("background-color" , "orange");
                break;
            case "Rejected":
                pendingApproval.getStyle().set("background-color" , "var(--lumo-error-color)");
                break;
            case "Approved":
                pendingApproval.getStyle().set("background-color" , "var(--lumo-success-color)");
                break;
            default:
                pendingApproval.getStyle().set("background-color" , "orange");
                break;
        }
        pendingApproval.getStyle().set("color" , "var(--lumo-base-color)");
        pendingApproval.getStyle().set("opacity" , "0.65");
        pendingApproval.getStyle().set("border-radius" , "1.0em");
        pendingApproval.getStyle().set("font-size" , "10px");

        Button role = new Button("Role in event : " + request.getRole());
        role.setHeight("20px");
        role.getStyle().set("background-color" , "var(--lumo-primary-color)");
        role.getStyle().set("color" , "var(--lumo-base-color)");
        role.getStyle().set("opacity" , "0.65");
        role.getStyle().set("border-radius" , "1.0em");
        role.getStyle().set("font-size" , "10px");

        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span name = new Span(request.getFirstName() + " "+ request.getLastName());
        name.addClassName("name");
        Span date = new Span("Jan 24 , 2020");

        Span eventName = new Span("Event : " + request.getEventAttended());
        eventName.addClassName("post");

        Span reward;
        reward = request.getEventType().equals("Outreach Event") ? new Span("Hours Earned : " + request.getRewardEarned()) : new Span("Money raised : " + request.getRewardEarned() + "$");
        reward.addClassName("post");

        Span email = new Span("Volunteer's email : " + request.getEmail());
        email.addClassName("post");

        HorizontalLayout info = new HorizontalLayout(eventName , reward);
        info.setPadding(false);
        info.setMargin(false);
        info.setAlignItems(Alignment.CENTER);
        info.setWidthFull();

        date.addClassName("date");
        header.add(name, date , role , pendingApproval);

        Span desc = new Span("Comments : " + request.getComment());
        desc.addClassName("post");

        Button approve = new Button("Approve");
        approve.getStyle().set("background-color" , "var(--lumo-success-color)");
        approve.getStyle().set("color" , "var(--lumo-base-color)");
        approve.getStyle().set("font-size" , "15px");
        approve.getStyle().set("margin-left" , "auto");
        approve.setHeight("35px");
        approve.setWidth("175px");

        Button deny = new Button("Deny");
        deny.getStyle().set("background-color" , "var(--lumo-error-color)");
        deny.getStyle().set("color" , "var(--lumo-base-color)");
        deny.getStyle().set("color" , "white");
        deny.getStyle().set("font-size" , "15px");
        deny.getStyle().set("margin-left" , "auto");
        deny.setHeight("35px");
        deny.setWidth("175px");

        approve.addClickListener(buttonClickEvent -> {
            createAndOpenApprovalWindow(request);
        });

        deny.addClickListener(buttonClickEvent -> {
           createAndOpenRejectionWindow(request);
        });

        VerticalLayout buttonsLay = new VerticalLayout(approve , deny);
        buttonsLay.setMargin(false);
        buttonsLay.setPadding(false);
        buttonsLay.getStyle().set("margin-left" , "auto");
        buttonsLay.getStyle().set("margin-top" , "auto");
        buttonsLay.getStyle().set("margin-bottom" , "auto");
        buttonsLay.setAlignItems(Alignment.CENTER);

        Span moreDetails = new Span();
        moreDetails.setText("View More Details");
        moreDetails.getStyle().set("font-size" , "16px");
        moreDetails.getStyle().set("color" , "var(--lumo-primary-color");
        moreDetails.addClassName("request-span");

        Span eventDetails = new Span();
        eventDetails.setText("View Event Info");
        eventDetails.getStyle().set("font-size" , "16px");
        eventDetails.getStyle().set("color" , "var(--lumo-primary-color");
        eventDetails.addClassName("request-span");

        moreDetails.addClickListener(spanClickEvent -> {
            Dialog proofImageDialog = new Dialog();

            H5 proofImageHeader = new H5("Proof of attendance");
            proofImageHeader.getStyle().set("margin-left" , "auto !important");
            proofImageHeader.getStyle().set("margin-right" , "auto !important");

            proofImageDialog.add(proofImageHeader , new Hr());
            proofImageDialog.add(request.getProofImage());

            proofImageDialog.open();
        });

        eventDetails.addClickListener(spanClickEvent -> {/*
            Event event = Database.loadEventByName(request.getEventAttended());*/

            Event event = dataService.loadEventByName(request.getEventAttended());
            createInfoWindow(event);
        });


        HorizontalLayout anchors = new HorizontalLayout(eventDetails , moreDetails);
        anchors.setMargin(false);
        anchors.setPadding(false);

        description.setMargin(false);
        description.setPadding(false);

        if(request.getStatus().equals("rejected")){
            deny.setVisible(false);
        }else if(request.getStatus().equals("approved")){
            approve.setVisible(false);
        }

        description.add(header, info , desc, email , anchors);
        description.setWidthFull();
        //HorizontalLayout card = new HorizontalLayout();
        card.add(description, buttonsLay);
        card.setWidthFull();
        return card;
    }

    private void createInfoWindow(Event event){
        Dialog infoWindow = new Dialog();
        infoWindow.setWidthFull();
        infoWindow.setHeight("75%");


        Label windowLabel = new Label("Middleton Robotics Event : " + event.getEventName());
        HorizontalLayout labelLay = new HorizontalLayout(windowLabel);
        labelLay.setAlignItems(Alignment.CENTER);
        labelLay.setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout windowLabellay = new VerticalLayout(labelLay , new HDivider());
        windowLabellay.setAlignItems(Alignment.CENTER);
        windowLabellay.setMargin(false);
        windowLabellay.setPadding(false);

        Grid<userInEvent> grid = new Grid<>(userInEvent.class);/*
        ArrayList<userInEvent> usersInEvent = Database.getAllRegisteredInEvent(event);*/

        List<userInEvent> usersInEvent = dataService.getAllRegisteredInEvent(event.getEventName());
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
        AddressLay.setAlignItems(Alignment.CENTER);
        AddressLay.setSpacing(true);

        HorizontalLayout availableSlotsLay = new HorizontalLayout(availableSlots);
        AddressLay.setAlignItems(Alignment.CENTER);
        availableSlotsLay.setSpacing(true);

        HorizontalLayout timingsLay = new HorizontalLayout(Timings);
        AddressLay.setAlignItems(Alignment.CENTER);
        timingsLay.setSpacing(true);

        HorizontalLayout mainInfo = new HorizontalLayout(Address , emptySpace , Timings);
        //HorizontalLayout mainInfo = new HorizontalLayout(AddressLay , timingsLay);
        mainInfo.setAlignItems(Alignment.CENTER);


        HorizontalLayout rolesCard = new HorizontalLayout(createRolesCard(event.getRoles()));
        rolesCard.setAlignItems(Alignment.CENTER);


        HorizontalLayout placeTimingSlots = new HorizontalLayout();
        placeTimingSlots.add(availableSlots , emptySpace , mainInfo);
        placeTimingSlots.setWidthFull();
        placeTimingSlots.setAlignItems(Alignment.CENTER);

        rolesCard.setWidthFull();

        rolesCard.getElement().getStyle().set("width" , "100%");
        rolesCard.getElement().getStyle().set("height" , "auto");
        rolesCard.getElement().getStyle().set("margin-top" , "auto");

        information.add(placeTimingSlots , description,  rolesCard );

        HorizontalLayout gridWithInfo = new HorizontalLayout(grid , information);
        gridWithInfo.setJustifyContentMode(JustifyContentMode.CENTER);


        VerticalLayout labelAndGrid = new VerticalLayout(windowLabellay , gridWithInfo);
        labelAndGrid.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout layoutAligner = new HorizontalLayout(labelAndGrid);
        layoutAligner.setJustifyContentMode(JustifyContentMode.CENTER);

        layoutAligner.getStyle().set("margin", "auto");
        layoutAligner.getStyle().set("width", "auto");
        layoutAligner.getStyle().set("display", "block");

        infoWindow.add(layoutAligner);
        infoWindow.open();
    }

    private VerticalLayout createRolesCard(ArrayList<Role> roles){
        VerticalLayout card = new VerticalLayout();

        card.setSpacing(false);

        Span cardLabel = new Span("Roles");
        cardLabel.getStyle().set("font-weight", "bold");
        HDivider HR = new HDivider();

        VerticalLayout Header = new VerticalLayout(cardLabel , HR);
        Header.setAlignItems(Alignment.CENTER);
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
        headerWithRoles.setAlignItems(Alignment.CENTER);
        headerWithRoles.setJustifyContentMode(JustifyContentMode.CENTER);

        card.add(Header , lists);
        card.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        return card;

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

    private void createAndOpenRejectionWindow(Request request){
        Dialog RejectionWindow = new Dialog();

        Label reason = new Label("Reason for rejection: ");
        reason.getStyle().set("font-weight" , "bold");
        reason.getStyle().set("color" , "var(--lumo-error-color)");

        Span desc = new Span("If you reject the hours/fundraising money, an email will be sent to the volunteer to update the submission. The message below will be sent to the volunteer's email");
        desc.getStyle().set("font-style" , "italic");
        desc.getStyle().set("font-size" , "15px");
        desc.getStyle().set("text-align" , "left");

        Span fromLabel = new Span("From : ");
        fromLabel.addClassName("fromLabel");

        TextField fromEmail = new TextField();
        fromEmail.setValue("gamers2333@gmail.com");
        fromEmail.setReadOnly(true);
        fromEmail.addClassName("fromEmailField");

        Button overrideDefaultEmailSender = new Button("Override");
        overrideDefaultEmailSender.addClassName("overrideDefaultEmailSender");

        overrideDefaultEmailSender.setIconAfterText(true);

        overrideDefaultEmailSender.addClickListener(buttonClickEvent -> {
           fromEmail.setReadOnly(false);
        });

        overrideDefaultEmailSender.setIcon(new Icon(VaadinIcon.KEY));

        HorizontalLayout fromLayout = new HorizontalLayout(fromLabel , fromEmail , overrideDefaultEmailSender);
        fromLayout.addClassName("fromLayout");

        Span toLabel = new Span("To : ");
        toLabel.addClassName("toLabel");

        TextField toEmail = new TextField();
        toEmail.setValue(request.getEmail());
        toEmail.setReadOnly(true);
        toEmail.setWidthFull();

        HorizontalLayout toLayout = new HorizontalLayout(toLabel , toEmail);
        toLayout.addClassName("toLayout");

        Span subjectLabel = new Span("Subject : ");
        subjectLabel.addClassName("subjectLabel");

        TextField subjectField = new TextField();
        subjectField.setValue("Middleton Robotics outreach : Request for " + request.getEventAttended());
        subjectField.setReadOnly(true);
        subjectField.addClassName("subjectField");

        Button change = new Button("Edit");
        change.addClickListener(buttonClickEvent -> {
           subjectField.setReadOnly(false);
        });

        change.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        change.addClassName("changeSubjectButton");

        HorizontalLayout subjectLayout = new HorizontalLayout(subjectLabel , subjectField , change);
        subjectLayout.addClassName("subjectLayout");

        RichTextEditor emailMessage = new RichTextEditor();


        Button rejectSend = new Button("Reject and send");
        rejectSend.getStyle().set("background-color" , "var(--lumo-error-color)");
        rejectSend.getStyle().set("color" , "var(--lumo-base-color)");
        rejectSend.getStyle().set("opacity" , "0.85");



        rejectSend.setWidthFull();

        rejectSend.addClickListener(buttonClickEvent -> {
          if(!emailMessage.isEmpty()) {
              Thread emailThread = new Thread(new MessageRunnable(toEmail.getValue(), fromEmail.getValue(),
                      "RDasari#04", subjectField.getValue(), emailMessage.getHtmlValue()));
              emailThread.start();
              dataService.updateRequestData(request , "rejected");

              RejectionWindow.close();

              String pendingReqNum = String.valueOf(dataService.getAllRequestsWithStatus("pending").size());


              numPendingApproval.setText(pendingReqNum);
              numApproved.setText(String.valueOf(dataService.getAllRequestsWithStatus("approved").size()));
              numRejected.setText(String.valueOf(dataService.getAllRequestsWithStatus("rejected").size()));

              requestGrid.setItems(dataService.getAllRequestsWithStatus(requestsTabs.getSelectedTab().getLabel()));


              if(request.getStatus().equals("pending")) {
                  AdminMainView.pendingRequests.setText(pendingReqNum);
                  AdminMainView.pendingRequests.setVisible(Integer.parseInt(pendingReqNum) == 0 ? false : true);
              }

          }else{
              Notification.show("Enter a message before proceeding to reject and send the email.");

          }

        });

        VerticalLayout rejectionLay = new VerticalLayout();
        rejectionLay.add(reason , desc , fromLayout , toLayout , subjectLayout ,  emailMessage , rejectSend);
        rejectionLay.setMargin(false);
        rejectionLay.setPadding(false);

        RejectionWindow.add(rejectionLay);
        RejectionWindow.open();

    }

    private void createAndOpenApprovalWindow(Request request){
        Dialog approvalWindow = new Dialog();

        Label reason = new Label("Reason for approval: ");
        reason.getStyle().set("font-weight" , "bold");
        reason.getStyle().set("color" , "var(--lumo-success-color)");

        Span desc = new Span("Approving the request will send a notification email to the volunteer.");
        desc.getStyle().set("font-style" , "italic");
        desc.getStyle().set("font-size" , "15px");
        desc.getStyle().set("text-align" , "left");

        Span fromLabel = new Span("From : ");
        fromLabel.addClassName("fromLabel");

        TextField fromEmail = new TextField();
        fromEmail.setValue("gamers2333@gmail.com");
        fromEmail.setReadOnly(true);
        fromEmail.addClassName("fromEmailField");

        Button overrideDefaultEmailSender = new Button("Override");
        overrideDefaultEmailSender.addClassName("overrideDefaultEmailSender");

        overrideDefaultEmailSender.setIconAfterText(true);

        overrideDefaultEmailSender.addClickListener(buttonClickEvent -> {
            fromEmail.setReadOnly(false);
        });

        overrideDefaultEmailSender.setIcon(new Icon(VaadinIcon.KEY));

        HorizontalLayout fromLayout = new HorizontalLayout(fromLabel , fromEmail , overrideDefaultEmailSender);
        fromLayout.addClassName("fromLayout");

        Span toLabel = new Span("To : ");
        toLabel.addClassName("toLabel");

        TextField toEmail = new TextField();
        toEmail.setValue(request.getEmail());
        toEmail.setReadOnly(true);
        toEmail.setWidthFull();

        HorizontalLayout toLayout = new HorizontalLayout(toLabel , toEmail);
        toLayout.addClassName("toLayout");

        Span subjectLabel = new Span("Subject : ");
        subjectLabel.addClassName("subjectLabel");

        TextField subjectField = new TextField();
        subjectField.setValue("Middleton Robotics outreach : Request for " + request.getEventAttended() + " approved");
        subjectField.setReadOnly(true);
        subjectField.addClassName("subjectField");

        Button change = new Button("Edit");
        change.addClickListener(buttonClickEvent -> {
            subjectField.setReadOnly(false);
        });

        change.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        change.addClassName("changeSubjectButton");

        HorizontalLayout subjectLayout = new HorizontalLayout(subjectLabel , subjectField , change);
        subjectLayout.addClassName("subjectLayout");

        RichTextEditor emailMessage = new RichTextEditor();


        Button approveSend = new Button("Appprove and send");
        approveSend.getStyle().set("background-color" , "var(--lumo-success-color)");
        approveSend.getStyle().set("color" , "var(--lumo-base-color)");
        approveSend.getStyle().set("opacity" , "0.85");



        approveSend.setWidthFull();

        approveSend.addClickListener(buttonClickEvent -> {
            if(!emailMessage.isEmpty()) {
                Thread emailThread = new Thread(new MessageRunnable(toEmail.getValue(), fromEmail.getValue(),
                        "RDasari#04", subjectField.getValue(), emailMessage.getHtmlValue()));
                emailThread.start();

                dataService.updateRequestData(request , "approved");
                approvalWindow.close();

                String pendingReqNum = String.valueOf(dataService.getAllRequestsWithStatus("pending").size());

                numPendingApproval.setText(pendingReqNum);
                numApproved.setText(String.valueOf(dataService.getAllRequestsWithStatus("approved").size()));
                numRejected.setText(String.valueOf(dataService.getAllRequestsWithStatus("rejected").size()));


                requestGrid.setItems(dataService.getAllRequestsWithStatus(tabLabelToStatus.get(requestsTabs.getSelectedTab().getLabel())));

                if(request.getStatus().equals("pending")) {

                    AdminMainView.pendingRequests.setText(pendingReqNum);
                    AdminMainView.pendingRequests.setVisible(Integer.parseInt(pendingReqNum) == 0 ? false : true);
                }

            }else{
                Notification.show("Enter a message before proceeding to reject and send the email.");

            }

        });

        VerticalLayout approvalLay = new VerticalLayout();
        approvalLay.add(reason , desc , fromLayout , toLayout , subjectLayout ,  emailMessage , approveSend);
        approvalLay.setMargin(false);
        approvalLay.setPadding(false);

        approvalWindow.add(approvalLay);
        approvalWindow.open();

    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorDiv = new Div();
        editorDiv.setId("editor-layout");
        splitLayout.addToSecondary(editorDiv);
    }




}
