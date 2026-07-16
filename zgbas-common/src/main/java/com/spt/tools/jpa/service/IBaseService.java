/**
 * 
 */
package com.spt.tools.jpa.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.IDataService;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * @author wlddh
 *
 */
public interface IBaseService<T extends IdEntity> extends IDataService<T> {

	void saveBatch(List<T> insertedRecords, List<T> updatedRecords, List<T> deletedRecords) throws ApplicationException;

	void delete(Long id) throws ApplicationException;

	T save(T entity) throws ApplicationException;

	T sumPage(Map<String, Object> searchParams);

	List<T> findAll();

	Page<T> findPage(Map<String, Object> searchParams, PageRequest pageRequest);

	Page<T> findPage(PageSearchVo queryVo);

	T getEntity(Long id);

}
