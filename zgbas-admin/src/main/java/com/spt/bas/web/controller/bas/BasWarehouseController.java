package com.spt.bas.web.controller.bas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.spt.bas.client.remote.IBsAreaClient;
import com.spt.bas.client.vo.CompanyAreaVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.FormConfigConstants;
import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.remote.IBsTemplateConfigClient;
import com.spt.bas.client.remote.IBsWarehouseClient;
import com.spt.bas.client.vo.BsWarehouseSearchVo;
import com.spt.bas.client.vo.BsWarehouseVo;
import com.spt.bas.client.vo.TemplateQueryVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;

/**
 * 仓库
 * @author
 *
 */
@Controller
@RequestMapping(value = "/bas/warehouse")
public class BasWarehouseController extends PageController<BsWarehouse, BaseVo>{

	@Autowired
	private IBsTemplateConfigClient templateConfigClient;
	@Autowired
	private IBsWarehouseClient bsWarehouseClient;
	@Autowired
	private IBsAreaClient areaClient;

	@Override
	public BaseClient<BsWarehouse> getService() {
		return bsWarehouseClient;
	}

	@ResponseBody
	@RequestMapping(value = "findWarehoseList/{typeCd}")
	public List<BsWarehouse> findWarehoseList(@PathVariable("typeCd") String typeCd) {
		TemplateQueryVo queryVo = new TemplateQueryVo();
		queryVo.setTypeCd(typeCd);
		Map<String, String> confValue = templateConfigClient.findTemplateValue(queryVo);
		BsWarehouseSearchVo vo=new BsWarehouseSearchVo();
		vo.setIds(confValue.get(FormConfigConstants.KEY_WAREHOUSEID));
		vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		List<BsWarehouse> list = bsWarehouseClient.findList(vo);
		return list;
	}

	@RequestMapping(value = "")
	public String index(Model model) {
		model.addAttribute("enableFlgs",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
		return "bas/warehouse";
	}

	@RequestMapping(value = "findWarehouses")
	public void findAllWarehouse(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		PageDown<BsWarehouse> page = bsWarehouseClient.findWarehouses(searchVo);
		JsonEasyUI.renderJson(response, page);
	}

	@RequestMapping(value = "validateWarehouse")
	public void validateWarehouse(BsWarehouse warehouse,HttpServletRequest request, HttpServletResponse response){
		if(warehouse!=null){
			warehouse.setEnterpriseId(ShiroUtil.getEnterpriseId());
			Long count = this.bsWarehouseClient.countWarehouse(warehouse);
			if(count>0l){
				RenderUtil.renderText("false", response);
				return;
			}
		}
		RenderUtil.renderText("true", response);
	}

	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id, Model model) {
		model.addAttribute("enableFlgs",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
//		List<BsWarehouse> lstWarehouse=bsWarehouseClient.findAll();
//		model.addAttribute("Warehouse", JsonUtil.obj2Json(lstWarehouse));
		model.addAttribute("id", id);
		BsWarehouse entity;
		if (id != null && id != 0) {
			entity = getEntity(id);
			model.addAttribute("entity", entity);
		} else {
			entity = new BsWarehouse();
			entity.setId(0l);
		}
		model.addAttribute("entity", entity);
		// 获取地区，展示的时候使用
		model.addAttribute("areaList",JsonUtil.obj2Json(areaClient.getAllArea()));
		return "bas/warehouse-detail";

	}
	@ModelAttribute("preload")
	public BsWarehouse getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				BsWarehouse entity = new BsWarehouse();
				entity.setId(0l);
				return entity;
			}
		}
		return null;
	}
