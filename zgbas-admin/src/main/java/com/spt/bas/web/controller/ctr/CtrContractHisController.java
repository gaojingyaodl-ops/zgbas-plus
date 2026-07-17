package com.spt.bas.web.controller.ctr;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.IApplyCtrDcsxClinent;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.ICtrContractOphisClient;
import com.spt.bas.client.remote.IPmApproveClient;
import com.spt.bas.client.vo.BusinessDeliveryExcelVo;
import com.spt.bas.client.vo.CtrContractOphisVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.XssExcelExp;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.vo.PmApproveVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.jpa.vo.IdEntity;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/ctr/contractOphis")
public class CtrContractHisController extends PageController<CtrContractOphisVo, BaseVo> {
	@Autowired
	private ICtrContractOphisClient ctrContractOphisClient;

	@Autowired
	private ICtrContractClient ctrContractClient;

	@Autowired
	private IPmApproveClient approveClient;

	@Autowired
	private IApplyCtrDcsxClinent applyCtrDcsxClinent;

	// 合同详情-操作记录
	@RequestMapping(value = "findOphis/{id}", method = RequestMethod.GET)
	public String findOphis(@PathVariable("id") Long id, Model model) {
		if (id != null && id > 01) {
			model.addAttribute("ophisId", id);
			CtrContract contractEntity = ctrContractClient.getEntity(id);
			if (null != contractEntity.getApproveId()) {
				PmApprove approveEntity = approveClient.getEntity(contractEntity.getApproveId());
				model.addAttribute("approveEntity", approveEntity);
			}
			model.addAttribute("contractStatusJson",
					JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
			model.addAttribute("approveStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		}
		return "ctr/contract_ophis";
	}

	@RequestMapping(value = "detailHisPage/{id}", method = RequestMethod.POST)
	public void detailHisPage(@PathVariable("id") Long id, PageSearchVo queryVo, HttpServletResponse response,
			HttpServletRequest request) {
		if (id != null && id > 0L) {
			Map<String, Object> searchParams = new HashMap<>();
			searchParams.put("EQL_ctrContractId", id);
			initSearch2(queryVo, request, searchParams);
			queryVo.setRows(100);
			PageDown<CtrContractOphisVo> findPage = ctrContractOphisClient.findPage(queryVo);
			List<CtrContractOphisVo> contents = findPage.getContent();
			for (CtrContractOphisVo CtrContractOphisVo : contents) {
				// 审批
				if (null != CtrContractOphisVo.getApproveId()) {
					StringBuilder remake = new StringBuilder();
					PmApproveVo approveVo = approveClient.getApproveVo(CtrContractOphisVo.getApproveId());
					if (approveVo != null) {
						remake.append(CtrContractOphisVo.getRemark() + " ")
								.append(CtrContractOphisVo.getCreateUserName()).append("→")
								.append(approveVo.getApprove().getLastApproveUserName());
						CtrContractOphisVo.setRemark(remake.toString());
						CtrContractOphisVo.setApproveStatus(approveVo.getApprove().getStatus());
						CtrContractOphisVo.setProcessName(approveVo.getApprove().getProcessName());
					}
				}
				if (CtrContractOphisVo.getRemark().indexOf(":") != -1) {
					String[] splitBuyAndSell = CtrContractOphisVo.getRemark().split(":");
					if (splitBuyAndSell.length >= 1) {
						String[] splitBuy = splitBuyAndSell[1].split(",");
						// 采购合同
						if (splitBuy.length >= 0) {
							// 查找合同id
							CtrContract ctr = new CtrContract();
							ctr.setContractNo(splitBuy[0].trim());
							CtrContract findByContractNo = ctrContractClient.findByContractNo(ctr);
							if (findByContractNo != null) {
								CtrContractOphisVo.setCtrBuyContractId(findByContractNo.getId());
								CtrContractOphisVo.setBuyContractNo(splitBuy[0]);
							}
						}
					}
					if (splitBuyAndSell.length >= 2) {
						// 销售合同
						CtrContract ctr = new CtrContract();
						ctr.setContractNo(splitBuyAndSell[2].trim());
						CtrContract findByContractNo = ctrContractClient.findByContractNo(ctr);
						if (findByContractNo != null) {
							CtrContractOphisVo.setCtrSellContractId(findByContractNo.getId());
							CtrContractOphisVo.setSellContractNo(splitBuyAndSell[2]);
						}
					}
				}
			}

			JsonEasyUI.renderJson(response, findPage);
		}
	}

	@RequestMapping(value = "detailHisPageAll", method = RequestMethod.POST)
	public void detailHisPageAll(PageSearchVo queryVo, HttpServletResponse response, HttpServletRequest request) {
		String ids = request.getParameter("ids");
		String s;
		if (!StringUtils.isEmpty(ids)) {
			String[] arr = ids.split(",");
			s=arr[arr.length-1];
			List<String> list=new ArrayList<>(Arrays.asList(arr));
			list.remove(list.size()-1);
			List<Long> contractIds = list.stream().filter(StringUtils::isNoneBlank).map(Long::parseLong).collect(Collectors.toList());
			Map<String, Object> searchParams = new HashMap<>();
			if(StringUtils.equals("true",s)){
				Long aLong = contractIds.get(0);
				CtrContract entity = ctrContractClient.getEntity(aLong);
				Long approveId = entity.getApproveId();
				List<ApplyCtrDCSX> byDCSXApproveIdAll = applyCtrDcsxClinent.findByDCSXApproveIdAll(approveId);
				List<Long> collect = byDCSXApproveIdAll.stream().map(IdEntity::getId).collect(Collectors.toList());
				searchParams.put("INL_ctrContractId", collect);
				if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, entity.getBusinessType())) {
					searchParams.put("EQS_contractGroup", "DCTP");
				} else {
					searchParams.put("EQS_contractGroup", "DCSX");
				}
			}else{
				searchParams.put("INL_ctrContractId", contractIds);
				searchParams.put("EQS_contractGroup", "CTR");
			}
			queryVo.setOrder("asc");
			queryVo.setSort("createdDate");
			queryVo.setRows(100);
			queryVo.setPage(1);
			initSearch2(queryVo, request, searchParams);
			PageDown<CtrContractOphisVo> findPage = ctrContractOphisClient.findPage(queryVo);
			List<CtrContractOphisVo> contents = findPage.getContent();
			Iterator<CtrContractOphisVo> it = contents.iterator();
			while (it.hasNext()){
				CtrContractOphisVo next = it.next();
				if (next.getRemark().startsWith("[关联合同号]") || next.getRemark().startsWith("[代采采购]")
						|| next.getRemark().startsWith("[代采销售]")) {
					it.remove();
					continue;
				}
				Long approveId = next.getApproveId();
				if (approveId != null) {
					PmApprove entity = approveClient.getEntity(approveId);
					if (Objects.nonNull(entity) && Boolean.FALSE.equals(entity.getEnableFlg())){
						it.remove();
						continue;
					}
					next.setApproveStatus(entity.getStatus());
					next.setApproveNo(entity.getApproveNo());
					// 获取审批名 从remark截取第一串【】的内容
					String applyTypeName = next.getRemark().substring(1, next.getRemark().indexOf("]"));
					applyTypeName = StringUtils.isBlank(applyTypeName) ? "" : applyTypeName;
					boolean cancelFlg = next.getRemark().contains("作废");
					next.setApproveTypeName(Boolean.TRUE.equals(cancelFlg) ? applyTypeName + "作废" : applyTypeName);
				}
			}
			JsonEasyUI.renderJson(response, findPage);
		}
	}

	@RequestMapping(value = "detailHisPageAllDCSX", method = RequestMethod.POST)
	public void detailHisPageAllDCSX(PageSearchVo queryVo, HttpServletResponse response,
								 HttpServletRequest request) {
		String ids = request.getParameter("ids");

		if (!StringUtils.isEmpty(ids)) {
			String[] arr = ids.split(",");
			List<Long> contractIds = Arrays.stream(arr).map(a -> Long.parseLong(a)).collect(Collectors.toList());
			Map<String, Object> searchParams = new HashMap<>();
			searchParams.put("INL_ctrContractId", contractIds);
			searchParams.put("EQS_contractGroup", "DCSX");
			queryVo.setOrder("asc");
			queryVo.setSort("createdDate");
			queryVo.setRows(50);
			initSearch2(queryVo, request, searchParams);
			PageDown<CtrContractOphisVo> findPage = ctrContractOphisClient.findPage(queryVo);
			List<CtrContractOphisVo> contents = findPage.getContent();
			Iterator<CtrContractOphisVo> it = contents.iterator();
			while (it.hasNext()){
				CtrContractOphisVo next = it.next();
				if (!next.getRemark().startsWith("[代采赊销收票]") && !next.getRemark().startsWith("[代采赊销付款]")) {
					it.remove();
					continue;
				}
				Long approveId = next.getApproveId();
				if (approveId != null) {
					PmApprove entity = approveClient.getEntity(approveId);
					next.setApproveStatus(entity.getStatus());
					next.setApproveNo(entity.getApproveNo());
					// 获取审批名 从remark截取第一串【】的内容
					next.setApproveTypeName(next.getRemark().substring(1, next.getRemark().indexOf("]")));
				}
			}
			JsonEasyUI.renderJson(response, findPage);
		}
	}


	/**
	 * 导出Excel业务提货审批单
	 * @param response
	 * @param request
	 * @throws ApplicationException
	 * @throws ParseException
	 * @throws IOException
	 */
	@RequestMapping("exportBusinessDelivery")
	public void exportBusinessDelivery(HttpServletResponse response, HttpServletRequest request) throws IOException {
		Map<String, String> paramMap = new HashMap<>();
		Long approveId = new Long( request.getParameter("approveId"));
		BusinessDeliveryExcelVo businessDelivery = ctrContractOphisClient.getBusinessDelivery(approveId);
		if (Objects.nonNull(businessDelivery)) {
			paramMap.put("productNames", businessDelivery.getProductNames());
			paramMap.put("deliveryName", businessDelivery.getDeliveryName());
			paramMap.put("matchUserName", businessDelivery.getMatchUserName());
			paramMap.put("deliveryAddress", "");
			XssExcelExp.excelReceiptDelivery("/excel/receiptDelivery.xlsx", paramMap,businessDelivery.getExcelDetailList(), response,  "业务提货审批单.xlsx");
		}
	}


	@Override
	public BaseClient<CtrContractOphisVo> getService() {
		return ctrContractOphisClient;
	}

	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
}
