/**
 *
 */
package com.spt.bas.server.listener;

import com.spt.auth.sdk.cache.ConfigUtil;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.cache.UserCache;
import com.spt.bas.server.cache.BsCompanyOurUtil;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.cache.FactoryCache;
import com.spt.bas.server.cache.WarehouseCache;
import com.spt.bas.server.util.BsCompanyIndustryUtil;
import com.spt.bas.server.util.ProductTypeUtility;
import com.spt.bas.server.util.TemplateContentUtility;
import com.spt.pm.cache.DeptCache;
import com.spt.pm.cache.PmNodeCache;
import com.spt.tools.core.cmd.CommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 应用启动监听器
 *
 * @author huangjian
 *
 */
@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CommandExecutor executor;
	@Value("${spt.app.appCode}")
	private String appCode;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		// 初始化缓存
		DictUtil.init(appCode);
		ConfigUtil.init();
		BsDictUtil.init();
		BsCompanyOurUtil.init();
		UserCache.init();
		DeptCache.init();
		PmNodeCache.init();
		TemplateContentUtility.init();
		ProductTypeUtility.init();
		BsCompanyIndustryUtil.init();
		FactoryCache.init();
		WarehouseCache.init();
		new Thread(executor).start();
		logger.info("---Application已启动---");
	}

}