//	@RequestMapping(value = "save", method = RequestMethod.POST)
//	public void save(@RequestParam("param") String param,HttpServletRequest request, HttpServletResponse response) {
//		List<BsWarehouse> entity=JSON.parseArray(param,BsWarehouse.class);
//		if(entity.size()==0){
//			return ;
//		}
//		try {
//			for(BsWarehouse list:entity){
//				bsWarehouseClient.save(list);
//				RenderUtil.renderSuccess("success", response);
//			}
//		} catch (Exception e) {
//			RenderUtil.renderFailure("failure", response);
//		}
//	}

	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		try {
			BsWarehouse entity = getService().getEntity(id);
			entity.setEnableFlg(false);
			bsWarehouseClient.save(entity);
			RenderUtil.renderSuccess("删除成功", response);
		} catch (Exception e) {
			RenderUtil.renderFailure("操作错误，请联系管理员", response);
		}
		return null;
	}

	@RequestMapping(value= "verify/{warehouseName}", method = RequestMethod.GET)
	public void verify(@Valid @ModelAttribute("warehouseName")String warehouseName,HttpServletRequest request, HttpServletResponse response){
		try{
			if(StringUtils.isNotBlank(warehouseName)){
				BsWarehouse bs=new BsWarehouse();
				bs.setWarehouseName(warehouseName);
				bs.setEnterpriseId(ShiroUtil.getEnterpriseId());
				List<BsWarehouse> list = bsWarehouseClient.findByWarehouseNameAndEnterpriseId(bs);
				//List<BsWarehouse> list = bsWarehouseClient.queryBsWarehouseName(bs);
				int count = 0;
				if(list!=null&&list.size()>0){
					count = list.size();
				}
				RenderUtil.renderSuccess(count+"", response);
			}
		}catch(Exception e){
			e.printStackTrace();
			RenderUtil.renderFailure("fail", response);
		}
	}

	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}

	@RequestMapping(value = "findWarehouseByCompanyId/{id}")
	public void findWarehouseByCompanyId(@PathVariable("id") Long id, HttpServletResponse response) {
		RenderUtil.renderJson(bsWarehouseClient.findWarehouseByCompanyId(id), response);
	}

	@RequestMapping(value = "findByCompanyId/{id}")
	public void findByCompanyId(@PathVariable("id") Long id, HttpServletResponse response) {
		RenderUtil.renderJson(bsWarehouseClient.findByCompanyId(id), response);
	}

	@RequestMapping(value = "findByCompanyIdAddr/{id}")
	public void findByCompanyIdAddr(@PathVariable("id") Long id, HttpServletResponse response) {
		 List<BsWarehouse> byCompanyIdAddr = bsWarehouseClient.findByCompanyIdAddr(id);
		 if(byCompanyIdAddr.size()>0){
			 for (BsWarehouse warehouse : byCompanyIdAddr) {
				 if(StringUtils.isNotEmpty(warehouse.getAreaCode())){
					 CompanyAreaVo vo = areaClient.getAreaVo(Long.valueOf(warehouse.getAreaCode()));
					 String area=" ";
					 if(null==vo.getCityName() && null==vo.getRegionName()){
						 area=vo.getProvinceName();
					 }
					 else if(null==vo.getCityName()){
						 area=vo.getProvinceName()+"/"+vo.getRegionName();
					 }
					 else if(null==vo.getRegionName()){
						 area=vo.getProvinceName()+"/"+vo.getCityName();
					 }
					 else{
						 area=vo.getProvinceName()+"/"+vo.getCityName()+"/"+vo.getRegionName();
					 }
					 if(vo.getProvinceName()!=null){
						 warehouse.setProvince(String.valueOf(vo.getProvinceName()));
					 }
					 if(vo.getCityName()!=null){
						 warehouse.setCity(String.valueOf(vo.getCityName()));
					 }else{
						 if(vo.getRegionName()!=null){
							 warehouse.setCity(String.valueOf(vo.getRegionName()));
						 }
						 vo.setRegionName(null);
					 }
					 if(vo.getRegionName()!=null){
						 warehouse.setArea(String.valueOf(vo.getRegionName()));
					 }
					 if(vo.getProvinceId()!=null){
						 warehouse.setProvinceCode(String.valueOf(vo.getProvinceId()));
					 }
					 if(vo.getCityId()!=null){
						 warehouse.setCityCode(String.valueOf(vo.getCityId()));
					 }else{
						 if(vo.getRegionId()!=null){
							 warehouse.setCityCode(String.valueOf(vo.getRegionId()));
						 }
						 vo.setRegionId(null);
					 }
					 if(vo.getRegionId()!=null){
						 warehouse.setAreaCode(String.valueOf(vo.getRegionId()));
					 }else{
						 warehouse.setAreaCode("无");
					 }
					 warehouse.setWarehouseName(area);
				 }
			 }
			 RenderUtil.renderJson(byCompanyIdAddr,response);
			 return;
		 }
		RenderUtil.renderJson(byCompanyIdAddr,response);
	}

}
