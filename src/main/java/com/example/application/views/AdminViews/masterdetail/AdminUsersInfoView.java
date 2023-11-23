package com.example.application.views.AdminViews.masterdetail;

import com.example.application.Entities.User.studentDataEntity;
import com.example.application.Resources.Services.DataExporters.AllUsersDataExporter;
import com.example.application.Resources.Services.DataExporters.UserDataExporter;
import com.example.application.Resources.Services.DataService;
import com.example.application.views.AdminViews.Main.AdminMainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route(value = "student_data" , layout = AdminMainView.class)
@PageTitle("Student Data")
@CssImport("styles/views/dataView/dataView.css")
@JsModule("styles/shared-styles.js")

public class AdminUsersInfoView extends HorizontalLayout {

    private GridPro<studentDataEntity> grid;
    private List<studentDataEntity> studentDataEntities;

    private Grid.Column<studentDataEntity> firstNameColumn;
    private Grid.Column<studentDataEntity> lastNameColumn;
    private Grid.Column<studentDataEntity> outreacHoursColumn;
    private Grid.Column<studentDataEntity> fundraisingColumn;
    private Grid.Column<studentDataEntity> numOfEventsColumn;
    private Grid.Column<studentDataEntity> outreachStatusColumn;
    private Grid.Column<studentDataEntity> moneyRaisedStatusColumn;
    private Grid.Column<studentDataEntity> moreInfoColumn;
    private Grid.Column<studentDataEntity> exportColumn;

    private ListDataProvider<studentDataEntity> dataProvider;

    private AllUsersDataExporter allUsersDataExporter;

    DataService dataService = new DataService();

    public AdminUsersInfoView(){
        addClassName("dataView");
        setSizeFull();
        setMargin(false);
        setPadding(false);

        studentDataEntities = dataService.getAllStudentData();

        allUsersDataExporter = new AllUsersDataExporter();

        createGrid();

        VerticalLayout fullPageLayout = new VerticalLayout(createTopBar() , grid );
        fullPageLayout.setPadding(false);
        fullPageLayout.setSpacing(false);

        add(fullPageLayout);

    }

    private HorizontalLayout configureActionOptions(){
        Span actions = new Span("ACTIONS");
        actions.addClassName("actionLabel");

        Icon export = new Icon(VaadinIcon.UPLOAD);
        export.setSize("15px");

        Icon editIcon = new Icon(VaadinIcon.EDIT);
        editIcon.setSize("15px");

        HorizontalLayout editButton = createEditCard("Edit Hours / Fundraisied Money" , editIcon);

        HorizontalLayout actionOptions = new HorizontalLayout();
        actionOptions.addClassName("actionOptions");
        actionOptions.add(actions);
        allUsersDataExporter.exportUsers(actionOptions);
        actionOptions.add(editButton);
        actionOptions.setVerticalComponentAlignment(Alignment.CENTER);
        actionOptions.setAlignItems(Alignment.START);
        return actionOptions;

    }

    private VerticalLayout createTopBar(){
        VerticalLayout topBar = new VerticalLayout();
        topBar.addClassName("top-bar");

       /* HorizontalLayout searchBar = new HorizontalLayout();
        searchBar.setWidthFull();

        TextField filterBar = new TextField();
        filterBar.setPlaceholder("Search");
        filterBar.setWidthFull();

        Icon searchIcon = new Icon(VaadinIcon.SEARCH);
        searchIcon.setSize("25px");
        searchIcon.setColor("black");

        Button searchButton = new Button();
        searchButton.setIcon(searchIcon);
        searchButton.addClassName("search-bar-button");


        RadioButtonGroup<String> viewAs = new RadioButtonGroup<>();
        viewAs.setItems("View as a grid" , "View as cards");
        viewAs.setValue("View as a grid");
        viewAs.setRequired(true);
        viewAs.getStyle().set("width" , "100%");

        searchBar.add(filterBar , searchButton , viewAs);
        searchBar.setVerticalComponentAlignment(Alignment.CENTER);
*/
        topBar.add( configureActionOptions());
        return topBar;
    }

