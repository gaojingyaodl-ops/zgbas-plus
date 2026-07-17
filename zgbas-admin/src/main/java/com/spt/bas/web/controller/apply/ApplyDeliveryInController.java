package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.LogisticsEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.BsWarehouseVo;
import com.spt.bas.client.vo.CtrLogisticsReqVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping(value = "/apply/deliveryIn")
public class ApplyDeliveryInController extends PageController<ApplyDeliveryIn, BaseVo> {
	@Autowired
	private IApplyDeliveryInClient applyDeliveryInClient;
	@Autowired
	private IBsProductTypeClient productTypeClient;
	@Autowired
	private IBsFactoryClient factoryClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IBsWarehouseClient warehouseClient;
	@Autowired
	private IBsWarehouseAddrClient warehouseAddrClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Autowired
	private IFileProcessRelClient fileProcessRelClient;
	@Resource
	private ICtrLogisticsDeliveryClient logisticsDeliveryClient;

	@Override
	public BaseClient<ApplyDeliveryIn> getService() {
		return applyDeliveryInClient;
	}

	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, Model model,PmPermissionVo permissionVo,HttpServletRequest request) {
		String contractId = request.getParameter("contractId");
		ApplyDeliveryIn entity = getEntity(id, contractId);
		if(entity.getLogisticsQuotation()!=null){
			String logisticsQuotation="无";
			entity.setLogisticsQuotation(logisticsQuotation);
		}
		String processCode = request.getParameter("processCode");
		model.addAttribute("processCode", processCode);
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		model.addAttribute("payTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
		model.addAttribute("deliveryTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		model.addAttribute("stockTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_STOCKTYPE)));
		model.addAttribute("spotTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SPOTTYPE)));
		List<BsWarehouse> warehouseList = warehouseClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		List<BsWarehouseAddr> warehouseAddrList = warehouseAddrClient.findAllWarehouseAddr();
		model.addAttribute("warehouseAddrsJson", JsonUtil.obj2Json(warehouseAddrList));
		model.addAttribute("warehouseJson", JsonUtil.obj2Json(warehouseList));
		model.addAttribute("entity", entity);
		model.addAttribute("productJson",
				JsonUtil.obj2Json(productTypeClient.findAll()));
		List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
		//入库方式
		model.addAttribute("deliveryInTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYIN_TYPE)));
		//入库性质
		model.addAttribute("deliveryInNatureJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYIN_NATURE)));
		//包装规格-全部
		model.addAttribute("packingSpecificaJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_IMPORTBUYPACKING)));
		//交货方式
		model.addAttribute("deliveryMode",
				JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BasConstants.DICT_TYPE_DELIVERYMODE)));

		// 附件类型
		List<FileProcessRel> fileTypeList = fileProcessRelClient.findList(processCode);
		model.addAttribute("fileTypeJson", JsonUtil.obj2Json(fileTypeList));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		model.addAttribute("nowDate", LocalDate.now().format(formatter));

		Boolean isFromTP = false;
		if (StringUtils.isNotBlank(contractId) && !StringUtils.equals("null",contractId) && !StringUtils.equals("undefined",contractId)) {
			CtrContract contract = ctrContractClient.getEntity(Long.valueOf(contractId));
			if (Objects.nonNull(contract)) {
				isFromTP = ctrContractClient.checkIsTP(contract.getContractNo());
			}
		} else {
			if (entity.getContractId() != null ) {
				CtrContract contract = ctrContractClient.getEntity(entity.getContractId());
				if (Objects.nonNull(contract)) {
					isFromTP = ctrContractClient.checkIsTP(contract.getContractNo());
				}
			}
		}

		String from = request.getParameter("from");
		if(from!=null){
			return "changeApply/oldDeliveryIn-content";
		}else{
			//处理审批中部分控件可编辑
			permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
			String contractApproveId = request.getParameter("contractApproveId");
			if (StringUtils.isNotEmpty(contractApproveId)){
				model.addAttribute("contractApproveId", Long.parseLong(request.getParameter("contractApproveId")));
			}
			model.addAttribute("psv", permissionVo);

			// 代采 白条进入新的入库页面 或者合同是背靠背业务
			if (Boolean.valueOf(request.getParameter("isFromBkb")) || checkIsBkb(entity.getContractNo()) || isFromTP) {
				return "apply/deliveryIn-contentBkb";
			}
			return "apply/deliveryIn-content";
		}
	}

	/**
	 * 校验是否是背靠背业务
	 * @param contracNo
	 * @return
	 */
	private boolean checkIsBkb(String contracNo) {
		if (StringUtils.isBlank(contracNo)){
			return false;
		}
		CtrContract contract = ctrContractClient.findByContractNoV2(contracNo);
		if (contract != null && BasConstants.BUSINESS_TYPE_ZY_BB.equals(contract.getBusinessType())) {
			return true;
		}
		return false;
	}

	@ModelAttribute("preload")
	public ApplyDeliveryIn getEntity(@RequestParam(value = "id", required = false) Long id, @RequestParam(value = "contractId", required = false) String contractId) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				ApplyDeliveryIn entity = new ApplyDeliveryIn();
				if (StringUtils.isNotBlank(contractId) && NumberUtil.isNumber(contractId)){
					entity = applyDeliveryInClient.generateApplyNo(Long.valueOf(contractId));
				}
				entity.setId(0L);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				return entity;
			}
		}
		return null;
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
		try {
			applyDeliveryInClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

	@RequestMapping(value = "findWarehouse", method = RequestMethod.POST)
	public void findWarehouse(Long id,HttpServletResponse response) {
		BsWarehouse warehouse = warehouseClient.getEntity(id);
		BsWarehouseAddr addr = warehouseAddrClient.findWarehouseAddr(warehouse);
		BsWarehouseVo vo = new BsWarehouseVo();
		BeanUtils.copyProperties(warehouse, vo);
		if (addr!=null) {
			vo.setDefaultAddr(addr.getWarehouseAddr());
		}
		RenderUtil.renderJson(vo,response);
	}

	@RequestMapping(value = "findWarehouseAddrDesc", method = RequestMethod.POST)
	public void findWarehouseAddrDesc(Long id,HttpServletResponse response) {
		if (id != 0L || id != null) {
			List<BsWarehouseAddr> addrList = warehouseAddrClient.findWarehouseAddrDesc(id);
			RenderUtil.renderJson(addrList,response);
		}
	}

	@RequestMapping(value = "queryCancelList", method = RequestMethod.POST)
	public void queryCancelList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		Map<String, Object> map = searchVo.getSearchParams();
		map.put("NEQS_status", BasConstants.APPROVE_STATUS_C);
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		Page<ApplyCancelDetail> page = applyDeliveryInClient.findPageDetail(searchVo);

		JsonEasyUI.renderJson(response, page);
	}

	/**
	 * 生成入库单据
	 * @param deliveryIn
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "generateInFile", method = RequestMethod.POST)
	public void generateInFile(ApplyDeliveryIn deliveryIn, HttpServletRequest request, HttpServletResponse response) {
		try {
			CtrLogisticsReqVo reqVo = new CtrLogisticsReqVo();
			reqVo.setApplyDeliveryIn(deliveryIn);
			String currNumber = request.getParameter("currNumber");
			if (StringUtils.isNotBlank(currNumber) && NumberUtil.isNumber(currNumber)) {
				reqVo.setCurrNumber(new BigDecimal(currNumber));
			}
			reqVo.setContractId(deliveryIn.getContractId());
			reqVo.setContractNo(deliveryIn.getContractNo());
			reqVo.setLogisticsEnum(LogisticsEnum.DELIVERY_IN);
			reqVo.setBizUserName(ShiroUtil.getCurrentUserName());
			CtrLogisticsFile logisticsFile = logisticsDeliveryClient.generateDeliveryFile(reqVo);
			RenderUtil.renderJson(JsonUtil.obj2Json(logisticsFile), response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			String msg = e.getMessage();
			if (e.getCause() != null) {
				msg = e.getCause().getMessage();
			}
			RenderUtil.renderFailure(msg, response);
		}
	}
}
