package com.spt.bas.web.controller.apply;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;

/**
 * 申请-合同调整申请
 */
@Controller
@RequestMapping(value = "/apply/contractAdjust")
public class ApplyContractAdjustController  extends PageController<ApplyContractAdjust, BaseVo> {

	@Autowired
	private IApplyContractAdjustClient applyContractAdjustClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Autowired
	private IApplyMatchDetailClient applyMatchDetailClient;
	@Autowired
	private IApplyMatchClient applyMatchClient;
	@Autowired
	private IApplyDeliveryOutClient applyDeliveryOutClient;
	@Autowired
	private IBsCompanyClient bsCompanyClient;

	@Override
	public BaseClient<ApplyContractAdjust> getService() {
		return applyContractAdjustClient;
	}

	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id,PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
		ApplyContractAdjust contractAdjust = getEntity(id);

		String processCode = request.getParameter("processCode");
		String contractId = request.getParameter("contractId");

		// 企业抬头
		model.addAttribute("ourCompanyNameJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));

		// 是否出库
		Boolean deliveryOutFlg = false;
		// 是否已收完全款
		Boolean recoveryFullFlg = false;

		// 新建
		if (id != null && id == 0L) {
			if (!StringUtils.isEmpty(contractId)) {
				List<CtrContract> contracts = ctrContractClient.findContractsByContractId(Long.parseLong(contractId));
				for (CtrContract contract : contracts) {
					// 新建赋值
					// 销售
					if (BasConstants.CONTRACT_TYPE_S.equals(contract.getContractType())) {
						// 合同id
						contractAdjust.setSellContractId(contract.getId());
						//合同编号
						contractAdjust.setSellContractNo(contract.getContractNo());
						// 下游企业
						contractAdjust.setSellCompanyId(contract.getCompanyId());
						contractAdjust.setSellCompanyIdB(contract.getCompanyId());
						// 下游企业
						contractAdjust.setSellCompanyName(contract.getCompanyName());
						contractAdjust.setSellCompanyNameB(contract.getCompanyName());
						// 下游企业抬头
						contractAdjust.setSellCompanyTitle(contract.getCompanyTitle());
						contractAdjust.setSellCompanyTitleB(contract.getCompanyTitle());
						// 业务员
						contractAdjust.setSellMatchUserName(contract.getMatchUserName());
						// 约定收款日期
						contractAdjust.setPayFullTime(contract.getPayFullTime());
						// 约定收款日期（修改后）
						contractAdjust.setPayFullTimeB(contract.getPayFullTime());
						// 约定收货日期
						contractAdjust.setSellDeliveryDate(contract.getDeliveryDateTo());
						// 约定收货日期 （修改后）
						contractAdjust.setSellDeliveryDateB(contract.getDeliveryDateTo());
						// 回款周期
						contractAdjust.setCreditDays(contract.getCreditCycle().intValue());
						// 加价
						contractAdjust.setPremium(contract.getPremium());
						// 加价（修改后）
						contractAdjust.setPremiumB(contract.getPremium());
						// 仓储费
						contractAdjust.setSellWarehouseAmount(contract.getWarehouseAmount());
						// 仓储费(修改后)
						contractAdjust.setSellWarehouseAmountB(contract.getWarehouseAmount());
						// 运输费
						contractAdjust.setSellTransformAmount(contract.getTransportAmount());
						// 运输费（修改后）
						contractAdjust.setSellTransformAmountB(contract.getTransportAmount());
						// 销售合同单价
						BigDecimal totalNumber = contract.getTotalNumber();
						BigDecimal totalAmount = contract.getTotalAmount();
						BigDecimal divide = totalAmount.divide(totalNumber, 4, BigDecimal.ROUND_HALF_UP);
						contractAdjust.setSellUnitPrice(divide);
						contractAdjust.setSellUnitPriceB(divide);
						// 销售合同金额
						contractAdjust.setSellTotalAmount(contract.getTotalAmount());
						// 销售合同金额（修改后）
						contractAdjust.setSellTotalAmountB(contract.getTotalAmount());
						// 损耗
						contractAdjust.setLossAmount(contract.getLossAmount());
						// 损耗
						contractAdjust.setLossAmountB(contract.getLossAmount());
						// 业务类型
						contractAdjust.setSettlementType(contract.getSettlementType());
						// 服务合同金额
						contractAdjust.setServiceAmount(contract.getServiceAmount());
						// 服务合同金额（修改后）
						contractAdjust.setServiceAmountB(contract.getServiceAmount());
						// 产品信息
						ApplyMatchDetail applyMatchDetail = applyMatchDetailClient.findByContractNo(contract.getContractNo());
						ApplyMatch applyMatch = applyMatchClient.getEntity(applyMatchDetail.getApplyMatchId());
						// 品种
						contractAdjust.setProductName(applyMatch.getProductName());
						// 牌号
						contractAdjust.setBrandNumber(applyMatch.getBrandNumber());
						// 厂商
						contractAdjust.setFactoryName(applyMatch.getFactoryName());
						// 包装规格
						String wrapSpecs = applyMatch.getWrapSpecs();
						if (StringUtils.isNotBlank(wrapSpecs)) {
							String value = DictUtil.getValue(BasConstants.DICT_TYPE_PACKINGSPECIFICATEXT, wrapSpecs);
							contractAdjust.setWrapSpecs(value);
						}
						String qualityStandard = applyMatch.getQualityStandard();
						if (StringUtils.isNotBlank(qualityStandard)) {
							String value = DictUtil.getValue(BasConstants.DICT_TYPE_QUALITYSTANDDARD, qualityStandard);
							contractAdjust.setQualityStandard(value);
						}
						List<ApplyDeliveryOut> byContractIdNoStatusB = applyDeliveryOutClient.findByContractIdNoStatusB(contract.getId());
						logger.info("ApplyDeliveryOutList:{}", byContractIdNoStatusB.size());
						deliveryOutFlg = !byContractIdNoStatusB.isEmpty();
						model.addAttribute("deliveryOutFlg", deliveryOutFlg);

						// 是否已收完全款
						if (contract.getDealedAmount().compareTo(contract.getTotalAmount()) >=0) {
							recoveryFullFlg = true;
							model.addAttribute("recoveryFullFlg", recoveryFullFlg);
						}

						BsCompany company = bsCompanyClient.getEntity(contract.getCompanyId());
						contractAdjust.setServerRate(company.getRate());

					} else if (BasConstants.CONTRACT_TYPE_B.equals(contract.getContractType())) {
						// 采购========
						// 合同id
						contractAdjust.setBuyContractId(contract.getId());
						// 合同数量
						contractAdjust.setTotalNumber(contract.getTotalNumber());
						contractAdjust.setTotalNumberB(contract.getTotalNumber());
						// 我方公司
						contractAdjust.setOurCompanyName(contract.getOurCompanyName());
						contractAdjust.setOurCompanyNameB(contract.getOurCompanyName());
						// 合同编号
						contractAdjust.setBuyContractNo(contract.getContractNo());
						// 上游企业id
						contractAdjust.setBuyCompanyId(contract.getCompanyId());
						contractAdjust.setBuyCompanyIdB(contract.getCompanyId());
						// 上游企业名
						contractAdjust.setBuyCompanyName(contract.getCompanyName());
						contractAdjust.setBuyCompanyNameB(contract.getCompanyName());

						// 上游企业抬头
						contractAdjust.setBuyCompanyTitle(contract.getCompanyTitle());
						contractAdjust.setBuyCompanyTitleB(contract.getCompanyTitle());

						// 约定收款日期
						contractAdjust.setBuyPayFullTime(contract.getPayFullTime());
						// 约定收款日期（修改后）
						contractAdjust.setBuyPayFullTimeB(contract.getPayFullTime());
						// 约定收货日期
						contractAdjust.setBuyDeliveryDate(contract.getDeliveryDateTo());
						// 约定收货日期 （修改后）
						contractAdjust.setBuyDeliveryDateB(contract.getDeliveryDateTo());

						// 业务员
						contractAdjust.setBuyMatchUserName(contract.getMatchUserName());
						// 采购合同单价
						// 单价
						BigDecimal totalNumber = contract.getTotalNumber();
						BigDecimal totalAmount = contract.getTotalAmount();
						BigDecimal divide = totalAmount.divide(totalNumber, 4, BigDecimal.ROUND_HALF_UP);
						contractAdjust.setBuyUnitPrice(divide);
						contractAdjust.setBuyUnitPriceB(divide);
						// 采购合同金额
						contractAdjust.setBuyTotalAmount(totalAmount);
						// 采购合同金额(修改后)
						contractAdjust.setBuyTotalAmountB(totalAmount);
						// 仓储费
						contractAdjust.setBuyWarehouseAmount(contract.getWarehouseAmount());
						// 仓储费(修改后)
						contractAdjust.setBuyWarehouseAmountB(contract.getWarehouseAmount());
						// 运输费
						contractAdjust.setBuyTransformAmount(contract.getTransportAmount());
						// 运输费(修改后)
						contractAdjust.setBuyTransformAmountB(contract.getTransportAmount());
						// 不含税单价
						ApplyMatchDetail applyMatchDetail = applyMatchDetailClient.findByContractNo(contract.getContractNo());
						contractAdjust.setNoTaxPrice(applyMatchDetail.getDealAmountNotax());

					}
				}
			}
		}
		model.addAttribute("entity", contractAdjust);
		String canEditFile = request.getParameter("hasEditFile");
		boolean canEdit = BooleanUtils.toBoolean(canEditFile);
		model.addAttribute("hasEditFile", canEdit);

		model.addAttribute("processCode", processCode);

		return "apply/contractAdjust-content";
	}

	@ModelAttribute("preload")
	public ApplyContractAdjust getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				ApplyContractAdjust entity = new ApplyContractAdjust();
				entity.setId(0L);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				return entity;
			}
		}
		return null;
	}

	@RequestMapping(value = "updateSellFileId", method = RequestMethod.POST)
	public void updateSellFileId(FileIdUpdateVo vo,
							 HttpServletResponse response) {
		try {
			applyContractAdjustClient.updateSellFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

	@RequestMapping(value = "updateBuyFileId", method = RequestMethod.POST)
	public void updateBuyFileId(FileIdUpdateVo vo,
								 HttpServletResponse response) {
		try {
			applyContractAdjustClient.updateBuyFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
								HttpServletResponse response) {
		try {
			applyContractAdjustClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}
}
