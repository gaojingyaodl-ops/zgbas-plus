/**
 * 
 */
package com.spt.tools.data.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;

/**
 * @author wlddh
 *
 */
public interface IDataService<T> {

	void saveBatch(List<T> insertedRecords, List<T> updatedRecords, List<T> deletedRecords) throws ApplicationException;

	void delete(Long id) throws ApplicationException;

	T save(T entity) throws ApplicationException;

	T sumPage(Map<String, Object> searchParams);

	List<T> findAll();

	Page<T> findPage(PageSearchVo queryVo);

	T getEntity(Long id);

}