    private HorizontalLayout createExportButton(String buttonName , Icon icon){
        Span nameInCaps = new Span(buttonName.toUpperCase());
        nameInCaps.getStyle().set("font-size" , "15px");

        HorizontalLayout buttonCard = new HorizontalLayout();
        buttonCard.add(icon , nameInCaps);
        buttonCard.setSpacing(false);
        buttonCard.getThemeList().add("spacing-s");
        buttonCard.setAlignItems(Alignment.CENTER);
        buttonCard.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonCard.addClassName("buttonCard");
        return buttonCard;
    }

    private HorizontalLayout createEditCard(String buttonName , Icon icon){
        Span nameInCaps = new Span(buttonName.toUpperCase());
        nameInCaps.getStyle().set("font-size" , "15px");

        HorizontalLayout buttonCard = new HorizontalLayout();
        buttonCard.add(icon , nameInCaps);
        buttonCard.setSpacing(false);
        buttonCard.getThemeList().add("spacing-s");
        buttonCard.setAlignItems(Alignment.CENTER);
        buttonCard.setJustifyContentMode(JustifyContentMode.CENTER);

        buttonCard.addClassName("editButtonCard");
        return buttonCard;
    }

    private Grid<studentDataEntity> configureGrid(ArrayList<studentDataEntity> data){
        Grid<studentDataEntity> studentDataGrid = new Grid<>(studentDataEntity.class);
        studentDataGrid.removeAllColumns();
        studentDataGrid.setSizeFull();

        studentDataGrid.addColumn(studentDataEntity::getFirstName).setHeader("First Name").setKey("firstName");
        studentDataGrid.addColumn(studentDataEntity::getLastName).setHeader("Last Name").setKey("lastName");
        studentDataGrid.addColumn(studentDataEntity::getHoursEarned).setHeader("Outreach hours");
        studentDataGrid.addColumn(studentDataEntity::getMoneyRaised).setHeader("Money Raised");
        studentDataGrid.addColumn(studentDataEntity::getNumOfEventsAttended).setHeader("# of events");
        studentDataGrid.addColumn(studentDataEntity::getOutReachHoursStatus).setHeader("Hours goal");
        studentDataGrid.addColumn(studentDataEntity::getMoneyRaisedStatus).setHeader("Fundraising quota");
        studentDataGrid.addComponentColumn(studentDataEntity -> getAnchor(studentDataEntity)).setHeader("");
        studentDataGrid.addComponentColumn(studentDataEntity -> getExportAnchor(studentDataEntity)).setHeader("");
        studentDataGrid.addClassName("studentDataGrid");

        studentDataGrid.setItems(data);

        studentDataGrid.getColumns().get(5).setClassNameGenerator(item ->{
           if(item.getOutReachHoursStatus().equals("Complete")){
               return "success";
           }else{
               return "error";
           }
        });

        studentDataGrid.getColumns().get(6).setClassNameGenerator(item ->{
            if(item.getMoneyRaisedStatus().equals("Complete")){
                return "success";
            }else{
                return "error";
            }
        });

        studentDataGrid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
        studentDataGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        return  studentDataGrid;

    }

    private Span getAnchor(studentDataEntity studentDataEntity ){
        Span span = new Span("View more info");
        span.getStyle().set("font-size" , "1em");
        span.getStyle().set("text-decoration" , "underline");
        span.getStyle().set("color" , "var(--lumo-primary-color");
        span.addClassName("request-span");

        span.addClickListener(spanClickEvent -> {
            UI.getCurrent().navigate(com.example.application.views.AdminViews.masterdetail.AdminInfoView.class , studentDataEntity.getFirstName() + "-" + studentDataEntity.getLastName());
        });

        return span;
    }

    private Anchor getExportAnchor(studentDataEntity studentDataEntity){
        UserDataExporter userDataExporter = new UserDataExporter(studentDataEntity.getFirstName() , studentDataEntity.getLastName());
        Anchor span = userDataExporter.configureExporter();
        span.getStyle().set("font-size" , "1em");
        span.getStyle().set("text-decoration" , "underline");
        span.getStyle().set("color" , "var(--lumo-primary-color");
        span.addClassName("request-span");

        return span;
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        addFiltersToGrid();
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");

        dataProvider = new ListDataProvider<>(studentDataEntities);

        grid.setDataProvider(dataProvider);
    }

