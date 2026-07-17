package com.spt.bas.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.spt.bas.client.entity.BasBrand;

public class ImportExcelUtil {
	
	//获取上传的List
	public static List<BasBrand> getExcelInfo(MultipartFile mFile) {  
    	//获取文件名 
    	String fileName = mFile.getOriginalFilename(); 
        try {
        	// 验证文件名是否合格  
            if (!validateExcel(fileName)) {
                return null;  
            }  
            // 根据文件名判断文件是2003版本还是2007版本  
            boolean isExcel2003 = true;
            if (isExcel2007(fileName)) {  
                isExcel2003 = false;  
            }  
            List<BasBrand> brandList = createExcel(mFile.getInputStream(), isExcel2003);
            return brandList;
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
    
  /** 
   * 根据excel里面的内容读取客户信息 
   * @param is 输入流 
   * @param isExcel2003 excel是2003还是2007版本 
   * @return 
   * @throws IOException 
   */  
    public static List<BasBrand> createExcel(InputStream is, boolean isExcel2003) {  
        
    	try{  
            Workbook wb = null; 
            // 当excel是2003时,创建excel2003 
            if (isExcel2003) { 
                wb = new HSSFWorkbook(is);
             // 当excel是2007时,创建excel2007
            } else {  
                wb = new XSSFWorkbook(is);  
            }  
            // 读取Excel里面的牌号信息
            List<BasBrand> brandList = readExcelValue(wb);  
            return brandList;
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
    
  /** 
   * 读取Excel里面客户的信息 
   * @param wb 
   * @return 
   */  
    private static List<BasBrand> readExcelValue(Workbook wb) {
    	int totalRows = 0;
    	int totalCells = 0;
        // 得到第一个shell  
        Sheet sheet = wb.getSheetAt(0);  
        // 得到Excel的行数  
        totalRows = sheet.getPhysicalNumberOfRows();  
        // 得到Excel的列数(前提是有行数)  
        if (totalRows > 1 && sheet.getRow(0) != null) {  
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();  
        }  
        List<BasBrand> brandList = new ArrayList<BasBrand>();  
        // 循环Excel行数  
        for (int r = 1; r < totalRows; r++) {  
            Row row = sheet.getRow(r);  
            if (row == null){  
                continue;  
            }  
            BasBrand brand = new BasBrand();  
            // 循环Excel的列  
            for (int c = 0; c < totalCells; c++) {  
                Cell cell = row.getCell(c);  
                if (null != cell) {  
                    if (c == 0) { 
                    	if(cell.getCellType() == CellType.NUMERIC){
                            String productCd = String.valueOf(cell.getNumericCellValue()); 
                            brand.setProductCd(productCd.substring(0, productCd.length()-2>0?productCd.length()-2:1));
                    	}else{
                    		brand.setProductCd(cell.getStringCellValue());
                    	}
                    } else if (c == 1) { 
                    	if(cell.getCellType() == CellType.NUMERIC){
                            String brandNumber = String.valueOf(cell.getNumericCellValue()); 
                            brand.setBrandNumber(brandNumber.substring(0, brandNumber.length()-2>0?brandNumber.length()-2:1));
                    	}else{
                    		brand.setBrandNumber(cell.getStringCellValue());
                    	}
                    }  
                }  
            }  
            // 添加到list
            brandList.add(brand);  
        }  
        return brandList;  
    }  
      
    /** 
     * 验证EXCEL文件 
     * @param filePath 
     * @return 
     */  
    public static boolean validateExcel(String filePath) {  
        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))) {  
            return false;  
        }  
        return true;  
    }  
      
    //是否是2003的excel，返回true是2003   
    public static boolean isExcel2003(String filePath)  {    
         return filePath.matches("^.+\\.(?i)(xls)$");    
     }    
     
    //是否是2007的excel，返回true是2007   
    public static boolean isExcel2007(String filePath)  {    
         return filePath.matches("^.+\\.(?i)(xlsx)$");    
     }
    
}
