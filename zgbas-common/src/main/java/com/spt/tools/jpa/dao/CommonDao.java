/**
 * 
 */
package com.spt.tools.jpa.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.spt.tools.jpa.dao.CommonDaoImpl.FuncOption;

/**
 * 通用服务接口: load*方法使用查询缓存
 * 
 * @author huangjian
 */
@SuppressWarnings("rawtypes")
public interface CommonDao {
	public <T> T findByPK(Class<T> entityClass, Object primaryKey);

	public <T> T findUnique(final String qlString, final Object... obj);

	public Long count(Class entityClass);

	public Long count(String qlString, Map<String, Object> parameter);

	public List findBy(String qlString, Map<String, Object> parameter);

	public List findBy(String qlString, Object... obj);

	public <T> List<T> findBy(String qlString, Class<T> resultClass, Map<String, Object> parameter);

	public <T> List<T> findBy(String qlString, Class<T> resultClass, Object... obj);

	public <T> List<T> findAll(Class<T> entityClass);

	public <T> List<T> findAll(Class<T> entityClass, Specification<T> spec);

	public <T> List<T> findAll(Class<T> entityClass, Sort sort);

	public <T> List<T> findAll(Class<T> entityClass, Specification<T> spec, Sort sort);

	public <T> Page<T> findPage(Class<T> entityClass, Specification<T> spec, Pageable pageable);

	public <T> Page<T> findPage(Class<T> entityClass, Pageable pageable);

	public Page findPage(String qlString, Map<String, Object> parameter, Pageable pageable);

	public List findBySql(String sqlString, Map<String, Object> parameter);

	public List findBySql(String sqlString, Object... obj);

	public List findBySql(String sqlString, Class resultClass, Map<String, Object> parameter);

	public List findBySql(String sqlString, Class resultClass, Object... obj);

	public List findByNamedQuery(String name, Map<String, Object> parameter);

	public List findByNamedQuery(String name, Object... obj);

	/** 使用查询缓存 */
	public <T> List<T> loadAll(Class<T> entityClass);

	public <T> List<T> loadAll(Class<T> entityClass, Specification<T> spec);

	public <T> List<T> loadAll(Class<T> entityClass, Sort sort);

	public <T> List<T> loadAll(Class<T> entityClass, Specification<T> spec, Sort sort);

	public List loadBy(String qlString, Map<String, Object> parameter);

	public List loadBySql(String sqlString, Class resultClass, Map<String, Object> parameter);

	public int batchExcute(String qlString, Map<String, Object> parameter);

	public int batchExcute(String qlString, Object... obj);

	public int batchExcuteSql(String sqlString, Map<String, Object> parameter);

	public int batchExcuteSql(String sqlString, Object... obj);

	public boolean executeStoreProcedure(String name, Map<Integer, Object> map);
	
	public boolean executeStoreProcedure(String name, Map<Integer, Object> map,List<FuncOption<?>> lstResult);

	public Object executeFunction(String name, Map<Integer, ? extends Object> map, Class entityClass);

	public void detach(Object entity);

	public void flush();
}
