package com.spt.bas.web.util;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @Author: gaojy
 * @create 2022/2/15 9:48
 * @version: 1.0
 * @description:
 */
@Component
public abstract class ExcelExp {
    protected XSSFWorkbook xssWb;

    protected XSSFSheet xssSheet;

    protected HSSFWorkbook hssWb;

    protected HSSFSheet hssSheet;

    /**
     * 设置页脚
     */
    public abstract void createFooter();

    /**
     *
     * 插入行
     * @param startRow
     * @param rows
     */
    public abstract void insertRows(int startRow, int rows);

    /**
     *
     * 替换模板中变量
     * @param map
     */
    public abstract void replaceExcelData(Map<String, String> map);
    public void replaceExcelDataByType(Map<String, Object> map){
        throw new RuntimeException("replaceExcelDataByType未实现");
    }

    /**
     * 下载excel
     * @param response
     * @param filaName
     * @throws IOException
     */
    public abstract void downloadExcel(HttpServletResponse response, String filaName) throws IOException;


    public XSSFWorkbook getXssWb() {
        return xssWb;
    }


    public void setXssWb(XSSFWorkbook xssWb) {
        this.xssWb = xssWb;
    }


    public XSSFSheet getXssSheet() {
        return xssSheet;
    }


    public void setXssSheet(XSSFSheet xssSheet) {
        this.xssSheet = xssSheet;
    }


    public HSSFWorkbook getHssWb() {
        return hssWb;
    }


    public void setHssWb(HSSFWorkbook hssWb) {
        this.hssWb = hssWb;
    }


    public HSSFSheet getHssSheet() {
        return hssSheet;
    }


    public void setHssSheet(HSSFSheet hssSheet) {
        this.hssSheet = hssSheet;
    }

}
