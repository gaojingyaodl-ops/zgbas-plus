package com.spt.bas.web.controller.apply;


import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.ApplyDelivery;
import com.spt.bas.client.entity.CtrProductFee;
import com.spt.bas.client.remote.IApplyDeliveryClient;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.ICtrProductFeeClient;
import com.spt.bas.client.vo.ApplyDeliveryApplyIdVo;
import com.spt.bas.client.vo.ApplyDeliveryCancelVo;
import com.spt.bas.client.vo.ApplyDeliveryReportVo;
import com.spt.bas.client.vo.CtrProductFeeVo;
import com.spt.bas.report.client.entity.RptApplyDeliveryReport;
import com.spt.bas.report.client.remote.IRptApplyDeliveryReportClient;
import com.spt.bas.report.client.vo.RptApplyDeliverySearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "/apply/deliveryPrint")
public class ApplyDeliveryPrintController extends SingleCrudControll<ApplyDelivery, BaseVo>{

	@Autowired
	private IApplyDeliveryClient applyDeliveryClient;
	@Autowired
	private IRptApplyDeliveryReportClient applyDeliveryReportClient;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private ICtrProductFeeClient ctrProductFeeClient;
	@Override
	public BaseClient<ApplyDelivery> getService() {
		return applyDeliveryClient;
	}
	@Override
	protected void preInsert(ApplyDelivery e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}


