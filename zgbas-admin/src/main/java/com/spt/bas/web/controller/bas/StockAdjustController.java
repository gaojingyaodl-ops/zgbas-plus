package com.spt.bas.web.controller.bas;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockAdjust;
import com.spt.bas.client.entity.StockAdjustDetail;
import com.spt.bas.client.remote.IStockAdjustClient;
import com.spt.bas.client.vo.StockAdjustAuditVo;
import com.spt.bas.client.vo.StockAdjustVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;

/**
 * 库存盘点
 *
 */
@Controller
@RequestMapping("/stock/adjust")
public class StockAdjustController extends SingleCrudControll<StockAdjust, BaseVo>{

	@Autowired
	private IStockAdjustClient stockAdjustClient;
	
	@Override
	public BaseClient<StockAdjust> getService() {
		return stockAdjustClient;
	}
	@Override
	protected void preInsert(StockAdjust e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
	@RequestMapping(value="")
	public String index(Model model){
		model.addAttribute("adjustStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.ADJUST_STATUS)));
		return "bas/stock-adjust";
	}
	
	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id, Model model) {
		StockAdjust adjust = getEntity(id);
		model.addAttribute("entity", adjust);
		return "bas/stock-adjustDetail";
	}
	
	@PostMapping(value = "saveAdjust")
	public void saveAdjust(StockAdjustVo vo,HttpServletRequest request,HttpServletResponse response){
		vo.setCreateUserId(ShiroUtil.getCurrentUserId());
		vo.setCreateUserName(ShiroUtil.getCurrentUserName());
		vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		List<StockAdjustDetail> lstInsert =  JsonEasyUI.getInsertRecords(StockAdjustDetail.class,request);
		List<StockAdjustDetail> lstUpdate =  JsonEasyUI.getUpdatedRecords(StockAdjustDetail.class,request);
		List<StockAdjustDetail> lstDelete =  JsonEasyUI.getDeletedRecords(StockAdjustDetail.class,request);
		vo.setBatchSub(lstInsert, lstUpdate, lstDelete);
		stockAdjustClient.saveAdjust(vo);
		RenderUtil.renderSuccess("保存成功", response);
	}
	
	@PostMapping(value="audit/{id}")
	public void audit(@PathVariable("id") Long id,HttpServletResponse response){
		StockAdjustAuditVo vo = new StockAdjustAuditVo();
		vo.setStockAdjustId(id);
		vo.setUserId(ShiroUtil.getCurrentUserId());
		vo.setUserName(ShiroUtil.getCurrentUserName());
		stockAdjustClient.audit(vo);
		RenderUtil.renderSuccess("保存成功", response);
	}
	
	@ModelAttribute("preload")
	public StockAdjust getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				StockAdjust entity = new StockAdjust();
				entity.setId(0l);
				entity.setAdjustStatus(BasConstants.ADJUST_STATUS_N);
				return entity;
			}
		}
		return null;
	}

}
