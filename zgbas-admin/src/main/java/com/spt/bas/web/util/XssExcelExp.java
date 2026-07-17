package com.spt.bas.web.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.spt.bas.client.entity.CtrContractLoadingDetail;
import com.spt.bas.client.vo.BusinessDeliveryExcelVo;
import com.spt.tools.core.number.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: gaojy
 * @create 2022/2/15 9:49
 * @version: 1.0
 * @description:
 */
@Component
public class XssExcelExp extends ExcelExp{
    protected Logger logger = LoggerFactory.getLogger(ExcelExp.class);
    public XssExcelExp() {
        super();
    }

    /**
     * 构造函数
     * ExcelExp
     * @param filePath 文件路径，如com/test/template/test.xlsx
     * @param sheetNum 要操作的页签，0为第一个页签
     * @throws IOException
     */
    public XssExcelExp(String filePath, int sheetNum) throws IOException {
        InputStream is = WorderToNewWordUtils.class.getResourceAsStream(filePath);
        // ZipSecureFile.setMinInflateRatio(-1.0d);
        xssWb = new XSSFWorkbook(is);
        xssSheet = xssWb.getSheetAt(sheetNum);
    }

    /**
     * 设置页脚
     */
    @Override
    public void createFooter(){
        Footer footer = xssSheet.getFooter();
        footer.setRight("第" + HSSFFooter.page() + "页，共" + HSSFFooter.numPages() + "页");
    }

    /**
     *
     * 插入行
     * @param startRow
     * @param rows
     */
    @Override
    public void insertRows(int startRow, int rows){
        int bottomRow = xssSheet.getLastRowNum();
        if(startRow > bottomRow){
            int n = startRow - bottomRow;
            for(int i = 1; i <= n; i++){
                xssSheet.createRow(bottomRow + i);
            }
        }
        xssSheet.shiftRows(startRow, xssSheet.getLastRowNum(), rows, true, false);
    }

    /**
     *
     * 替换模板中变量
     * @param map
     */
    @Override
    public void replaceExcelData(Map<String, String> map){
        int rowNum = xssSheet.getLastRowNum() + 5;
        for(int i = 0;i <= rowNum; i++){
            XSSFRow row = xssSheet.getRow(i);
            if(row == null) {
                continue;
            }
            // row.getPhysicalNumberOfCells() + 1 有数据读取不到的情况出现，故此多加1列
            for (int j = 0; j < row.getPhysicalNumberOfCells() + 10; j++) {
                XSSFCell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                cell.setCellType(CellType.STRING);
                String key = cell.getStringCellValue();
                if (map.containsKey(key)) {
                    cell.setCellValue(map.get(key));
                }
            }
        }
    }