	@RequestMapping(value = "", method = RequestMethod.GET)
	public String content(Model model) {
		//提货方式
		model.addAttribute("deliveryTypeJson",
					JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		//交货方式
		model.addAttribute("deliveryModeJson",
					JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
		//出库方式
		model.addAttribute("deliveryOutTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYOUT_TYPE)));
		//状态
		model.addAttribute("printStatusJson",
					JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PRINTSTATUS)));
		model.addAttribute("hasInvalid", ShiroUtil.isPermitted(PermissionEnum.APPROVE_OUT_INVALID.getPermissionCode()));
		//货品树
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		model.addAttribute("businessTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
		//更改提货单权限
		boolean canUpdate = false;
		if(ShiroUtil.isPermitted(PermissionEnum.APPROVE_CTR_UPDATE_STOCKTYPE.getPermissionCode())){
			canUpdate = true;
		}
		model.addAttribute("canUpdate", canUpdate);
		return "apply/deliveryPrint";
	}

	@RequestMapping("findDeliveryPage")
	public void findDeliveryPage(RptApplyDeliverySearchVo searchVo, HttpServletResponse response, HttpServletRequest request) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptApplyDeliveryReport> page = applyDeliveryReportClient.findApplyDeliveryPage(searchVo);
		JsonEasyUI.renderJson(response, page);
	}

	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id, Model model) {
		RptApplyDeliverySearchVo searchVo = new RptApplyDeliverySearchVo();
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		searchVo.setApplyDeliveryId(id);
		PageDown<RptApplyDeliveryReport> page = applyDeliveryReportClient.findApplyDeliveryPage(searchVo);
		List<RptApplyDeliveryReport> reportList = page.getContent();
		if (!reportList.isEmpty()) {
			RptApplyDeliveryReport applyDeliveryReport = reportList.get(0);
			model.addAttribute("report", applyDeliveryReport);
			CtrProductFeeVo feeVo = new CtrProductFeeVo();
			feeVo.setApplyDeliveryId(applyDeliveryReport.getApplyDeliveryId());
			feeVo.setEnterpriseId(applyDeliveryReport.getEnterpriseId());
			CtrProductFee entity = ctrProductFeeClient.findByDeliveryId(feeVo);
			if (entity == null) {
				Long deliveryOutApplyId = applyDeliveryReport.getDeliveryOutApplyId();
				entity = ctrProductFeeClient.getDefaultCtrProductFee(deliveryOutApplyId);
			}
			model.addAttribute("entity", entity);
		}
		//获取业务员树
		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
		//费率
		model.addAttribute("contractFeeRate",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTFEERATE)));
		//业务类型
		model.addAttribute("businessTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
		return "apply/deliveryPrint-detail";
	}

	@RequestMapping(value = "saveDelivery")
	public void saveDelivery(ApplyDeliveryReportVo delivery, HttpServletRequest request, HttpServletResponse response) {
		if (delivery != null && delivery.getApplyDeliveryId() != null) {
			Long applyDeliveryId = delivery.getApplyDeliveryId();
			ApplyDelivery entity = applyDeliveryClient.getEntity(applyDeliveryId);
			if (entity != null) {
				try {
					delivery.setProductId(entity.getProductId());
					delivery.setEnterpriseId(entity.getEnterpriseId());
					applyDeliveryClient.saveDeliveryDetail(delivery);
					ctrProductFeeClient.saveProductFee(delivery);
					RenderUtil.renderSuccess("success", response);
				} catch (Exception e) {
					logger.error(e.getMessage());
					RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
				}
			}
		}
	}

	/**提货单*/
	@RequestMapping(value="printThd",method = RequestMethod.GET)
	public String printThd(@RequestParam("id") Long id, Model model){
		ApplyDeliveryApplyIdVo vo=new ApplyDeliveryApplyIdVo();
		vo.setId(id);
		ApplyDelivery applyDelivery=applyDeliveryClient.getApplyDeliveryEntity(vo);
		if (applyDelivery!=null) {
			model.addAttribute("contractText", applyDelivery.getContent());
			model.addAttribute("printCount", applyDelivery.getPrintCount());
			model.addAttribute("id", applyDelivery.getId());
		}
		return "apply/deliveryPrint-thd";
	}

	/**送柜单*/
	@RequestMapping(value="printSend",method = RequestMethod.GET)
	public String printSend(@RequestParam("id") Long id, Model model){
		ApplyDeliveryApplyIdVo vo=new ApplyDeliveryApplyIdVo();
		vo.setId(id);
		ApplyDelivery applyDelivery=applyDeliveryClient.getApplyDeliverySendSingleEntity(vo);
		if (applyDelivery!=null) {
			model.addAttribute("contractText", applyDelivery.getContent());
			model.addAttribute("printCount", applyDelivery.getPrintCount());
			model.addAttribute("id", applyDelivery.getId());

		}
		return "apply/deliveryPrint-Sgd";
	}
	@RequestMapping(value="printHzd",method = RequestMethod.GET)
	public String printHzd(@RequestParam("id") Long id, Model model){
		ApplyDeliveryApplyIdVo vo=new ApplyDeliveryApplyIdVo();
		vo.setId(id);
		ApplyDelivery applyDelivery=applyDeliveryClient.getApplyDeliveryInvoiceEntity(vo);
		if (applyDelivery!=null) {
			model.addAttribute("contractText", applyDelivery.getContent());
			model.addAttribute("printCount", applyDelivery.getPrintCount());
			model.addAttribute("id", applyDelivery.getId());

		}
		return "apply/deliveryPrint-hzd";
	}

	@RequestMapping(value="printPsd",method = RequestMethod.GET)
	public String printPsd(@RequestParam("id") Long id, Model model){
		ApplyDeliveryApplyIdVo vo=new ApplyDeliveryApplyIdVo();
		vo.setId(id);
		ApplyDelivery applyDelivery=applyDeliveryClient.getApplyDeliveryDistributionEntity(vo);
		if (applyDelivery!=null) {
			model.addAttribute("contractText", applyDelivery.getContent());
			model.addAttribute("printCount", applyDelivery.getPrintCount());
			model.addAttribute("id", applyDelivery.getId());

		}
		return "apply/deliveryPrint-psd";
	}

	/**提货单作废*/
	@RequestMapping(value="printCancel",method = RequestMethod.POST)
	public void printCancel(ApplyDeliveryCancelVo cancelVo,HttpServletResponse response){
		try{
			cancelVo.setUserId(ShiroUtil.getCurrentUserId());
			cancelVo.setUserName(ShiroUtil.getCurrentUserName());
			applyDeliveryClient.doCancel(cancelVo);
			RenderUtil.renderSuccess("success", response);
		}catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("failure", response);
		}
	}

	@RequestMapping(value="startPrint",method=RequestMethod.POST)
	public void startPrint(String printCount,Long id,HttpServletResponse response){
//		try {
//			ApplyDeliveryVo vo=new ApplyDeliveryVo();
//			vo.setPrintCount(add(Integer.parseInt(printCount), 1));
//			vo.setId(id);
//			applyDeliveryClient.startPrint(vo);
//			RenderUtil.renderSuccess(JsonUtil.obj2Json(vo.getPrintCount()), response);
//		} catch (Exception e) {
//			logger.error("errorId:", e);
//			RenderUtil.renderFailure("failure", response);
//		}
	}


}
