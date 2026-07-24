package com.spt.bas.purchase.wx.server.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class FreemarkerUtil {
	private static Logger logger = LoggerFactory.getLogger(FreemarkerUtil.class);

	/**
	 * 获取模板内容
	 *
	 * @param template 模板文件
	 * @param map      模板参数
	 * @return 渲染后的模板内容
	 * @throws IOException       IOException
	 * @throws TemplateException TemplateException
	 */
	public static String getTemplate(String template, Map map) throws IOException, TemplateException {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);
		String templatePath = FreemarkerUtil.class.getResource("/").getPath()+ "template";
		logger.info("FreemarkerUtil template  = " + template);
		logger.info("templatePath = " + templatePath);
		cfg.setDirectoryForTemplateLoading(new File(templatePath));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
		cfg.setWrapUncheckedExceptions(true);
		Template temp = cfg.getTemplate(template);
		StringWriter stringWriter = new StringWriter();
		temp.process(map, stringWriter);
		return stringWriter.toString();
	}

}
