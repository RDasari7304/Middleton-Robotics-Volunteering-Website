

import com.example.application.Entities.Event.Event;
import com.example.application.Entities.Event.Request;
import com.example.application.Entities.Event.detailEvent;
import com.example.application.Entities.User.User;
import com.example.application.Resources.Services.Database;
import com.example.application.Resources.Extras.Util;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@Route("test")
@PageTitle("test")
@CssImport("styles/views/authenticationViews/test.css")
@CssImport(value = "styles/views/dashboard/dashView.css", include = "lumo-badge")
public class test extends HorizontalLayout {
    ArrayList<detailEvent> allUserEvents = new ArrayList<>();

    public test(){
        Button buttonCreateUser = new Button("Create request" , event ->{
            Image image = new Image("oie_transparent.png" , "alt");
            Request request = new Request("Tarun" , "Dasari" , "gamers2333@gmail.com" , "Camp Director" , "I managed everyone and what they were doing to make sure they were on track." , 50.0 , "pending" , "Robotics Summer Camp" , "Outreach Event" , Util.getYearBound() , image);
            Database.addRequest(request);
        });

        exportUsers();
        add(buttonCreateUser);
    }

    private byte[] getWorkbookBytes(XSSFWorkbook wb) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        wb.write(bos); // omitted try-catch for brevity
        bos.close(); // omitted try-catch for brevity
        return bos.toByteArray();
    }

    private void exportUsers(){
        ArrayList<User> allUsers = Database.getAllUsers();/*
        ArrayList<Event> allEvents = Database.getAllEvents();*/
        XSSFWorkbook workbook = new XSSFWorkbook();
        createUserInfoSheet(allUsers, workbook);/*
        createEventSheet(allEvents, workbook);*/

        Button downloadBtn = new Button("Download Excel file", new Icon(VaadinIcon.DOWNLOAD));
        /*FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(
                new StreamResource("user_info.xlsx",
                        () -> new ByteArrayInputStream(getWorkbookBytes(workbook))));*/
        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(
                new StreamResource("user_info.xlsx",
                        () -> {
                            try {
                                return new ByteArrayInputStream(getWorkbookBytes(workbook));
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }));

        buttonWrapper.wrapComponent(downloadBtn);

        /*catch (IOException ex){
            ex.printStackTrace();
            Utils.notify("Failed to export data");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            Utils.notify("File not found");
        }*/

        add(buttonWrapper);
        /*Anchor download = null;
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            download = new Anchor(new StreamResource("user_info.xlsx" , () -> new ByteArrayInputStream(bos.toByteArray())) , "Export Data");
            download.getElement().setAttribute("download", true);
            Utils.notify("Data export is available");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            Utils.notify("File not found");
        }
        catch (IOException ex){
            ex.printStackTrace();
            Utils.notify("Failed to export data");
        }
        board.add(download);*/
    }

    private void createUserInfoSheet(ArrayList<User> allUsers,
                                     XSSFWorkbook workbook){
        String[] columns = {"First Name", "Last Name","Email", "Team Name",
                "# of Fundraising Events", "Amount Raised",
                "# of Outreach Events", "Hours Done"};
        XSSFSheet sheet = workbook.createSheet("User Info");


        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 13);
        headerFont.setColor(IndexedColors.GOLD.getIndex());

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(192,0,0)));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.MEDIUM);
        headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.MEDIUM);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < columns.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int numComplete = 0, numInProgress = 0, numBehind = 0;
        int total = 0;

        int rowCount = 1;
        for(User user : allUsers){
            ArrayList<Request> allEventsFinished = Database.getAllRequestsForUserWithStatus(user.getFirstName() , user.getLastName() , "approved");

            Row row = sheet.createRow(rowCount);
            row.createCell(0).setCellValue(user.getFirstName());
            row.createCell(1).setCellValue(user.getLastName());
            row.createCell(2).setCellValue(user.getEmail());
            row.createCell(3).setCellValue(user.getTeam());

            Font teamFont = workbook.createFont();
            XSSFCellStyle teamStyle = workbook.createCellStyle();
            String teamName = user.getTeam();
            switch (teamName) {
                case "Maelstrom":
                    teamStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(23, 73, 254)));
                    teamStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    teamFont.setColor(IndexedColors.WHITE.getIndex());
                    break;
                case "Masquerade":
                    teamStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 0, 0)));
                    teamStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    teamFont.setColor(IndexedColors.GOLD.getIndex());
                    break;
                case "Minotaur":
                    teamStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(216, 0, 1)));
                    teamStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    teamFont.setColor(IndexedColors.WHITE1.getIndex());
                    break;
                case "Mercury":
                case "Minerva":
                    teamStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 0, 0)));
                    teamStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    teamFont.setColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    break;
            }
            teamStyle.setFont(teamFont);
            row.getCell(3).setCellStyle(teamStyle);

           /* ArrayList<detailEvent> allEvents = new ArrayList<>();DB.getAllUserEvents(
                    LoggedInUserDetails.getFirstName(), LoggedInUserDetails.getLastName());*/

            double fundraisingAmount = user.getTotalMoneyFundraised();
            int numOfOutreachEvents = allEventsFinished.size();
            int numOfFundraisingEvents = 0;
            double outreachHours = user.getTotalOutreachHoursEarned();


            row.createCell(4).setCellValue(numOfFundraisingEvents);

            Font dollarFont = workbook.createFont();
            XSSFCellStyle dollarStyle = workbook.createCellStyle();
            double targetFundraisingAmount = 0;
            switch (teamName){
                case "Mercury":
                case "Minerva":
                    targetFundraisingAmount = 400;
                    break;
                default:
                    targetFundraisingAmount = 600;
            }

            double percentage = targetFundraisingAmount == 0 ? 0 :
                    fundraisingAmount/targetFundraisingAmount;
            if(percentage >= 1.0){
                dollarFont.setColor(IndexedColors.GREEN.getIndex());
                dollarStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(198, 239, 206)));
                dollarStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                numComplete++;
            }
            else if(percentage >= .51 && percentage <= .99){
                dollarFont.setColor(IndexedColors.BROWN.getIndex());
                dollarStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 235, 156)));
                dollarStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                numInProgress++;
            }
            else{
                dollarFont.setColor(IndexedColors.DARK_RED.getIndex());
                dollarStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 199, 206)));
                dollarStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                numBehind++;
            }
            total = numComplete + numInProgress + numBehind;
            dollarStyle.setFont(dollarFont);
            DataFormat df = workbook.createDataFormat();
            dollarStyle.setDataFormat(df.getFormat("$#,#0.00"));

            Cell fundraisingCell = row.createCell(5);
            fundraisingCell.setCellStyle(dollarStyle);
            fundraisingCell.setCellValue(fundraisingAmount);

            Font numFont = workbook.createFont();
            XSSFCellStyle numStyle = workbook.createCellStyle();
            if(numOfOutreachEvents == 0){
                numFont.setColor(IndexedColors.DARK_RED.getIndex());
                numStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 199, 206)));
                numStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            else if(numOfOutreachEvents > 0 && numOfOutreachEvents < 3){
                numFont.setColor(IndexedColors.BROWN.getIndex());
                numStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 235, 156)));
                numStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            else if(numOfOutreachEvents >= 3){
                numFont.setColor(IndexedColors.GREEN.getIndex());
                numStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(198, 239, 206)));
                numStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            numStyle.setFont(numFont);

            Cell numOutreach = row.createCell(6);
            numOutreach.setCellStyle(numStyle);
            numOutreach.setCellValue(numOfOutreachEvents);

            row.createCell(7).setCellValue(outreachHours);

            /*if(rowCount % 2 == 0) {
                for (int i = 0; i < columns.length; i++) {
                    row.getCell(i).setCellStyle(alternateStyle);
                }
            }*/
            rowCount++;

        }

        for(int i = 0; i < columns.length; i++){
            sheet.autoSizeColumn(i);
        }

        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0,0,
                0,0, 0,sheet.getLastRowNum() + 2,5,20);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Percentage of People who met fundraising goals");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.BOTTOM);
