package com.spt.bas.server.cache;//package com.spt.bas.server.cache;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.common.base.Splitter;
//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.LoadingCache;
//import com.hsoft.admin.sdk.entity.SysUser;
//import com.hsoft.admin.sdk.open.IAdminOpenFacade;
//import com.spt.bas.client.constant.BasConstants;
//import com.spt.tools.core.cache.LocalCacheManager;
//import com.spt.tools.core.util.SpringContextHolder;
//
//public final class UserCache {
//	private static AtomicBoolean isInited = new AtomicBoolean(false);
//	private static final Logger log = LoggerFactory.getLogger(UserCache.class);
//	private static LoadingCache<Long, SysUser> cache;
//
//	private UserCache() {
//		if (!isInited.get()) {
//			init();
//		}
//	}
//
//	public static void init() {
//		log.info("---初始化用户缓存");
//		cache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
//				.build(new CacheLoader<Long, SysUser>() {
//					@Override
//					public SysUser load(Long userId) throws Exception {
//						// 在每次get，从缓存那东西的时候，如果是第一次，调用load方法，如果是两次get时间在60分钟之间，就不会调用load,这个load
//						IAdminOpenFacade service = SpringContextHolder.getBean(IAdminOpenFacade.class);
//						SysUser entity = service.findUserById(userId);
//						return entity;
//
//					}
//				});
//
//		LocalCacheManager.register(cache);
//		isInited.set(true);
//	}
//
//	public static SysUser getEntity(Long userId) {
//		if (userId == null || userId == 0) {
//			return null;
//		}
//		try {
//			return cache.get(userId);
//		} catch (Exception e) {
//			IAdminOpenFacade service = SpringContextHolder.getBean(IAdminOpenFacade.class);
//			SysUser entity = service.findUserById(userId);
//			return entity;
//		}
//	}
//
//	public static String getUserName(String userIds) {
//		if (StringUtils.isBlank(userIds)) {
//			return null;
//		}
//		List<String> lstUserIds = Splitter.on(BasConstants.SEPARATE).splitToList(userIds);
//		StringBuilder sbUserNames =new StringBuilder();
//		for(String userId : lstUserIds) {
//			if (StringUtils.isBlank(userId)) {
//				continue;
//			}
//
//			String userName = getUserName(Long.valueOf(userId));
//			if (userName !=null) {
//				sbUserNames.append(userName).append(BasConstants.SEPARATE);
//			}
//		}
//		String userNames = sbUserNames.substring(0, sbUserNames.length()-1);
//		return userNames;
//	}
//
//	public static String getUserName(Long id) {
//		SysUser entity = getEntity(id);
//		if (entity != null) {
//			return entity.getName();
//		}
//		return null;
//
//	}
//}
