package org.loxf.jyadmin.base.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Excel导入工具
 */
public class ExcelUtil {
    /**
     * 获取excel表头
     *
     * @param is
     * @param fileName
     * @return
     * @throws IOException
     */
    public static List<String> loadTitle(InputStream is, String fileName) throws IOException {
        List<String> set = new ArrayList<String>();
        try {
            if (null == is) {
                return set;
            }
            Workbook wb = null;
            String ext = getExtensionName(fileName);
            if ("xls".equals(ext)) {
                wb = new HSSFWorkbook(is);
            } else if ("xlsx".equals(ext)) {
                wb = new XSSFWorkbook(is);
            } else {
                throw new RuntimeException("必须为xls,xlsx类型");
            }
            Sheet sheet = wb.getSheetAt(0);
            int rowNumb = sheet.getFirstRowNum();
            Row row = sheet.getRow(rowNumb);
            int cellNumb = row.getLastCellNum();
            for (int i = 0; i < cellNumb; i++) {
                set.add(row.getCell(i).toString().trim());
            }
        } finally {
            if (null != is) {
                is.close();
            }
        }
        return set;
    }


    /**
     * 获取excel表格数据
     *
     * @param is
     * @param fileName
     * @return
     * @throws IOException
     */
    public static List<List<String>> loadData(InputStream is, String fileName) throws IOException {
        List<List<String>> dataList = new ArrayList<List<String>>();
        try {
            if (null == is) {
                return dataList;
            }
            Workbook wb = null;
            String ext = getExtensionName(fileName);
            if ("xls".equals(ext)) {
                wb = new HSSFWorkbook(is);
            } else if ("xlsx".equals(ext)) {
                wb = new XSSFWorkbook(is);
            } else {
                throw new RuntimeException("必须为xls,xlsx类型");
            }
            Sheet sheet = wb.getSheetAt(0);
            int rowNumb = sheet.getFirstRowNum();
            int lastRowNumb = sheet.getLastRowNum();
            for (int i = rowNumb + 1; i <= lastRowNumb; i++) {
                Row row = sheet.getRow(i);
                int cellNumb = row.getLastCellNum();
                List<String> data = new ArrayList<String>();
                for (int j = 0; j < cellNumb; j++) {
                    Cell cell = row.getCell(j);
                    if (null == cell) {
                        data.add("");
                        continue;
                    }
                    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            Date d = cell.getDateCellValue();
                            DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            data.add(formater.format(d));
                        } else {
                            DecimalFormat decimalFormat = new DecimalFormat("0");
                            String value = decimalFormat.format(cell.getNumericCellValue());
                            if (StringUtils.isNotBlank(value) && value.endsWith(".0")) {
                                value = value.replace(".0", "");
                            }
                            data.add(value);
                        }
                    } else {
                        data.add(cell.getStringCellValue());
                    }
                }
                dataList.add(data);
            }
        } finally {
            is.close();
        }
        return dataList;
    }

    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
    private static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1).toLowerCase();
            }
        }
        return filename;
    }
}
