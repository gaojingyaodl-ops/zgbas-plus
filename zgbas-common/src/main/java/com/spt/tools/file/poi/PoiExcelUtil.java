/**
 * 
 */
package com.spt.tools.file.poi;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.spt.tools.core.date.DateOperator;

/**
 * XSS
 * 
 * @author huangjian
 * 
 */
public class PoiExcelUtil {

	public static final String WB_TYPE_2003 = "2003";
	public static final String WB_TYPE_2007 = "2007";
	public static final String WB_TYPE_2010 = "2010";

	public static int maxLine = 65535;

	public static Workbook newWorkbook(String type) {
		Workbook wb=null;
		switch (type) {
		case WB_TYPE_2003:
			wb = new HSSFWorkbook();
			break;
		case WB_TYPE_2007:
			wb = new XSSFWorkbook();
			break;
		case WB_TYPE_2010:
			wb = new SXSSFWorkbook();
			break;
		default:
			wb = new HSSFWorkbook();
			break;
		}
		return wb;
	}

	/**
	 * 2007标题样式XSSFWorkbook
	 * 
	 * @param wb
	 * @return
	 */
	public static CellStyle getTitleStyle(Workbook wb) {

		CellStyle style = wb.createCellStyle(); // 标题样式
		style.setAlignment(HorizontalAlignment.CENTER); // 水平居中
		style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setWrapText(true);
		style.setFillForegroundColor(HSSFColorPredefined.GREY_40_PERCENT.getIndex());
		style.setFillBackgroundColor(HSSFColorPredefined.GREY_40_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}

	/**
	 * 2007内容样式XSSFWorkbook
	 * 
	 * @param wb
	 * @return
	 */
	public static CellStyle getCellStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle(); // 单元格样式
		style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		// style.setWrapText(true);
		// style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
		// style.setFillBackgroundColor(HSSFColor.GREY_40_PERCENT.index);
		// style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		return style;
	}

	/**
	 * 2007样式XSSFWorkbook(自定义格式：如yyyy-MM-d，##.##)
	 * 
	 * @param wb
	 * @return
	 */
	public static CellStyle getCellDataStyle(Workbook wb, String formart) {
		CellStyle style = wb.createCellStyle(); // 单元格样式
		style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		DataFormat format = wb.createDataFormat();
		style.setDataFormat(format.getFormat(formart));
		return style;
	}

	/** 创建表头 */
	public static void creatHeads(Workbook wb, Sheet sheet, String[] titles, int[] widthes) {
		createHeadsForstartRow(wb, sheet, titles, widthes, 0);
	}

	/****
	 * 
	 * @param wb
	 * @param sheet
	 * @param titles
	 * @param widthes
	 *            长度
	 * @param startRow
	 *            开始行 0为第一列
	 */
	public static void createHeadsForstartRow(Workbook wb, Sheet sheet, String[] titles, int[] widthes, int startRow) {
		Row head =sheet.createRow(startRow);
		CellStyle titleStyle = getTitleStyle(wb);
		head.setHeightInPoints(25);
		for (int i = 0; i < titles.length; i++) {
			Cell cell = head.createCell(i);
			cell.setCellValue(titles[i]);
			cell.setCellStyle(titleStyle);
			sheet.setColumnWidth((short) i, 256 * widthes[i]); // 调整第一列宽度:256 * 30
		}

	}

	/**
	 * 导出2007文件
	 * 
	 * @param wb
	 * @param response
	 * @param title
	 * @throws IOException
	 */
	public static void write(Workbook wb, HttpServletResponse response, String title) throws IOException {

		if (wb == null) {
			wb = new XSSFWorkbook();
		}
		String suffix =".xlsx";
		if (wb instanceof HSSFWorkbook) {
			suffix =".xls";
		}
		response.setHeader("Content-Disposition",
				"attachment;filename=" + new String((title).getBytes("gbk"), "iso8859-1") + suffix);
		response.setHeader("Connection", "close");
		response.setHeader("Content-Type", "application/vnd.ms-excel");
		wb.write(response.getOutputStream());
	}

	public static <T> Workbook createWorkbook(String title, String[] titles, String[] attrs, List<T> dataset) {

		return createWorkbook(null, title, titles, attrs, dataset, DateOperator.FORMAT_STR);

	}

	public static <T> Workbook createWorkbook(String title, String[] titles, String[] attrs, List<T> dataset,
			String pattern) {

		return createWorkbook(null, title, titles, attrs, dataset, pattern);

	}

	public static <T> Workbook createWorkbook(Workbook workbook, String title, String[] titles, String[] attrs,
			List<T> dataset) {
		return createWorkbook(workbook, title, titles, attrs, dataset, DateOperator.FORMAT_STR);
	}

