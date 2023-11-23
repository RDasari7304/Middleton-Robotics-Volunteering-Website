package com.example.application.Resources.Services.DataExporters;

import com.example.application.Entities.Event.Event;
import com.example.application.Entities.Event.Request;
import com.example.application.Entities.User.User;
import com.example.application.Resources.Services.DataService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xssf.usermodel.*;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AllUsersDataExporter {

    DataService dataService = new DataService();

    private byte[] getWorkbookBytes(XSSFWorkbook wb) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        wb.write(bos); // omitted try-catch for brevity
        bos.close(); // omitted try-catch for brevity
        return bos.toByteArray();
    }

    public void exportUsers(HorizontalLayout horizontalLayout){
        List<User> allUsers = dataService.getAllUsers();
        List<Event> allEvents = dataService.getAllEvents();
        XSSFWorkbook workbook = new XSSFWorkbook();
        createUserInfoSheet(allUsers, workbook);
        createEventSheet(allEvents, workbook);

        Button downloadBtn = new Button("Export Data", new Icon(VaadinIcon.DOWNLOAD));
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

        horizontalLayout.add(buttonWrapper);
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

    private void createUserInfoSheet(List<User> allUsers,
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
            List<Request> allEventsFinished = dataService.getAllRequestsForUserWithStatus(user.getFirstName() , user.getLastName() , "approved");

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
            int numOfOutreachEvents = 0;
            int numOfFundraisingEvents = 0;
            double outreachHours = user.getTotalOutreachHoursEarned();

            for(Request r : allEventsFinished){
                if(r.getEventType().equals("Outreach Event")){
                    numOfOutreachEvents++;
                }else{
                    numOfFundraisingEvents++;
                }
            }


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

    private void createEventSheet(List<Event> allEvents,
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



}
