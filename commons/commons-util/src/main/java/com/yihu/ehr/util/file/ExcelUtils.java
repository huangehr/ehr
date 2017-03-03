package com.yihu.ehr.util.file;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel 工具类
 *
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/3/2.
 */
public class ExcelUtils {

    /** 总行数 */
    private int totalRows = 0;
    /** 总列数 */
    private int totalCells = 0;

    public int getTotalRows() {
        return totalRows;
    }

    public int getTotalCells() {
        return totalCells;
    }

    /**
     * 创建Excel文档
     *
     * @return
     */
    public static HSSFWorkbook createWorkBook() {
        return new HSSFWorkbook();
    }

    /**
     * 创建sheet
     *
     * @param wb
     * @param sheetName
     * @return
     */
    public static HSSFSheet createSheet(HSSFWorkbook wb, String sheetName) {
        return wb.createSheet(sheetName);
    }

    /**
     * 创建一行多列
     *
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
     *
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
     *
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
     *
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


    /* **************************************** Excel 读取 *************************************************************** */

    /**
     *  读取Excel内容
     * @param excelFile  Excel文件
     * @return
     * @throws IOException
     */
    public static List<Map<String,Object>> readExcel(File excelFile) throws IOException {
        List<Map<String,Object>> list = null;
        boolean isExcel2007 = isExcel2007(excelFile.getName());
        if (isExcel2007){
            list = readExcel2007(excelFile);
        }else {
            list = readExcel2003(excelFile);
        }
        return list;
    }
    
    public static List<Map<String,Object>> readExcel2003(File excelFile) throws IOException {
        InputStream is = new FileInputStream(excelFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        Map<String,Object> student = null;
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            //获取表头字段
            HSSFRow columnName = hssfSheet.getRow(0);
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    student = setCellVal(columnName,hssfRow);
                    list.add(student);
                }
            }
        }
        return list;
    }

    @SuppressWarnings("static-access")
    public static String getExcelValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            // 返回字符串类型的值
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

    public static List<Map<String,Object>> readExcel2007(File excelFile) throws IOException {
        InputStream is = new FileInputStream(excelFile);
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is);
        Map<String,Object> result = null;
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            //获取表头字段
            XSSFRow columnName = xssfSheet.getRow(0);
            // 循环行Row
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow hssfRow = xssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    result = setCellVal(columnName,hssfRow);
                    list.add(result);
                }
            }
        }
        return list;
    }

    @SuppressWarnings("static-access")
    public static String getExcelValue(XSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            // 返回字符串类型的值
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

    public static  Map<String,Object> setCellVal(XSSFRow names,XSSFRow values) {
        Map<String,Object> map = new HashMap<>();
        String name =null;
        String value =null;
        for (int i=0;i<names.getLastCellNum();i++){
            name = getExcelValue(names.getCell(i));
            value = getExcelValue(values.getCell(i));
            map.put(name,value);
        }
        return map;
    }

    public static  Map<String,Object> setCellVal(HSSFRow names,HSSFRow values) {
        Map<String,Object> map = new HashMap<>();
        String name =null;
        String value =null;
        for (int i=0;i<names.getLastCellNum();i++){
            name = getExcelValue(names.getCell(i));
            value = getExcelValue(values.getCell(i));
            map.put(name,value);
        }
        return map;
    }
    
    public static boolean isExcel2007(String filePath)
    {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }



    public static void main(String[] args) throws IOException {
        String path ="e://test.xlsx";
        List<Map<String, Object>> list = ExcelUtils.readExcel(new File(path));
        System.out.println(list.toString());
    }

}
