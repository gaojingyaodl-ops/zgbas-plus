package com.spt.pm.cache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.spt.pm.entity.PmProcessNode;
import com.spt.pm.service.IPmProcessNodeService;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.util.SpringContextHolder;

public final class PmNodeCache {
	private static AtomicBoolean isInited = new AtomicBoolean(false);
	private static final Logger log = LoggerFactory.getLogger(PmNodeCache.class);
	private static LoadingCache<Long, PmProcessNode> cache;

	private PmNodeCache() {
		if (!isInited.get()) {
			init();
		}
	}

	public static void init() {
		log.info("---初始化审批节点缓存");
		cache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
				.build(new CacheLoader<Long, PmProcessNode>() {
					@Override
					public PmProcessNode load(Long nodeId) throws Exception {
						// 在每次get，从缓存那东西的时候，如果是第一次，调用load方法，如果是两次get时间在60分钟之间，就不会调用load,这个load
						IPmProcessNodeService service = SpringContextHolder.getBean(IPmProcessNodeService.class);
						PmProcessNode entity = service.getEntity(nodeId);
						return entity;

					}
				});

		LocalCacheManager.register(cache);
		isInited.set(true);
	}

	public static PmProcessNode getEntity(Long id) {
		if (id == null || id == 0) {
			return null;
		}
		try {
			return cache.get(id);
		} catch (Exception e) {
			IPmProcessNodeService service = SpringContextHolder.getBean(IPmProcessNodeService.class);
			PmProcessNode entity = service.getEntity(id);
			return entity;
		}
	}

	public static String getNodeName(Long id) {
		PmProcessNode entity = getEntity(id);
		if (entity != null) {
			return entity.getNodeName();
		}
		return null;

	}
	public static String getNodeCode(Long id) {
		PmProcessNode entity = getEntity(id);
		if (entity != null) {
			return entity.getNodeCode();
		}
		return null;
		
	}
}
