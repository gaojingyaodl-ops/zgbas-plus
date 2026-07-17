package com.spt.bas.server.util;

import com.spt.bas.client.entity.RptBaseCost;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author: zhaoww
 * @create 2023/3/22 16:13
 * @version: 1.0
 * @description:
 */
@Slf4j
public class BaseCostExcelImportUtil {
    //获取上传的List
    public static List<RptBaseCost> getExcelInfo(InputStream inputStream) {
        return createExcel(inputStream);
    }

    /**
     * 根据excel里面的内容读取客户信息
     *
     * @param is          输入流
     * @return
     * @throws IOException
     */
    public static List<RptBaseCost> createExcel(InputStream is) {
        try {
            Workbook wb = WorkbookFactory.create(is);
            // 读取Excel信息
            return readExcelValue(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取限额批复Excel信息
     *
     * @param wb
     * @return
     */
    private static List<RptBaseCost> readExcelValue(Workbook wb) {
        int totalRows;
        int totalCells = 0;
        // 得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        // 得到Excel的行数
        totalRows = sheet.getPhysicalNumberOfRows();
        // 得到Excel的列数(前提是有行数)
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(3).getPhysicalNumberOfCells();
        }
        List<RptBaseCost> resultList = new ArrayList<>();
        RptBaseCost rptBaseCost;
        String dateString = null;
        // 获取保单号
        Row sheetRow = sheet.getRow(2);
        if (Objects.nonNull(sheetRow)) {
            String dateStr = new DataFormatter().formatCellValue(sheetRow.getCell(4));
            dateString = dateStr;
            try {
                DateFormat fmt =new SimpleDateFormat("yyyy-MM");
                Date date = fmt.parse(dateStr);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (StringUtils.isBlank(dateString)) {
            log.error("日期不能为空!");
            return resultList;
        }
        // 循环Excel行数
        for (int r = 3; r <= totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            String userName = new DataFormatter().formatCellValue(row.getCell(2));
            if (StringUtils.isBlank(userName)) {
                continue;
            }
            // 循环Excel的列
            rptBaseCost = new RptBaseCost();
            for (int c = 2; c < totalCells; c++) {
                Cell cell = row.getCell(c);
                if (Objects.isNull(cell)) {
                    continue;
                }
                double numericValue = 0.0d;
                if (c == 10 && cell.getCellType() == CellType.FORMULA) {
                    numericValue = cell.getNumericCellValue();
                }
                String value = new DataFormatter().formatCellValue(cell);
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                try {
                    switch (c) {
                        case 2:
                            rptBaseCost.setMatchUserName(value.trim());
                            break;
                        case 3:
                            rptBaseCost.setBranchName(value.trim());
                            break;
                        case 4:
                            rptBaseCost.setWages(parseBigDecimal(value.trim()));
                            break;
                        case 5:
                            rptBaseCost.setCommission(parseBigDecimal(value.trim()));
                            break;
                        case 6:
                            rptBaseCost.setOtherCost(parseBigDecimal(value.trim()));
                            break;
                        case 7:
                            rptBaseCost.setSocialSecurity(parseBigDecimal(value.trim()));
                            break;
                        case 8:
                            rptBaseCost.setProvidentFund(parseBigDecimal(value.trim()));
                            break;
                        case 9:
                            rptBaseCost.setEvectionCost(parseBigDecimal(value.trim()));
                            break;
                        case 10:
                            if (numericValue > 0) {
                                rptBaseCost.setTotalCost(new BigDecimal(Double.toString(numericValue)));
                            } else {
                                rptBaseCost.setTotalCost(parseBigDecimal(value.trim()));
                            }
                            break;
                        case 11:
                            rptBaseCost.setRemark(value.trim());
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    log.error("readExcelValue error cellRow:{},errorMessage:{} ", r + "行", e.getMessage());
                }
            }
            rptBaseCost.setBaseDate(dateString);
            // 添加到list
            resultList.add(rptBaseCost);
        }
        return resultList;
    }

    public static BigDecimal parseBigDecimal(String bigDecimalStr) {
        try {
            if (bigDecimalStr.contains(",")) {
                bigDecimalStr = bigDecimalStr.replace("," , "");
            }
            if (bigDecimalStr.contains("，")) {
                bigDecimalStr = bigDecimalStr.replace("，" , "");
            }
            return new BigDecimal(bigDecimalStr);
        } catch (Exception e) {
            log.error("parseBigDecimal error:{}", bigDecimalStr);
        }
        return null;
    }
}
