package com.spt.bas.server.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.spt.tools.core.constants.CommonErrorId;
import com.spt.tools.core.exception.ApplicationException;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkUtils {

	public static String merge(String content,Map<String,Object> param) throws ApplicationException {
		return merge(content, param);
	}
	public static String merge(String content,Object param) throws ApplicationException {
		Configuration  cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS); 
		StringWriter sw = new StringWriter();
		try {
			Template t  = new freemarker.template.Template("", new StringReader(content), cfg);
			t.process(param, sw);
			content = sw.toString();
		}  catch (Exception e) {
			throw new ApplicationException(CommonErrorId.ERROR_DATA_EXCHANGE, "合并模板异常", e);
		}
		return content;
	}
	
	public static Date getMaxDate(List<Date> timeList) {
		Date maxDate = timeList.get(0);
		for (int i = 1; i < timeList.size(); i++) {
			Date date = timeList.get(i);
			if (maxDate == null) {
				maxDate = date;
			}
			if (date != null && maxDate != null && date.after(maxDate)) {
				maxDate = date;
			}
			
		}
		return maxDate;
	}
	
}
