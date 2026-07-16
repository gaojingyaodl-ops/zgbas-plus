/**
 * 
 */
package com.spt.tools.data.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.data.vo.DataEntity;

/**
 * @author wlddh
 *
 */
public abstract class BaseApi<T extends DataEntity> {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public abstract IDataService<T> getService();

	@PostMapping(value = "saveBatch")
	public void saveBatch(@RequestBody BatchSaveVo<T> batchSaveVo) throws ApplicationException{
		getService().saveBatch(batchSaveVo.getInsertedRecords(), batchSaveVo.getUpdatedRecords(),
				batchSaveVo.getDeletedRecords());
	}

	@PostMapping(value = "delete")
	public void delete(@RequestBody Long id) throws ApplicationException {
		getService().delete(id);
	}

	@PostMapping(value = "findPage")
	public Page<T> findPage(@RequestBody PageSearchVo queryVo) {
		Page<T> pageVo = getService().findPage(queryVo);
		return pageVo;
	}

	@PostMapping(value = "sumPage")
	public T sumPage(@RequestBody Map<String, Object> searchParams) {
		return getService().sumPage(searchParams);
	}

	@PostMapping(value = "save")
	public T save(@RequestBody T entity) throws ApplicationException{
		T t = getService().save(entity);
		return t;
	}

	@PostMapping(value = "findAll")
	public List<T> findAll() {
		return getService().findAll();
	}

	@PostMapping(value = "getEntity")
	public T getEntity(@RequestBody Long id) {
		T t = getService().getEntity(id);
		return t;
	}
}
