/**
 * 
 */
package com.spt.tools.jpa.dao;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

/**
 * @author huangjian
 */
@SuppressWarnings("rawtypes")
public class CommonDaoImpl implements CommonDao {
	private Logger log = LoggerFactory.getLogger(getClass());
	@PersistenceContext
	private EntityManager em;

	public Long count(Class entityClass) {
		StringBuffer hql = new StringBuffer("select count(*) from ");
		hql.append(entityClass.getSimpleName());
		return em.createQuery(hql.toString(), Long.class).getSingleResult();
	}

	public Long count(String qlString, Map<String, Object> parameter) {
		String fromHql = qlString;
		// select子句与order by子句会影响count查询,进行简单的排除.
		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");
		String countHql = "select count(*) " + fromHql;
		TypedQuery<Long> query = em.createQuery(countHql, Long.class);
		for (String param : parameter.keySet()) {
			query.setParameter(param, parameter.get(param));
		}
		return query.getSingleResult();
	}

	@Override
	public <T> T findByPK(Class<T> entityClass, Object primaryKey) {
		return em.find(entityClass, primaryKey);
	}

	@Override
	public List findBy(String qlString, Map<String, Object> parameter) {
		Query query = em.createQuery(qlString);
		for (String param : parameter.keySet()) {
			query.setParameter(param, parameter.get(param));
		}
		return query.getResultList();
	}

	public Page findPage(String qlString, Map<String, Object> parameter, Pageable pageable) {
		Query query = em.createQuery(qlString);
		for (String param : parameter.keySet()) {
			query.setParameter(param, parameter.get(param));
		}
		return pageable == null ? new PageImpl(query.getResultList()) : readPage(qlString, query, pageable, parameter);
	}

	private <T> Page<T> readPage(String qlString, Query query, Pageable pageable, Map<String, Object> parameter) {

		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		Long total = count(qlString, parameter);
		List<T> content = total > pageable.getOffset() ? query.getResultList() : Collections.<T>emptyList();

		return new PageImpl<T>(content, pageable, total);
	}

	@Override
	public <T> List<T> findBy(String qlString, Class<T> resultClass, Map<String, Object> parameter) {
		TypedQuery<T> query = em.createQuery(qlString, resultClass);
		for (String param : parameter.keySet()) {
			query.setParameter(param, parameter.get(param));
		}
		return query.getResultList();
	}

	@Override
	public List findBy(String qlString, Object... obj) {
		Query query = em.createQuery(qlString);
		for (int i = 0; i < obj.length; i++) {
			query.setParameter(i + 1, obj[i]);
		}
		return query.getResultList();
	}

	@Override
	public <T> List<T> findBy(String qlString, Class<T> resultClass, Object... obj) {
		TypedQuery<T> query = em.createQuery(qlString, resultClass);
		for (int i = 0; i < obj.length; i++) {
			query.setParameter(i + 1, obj[i]);
		}
		return query.getResultList();
	}

	public <T> List<T> findAll(Class<T> entityClass) {
		return getQuery(entityClass, null, (Sort) null).getResultList();
	}

	public <T> List<T> findAll(Class<T> entityClass, Specification<T> spec) {
		return getQuery(entityClass, spec, (Sort) null).getResultList();
	}

	public <T> Page<T> findPage(Class<T> entityClass, Specification<T> spec, Pageable pageable) {
		TypedQuery<T> query = getQuery(entityClass, spec, pageable);
		return pageable == null ? new PageImpl<T>(query.getResultList()) : readPage(entityClass, query, pageable, spec);
	}

	public <T> List<T> findAll(Class<T> entityClass, Sort sort) {
		return getQuery(entityClass, null, sort).getResultList();
	}

	public <T> List<T> findAll(Class<T> entityClass, Specification<T> spec, Sort sort) {
		return getQuery(entityClass, spec, sort).getResultList();
	}

	public <T> Page<T> findPage(Class<T> entityClass, Pageable pageable) {
		if (null == pageable) {
			return new PageImpl<T>(findAll(entityClass));
		}
		return findPage(null, pageable);
	}

	@Override
	public List findBySql(String sqlString, Map<String, Object> parameter) {
		Query query = em.createNativeQuery(sqlString);
		for (String param : parameter.keySet()) {
			query.setParameter(param, parameter.get(param));
		}
		return query.getResultList();
	}

