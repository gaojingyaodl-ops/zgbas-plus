package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInternalBuy;
import com.spt.bas.client.entity.ApplyInternalBuyDetail;
import com.spt.bas.client.remote.IApplyInternalBuyClient;
import com.spt.bas.client.remote.IApplyInternalBuyDetailClient;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/apply/internalBuy")
public class ApplyInternalBuyController extends PageController<ApplyInternalBuy, BaseVo>{

	@Autowired
	private IApplyInternalBuyClient applyInternalBuyClient;
	@Autowired
	private IApplyInternalBuyDetailClient applyInternalBuyDetailClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Override
	public BaseClient<ApplyInternalBuy> getService() {
		return applyInternalBuyClient;
	}
	
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id,PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
		ApplyInternalBuy internalBuy = getEntity(id);
		model.addAttribute("entity", internalBuy);
		if(id>0){
			List<ApplyInternalBuyDetail> list = applyInternalBuyDetailClient.findByApplyInternalBuyId(id);
			for (ApplyInternalBuyDetail detail : list) {
				if(StringUtils.equals(detail.getDetailType(), BasConstants.CONTRACTADJUSTDETAILTYPE_N)){
					model.addAttribute("ndetail", detail);
				}else{
					model.addAttribute("odetail", detail);
				}
			}
		}
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, internalBuy.getApproveId());
		model.addAttribute("psv", permissionVo);
		//产品类型
		model.addAttribute("productType",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		return "apply/interBuy-content";
	}
	
	@ModelAttribute("preload")
	public ApplyInternalBuy getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				ApplyInternalBuy entity = new ApplyInternalBuy();
				entity.setId(0l);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				return entity;
			}
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping("findDetailByStockDetailId")
	public List<ApplyInternalBuyDetail> findDetailByStockDetailId(@RequestParam("stockDetailId") Long stockDetailId){
		return applyInternalBuyDetailClient.findByStockDetailId(stockDetailId);
	}

}
