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
import com.spt.bas.client.entity.BsProductType;
import com.spt.bas.client.entity.BsProductTypeAccess;
import com.spt.bas.server.dao.BsProductTypeDao;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.collection.CollectionUtil;
import com.spt.tools.core.util.SpringContextHolder;
import com.spt.tools.data.easyui.EasyTreeNode;

public class ProductTypeUtility {

	private final static Logger log = LoggerFactory.getLogger(ProductTypeUtility.class);

	private static LoadingCache<Long, List<BsProductType>> productTypeCache;
	private static LoadingCache<String, List<BsProductType>> productTypeCacheAll;
	private static LoadingCache<String, BsProductType> cacheOne;

	public static void init() {
		productTypeCache = CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES)
				.build(new CacheLoader<Long, List<BsProductType>>() {
					@Override
					public List<BsProductType> load(Long enterpriseId) throws Exception {
						BsProductTypeDao dao = SpringContextHolder.getBean(BsProductTypeDao.class);
						return dao.findAllByEnterpriseId(enterpriseId);
					}
				});
		LocalCacheManager.register(productTypeCache);
		productTypeCacheAll = CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES)
				.build(new CacheLoader<String, List<BsProductType>>() {
					@Override
					public List<BsProductType> load(String all) throws Exception {
						BsProductTypeDao dao = SpringContextHolder.getBean(BsProductTypeDao.class);
						return dao.findAllByOrderByIdAsc();
					}
				});
		LocalCacheManager.register(productTypeCacheAll);
		cacheOne = CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES)
				.build(new CacheLoader<String, BsProductType>() {
					@Override
					public BsProductType load(String productCode) throws Exception {
						BsProductTypeDao dao = SpringContextHolder.getBean(BsProductTypeDao.class);
						return dao.findProductTypeCode(productCode);
					}
				});
		LocalCacheManager.register(cacheOne);
	}

	private static Map<Integer, List<BsProductType>> getLevelMap(List<BsProductType> typeList) {
		Map<Integer, List<BsProductType>> rtn = new HashMap<Integer, List<BsProductType>>();
		for (BsProductType type : typeList) {
			Integer level = type.getLevel();
			List<BsProductType> typeLevelList = rtn.get(level);
			if (typeLevelList == null) {
				typeLevelList = new ArrayList<BsProductType>();
				typeLevelList.add(type);
			} else {
				typeLevelList.add(type);
			}
			rtn.put(level, typeLevelList);
		}
		return rtn;
	}

	private static EasyTreeNode getTreeNode(Map<Integer, List<BsProductType>> map, BsProductType parent) {
		return getTreeNode(map, parent, null);
	}

	private static EasyTreeNode getTreeNode(Map<Integer, List<BsProductType>> map, BsProductType parent,
			List<BsProductTypeAccess> lstSelect) {
		EasyTreeNode node = new EasyTreeNode();
		node.setText(parent.getTypeName());
		String parentTypeCode = parent.getTypeCode();
		node.setId(String.valueOf(parentTypeCode));
		if (lstSelect != null) {
			boolean isContain = CollectionUtil.contain(lstSelect, "productCd", parent.getTypeCode());
			node.setChecked(isContain);
		}

		List<EasyTreeNode> children = new ArrayList<EasyTreeNode>();
		Integer curLevel = parent.getLevel() + 1;
		List<BsProductType> levelList = map.get(curLevel);
		if (levelList != null) {
			for (BsProductType type : levelList) {
				String typeCode = type.getTypeCode();
				if (typeCode.startsWith(parentTypeCode)) {
					EasyTreeNode childrenNode = getTreeNode(map, type, lstSelect);
					children.add(childrenNode);
				}
			}
		}
		node.setChildren(children);

		return node;
	}

	public static List<EasyTreeNode> getAllTree(Long enterpriseId) {
		List<EasyTreeNode> nodes = new ArrayList<EasyTreeNode>();
		try {
			List<BsProductType> typeList = productTypeCache.get(enterpriseId);
			List<BsProductType> lstAll = new ArrayList<>();
			for (BsProductType type : typeList) {
				initAll(lstAll, type);
			}
			Map<Integer, List<BsProductType>> map = getLevelMap(lstAll);
			List<BsProductType> parentList = map.get(0);
			if (parentList != null) {
				for (BsProductType parent : parentList) {
					nodes.add(getTreeNode(map, parent));
				}
			}

		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return nodes;
	}

	private static void initAll(List<BsProductType> lstAll, BsProductType type) throws ExecutionException {
		if (!CollectionUtil.contain(lstAll, "id", type.getId())) {
			lstAll.add(type);
			String typeCode = type.getTypeCode();
			if (typeCode.split("_").length > 1) {
				String parentCode = typeCode.substring(0, typeCode.lastIndexOf("_"));
				BsProductType parent = cacheOne.get(parentCode);
				if (parent != null) {
					initAll(lstAll, parent);
				}
			}
		}
	}

	public static List<EasyTreeNode> getAllTree(List<BsProductTypeAccess> lstSelect) {
		List<EasyTreeNode> nodes = new ArrayList<EasyTreeNode>();
		try {
			List<BsProductType> typeList = productTypeCacheAll.get("All");
			Map<Integer, List<BsProductType>> map = getLevelMap(typeList);
			List<BsProductType> parentList = map.get(0);
			if (parentList != null) {
				for (BsProductType parent : parentList) {
					nodes.add(getTreeNode(map, parent, lstSelect));
				}
			}

		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return nodes;
	}
}
