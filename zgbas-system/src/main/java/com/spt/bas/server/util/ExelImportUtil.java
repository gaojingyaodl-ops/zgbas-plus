package com.spt.bas.server.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.spt.bas.client.vo.DaDiExcelVo;
import com.spt.bas.client.vo.PiccExcelVo;
import com.spt.bas.client.vo.PiccInsuranceExcelVo;
import com.spt.bas.client.vo.ZhongYinExcelVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @Author: gaojy
 * @create 2022/4/18 16:13
 * @version: 1.0
 * @description:
 */
@Slf4j
public class ExelImportUtil {
    //获取上传的List
    public static List<PiccExcelVo> getPiccExcelInfo(InputStream inputStream) {
        return createPiccExcel(inputStream, false);
    }

    public static List<DaDiExcelVo> getDaDiExcelInfo(InputStream inputStream) {
        return createDaDiExcel(inputStream, false);
    }
    
    public static List<ZhongYinExcelVo> getZhongYinExcelInfo(InputStream inputStream) {
        return createZhongYinExcel(inputStream, false);
    }
    
    public static List<PiccInsuranceExcelVo> getPiccInsuranceExcelInfo(InputStream inputStream) {
        return createPiccInsuranceExcel(inputStream, false);
    }

    /**
     * 根据excel里面的内容读取客户信息
     *
     * @param is          输入流
     * @param isExcel2003 excel是2003还是2007版本
     * @return
     * @throws IOException
     */
    public static List<PiccExcelVo> createPiccExcel(InputStream is, boolean isExcel2003) {
        try {
            Workbook wb;
            // 当excel是2003时,创建excel2003
            if (isExcel2003) {
                try{
                    wb = new HSSFWorkbook(is);
                } catch (Exception e) {
                    wb = new XSSFWorkbook(is);
                }
                // 当excel是2007时,创建excel2007
            } else {
                try {
                    wb = new XSSFWorkbook(is);
                } catch (Exception e) {
                    wb = new HSSFWorkbook(is);
                }
            }
            // 读取Excel信息
            return readPiccExcelValue(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据excel里面的内容读取客户信息
     *
     * @param is          输入流
     * @param isExcel2003 excel是2003还是2007版本
     * @return
     * @throws IOException
     */
    public static List<DaDiExcelVo> createDaDiExcel(InputStream is, boolean isExcel2003) {
        try {
            Workbook wb;
            // 当excel是2003时,创建excel2003
            if (isExcel2003) {
                try{
                    wb = new HSSFWorkbook(is);
                } catch (Exception e) {
                    wb = new XSSFWorkbook(is);
                }
                // 当excel是2007时,创建excel2007
            } else {
                try {
                    wb = new XSSFWorkbook(is);
                } catch (Exception e) {
                    wb = new HSSFWorkbook(is);
                }
            }
            // 读取Excel信息
            return readDaDiExcelValue(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 根据excel里面的内容读取客户信息
     *
     * @param is          输入流
     * @param isExcel2003 excel是2003还是2007版本
     * @return
     * @throws IOException
     */
    public static List<ZhongYinExcelVo> createZhongYinExcel(InputStream is, boolean isExcel2003) {
        try {
            Workbook wb;
            // 当excel是2003时,创建excel2003
            if (isExcel2003) {
                try{
                    wb = new HSSFWorkbook(is);
                } catch (Exception e) {
                    wb = new XSSFWorkbook(is);
                }
                // 当excel是2007时,创建excel2007
            } else {
                try {
                    wb = new XSSFWorkbook(is);
                } catch (Exception e) {
                    wb = new HSSFWorkbook(is);
                }
            }
            // 读取Excel信息
            return readZhongYinExcelValue(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据excel里面的内容读取人保保费流水信息
     *
     * @param is          输入流
     * @param isExcel2003 excel是2003还是2007版本
     * @return
     * @throws IOException
     */
    public static List<PiccInsuranceExcelVo> createPiccInsuranceExcel(InputStream is, boolean isExcel2003) {
        try {
            Workbook wb;
            // 当excel是2003时,创建excel2003
            if (isExcel2003) {
                try{
                    wb = new HSSFWorkbook(is);
                } catch (Exception e) {
                    wb = new XSSFWorkbook(is);
                }
                // 当excel是2007时,创建excel2007
            } else {
                try {
                    wb = new XSSFWorkbook(is);
                } catch (Exception e) {
                    wb = new HSSFWorkbook(is);
                }
            }
            // 读取Excel信息
            return readPiccInsuranceExcelValue(wb);
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
    private static List<PiccExcelVo> readPiccExcelValue(Workbook wb) {
        int totalRows;
        int totalCells = 0;
        // 得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        // 得到Excel的行数
        totalRows = sheet.getPhysicalNumberOfRows();
        // 得到Excel的列数(前提是有行数)
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        List<PiccExcelVo> resultList = new ArrayList<>();
        PiccExcelVo picc;
        String businessNo = null;
        // 获取保单号
//        Row sheetRow = sheet.getRow(1);
//        if (Objects.nonNull(sheetRow)) {
//            businessNo = sheetRow.getCell(1).getStringCellValue();
//        }
        // 循环Excel行数
        for (int r = 1; r <= totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            // 循环Excel的列
            picc = new PiccExcelVo();
            picc.setBussinessNo(businessNo);
            for (int c = 0; c < totalCells; c++) {
                Cell cell = row.getCell(c);
                if (Objects.isNull(cell)) {
                    continue;
                }
                String value = "";
                if (cell.getCellType() == CellType.STRING) {
                    value = cell.getStringCellValue();
                } else {
                    // 处理 NUMERIC 类型，例如将数字转为字符串
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    value = nf.format(cell.getNumericCellValue());

                }
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                try {
                    switch (c) {
                        case 0:
                            picc.setCorpSerialNo(value.trim());
                            break;
                        case 1:
                            picc.setCompanyName(value.trim());
                            break;
                        case 2:
                            picc.setRiskCompAddress(value);
                            break;
                        case 3:
                            picc.setPiccCode(value.trim());
                            break;
                        case 9:
                            picc.setAppliAmount(parseNumber(value));
                            break;
                        case 11:
                            picc.setApprovedQuota(parseNumber(value));
                        case 14:
                            picc.setPaidTerm(parseNumber(value));
                            break;
                        case 15:
                            picc.setPiccApproveDate(parseDate(value));
                            break;
                        case 16:
                            picc.setBussStartDate(parseDate(value));
                            break;
                        case 17:
                            picc.setBussEndDate(parseDate(value));
                            break;
                        case 18:
                            picc.setPiccHaveusedAmount(parseNumber(value));
                            break;
                        case 19:
                            picc.setPiccUseAbleaMount(parseNumber(value));
                            break;
                        case 20:
                            picc.setCompensationRatio(parseNumber(value));
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    log.error("readExcelValue error cellRow:{},errorMessage:{} ", r + "行", e);
                }
            }
            // 添加到list
            resultList.add(picc);
        }
        return resultList;
    }

    /**
     * 读取大地额度Excel信息
     *
     * @param wb
     * @return
     */
    private static List<DaDiExcelVo> readDaDiExcelValue(Workbook wb) {
        int totalRows;
        int totalCells = 0;
        // 得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        // 得到Excel的行数
        totalRows = sheet.getPhysicalNumberOfRows();
        // 得到Excel的列数(前提是有行数)
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        List<DaDiExcelVo> resultList = new ArrayList<>();
        DaDiExcelVo daDi;
        // 循环Excel行数
        for (int r = 1; r <= totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            // 循环Excel的列
            daDi = new DaDiExcelVo();
            
            for (int c = 0; c < totalCells; c++) {
                Cell cell = row.getCell(c);
                if (Objects.isNull(cell)) {
                    continue;
                }
                String value = "";
                if (cell.getCellType() == CellType.STRING) {
                    value = cell.getStringCellValue();
                } else {
                    // 处理 NUMERIC 类型，例如将数字转为字符串
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    value = nf.format(cell.getNumericCellValue());

                }
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                try {
                    switch (c) {
                        case 0:
                            daDi.setCompanyName(value.trim());
                            break;
                        case 1:
                            daDi.setDaDiCreditAmount(parseNumber(value.trim()));
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    log.error("readExcelValue error cellRow:{},errorMessage:{} ", r + "行", e);
                }
            }
            // 添加到list
            resultList.add(daDi);
        }
        return resultList;
    }
    
    /**
     * 读取中银额度Excel信息
     *
     * @param wb
     * @return
     */
    private static List<ZhongYinExcelVo> readZhongYinExcelValue(Workbook wb) {
        int totalRows;
        int totalCells = 0;
        // 得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        // 得到Excel的行数
        totalRows = sheet.getPhysicalNumberOfRows();
        // 得到Excel的列数(前提是有行数)
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        List<ZhongYinExcelVo> resultList = new ArrayList<>();
        ZhongYinExcelVo zhongYinExcelVo;
        // 循环Excel行数
        for (int r = 1; r <= totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            // 循环Excel的列
            zhongYinExcelVo = new ZhongYinExcelVo();
            
            for (int c = 0; c < totalCells; c++) {
                Cell cell = row.getCell(c);
                if (Objects.isNull(cell)) {
                    continue;
                }
                String value = "";
                if (cell.getCellType() == CellType.STRING) {
                    value = cell.getStringCellValue();
                } else {
                    // 处理 NUMERIC 类型，例如将数字转为字符串
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    value = nf.format(cell.getNumericCellValue());

                }
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                try {
                    switch (c) {
                        case 0:
                            zhongYinExcelVo.setCompanyName(value.trim());
                            break;
                        case 1:
                            zhongYinExcelVo.setZhongYinCreditAmount(parseNumber(value.trim()));
                            break;
                        case 2:
                            zhongYinExcelVo.setZhongYinApproveDate(parseDate(value.trim()));
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    log.error("readExcelValue error cellRow:{},errorMessage:{} ", r + "行", e);
                }
            }
            // 添加到list
            resultList.add(zhongYinExcelVo);
        }
        return resultList;
    }

    /**
     * 读取人保保费流水Excel信息
     *
     * @param wb
     * @return
     */
    private static List<PiccInsuranceExcelVo> readPiccInsuranceExcelValue(Workbook wb) {
        int totalRows;
        int totalCells = 0;
        // 得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        // 得到Excel的行数
        totalRows = sheet.getPhysicalNumberOfRows();
        // 得到Excel的列数(前提是有行数)
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        List<PiccInsuranceExcelVo> resultList = new ArrayList<>();
        PiccInsuranceExcelVo piccInsurance;
        // 循环Excel行数
        for (int r = 1; r <= totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            // 循环Excel的列
            piccInsurance = new PiccInsuranceExcelVo();

            for (int c = 0; c < totalCells; c++) {
                Cell cell = row.getCell(c);
                if (Objects.isNull(cell)) {
                    continue;
                }
                String value = "";
                if (cell.getCellType() == CellType.STRING) {
                    value = cell.getStringCellValue();
                } else {
                    // 处理 NUMERIC 类型，例如将数字转为字符串
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setGroupingUsed(false);
                    value = nf.format(cell.getNumericCellValue());

                }
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                try {
                    switch (c) {
                        case 8:
                            piccInsurance.setCreditCycle(value.trim());
                            break;
                        case 11:
                            piccInsurance.setContractNo(value.trim());
                            break;
                        case 14:
                            piccInsurance.setInsuranceRate(parseNumber(value.trim()));
                            break;
                        case 15:
                            piccInsurance.setInsuranceAmount(parseNumber(value.trim()));
                            break;
                        case 16:
                            piccInsurance.setEntryDate(value.trim());
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    log.error("readExcelValue error cellRow:{},errorMessage:{} ", r + "行", e);
                }
            }
            // 添加到list
            resultList.add(piccInsurance);
        }
        return resultList;
    }

    /**
     * 验证EXCEL文件
     *
     * @param filePath
     * @return
     */
    public static boolean validateExcel(String filePath) {
        return filePath != null && (isExcel2003(filePath) || isExcel2007(filePath));
    }

    //是否是2003的excel，返回true是2003
    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    //是否是2007的excel，返回true是2007
    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    public static Date parseDate(String dateStr) {
        try {
            return DateUtil.parse(dateStr);
        } catch (Exception e) {
            log.info("parseDate error:{}", dateStr);
        }
        return null;
    }

    public static String parseNumber(String numStr) {
        try {
            String trim = Pattern.compile("[^0-9.]").matcher(numStr).replaceAll("").replaceAll("\\.0*$", "").trim();
            return (StringUtils.isBlank(trim) || !NumberUtil.isNumber(trim)) ? "0" : trim;
        } catch (Exception e) {
            log.info("parseNumber error:{}", numStr);
        }
        return null;
    }

    public static void main(String[] args) {
        String aa = "3,000,000￥";
        String s = parseNumber(aa);
        log.info(s);
    }
}
