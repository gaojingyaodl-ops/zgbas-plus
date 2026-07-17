package com.spt.bas.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.spt.bas.client.entity.BsTemplateConfig;
import com.spt.bas.server.service.IBsTemplateConfigService;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.util.SpringContextHolder;

public class TemplateContentUtility {
	private final static Logger log = LoggerFactory.getLogger(TemplateContentUtility.class);
	
	private static LoadingCache<String, List<BsTemplateConfig>> templateCache;

	/**
	 * 构造器
	 */
	private TemplateContentUtility() {
	}

	/**
	 * 初始化模板工具
	 * 
	 * @param ctx
	 */
	public static void init() {
		templateCache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
				.build(new CacheLoader<String, List<BsTemplateConfig>>() {
					@Override
					public List<BsTemplateConfig> load(String templateCat) throws Exception {
						IBsTemplateConfigService service = SpringContextHolder.getBean(IBsTemplateConfigService.class);
						return service.findByTemplateCat(templateCat);
						
					}
				});

		LocalCacheManager.register(templateCache);
	}
	
	public static void refresh(String category) {
	    templateCache.refresh(category);
	    log.info("refresh {} finished.", category);
	}
	/**
	 * 清空缓存
	 */
	public static void cleanUp() {
		if (templateCache != null) {
			templateCache.cleanUp();
		}
	}

	

	/**
	 * 根据模板类别，模板ID获取模板MAP
	 * 
	 * @param category
	 *            模板类别
	 * @param templateId
	 *            模板ID
	 * @return KEY：模板标识 VALUE： KEY： 语言标识 VALUE： 模板entity
	 * 
	 */
	public static Map<String, BsTemplateConfig> getTemplateMap(String category, String templateId) {
		Map<String, BsTemplateConfig> rtn = new HashMap<String, BsTemplateConfig>();
		try {
			List<BsTemplateConfig> list = getTemplateMap(category).get(templateId);
			for (BsTemplateConfig entity : list) {
				String key = entity.getLang();
				rtn.put(key, entity);
			}
		} catch (Exception e) {
			log.warn(e.getMessage());

		}
		return rtn;
	}
	
	/**
	 * 根据模板类别，模板ID，模板标识和语言标识，获取一个模板entity
	 * 
	 * @param category
	 *            模板类别
	 * @param templateId
	 *            模板ID
	 
	 * @param lang
	 *            语言标识
	 * @return
	 */
	public static BsTemplateConfig getTemplate(String category, String templateId,
			String lang) {
		return getTemplateMap(category, templateId).get(lang);
	}
	
	//获取模板,若无关联模板则找到其父类模板
	public static BsTemplateConfig findTemplate(String category, String templateId, String lang){
		BsTemplateConfig template = getTemplate(category, templateId,lang);
		if(template==null){
			String[] str = templateId.split("_");
			if(str.length>1){
				int subLength = templateId.length()-str[str.length-1].length()-1;
				String strTemplateId =  templateId.substring(0,subLength);
				template = findTemplate(category,strTemplateId,lang);
			}
		}
		return template;
	}
	
	/**
	 * 根据模板类别获取模板MAP KEY：模板ID VALUE：模板entity列表
	 * 
	 * @param category
	 *            模板类别
	 * @return
	 */
	public static Map<String, List<BsTemplateConfig>> getTemplateMap(String category) {
		Map<String, List<BsTemplateConfig>> rtn = new HashMap<String, List<BsTemplateConfig>>();
		List<BsTemplateConfig> list = new ArrayList<BsTemplateConfig>();
		try {
			list = templateCache.get(category);
		} catch (ExecutionException e) {
			log.warn(e.getMessage());
		}
		if (list.size() > 0) {
			for (BsTemplateConfig entity : list) {
				List<BsTemplateConfig> sonList;
				String key = entity.getTemplateid();
				if (!rtn.containsKey(key)) {
					sonList = new ArrayList<BsTemplateConfig>();
					sonList.add(entity);
					rtn.put(key, sonList);
				} else {
					sonList = rtn.get(key);
					sonList.add(entity);
				}
			}
		}
		return rtn;
	}
	
	public static Map<String, List<BsTemplateConfig>> getTemplateMap2(String category, String templateId) {
		Map<String, List<BsTemplateConfig>> rtn = new HashMap<String, List<BsTemplateConfig>>();
		try {
			List<BsTemplateConfig> list = getTemplateMap(category).get(templateId);
			if (list.size() > 0) {
				for (BsTemplateConfig entity : list) {
					List<BsTemplateConfig> sonList;
					String key = entity.getLang();
					if (!rtn.containsKey(key)) {
						sonList = new ArrayList<BsTemplateConfig>();
						sonList.add(entity);
						rtn.put(key, sonList);
					} else {
						sonList = rtn.get(key);
						sonList.add(entity);
					}
				}
			}
		} catch (Exception e) {
			log.warn(e.getMessage());

		}
		return rtn;
	} 
	
	public static Map<Long, BsTemplateConfig> getTemplateMap2(String category, String templateId,String lang) {
		Map<Long, BsTemplateConfig> rtn = new HashMap<Long, BsTemplateConfig>();
		try {
			List<BsTemplateConfig> list = getTemplateMap2(category,templateId).get(lang);
			for (BsTemplateConfig entity : list) {
				Long key = entity.getEnterpriseId();
				rtn.put(key, entity);
			}
		} catch (Exception e) {
			log.warn(e.getMessage());

		}
		return rtn;
	}
	
	public static BsTemplateConfig getTemplate(String category, String templateId,
			String lang , Long enterpriseId) {
		
		if(enterpriseId !=null){
			return getTemplateMap2(category, templateId, lang).get(enterpriseId);
		}else{
			return getTemplateMap(category, templateId).get(lang);
		}
		
	}
}
