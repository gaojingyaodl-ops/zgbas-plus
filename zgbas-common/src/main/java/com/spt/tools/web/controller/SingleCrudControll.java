/**
 * 
 */
package com.spt.tools.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;

import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.data.vo.DataEntity;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;

/**
 * 单表‘增删改查’功能
 * 
 * @author huangjian
 * 
 */
public abstract class SingleCrudControll<T extends DataEntity, V extends BaseVo> extends PageController<T, V> {
	@RequestMapping(value = "saveBatch")
	public String saveBatch(HttpServletRequest request, HttpServletResponse response) {
		Class<V> voClass = getVoClass();
		Class<T> entityClass = getEntityClass();
		List<T> listInserted;
		List<T> listUpdated;
		List<T> listDeleted;
		if (voClass != BaseVo.class) {
			List<V> insertedRecords = JsonEasyUI.getInsertRecords(voClass, request);
			List<V> updatedRecords = JsonEasyUI.getUpdatedRecords(voClass, request);
			List<V> deletedRecords = JsonEasyUI.getDeletedRecords(voClass, request);
			listInserted = vo2Entity(insertedRecords);
			listUpdated = vo2Entity(updatedRecords);
			listDeleted = vo2Entity(deletedRecords);
		} else {
			listInserted = JsonEasyUI.getInsertRecords(entityClass, request);
			listUpdated = JsonEasyUI.getUpdatedRecords(entityClass, request);
			listDeleted = JsonEasyUI.getDeletedRecords(entityClass, request);
		}
		BatchSaveVo<T> batchVo =new BatchSaveVo<>();
		batchVo.setDeletedRecords(listDeleted);
		batchVo.setInsertedRecords(listInserted);
		batchVo.setUpdatedRecords(listUpdated);
		preInsert(listInserted);
		preUpdate(listUpdated);
		getService().saveBatch(batchVo);
		RenderUtil.renderSuccess("保存成功", response);
		return null;
	}
	
	private void preInsert(List<T> listInserted){
		for(T e:listInserted){
			preInsert(e);
		}
	}
	
	protected void preInsert(T e){};
	
	private void preUpdate(List<T> listUpdated){
		for(T e:listUpdated){
			preUpdate(e);
		}
	}
	protected void preUpdate(T e){};
	

	private List<T> vo2Entity(List<V> ListVo) {
		try {
			List<T> list = new ArrayList<>(ListVo.size());
			for (V vo : ListVo) {
				T entity = getEntityClass().newInstance();
				copyVo2Entity(vo, entity);
				list.add(entity);
			}
			return list;
		} catch (Exception e) {
			logger.error("vo2Entity error", e);
		}
		return null;
	}

	/** 将Vo属性copy到实体，用于保存 */
	protected void copyVo2Entity(V vo, T entity) {
//		entity.setId(vo.getId());
	}
}