    private void addColumnsToGrid() {
        createFirstNameColumn();
        createLastNameColumn();
        createOutreachHoursColumn();
        createFundraisingColumn();
        createNumEventsColumn();
        createOutreachStatusColumn();
        createMoneyRaisedStatusColumn();
        createViewMoreInfoColumn();
        createExportColumn();
    }

    private void createFirstNameColumn() {
        firstNameColumn = grid.addColumn(studentDataEntity::getFirstName, "id").setHeader("First Name").setFlexGrow(1);
    }

    private void createLastNameColumn() {
        lastNameColumn = grid.addColumn(studentDataEntity::getLastName, "id").setHeader("Last Name").setFlexGrow(1);
    }

    private void createOutreachHoursColumn(){
        outreacHoursColumn = grid.addColumn(studentDataEntity::getHoursEarned , "id").setHeader("Outreach Hours").setFlexGrow(1);
    }

    private void createFundraisingColumn(){
        fundraisingColumn = grid.addColumn(studentDataEntity::getMoneyRaised , "id").setHeader("Money Raised").setFlexGrow(1);
    }

    private void createNumEventsColumn(){
        numOfEventsColumn = grid.addColumn(studentDataEntity::getNumOfEventsAttended , "id").setHeader("# of Events").setFlexGrow(1);
    }

    private void createViewMoreInfoColumn(){
        moreInfoColumn = grid.addComponentColumn(studentDataEntity -> getAnchor(studentDataEntity)).setFlexGrow(1);
    }

    private void createExportColumn(){
        exportColumn = grid.addComponentColumn(studentDataEntity -> getExportAnchor(studentDataEntity)).setFlexGrow(0);
    }



    private void createOutreachStatusColumn() {
        outreachStatusColumn = grid.addEditColumn(studentDataEntity::getOutReachHoursStatus, new ComponentRenderer<>(studentDataEntity -> {
            Span span = new Span();
            span.setText((studentDataEntity.getOutReachHoursStatus()));

            if(studentDataEntity.getOutReachHoursStatus().equals("Complete")){
                span.getElement().setAttribute("theme", "badge success");
            }else{
                span.getElement().setAttribute("theme", "badge");
            }
            return span;
        })).select((item, newValue) -> item.setOutReachHoursStatus(newValue), Arrays.asList("In Progress" , "Complete"))
                .setComparator(client -> client.getOutReachHoursStatus()).setHeader("Outreach Hours Status").setFlexGrow(1);
    }

    private void createMoneyRaisedStatusColumn() {
        moneyRaisedStatusColumn = grid.addEditColumn(studentDataEntity::getMoneyRaisedStatus, new ComponentRenderer<>(studentDataEntity -> {
            Span span = new Span();
            span.setText((studentDataEntity.getMoneyRaisedStatus()));

            if(studentDataEntity.getMoneyRaisedStatus().equals("Complete")){
                span.getElement().setAttribute("theme", "badge success");
            }else{
                span.getElement().setAttribute("theme", "badge");
            }
            return span;
        })).select((item, newValue) -> item.setMoneyRaisedStatus(newValue), Arrays.asList("In Progress" , "Complete"))
                .setComparator(client -> client.getMoneyRaisedStatus()).setHeader("Money Raised Status").setFlexGrow(1);
    }


    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        TextField firstNameFilter = new TextField();
        firstNameFilter.setPlaceholder("Filter");
        firstNameFilter.setClearButtonVisible(true);
        firstNameFilter.setWidth("100%");
        firstNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        firstNameFilter.addValueChangeListener(event -> dataProvider.addFilter(
                studentDataEntity -> StringUtils.containsIgnoreCase(studentDataEntity.getFirstName(), firstNameFilter.getValue())));
        filterRow.getCell(firstNameColumn).setComponent(firstNameFilter);

        TextField lastNameFilter = new TextField();
        lastNameFilter.setPlaceholder("Filter");
        lastNameFilter.setClearButtonVisible(true);
        lastNameFilter.setWidth("100%");
        lastNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        lastNameFilter.addValueChangeListener(event -> dataProvider.addFilter(
                studentDataEntity -> StringUtils.containsIgnoreCase(studentDataEntity.getLastName(), lastNameFilter.getValue())));
        filterRow.getCell(lastNameColumn).setComponent(lastNameFilter);

