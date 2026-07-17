package com.spt.bas.web.controller.apply;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyManualSettlement;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.IApplyManualSettlementClient;
import com.spt.bas.client.remote.IBudgetSettlementClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.vo.BudgetSettlementVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping(value = "/apply/manualSettlement")
public class ApplyManualSettlementController extends PageController<ApplyManualSettlement, BaseVo> {

	@Autowired
	private IApplyManualSettlementClient applyManualSettlementClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Autowired
	private IBudgetSettlementClient budgetSettlementClient;

	@Override
	public BaseClient<ApplyManualSettlement> getService() {
		return applyManualSettlementClient;
	}

	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, Model model,PmPermissionVo permissionVo,HttpServletRequest request) {
		ApplyManualSettlement entity = getEntity(id);
		String processCode = request.getParameter("processCode");
		String contractId = request.getParameter("contractId");
		Long sellContractId = null;
		// 新建
		if (id != null && id == 0L) {
			if (!StringUtils.isEmpty(contractId)) {
				List<CtrContract> contracts = ctrContractClient.findContractsByContractId(Long.parseLong(contractId));
				for (CtrContract contract : contracts) {
					// 新建赋值
					// 销售
					if (BasConstants.CONTRACT_TYPE_S.equals(contract.getContractType())) {
						sellContractId = contract.getId();
						// 开票金额
						entity.setInvoiceAmount(contract.getBilledAmount());
						entity.setInvoiceAmountB(contract.getBilledAmount());
						// 采购合同号
						entity.setSellContractNo(contract.getContractNo());
						// 采购合同金额
						entity.setSellContractTotalAmount(contract.getTotalAmount());
						entity.setSellContractTotalAmountB(contract.getTotalAmount());
						// 收款金额
						entity.setReceiveAmount(contract.getDealedAmount());
						entity.setReceiveAmountB(contract.getDealedAmount());
						// 确认收货数量
						entity.setConfirmReceivedGoods(contract.getConfirmReceiveNumber());
						entity.setConfirmReceivedGoodsB(contract.getConfirmReceiveNumber());
						// 出库数量
						entity.setDeliveryOutNumber(contract.getWarehouseNumber());
						entity.setDeliveryOutNumberB(contract.getWarehouseNumber());
						// 约定收全款日期
						entity.setPayFullTime(contract.getPayFullTime());
						entity.setPayFullTimeB(contract.getPayFullTime());
						// 服务费合同金额
						entity.setServiceTotalAmount(contract.getServiceAmount());
						entity.setServiceTotalAmountB(contract.getServiceAmount());
						// 已收服务费金额
						entity.setReceiveServiceAmount(contract.getReceiveServiceAmount());
						entity.setReceiveServiceAmountB(contract.getReceiveServiceAmount());
						// 服务费开票金额
						entity.setServiceInvoiceAmount(contract.getServiceBilledAmount());
						entity.setServiceInvoiceAmountB(contract.getServiceBilledAmount());
						// 需方企业
						entity.setSellCompanyName(contract.getCompanyName());
						// 需方业务员
						entity.setSellMatchUserName(contract.getMatchUserName());
						// 单价
						BigDecimal totalNumber = contract.getTotalNumber();
						BigDecimal totalAmount = contract.getTotalAmount();
						BigDecimal divide = totalAmount.divide(totalNumber, 2, BigDecimal.ROUND_HALF_UP);
						entity.setSellUnitPrice(divide);
						entity.setBreachAmount(contract.getBreachAmount());
						entity.setBreachDays(contract.getBreachDays().intValue());
						entity.setReceiveBreachAmount(contract.getReceiveBreachAmount());
						entity.setPayFullTime(contract.getPayFullTime());
						// 仓储费
						entity.setSellWarehouseAmount(contract.getWarehouseAmount());
						entity.setSellWarehouseAmountB(contract.getWarehouseAmount());
						// 运输费
						entity.setSellTransAmount(contract.getTransportAmount());
						entity.setSellTransAmountB(contract.getTransportAmount());
					} else if (BasConstants.CONTRACT_TYPE_B.equals(contract.getContractType())) {
						// 采购========
						// 收票金额
						entity.setReceiveInvoiceAmount(contract.getBilledAmount());
						entity.setReceiveInvoiceAmountB(contract.getBilledAmount());
						// 采购合同号
						entity.setBuyContractNo(contract.getContractNo());
						// 采购合同额
						entity.setBuyContractTotalAmount(contract.getTotalAmount());
						entity.setBuyContractTotalAmountB(contract.getTotalAmount());
						// 付款金额
						entity.setPayAmount(contract.getDealedAmount());
						entity.setPayAmountB(contract.getDealedAmount());
						// 入库数量
						entity.setDeliveryInNumber(contract.getWarehouseNumber());
						entity.setDeliveryInNumberB(contract.getWarehouseNumber());
						// 供方企业
						entity.setBuyCompanyName(contract.getCompanyName());
						// 供方业务员
						entity.setBuyMatchUserName(contract.getMatchUserName());
						// 单价
						BigDecimal totalNumber = contract.getTotalNumber();
						BigDecimal totalAmount = contract.getTotalAmount();
						BigDecimal divide = totalAmount.divide(totalNumber, 4, BigDecimal.ROUND_HALF_UP);
						entity.setBuyUnitPrice(divide);
					}
				}
				// 决算字段
				BudgetSettlementVo budgetSettlementVo = budgetSettlementClient.findBySellContractIdWithAnyStatus(sellContractId);
				if (budgetSettlementVo != null) {
					entity.setFineOfSalesman(budgetSettlementVo.getFineOfSalesman());
					logger.info("copyBefore entity:{}", JsonUtil.obj2Json(entity));
					BeanUtils.copyProperties(budgetSettlementVo, entity,"id");
					// 补充字段：公司毛利、利润率 部分历史数据确实这两个字段
					BigDecimal multiply = budgetSettlementVo.getMarginAmount()
							.multiply(budgetSettlementVo.getCompanyCommissionRate());
					entity.setCompanyCommissionAmount(multiply.setScale(2, BigDecimal.ROUND_HALF_UP));
					entity.setGrossProfitRate(budgetSettlementVo.getMarginAmount().divide(entity.getSellContractTotalAmount(), 4, BigDecimal.ROUND_HALF_UP).setScale(2,BigDecimal.ROUND_HALF_UP));
					logger.info("copyAfter entity:{}", JsonUtil.obj2Json(entity));
				}
			}
		}

		String canEditFile = request.getParameter("hasEditFile");
		boolean canEdit = BooleanUtils.toBoolean(canEditFile);
		model.addAttribute("hasEditFile", canEdit);

		model.addAttribute("processCode", processCode);
		model.addAttribute("entity", entity);

		//处理审批中部分控件可编辑
		Long approveId=entity.getApproveId();
		model.addAttribute("contractApproveId", approveId);
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		String contractApproveId = request.getParameter("contractApproveId");
		if (StringUtils.isNotEmpty(contractApproveId)){
			model.addAttribute("contractApproveId", Long.parseLong(request.getParameter("contractApproveId")));
		}
		model.addAttribute("psv", permissionVo);

		return "apply/applyManualSettlement";
	}



	@ModelAttribute("preload")
	public ApplyManualSettlement getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				ApplyManualSettlement entity = new ApplyManualSettlement();
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
			applyManualSettlementClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}


}
