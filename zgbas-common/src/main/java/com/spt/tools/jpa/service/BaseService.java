/**
 * 
 */
package com.spt.tools.jpa.service;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.reflect.ReflectUtils;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.dao.CommonDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * @author huangjian
 * 
 */
public abstract class BaseService<T extends IdEntity> implements IBaseService<T> {
	public abstract BaseDao<T> getBaseDao();
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public Class<T> getEntityClazz(){
		return ReflectUtils.getSuperClassGenricType(this.getClass(), 0);
	};
	@Autowired
	protected CommonDao commonDao;
	@PersistenceContext
	protected EntityManager em;
	
	/** 使用查询缓存 */
	public List<T> loadAll() {
		Sort sort = getDefaultSort();
		List<T> list;
		if (sort != null) {
			list = commonDao.loadAll(getEntityClazz(), sort);
		} else {
			list = commonDao.loadAll(getEntityClazz());
		}
		return list;
	}

	public List<T> findAll() {
		Sort sort = getDefaultSort();
		List<T> list;
		if (sort != null) {
			list = (List<T>) getBaseDao().findAll(sort);
		} else {
			list = (List<T>) getBaseDao().findAll();
		}
		return list;
	}

	/** 设置默认排序 */
	protected Sort getDefaultSort() {
		Sort sort=Sort.by(Direction.DESC, "id");
		return sort;
	}
	/**翻页查询*/
	@Override
	public Page<T> findPage(Map<String, Object> searchParams, PageRequest pageRequest) {
		Specification<T> spec = WebUtil.buildSpecification(searchParams);
		PageRequest pageRequestNew=pageRequest;
		if (pageRequest.getSort()==null && getDefaultSort()!=null){
			pageRequestNew= PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), getDefaultSort());
		}
		Page<T> page = getBaseDao().findAll(spec, pageRequestNew);
		return page;
	}
	
	@Override
	public Page<T> findPage(PageSearchVo queryVo) {
		String sortField = queryVo.getSort();
		String order = queryVo.getOrder();
		Sort sort = null;
		if (StringUtils.isNotBlank(sortField)) {
			Direction direction = Direction.ASC;
			if (Direction.DESC.name().equalsIgnoreCase(order)) {
				direction = Direction.DESC;
			}
			sort = Sort.by(direction, sortField);
		}
		PageRequest pageRequest = sort != null
				? PageRequest.of(queryVo.getPage() - 1, queryVo.getRows(), sort)
				: PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
		Page<T> page = findPage(queryVo.getSearchParams(), pageRequest);
		// sort属性无法反序列化，下面代码重新组装page对象，去掉sort属性
		PageRequest pageRequest_new = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
		Page<T> pageVo = new PageImpl<>(page.getContent(), pageRequest_new, page.getTotalElements());
		return pageVo;
	}

	
	/**翻页查询统计*/
	public T sumPage(Map<String, Object> searchParams){
		return null;
	};
	
	@Override
	public T getEntity(Long id) {
		return getBaseDao().findOne(id);
	}

	@Transactional(readOnly = false)
	public T save(T entity) throws ApplicationException {
		return getBaseDao().save(entity);
	}

	@Transactional(readOnly = false)
	public void delete(Long id) throws ApplicationException {
		getBaseDao().delete(id);
	}

	@Transactional(readOnly = false)
	public void saveBatch(List<T> insertedRecords, List<T> updatedRecords, List<T> deletedRecords) throws ApplicationException  {
		for (T entity : insertedRecords) {
			save(entity);
		}
		for (T entity : updatedRecords) {
			save(entity);
		}
		for (T entity : deletedRecords) {
			delete(entity.getId());
		}
	}
}
