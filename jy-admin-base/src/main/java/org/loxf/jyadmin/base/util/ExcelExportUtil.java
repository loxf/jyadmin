package org.loxf.jyadmin.base.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ExcelExportUtil {
    /**
     * Excel文件导出
     *
     * @param fileName      导出文件名
     * @param titles        表格标题
     * @param propertyNames 表格数据对应po属性
     * @param dataList
     * @param response
     */
    public static void doExport(String fileName, String[] titles, String[] propertyNames, List<?> dataList, HttpServletResponse response) {
        ServletOutputStream out = null;
        HSSFWorkbook workbook1 = makeExcelHead(fileName, titles.length - 1);
        HSSFWorkbook workbook2 = makeSecondHead(workbook1, titles);
        try {
            out = response.getOutputStream();
            HSSFWorkbook workbook = exportExcelData(workbook2, dataList, propertyNames);
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1") + ".xls";
            response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
            workbook.write(out);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成excel头部信息
     *
     * @param title
     * @param cellRangeAddressLength
     * @return
     */
    private static HSSFWorkbook makeExcelHead(String title, int cellRangeAddressLength) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFCellStyle styleTitle = createStyle(workbook, (short) 16);
        HSSFSheet sheet = workbook.createSheet(title);
        sheet.setDefaultColumnWidth(25);
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, cellRangeAddressLength);
        sheet.addMergedRegion(cellRangeAddress);
        HSSFRow rowTitle = sheet.createRow(0);
        HSSFCell cellTitle = rowTitle.createCell(0);
        // 为标题设置背景颜色
        styleTitle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        styleTitle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        cellTitle.setCellValue(title);
        cellTitle.setCellStyle(styleTitle);
        return workbook;
    }

    /**
     * 设定二级标题
     *
     * @param workbook
     * @param secondTitles
     * @return
     */
    private static HSSFWorkbook makeSecondHead(HSSFWorkbook workbook, String[] secondTitles) {
        // 创建用户属性栏
        HSSFSheet sheet = workbook.getSheetAt(0);
        HSSFRow rowField = sheet.createRow(1);
        HSSFCellStyle styleField = createStyle(workbook, (short) 13);
        for (int i = 0; i < secondTitles.length; i++) {
            HSSFCell cell = rowField.createCell(i);
            cell.setCellValue(secondTitles[i]);
            cell.setCellStyle(styleField);
        }
        return workbook;
    }

    /**
     * 执行导出操作
     *
     * @param workbook
     * @param dataList
     * @param beanPropertys
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private static <T> HSSFWorkbook exportExcelData(HSSFWorkbook workbook, List<T> dataList, String[] beanPropertys) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HSSFSheet sheet = workbook.getSheetAt(0);
        // 填充数据
        HSSFCellStyle styleData = workbook.createCellStyle();
        styleData.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleData.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        for (int j = 0; j < dataList.size(); j++) {
            HSSFRow rowData = sheet.createRow(j + 2);
            T t = dataList.get(j);
            for (int k = 0; k < beanPropertys.length; k++) {
                Object value = BeanUtils.getProperty(t, beanPropertys[k]);
                value = (null == value) ? "" : value;
                HSSFCell cellData = rowData.createCell(k);
                cellData.setCellValue(value.toString());
                cellData.setCellStyle(styleData);
            }
        }
        return workbook;
    }

    /**
     * 提取公共的样式
     *
     * @param workbook
     * @param fontSize
     * @return
     */
    private static HSSFCellStyle createStyle(HSSFWorkbook workbook, short fontSize) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 创建一个字体样式
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints(fontSize);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);
        return style;
    }
}
