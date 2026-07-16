package com.spt.tools.data.service;

import java.util.List;
import java.util.Map;

import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.data.vo.DataEntity;
import com.spt.tools.data.vo.PageDown;

public class BaseClientFallback<T extends DataEntity> implements BaseClient<T> {

	@Override
	public void delete(Long id) {
		
	}

	@Override
	public T save(T entity) {
		return null;
	}

	@Override
	public T getEntity(Long id) {
		return null;
	}

	@Override
	public void saveBatch(BatchSaveVo<T> batchSaveVo) {
		
	}

	@Override
	public PageDown<T> findPage(PageSearchVo queryVo) {
		return null;
	}

	@Override
	public T sumPage(Map<String, Object> searchParams) {
		return null;
	}

	@Override
	public List<T> findAll() {
		return null;
	}

}
