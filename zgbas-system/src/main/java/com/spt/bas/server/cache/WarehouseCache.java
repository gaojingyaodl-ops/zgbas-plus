package com.spt.bas.server.cache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.server.service.IBsWarehouseService;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.util.SpringContextHolder;

public final class WarehouseCache {
	private static AtomicBoolean isInited = new AtomicBoolean(false);
	private static final Logger log = LoggerFactory.getLogger(WarehouseCache.class);
	private static LoadingCache<Long, BsWarehouse> cache;

	private WarehouseCache() {
		if (!isInited.get()) {
			init();
		}
	}

	public static void init() {
		log.info("---初始化仓库缓存");
		cache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
				.build(new CacheLoader<Long, BsWarehouse>() {
					@Override
					public BsWarehouse load(Long nodeId) throws Exception {
						// 在每次get，从缓存那东西的时候，如果是第一次，调用load方法，如果是两次get时间在60分钟之间，就不会调用load,这个load
						IBsWarehouseService service = SpringContextHolder.getBean(IBsWarehouseService.class);
						BsWarehouse entity = service.getEntity(nodeId);
						return entity;

					}
				});

		LocalCacheManager.register(cache);
		isInited.set(true);
	}

	public static BsWarehouse getEntity(Long id) {
		if (id == null || id == 0) {
			return null;
		}
		try {
			return cache.get(id);
		} catch (Exception e) {
			IBsWarehouseService service = SpringContextHolder.getBean(IBsWarehouseService.class);
			BsWarehouse entity = service.getEntity(id);
			return entity;
		}
	}

	public static String getWarehouseName(Long id) {
		BsWarehouse entity = getEntity(id);
		if (entity != null) {
			return entity.getWarehouseName();
		}
		return null;

	}
}
