package com.spt.bas.web.controller.bas;


import com.spt.bas.client.entity.BsArea;
import com.spt.bas.client.entity.BsAreaCost;
import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.remote.IBsAreaClient;
import com.spt.bas.client.remote.IBsAreaCostClient;
import com.spt.bas.client.remote.IBsWarehouseClient;
import com.spt.bas.client.vo.AcquirePriceVo;
import com.spt.bas.client.vo.CompanyAreaVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 仓库
 * @author
 *
 */
@Controller
@RequestMapping(value = "/bs/areaCost")
public class BsAreaCostController extends SingleCrudControll<BsAreaCost, BaseVo>{

	@Autowired
	private IBsAreaCostClient bsAreaCostClient;
	
	@Autowired
	private IBsAreaClient bsAreaClient;
	
	@Autowired
    private IBsWarehouseClient bsWarehouseClient;
	@Override
	public BaseClient<BsAreaCost> getService() {
		return bsAreaCostClient;
	}
	@Override
	protected void preInsert(BsAreaCost e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
	@RequestMapping(value = "")
	public String index(Model model) {
		model.addAttribute("enterpriseId",ShiroUtil.getEnterpriseId());
		return "bas/areaCost";
	}
	
	@RequestMapping(value = "findAreaCosts")
	public void findAllWarehouse(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		PageDown<BsAreaCost> page = bsAreaCostClient.findPage(searchVo);
		JsonEasyUI.renderJson(response, page);
	}
	
	//省市区获取
	@RequestMapping("ajaxGetCity")
    public void ajaxGetCity(HttpServletRequest request,HttpServletResponse response,String id)  {
        List<BsArea> findTopLevel = new ArrayList<BsArea>();
		if(!id.equals("")){
			if( String.valueOf(1).equals(id)){
				findTopLevel = bsAreaClient.findTopLevel();
			} else {
				findTopLevel = bsAreaClient.findByParentId(id);
			}
		}
        RenderUtil.renderJson(findTopLevel, response);
    }
	
	
	@RequestMapping("ajaxfindByAreaCode")
	public void ajaxfindByAreaCode(HttpServletRequest request,HttpServletResponse response,String id)  {
		try {
			List<BsAreaCost> areaCodes = bsAreaCostClient.findByAreaCode(id);
			if(null != areaCodes && areaCodes.size() > 0){
				RenderUtil.renderJson(areaCodes.get(0), response);
			}
		} catch (Exception e) {
			RenderUtil.renderFailure("failure" + e.getMessage(), response);
		}


	}

	
	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id, Model model) {
	
		model.addAttribute("id", id);
		BsAreaCost entity;
		if (id != null && id != 0) {
			entity = getEntity(id);
			//查找地区所属的市和省
			CompanyAreaVo areaVo = bsAreaClient.getAreaVo(Long.parseLong(entity.getAreaCode()));
			entity.setAreaVo(areaVo);
			model.addAttribute("entity", entity);
		} else {
			entity = new BsAreaCost();
			entity.setId(0l);
			CompanyAreaVo areaVo = new CompanyAreaVo();
			areaVo.setProvinceId(0l);
			entity.setAreaVo(areaVo);
		}
		model.addAttribute("entity", entity);
		return "bs/areaCost_detail";
	
	}

	@ModelAttribute("preload")
	public BsAreaCost getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				BsAreaCost entity = new BsAreaCost();
				entity.setId(0l);
				return entity;
			}
		}
		return null;
	}
	
	@PostMapping(value="saveAreaCost")
	public void saveAddr(BsAreaCost vo, HttpServletRequest request,HttpServletResponse response) throws ApplicationException{
		try{
		vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		String[] split = vo.getAreaCode().split(",");
		vo.setAreaCode(split[split.length-1]);
		//vo.setAreaCode(vo.getAreaCode().replace(",",""));
		//先查找
		List<BsAreaCost> areaCodeByCode = bsAreaCostClient.findByAreaCode(vo.getAreaCode());
		if( null != areaCodeByCode){
			RenderUtil.renderText( "地区单价存在！", response);
			return;
		}else{
			bsAreaCostClient.save(vo);
			RenderUtil.renderText("保存成功", response);
		}
		}
		catch (Exception e) {
			logger.error("saveAreaCost", e);
			RenderUtil.renderFailure("failure" + e.getMessage(), response);
		}
	}
	
	@RequestMapping(value="delete/{id}",method = RequestMethod.POST)
	public void delete(@PathVariable("id") Long id,HttpServletResponse response) throws IllegalAccessError{
		try {
			getService().delete(id);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			RenderUtil.renderText(e.getMessage(),response);
		}	
		
	}
	
	//根据仓库名称或者地区code获取单价
	@RequestMapping(value = "acquirePriceVo")
	public void acquirePriceVo(AcquirePriceVo priceVo, HttpServletRequest request, HttpServletResponse response) {
		AcquirePriceVo getPrice = new AcquirePriceVo();
		if(StringUtils.isNotBlank(priceVo.getWarehouseName())){
			//优先根据仓库名称获取单价
			BsWarehouse house = new BsWarehouse();
			house.setEnterpriseId(ShiroUtil.getEnterpriseId());
			house.setWarehouseName(priceVo.getWarehouseName());
			List<BsWarehouse>BsWarehouseList = bsWarehouseClient.findByWarehouseNameAndEnterpriseId(house);
			if(null !=BsWarehouseList && BsWarehouseList.size() > 0){
				if( null != BsWarehouseList.get(0).getWarehouseUnitCost()){
					getPrice.setWarehouseUnitCost( BsWarehouseList.get(0).getWarehouseUnitCost());
				}
				RenderUtil.renderJson(getPrice, response);
				return;
			}
		} else {
			List<BsAreaCost> findByAreaCodes = bsAreaCostClient.findByAreaCode(priceVo.getAreaCode());
			if(null != findByAreaCodes && findByAreaCodes.size() > 0){
				if(null != findByAreaCodes.get(0).getWarehouseUnitCost()){
					getPrice.setWarehouseUnitCost(findByAreaCodes.get(0).getWarehouseUnitCost());
				}
			}
			RenderUtil.renderJson(getPrice, response);
			return;
		}
		RenderUtil.renderJson(getPrice, response);
	}
}
