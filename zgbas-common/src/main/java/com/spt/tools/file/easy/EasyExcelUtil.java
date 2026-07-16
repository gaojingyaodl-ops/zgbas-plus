package com.spt.tools.file.easy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;

public class EasyExcelUtil {

	/**
	 * 针对较少的记录数(20W以内大概)可以调用该方法一次性查出然后写入到EXCEL的一个SHEET中 注意：
	 * 一次性查询出来的记录数量不宜过大，不会内存溢出即可。
	 *
	 * @throws IOException
	 */
	public static void write(String filePath, List<? extends BaseRowModel> userList,
			Class<? extends BaseRowModel> clazz) throws IOException {

		// 生成EXCEL并指定输出路径
		OutputStream out = new FileOutputStream(filePath);
		write(out, userList, clazz);
	}

	public static void write(OutputStream out, List<? extends BaseRowModel> userList,
			Class<? extends BaseRowModel> clazz) throws IOException {

		// 生成EXCEL并指定输出路径
		ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
		// 设置SHEET
		Sheet sheet = new Sheet(1, 0, clazz);
		sheet.setSheetName("sheet1");
		// 设置列宽 设置每列的宽度
//		Map<Integer, Integer> columnWidth = new HashMap<>();
//		columnWidth.put(0,15000);columnWidth.put(1,3000);columnWidth.put(2,3000);
//		sheet.setColumnWidthMap(columnWidth);
//		sheet.setAutoWidth(Boolean.TRUE);
		writer.write(userList, sheet);
		writer.finish();
	}

	public static void write(String filePath, List<List<String>> lstData, List<List<String>> titles)
			throws IOException {
		OutputStream out = new FileOutputStream(filePath);
		write(out, lstData, titles);
	}

	public static void write(OutputStream out, List<List<String>> lstData, List<List<String>> titles)
			throws IOException {

		// 生成EXCEL并指定输出路径
		ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
		// 设置SHEET
		Sheet sheet = new Sheet(1, 0);
		sheet.setSheetName("sheet1");
		// 设置标题
		Table table = new Table(1);
		table.setHead(titles);
		writer.write0(lstData, sheet);
		writer.finish();
	}

	/** 返回指定类型数据 */
	public static <T extends BaseRowModel> List<T> read(String filePath, Class<T> clazz) {
		List<T> rows = new ArrayList<T>();
		try {
			File file = new File(filePath);
			// 解析每行结果在listener中处理
			InputStream is = new FileInputStream(file);
			rows = read(is, clazz);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rows;
	}

	public static <T extends BaseRowModel> List<T> read(InputStream is, Class<T> clazz) {
		List<T> rows = new ArrayList<T>();
		ExcelReader reader = new ExcelReader(is, null, new ReadEventListener<T>(rows), false);
		Sheet sheet = new Sheet(1, 0);
		sheet.setClazz(clazz);
		reader.read(sheet);
		return rows;
	}

	/** 返回默认类型数据 */
	public static List<List<String>> read(String filePath) {
		List<List<String>> rows = null;
		try {
			File file = new File(filePath);
			// 解析每行结果在listener中处理
			InputStream is = new FileInputStream(file);
			rows = read(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rows;
	}

	public static List<List<String>> read(InputStream is) {
		List<List<String>> rows = new ArrayList<List<String>>();

		ExcelReader reader = new ExcelReader(is, null, new ReadEventListener<List<String>>(rows), false);
		Sheet sheet = new Sheet(1, 0);
		reader.read(sheet);
		return rows;
	}

}
