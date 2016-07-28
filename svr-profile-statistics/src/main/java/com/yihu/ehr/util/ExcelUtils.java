package com.yihu.ehr.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Created by lyr on 2016/7/27.
 */
public class ExcelUtils {

    /**
     * 创建Excel文档
     * @return
     */
    public static HSSFWorkbook createWorkBook() {
        return new HSSFWorkbook();
    }

    /**
     * 创建sheet
     * @param wb
     * @param sheetName
     * @return
     */
    public static HSSFSheet createSheet(HSSFWorkbook wb, String sheetName) {
        return wb.createSheet(sheetName);
    }

    /**
     * 创建一行多列
     * @param sheet
     * @param rowNum
     * @param columnNum
     * @param cellStyle
     * @return
     */
    public static HSSFRow createRow(HSSFSheet sheet, int rowNum, int columnNum, HSSFCellStyle cellStyle) {
        HSSFRow row = sheet.createRow(rowNum);

        if (columnNum > 0) {
            for (int i = 0; i < columnNum; i++) {
                HSSFCell cell = row.createCell(i);
                if (cellStyle != null) {
                    cell.setCellStyle(cellStyle);
                }
            }
        }

        return row;
    }

    /**
     * 创建多行多列
     * @param sheet
     * @param rowNum
     * @param rowCount
     * @param columnNum
     * @param cellStyle
     * @return
     */
    public static boolean createRows(HSSFSheet sheet, int rowNum, int rowCount, int columnNum, HSSFCellStyle cellStyle) {
        for (int i = 0; i < rowCount; i++) {
            HSSFRow row = sheet.createRow(rowNum + i);

            if (columnNum > 0) {
                for (int j = 0; j < columnNum; j++) {
                    HSSFCell cell = row.createCell(j);
                    if (cellStyle != null) {
                        cell.setCellStyle(cellStyle);
                    }
                }
            }
        }

        return true;
    }

    /**
     * 合并单元格
     * @param sheet
     * @param firstRow
     * @param lastRow
     * @param firstCol
     * @param lastCol
     * @return
     */
    public static int mergeRegion(HSSFSheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        int num = sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
        return num;
    }

    /**
     * 创建单元格样式
     * @param wb
     * @param align
     * @param bold
     * @param border
     * @param backColor
     * @return
     */
    public static HSSFCellStyle createCellStyle(HSSFWorkbook wb, short align, boolean bold, boolean border, short backColor) {
        HSSFCellStyle style = wb.createCellStyle();

        //字体
        HSSFFont font = wb.createFont();

        font.setBold(bold);
        font.setFontName("宋体");
        font.setFontHeight((short) 220);
        style.setFont(font);

        //设置对齐
        style.setAlignment(align);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        if (border) {
            // 设置边框
            style.setBottomBorderColor(HSSFColor.BLACK.index);
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        }

        if (backColor > 0) {
            style.setFillBackgroundColor((short) backColor);
        }

        return style;
    }
}
