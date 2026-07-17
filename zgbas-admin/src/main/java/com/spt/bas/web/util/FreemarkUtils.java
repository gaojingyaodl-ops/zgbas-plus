package com.spt.bas.web.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkUtils {
	private static Logger logger = LoggerFactory.getLogger(FreemarkUtils.class);

	public static String merge(String content, Map<String, Object> param) {
		return merge(content, param);
	}

	public static String merge(String content, Object param) {
		Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		StringWriter sw = new StringWriter();
		try {
			Template t = new freemarker.template.Template("", new StringReader(content), cfg);
			t.process(param, sw);
			content = sw.toString();
		} catch (Exception e) {
			logger.error("合并模板异常", e);
		}
		return content;
	}

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
		String templatePath = FreemarkUtils.class.getResource("/").getPath()+ "template";
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
