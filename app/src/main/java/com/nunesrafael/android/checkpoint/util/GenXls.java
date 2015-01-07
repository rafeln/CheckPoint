package com.nunesrafael.android.checkpoint.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GenXls {

    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("ddMMyyyy_HHmmss");

    private static HSSFCellStyle cellStyle;
    private static HSSFCellStyle headerStyle;

    public static void export(Context context, String[] columns, List<Object[]> rows) {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Relatório de ponto");
        //sheet.getPrintSetup().setLandscape(true);

        ArrayList<Object[]> rowsArr = new ArrayList<>();
        rowsArr.add(columns);
        rowsArr.addAll(rows);

        short cellDateFormat = workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy");

        int rownum = 0;
        for (Object[] rowObject : rowsArr) {
            Row row = sheet.createRow(rownum++);
            int cellnum = 0;
            for (Object obj : rowObject) {
                Cell cell = row.createCell(cellnum++);

                //header
                if (rownum == 1) {
                    cell.setCellStyle(getHeaderCellStyle(workbook));
                } else {//body
                    cell.setCellStyle(getBodyCellStyle(workbook));
                }

                if(obj instanceof Date) {
                    CellStyle cellStyle = cell.getCellStyle();
                    cellStyle.setDataFormat(cellDateFormat);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue((Date)obj);
                } else if(obj instanceof Boolean) {
                    cell.setCellValue((Boolean)obj);
                } else if(obj instanceof String) {
                    cell.setCellValue((String)obj);
                } else if(obj instanceof Number) {
                    cell.setCellValue(Double.parseDouble(obj.toString()));
                } else {
                    if (obj != null) {
                        cell.setCellValue(obj.toString());
                    }
                }

            }
        }

        sheet.setColumnWidth(0, 3000);
//        for (int i = 0; i < columns.length; i++) {
//            sheet.autoSizeColumn(i);
//        }

        try {

            String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + FILE_DATE_FORMAT.format(new Date()) + ".xls";

            FileOutputStream report = new FileOutputStream(filePath);
            workbook.write(report);
            report.flush();
            report.close();

            Toast.makeText(context, "Exportado para: " + filePath, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.ms-excel");
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cellStyle = null;
            headerStyle = null;
        }
    }

    private static HSSFCellStyle getHeaderCellStyle(HSSFWorkbook workbook) {

        if (cellStyle != null) {
            return cellStyle;
        }

        headerStyle = workbook.createCellStyle();

        HSSFFont font = workbook.createFont();
        font.setBoldweight((short) 2);
        font.setFontHeightInPoints((short) 14);
        font.setFontName("Arial");
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        headerStyle.setFont(font);
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        headerStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        addBorder(headerStyle);

        return headerStyle;
    }

    private static HSSFCellStyle getBodyCellStyle(HSSFWorkbook workbook) {

        if (cellStyle != null) {
            return cellStyle;
        }
        cellStyle = workbook.createCellStyle();

        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Arial");

        cellStyle.setFont(font);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        addBorder(cellStyle);

        return cellStyle;
    }

    private static void addBorder(HSSFCellStyle style) {
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor(HSSFColor.BLACK.index);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setTopBorderColor(HSSFColor.BLACK.index);
    }

}