    public void replaceExcelDataByType(Map<String, Object> map){
        int rowNum = xssSheet.getLastRowNum() + 5;
        for(int i = 0;i <= rowNum; i++){
            XSSFRow row = xssSheet.getRow(i);
            if(row == null) {
                continue;
            }
            // row.getPhysicalNumberOfCells() + 1 有数据读取不到的情况出现，故此多加1列
            for (int j = 0; j < row.getPhysicalNumberOfCells() + 10; j++) {
                XSSFCell cell = row.getCell(j);
                if (cell == null || cell.getCellType() == CellType.FORMULA) {
                    continue;
                }
                cell.setCellType(CellType.STRING);
                String key = cell.getStringCellValue();
                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    if (value instanceof MyCellType) {
                        MyCellType myCell = (MyCellType) value;
                        cell.setCellType(myCell.getType());
                        if (myCell.getType().equals(CellType.NUMERIC)) {
                            Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
                            Matcher isNum = pattern.matcher(myCell.getValue());
                            if (isNum.matches()) {
                                cell.setCellValue(Double.parseDouble(myCell.getValue()));
                                continue;
                            }
                        }
                        cell.setCellValue(myCell.getValue());
                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
            }
        }
    }


    /**
     * 下载excel
     * @param response
     * @param filaName
     * @throws IOException
     */
    @Override
    public void downloadExcel(HttpServletResponse response, String filaName) throws IOException{
        String encodeFileName = URLEncoder.encode(filaName,"UTF-8");
        response.addHeader("Content-Disposition","attachment;filename=" +encodeFileName);
        ServletOutputStream out = response.getOutputStream();
        xssSheet.setForceFormulaRecalculation(true);
        xssWb.write(out);
        out.flush();
        out.close();
    }

    public static void excelExp(String fileUrl,Map<String, String> map,HttpServletResponse response, String fileName) throws IOException {
        //传递模板地址和要操作的页签
        ExcelExp excel = new XssExcelExp(fileUrl, 0);

        //创建页脚，打印excel时显示页数
        excel.createFooter();

        //为模板中变量赋值
        excel.replaceExcelData(map);

        //导出，此处只封装了浏览器下载方式
        //调用downloadExcel，返回输出流给客户端
        excel.downloadExcel(response, fileName);
    }

    /**
     * 定制导出-提货单
     * @param fileUrl
     * @param paramMap
     * @param response
     * @param fileName
     * @throws IOException
     */
    public static void excelLoading(String fileUrl, Map<String, String> paramMap, List<CtrContractLoadingDetail> loadingDetails, HttpServletResponse response, String fileName) throws IOException {
        //传递模板地址和要操作的页签
        ExcelExp excel = new XssExcelExp(fileUrl, 0);

        //创建页脚，打印excel时显示页数
        excel.createFooter();

        //为模板中变量赋值
        int detailNum = loadingDetails.size();
        for (int i = 0; i < 5 - detailNum; i++) {
            loadingDetails.add(new CtrContractLoadingDetail());
            excel.getXssSheet().getRow(11 - i).setHeight((short) 0);
        }
        for (int i = 0; i < loadingDetails.size(); i++) {
            String index = i == 0 ? "" : String.valueOf(i);
            paramMap.put("productName" + index, loadingDetails.get(i).getProductName());
            paramMap.put("factoryName" + index, loadingDetails.get(i).getFactoryName());
            paramMap.put("brandNumber" + index, loadingDetails.get(i).getBrandNumber());
            paramMap.put("dealNumber" + index, NumberUtil.formatNumber(loadingDetails.get(i).getDealNumber(), "#.####"));
            paramMap.put("numberUnit" + index, loadingDetails.get(i).getNumberUnit());
            paramMap.put("plateNumber" + index, loadingDetails.get(i).getPlateNumber());
            paramMap.put("driverName" + index, loadingDetails.get(i).getDriverName());
            paramMap.put("driverCardNo" + index, loadingDetails.get(i).getDriverCardNo());
        }
        excel.replaceExcelData(paramMap);
        //导出，此处只封装了浏览器下载方式
        excel.downloadExcel(response, fileName);
    }


    /**
     * 定制导出-配送单
     * @param fileUrl
     * @param paramMap
     * @param response
     * @param fileName
     * @throws IOException
     */
    public static void excelDelivery(String fileUrl, Map<String, String> paramMap, List<CtrContractLoadingDetail> loadingDetails, HttpServletResponse response, String fileName) throws IOException {
        //传递模板地址和要操作的页签
        ExcelExp excel = new XssExcelExp(fileUrl, 0);

        //创建页脚，打印excel时显示页数
        excel.createFooter();

        //为模板中变量赋值
        int detailNum = loadingDetails.size();
        for (int i = 0; i < 5 - detailNum; i++) {
            loadingDetails.add(new CtrContractLoadingDetail());
            excel.getXssSheet().getRow(15 - i).setHeight((short) 0);
        }
        for (int i = 0; i < loadingDetails.size(); i++) {
            String index = i == 0 ? "" : String.valueOf(i);
            paramMap.put("productName" + index, loadingDetails.get(i).getProductName());
            paramMap.put("factoryName" + index, loadingDetails.get(i).getFactoryName());
            paramMap.put("brandNumber" + index, loadingDetails.get(i).getBrandNumber());
            paramMap.put("dealNumber" + index, NumberUtil.formatNumber(loadingDetails.get(i).getDealNumber(), "#.####"));
            paramMap.put("numberUnit" + index, loadingDetails.get(i).getNumberUnit());
        }
        excel.replaceExcelData(paramMap);
        //导出，此处只封装了浏览器下载方式
        excel.downloadExcel(response, fileName);
    }

    /**
     * 物流单据——送货通知单导出
     *
     * @param fileUrl  文件地址
     * @param paramMap 文件参数
     * @param response 响应
     * @param fileName 文件名
     * @throws IOException 异常
     */
    public static void deliveryNoticeExcel(String fileUrl, Map<String, String> paramMap, HttpServletResponse response, String fileName) throws IOException {
        //传递模板地址和要操作的页签
        ExcelExp excel = new XssExcelExp(fileUrl, 0);
        //创建页脚，打印excel时显示页数
        excel.createFooter();
        excel.replaceExcelData(paramMap);
        //导出，此处只封装了浏览器下载方式
        excel.downloadExcel(response, fileName);
    }

    /**
     * 物流单据——送货通知单导出
     *
     * @param fileUrl  文件地址
     * @param paramMap 文件参数
     * @param response 响应
     * @param fileName 文件名
     * @throws IOException 异常
     */
    public static void deliveryNoticeExcelByType(String fileUrl, Map<String, Object> paramMap, HttpServletResponse response, String fileName) throws IOException {
        //传递模板地址和要操作的页签
        ExcelExp excel = new XssExcelExp(fileUrl, 0);
        //创建页脚，打印excel时显示页数
        excel.createFooter();
        excel.replaceExcelDataByType(paramMap);
        //导出，此处只封装了浏览器下载方式
        excel.downloadExcel(response, fileName);
    }

    /**
     * 定制导出-签收单
     * @param fileUrl
     * @param paramMap
     * @param response
     * @param fileName
     * @throws IOException
     */
    public static void excelReceive(String fileUrl, Map<String, String> paramMap, List<CtrContractLoadingDetail> loadingDetails, HttpServletResponse response, String fileName) throws IOException {
        //传递模板地址和要操作的页签
        ExcelExp excel = new XssExcelExp(fileUrl, 0);

        //创建页脚，打印excel时显示页数
        excel.createFooter();

        //为模板中变量赋值
        int detailNum = loadingDetails.size();
        for (int i = 0; i < 5 - detailNum; i++) {
            loadingDetails.add(new CtrContractLoadingDetail());
            excel.getXssSheet().getRow(9 - i).setHeight((short) 0);
        }
        for (int i = 0; i < loadingDetails.size(); i++) {
            String index = i == 0 ? "" : String.valueOf(i);
            paramMap.put("productName" + index, loadingDetails.get(i).getProductName());
            paramMap.put("factoryName" + index, loadingDetails.get(i).getFactoryName());
            paramMap.put("brandNumber" + index, loadingDetails.get(i).getBrandNumber());
            paramMap.put("dealNumber" + index, NumberUtil.formatNumber(loadingDetails.get(i).getDealNumber(), "#.####"));
            paramMap.put("numberUnit" + index, loadingDetails.get(i).getNumberUnit());
        }
        excel.replaceExcelData(paramMap);
        //导出，此处只封装了浏览器下载方式
        excel.downloadExcel(response, fileName);
    }

    /**
     * 定制导出-导出面单
     * @param fileUrl
     * @param map
     * @param response
     * @param fileName
     * @throws IOException
     */
    public static void excelReceiptDelivery(String fileUrl, Map<String, String> map, List<BusinessDeliveryExcelVo.ExcelDetail> detailList, HttpServletResponse response, String fileName) throws IOException {
        //传递模板地址和要操作的页签
        ExcelExp excel = new XssExcelExp(fileUrl, 0);

        //创建页脚，打印excel时显示页数
        excel.createFooter();

        //插入行
        int startRow = 5;//起始行
        int rows = detailList.size();//插入行数
        excel.insertRows(startRow, rows);

        //在插入的行中写入数据
        XssExcelExp.wirteXssExcel(excel, detailList);

        //为模板中变量赋值
        excel.replaceExcelData(map);

        //导出，此处只封装了浏览器下载方式
        excel.downloadExcel(response, fileName);
    }

    public static void wirteXssExcel(ExcelExp excel, List<BusinessDeliveryExcelVo.ExcelDetail> detailList) {
        XSSFSheet sheet = excel.getXssSheet();
        // XSSFCellStyle rowStyle = excel.getXssSheet().getRow(3).getRowStyle();
        for (int i = 0; i < detailList.size(); i++) {
            XSSFRow row = sheet.createRow(i + 4);
            row.createCell(0).setCellValue(parseDefaultParam(detailList.get(i).getCompanyName()));
            row.createCell(1).setCellValue(parseDefaultParam(DateUtil.format(detailList.get(i).getExcelDate(), DatePattern.CHINESE_DATE_PATTERN)));
            row.createCell(2).setCellValue(detailList.get(i).getSubject());
            row.createCell(3).setCellValue(detailList.get(i).getContractNo());
            row.createCell(4).setCellValue(parseDefaultParam(NumberUtil.formatNumber(detailList.get(i).getDeliveryInNumber(), "#.###")));
            row.createCell(5).setCellValue(parseDefaultParam(NumberUtil.formatNumber(detailList.get(i).getDeliveryNumber(), "#.###")));
            row.createCell(6).setCellValue(parseDefaultParam(NumberUtil.formatNumber(detailList.get(i).getDeliveryNoNumber(), "#.###")));

            // row.setRowStyle(rowStyle);
        }
    }

    private static String parseDefaultParam(String param){
        return StringUtils.isNotBlank(param) ? param : "";
    }

    public static class MyCellType {
        private CellType type;
        private String value;

        public CellType getType() {
            return type;
        }

        public void setType(CellType type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public MyCellType(CellType type, String value) {
            this.type = type;
            this.value = value;
        }

        public MyCellType() {
        }
    }
}
