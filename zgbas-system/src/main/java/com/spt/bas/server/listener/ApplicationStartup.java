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
		// Phase 5 Plan 05-05 (D-P5-09): init the WX dict cache — same-root fix for the
		// Phase-3 /wx/* login gap (memory authsdk-static-cache-init-gap: WX /wx/* dict
		// cache was never initialized). Fully-qualified to disambiguate from the monolith
		// com.spt.bas.server.cache.BsDictUtil above; the WX ApplicationStartup is NOT
		// migrated wholesale (would add a 2nd ApplicationReadyEvent listener and double-run
		// DictUtil/executor). DictUtil.init(appCode) and new Thread(executor) stay single.
		com.spt.bas.purchase.wx.server.cache.BsDictUtil.init();
		new Thread(executor).start();
		logger.info("---Application已启动---");
	}

}
