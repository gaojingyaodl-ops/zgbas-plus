package com.spt.bas.client.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.BsDictType;
import com.spt.bas.client.remote.IBsDictClient;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.util.SpringContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public final class BsDictUtil {
	private static AtomicBoolean isInited = new AtomicBoolean(false);
	private static final Logger log = LoggerFactory.getLogger(BsDictUtil.class);
	private static LoadingCache<String, Map<String, Map<String, BsDictData>>> dicCache;

	private BsDictUtil() {
		if (!isInited.get()) {
			init();
		}
	}

	/**
	 * 初始化：读入所有的字典条目到缓存
	 * 
	 * @param ctx
	 * @throws ExecutionException
	 */
	public static void init() {
		log.info("---初始化业务数据字典");
		dicCache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Map<String, Map<String, BsDictData>>>() {
					@Override
					public Map<String, Map<String, BsDictData>> load(String key) throws Exception {
						// 在每次get，从缓存那东西的时候，如果是第一次，调用load方法，如果是两次get时间在60分钟之间，就不会调用load,这个load
						IBsDictClient service = SpringContextHolder.getBean(IBsDictClient.class);
						List<BsDictType> entityList = service.findAll();
						Map<String, Map<String, BsDictData>> dicMap = new HashMap<>();
						for (BsDictType type : entityList) {
							for (BsDictData entity : type.getDictDatas()) {

								Map<String, BsDictData> element;
								String category = type.getDictTypeCd();
								String categoryKey = type.getEnterpriseId() + category;
								if (!dicMap.containsKey(categoryKey)) {
									element = new LinkedHashMap<>();
									element.put(entity.getDictCd(), entity);
									dicMap.put(categoryKey, element);
								} else {
									element = dicMap.get(categoryKey);
									element.put(entity.getDictCd(), entity);
								}
							}
						}
						return dicMap;

					}
				});

		LocalCacheManager.register(dicCache);
		isInited.set(true);
	}

	/**
	 * 根据类别键和标识键获取条目的中文名称 例如，传入： category = "priceUnit", key = "A", 返回："美元"
	 * 
	 * @param key
	 * @param value
	 * @throws ExecutionException
	 */
	public static String getValue(Long companyId, String category, String key) {
		if (StringUtils.isBlank(key)){
			return "";
		}
		return getValueInternal(companyId, category, key, false);
	}

	public static String getKey(Long companyId, String category, String value) {
		List<BsDictData> dictDataList = getListByCategory(companyId, category);
		if (CollectionUtils.isNotEmpty(dictDataList)) {
			for (BsDictData bsDictData : dictDataList) {
				if (StringUtils.equals(value, bsDictData.getDictName())) {
					return bsDictData.getDictCd();
				}
			}
		}
		return null;
	}

	/**
	 * 根据类别键和标识键获取条目的英文名称 例如，传入： category = "priceUnit", key = "A", 返回："USD"
	 * 
	 * @param key
	 * @param value
	 * @throws ExecutionException
	 */
	public static String getValueEn(Long companyId, String category, String key) {
		return getValueInternal(companyId, category, key, true);
	}

	/**
	 * 根据类别键获取所有字典表项
	 * 
	 * @param category
	 *            类别键
	 * @return
	 * @throws ExecutionException
	 */
	public static List<BsDictData> getListByCategory(Long companyId, String category) {
		List<BsDictData> rtn = new ArrayList<BsDictData>();
		try {
			if (dicCache == null){
				init();
			}
			String categoryKey = companyId + category;
			Map<String, BsDictData> map = dicCache.get("DICT").get(categoryKey);
			if (map != null) {
				Set<String> keySet = map.keySet();
				for (String key : keySet) {
					rtn.add(map.get(key));
				}
			}
			return rtn;
		} catch (ExecutionException e) {
			log.error(e.getMessage(), e);
			return rtn;
		}

	}

	private static String getValueInternal(Long companyId, String category, String key, boolean ifEnglish) {
		try {
			if (dicCache == null){
				init();
			}
			Map<String, Map<String, BsDictData>> elementAll = dicCache.get("DICT");
			String categoryKey = companyId + category;
			Map<String, BsDictData> element = elementAll.get(categoryKey);
			if (element == null) {
				return null;
			} else {
				BsDictData entity = element.get(key);
				if (entity == null || Boolean.FALSE.equals(entity.getEnableFlg())) {
					return null;
				} else {
					if (!ifEnglish) {
						return entity.getDictName();
					} else {
						return SpringContextHolder.getMessage(entity.getDictCd(), Locale.ENGLISH);
					}
				}
			}
		} catch (ExecutionException e) {
			log.error(e.getMessage(), e);
			return "";
		}
	}
}