        TextField outreachHoursFilter = new TextField();
        outreachHoursFilter.setPlaceholder("Filter");
        outreachHoursFilter.setClearButtonVisible(true);
        outreachHoursFilter.setWidth("100%");
        outreachHoursFilter.setValueChangeMode(ValueChangeMode.EAGER);
        outreachHoursFilter.addValueChangeListener(event -> dataProvider.addFilter(
                studentDataEntity -> StringUtils.containsIgnoreCase(Double.toString(studentDataEntity.getHoursEarned()), outreachHoursFilter.getValue())));
        filterRow.getCell(outreacHoursColumn).setComponent(outreachHoursFilter);

        TextField moneyRaisedFilter = new TextField();
        moneyRaisedFilter.setPlaceholder("Filter");
        moneyRaisedFilter.setClearButtonVisible(true);
        moneyRaisedFilter.setWidth("100%");
        moneyRaisedFilter.setValueChangeMode(ValueChangeMode.EAGER);
        moneyRaisedFilter.addValueChangeListener(event -> dataProvider.addFilter(
                studentDataEntity -> StringUtils.containsIgnoreCase(Double.toString(studentDataEntity.getMoneyRaised()), moneyRaisedFilter.getValue())));
        filterRow.getCell(fundraisingColumn).setComponent(moneyRaisedFilter);

        TextField numEventsFilter = new TextField();
        numEventsFilter.setPlaceholder("Filter");
        numEventsFilter.setClearButtonVisible(true);
        numEventsFilter.setWidth("100%");
        numEventsFilter.setValueChangeMode(ValueChangeMode.EAGER);
        numEventsFilter.addValueChangeListener(event -> dataProvider.addFilter(
                studentDataEntity -> StringUtils.containsIgnoreCase(Integer.toString(studentDataEntity.getNumOfEventsAttended()), numEventsFilter.getValue())));
        filterRow.getCell(numOfEventsColumn).setComponent(numEventsFilter);


        ComboBox<String> outreachStatusFilter = new ComboBox<>();
        outreachStatusFilter.setItems(Arrays.asList("In Progress", "Complete"));
        outreachStatusFilter.setPlaceholder("Filter");
        outreachStatusFilter.setClearButtonVisible(true);
        outreachStatusFilter.setWidth("100%");
        outreachStatusFilter.addValueChangeListener(
                event -> dataProvider.addFilter(studentDataEntity -> isOutreachStatusEqual(studentDataEntity, outreachStatusFilter)));
        filterRow.getCell(outreachStatusColumn).setComponent(outreachStatusFilter);

        ComboBox<String> moneyRaisedStatusFilter = new ComboBox<>();
        moneyRaisedStatusFilter.setItems(Arrays.asList("In Progress", "Complete"));
        moneyRaisedStatusFilter.setPlaceholder("Filter");
        moneyRaisedStatusFilter.setClearButtonVisible(true);
        moneyRaisedStatusFilter.setWidth("100%");
        moneyRaisedStatusFilter.addValueChangeListener(
                event -> dataProvider.addFilter(studentDataEntity -> isMoneyRaisedStatusEqual(studentDataEntity, moneyRaisedStatusFilter)));
        filterRow.getCell(moneyRaisedStatusColumn).setComponent(moneyRaisedStatusFilter);

    }


    private boolean isOutreachStatusEqual(studentDataEntity studentDataEntity , ComboBox<String> stringComboBox){
        String filterValue = stringComboBox.getValue();
        if(filterValue != null){
            return StringUtils.equals(studentDataEntity.getOutReachHoursStatus() , filterValue);
        }
        return true;
    }

    private boolean isMoneyRaisedStatusEqual(studentDataEntity studentDataEntity , ComboBox<String> stringComboBox){
        String filterValue = stringComboBox.getValue();
        if(filterValue != null){
            return StringUtils.equals(studentDataEntity.getMoneyRaisedStatus() , filterValue);
        }
        return true;
    }




}