/*

        XDDFDataSource<String> types = XDDFDataSourcesFactory.fromArray(
                new String[]{"Completed Goal", "In Progress", "Behind"}
        );
        Double[] percentages = new Double[]{
                (((double) numComplete)/total), (((double) numInProgress)/total),
                (((double) numBehind)/total)
        };
        XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromArray(
                percentages);
*/
/*
        XDDFChartData data = chart.createData(ChartTypes.PIE, null, null);
        data.setVaryColors(true);*//*
        data.addSeries(types, values);*//*
        chart.plot(data);*/
    }

    private void createEventSheet(ArrayList<Event> allEvents,
                                  XSSFWorkbook workbook){

        String[] columns = {"Event Name", "Event Type",
                "Location", "Starting Date", "Ending Date",
                "Starting Time", "Ending Time", "Available Slots"};
        Sheet sheet = workbook.createSheet("Events");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 13);
        headerFont.setColor(IndexedColors.GOLD.getIndex());

        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < columns.length; i++){
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(192,0,0)));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.MEDIUM);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.MEDIUM);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFont(headerFont);

            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowCount = 1;
        for(Event event : allEvents){
            Row row = sheet.createRow(rowCount);
            row.createCell(0).setCellValue(event.getEventName());
            row.createCell(1).setCellValue(event.getEventType());
            row.createCell(2).setCellValue(event.getLocation());
            row.createCell(3).setCellValue(event.getStartingDate());
            row.createCell(4).setCellValue(event.getEndingDate());
            row.createCell(5).setCellValue(event.getStartingTime());
            row.createCell(6).setCellValue(event.getEndTime());
            row.createCell(7).setCellValue(event.getSlotsAvaliable());
            if(rowCount % 2 == 0 && rowCount != 0) {
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    System.out.print(j);
                    Cell cell = row.getCell(j);
                    XSSFCellStyle alternateStyle = workbook.createCellStyle();
                    alternateStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255,153,153)));
                    alternateStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THICK);
                    alternateStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THICK);
                    alternateStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(alternateStyle);
                }
            }
            rowCount++;
        }

        for(int i = 0; i < columns.length; i++){
            sheet.autoSizeColumn(i);
        }
    }

    /*private ArrayList<studentDataEntity> studentDataEntities;

    public test(){
        Database.connect();
        setSizeFull();

       *//* studentDataEntities = new ArrayList<>();
        studentDataEntities.add(new studentDataEntity("r" , "d" , "gamers2333@gmail.com" , 5 , 29 , 600));
        studentDataEntities.add(new studentDataEntity("Rohit" , "Dasari" , "gamers2333@gmail.com" , 5 , 30 , 550));
        studentDataEntities.add(new studentDataEntity("Rohit" , "Dasari" , "gamers2333@gmail.com" , 5 , 45 , 634));
        studentDataEntities.add(new studentDataEntity("Rohit" , "Dasari" , "gamers2333@gmail.com" , 5 , 10 , 590));
        studentDataEntities.add(new studentDataEntity("Rohit" , "Dasari" , "gamers2333@gmail.com" , 5 , 31 , 610 ));


        Button addRequest = new Button("");
        addRequest.addClickListener(buttonClickEvent -> {
            Database.createAllTablesRoutine();
           Database.addUser("Rohit" , "Dasari" , "g" , "g" , "gamers2333@gmail.com" , "Maelstrom" , "Adult");
        });

        createGrid();
        add(grid , addRequest);*//*



    }

   

    private GridPro<studentDataEntity> grid;

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
        moreInfoColumn = grid.addComponentColumn(studentDataEntity -> getViewInfoSpan(studentDataEntity)).setFlexGrow(0);
    }

    private void createExportColumn(){
        exportColumn = grid.addComponentColumn(studentDataEntity -> getExportSpan(studentDataEntity)).setFlexGrow(0);
    }

    public Anchor getExportSpan(studentDataEntity studentDataEntity){
        return new Anchor("" , "Export");
    }

    private Anchor getViewInfoSpan(studentDataEntity studentDataEntity){
        return new Anchor("" , "View more info");
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
                .setComparator(client -> client.getOutReachHoursStatus()).setHeader("Outreach Hours Status").setFlexGrow(0);
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
                .setComparator(client -> client.getMoneyRaisedStatus()).setHeader("Money Raised Status").setFlexGrow(0);
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
                event -> dataProvider.addFilter(studentDataEntity -> isMoneyRaisedStatusEqual(studentDataEntity, outreachStatusFilter)));
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
*/


   /* ArrayList<String> row1Strings;//{"" , "" , "" , "IMPACT:"};
    ArrayList<String> rowColStart;

    ArrayList<String> row2Strings;//{"" , "" , "" , "STEM Related"};
    ArrayList<String> row3Strings;//{"" , "" , "" , "FIRST Related"};
    ArrayList<String> row4Strings; //{"Last Name" , "First Name" , "Program" , "# Events Signed up" , "# Events Approved"};
    ArrayList<String> info;//{"Dasari" , "Tarunkrishna" , "FTC 3846 - Maelstrom" , "5" , "2"};
    ArrayList<Integer> eventsColumns;
    ArrayList<exportEventDetails> exportEventDetail;
    int[] coloredCells1;
    int[] coloredCells2;
    int[] coloredCells3;
    int[] coloredCells4;
    int[] coloredCells5;

    public test(){
        eventsColumns = new ArrayList<>();
        rowColStart = new ArrayList<>();

        row1Strings = new ArrayList<>();
        row1Strings.add("");
        row1Strings.add("");
        row1Strings.add("");
        row1Strings.add("IMPACT:");

        row2Strings = new ArrayList<>();
        row2Strings.add("");
        row2Strings.add("");
        row2Strings.add("");
        row2Strings.add("STEM Related");

        row3Strings = new ArrayList<>();
        row3Strings.add("");
        row3Strings.add("");
        row3Strings.add("");
        row3Strings.add("FIRST Related");

        row4Strings = new ArrayList<>();
        row4Strings.add("Last Name");
        row4Strings.add("First Name");
        row4Strings.add("Program");
        row4Strings.add("# Events Signed up");
        row4Strings.add("# Events Approved");

        info = new ArrayList<>();
        info.add("Dasari");
        info.add("Rohit");
        info.add("FTC 3846 - Maelstrom");
        info.add("5");
        info.add("4");

        exportEventDetail = new ArrayList<>();
        exportEventDetail.add(new exportEventDetails(150 , true , true , "Tune up Tampa 9/21/19" , 50));
        exportEventDetail.add(new exportEventDetails(550 , true , false , "Summer Camp 2020" , 75));
        exportEventDetail.add(new exportEventDetails(1000 , false , false , "Road Run Marathon 10/20/19" , 65));
        exportEventDetail.add(new exportEventDetails(250 , false , true , "Tune up Tampa 9/21/19" , 50));

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Student Data");

        XSSFRow coloredRowOne = sheet.createRow(0);
        XSSFRow coloredRowTwo = sheet.createRow(1);
        XSSFRow coloredRowThree = sheet.createRow(2);
        XSSFRow coloredRowFour = sheet.createRow(3);
        XSSFRow coloredRowFive = sheet.createRow(4);

        CellRangeAddress region1 = new CellRangeAddress(0 , 0 , 3 , 4);
        CellRangeAddress region2 = new CellRangeAddress(1 , 1 , 3 , 4);
        CellRangeAddress region3 = new CellRangeAddress(2 , 2 , 3 , 4);

        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);

        XSSFCellStyle coloredStylerow3 = workbook.createCellStyle();
        coloredStylerow3.setFillForegroundColor(new XSSFColor(new java.awt.Color(214, 220, 228)));
        coloredStylerow3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        coloredStylerow3.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStylerow3.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        XSSFCellStyle coloredStylerow1 = workbook.createCellStyle();
        coloredStylerow1.setFillForegroundColor(new XSSFColor(new java.awt.Color(214, 220, 228)));
        coloredStylerow1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        coloredStylerow1.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        XSSFCellStyle coloredStylerow2 = workbook.createCellStyle();
        coloredStylerow2.setFillForegroundColor(new XSSFColor(new java.awt.Color(214, 220, 228)));
        coloredStylerow2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        coloredStylerow2.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        XSSFCellStyle coloredStylerow4 = workbook.createCellStyle();
        coloredStylerow4.setFillForegroundColor(new XSSFColor(new java.awt.Color(214, 220, 228)));
        coloredStylerow4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        coloredStylerow4.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStylerow4.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStylerow4.setAlignment(HorizontalAlignment.CENTER);
        coloredStylerow4.setVerticalAlignment(VerticalAlignment.CENTER);
        coloredStylerow4.setFont(headerFont);

        XSSFCellStyle coloredStyle5 = workbook.createCellStyle();
        coloredStyle5.setFillForegroundColor(new XSSFColor(new java.awt.Color(215, 215, 215)));
        //coloredStyle5.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        coloredStyle5.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        coloredStyle5.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle5.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle5.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle5.setAlignment(HorizontalAlignment.LEFT);

        XSSFCellStyle coloredStyle11 = workbook.createCellStyle();
        coloredStyle11.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 255, 0)));
        //coloredStyle5.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        coloredStyle11.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        coloredStyle11.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle11.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle11.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle11.setTopBorderColor(new XSSFColor(new java.awt.Color(215, 215, 215)));
        coloredStyle11.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle11.setAlignment(HorizontalAlignment.CENTER);
        coloredStyle11.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFCellStyle coloredStyle10 = workbook.createCellStyle();
        coloredStyle10.setFillForegroundColor(new XSSFColor(new java.awt.Color(215, 215, 215)));
        coloredStyle10.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        coloredStyle10.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle10.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle10.setAlignment(HorizontalAlignment.CENTER);
        coloredStyle10.setVerticalAlignment(VerticalAlignment.CENTER);
        coloredStyle10.setFont(headerFont);
        coloredStyle10.setWrapText(true);

        XSSFCellStyle coloredStyle6 = workbook.createCellStyle();
        coloredStyle6.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        coloredStyle6.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        coloredStyle6.setAlignment(HorizontalAlignment.CENTER);
        coloredStyle6.setVerticalAlignment(VerticalAlignment.CENTER);
        coloredStyle6.setWrapText(true);
        coloredStyle6.setFont(headerFont);
        coloredStyle6.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle6.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle6.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle6.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        XSSFCellStyle coloredStyle7 = workbook.createCellStyle();
        coloredStyle7.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 255, 0)));
        coloredStyle7.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        coloredStyle7.setAlignment(HorizontalAlignment.CENTER);
        coloredStyle7.setVerticalAlignment(VerticalAlignment.CENTER);
        coloredStyle7.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle7.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle7.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        XSSFCellStyle coloredStyle8 = workbook.createCellStyle();
        coloredStyle8.setFillForegroundColor(new XSSFColor(new java.awt.Color(198, 224, 180)));
        coloredStyle8.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        coloredStyle8.setAlignment(HorizontalAlignment.CENTER);
        coloredStyle8.setVerticalAlignment(VerticalAlignment.CENTER);
        coloredStyle8.setWrapText(true);
        coloredStyle8.setFont(headerFont);
        coloredStyle8.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        XSSFCellStyle coloredStyle9 = workbook.createCellStyle();
        coloredStyle9.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 230, 153)));
        coloredStyle9.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        coloredStyle9.setAlignment(HorizontalAlignment.CENTER);
        coloredStyle9.setVerticalAlignment(VerticalAlignment.CENTER);
        coloredStyle9.setWrapText(true);
        coloredStyle9.setFont(headerFont);
        coloredStyle9.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        coloredStyle9.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        ArrayList<XSSFCellStyle> firstRowStyles = new ArrayList<>();
        firstRowStyles.add(coloredStylerow1);
        firstRowStyles.add(coloredStylerow1);
        firstRowStyles.add(coloredStylerow1);
        firstRowStyles.add(coloredStyle6);

        ArrayList<XSSFCellStyle> secondRowStyles = new ArrayList<>();
        secondRowStyles.add(coloredStylerow1);
        secondRowStyles.add(coloredStylerow1);
        secondRowStyles.add(coloredStylerow1);
        secondRowStyles.add(coloredStyle6);

        ArrayList<XSSFCellStyle> thirdRowStyles = new ArrayList<>();
        thirdRowStyles.add(coloredStylerow1);
        thirdRowStyles.add(coloredStylerow1);
        thirdRowStyles.add(coloredStylerow1);
        thirdRowStyles.add(coloredStyle6);

        ArrayList<XSSFCellStyle> fourRowStyles = new ArrayList<>();
        fourRowStyles.add(coloredStylerow4);
        fourRowStyles.add(coloredStylerow4);
        fourRowStyles.add(coloredStylerow4);
        fourRowStyles.add(coloredStyle6);
        fourRowStyles.add(coloredStyle6);

        ArrayList<XSSFCellStyle> fifthRowStyles = new ArrayList<>();
        fifthRowStyles.add(coloredStyle5);
        fifthRowStyles.add(coloredStyle5);
        fifthRowStyles.add(coloredStyle5);
        fifthRowStyles.add(coloredStyle7);
        fifthRowStyles.add(coloredStyle6);

        for(exportEventDetails e : exportEventDetail){
            row4Strings.add(e.getEventName());
            info.add(Double.toString(e.getReward()));
            row1Strings.add(Integer.toString(e.getImpact()));
            row2Strings.add(e.getStemRelated());
            row3Strings.add(e.getFirstRelated());

            eventsColumns.add(info.size() - 1);
            String col_letter = CellReference.convertNumToColString(info.size() - 1);
            rowColStart.add(col_letter);

            firstRowStyles.add(coloredStyle9);
            secondRowStyles.add(coloredStyle9);
            thirdRowStyles.add(coloredStyle9);
            fourRowStyles.add(coloredStyle10);
            fifthRowStyles.add(coloredStyle10);
        }

        coloredCells1 = new int[row1Strings.size()];
        coloredCells2 = new int[row2Strings.size()];
        coloredCells3 = new int[row3Strings.size()];
        coloredCells4 = new int[row4Strings.size()];
        coloredCells5 = new int[info.size() ];

        for(int i = 0 ; i < coloredCells1.length ; i ++){
            if(i > 3) {
                XSSFCell cell1 = coloredRowOne.createCell(i +1);
                if(i < (3 + exportEventDetail.size() + 1)){
                    cell1.setCellValue(Integer.parseInt(row1Strings.get(i)));
                    cell1.setCellStyle(firstRowStyles.get(i));
                }else{
                cell1.setCellValue(row1Strings.get(i));
                cell1.setCellStyle(firstRowStyles.get(i));}
            }else{
                XSSFCell cell1 = coloredRowOne.createCell(i );
                cell1.setCellValue(row1Strings.get(i));
                cell1.setCellStyle(firstRowStyles.get(i));
            }
        }

        for(int i = 0 ; i < coloredCells2.length ; i ++){
            if(i > 3) {
                XSSFCell cell1 = coloredRowTwo.createCell(i + 1);
                cell1.setCellValue(row2Strings.get(i));
                cell1.setCellStyle(secondRowStyles.get(i));
            }else{
                XSSFCell cell1 = coloredRowTwo.createCell(i);
                cell1.setCellValue(row2Strings.get(i));
                cell1.setCellStyle(secondRowStyles.get(i));
            }
        }

        for(int i = 0 ; i < coloredCells3.length ; i ++){
            if(i > 3) {
                XSSFCell cell1 = coloredRowThree.createCell(i + 1);
                cell1.setCellValue(row3Strings.get(i));
                cell1.setCellStyle(thirdRowStyles.get(i));
            }else{
                XSSFCell cell1 = coloredRowThree.createCell(i);
                cell1.setCellValue(row3Strings.get(i));
                cell1.setCellStyle(thirdRowStyles.get(i));
            }

        }

        for(int i = 0 ; i < coloredCells4.length ; i ++){
            XSSFCell cell1 = coloredRowFour.createCell(i);
            cell1.setCellValue(row4Strings.get(i));
            cell1.setCellStyle(fourRowStyles.get(i));
        }

        for(int i = 0 ; i < coloredCells5.length ; i ++){
            XSSFCell cell1 = coloredRowFive.createCell(i);
            if(i > 3 && eventsColumns.size() != 0 && i < (4 + eventsColumns.size() + 1)){
                        cell1.setCellValue(Double.parseDouble(info.get(i)));
                        cell1.setCellStyle(fifthRowStyles.get(i));
            }else{
            cell1.setCellValue(info.get(i));
            cell1.setCellStyle(fifthRowStyles.get(i));}
        }

        int[] colorRow1New;
        int[] colorRow2New;
        int[] colorRow3New;
        int[] colorRow4New;
        int[] colorRow5New;

        if(eventsColumns.size() != 0){
            String impactFormula = "SUM(" + rowColStart.get(0) + "1:" + rowColStart.get(rowColStart.size() - 1) + "1" + ")";
            String countStem = "SUMIF("+"$"+rowColStart.get(0)+"$2:"+ "$"+rowColStart.get(rowColStart.size() - 1) + "$2" + ",\"Y\"," + rowColStart.get(0) + "5:" + rowColStart.get(rowColStart.size() - 1) + "5)";
            String countFirst = "SUMIF("+"$"+rowColStart.get(0)+"$3:"+ "$"+rowColStart.get(rowColStart.size() - 1) + "$3" + ",\"Y\"," + rowColStart.get(0) + "5:" + rowColStart.get(rowColStart.size() - 1) + "5)";
            String sumOfEventsFormula = "SUM(" + rowColStart.get(0) + "5:" + rowColStart.get(rowColStart.size() - 1) + "5" + ")";

            String fourRowValue = "Total Impact / Count / Hours";
            String fourRowValue2 = "STEM";
            String fourRowValue3 = "FIRST";

            row1Strings.add(impactFormula);
            row2Strings.add("");
            row3Strings.add("");
            row4Strings.add(fourRowValue);
            info.add(sumOfEventsFormula);

            firstRowStyles.add(coloredStyle9);
            secondRowStyles.add(coloredStyle9);
            thirdRowStyles.add(coloredStyle9);
            fourRowStyles.add(coloredStyle6);
            fifthRowStyles.add(coloredStyle11);

            fourRowStyles.add(coloredStyle6);
            fifthRowStyles.add(coloredStyle11);
            fourRowStyles.add(coloredStyle6);
            fifthRowStyles.add(coloredStyle11);

            row4Strings.add(fourRowValue2);
            row4Strings.add(fourRowValue3);

            info.add(countStem);
            info.add(countFirst);
        }

        colorRow1New = new int[row1Strings.size()];
        colorRow2New = new int[row2Strings.size()];
        colorRow3New = new int[row3Strings.size()];
        colorRow4New = new int[row4Strings.size()];
        colorRow5New = new int[info.size()];

        int firstRowi = coloredRowOne.getLastCellNum();
        int secondRowi = coloredRowTwo.getLastCellNum();
        int thirdRowi = coloredRowThree.getLastCellNum();
        int fourthRowi = coloredRowFour.getLastCellNum();
        int fifthRowi = coloredRowFive.getLastCellNum();

        XSSFCell cell = coloredRowOne.createCell(firstRowi);
        XSSFCell cell2 = coloredRowTwo.createCell(secondRowi);
        XSSFCell cell3 = coloredRowThree.createCell(thirdRowi);

        for(int i = fourthRowi ; i < colorRow4New.length ; i ++){
            XSSFCell cell1 = coloredRowFour.createCell(i);
            cell1.setCellValue(row4Strings.get(i));
            cell1.setCellStyle(fourRowStyles.get(i));
        }

        for(int i = fifthRowi ; i < colorRow5New.length ; i ++){
            XSSFCell cell1 = coloredRowFive.createCell(i);
            cell1.setCellFormula(info.get(i));
            cell1.setCellStyle(fifthRowStyles.get(i));
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            evaluator.evaluateFormulaCell(cell1);
        }


        if(eventsColumns.size() != 0){
            String formula = "SUM(" + rowColStart.get(0) + "1:" + rowColStart.get(rowColStart.size() - 1) + "1" + ")";
            //totalImpactCell.setCellType(CellType.FORMULA);
            cell.setCellFormula(formula);
            cell.setCellStyle(coloredStyle9);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            evaluator.evaluateFormulaCell(cell);

            cell2.setCellValue("");
            cell3.setCellValue("");

            cell2.setCellStyle(coloredStyle9);
            cell3.setCellStyle(coloredStyle9);

        }else{
            cell.setCellValue(0);
        }


        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.setColumnWidth(3 , 9 * 260);
        sheet.setColumnWidth(4 , 9 * 260);

        for(int i : eventsColumns){
            sheet.setColumnWidth(i , 13 * 256);
        }

        sheet.setColumnWidth(eventsColumns.get(eventsColumns.size() - 1) + 1 , 17 * 256);

        RegionUtil.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN, region1, sheet);
        RegionUtil.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN, region1, sheet);

        RegionUtil.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN, region2, sheet);
        RegionUtil.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN, region2, sheet);

        RegionUtil.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN, region3, sheet);
        RegionUtil.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN, region3, sheet);

        sheet.addMergedRegion(region1);
        sheet.addMergedRegion(region2);
        sheet.addMergedRegion(region3);

        final ByteArrayOutputStream bos;
        Anchor download = null;
        try {
            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();

            download = new Anchor(new StreamResource("rohi32243.xlsx" , () -> new ByteArrayInputStream(bos.toByteArray())) , "download");
            download.getElement().setAttribute("download", true);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        add(download);

    }

    private int calculateYStem(ArrayList<exportEventDetails> stemArray){
        int i =0;
        for(exportEventDetails s : stemArray){
            if(s.getStemRelated().equals("Y")){
                i++;
            }
        }

        return i;
    }

    private int calculateYFirst(ArrayList<exportEventDetails> stemArray){
        int i =0;
        for(exportEventDetails s : stemArray){
            if(s.getFirstRelated().equals("Y")){
                i++;
            }
        }

        return i;
    }*/



}


