package com.spt.tools.core.cache;

import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.cache.LoadingCache;

/**
 * 本地缓存管理器
 * 
 * @author wangyilin
 *
 */
@SuppressWarnings("rawtypes")
public final class LocalCacheManager
{
	private static CopyOnWriteArrayList<LoadingCache> cacheList;
	
	static 
	{
		cacheList = new CopyOnWriteArrayList<LoadingCache>();
	}
	
	/**
	 * 注册新的本地缓存
	 * @param cache
	 */
	public static void register(LoadingCache cache)
	{
		cacheList.add(cache);
	}
	
	/**
	 * 移除新的本地缓存
	 * @param cache
	 */
	public static void remove(LoadingCache cache)
	{
		cacheList.remove(cache);
	}
	
	/**
	 * 刷新所有缓存
	 */
	public static void refreshAll()
	{
		for (LoadingCache cache : cacheList)
		{
			cache.invalidateAll();
		}
	}
}
