package com.spt.bas.web.controller.bas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spt.bas.web.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Maps;
import com.spt.bas.client.entity.BsWarehouseAddr;
import com.spt.bas.client.remote.IBsWarehouseAddrClient;
import com.spt.bas.client.remote.IBsWarehouseClient;
import com.spt.bas.client.vo.BsWarehouseAddrVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
/**
 * 仓库地址
 * @author yangjie
 *
 */
@Controller
@RequestMapping(value = "/bas/warehouseAddr")
public class BasWarehouseAddrController extends SingleCrudControll<BsWarehouseAddr, BaseVo>{
	@Autowired
    private IBsWarehouseAddrClient bsWarehouseAddrClient;
	
	@Autowired
    private IBsWarehouseClient bsWarehouseClient;

	@Override
	public BaseClient<BsWarehouseAddr> getService() {
		return bsWarehouseAddrClient;
	}
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	
	@RequestMapping(value = "querywarehouseAddr/{id}", method = RequestMethod.GET)
	public String querywarehouseAddr(@PathVariable("id") Long id,Model model){
	    if(id!=null){
	    	model.addAttribute("warehouseId", id);//仓库id
	    }
		return "bas/warehouseAddr";
	}
	
	@RequestMapping(value = "addrList/{id}", method = RequestMethod.POST)
	public void addrList(@PathVariable("id") Long id, HttpServletResponse response){
		if(id!=null){
			PageSearchVo searchVo = new PageSearchVo();
			searchVo.setSort("id");
			searchVo.setOrder("DESC");
			searchVo.setRows(50);
			Map<String, Object> searchParams = new HashMap<String, Object>();
			searchParams.put("EQL_warehouseId",id);
			searchVo.setSearchParams(searchParams);
			PageDown<BsWarehouseAddr> page = bsWarehouseAddrClient.findPage(searchVo);
			JsonEasyUI.renderJson(response, page);
		}
	}
	
	/*@RequestMapping(value="update/{id}",method = RequestMethod.POST)
	public void update(@PathVariable("id") Long id,HttpServletResponse response){
		try {
			BsWarehouseAddr bsWarehouseAddr = getService().getEntity(id);
			bsWarehouseAddr.setDefaultFlg(true);
			getService().save(bsWarehouseAddr);
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			RenderUtil.renderFailure("failure",response);
		}	
		RenderUtil.renderSuccess("success", response);
	}*/
	
	@PostMapping(value="saveAddr")
	public void saveAddr(BsWarehouseAddrVo vo, HttpServletRequest request,HttpServletResponse response) throws ApplicationException{
		try{
		List<BsWarehouseAddr> lstInsert = JsonEasyUI.getInsertRecords(BsWarehouseAddr.class, request);
		List<BsWarehouseAddr> lstUpdate = JsonEasyUI.getUpdatedRecords(BsWarehouseAddr.class, request);
		List<BsWarehouseAddr> lstDelete = JsonEasyUI.getDeletedRecords(BsWarehouseAddr.class, request);
		vo.setBatchSub(lstInsert, lstUpdate, lstDelete);
		//bsWarehouseAddrClient.saveAddr(vo);
		vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		// 去除换行字符串
		if(StringUtils.isNotEmpty(vo.getStorageComputeFormula())){
			String storageComputeFormula = vo.getStorageComputeFormula();
			storageComputeFormula =storageComputeFormula.replaceAll("[\n\r]", "");;
			vo.setStorageComputeFormula(storageComputeFormula);
		}
		bsWarehouseClient.saveAddr(vo);
		RenderUtil.renderSuccess("success", response);
		}
		catch (Exception e) {
			logger.error("saveAccount", e);
		}
	}
	
	@RequestMapping(value="delete/{id}",method = RequestMethod.POST)
	public void update(@PathVariable("id") Long id,HttpServletResponse response) throws IllegalAccessError{
		try {
			getService().delete(id);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			RenderUtil.renderFailure("只有一条数据不能删除",response);
		}	
		
	}
	
	@RequestMapping(value = "validateWarehouse")
	public void validateWarehouse(BsWarehouseAddr addr,HttpServletRequest request, HttpServletResponse response){
		if(addr!=null){
			Long count = this.bsWarehouseAddrClient.countWarehouse(addr);
			if(count>0l){
				RenderUtil.renderText("false", response);
				return;
			}
		}
		RenderUtil.renderText("true", response);
	}
	
}
