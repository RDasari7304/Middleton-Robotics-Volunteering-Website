package com.example.application.Resources.Services.DataExporters;

import com.example.application.Entities.Event.exportEventDetails;
import com.example.application.Entities.User.StudentInfo;
import com.example.application.Resources.Services.DataService;
import com.example.application.Resources.Services.Database;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Route("DataExport")
public class UserDataExporter extends VerticalLayout {
    private ArrayList<String> row1Strings;//{"" , "" , "" , "IMPACT:"};
    private ArrayList<String> rowColStart;

    private ArrayList<String> row2Strings;//{"" , "" , "" , "STEM Related"};
    private ArrayList<String> row3Strings;//{"" , "" , "" , "FIRST Related"};
    private ArrayList<String> row4Strings; //{"Last Name" , "First Name" , "Program" , "# Events Signed up" , "# Events Approved"};
    private StudentInfo studentInfo;
    private List<String> info;//{"Dasari" , "Tarunkrishna" , "FTC 3846 - Maelstrom" , "5" , "2"};
    private ArrayList<Integer> eventsColumns;
    private List<exportEventDetails> exportEventDetail;
    private int[] coloredCells1;
    private int[] coloredCells2;
    private int[] coloredCells3;
    private int[] coloredCells4;
    private int[] coloredCells5;

    private XSSFWorkbook workbook;

    private String firstName;
    private String lastName;

    DataService dataService = new DataService();

    public UserDataExporter(String firstName , String lastName){
        eventsColumns = new ArrayList<>();
        rowColStart = new ArrayList<>();

        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Anchor configureExporter(){
        configureLists(firstName , lastName);
        configureFile();
        Anchor anchor;

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();;
        try {

            workbook.write(bos);
            workbook.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        anchor = new Anchor(new StreamResource(firstName + lastName + "RoboticsData.xlsx" , () -> new ByteArrayInputStream(bos.toByteArray())) , "Export");
        anchor.getElement().setAttribute("download", true);

        return anchor;

    }

    private void configureFile(){
        workbook = new XSSFWorkbook();
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
        coloredCells5 = new int[info.size() - 1];

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
/*
        sheet.setColumnWidth(eventsColumns.get(eventsColumns.size() - 1) + 1 , 17 * 256);*/

        RegionUtil.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN, region1, sheet);
        RegionUtil.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN, region1, sheet);

        RegionUtil.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN, region2, sheet);
        RegionUtil.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN, region2, sheet);

        RegionUtil.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN, region3, sheet);
        RegionUtil.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN, region3, sheet);

        sheet.addMergedRegion(region1);
        sheet.addMergedRegion(region2);
        sheet.addMergedRegion(region3);
    }

    private void configureLists(String firstName , String lastName){
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

        info = dataService.getUserInfo(firstName , lastName);
        System.out.println("info first: " + info.get(0));
        System.out.println("info first: " + info.get(1));
        System.out.println("info first: " + info.get(2));

        exportEventDetail = dataService.getAllExportEventDetailsWithStatus(firstName , lastName , "approved");
/*
        exportEventDetail = Database.getAllExportEventsWithStatus(firstName , lastName , "approved");*/
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
    }
}
