package com.spt.bas.server.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.spt.auth.sdk.entity.SysEnterpriseSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class EnterpriseCache {

	private static AtomicBoolean isInited = new AtomicBoolean(false);
	private static final Logger log = LoggerFactory.getLogger(EnterpriseCache.class);
	private static LoadingCache<Long, SysEnterpriseSdk> cache;

	private EnterpriseCache() {
		if (!isInited.get()) {
			init();
		}
	}

	public static void init() {
		log.info("---初始化EnterpriseCache");
		cache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
				.build(new CacheLoader<Long, SysEnterpriseSdk>() {
					@Override
					public SysEnterpriseSdk load(Long enterpriseId) throws Exception {
						// 在每次get，从缓存那东西的时候，如果是第一次，调用load方法，如果是两次get时间在60分钟之间，就不会调用load,这个load
						IAuthOpenFacade service = SpringContextHolder.getBean(IAuthOpenFacade.class);
						SysEnterpriseSdk entity = service.findEnterpriseById(enterpriseId);
						return entity;

					}
				});

		LocalCacheManager.register(cache);
		isInited.set(true);
	}

	public static SysEnterpriseSdk getEntity(Long enterpriseId) {
		if (enterpriseId == null || enterpriseId == 0) {
			return null;
		}
		try {
			return cache.get(enterpriseId);
		} catch (Exception e) {
			IAuthOpenFacade service = SpringContextHolder.getBean(IAuthOpenFacade.class);
			SysEnterpriseSdk entity = service.findEnterpriseById(enterpriseId);
			return entity;
		}
	}

	public static boolean isHGIndustry(Long enterpriseId){
		SysEnterpriseSdk entity = getEntity(enterpriseId);
		if(entity == null){
			return false;
		}else{
			if("HG".equals(entity.getIndustry())){
				return true;
			}else{
				return false;
			}
		}
	}
}