	/**
	 * 这是一个通用的方法，利用了 JAVA 的反射机制，可以将放置在 JAVA 集合中并且符号一定条件的数据以 EXCEL 的形式输出到指定 IO 设备上
	 * 
	 * @param title
	 *            表格标题名
	 * @param jsonValue
	 *            例如:{mobile='手机号',amount='充值总额',merchantName='商家',.....}
	 * @param dataset
	 *            需要显示的数据集合 , 集合中一定要放置符合 javabean 风格的类的对象。此方法支持的 javabean
	 *            属性的数据类型有基本数据类型及 String,Date
	 * @param pattern
	 *            如果有时间数据，设定输出格式。默认为 "yyyy-MM-dd"
	 */
	public static <T> Workbook createWorkbook(Workbook workbook, String title, String[] titles, String[] attrs,
			List<T> dataset, String pattern) {
		int linesNum = 0;
		// 声明一个工作薄
		if (workbook == null) {
			workbook = new XSSFWorkbook();
		}
		if (dataset != null) {
			linesNum = dataset.size();
		}
		if (maxLine >= linesNum) {
			// 创建单元格格式
			createSheet(title, dataset, pattern, titles, attrs, workbook);
		} else {
			throw new RuntimeException("poi导出excel版本为2003兼容版，每sheet不能超过" + maxLine + "行。现总行数为：" + linesNum);
		}

		return workbook;

	}

	/**
	 * 现在用的poi版本导出excel版本为2007-2003兼容版本 excel最大行数支持为65536行 进行判断是否超过，如超过则新建sheet
	 * 
	 * @param title
	 * @param dataset
	 * @param pattern
	 * @param jsonObject
	 * @param workbook
	 * @return: void
	 */
	private static <T> void createSheet(String title, List<T> dataset, String pattern, String[] titles, String[] attrs,
			Workbook workbook) {

		// 生成一个表格
		Sheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为 15 个字节
		sheet.setDefaultColumnWidth(15);
		// 产生表格标题行
		// 生成一个样式
		CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
		/** 创建表头 */
		int[] widthes = new int[titles.length];
		for (int i = 0; i < titles.length; i++) {
			widthes[i] = 15;
		}
		creatHeads(workbook, sheet, titles, widthes);

		// 遍历集合数据，产生数据行
		createRows(sheet, dataset, attrs, 0, cellStyle, pattern);
	}

	public static void createRows(Sheet sheet, List<?> list, String[] attrs, int start, CellStyle cellStyle) {
		createRows(sheet, list, attrs, start, cellStyle, DateOperator.FORMAT_STR_WITH_TIME);
	}

	public static void createRows(Sheet sheet, List<?> list, String[] attrs, int start, CellStyle cellStyle,
			String pattern) {

		Row row;
		for (Object o : list) {
			start++;
			row = sheet.createRow(start);
			row.setHeightInPoints(20);
			for (int i = 0; i < attrs.length; i++) {
				String attrName = attrs[i];
				Cell cell = row.createCell(i);
				cell.setCellStyle(cellStyle);
				try {
					Object value = PropertyUtils.getProperty(o, attrName);
					// 判断值的类型后进行强制类型转换
					setCellValue(cell, value, pattern);
				} catch (Exception e) {

				}
			}
		}
	}

	public static void setCellValue(Cell cell, Object value) {
		setCellValue(cell, value, DateOperator.FORMAT_STR_WITH_TIME);
	}

	public static void setCellValue(Cell cell, Object value, String pattern) {

		if (value instanceof BigDecimal) {
			double intValue = ((BigDecimal) value).doubleValue();
			cell.setCellValue(intValue);
			cell.setCellType(CellType.NUMERIC);
			// style.setDataFormat(df.getFormat("#,##0.00"));
		} else if (value instanceof Float) {
			float fValue = (Float) value;
			cell.setCellValue(fValue);
			cell.setCellType(CellType.NUMERIC);
			// style.setDataFormat(df.getFormat("#,##0.00"));
		} else if (value instanceof Double) {
			double dValue = (Double) value;
			cell.setCellValue(dValue);
			cell.setCellType(CellType.NUMERIC);
			// style.setDataFormat(df.getFormat("#,##0.00"));
		} else if (value instanceof Long) {
			long longValue = (Long) value;
			cell.setCellValue(longValue);
			cell.setCellType(CellType.NUMERIC);
			// style.setDataFormat(df.getFormat("#,##0.00"));
		} else if (value instanceof Date) {
			Date date = (Date) value;
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			String textValue = sdf.format(date);
			cell.setCellValue(textValue);
		} else if (value instanceof Integer) {
			Integer intValue = (Integer) value;
			cell.setCellValue(intValue);
			cell.setCellType(CellType.NUMERIC);
		} else {
			// 其它数据类型都当作字符串简单处理
			String textValue = value == null ? "" : value.toString();
			// Pattern p = Pattern.compile("^//d+(//.//d+)?$");
			// Matcher matcher = p.matcher(textValue);
			// if ( matcher.matches() ) {
			// // 是数字当作 double 处理
			// cell.setCellValue(Double.parseDouble(textValue));
			// } else {
			// // HSSFRichTextString richString = new
			// // HSSFRichTextString(textValue);
			// // HSSFFont font3 = workbook.createFont();
			// // richString.applyFont(font3);
			// cell.setCellValue(textValue);
			// }
			cell.setCellValue(textValue);
		}
		// return textValue;
	}

}