	@Override
	public List findBySql(String sqlString, Object... obj) {
		Query query = em.createNativeQuery(sqlString);
		for (int i = 0; i < obj.length; i++) {
			query.setParameter(i + 1, obj[i]);
		}
		return query.getResultList();
	}

	@Override
	public List findBySql(String sqlString, Class resultClass, Map<String, Object> parameter) {
		Query query = em.createNativeQuery(sqlString, resultClass);
		for (String param : parameter.keySet()) {
			query.setParameter(param, parameter.get(param));
		}
		return query.getResultList();
	}

	@Override
	public List findBySql(String sqlString, Class resultClass, Object... obj) {
		Query query = em.createNativeQuery(sqlString, resultClass);
		for (int i = 0; i < obj.length; i++) {
			query.setParameter(i + 1, obj[i]);
		}
		return query.getResultList();
	}

	@Override
	public List findByNamedQuery(String name, Map<String, Object> parameter) {
		Query query = em.createNamedQuery(name);
		for (String param : parameter.keySet()) {
			query.setParameter(param, parameter.get(param));
		}
		return query.getResultList();
	}

	@Override
	public List findByNamedQuery(String name, Object... obj) {
		Query query = em.createNamedQuery(name);
		for (int i = 0; i < obj.length; i++) {
			query.setParameter(i + 1, obj[i]);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T findUnique(String qlString, Object... obj) {
		List list = findBy(qlString, obj);
		if (list.size() == 1) {
			return (T) list.get(0);
		}
		return null;
	}

	public <T> List<T> loadAll(Class<T> entityClass) {
		TypedQuery<T> query = getQuery(entityClass, null, (Sort) null);
		query.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	public <T> List<T> loadAll(Class<T> entityClass, Specification<T> spec) {
		TypedQuery<T> query = getQuery(entityClass, spec, (Sort) null);
		query.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	public <T> List<T> loadAll(Class<T> entityClass, Sort sort) {
		TypedQuery<T> query = getQuery(entityClass, null, sort);
		query.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	public <T> List<T> loadAll(Class<T> entityClass, Specification<T> spec, Sort sort) {
		TypedQuery<T> query = getQuery(entityClass, spec, sort);
		query.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	public List loadBySql(String sqlString, Class resultClass, Map<String, Object> parameter) {
		Query query = em.createNativeQuery(sqlString, resultClass);
		for (String param : parameter.keySet()) {
			query.setParameter(param, parameter.get(param));
		}
		query.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	public List loadBy(String qlString, Map<String, Object> parameter) {
		Query query = em.createQuery(qlString);
		for (String param : parameter.keySet()) {
			query.setParameter(param, parameter.get(param));
		}
		return query.getResultList();
	}

	private <T> Page<T> readPage(Class<T> entityClass, TypedQuery<T> query, Pageable pageable, Specification<T> spec) {

		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		Long total = executeCountQuery(getCountQuery(entityClass, spec));
		List<T> content = total > pageable.getOffset() ? query.getResultList() : Collections.<T>emptyList();

		return new PageImpl<T>(content, pageable, total);
	}

	private static Long executeCountQuery(TypedQuery<Long> query) {

		Assert.notNull(query);

		List<Long> totals = query.getResultList();
		Long total = 0L;

		for (Long element : totals) {
			total += element == null ? 0 : element;
		}

		return total;
	}

	private <T> TypedQuery<Long> getCountQuery(Class<T> entityClass, Specification<T> spec) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);

		Root<T> root = applySpecificationToCriteria(entityClass, spec, query);

		if (query.isDistinct()) {
			query.select(builder.countDistinct(root));
		} else {
			query.select(builder.count(root));
		}

		return em.createQuery(query);
	}

	private <T> TypedQuery<T> getQuery(Class<T> entityClass, Specification<T> spec, Pageable pageable) {

		Sort sort = pageable == null ? null : pageable.getSort();
		return getQuery(entityClass, spec, sort);
	}

	private <T> TypedQuery<T> getQuery(Class<T> entityClass, Specification<T> spec, Sort sort) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(entityClass);

		Root<T> root = applySpecificationToCriteria(entityClass, spec, query);
		query.select(root);

		if (sort != null) {
			query.orderBy(toOrders(sort, root, builder));
		}

		return applyLockMode(em.createQuery(query));
	}

	private <T, S> Root<T> applySpecificationToCriteria(Class<T> entityClass, Specification<T> spec,
			CriteriaQuery<S> query) {
		Assert.notNull(query);
		Root<T> root = query.from(entityClass);

		if (spec == null) {
			return root;
		}

		CriteriaBuilder builder = em.getCriteriaBuilder();
		Predicate predicate = spec.toPredicate(root, query, builder);

		if (predicate != null) {
			query.where(predicate);
		}

		return root;
	}

	private <T> TypedQuery<T> applyLockMode(TypedQuery<T> query) {
		LockModeType type = null;
		return type == null ? query : query.setLockMode(type);
	}

	@Override
	public int batchExcute(String qlString, Map<String, Object> parameter) {
		Query query = em.createQuery(qlString);
		for (String param : parameter.keySet()) {
			query.setParameter(param, parameter.get(param));
		}
		return query.executeUpdate();
	}

	@Override
	public int batchExcute(String qlString, Object... obj) {
		Query query = em.createQuery(qlString);
		for (int i = 0; i < obj.length; i++) {
			query.setParameter(i + 1, obj[i]);
		}
		return query.executeUpdate();
	}

	@Override
	public int batchExcuteSql(String sqlString, Map<String, Object> parameter) {
		Query query = em.createNativeQuery(sqlString);
		for (String param : parameter.keySet()) {
			query.setParameter(param, parameter.get(param));
		}
		return query.executeUpdate();
	}

	@Override
	public int batchExcuteSql(String sqlString, Object... obj) {
		Query query = em.createNativeQuery(sqlString);
		for (int i = 0; i < obj.length; i++) {
			query.setParameter(i + 1, obj[i]);
		}
		return query.executeUpdate();
	}

	public void detach(Object entity) {
		em.detach(entity);
	}

	public void flush() {
		em.flush();
	}

	@Override
	public boolean executeStoreProcedure(String name, Map<Integer, Object> map) {
		return executeStoreProcedure(name, map, null);
	}

	// "{ call powerdesk.bid_temp_pkg.validateBidDivision(?,?,?,?)}"
	public boolean executeStoreProcedure(String name, Map<Integer, Object> map, List<FuncOption<?>> lstResult) {
		SessionImpl session = (SessionImpl) em.getDelegate();
		boolean isNewSession = false;
		if (session.isClosed()) {
			SessionFactoryImplementor sf = (SessionFactoryImplementor) session.getSessionFactory();
			session = (SessionImpl) sf.openSession();
			isNewSession = true;
		}
		Connection conn = session.connection();
		CallableStatement callableStatement = null;
		try {
			log.info("excute:" + name);
			callableStatement = conn.prepareCall(name);
			for (int i = 0; i < map.size(); i++) {
				Object value = map.get(i);
				if (value instanceof String) {
					callableStatement.setString(i + 1, (String) value);
				} else if (value instanceof Date) {
					callableStatement.setDate(i + 1, (Date) value);
				} else if (value instanceof Integer) {
					callableStatement.setInt(i + 1, (Integer) value);
				} else {
					if (value == null) {
						callableStatement.setString(i + 1, null);
					} else {
						callableStatement.setObject(i + 1, value);
					}
				}
//				callableStatement.setString(i + 1, map.get(i));
			}
			boolean hadResults = callableStatement.execute();
			if (lstResult != null) {
				int i = 0;
				while (hadResults) {
					ResultSet rs = callableStatement.getResultSet();
					FuncOption funcOption = lstResult.get(i);
					Class resultType = funcOption.getResultType();
					if (funcOption.getFuncType() == FuncType.LIST) {
						List list = funcOption.getList();
						if (list != null) {
							list.addAll(resut2Entity(rs, resultType));
						}
					} else {
						if (funcOption.getFuncType() == FuncType.LONG) {
							if (rs.next()) {
								Object obj = rs.getObject(1);
								Long cnt = Long.valueOf(obj.toString());
								funcOption.setObject(cnt);
							}
						} else if (funcOption.getFuncType() == FuncType.OBJECT) {
							List list = resut2Entity(rs, resultType);
							if (list.size() > 0) {
								funcOption.setObject(list.get(0));
							}
						}
					}
					hadResults = callableStatement.getMoreResults(); // 检查是否存在更多结果集
					i++;
				}
			}
		} catch (Exception e) {
			log.error("executeStoreProcedure error", e);
			throw new RuntimeException(e);
		} finally {
			try {
				if (callableStatement != null) {
					callableStatement.close();
				}
				if (isNewSession && !conn.isClosed()) {
					conn.close();
				}
				if (isNewSession && session != null && !session.isClosed()) {
					session.close();
				}
			} catch (Exception e2) {
				log.error("executeStoreProcedure error", e2);
				throw new RuntimeException(e2);
			}
		}
		return true;
	}

	public static enum FuncType {
		LIST, OBJECT, LONG
	}

	public static class FuncOption<T> {
		private List<T> list = new ArrayList<>();
		private T object;
		private Class<T> resultType;
		private FuncType funcType;

		public static FuncOption<Long> newLong() {
			return new FuncOption<>(Long.class, FuncType.LONG);
		}

		public static <E> FuncOption<E> newList(Class<E> resultType) {
			return new FuncOption<>(resultType, FuncType.LIST);
		}

		public static <E> FuncOption<E> newObject(Class<E> resultType) {
			return new FuncOption<>(resultType, FuncType.OBJECT);
		}
		
		public FuncOption(Class<T> resultType, FuncType funcType) {
			this.resultType = resultType;
			this.funcType = funcType;
		}

		public Class getResultType() {
			return resultType;
		}

		public FuncType getFuncType() {
			return funcType;
		}

		public void setFuncType(FuncType funcType) {
			this.funcType = funcType;
		}

		public List<T> getList() {
			return list;
		}

		public void setList(List<T> list) {
			this.list = list;
		}

		public T getObject() {
			return object;
		}

		public void setObject(T object) {
			this.object = object;
		}

	}

	private List resut2Entity(ResultSet rs, Class entityClass) throws Exception {
		List list = new ArrayList();
		if (rs == null) {
			return list;
		}
		ResultSetMetaData tsmt = rs.getMetaData();
		int count = tsmt.getColumnCount();
		while (rs.next()) {
			Object result = entityClass.newInstance();
			for (int i = 1; i <= count; i++) {
				String propertyName = column2PropertyName(tsmt.getColumnName(i));
				if (result != null) {
					if (Types.DATE == tsmt.getColumnType(i)) {
						Date value = rs.getDate(i);
						java.util.Date value1 = value == null ? null : new java.util.Date(value.getTime());
						setValue2Entity(result, propertyName, value1);
					} else {
						Object value = rs.getObject(i);
						setValue2Entity(result, propertyName, value);
					}
				} else {
					result = rs.getObject(i);
				}
			}
			list.add(result);
		}

		return list;
	}

	/**
	 * 执行函数 <br/>
	 * executeStoreProcedure("{?= call fn_find_vessel_seaman2(?,?,?)}",
	 * map,SmmVesselSeamanVo.class);
	 * 
	 */
	public Object executeFunction(String name, Map<Integer, ? extends Object> map, Class entityClass) {
		Object resultObjct;
		List list = new ArrayList();
		SessionImpl session = (SessionImpl) em.getDelegate();
		boolean isNewSession = false;
		if (session.isClosed()) {
			SessionFactoryImplementor sf = (SessionFactoryImplementor) session.getSessionFactory();
			session = (SessionImpl) sf.openSession();
			isNewSession = true;
		}
		Connection conn = session.connection();
		CallableStatement callableStatement = null;
		ResultSet rs = null;
		log.info("excute:" + name);
		try {
			callableStatement = conn.prepareCall(name);
			if (entityClass == null) {
				callableStatement.registerOutParameter(1, Types.NUMERIC);
			} else if (entityClass == String.class) {
				callableStatement.registerOutParameter(1, Types.VARCHAR);
			} else {
				callableStatement.registerOutParameter(1, Types.REF);
			}
			for (int i = 1; i <= map.size(); i++) {
				Object value = map.get(i);
				if (value instanceof String) {
					callableStatement.setString(i + 1, (String) value);
				} else if (value instanceof Date) {
					callableStatement.setDate(i + 1, (Date) value);
				} else if (value instanceof Integer) {
					callableStatement.setInt(i + 1, (Integer) value);
				} else {
					if (value == null) {
						callableStatement.setString(i + 1, null);
					} else {
						callableStatement.setObject(i + 1, value);
					}
				}
				// callableStatement.setObject(i + 1, (String) value);
			}
			callableStatement.execute();
			if (entityClass == null) {
				resultObjct = callableStatement.getObject(1);
			} else if (entityClass == String.class) {
				resultObjct = callableStatement.getObject(1);
			} else {
				resultObjct = list;
				rs = (ResultSet) callableStatement.getObject(1);
			}
			result2Entity(rs, entityClass, list);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (callableStatement != null) {
					callableStatement.close();
				}
				if (isNewSession && !conn.isClosed()) {
					conn.close();
				}
				if (isNewSession && session != null && !session.isClosed()) {
					session.close();
				}
			} catch (Exception e2) {
				log.error("executeFunction error", e2);
				throw new RuntimeException(e2);
			}
		}
		return resultObjct;
	}

	private void result2Entity(ResultSet rs, Class entityClass, List list) throws Exception {
		if (rs == null) {
			return;
		}
		ResultSetMetaData tsmt = rs.getMetaData();
		int count = tsmt.getColumnCount();
		while (rs.next()) {
			Object result = entityClass.newInstance();
			for (int i = 1; i <= count; i++) {
				String propertyName = column2PropertyName(tsmt.getColumnName(i));
				if (result != null) {
					if (Types.DATE == tsmt.getColumnType(i)) {
						Date value = rs.getDate(i);
						java.util.Date value1 = value == null ? null : new java.util.Date(value.getTime());
						setValue2Entity(result, propertyName, value1);
					} else if (Types.NUMERIC == tsmt.getColumnType(i)) {
						Object value = rs.getObject(i);
						setValue2Entity(result, propertyName, value);
					} else if (Types.VARCHAR == tsmt.getColumnType(i) || Types.CHAR == tsmt.getColumnType(i)) {
						Object value = rs.getObject(i);
						setValue2Entity(result, propertyName, value);
					}
				} else {
					result = rs.getObject(i);
				}
			}
			list.add(result);
		}

		rs.close();
	}

	private String column2PropertyName(String columnName) {
		StringBuffer propertyName = new StringBuffer();
		String[] str = columnName.split("_");
		propertyName.append(StringUtils.lowerCase(str[0]));
		for (int i = 1; i < str.length; i++) {
			propertyName.append(firstCharToUpperCase(StringUtils.lowerCase(str[i])));
		}
		return propertyName.toString();
	}

	private String firstCharToUpperCase(String str) {
		String result = StringUtils.upperCase(StringUtils.substring(str, 0, 1)) + StringUtils.substring(str, 1);
		return result;
	}

	private void setValue2Entity(Object entity, String propertyName, Object value) {
		try {
			if (value != null) {
				Method method = null;
				if (value instanceof java.util.Date) {
					method = getSetterMethod(propertyName, entity.getClass(), java.util.Date.class);
				} else if (value instanceof String) {
					method = getSetterMethod(propertyName, entity.getClass(), String.class);
				} else if (value instanceof Long) {
					method = getSetterMethod(propertyName, entity.getClass(), Long.class);
					if (method == null) {
						value = new BigDecimal(((Long) value));
						method = getSetterMethod(propertyName, entity.getClass(), BigDecimal.class);
					}
				} else if (value instanceof BigDecimal) {
					method = getSetterMethod(propertyName, entity.getClass(), BigDecimal.class);
					if (method == null) {
						method = getSetterMethod(propertyName, entity.getClass(), Long.class);
						value = ((BigDecimal) value).longValue();
					}
					if (method == null) {
						method = getSetterMethod(propertyName, entity.getClass(), Boolean.class);
						value = new BooleanConverter().convert(boolean.class, value);
					}
				}
				if (method != null) {
					method.invoke(entity, new Object[] { value });
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Method getSetterMethod(String fieldName, Class entityClass, Class clazz) {
		String methodName = "set" + firstCharToUpperCase(fieldName);
		Method method = null;

		try {
			method = entityClass.getMethod(methodName, new Class[] { clazz });
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		return method;
	}
}
