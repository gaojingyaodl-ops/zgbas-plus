package com.spt.pm.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DeptCache {
	private static AtomicBoolean isInited = new AtomicBoolean(false);
	private static final Logger log = LoggerFactory.getLogger(DeptCache.class);
	private static LoadingCache<Long, SysDeptSdk> cache;

	private DeptCache() {
		if (!isInited.get()) {
			init();
		}
	}

	public static void init() {
		log.info("---初始化机构缓存");
		cache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
				.build(new CacheLoader<Long, SysDeptSdk>() {
					@Override
					public SysDeptSdk load(Long deptId) throws Exception {
						// 在每次get，从缓存那东西的时候，如果是第一次，调用load方法，如果是两次get时间在60分钟之间，就不会调用load,这个load
						IAuthOpenFacade service = SpringContextHolder.getBean(IAuthOpenFacade.class);
						SysDeptSdk entity = service.findDeptById(deptId);
						return entity;

					}
				});

		LocalCacheManager.register(cache);
		isInited.set(true);
	}

	public static SysDeptSdk getEntity(Long deptId) {
		if (deptId == null || deptId == 0) {
			return null;
		}
		try {
			return cache.get(deptId);
		} catch (Exception e) {
			IAuthOpenFacade service = SpringContextHolder.getBean(IAuthOpenFacade.class);
			SysDeptSdk entity = service.findDeptById(deptId);
			return entity;
		}
	}

	public static String getDeptName(List<Object> lstDeptIds,String sperate) {
		if (lstDeptIds==null) {
			return null;
		}
//		List<String> lstDeptIds = Splitter.on(sperate).splitToList(userIds);
		StringBuilder sbDeptNames =new StringBuilder();
		lstDeptIds.forEach(deptId->{
			if (deptId==null || deptId.toString()=="") {
				return;
			}

			String deptName = getDeptName(Long.valueOf((String)deptId));
			if (deptName !=null) {
				sbDeptNames.append(deptName).append(sperate);
			}
		});
//		for(String userId : lstDeptIds) {
//			if (StringUtils.isBlank(userId)) {
//				continue;
//			}
//
//			String deptName = getDeptName(Long.valueOf(userId));
//			if (deptName !=null) {
//				sbDeptNames.append(deptName).append(sperate);
//			}
//		}
		String deptNames = sbDeptNames.substring(0, sbDeptNames.length()-1);
		return deptNames;
	}

	public static String getDeptName(Long id) {
		SysDeptSdk entity = getEntity(id);
		if (entity != null) {
			return entity.getDeptName();
		}
		return null;

	}
}
