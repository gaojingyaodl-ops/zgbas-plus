package com.spt.bas.server.cache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.spt.bas.client.entity.BsFactory;
import com.spt.bas.server.service.IBsFactoryService;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.util.SpringContextHolder;

public final class FactoryCache {
	private static AtomicBoolean isInited = new AtomicBoolean(false);
	private static final Logger log = LoggerFactory.getLogger(FactoryCache.class);
	private static LoadingCache<Long, BsFactory> cache;

	private FactoryCache() {
		if (!isInited.get()) {
			init();
		}
	}

	public static void init() {
		log.info("---初始化厂商缓存");
		cache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
				.build(new CacheLoader<Long, BsFactory>() {
					@Override
					public BsFactory load(Long nodeId) throws Exception {
						// 在每次get，从缓存那东西的时候，如果是第一次，调用load方法，如果是两次get时间在60分钟之间，就不会调用load,这个load
						IBsFactoryService service = SpringContextHolder.getBean(IBsFactoryService.class);
						BsFactory entity = service.getEntity(nodeId);
						return entity;

					}
				});

		LocalCacheManager.register(cache);
		isInited.set(true);
	}

	public static BsFactory getEntity(Long id) {
		if (id == null || id == 0) {
			return null;
		}
		try {
			return cache.get(id);
		} catch (Exception e) {
			IBsFactoryService service = SpringContextHolder.getBean(IBsFactoryService.class);
			BsFactory entity = service.getEntity(id);
			return entity;
		}
	}

	public static String getFactoryName(Long id) {
		BsFactory entity = getEntity(id);
		if (entity != null) {
			return entity.getFactoryName();
		}
		return null;

	}
}
