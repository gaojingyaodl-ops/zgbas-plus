package com.spt.bas.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.spt.bas.client.entity.BsCompanyIndustry;
import com.spt.bas.server.dao.BsCompanyIndustryDao;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.collection.CollectionUtil;
import com.spt.tools.core.util.SpringContextHolder;
import com.spt.tools.data.easyui.EasyTreeNode;

public class BsCompanyIndustryUtil {

	private static LoadingCache<String, List<BsCompanyIndustry>> industryCache;
	private static LoadingCache<Long, BsCompanyIndustry> cacheOne;
	
	public static void init() {
		industryCache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
				.build(new CacheLoader<String, List<BsCompanyIndustry>>() {
					@Override
					public List<BsCompanyIndustry> load(String industryCode) throws Exception {
						BsCompanyIndustryDao companyIndustryDao = SpringContextHolder.getBean(BsCompanyIndustryDao.class);
						return companyIndustryDao.findAll();
					}
				});
		LocalCacheManager.register(industryCache);
		cacheOne = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
				.build(new CacheLoader<Long, BsCompanyIndustry>() {
					@Override
					public BsCompanyIndustry load(Long parentIndustryId) throws Exception {
						BsCompanyIndustryDao companyIndustryDao = SpringContextHolder.getBean(BsCompanyIndustryDao.class);
						return companyIndustryDao.findOne(parentIndustryId);
					}
				});
		LocalCacheManager.register(cacheOne);

	}
	
	public static List<EasyTreeNode> getAllTree() {
		List<EasyTreeNode> nodes = new ArrayList<EasyTreeNode>();
		try {
			List<BsCompanyIndustry> industryList = industryCache.get("ALL");
			List<BsCompanyIndustry> listAll = new ArrayList<>();
			for (BsCompanyIndustry industry : industryList) {
				initAll(listAll, industry);
			}
			Map<Integer, List<BsCompanyIndustry>> map = getLevelMap(listAll);
			List<BsCompanyIndustry> parentList = map.get(1);
			if (parentList != null) {
				for (BsCompanyIndustry dustry : parentList) {
					nodes.add(getTreeNode(map, dustry));
				}
			}
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return nodes;
	}
	
	private static void initAll(List<BsCompanyIndustry> lstAll, BsCompanyIndustry industry) throws ExecutionException {
		if (!CollectionUtil.contain(lstAll, "id", industry.getId())) {
			lstAll.add(industry);
			String parentIndustryId = industry.getParentIndustryId();
			if (parentIndustryId != null && parentIndustryId != "") {
				Long parentId = Long.valueOf(parentIndustryId);
				if (parentId != null) {
					BsCompanyIndustry parent = cacheOne.get(parentId);
					initAll(lstAll, parent);
				}
			}
		}
	}
	
	private static Map<Integer, List<BsCompanyIndustry>> getLevelMap(List<BsCompanyIndustry> industryList) {
		Map<Integer, List<BsCompanyIndustry>> rtn = new HashMap<Integer, List<BsCompanyIndustry>>();
		for (BsCompanyIndustry industry : industryList) {
			Integer grand = industry.getGrand();
			List<BsCompanyIndustry> grandList = rtn.get(grand);
			if (grandList == null) {
				grandList = new ArrayList<BsCompanyIndustry>();
				grandList.add(industry);
			} else {
				grandList.add(industry);
			}
			rtn.put(grand, grandList);
		}
		return rtn;
	}
	
	private static EasyTreeNode getTreeNode(Map<Integer, List<BsCompanyIndustry>> map, BsCompanyIndustry parent) {
		EasyTreeNode node = new EasyTreeNode();
		node.setText(parent.getIndustryName());
		node.setId(String.valueOf(parent.getId()));
//		if (lstSelect != null) {
//			boolean isContain = CollectionUtil.contain(lstSelect, "productCd", parent.getTypeCode());
//			node.setChecked(isContain);
//		}

		List<EasyTreeNode> children = new ArrayList<EasyTreeNode>();
		Integer curLevel = parent.getGrand()+1;
		List<BsCompanyIndustry> levelList = map.get(curLevel);
		if (levelList != null) {
			for (BsCompanyIndustry industry : levelList) {
				String parentId = industry.getParentIndustryId();
				if (parentId != null && Long.valueOf(parentId) == parent.getId()) {
					EasyTreeNode childrenNode = getTreeNode(map, industry);
					children.add(childrenNode);
				}
			}
		}
		node.setChildren(children);

		return node;
	}
}
