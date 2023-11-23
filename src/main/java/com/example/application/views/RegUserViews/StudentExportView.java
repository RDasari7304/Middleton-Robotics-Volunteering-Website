package com.example.application.views.RegUserViews;

import com.example.application.Entities.Event.detailEvent;
import com.example.application.Resources.Extras.Util;
import com.vaadin.flow.component.grid.Grid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;

public class StudentExportView {

    public static void exportData(ArrayList<detailEvent> events,
                                  Grid<detailEvent> grid) throws IOException{
        String[] colums = {"Event Name","Starting Date",
        "Event Type","Role","Reward"};/*
        ArrayList<Event> events = DB.getEventsByName(LoggedInUserDetails.getFirstName(),
                LoggedInUserDetails.getLastName());*/

        //File file = new File("Downloads");
        Workbook workBook = new XSSFWorkbook(/*file.toString()*/);
        Sheet sheet = workBook.createSheet("Events");

        Font headerFont = workBook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 13);
        headerFont.setColor(IndexedColors.MAROON.getIndex());

        CellStyle headerStyle = workBook.createCellStyle();
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < colums.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colums[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;

        for(detailEvent event : events){
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(event.getEventName());
            row.createCell(1).setCellValue(event.getStartingDate());
            row.createCell(2).setCellValue(event.getEventType());
            row.createCell(3).setCellValue(event.getRole());
            row.createCell(4).setCellValue(event.getReward());
        }

        for(int i = 0; i < colums.length; i++){
            sheet.autoSizeColumn(i);
        }
        //final ByteArrayOutputStream bos;
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workBook.write(bos);
            workBook.close();
            Util.notify("Data exported successfully");
        }
        catch (IOException ex){
            ex.printStackTrace();
            Util.notify("Failed to export data");
        }
    }
}
