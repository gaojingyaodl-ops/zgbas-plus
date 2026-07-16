/**
 * 
 */
package com.spt.tools.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.reflect.ReflectUtils;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.DataEntity;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.filter.IDefaultSearchFilter;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;

/**
 * 单表翻页基类
 * 
 * @author huangjian
 * 
 */
public abstract class PageController<T extends DataEntity, V extends BaseVo> {
	private static final String LOG_OPT_DEL = "1";
	private static final String DESC = "desc";
	private static final String ASC = "asc";
	
	public abstract BaseClient<T> getService();

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required=false)
	private IDefaultSearchFilter defaultSearchFilter;
	
	/** 列表查询方法 */
	@RequestMapping(value = "list")
	public String list(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		Page<T> page = findPage(searchVo, request, response);
		Page<V> pageVo = entity2Vo(page);
		T e = sumPage(request, response);
		Map<String, Object> footer = null;
		if (e != null) {
			footer = entity2Footer(e);
		}
		JsonEasyUI.renderJson(response, pageVo == null ? page : pageVo, footer);
		return null;
	}

	/** 实体值赋值给合计map */
	protected Map<String, Object> entity2Footer(T e) {

		return null;
	}

	protected Page<V> entity2Vo(Page<T> page) {
		try {
			Class<V> voClass = getVoClass();
			if (voClass != BaseVo.class) {
				List<V> list = new ArrayList<>();
				for (T entity : page.getContent()) {
					V vo = getVoClass().newInstance();
					copyEntity2Vo(entity, vo);
					list.add(vo);
				}
				PageRequest pageRequest = PageRequest.of(page.getNumber(), page.getSize(), page.getSort());
				Page<V> pageVo = new PageImpl<>(list, pageRequest, page.getTotalElements());
				return pageVo;
			}
		} catch (Exception e) {
			logger.error("entity2Vo error!", e);
		}
		return null;
	}

	/**
	 * 将实体属性copy到vo，用于查询显示
	 */
	protected void copyEntity2Vo(T e, V v) {
//		v.setId(e.getId());
//		v.setCreatedDate(e.getCreatedDate());
//		v.setUpdatedDate(e.getUpdatedDate());
	}

	/** 翻页查询 */
	protected Page<T> findPage(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		PageDown<T> page = getService().findPage(searchVo);
		return page;
	}

	/** 初始化查询条件 */
	protected void initSearch(PageSearchVo searchVo, HttpServletRequest request) {
		Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
		searchParams.putAll(getDefaultFilter());
		String sortField = searchVo.getSort();
		String order = searchVo.getOrder();
		// 设置默认排序方式
		Map<String, String> mapOrder = new LinkedHashMap<String, String>();
		if (!StringUtils.isEmpty(sortField)) {
			mapOrder.put(sortField, order);
		}
		searchVo.setSearchParams(searchParams);
	}
	/**
	 * 初始化查询条件 （带初始参数）
	 * @param searchVo
	 * @param request
	 * @param initParam
	 */
	protected void initSearch2(PageSearchVo searchVo, HttpServletRequest request,Map<String,Object> initParam) {
		Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
		if(initParam!=null && initParam.size()!=0)
		{
		    for(Entry<String, Object> entry:initParam.entrySet()){
		    	searchParams.put(entry.getKey(), entry.getValue());
		    }
		}
		searchParams.putAll(getDefaultFilter());
		String sortField = searchVo.getSort();
		String order = searchVo.getOrder();
		// 设置默认排序方式
		Map<String, String> mapOrder = new LinkedHashMap<String, String>();
		if (!StringUtils.isEmpty(sortField)) {
			mapOrder.put(sortField, order);
		}
		searchVo.setSearchParams(searchParams);
	}

	/** 翻页查询统计 */
	protected T sumPage(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
		searchParams.putAll(getDefaultFilter());
		T e = getService().sumPage(searchParams);
		return e;
	}

	/** 默认查询条件 */
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = new HashMap<>();
		
		if (defaultSearchFilter != null) {
			map.putAll(defaultSearchFilter.filter());
		}
		
		return map;
	};

	/** 默认排序条件 */
	protected Map<String, String> getDefaultOrder() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("id", DESC);
		return map;
	}

	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		try {
			T e = getService().getEntity(id);
			getService().delete(id);
			doLog(request, e, LOG_OPT_DEL);
			RenderUtil.renderSuccess("删除成功", response);
		} catch (Exception e) {
			logger.error("delete record error!", e);
			RenderUtil.renderFailure("操作错误，请联系管理员", response);
		}
		return null;
	}

	/** 记录日志 */
	protected void doLog(HttpServletRequest request, T e, String operation) {

	}

	protected Class<T> getEntityClass() {
		Class<T> entityClass = ReflectUtils.getSuperClassGenricType(getClass(), 0);
		return entityClass;
	}

	protected Class<V> getVoClass() {
		Class<V> entityClass = ReflectUtils.getSuperClassGenricType(getClass(), 1);
		;
		return entityClass;
	}
}
