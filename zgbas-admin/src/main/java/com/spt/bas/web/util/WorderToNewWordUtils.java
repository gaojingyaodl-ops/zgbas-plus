package com.spt.bas.web.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;



public class WorderToNewWordUtils {
	
	/**下载合同模板*/
	public static File loadTemplate(String urlPath) {
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		HttpURLConnection httpUrl = null;
		File file =null;
		URL url = null;
		int BUFFER_SIZE = 1024;
		byte[] buf = new byte[BUFFER_SIZE];
		int size = 0;
		try {
			url = new URL(urlPath);
			httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			file =File.createTempFile("template", "docx");
			file.deleteOnExit();
			fos = new FileOutputStream(file);
			while ((size = bis.read(buf)) != -1) {
				fos.write(buf, 0, size);
			}
			fos.flush();
		} catch (IOException e) {
		} catch (ClassCastException e) {
		} finally {
			try {
				fos.close();
				bis.close();
				httpUrl.disconnect();
			} catch (IOException e) {
			} catch (NullPointerException e) {
			}
		}
		return file;
	}
	
	 /**
     * 根据模板生成新word文档
     * 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
     * @param inputUrl 模板存放地址
     * @param outPutUrl 新文档存放地址
     * @param textMap 需要替换的信息集合
     * @param tableList 需要插入的表格信息集合
     * @return 成功返回true,失败返回false
     */
    public static File changWord(String inputUrl, Map<String, String> textMap, List<String[]> tableList) {
    	 File file = null;
        //模板转换默认成功

        try {
        	InputStream in =WorderToNewWordUtils.class.getResourceAsStream(inputUrl);
            //获取docx解析对象
            XWPFDocument document = new XWPFDocument(in);
            //解析替换文本段落对象
            WorderToNewWordUtils.changeText(document, textMap);
            //解析替换表格对象
            WorderToNewWordUtils.changeTable(document, textMap, tableList);
 
            //生成新的word
            file = File.createTempFile("contract",".docx");
            //  file = new File(outputUrl);
            file.deleteOnExit();
            FileOutputStream stream = new FileOutputStream(file);
            document.write(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            
        }
 
        return file;
 
    }
    
    public static File changWord2(InputStream in, Map<String, String> textMap, List<String[]> tableList) {
   	 File file = null;
       //模板转换默认成功

       try {
           //获取docx解析对象
           XWPFDocument document = new XWPFDocument(in);
           //解析替换文本段落对象
           WorderToNewWordUtils.changeText(document, textMap);
           //解析替换表格对象
           WorderToNewWordUtils.changeTable(document, textMap, tableList);

           //生成新的word
           file = File.createTempFile("contract",".docx");
           //  file = new File(outputUrl);
           file.deleteOnExit();
           FileOutputStream stream = new FileOutputStream(file);
           document.write(stream);
           stream.close();
       } catch (IOException e) {
           e.printStackTrace();
           
       }

       return file;

   }
    /**
     * 替换段落文本
     * @param document docx解析对象
     * @param textMap 需要替换的信息集合
     */
    public static void changeText(XWPFDocument document, Map<String, String> textMap){
        //获取段落集合
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (Iterator<XWPFParagraph> iterator = paragraphs.iterator(); iterator.hasNext();) {
			XWPFParagraph paragraph = (XWPFParagraph) iterator.next();
			 //判断此段落时候需要进行替换
            String text = paragraph.getText();
            if(checkText(text)){
				String ptxt = paragraph.getText();
				String pvalue = FreemarkUtils.merge(ptxt, textMap); //changeValue(ptxt, textMap);
				List<XWPFRun> runs = paragraph.getRuns();
				int size = runs.size();
                for(int i =0;i<size;i++) {
                	paragraph.removeRun(0);
                }
			 	XWPFRun r1=paragraph.createRun();
                r1.setText(pvalue);
                
//                List<XWPFRun> runs = paragraph.getRuns();
//                for (XWPFRun run : runs) {
//                    //替换模板原来位置
//                    run.setText(changeValue(run.toString(), textMap),0);
//                }
            }
		}
//        for (XWPFParagraph paragraph : paragraphs) {
//            //判断此段落时候需要进行替换
//            String text = paragraph.getText();
//            if(checkText(text)){
//                List<XWPFRun> runs = paragraph.getRuns();
//                for (XWPFRun run : runs) {
//                    //替换模板原来位置
//                    run.setText(changeValue(run.toString(), textMap),0);
//                }
//            }
//        }
 
    }
 
    /**
     * 替换表格对象方法
     * @param document docx解析对象
     * @param textMap 需要替换的信息集合
     * @param tableList 需要插入的表格信息集合
     */
    public static void changeTable(XWPFDocument document, Map<String, String> textMap,
            List<String[]> tableList){
        //获取表格对象集合
        List<XWPFTable> tables = document.getTables();
        for (int i = 0; i < tables.size(); i++) {
            //只处理行数大于等于2的表格，且不循环表头
            XWPFTable table = tables.get(i);
            if(table.getRows().size()>1){
                //判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
                if(checkText(table.getText())){
                    List<XWPFTableRow> rows = table.getRows();
                    //遍历表格,并替换模板
                    eachTable(rows, textMap);
                }else{
//                  System.out.println("插入"+table.getText());
                    insertTable(document,table, tableList,textMap);
                }
            }else{
          	  List<XWPFTableRow> rows = table.getRows();
              //遍历表格,并替换模板
              eachTable(rows, textMap);
        }
        }
    }
 
 
 
 
 
    /**
     * 遍历表格
     * @param rows 表格行对象
     * @param textMap 需要替换的信息集合
     */
    public static void eachTable(List<XWPFTableRow> rows ,Map<String, String> textMap){
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                //判断单元格是否需要替换
                if(checkText(cell.getText())){
                    List<XWPFParagraph> paragraphs = cell.getParagraphs();
                    for (Iterator<XWPFParagraph> iterator = paragraphs.iterator(); iterator.hasNext();) {
						XWPFParagraph paragraph = (XWPFParagraph) iterator.next();
						String ptxt = paragraph.getText();
						String pvalue = FreemarkUtils.merge(ptxt, textMap); //changeValue(ptxt, textMap);
						List<XWPFRun> runs = paragraph.getRuns();
						int size = runs.size();
		                for(int i =0;i<size;i++) {
		                	paragraph.removeRun(0);
		                }
					 	XWPFRun r1=paragraph.createRun();
		                r1.setText(pvalue);
//		                cell.setParagraph(paragraph);
		              
						
//						for (XWPFRun run : runs) {
//							String value = changeValue(run.toString(), textMap);
//							run.setText(value, 0);
//						}
                    }
                }
            }
        }
    }
 
    /**
     * 为表格插入数据，行数不够添加新行
     * @param table 需要插入数据的表格
     * @param tableList 插入数据集合
     */
    public static void insertTable(XWPFDocument document,XWPFTable table, List<String[]> tableList,Map<String, String> textMap){
        //创建行,根据需要插入的数据添加新行，不处理表头
        for(int i = 1; i < tableList.size(); i++){
            XWPFTableRow row =table.createRow();
        }
        //遍历表格插入数据
        List<XWPFTableRow> rows = table.getRows();
        for(int i = 1; i < rows.size(); i++){
            XWPFTableRow newRow = table.getRow(i);
            List<XWPFTableCell> cells = newRow.getTableCells();
            for(int j = 0; j < cells.size(); j++){
                XWPFTableCell cell = cells.get(j);
                XWPFParagraph p1=cell.getParagraphs().get(0);
                XWPFRun r1=p1.createRun();//p1.createRun()将一个新运行追加到这一段
                r1.setText(tableList.get(i-1)[j]);
                r1.setFontSize(9);//---字体大小
                cell.setParagraph(p1);
//                cell.setText(tableList.get(i-1)[j]);
            }
        }
        
        XWPFTableRow rownew =table.createRow();
        int cc =rows.size()-1;
        mergeCellsHorizontal(table,cc,0,6,textMap);
    }
    
    public static  void mergeCellsHorizontal(XWPFTable table, int row, int fromCell, int toCell,Map<String, String> textMap) {  
        for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {  
            XWPFTableCell cell = table.getRow(row).getCell(cellIndex);  
            if ( cellIndex == fromCell ) {  
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);  
            } else {  
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);  
            } 
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            cell.setText(textMap.get("total"));
        }  
    }
 
    /**
     * 判断文本中时候包含$
     * @param text 文本
     * @return 包含返回true,不包含返回false
     */
    public static boolean checkText(String text){
        boolean check  =  false;
        if (StringUtils.isBlank(text)) {
			text = "";
        }
        if(text.indexOf("$")!= -1){
            check = true;
        }
        return check;
 
    }
 
    /**
     * 匹配传入信息集合与模板
     * @param value 模板需要替换的区域
     * @param textMap 传入信息集合
     * @return 模板需要替换区域信息集合对应值
     */
    private static String changeValue(String value, Map<String, String> textMap){
        Set<Entry<String, String>> textSets = textMap.entrySet();
        for (Entry<String, String> textSet : textSets) {
            //匹配模板与替换值 格式${key}
            String key = "${"+textSet.getKey()+"}";
            if (value!=null ) {
            	if(value.indexOf(key)!= -1){
                    value = textSet.getValue();
                    break;
                }
            } else {
            	System.out.println("changeValue>>>>"+value);
            }
        }
        //模板未匹配到区域替换为空
        if(checkText(value)){
            value = "";
        }
        return value;
    }
    
    
    /**
     * 根据模板生成新word文档
     * 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
     * @param inputUrl 模板存放地址
     * @param outPutUrl 新文档存放地址
     * @param textMap 需要替换的信息集合
     * @param tableList 需要插入的表格信息集合
     * @return 成功返回true,失败返回false
     */
    public static File changWords(String inputUrl, String outputUrl,
            Map<String, String> textMap, List<String[]> tableList) {

        //模板转换默认成功
   	 File file = null;
        try {
        	InputStream in =WorderToNewWordUtils.class.getResourceAsStream(inputUrl);
            //获取docx解析对象
            XWPFDocument document = new XWPFDocument(in);
            //解析替换文本段落对象
            WorderToNewWordUtils.changeText(document, textMap);
            //解析替换表格对象
            WorderToNewWordUtils.changeTable(document, textMap, tableList);
 
            //生成新的word
             file = new File(outputUrl);
            FileOutputStream stream = new FileOutputStream(file);
            document.write(stream);
            stream.close();
 
        } catch (IOException e) {
            e.printStackTrace();
          
        }
 
        return file;
 
    }
	
	public static void download(HttpServletResponse response, File file) {
		if (!file.exists()) {
			System.out.println("下载文件不存在");
		}

		try {
			response.reset();
			// 设置ContentType
			response.setContentType("application/octet-stream; charset=utf-8");
			// 处理中文文件名中文乱码问题
			String fileName = new String(file.getName().getBytes("utf-8"), "ISO-8859-1");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			IOUtils.copy(new FileInputStream(file), response.getOutputStream());
		} catch (Exception e) {
		}
	}

    /**
     * 延时指定时间后，主动删除临时文件
     *
     * @param file
     * @param delay
     * @param timeUnit
     */
    public static void scheduleFileDeletion(File file, long delay, TimeUnit timeUnit) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, delay, timeUnit);

        executor.shutdown();
    }
}
