/**
 * 
 */
package com.spt.tools.data.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.data.vo.DataEntity;
import com.spt.tools.data.vo.PageDown;

/**
 * @author wlddh
 *
 */
public interface BaseClient<T extends DataEntity> {
	@PostMapping(value = "delete")
	public void delete(@RequestBody Long id);

	@PostMapping(value = "findPage")
	public PageDown<T> findPage(@RequestBody PageSearchVo queryVo);

	@PostMapping(value = "sumPage")
	public T sumPage(@RequestBody Map<String, Object> searchParams);

	@PostMapping(value = "save")
	public T save(@RequestBody T entity);

	@PostMapping(value = "findAll")
	public List<T> findAll();

	@PostMapping(value = "getEntity")
	public T getEntity(@RequestBody Long id);
	
	@PostMapping(value = "saveBatch")
	void saveBatch(@RequestBody BatchSaveVo<T> batchSaveVo);
}
