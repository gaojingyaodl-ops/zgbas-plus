
package com.spt.bas.server.service.impl;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Splitter;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.dto.CtrContractDto;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.client.vo.protocol.DzdAgreement;
import com.spt.bas.server.cache.BsCompanyOurUtil;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.TemplateContentUtility;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.dao.PmApproveHistoryDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveHistory;
import com.spt.pm.entity.PmProcess;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.constants.CommonErrorId;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class CtrContractServiceImpl extends BaseService<CtrContract> implements ICtrContractService {
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private CtrProductDao ctrProductDao;
	@Autowired
	private ICtrProductService productService;
	@Autowired
	private CtrContractTextDao ctrContractTextDao;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private IStockDetailPresellService stockDetailPresellService;
	@Autowired
	private CtrContractApplyDao ctrContractApplyDao;
	@Autowired
	private ApplyReceiveDao applyReceiveDao;
	@Autowired
	private ApplyDeliveryOutDao deliveryOutDao;
	@Autowired
	private ApplyInvoiceDao invoiceDao;
	@Autowired
	private ApplyPayDao applyPayDao;
	@Autowired
	private ApplyDeliveryInDao deliveryInDao;
	@Autowired
	private ApplyInvoiceReceivedDao invoiceReceivedDao;
	@Autowired
	private ICtrContractApplyService ctrcontractApplyService;
	@Autowired
	private ApplyDiscussDao applyDiscussDao;
	@Autowired
	private ApplyLossDao applyLossDao;
	@Autowired
	private ApplyCompanyTitleDao applyCompanyTitleDao;
	@Autowired
	private PmApproveDao pmApproveDao;
	@Autowired
	private PmApproveHistoryDao pmApproveHistoryDao;
	@Autowired
	private CtrServiceContractDao serviceContractDao;
	@Autowired
	private ApplyMatchDetailDao applyMatchDetailDao;
	@Autowired
	private ApplyProductDetailDao applyProductDetailDao;
	@Autowired
	private IBsCompanyService companyService;
	@Resource
	private IApplyConfrimReceiptService applyConfrimReceiptService;
	@Resource
	private IBsCompanyDcsxService bsCompanyDcsxService;
	@Resource
	private PmProcessDao pmProcessDao;

	@Override
	public BaseDao<CtrContract> getBaseDao() {
		return ctrContractDao;
	}

	@Override
	public Class<CtrContract> getEntityClazz() {
		return CtrContract.class;
	}

	@Value("${file.show.url}")
	private String fileShowUrl;
	@Value("${file.download.url}")
	private String fileDownLoadUrl;

	/**
	 * 合计条件查询
	 */
	@Override
	public CtrContract sumPageContract(ContractSearchVo queryVo) {
		Long deptLeaderId = queryVo.getDeptLeaderId();
		String searchType = queryVo.getSearchType();// A：查询本中心所有hetong  P:查看本中心所有预售合同
		String payCondition = queryVo.getPayCondition(); // 收付款条件
		String warehouseCondition = queryVo.getWarehouseCondition();// 出入库条件
		String billCondition = queryVo.getBillCondition(); // 收开票条件
		String contractTypes = queryVo.getContractTypes();
		Boolean saasContractFlg = queryVo.getSaasContractFlg();
		String contractSource = queryVo.getContractSource();
		Boolean payDateChange = queryVo.getPayDateChange();
		List<Long> hgMatchUserIdList = queryVo.getHgMatchUserIdList();
		Specification<CtrContract> spe = WebUtil.buildSpecification(queryVo.getSearchParams());
		Specification<CtrContract> spec = dealFindCondition(contractSource, saasContractFlg, payDateChange, payCondition, warehouseCondition,
				billCondition, contractTypes, spe,queryVo.getBusinessType(), queryVo.getProductTypeCondition(), queryVo.getHgMatchUserIdList());
		spec = dealWithConfirmReceiptCondition(queryVo, spec);
		Specification<CtrContract> spec_userId = WebUtil.buildSpecification("EQL_matchUserId_OR_EQL_cooperationMatchUserId", queryVo.getUserId());
		if (CollectionUtils.isNotEmpty(hgMatchUserIdList) && hgMatchUserIdList.contains(queryVo.getUserId())){
			spec_userId = spec_userId.or(WebUtil.buildSpecification("INL_matchUserId", hgMatchUserIdList));
		}
		// Specification<CtrContract> spec_viewPreSell = WebUtil.buildSpecification("EQS_source", BasConstants.APPLY_TYPE_L);

		List<Long> myDeptId = authOpenFacade.findMyDeptId(queryVo.getUserId());
		List<Long> allDeptId = authOpenFacade.findMyDeptId(deptLeaderId);
		if (!queryVo.isAdmin()) {
			if (allDeptId.size() == 0 || myDeptId.size() == 0) {
				//spec = Specification.where(spec).and(spec_userId);
				myDeptId.add(0L);
				allDeptId.add(0L);
			}
			if (StringUtils.equals("A", searchType)) {
				//1.查看本中心所有合同
				Specification<CtrContract> spec_department = WebUtil.buildSpecification("INL_deptId", allDeptId);
				Specification<CtrContract> spec_department_userId = Specification.where(spec_userId).or(spec_department);
				spec = Specification.where(spec).and(spec_department_userId);
			}else if (StringUtils.equals("P", searchType)) {
				//2.查看本中心所有预售合同 + 所属部门的所有合同
				Specification<CtrContract> newSpec;
				Specification<CtrContract> spec_department_mydept= WebUtil.buildSpecification("INL_deptId", myDeptId);
				Specification<CtrContract> spec_department_allDept = WebUtil.buildSpecification("INL_deptId", allDeptId);
				Specification<CtrContract> spec_department_userId = Specification.where(spec_userId).or(spec_department_mydept);
				Specification<CtrContract> spec_department_viewPreSell = Specification.where(spec_department_allDept);
				newSpec = Specification.where(spec_department_userId).or(spec_department_viewPreSell);
				spec = Specification.where(spec).and(newSpec);
			}else {
				//3.查看所属部门的所有合同
				Specification<CtrContract> spec_department = WebUtil.buildSpecification("INL_deptId", myDeptId);
				Specification<CtrContract> spec_department_userId = Specification.where(spec_userId).or(spec_department);
				spec = Specification.where(spec).and(spec_department_userId);
			}
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<?> query = cb.createQuery();
		Root<CtrContract> root = query.from(CtrContract.class);
		CriteriaQuery<?> cq = query.where(spec.toPredicate(root, query, cb)).multiselect(
				cb.sum(root.get("totalNumber")), cb.sum(root.get("warehouseNumber")), cb.sum(root.get("totalAmount")),
				cb.sum(root.get("dealedAmount")), cb.sum(root.get("billedAmount")), cb.sum(root.get("bondAmount")),
				cb.sum(root.get("refundAmount")), cb.sum(root.get("totalAmount")), cb.sum(root.get("warehouseAmount")),
				cb.sum(root.get("transportAmount")), cb.sum(root.get("stevedorage")),  cb.sum(root.get("breachAmount")),
				cb.sum(root.get("receiveBreachAmount")), cb.sum(cb.prod(root.get("insuranceRate"), root.get("totalAmount"))));
		TypedQuery<?> tq = em.createQuery(cq);
		Object[] result = ((Object[]) tq.getSingleResult());
		CtrContract sum = new CtrContract();
		BigDecimal totalNumber = (BigDecimal) result[0];
		BigDecimal warehouseNumber = (BigDecimal) result[1];
		BigDecimal totalAmount = (BigDecimal) result[2];
		BigDecimal dealedAmount = (BigDecimal) result[3];
		BigDecimal billedAmount = (BigDecimal) result[4];
		BigDecimal bondAmount = (BigDecimal) result[5];
		BigDecimal warehouseAmount = (BigDecimal) result[8];
		BigDecimal transportAmount = (BigDecimal) result[9];
		BigDecimal stevedorage = (BigDecimal) result[10];
		BigDecimal breachAmount = (BigDecimal) result[11];
		BigDecimal receiveBreachAmount = (BigDecimal) result[12];
		BigDecimal insuranceAmount = (BigDecimal) result[13];
		sum.setTotalNumber(totalNumber);
		sum.setWarehouseNumber(warehouseNumber);
		sum.setTotalAmount(totalAmount);
		sum.setBilledAmount(billedAmount);
		sum.setBondAmount(bondAmount);
		sum.setDealedAmount(dealedAmount);
		sum.setWarehouseAmount(warehouseAmount);
		sum.setTransportAmount(transportAmount);
		sum.setStevedorage(stevedorage);
		sum.setBreachAmount(breachAmount);
		sum.setReceiveBreachAmount(receiveBreachAmount);
		sum.setInterestAmount(insuranceAmount);
		sum.setLossAmount(getReceivableBalanceTotal(spec));
		sum.setReceiveServiceAmount(sum.getTotalAmount().subtract(sum.getDealedAmount()));
		return sum;
	}

	private BigDecimal getReceivableBalanceTotal(Specification<CtrContract> spec){
		Specification<CtrContract> spec_contractType = WebUtil.buildSpecification("EQS_contractType", "S");
		Specification<CtrContract> new_spec = spec.and(spec_contractType);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<?> query = cb.createQuery();
		Root<CtrContract> root = query.from(CtrContract.class);
		CriteriaQuery<?> cq = query.where(new_spec.toPredicate(root, query, cb))
				.multiselect(cb.sum(root.get("totalAmount")), cb.sum(root.get("dealedAmount")), cb.sum(root.get("breachAmount")), cb.sum(root.get("receiveBreachAmount")));
		TypedQuery<?> tq = em.createQuery(cq);
		Object[] result = ((Object[]) tq.getSingleResult());
		BigDecimal totalAmount = getDefaultBigDecimal((BigDecimal) result[0]);
		BigDecimal dealedAmount = getDefaultBigDecimal((BigDecimal) result[1]);
		BigDecimal breachAmount = getDefaultBigDecimal((BigDecimal) result[2]);
		BigDecimal receiveBreachAmount = getDefaultBigDecimal((BigDecimal) result[3]);
		return totalAmount.subtract(dealedAmount).add(breachAmount).subtract(receiveBreachAmount);
	}

	@Override
	public List<CtrContract> findApproveByOrder(Long approveId) {
		return ctrContractDao.findApproveByOrder(approveId);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Page<ContractShowVo> findPageContract(ContractSearchVo queryVo) {
		// 注意！！！，本方法如有修改，必须同时修改下首页相关项的取数逻辑
		// logger.info("findPageContract queryVo:{}", JsonUtil.obj2Json(queryVo));
		Sort sort = Sort.by(Direction.DESC, "id");
		String businessType = queryVo.getBusinessType();
		if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) || StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,businessType)) {
			List<Sort.Order> sorts = new ArrayList<>();
			sorts.add(new Sort.Order(Direction.DESC, "pairCode"));
			sorts.add(new Sort.Order(Direction.ASC, "id"));
			sort = Sort.by(sorts);
		}
		Long deptLeaderId = queryVo.getDeptLeaderId();
		List<Long> hgMatchUserIdList = queryVo.getHgMatchUserIdList();
		PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows(), sort);//分页
		Specification<CtrContract> spec_userId = WebUtil.buildSpecification("EQL_matchUserId_OR_EQL_cooperationMatchUserId", queryVo.getUserId());
		if (CollectionUtils.isNotEmpty(hgMatchUserIdList) && hgMatchUserIdList.contains(queryVo.getUserId())){
			spec_userId = spec_userId.or(WebUtil.buildSpecification("INL_matchUserId", hgMatchUserIdList));
		}

		String searchType = queryVo.getSearchType();// A：查询本中心所有hetong  P:查看本中心所有预售合同
		String payCondition = queryVo.getPayCondition(); // 收付款条件
		String warehouseCondition = queryVo.getWarehouseCondition();// 出入库条件
		String billCondition = queryVo.getBillCondition(); // 收开票条件
		String contractTypes = queryVo.getContractTypes();
		Boolean saasContractFlg = queryVo.getSaasContractFlg();
		String contractSource = queryVo.getContractSource();
		Boolean payDateChange = queryVo.getPayDateChange();
		Specification<CtrContract> spe = WebUtil.buildSpecification(queryVo.getSearchParams());
		Specification<CtrContract> spec = dealFindCondition(contractSource, saasContractFlg, payDateChange, payCondition, warehouseCondition,
				billCondition, contractTypes, spe,businessType, queryVo.getProductTypeCondition(), queryVo.getHgMatchUserIdList());
		spec = dealWithConfirmReceiptCondition(queryVo, spec);
		if (!queryVo.isAdmin()) {
			List<Long> myDeptId = authOpenFacade.findMyDeptId(queryVo.getUserId());
			// logger.info("myDeptId:{}", myDeptId);
			List<Long> allDeptId = authOpenFacade.findMyDeptId(deptLeaderId);
			// logger.info("allDeptId:{}", allDeptId);
			if (allDeptId.size() == 0 || myDeptId.size() == 0) {
				myDeptId.add(0L);
				allDeptId.add(0L);
			}
			if (StringUtils.equals("A", searchType)) {
				DeptSearchVo sysDeptSearchVo = new DeptSearchVo();
				sysDeptSearchVo.setUserId(queryVo.getUserId());
				sysDeptSearchVo.setDeptType(PmConstants.NODE_TYPE_CENTER);
				SysDeptSdk dept = authOpenFacade.findDept(sysDeptSearchVo);
				// logger.info("wsDept:{}", JsonUtil.obj2Json(dept));
				if(Objects.nonNull(dept)) {
					if ("ws".equals(dept.getDeptAbbr())) {
						DeptSearchVo sysDeptSearchVo1 = new DeptSearchVo();
						sysDeptSearchVo1.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
						List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(sysDeptSearchVo1);
						allDeptId = deptAll.stream().filter(a -> !StringUtils.isEmpty(a.getDeptAbbr())&& a.getDeptAbbr().contains(dept.getDeptAbbr())).map(SysDeptSdk::getDeptId).collect(Collectors.toList());
						// logger.info("allDeptId1{}", JsonUtil.obj2Json(allDeptId));
					}
				}
				//可以查看本中心所有合同
				Specification<CtrContract> spec_department = WebUtil.buildSpecification("INL_deptId", allDeptId);
				Specification<CtrContract> spec_department_userId = Specification.where(spec_userId).or(spec_department);
				spec = Specification.where(spec).and(spec_department_userId);
			}else if (StringUtils.equals("P", searchType)) {
				//可以查看本中心所有预售合同
				Specification<CtrContract> newSpec;
				Specification<CtrContract> spec_department_mydept= WebUtil.buildSpecification("INL_deptId", myDeptId);
				Specification<CtrContract> spec_department_allDept = WebUtil.buildSpecification("INL_deptId", allDeptId);
				Specification<CtrContract> spec_department_userId = Specification.where(spec_userId).or(spec_department_mydept);
				Specification<CtrContract> spec_department_viewPreSell = Specification.where(spec_department_allDept);
				newSpec = Specification.where(spec_department_userId).or(spec_department_viewPreSell);
				spec = Specification.where(spec).and(newSpec);
			}else if(StringUtils.equals("D",searchType)){
				// 业务助理查询本业务部所有合同
				//可以查看所属于自己的合同
				DeptSearchVo sysDeptSearchVo = new DeptSearchVo();
				sysDeptSearchVo.setUserId(queryVo.getUserId());
				Specification<CtrContract> spec_department = WebUtil.buildSpecification("INL_deptId", queryVo.getDeptIdList());
				Specification<CtrContract> spec_business = Specification.where(spec_department);
				Specification<CtrContract> spec_department_userId = Specification.where(spec_userId).or(spec_business);
				spec = Specification.where(spec).and(spec_department_userId);
			} else if(StringUtils.equals("B",searchType)){
				DeptSearchVo sysDeptSearchVo = new DeptSearchVo();
				sysDeptSearchVo.setUserId(queryVo.getUserId());
				SysDeptSdk dept = authOpenFacade.findDept(sysDeptSearchVo);
				Specification<CtrContract> Bl = WebUtil.buildSpecification("EQS_businessTypeDcsx", BasConstants.BUSINESS_TYPE_BL);
				Specification<CtrContract> DcsxBl = WebUtil.buildSpecification("EQS_businessTypeDcsx", BasConstants.BUSINESS_TYPE_DCSXBL);
				//查看所有的保理合同
				Specification<CtrContract> spec_department = WebUtil.buildSpecification("INL_deptId", dept.getDeptId());
				Specification<CtrContract> spec_department_userId = Specification.where(spec_userId).or(spec_department).or(Bl).or(DcsxBl);
				spec = Specification.where(spec).and(spec_department_userId);
			} else if (!queryVo.getFunderFlg()){
				//可以查看所属于自己的合同
				Specification<CtrContract> spec_department = WebUtil.buildSpecification("INL_deptId", myDeptId);
				Specification<CtrContract> spec_department_userId = Specification.where(spec_userId).or(spec_department);
				spec = Specification.where(spec).and(spec_department_userId);
			}
		}
		Page<CtrContract> page = getBaseDao().findAll(spec, pageRequest);

		List<BsCompany> companyList = companyService.findAll();
		// 使用Stream将List转换为Map
		Map<Long, BsCompany> companyMap = companyList.stream()
				.collect(Collectors.toMap(BsCompany::getId, company -> company));

		List<Long> lstIds = new ArrayList<>();
		List<ContractShowVo> voList = new ArrayList<>();
		for (CtrContract ctr : page.getContent()) {
			Long contractId = ctr.getId();
			ContractShowVo vo = new ContractShowVo();
			BeanUtils.copyProperties(ctr, vo);
			BsCompany bsCompany = companyMap.get(ctr.getCompanyId());
			if(Objects.nonNull(bsCompany)) {
				Boolean accessReportFlg = bsCompany.getAccessReportFlg();
				if(Boolean.TRUE.equals(accessReportFlg)) {
					vo.setAccessReportFlg("是");
				} else {
					vo.setAccessReportFlg("否");
				}
				Boolean actualGuaranteeFlg = bsCompany.getActualGuaranteeFlg();
				if(Boolean.TRUE.equals(actualGuaranteeFlg)) {
					vo.setLiabilityFlg("是");
				} else {
					vo.setLiabilityFlg("否");
				}
			}
			// 计算履约状态
			parseViolate(vo);

			// 确定预售还能否再次发起采购
			if (ctr.getSource() != null && ctr.getSource().equals(BasConstants.APPLY_TYPE_L)) {
				List<CtrProduct> list = productService.findByContractId(ctr.getId());
				for (CtrProduct ctrProduct : list) {
					StockDetailPresell presell = stockDetailPresellService.findByCtrProductId(ctrProduct.getId());
					if (presell != null) {
						BigDecimal presellNumber = presell.getPresellNumber();
						BigDecimal buyNumber = presell.getBuyedNumber().add(presell.getApproveBuyNumber());
						if (presellNumber.compareTo(buyNumber) > 0) {
							vo.setCanStartBuy(true);
							break;
						}
					}
				}
			}
			lstIds.add(contractId);
			vo.setContractDifTime(getDayDiffer(vo.getContractTime(), vo.getContractEndTime()));

			if(StringUtils.equals(BasConstants.CONTRACTTYPE_SELL,ctr.getContractType())){
				BigDecimal warehouseNumber = ctr.getWarehouseNumber(); // 出库数量
				BigDecimal totalNumber = ctr.getTotalNumber();// 总数量
				BigDecimal dealedAmount = getDefaultBigDecimal(ctr.getDealedAmount());
				BigDecimal totalAmount = ctr.getTotalAmount();
				BigDecimal breachAmount = ctr.getBreachAmount();
				BigDecimal receiveBreachAmount = ctr.getReceiveBreachAmount();
				if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,ctr.getBusinessType())) {

					BigDecimal tpInterest = ctr.getTpInterest();
					BigDecimal approveTpInterest = ctr.getApproveTpInterest();

					BigDecimal receiveBalance = totalAmount.add(tpInterest).subtract(approveTpInterest).subtract(dealedAmount);
					vo.setReceivableBalance(receiveBalance);
					vo.setReceivablePrincipal(receiveBalance);

					BigDecimal settlementTotalAmount = ctr.getSettlementTotalAmount();
					if (settlementTotalAmount != null && settlementTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
						totalAmount = settlementTotalAmount;
					}
					BigDecimal dealPrice = totalAmount.divide(totalNumber,4, BigDecimal.ROUND_HALF_UP);
					BigDecimal receiveTotalAmount = dealedAmount;
					if (receiveTotalAmount.compareTo(BigDecimal.ZERO) == 0) {
						vo.setUsedDeliveryAmount(BigDecimal.ZERO);
						vo.setRemainingDeliveryAmount(BigDecimal.ZERO);
					} else {
						if (warehouseNumber.compareTo(BigDecimal.ZERO) == 0) {
							vo.setUsedDeliveryAmount(BigDecimal.ZERO);
							vo.setRemainingDeliveryAmount(receiveTotalAmount);
						} else {
							BigDecimal usedDeliveryAmount = warehouseNumber.multiply(dealPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
							vo.setUsedDeliveryAmount(usedDeliveryAmount);
							vo.setRemainingDeliveryAmount(receiveTotalAmount.subtract(usedDeliveryAmount).setScale(2, BigDecimal.ROUND_HALF_UP));
						}
					}

				} else {
					BigDecimal receiveBalance = totalAmount.subtract(dealedAmount).add(breachAmount).subtract(receiveBreachAmount);
					vo.setReceivableBalance(receiveBalance);
					vo.setReceivablePrincipal(totalAmount.subtract(dealedAmount));
				}
				if(warehouseNumber != null && totalNumber != null){
					// 非则一出库数量小于总数量显示新增送货单
					if(totalNumber.compareTo(warehouseNumber) > 0){
						vo.setAllWarehouse(true);
					}
				}
			}
			voList.add(vo);
		}
		//查询最后操作时间
		parseLastTime(voList, queryVo);

		if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) || StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, businessType)||StringUtils.equals(BasConstants.BUSINESS_TYPE_KC_CG, businessType)) {
			voList = makePairCode(voList);
		}

		Map<String, BsCompanyDcsx> companyConfigMap = bsCompanyDcsxService.getCompanyConfigMap();
		for (ContractShowVo vo : voList) {
			// 逾期金额 ：在定金时间点未收到定金的显示定金金额
			if (vo.getPayBondTime() != null && new Date().after(vo.getPayBondTime())) {
				vo.setOrverdurAmount(vo.getBondAmount());
			}
			// 逾期金额 ：在收全款时间点未收到全款的显示余款
			if (vo.getPayFullTime() != null && new Date().after(vo.getPayFullTime())) {
				vo.setOrverdurAmount(vo.getTotalAmount().subtract(vo.findRealDealedAmount()));
			}
			Boolean existContractTextFileId = false;
			String contractFileUrl = "";
			if (StringUtils.isNotBlank(vo.getBuyContentFileId())){
				existContractTextFileId = true;
				List<String> idList = Splitter.on(",").omitEmptyStrings().splitToList(vo.getBuyContentFileId());
				if (CollectionUtils.isNotEmpty(idList)){
					contractFileUrl = fileShowUrl + "/view/show/" + idList.get(idList.size()-1);
				}
			}else if(StringUtils.isNotBlank(vo.getSellContentFileId())){
				existContractTextFileId = true;
				List<String> idList = Splitter.on(",").omitEmptyStrings().splitToList(vo.getSellContentFileId());
				if (CollectionUtils.isNotEmpty(idList)){
					contractFileUrl = fileShowUrl + "/view/show/" + idList.get(idList.size() - 1);
				}
			}
			vo.setExistContractTextFileId(existContractTextFileId);
			vo.setContractFileUrl(contractFileUrl);
			vo.setInsuranceAmount(vo.parseInsuranceAmount());
			String protocolFileUrl = "";
			if (StringUtils.isNotBlank(vo.getProtocolFileId())) {
				List<String> idList = Splitter.on(",").omitEmptyStrings().splitToList(vo.getProtocolFileId());
				protocolFileUrl = fileShowUrl + "/view/show/" + idList.get(idList.size() - 1);
			}
			vo.setProtocolFileUrl(protocolFileUrl);
			if (Objects.nonNull(companyConfigMap) && Objects.nonNull(companyConfigMap.get(vo.getCompanyName()))) {
				vo.setVirtualBillFlag(companyConfigMap.get(vo.getCompanyName()).getOurCompanyFlag());
			}

			String shippingFileUrl = "";
			if (StringUtils.isNotBlank(vo.getShippingFileId())){
				List<String> idList = Splitter.on(",").omitEmptyStrings().splitToList(vo.getShippingFileId());
				if (CollectionUtils.isNotEmpty(idList)){
					shippingFileUrl = fileDownLoadUrl + "/view/download/" + idList.get(idList.size()-1);
				}
			}
			vo.setShippingFileUrl(shippingFileUrl);
		}

		// sort属性无法反序列化，下面代码重新组装page对象，去掉sort属性
		PageRequest pageRequest_new = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
		Page<ContractShowVo> pageVo = new PageImpl<>(voList, pageRequest_new, page.getTotalElements());
		return pageVo;
	}

	private BigDecimal getDefaultBigDecimal(BigDecimal value){
		return (Objects.isNull(value) || value.compareTo(BigDecimal.ZERO) < 0) ? BigDecimal.ZERO : value;
	}

	/**
	 * 通过计算给履约状态添加应的值
	 * @param
	 * @return
	 */
	private void parseViolate(ContractShowVo vo) {
		// 取到付全款日期
		Date payFullTime = vo.getPayFullTime();

		// 取到标识
		Boolean violateTreatyFlg = vo.getViolateTreatyFlg();
		if (payFullTime != null) {

			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(payFullTime);
			long time1 = cal.getTimeInMillis();
			cal.setTime(date);
			long time2 = cal.getTimeInMillis();
			long between_days = (time2 - time1) / (1000 * 3600 * 24);
			if (between_days <= 0) {
				vo.setViolateFlg("进行中");
			} else if (between_days <= 10 && between_days > 0) {
				vo.setViolateFlg("宽限期");
			} else if (between_days <= 15 && between_days > 10) {
				vo.setViolateFlg("催告期");
			} else {
				vo.setViolateFlg("逾期");
			}
			if ( violateTreatyFlg != null){
				if (violateTreatyFlg) {
					vo.setViolateFlg("违约");
				}
			}
		}
	}

	@Override
	public CtrContract findByStockDetailId(Long stockDetailId) {
		return ctrContractDao.findContractByStockDetailId(stockDetailId);
	}

	@Override
	public CtrContract findContractByLinkContractId(Long linkContractId) {
		return ctrContractDao.findContractByLinkContractId(linkContractId);
	}

	private Specification<CtrContract> dealWithConfirmReceiptCondition(ContractSearchVo queryVo, Specification<CtrContract> spec){
		Date confirmFileDateBegin = queryVo.getConfirmFileDateBegin();
		Date confirmFileDateEnd = queryVo.getConfirmFileDateEnd();
		if (Objects.isNull(confirmFileDateBegin) && Objects.isNull(confirmFileDateEnd)){
			return spec;
		}
		List<Long> contractIdList = applyConfrimReceiptService.findContractIdByDate(confirmFileDateBegin, confirmFileDateEnd);
		Specification<CtrContract> confirmSpec = WebUtil.buildSpecification("INL_id", contractIdList);
		spec = Specification.where(spec).and(confirmSpec);
		return spec;
	}
	private Specification<CtrContract> dealFindCondition(String contractSource, Boolean saasContractFlg, Boolean payDateChange, String payCondition,
														 String warehouseCondition, String billCondition, String contractTypes,
														 Specification<CtrContract> spec,String businessType, String productTypeCondition,
														 List<Long> matchUserIdList) {
//		if (BasConstants.PRODUCT_TYPE_HG.equals(productTypeCondition)) {
//			// 创建 Specification 进行条件筛选
//			Specification<CtrContract> specWarehouseNumberN = new Specification<CtrContract>() {
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//					// Join 关联 t_ctr_product 表
//					Join<CtrContract, CtrProduct> productsJoin = root.join("ctrProductList", JoinType.INNER);
//					// 设定 product_cd 不以 "HG" 开头
//					return cb.like(productsJoin.get("productCd"), "HG%");
//				}
//			};
//
//			// 将当前 spec 与新创建的 specWarehouseNumberN 组合
//			spec = Specification.where(spec).and(specWarehouseNumberN);
//		}
//		if (BasConstants.PRODUCT_TYPE_NHG.equals(productTypeCondition)) {
//			// 创建 Specification 进行条件筛选
//			Specification<CtrContract> specWarehouseNumberN = new Specification<CtrContract>() {
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//					// Join 关联 t_ctr_product 表
//					Join<CtrContract, CtrProduct> productsJoin = root.join("ctrProductList", JoinType.INNER);
//					// 设定 product_cd 不以 "HG" 开头
//					return cb.notLike(productsJoin.get("productCd"), "HG%");
//				}
//			};
//
//			// 将当前 spec 与新创建的 specWarehouseNumberN 组合
//			spec = Specification.where(spec).and(specWarehouseNumberN);
//		}

		if (BasConstants.PRODUCT_TYPE_HG.equals(productTypeCondition)) {
			// 如果 matchUserIdList 不为空，则创建一个包含 matchUserId 的 IN 查询条件
			Specification<CtrContract> specInMatchUserId = null;
			if (matchUserIdList != null && !matchUserIdList.isEmpty()) {
				specInMatchUserId = new Specification<CtrContract>() {
					private static final long serialVersionUID = 1L;
					@Override
					public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
						return root.get("matchUserId").in(matchUserIdList);
					}
				};
				spec = Specification.where(spec).and(specInMatchUserId);
			}
		}
		if (BasConstants.PRODUCT_TYPE_NHG.equals(productTypeCondition)) {
			// 将当前 spec 与新创建的 specWarehouseNumberN 组合
			Specification<CtrContract> specNotInMatchUserId = null;
			if (matchUserIdList != null && !matchUserIdList.isEmpty()) {
				specNotInMatchUserId = new Specification<CtrContract>() {
					private static final long serialVersionUID = 1L;
					@Override
					public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
						return cb.not(root.get("matchUserId").in(matchUserIdList));
					}
				};
				spec = Specification.where(spec).and(specNotInMatchUserId);
			}

		}

		// 未出入库：WN 已出入库：WY
		if (BasConstants.APPLY_TYPE_WN.equals(warehouseCondition)) {
			Specification<CtrContract> specWarehouseNumberN = new Specification<CtrContract>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.gt(root.get("totalNumber"), root.get("warehouseNumber"));
				}
			};
			spec = Specification.where(spec).and(specWarehouseNumberN);
		}
		if (BasConstants.APPLY_TYPE_WY.equals(warehouseCondition)) {
			Specification<CtrContract> specWarehouseNumberY = new Specification<CtrContract>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.equal(root.get("totalNumber"), root.get("warehouseNumber"));
				}
			};
			spec = Specification.where(spec).and(specWarehouseNumberY);
		}

		// 未收付款：PN 已收付款：PY
		if (BasConstants.APPLY_TYPE_PN.equals(payCondition)) {
			Specification<CtrContract> specPayN = new Specification<CtrContract>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"),root.get("interestAmount"));
					if(StringUtils.equals(BasConstants.BUSINESS_TYPE_KC_CG,businessType)){
						Expression<BigDecimal> finalAmount = cb.sum(root.get("finalTotalAmount"),root.get("interestAmount"));
						Predicate finalTotalAmountIsNull = cb.isNull(root.get("finalTotalAmount"));
						Predicate finalAmountGreaterThanDealedAmount = cb.gt(finalAmount, root.get("dealedAmount"));
						Predicate sumAmountGreaterThanDealedAmount = cb.gt(sumAmount, root.get("dealedAmount"));
						return cb.or(
								cb.and(finalTotalAmountIsNull, sumAmountGreaterThanDealedAmount),
								cb.and(cb.not(finalTotalAmountIsNull), finalAmountGreaterThanDealedAmount)
						);
					}
					return cb.gt(sumAmount, root.get("dealedAmount"));
				}
			};
			spec = Specification.where(spec).and(specPayN);
		}
		if (BasConstants.APPLY_TYPE_PY.equals(payCondition)) {
			Specification<CtrContract> specPayY = new Specification<CtrContract>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"),root.get("interestAmount"));
					if(StringUtils.equals(BasConstants.BUSINESS_TYPE_KC_CG,businessType)){
						Expression<BigDecimal> finalAmount = cb.sum(root.get("finalTotalAmount"),root.get("interestAmount"));
						Predicate finalTotalAmountIsNull = cb.isNull(root.get("finalTotalAmount"));
						Predicate finalAmountGreaterThanDealedAmount = cb.equal(finalAmount, root.get("dealedAmount"));
						Predicate sumAmountGreaterThanDealedAmount = cb.equal(sumAmount, root.get("dealedAmount"));
						return cb.or(
								cb.and(finalTotalAmountIsNull, sumAmountGreaterThanDealedAmount),
								cb.and(cb.not(finalTotalAmountIsNull), finalAmountGreaterThanDealedAmount)
						);
					}
					return cb.equal(sumAmount, root.get("dealedAmount"));
				}
			};
			spec = Specification.where(spec).and(specPayY);
		}

		// 未收开票：BN 已收开票：BY
		if (BasConstants.APPLY_TYPE_BN.equals(billCondition)) {
			Specification<CtrContract> specBillN = new Specification<CtrContract>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"),root.get("interestAmount"));
					if(StringUtils.equals(BasConstants.BUSINESS_TYPE_KC_CG,businessType)){
						Expression<BigDecimal> finalAmount = cb.sum(root.get("finalTotalAmount"),root.get("interestAmount"));
						Predicate finalTotalAmountIsNull = cb.isNull(root.get("finalTotalAmount"));
						Predicate finalAmountGreaterThanDealedAmount = cb.gt(finalAmount, root.get("billedAmount"));
						Predicate sumAmountGreaterThanDealedAmount = cb.gt(sumAmount, root.get("billedAmount"));
						return cb.or(
								cb.and(finalTotalAmountIsNull, sumAmountGreaterThanDealedAmount),
								cb.and(cb.not(finalTotalAmountIsNull), finalAmountGreaterThanDealedAmount)
						);
					}
					return cb.gt(sumAmount, root.get("billedAmount"));
				}
			};
			spec = Specification.where(spec).and(specBillN);
		}
		if (BasConstants.APPLY_TYPE_BY.equals(billCondition)) {
			Specification<CtrContract> specBillY = new Specification<CtrContract>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"),root.get("interestAmount"));
					if(StringUtils.equals(BasConstants.BUSINESS_TYPE_KC_CG,businessType)){
						Expression<BigDecimal> finalAmount = cb.sum(root.get("finalTotalAmount"),root.get("interestAmount"));
						Predicate finalTotalAmountIsNull = cb.isNull(root.get("finalTotalAmount"));
						Predicate finalAmountGreaterThanDealedAmount = cb.equal(finalAmount, root.get("billedAmount"));
						Predicate sumAmountGreaterThanDealedAmount = cb.equal(sumAmount, root.get("billedAmount"));
						return cb.or(
								cb.and(finalTotalAmountIsNull, sumAmountGreaterThanDealedAmount),
								cb.and(cb.not(finalTotalAmountIsNull), finalAmountGreaterThanDealedAmount)
						);
					}
					return cb.equal(sumAmount, root.get("billedAmount"));
				}
			};
			spec = Specification.where(spec).and(specBillY);
		}

		// 判断有效无效
		if (StringUtils.isNotBlank(contractTypes)) {
			Specification<CtrContract> specActives = null;
			if (BasConstants.DICT_TYPE_Y.equals(contractTypes)) {
				specActives = WebUtil.buildSpecification("NEQS_contractStatus", BasConstants.CONTRACTSTATUS_C);
			} else if (BasConstants.DICT_TYPE_N.equals(contractTypes)) {
				specActives = WebUtil.buildSpecification("EQS_contractStatus", BasConstants.CONTRACTSTATUS_C);
			}
			if (specActives != null) {
				spec = Specification.where(spec).and(specActives);
			}
		}

		//是否为查看saas合同
		if (Boolean.TRUE.equals(saasContractFlg) && StringUtils.isNotBlank(contractSource)) {
			Specification<CtrContract> specViewSaasContract = WebUtil.buildSpecification("EQS_source", contractSource);
			spec = Specification.where(spec).and(specViewSaasContract);
		}

		// 是否存在回款日期变动
		if (Boolean.TRUE.equals(payDateChange)){
			Specification<CtrContract> specPayDateChange = new Specification<CtrContract>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.notEqual(cb.function("DATE_FORMAT", String.class, root.get("payFullTime"), cb.literal("%Y%m%d")), cb.function("DATE_FORMAT", String.class, root.get("appointPayFullTime"), cb.literal("%Y%m%d")));
				}
			};
			spec = Specification.where(spec).and(specPayDateChange);
		}
		return spec;
	}

	@Override
	public List<CtrContract> findByLinkContractIdLink(String linkContractId) {
		if (StringUtils.isBlank(linkContractId)) {
			return null;
		}
		if (!StringUtils.startsWith(linkContractId, ",")) {
			linkContractId = "," + linkContractId;
		}
		if (!StringUtils.endsWith(linkContractId, ",")) {
			linkContractId = linkContractId + ",";
		}
		return ctrContractDao.findByLinkContractIdLink(linkContractId);
	}

	@Override
	public List<CtrContract> findByIdIn(Long[] arr) {
		return ctrContractDao.findByIdIn(arr);
	}

	@Override
	public List<CtrContract> findByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}
		return ctrContractDao.findByIds(ids);
	}

	@Override
	public Page<CtrContractChooseVo> findPageChoose(ContractSearchVo queryVo) {
		Sort sort = Sort.by(Direction.DESC, "id");
		boolean cancelFlg = queryVo.getCancelFlg();
		PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows(), sort);
		Specification<CtrContract> spe = WebUtil.buildSpecification(queryVo.getSearchParams());
		if (!cancelFlg) {
			Specification<CtrContract> spec_contractStatus = WebUtil.buildSpecification("NEQS_contractStatus",
					BasConstants.CONTRACTSTATUS_B);
			spe = Specification.where(spe).and(spec_contractStatus);
		}
		String type = queryVo.getType();// 'I':收/开票 'R':收/付款 'W':出/入库
		Specification<CtrContract> spec_apply = new Specification<CtrContract>() {
			private static final long serialVersionUID = -4378449331438560686L;

			@Override
			public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Subquery<CtrContractApply> sq = query.subquery(CtrContractApply.class);
				Root<CtrContract> sqc = sq.correlate(root);
				Join<CtrContract, CtrContractApply> sqo = sqc.join("ctrContractApply");
				// Predicate predicate = cb.gt(root.get("totalAmount"),
				// sqo.get("applyPayAmount"));
				if (StringUtils.isNotBlank(type)) {
					Predicate predicate = null;
					if (BasConstants.APPLY_STATUS_I.equals(type)) {
						// 收/开票
						Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"),root.get("interestAmount"));
						predicate = cb.gt(sumAmount, sqo.get("applyBillAmount"));
					} else if (BasConstants.APPLY_STATUS_R.equals(type)) {
						// 收/付款
						Expression<BigDecimal> sumAmount = cb.sum(root.get("totalAmount"),root.get("interestAmount"));
						predicate = cb.gt(sumAmount, sqo.get("applyPayAmount"));
					} else if (BasConstants.STATUS_DI.equals(type)) {
						//入库
						predicate = cb.gt(root.get("totalNumber"), sqo.get("applyWarehouseNumber"));
					} else if(BasConstants.STATUS_DO.equals(type)) {
						//出库
						//Predicate amount_predicate = cb.gt(root.get("dealedAmount"), 0);
						predicate = cb.gt(root.get("totalNumber"), sqo.get("applyWarehouseNumber"));
						//predicate = cb.and(predicate,amount_predicate);
					} else if (BasConstants.APPLY_STATUS_B.equals(type)) {
						//退款
						//1收款金额大于0
						predicate = cb.greaterThan(root.get("dealedAmount"), 0);
						predicate = cb.and(predicate);
						//2可退款金额大于0
						Predicate greaterThan = cb.greaterThan(root.get("dealedAmount"), sqo.get("applyRefundAmount"));
						predicate = cb.and(predicate,greaterThan);
						//predicate = cb.and(greaterThan);
					}
					sq.select(sqo).where(predicate);
				}
				// sq.select(sqo).where(predicate);
				return cb.exists(sq);
			}
		};
		spe = Specification.where(spe).and(spec_apply);
		Page<CtrContract> page = getBaseDao().findAll(spe, pageRequest);
		List<CtrContractChooseVo> list = new ArrayList<>();
		for (CtrContract ctr : page.getContent()) {
			CtrContractChooseVo chooseVo = new CtrContractChooseVo();
			BeanUtils.copyProperties(ctr, chooseVo);
			CtrContractApply apply = ctrContractApplyDao.findByCtrContractId(ctr.getId());
			chooseVo.setApplyBillAmount(apply.getApplyBillAmount());
			chooseVo.setApplyPayAmount(apply.getApplyPayAmount());
			chooseVo.setApplyWarehouseNumber(apply.getApplyWarehouseNumber());
			chooseVo.setApplyRefundAmount(apply.getApplyRefundAmount());
			chooseVo.setApplyServiceAmount(apply.getApplyServiceAmount());
			list.add(chooseVo);
		}
		// sort属性无法反序列化，下面代码重新组装page对象，去掉sort属性
		PageRequest pageRequest_new = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
		Page<CtrContractChooseVo> pageVo = new PageImpl<>(list, pageRequest_new, page.getTotalElements());
		return pageVo;
	}

	@Override
	public List<Long> findIdByLinkContractId(String id) {
		return ctrContractDao.findIdByLinkContractId(id);
	}


	@Override
	public CtrContractChooseVo findByContractId(Long contractId) {
		CtrContractChooseVo vo = new CtrContractChooseVo();
		if (contractId != null) {
			CtrContract entity = this.getEntity(contractId);
			BeanUtils.copyProperties(entity, vo);
			CtrContractApply apply = ctrContractApplyDao.findByCtrContractId(contractId);
			if (apply != null) {
				vo.setApplyBillAmount(apply.getApplyBillAmount());
				vo.setApplyPayAmount(apply.getApplyPayAmount());
				vo.setApplyWarehouseNumber(apply.getApplyWarehouseNumber());
				vo.setApplyRefundAmount(apply.getApplyRefundAmount());
				vo.setApplyServiceAmount(apply.getApplyServiceAmount());
			}
			if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, entity.getContractType())) {
				//查询对应采购合同对方企业ID
				String linkContractId = entity.getLinkContractId();
				if (StringUtils.isNotBlank(linkContractId)) {
					List<String> sellIdList = Splitter.on(",").omitEmptyStrings().splitToList(linkContractId);
					List<Long> buyContractList = sellIdList.stream().map(a->Long.valueOf(a)).collect(Collectors.toList());
					if (buyContractList != null && buyContractList.size() > 0) {
						Long buyContractId = buyContractList.get(0);
						CtrContract buyContract = this.getEntity(buyContractId);
						vo.setBuyCompanyId(buyContract.getCompanyId());
					}
				}
			}
		}
		return vo;
	}

	@Override
	public CtrServiceContractChooseVo findByServiceContractId(Long serviceContractId) {
		CtrServiceContractChooseVo vo = new CtrServiceContractChooseVo();
		if (serviceContractId != null) {
			CtrServiceContract one = serviceContractDao.findOne(serviceContractId);
			BeanUtils.copyProperties(one, vo);
			CtrContractApply apply = ctrContractApplyDao.findByCtrContractId(one.getCtrContractId());
			if (apply != null) {
				vo.setApplyServiceAmount(apply.getApplyServiceAmount());
				vo.setApplyBillAmount(apply.getApplyServiceBillAmount());
			}
		}
		return vo;
	}


	private void parseLastTime(List<ContractShowVo> showVoList, ContractSearchVo queryVo) {
		// 概略查询不需要耗时查询最近操作日期
		if (Boolean.TRUE.equals(queryVo.getOutLineFlg())){
			return;
		}
		List<Long> contractIdList = showVoList.stream().map(ContractShowVo::getId).collect(Collectors.toList());
		List<CtrLastDateVo> receiveList = applyReceiveDao.findLastPay(contractIdList);
		List<CtrLastDateVo> deliveryOutList = deliveryOutDao.findLastDelivery(contractIdList);
		List<CtrLastDateVo> invoiceList = invoiceDao.findLastBill(contractIdList);
		List<CtrLastDateVo> payList = applyPayDao.findLastPay(contractIdList);
		List<CtrLastDateVo> deliveryInList = deliveryInDao.findLastDelivery(contractIdList);
		List<CtrLastDateVo> invoideReceiveList = invoiceReceivedDao.findLastBill(contractIdList);
		List<CtrContractText> contractTextList = ctrContractTextDao.findByCtrContractIds(contractIdList);
		for (ContractShowVo showVo : showVoList) {
			Long contractId = showVo.getId();
			if (BasConstants.CONTRACT_TYPE_S.equals(showVo.getContractType())) {
				showVo.setLastPayDate(receiveList.stream()
						.filter(c -> Objects.equals(contractId, c.getContractId()))
						.map(CtrLastDateVo::getLastDate)
						.filter(Objects::nonNull)
						.findAny().orElse(null));
				showVo.setLastDeliveryDate(deliveryOutList.stream()
						.filter(c -> Objects.equals(contractId, c.getContractId()))
						.map(CtrLastDateVo::getLastDate)
						.filter(Objects::nonNull)
						.findAny().orElse(null));
				showVo.setLastBillDate(invoiceList.stream()
						.filter(c -> Objects.equals(contractId, c.getContractId()))
						.map(CtrLastDateVo::getLastDate)
						.filter(Objects::nonNull)
						.findAny().orElse(null));
			} else {
				showVo.setLastPayDate(payList.stream()
						.filter(c -> Objects.equals(contractId, c.getContractId()))
						.map(CtrLastDateVo::getLastDate)
						.filter(Objects::nonNull)
						.findAny().orElse(null));
				showVo.setLastDeliveryDate(deliveryInList.stream()
						.filter(c -> Objects.equals(contractId, c.getContractId()))
						.map(CtrLastDateVo::getLastDate)
						.filter(Objects::nonNull)
						.findAny().orElse(null));
				showVo.setLastBillDate(invoideReceiveList.stream()
						.filter(c -> Objects.equals(contractId, c.getContractId()))
						.map(CtrLastDateVo::getLastDate)
						.filter(Objects::nonNull)
						.findAny().orElse(null));
			}
			showVo.setExistContractText(CollectionUtils.isNotEmpty(contractTextList) && (contractTextList.stream()
					.filter(Objects::nonNull)
					.filter(t -> Objects.equals(contractId, t.getCtrContractId()))
					.findAny().isPresent()));
		}
	}

	@Override
	public CtrContract findByContractNo(String contractNo) {
		return ctrContractDao.findByContractNo(contractNo);
	}

	public int getDayDiffer(Date startDate, Date endDate) {
		if(startDate == null) {
			return 0;
		}
		if(endDate == null) {
			endDate = new Date();
		}
		long days = DateOperator.getDays(DateOperator.truncDate(startDate), DateOperator.truncDate(endDate));
		return (int)days;
	}

	@Override
	public CtrContractDetailVo findDetailByContractId(Long contractId) {
		CtrContractDetailVo detailVo = new CtrContractDetailVo();
		CtrContract entity = this.getEntity(contractId);
		if (entity != null) {
			CtrContractApply contractApply = ctrcontractApplyService.findByContractId(contractId);
			List<CtrProduct> productList = ctrProductDao.findByCtrContractId(contractId);
			CtrProduct ctrProduct = productList.get(0);
			if (contractApply != null) {
				detailVo.setRealPayTime(contractApply.getRealPayDate());
			}
			detailVo.setWarehouseName(ctrProduct.getWarehouseName());
			detailVo.setWarehouseAddrs(entity.getDeliveryAddr());
			detailVo.setDoubleCheckFileId(entity.getDoubleCheckFileId());
			BigDecimal interestAmount = entity.getInterestAmount();
			if (interestAmount == null) {
				interestAmount = BigDecimal.ZERO;
			}
			detailVo.setInterestAmount(interestAmount);
			BigDecimal receiveInterestAmount = entity.getReceiveInterestAmount();
			if (receiveInterestAmount == null){
				receiveInterestAmount = BigDecimal.ZERO;
			}
			detailVo.setTransportAmount(entity.getTransportAmount());
			detailVo.setWarehouseAmount(entity.getWarehouseAmount());
			detailVo.setDeliveryFee(entity.getDeliveryFee());
			detailVo.setReceiveInterestAmount(receiveInterestAmount);
			detailVo.setOurCompanyName(entity.getOurCompanyName());
			detailVo.setDeliveryMode(entity.getDeliveryMode());
			detailVo.setBusinessType(entity.getBusinessType());
			detailVo.setMatchCreditFlg(entity.getMatchCreditFlg());
		}
		return detailVo;
	}

	/** 收货证明 */
	@Override
	public ApproveFormPrintVo printApplyConfirm(Long contractId) {
		ApproveFormPrintVo vo = new ApproveFormPrintVo();
		CtrContract entity = this.getEntity(contractId);
		List<CtrProduct> proList = ctrProductDao.findByCtrContractId(contractId);
		vo.setGoodsTotalNum(entity.getTotalNumber());
		vo.setOurCompanyName(entity.getOurCompanyName());
		vo.setCompanyName(entity.getCompanyName());
		vo.setContractNo(entity.getContractNo());
		vo.setStrDate(DateOperator.formatDate(new Date(), "yyyy/MM/dd"));
		vo.setBuyDetailList(proList);
		BsTemplateConfig template = TemplateContentUtility.getTemplate("matchApplyPrint","FMC_APPLY_CONFIRM","CH",entity.getEnterpriseId());
		try {
			String content = contentMerge(template.getContent(),vo);
			vo.setContent(content);
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vo;
	}

	@SuppressWarnings("deprecation")
	private String contentMerge(String content,ApproveFormPrintVo entity) throws ApplicationException {
		Configuration  cfg = new Configuration();
		StringWriter sw = new StringWriter();
		try {
			Template t  = new Template("", new StringReader(content), cfg);
			t.process(entity, sw);
			content = sw.toString();
		}  catch (Exception e) {
			throw new ApplicationException(CommonErrorId.ERROR_DATA_EXCHANGE, "合并模板异常", e);
		}
		return content;
	}

	private List<ContractShowVo> makePairCode(List<ContractShowVo> content) {
		Long pairId = 1L;
		for (int i = 0; i < content.size(); i++) {
			ContractShowVo ctrContract = content.get(i);
			Long pair_id = ctrContract.getPairId();
			String pairCode = ctrContract.getPairCode();
			if (StringUtils.isNotBlank(pairCode) && pair_id == null) {
				content.get(i).setPairId(pairId);
				for (int j = i + 1; j < content.size(); j++) {
					if (j < content.size() && StringUtils.equals(pairCode, content.get(j).getPairCode())) {
						content.get(j).setPairId(pairId);
					}
				}
				pairId++;
			}
		}
		return content;
	}

	/**
	 * 根据企业Id获取存续累计未完结合同金额
	 * 采购累计未完结金额 = 累计未付全款金额
	 * 销售累计未完结金额 = 累计未收全票金额
	 */
	@Override
	public BigDecimal getTotalAmountByCompanyId(Long companyId,Long enterpriseId,String contractType) {
		BigDecimal totalAmount = null;
		if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contractType)) {
			totalAmount = ctrContractDao.findDealedAmount(companyId,enterpriseId,contractType);
		}else {
			totalAmount = ctrContractDao.findBilledAmount(companyId, enterpriseId, contractType);
		}
		return totalAmount == null ? BigDecimal.ZERO : totalAmount;
	}

	/**
	 * 根据企业Id判断累计未完结订单数是否超过2笔
	 * 采购未完结订单数 = 未付全款的订单数
	 * 销售未完结订单数 = 未收全票的订单数
	 */
	@Override
	public Boolean existOrder(Long companyId,Long enterpriseId,String contractType) {
		Boolean existOrderFlg = false;
		Integer orderNumber = 0;
		if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contractType)) {
			orderNumber = ctrContractDao.existDealedOrder(companyId, enterpriseId, contractType);
		}else {
			orderNumber = ctrContractDao.existBilledOrder(companyId, enterpriseId, contractType);
		}
		if (orderNumber != null && orderNumber > 2) {
			existOrderFlg = true;
		}
		return existOrderFlg;
	}

	@Override
	public List<PmApproveHistoryVo> getApproveHistory(Long buyContractId, Long sellContractId) {
		List<PmApproveHistoryVo> historyVoList = new ArrayList<>();
		List<ApplyDiscuss> discussList = applyDiscussDao.findByBuyContractId(buyContractId);
		List<ApplyLoss> lossList = applyLossDao.findBySellContractId(sellContractId);
		List<ApplyCompanyTitle> buyTitle = applyCompanyTitleDao.findByContractId(buyContractId);
		List<ApplyCompanyTitle> sellTitle = applyCompanyTitleDao.findByContractId(sellContractId);
		discussList.forEach(d->{
			Long approveId = d.getApproveId();
			PmApprove pmApprove = pmApproveDao.findOne(approveId);
			PmApproveHistoryVo historyVo = new PmApproveHistoryVo();
			historyVo.setApproveId(approveId);
			historyVo.setHistoryName(pmApprove.getProcessName());
			historyVo.setApproveDate(pmApprove.getLastApproveDate());
			List<PmApproveHistory> history = pmApproveHistoryDao.findByApproveId(approveId);
			PmApproveHistory pmApproveHistory = history.get(history.size()-1);
			historyVo.setApproveUserName(pmApproveHistory.getApproveUserName());
			historyVo.setApproveStepName(pmApproveHistory.getStepName());
			historyVo.setApproveNo(pmApprove.getApproveNo());
			historyVoList.add(historyVo);
		});
		lossList.forEach(d->{
			Long approveId = d.getApproveId();
			PmApprove pmApprove = pmApproveDao.findOne(approveId);
			PmApproveHistoryVo historyVo = new PmApproveHistoryVo();
			historyVo.setApproveId(approveId);
			historyVo.setHistoryName(pmApprove.getProcessName());
			historyVo.setApproveDate(pmApprove.getLastApproveDate());
			List<PmApproveHistory> history = pmApproveHistoryDao.findByApproveId(approveId);
			PmApproveHistory pmApproveHistory = history.get(history.size()-1);
			historyVo.setApproveUserName(pmApproveHistory.getApproveUserName());
			historyVo.setApproveStepName(pmApproveHistory.getStepName());
			historyVo.setApproveNo(pmApprove.getApproveNo());
			historyVoList.add(historyVo);
		});
		buyTitle.forEach(d->{
			Long approveId = d.getApproveId();
			PmApprove pmApprove = pmApproveDao.findOne(approveId);
			PmApproveHistoryVo historyVo = new PmApproveHistoryVo();
			historyVo.setApproveId(approveId);
			historyVo.setHistoryName(pmApprove.getProcessName());
			historyVo.setApproveDate(pmApprove.getLastApproveDate());
			List<PmApproveHistory> history = pmApproveHistoryDao.findByApproveId(approveId);
			PmApproveHistory pmApproveHistory = history.get(history.size()-1);
			historyVo.setApproveUserName(pmApproveHistory.getApproveUserName());
			historyVo.setApproveStepName(pmApproveHistory.getStepName());
			historyVo.setApproveNo(pmApprove.getApproveNo());
			historyVoList.add(historyVo);
		});
		sellTitle.forEach(d->{
			Long approveId = d.getApproveId();
			PmApprove pmApprove = pmApproveDao.findOne(approveId);
			PmApproveHistoryVo historyVo = new PmApproveHistoryVo();
			historyVo.setApproveId(approveId);
			historyVo.setHistoryName(pmApprove.getProcessName());
			historyVo.setApproveDate(pmApprove.getLastApproveDate());
			List<PmApproveHistory> history = pmApproveHistoryDao.findByApproveId(approveId);
			PmApproveHistory pmApproveHistory = history.get(history.size()-1);
			historyVo.setApproveUserName(pmApproveHistory.getApproveUserName());
			historyVo.setApproveStepName(pmApproveHistory.getStepName());
			historyVo.setApproveNo(pmApprove.getApproveNo());
			historyVoList.add(historyVo);
		});
		return historyVoList;
	}

	@Override
	public CtrContract findByContractNoV2(String contractNo) {
		return ctrContractDao.findByContractNo(contractNo);
	}

	@Override
	public CtrContract findBuyContractBySellContractId(Long sellContractId) {
		CtrContract one = ctrContractDao.findOne(sellContractId);
		return ctrContractDao.findByApproveIdAndContractType(one.getApproveId(), BasConstants.CONTRACT_TYPE_B);
	}

	@Override
	public CtrContract findSellContractByBuyContractId(Long buyContractId) {
		CtrContract one = ctrContractDao.findOne(buyContractId);
		return ctrContractDao.findByApproveIdAndContractType(one.getApproveId(), BasConstants.CONTRACT_TYPE_S);
	}

	@Override
	public List<CtrContract> findByApproveId(Long approveId) {
		return ctrContractDao.findContractIdByApproveId(approveId);
	}

	@Override
	public List<CtrContract> findByApproveIds(List<Long> approveIds) {
		return ctrContractDao.findContractIdByApproveIds(approveIds);
	}

	@Override
	public List<CtrContract> findByCompanyId(Long companyId) {
		return ctrContractDao.findByCompanyId(companyId);
	}

	@Override
	public List<CtrContract> findByAllSellContract() {
		return ctrContractDao.findByContractTypeDCSX(BasConstants.CONTRACT_TYPE_S);
	}

	@Override
	public List<CtrContract> findCtrContractBreach() {
		return ctrContractDao.findCtrContractBreach();
	}

	@Override
	public void updateContractData(String contractNo, BigDecimal actualContractAmount, BigDecimal deductibleAmount, BigDecimal originalContractAmount) {
		ctrContractDao.updateContractData(contractNo,actualContractAmount,deductibleAmount,originalContractAmount);
	}

	@Override
	public List<CtrContract> findByContractTypeDCSXBl() {
		return ctrContractDao.findByContractTypeDCSXBl(BasConstants.CONTRACT_TYPE_S);
	}

	@Override
	public void updateStatusByContractNo(String factorStatus, String contractNo) {
		ctrContractDao.updateStatusByContractNo(factorStatus,contractNo);
	}

	@Override
	public CtrContract findByCustomerOrderCode(String customerOrderCode) {
		return ctrContractDao.findByCustomerOrderCode(customerOrderCode);
	}

	/**
	 * 订单预警查询
	 * @param vo
	 * @return
	 */
	@Override
	public Page<CtrContract> findByOrderWarn(ContractOrderVo vo) {
		Sort sort = Sort.by(Direction.DESC, "id");
		Specification<CtrContract> spe = null;
		Specification<CtrContract> spec = null;
		List<String> statusList = new ArrayList<>();
		statusList.add(BasConstants.CONTRACTSTATUS_D);
		statusList.add(BasConstants.CONTRACTSTATUS_C);
		Specification<CtrContract> contractStatus = WebUtil.buildSpecification("NINS_contractStatus",statusList);
		Specification<CtrContract> enterpriseId = WebUtil.buildSpecification("EQL_enterpriseId",  BasConstants.ZG_ENTERPRISE_ID);
		Specification<CtrContract> likesContractNo = WebUtil.buildSpecification("EQS_contractNo",  vo.getContractNo());
		Specification<CtrContract> likesCompanyName = WebUtil.buildSpecification("LIKES_companyName", vo.getCompanyName());
		Specification<CtrContract> likesOurCompanyName = WebUtil.buildSpecification("EQS_ourCompanyName", vo.getOurCompanyName());
		Specification<CtrContract> likesPerformanceStatus = WebUtil.buildSpecification("EQS_performanceStatus", vo.getPerformanceStatus());
		// 业务类型
		Specification<CtrContract> typeDcsx = null;
		if (StringUtils.isNotBlank(vo.getBusinessType())){
			if (vo.getBusinessType().equals("PT")){
				// 查询为空或者为PT的都是普通赊销类型
				// Specification查寻列值为空
				typeDcsx = (root, criteriaQuery, cb) -> {
					List<Predicate> list = Lists.newArrayList();
					List<Predicate> or2 = Lists.newArrayList();
					or2.add(cb.isNull(root.get("businessTypeDcsx")));
					or2.add(cb.equal(root.get("businessTypeDcsx"),vo.getBusinessType()));
					list.add(cb.or(or2.toArray(new Predicate[or2.size()])));
					return cb.and(list.toArray(new Predicate[list.size()]));
				};
			}else{
				typeDcsx = WebUtil.buildSpecification("EQS_businessTypeDcsx", vo.getBusinessType());
			}
		}
		// 业务员
		Specification<CtrContract> matchUserId = null;
		if (vo.getMatchUserId() != null){
			matchUserId = WebUtil.buildSpecification("EQL_matchUserId", vo.getMatchUserId());
		}
		spec = Specification.where(spec).and(likesCompanyName).and(likesContractNo).and(likesOurCompanyName).and(likesPerformanceStatus).and(typeDcsx).and(matchUserId);


		Specification<CtrContract> contractType = WebUtil.buildSpecification("EQS_contractType","S");
		Specification<CtrContract> performanceStatus = null;
		if(StringUtils.isNotBlank(vo.getPerformanceStatus())){
			Specification<CtrContract> status = WebUtil.buildSpecification("EQS_performanceStatus",vo.getPerformanceStatus());
			performanceStatus = Specification.where(status);
		} else {
			Specification<CtrContract> status_B = WebUtil.buildSpecification("EQS_performanceStatus","B");
			Specification<CtrContract> status_D = WebUtil.buildSpecification("EQS_performanceStatus","D");
			Specification<CtrContract> status_S = WebUtil.buildSpecification("EQS_performanceStatus","S");
			Specification<CtrContract> status_P = WebUtil.buildSpecification("EQS_performanceStatus","P");
			performanceStatus = Specification.where(status_B).or(status_D).or(status_S).or(status_P);
		}

		Specification<CtrContract> matchCreditFlg = WebUtil.buildSpecification("EQB_matchCreditFlg",true);
		if (spec != null){
			spe = Specification.where(spe).and(spec);
		}
		if (!vo.isAdmin()) {
			Specification<CtrContract> spec_userId = WebUtil.buildSpecification("EQL_matchUserId", vo.getUserId());
			if (vo.getUserId().equals(vo.getDeptLeaderId())){
				DeptSearchVo sysDeptSearchVo = new DeptSearchVo();
				sysDeptSearchVo.setUserId(vo.getUserId());
				SysDeptSdk dept = authOpenFacade.findDept(sysDeptSearchVo);
				Specification<CtrContract> spec_department = WebUtil.buildSpecification("EQL_deptId", dept.getDeptId());
				Specification<CtrContract> spec_department_userId = Specification.where(spec_userId).or(spec_department);
				spe = Specification.where(spe).and(spec_department_userId);
			}else{
				spe = Specification.where(spe).and(spec_userId);
			}
		}
		spe = Specification.where(spe).and(performanceStatus).and(contractType).and(matchCreditFlg).and(contractStatus).and(enterpriseId);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows(),sort);
		Page<CtrContract> page = getBaseDao().findAll(spe, pageable);
		return page;
	}

	/**
	 * 诉讼管理查询
	 * @param vo
	 * @return
	 */
	@Override
	public Page<CtrContract> findByLitigation(ContractOrderVo vo) {
		Sort sort = Sort.by(Direction.DESC, "id");
		Specification<CtrContract> spe = null;
		Specification<CtrContract> spec = null;
		List<String> statusList = new ArrayList<>();
		statusList.add(BasConstants.CONTRACTSTATUS_D);
		statusList.add(BasConstants.CONTRACTSTATUS_C);
		Specification<CtrContract> contractStatus = WebUtil.buildSpecification("NINS_contractStatus",statusList);
		Specification<CtrContract> enterpriseId = WebUtil.buildSpecification("EQL_enterpriseId",  BasConstants.ZG_ENTERPRISE_ID);
		Specification<CtrContract> likesContractNo = WebUtil.buildSpecification("EQS_contractNo",  vo.getContractNo());
		Specification<CtrContract> likesCompanyName = WebUtil.buildSpecification("LIKES_companyName", vo.getCompanyName());
		Specification<CtrContract> likesOurCompanyName = WebUtil.buildSpecification("EQS_ourCompanyName", vo.getOurCompanyName());
		spec = Specification.where(spec).and(likesCompanyName).and(likesContractNo).and(likesOurCompanyName);

		String productType = vo.getProductType();
		List<Long> hgMatchUserIdList = vo.getHgMatchUserIdList();
		if (BasConstants.PRODUCT_TYPE_HG.equals(productType)) {
			// 如果 matchUserIdList 不为空，则创建一个包含 matchUserId 的 IN 查询条件
			Specification<CtrContract> specInMatchUserId = null;
			if (hgMatchUserIdList != null && !hgMatchUserIdList.isEmpty()) {
				specInMatchUserId = new Specification<CtrContract>() {
					private static final long serialVersionUID = 1L;
					@Override
					public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
						return root.get("matchUserId").in(hgMatchUserIdList);
					}
				};
				spec = Specification.where(spec).and(specInMatchUserId);
			}
		}
		if (BasConstants.PRODUCT_TYPE_NHG.equals(productType)) {
			// 将当前 spec 与新创建的 specWarehouseNumberN 组合
			Specification<CtrContract> specNotInMatchUserId = null;
			if (hgMatchUserIdList != null && !hgMatchUserIdList.isEmpty()) {
				specNotInMatchUserId = new Specification<CtrContract>() {
					private static final long serialVersionUID = 1L;
					@Override
					public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
						return cb.not(root.get("matchUserId").in(hgMatchUserIdList));
					}
				};
				spec = Specification.where(spec).and(specNotInMatchUserId);
			}

		}
		
		// 销售合同
		Specification<CtrContract> contractType = WebUtil.buildSpecification("EQS_contractType","S");
		// 逾期或者违约的合同
		Specification<CtrContract> performanceStatus = Specification.where(null);
		if(StringUtils.isNotEmpty(vo.getPerformanceStatus())){
			Specification<CtrContract> status = WebUtil.buildSpecification("EQS_performanceStatus",vo.getPerformanceStatus());
			performanceStatus=Specification.where(performanceStatus).and(status);
		} else {
			Specification<CtrContract> status_S = WebUtil.buildSpecification("EQS_performanceStatus","S");
			Specification<CtrContract> status_P = WebUtil.buildSpecification("EQS_performanceStatus","P");
			performanceStatus=Specification.where(performanceStatus).or(status_S).or(status_P);
		}
		// 不是代采合同
		Specification<CtrContract> matchCreditFlg = WebUtil.buildSpecification("EQB_matchCreditFlg",true);
		if (spec != null){
			spe = Specification.where(spe).and(spec);
		}
		if (!vo.isAdmin()) {
			Specification<CtrContract> spec_userId = WebUtil.buildSpecification("EQL_matchUserId", vo.getUserId());
			if (vo.getUserId().equals(vo.getDeptLeaderId())){
				DeptSearchVo sysDeptSearchVo = new DeptSearchVo();
				sysDeptSearchVo.setUserId(vo.getUserId());
				SysDeptSdk dept = authOpenFacade.findDept(sysDeptSearchVo);
				Specification<CtrContract> spec_department = WebUtil.buildSpecification("EQL_deptId", dept.getDeptId());
				Specification<CtrContract> spec_department_userId = Specification.where(spec_userId).or(spec_department);
				spe = Specification.where(spe).and(spec_department_userId);
			}else{
				spe = Specification.where(spe).and(spec_userId);
			}
     }
		spe = Specification.where(spe).and(performanceStatus).and(contractType).and(matchCreditFlg).and(contractStatus).and(enterpriseId);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows(),sort);
		Page<CtrContract> page = getBaseDao().findAll(spe, pageable);
		return page;
	}

	/**
	 * 查询指定供应商未发货的采购合同
	 * @param companyId
	 * @return
	 */
	@Override
	public List<CtrContract> findUnDelivery(Long companyId) {
		return ctrContractDao.findUnDelivery(companyId);
	}

	@Override
	public Page<CtrContract> findUnDeliveryPage(PageSearchVo searchVo) {
		Sort sort = Sort.by(Direction.DESC, "id");
		Map<String, Object> searchParams = searchVo.getSearchParams();
		searchParams.put("LTED_deliveryDateTo", DateOperator.addDays(DateOperator.truncDate(new Date()), -1));
		// searchParams.put("EQM_warehouseNumber", BigDecimal.ZERO);
		searchParams.put("NEQS_status", BasConstants.CONTRACTSTATUS_C);
		searchParams.put("EQS_contractType", BasConstants.CONTRACT_TYPE_B);
		searchParams.put("EQB_matchCreditFlg", true);
		Specification<CtrContract> spec = WebUtil.buildSpecification(searchParams);
		Specification<CtrContract> spec_warehouseNumber = new Specification<CtrContract>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.gt(root.get("totalNumber"), root.get("warehouseNumber"));
			}
		};
		spec = Specification.where(spec).and(spec_warehouseNumber);
		Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);
		return getBaseDao().findAll(spec, pageable);
	}

	@Override
	public Page<CtrContract> findByCompanyInterest(CtrContractDto ctrContractDto) {
		Map<String, Object> searchParams = new HashMap<>();
		searchParams.put("EQS_contractType", BasConstants.CONTRACT_TYPE_S);
		if (Objects.nonNull(ctrContractDto.getCompanyId())) {
			searchParams.put("EQL_companyId", ctrContractDto.getCompanyId());
		}
		if (CollectionUtils.isNotEmpty(ctrContractDto.getContractNoList())) {
			searchParams.put("NINS_contractNo", ctrContractDto.getContractNoList());
		}
		if (Objects.nonNull(ctrContractDto.getUserId())) {
			searchParams.put("EQL_matchUserId", ctrContractDto.getUserId());
		}
		Sort sort = Sort.by(Direction.DESC, "id");
		Specification<CtrContract> spc = WebUtil.buildSpecification(searchParams);
		Specification<CtrContract> spec_breach = new Specification<CtrContract>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<CtrContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.gt(root.get("breachAmount"), root.get("receiveBreachAmount"));
			}
		};
		Pageable pageable = PageRequest.of(ctrContractDto.getPage() - 1, ctrContractDto.getRows(), sort);
		spc = Specification.where(spc).and(spec_breach);
		return getBaseDao().findAll(spc, pageable);
	}

	/**
	 * 判断电子合同是否使用特殊银行账户
	 * 如果上游我方是奥顺宇，修改的是下游我方开户行账号；
	 * 如果下游我方是奥顺宇，修改的是上游我方开户行账号；
	 * @param contractId
	 * @return
	 */
	@Override
	public boolean judgeUseSpecialBankContractId(Long contractId) {
		CtrContract entity = ctrContractDao.findOne(contractId);
		if (Objects.isNull(entity)) {
			return false;
		}

		// 上下游合同  获取另外一个合同
		CtrContract otherContract = ctrContractDao.findOtherContract(entity.getApproveId(), contractId);
		if (Objects.isNull(otherContract)) {
			return false;
		}

		// 处于奥顺宇链条合同中 且 当前合同明细的我方抬头不是奥顺宇
		if (!StringUtils.equals(BasConstants.COMPANY_NAME_ASY, entity.getOurCompanyName()) && StringUtils.equals(BasConstants.COMPANY_NAME_ASY, otherContract.getOurCompanyName())) {
			return true;
		}
		return false;
	}

	/**
	 * 判断电子合同是否使用特殊银行账户
	 * 如果上游我方是奥顺宇，修改的是下游我方开户行账号；
	 * 如果下游我方是奥顺宇，修改的是上游我方开户行账号；
	 * @param applyMatchDetailId
	 * @return
	 */
	@Override
	public boolean judgeUseSpecialBankApplyMatchDetailId(Long applyMatchDetailId) {
		ApplyMatchDetail matchDetail = applyMatchDetailDao.findOne(applyMatchDetailId);
		if (Objects.isNull(matchDetail)) {
			return false;
		}

		// 上下游合同  获取另外一个审批明细
		ApplyMatchDetail otherMatchDetail = applyMatchDetailDao.findOtherMatchDetail(matchDetail.getApplyMatchId(), applyMatchDetailId);
		if (Objects.isNull(otherMatchDetail)) {
			return false;
		}

		// 处于奥顺宇链条合同中 且 当前合同明细的我方抬头不是奥顺宇
		if (!StringUtils.equals(BasConstants.COMPANY_NAME_ASY, matchDetail.getOurCompanyName()) && StringUtils.equals(BasConstants.COMPANY_NAME_ASY, otherMatchDetail.getOurCompanyName())) {
			return true;
		}
		return false;
	}

	/**
	 * 查询合同出入库明细
	 *
	 * @param contractIds
	 * @return
	 */
	@Override
	public Map<Long, ApplyDeliveryExportVo> getDeliveryExportVo(List<Long> contractIds) {
		Map<Long, ApplyDeliveryExportVo> exportVoMap = new HashMap<>();
		List<ApplyDeliveryIn> deliveryInList = deliveryInDao.findDeliveryInByContractIds(contractIds);
		List<ApplyDeliveryOut> deliveryOutList = deliveryOutDao.findDeliveryInByContractIds(contractIds);
		if (CollectionUtils.isNotEmpty(deliveryInList)) {
			ApplyDeliveryExportVo.ExportVo vo;
			for (ApplyDeliveryIn applyDeliveryIn : deliveryInList) {
				Long contractId = applyDeliveryIn.getContractId();
				Date warehouseInDate = applyDeliveryIn.getWarehouseInDate();
				BigDecimal dealNumber = getSumDeliveryNum(applyDeliveryIn.getId(), BasConstants.APPLY_TYPE_I);
				vo = new ApplyDeliveryExportVo.ExportVo();
				vo.setDeliveryDate(warehouseInDate);
				vo.setDealNumber(dealNumber);
				ApplyDeliveryExportVo exportVo = exportVoMap.get(contractId);
				if (Objects.isNull(exportVo)) {
					exportVo = new ApplyDeliveryExportVo();
					List<ApplyDeliveryExportVo.ExportVo> exportVoList = new ArrayList<>();
					exportVoList.add(vo);
					exportVo.setContractId(contractId);
					exportVo.setExportList(exportVoList);
				} else {
					List<ApplyDeliveryExportVo.ExportVo> exportList = exportVo.getExportList();
					exportList.add(vo);
					exportVo.setExportList(exportList);
				}
				exportVoMap.put(contractId, exportVo);
			}
		}
		if (CollectionUtils.isNotEmpty(deliveryOutList)) {
			ApplyDeliveryExportVo.ExportVo vo;
			for (ApplyDeliveryOut applyDeliveryOut : deliveryOutList) {
				Long contractId = applyDeliveryOut.getContractId();
				Date warehouseInDate = applyDeliveryOut.getWarehouseOutDate();
				BigDecimal dealNumber = getSumDeliveryNum(applyDeliveryOut.getId(), BasConstants.APPLY_TYPE_O);
				vo = new ApplyDeliveryExportVo.ExportVo();
				vo.setDeliveryDate(warehouseInDate);
				vo.setDealNumber(dealNumber);
				ApplyDeliveryExportVo exportVo = exportVoMap.get(contractId);
				if (Objects.isNull(exportVo)) {
					exportVo = new ApplyDeliveryExportVo();
					List<ApplyDeliveryExportVo.ExportVo> exportVoList = new ArrayList<>();
					exportVoList.add(vo);
					exportVo.setContractId(contractId);
					exportVo.setExportList(exportVoList);
				} else {
					List<ApplyDeliveryExportVo.ExportVo> exportList = exportVo.getExportList();
					exportList.add(vo);
					exportVo.setExportList(exportList);
				}
				exportVoMap.put(contractId, exportVo);
			}
		}
		return exportVoMap;
	}

	@Override
	public  List<CtrContract> autoPayAmount() {
		return ctrContractDao.autoPayAmount();
	}

	@Override
	public List<CtrContract> autoPayBondAmount() {
		return ctrContractDao.autoPayBondAmount();
	}

	@Override
	public List<CompanyOrderResVo> findCompanyOrder(String minute) {
		List<CompanyOrderResVo> resVoList = new ArrayList<>();
		try {
			LocalDateTime endDateTime = LocalDateTime.now();
			Integer minuteInt = 5;
			if(StringUtils.isNotBlank(minute)) {
				try {
					minuteInt = Integer.valueOf(minute);
				} catch (Exception e) {
					logger.error("string to int error",e);
				}
			}
			LocalDateTime startDateTime = endDateTime.minus(minuteInt, ChronoUnit.MINUTES);
			List<CtrContract> contractList = ctrContractDao.findCompanyOrder(Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime));
			if(CollectionUtils.isNotEmpty(contractList)) {
				for (CtrContract contract : contractList) {
					CompanyOrderResVo resVo = new CompanyOrderResVo();
					resVo.setContractNo(contract.getContractNo());
					resVo.setCompanyName(contract.getCompanyName());
					resVo.setTotalNumber(contract.getTotalNumber());
					resVo.setTotalAmount(contract.getTotalAmount());
					resVo.setContractTime(contract.getContractTime());
					resVo.setBusinessType(Boolean.TRUE.equals(contract.getMatchCreditFlg()) ? BasConstants.DICT_TYPE_BUSINESS_SX : BasConstants.DICT_TYPE_BUSINESS_DC);
					resVoList.add(resVo);
				}
			}
		} catch (Exception e) {
			logger.error("查询企业交易信息失败",e);
		}

		return resVoList;
	}

	@Override
	public List<CtrContract> findByContractNoLikes(String contractNo) {
		return ctrContractDao.findByContractNoLikes(contractNo);
	}

	@Override
	public Page<CtrContract> findCtrContractPage(Pageable page) {
		return ctrContractDao.findAll(page);
	}

	@Override
	public Integer selectAllCount() {
		return ctrContractDao.selectAllCount();
	}

	@Override
	public List<ApplyReceive> findDiscountContractList(CtrContract ctrContract) {
		List<ApplyReceive> resultList = new ArrayList<>();
		Long contractId = ctrContract.getId();
		String companyName = ctrContract.getCompanyName();
		String ourCompanyName = ctrContract.getOurCompanyName();
		String payMode = ctrContract.getPayMode();
		String payType = ctrContract.getPayType();
		List<CtrContract> contractList = null;
		if (StringUtils.equals(BasConstants.PAY_TYPE_T, payType)) {
			contractList = ctrContractDao.findDiscountContractList(ourCompanyName, companyName, contractId);
			boolean existDiscountFlg = contractList.stream().anyMatch(contract -> Objects.equals(contract.getId(), contractId));
			if (Boolean.FALSE.equals(existDiscountFlg)) {
				CtrContract targetContract = ctrContractDao.findOne(contractId);
				contractList.add(0, targetContract);
			}
		}
		if (StringUtils.equals(BasConstants.PAY_MODE_H, payMode)) {
			contractList = ctrContractDao.findDraftContractList(ourCompanyName, companyName, contractId);
		}
		if (CollectionUtils.isEmpty(contractList) && Objects.nonNull(contractId)) {
			contractList = ctrContractDao.findByIdIn(new Long [] {contractId});
		}
		if (CollectionUtils.isEmpty(contractList)) {
			return resultList;
		}
		List<Long> contractIdList = contractList.stream().map(CtrContract::getId).collect(Collectors.toList());
		List<CtrContractApply> contractApplyList = ctrContractApplyDao.findByCtrContractIdIn(contractIdList);
		Map<Long, CtrContractApply> contractApplyMap = contractApplyList.stream().collect(Collectors.toMap(CtrContractApply::getCtrContractId, c -> c, (a, b) -> b));
		contractList.forEach(contract->{
			ApplyReceive entity = new ApplyReceive();
			entity.setContractId(contract.getId());
			entity.setContractNo(contract.getContractNo());
			entity.setTotalAmount(contract.getTotalAmount());
			entity.setPayedAmount(contract.getDealedAmount());
			entity.setBreachAmount(contract.getBreachAmount());
			entity.setReceiveBreachAmount(contract.getReceiveBreachAmount());
			entity.setUnDiscountAmount(contract.getDiscountChargeAmount().subtract(contract.getDiscountReceiveAmount()));
			CtrContractApply apply = contractApplyMap.get(contract.getId());
			if (Objects.nonNull(apply)){
				entity.setUnpayedAmount(contract.getTotalAmount().subtract(apply.getApplyPayAmount()));
			}
			resultList.add(entity);
		});
		return resultList;
	}

	@Override
	public List<ApplyReceive> findTpDiscountContractList(CtrContract ctrContract) {
		List<ApplyReceive> resultList = new ArrayList<>();
		Long contractId = ctrContract.getId();
		String companyName = ctrContract.getCompanyName();
		String ourCompanyName = ctrContract.getOurCompanyName();
		String payMode = ctrContract.getPayMode();
		String payType = ctrContract.getPayType();
		List<CtrContract> contractList = null;
		if (StringUtils.equals(BasConstants.PAY_TYPE_T, payType)) {
			contractList = ctrContractDao.findTpDiscountContractList(ourCompanyName, companyName, contractId);
			boolean existDiscountFlg = contractList.stream().anyMatch(contract -> Objects.equals(contract.getId(), contractId));
			if (Boolean.FALSE.equals(existDiscountFlg)) {
				CtrContract targetContract = ctrContractDao.findOne(contractId);
				contractList.add(0, targetContract);
			}
		}
		if (StringUtils.equals(BasConstants.PAY_MODE_H, payMode)) {
			contractList = ctrContractDao.findTpDraftContractList(ourCompanyName, companyName, contractId);
		}
		if (CollectionUtils.isEmpty(contractList) && Objects.nonNull(contractId)) {
			contractList = ctrContractDao.findByIdIn(new Long [] {contractId});
		}
		if (CollectionUtils.isEmpty(contractList)) {
			return resultList;
		}
		List<Long> contractIdList = contractList.stream().map(CtrContract::getId).collect(Collectors.toList());
		List<CtrContractApply> contractApplyList = ctrContractApplyDao.findByCtrContractIdIn(contractIdList);
		Map<Long, CtrContractApply> contractApplyMap = contractApplyList.stream().collect(Collectors.toMap(CtrContractApply::getCtrContractId, c -> c, (a, b) -> b));
		contractList.forEach(contract->{
			ApplyReceive entity = new ApplyReceive();
			entity.setContractId(contract.getId());
			entity.setContractNo(contract.getContractNo());
			entity.setTotalAmount(contract.getTotalAmount());
			entity.setPayedAmount(contract.getDealedAmount());
			entity.setBreachAmount(contract.getBreachAmount());
			entity.setReceiveBreachAmount(contract.getReceiveBreachAmount());
			entity.setUnDiscountAmount(contract.getDiscountChargeAmount().subtract(contract.getDiscountReceiveAmount()));
			entity.setTpInterest(contract.getTpInterest());
			entity.setApproveTpInterest(contract.getApproveTpInterest());
			CtrContractApply apply = contractApplyMap.get(contract.getId());
			if (Objects.nonNull(apply)){
				entity.setUnpayedAmount(contract.getTotalAmount().add(contract.getTpInterest()).subtract(contract.getApproveTpInterest()).subtract(apply.getApplyPayAmount()));
			}
			resultList.add(entity);
		});
		return resultList;
	}

	/**
	 * 根据企业ID获取逾期合同
	 * @param companyId
	 * @return
	 */
	@Override
	public List<CtrContract> findOverdueContractListByCompanyId(Long companyId) {
		return ctrContractDao.findOverdueContractListByCompanyId(companyId);
	}

	/**
	 * 是否有超三天未发货订单
	 * @param companyId
	 * @return
	 */
	@Override
	public Boolean findUnDelivery3Day(Long companyId) {
		String unDeliveryContractNos = ctrContractDao.findUnDelivery3Day(companyId);
		if (StringUtils.isNotBlank(unDeliveryContractNos) && CollectionUtils.isNotEmpty(Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(unDeliveryContractNos))) {
			return true;
		}
		return false;
	}

	@Override
	public CtrContract findContractByVirtualId(Long virtualId) {
		if (Objects.isNull(virtualId)) {
			return null;
		}
		List<CtrContract> contractList = ctrContractDao.findCtrContractByVirtualId(virtualId);
		return contractList.stream().filter(c -> !StringUtils.equals(BasConstants.CONTRACTSTATUS_C, c.getContractStatus())).findFirst().orElse(null);
	}

	@Override
	public CtrContract findSpecialChainContract(Long approveId) {
		return ctrContractDao.findSpecialChainByApproveId(approveId);
	}

	@Override
	public void updatePiccPushFlg(Long contractId, Boolean piccPushFlg) {
		ctrContractDao.updatePiccPushFlg(contractId, piccPushFlg);
	}

	@Override
	public void updatePiccDeclareStatus(Long contractId, String piccDeclareStatus) {
		ctrContractDao.updatePiccDeclareStatus(contractId, piccDeclareStatus);
	}

	@Override
	public void updatePiccReceiveFlg(Long contractId, Boolean piccReceiveFlg) {
		ctrContractDao.updatePiccReceiveFlg(contractId, piccReceiveFlg);
	}

	private BigDecimal getSumDeliveryNum(Long applyId, String applyType){
		BigDecimal deliveryNum = BigDecimal.ZERO;
		List<Object[]> detail = applyProductDetailDao.sumApplyDetail(applyId, applyType);
		if (CollectionUtils.isNotEmpty(detail)){
			for (Object[] d : detail) {
				deliveryNum = deliveryNum.add(Objects.isNull(d[2]) ? BigDecimal.ZERO : (BigDecimal) d[2]);
			}
		}
		return deliveryNum;
	}

	@Override
	public CtrContract sumByLitigation(ContractOrderVo vo) {
		Specification<CtrContract> spe = null;
		Specification<CtrContract> spec = null;
		List<String> statusList = new ArrayList<>();
		statusList.add(BasConstants.CONTRACTSTATUS_D);
		statusList.add(BasConstants.CONTRACTSTATUS_C);
		Specification<CtrContract> contractStatus = WebUtil.buildSpecification("NINS_contractStatus",statusList);
		Specification<CtrContract> enterpriseId = WebUtil.buildSpecification("EQL_enterpriseId",  BasConstants.ZG_ENTERPRISE_ID);
		Specification<CtrContract> likesContractNo = WebUtil.buildSpecification("EQS_contractNo",  vo.getContractNo());
		Specification<CtrContract> likesCompanyName = WebUtil.buildSpecification("LIKES_companyName", vo.getCompanyName());
		Specification<CtrContract> likesOurCompanyName = WebUtil.buildSpecification("EQS_ourCompanyName", vo.getOurCompanyName());
		spec = Specification.where(spec).and(likesCompanyName).and(likesContractNo).and(likesOurCompanyName);
		// 销售合同
		Specification<CtrContract> contractType = WebUtil.buildSpecification("EQS_contractType","S");
		// 逾期或者违约的合同
		Specification<CtrContract> performanceStatus = Specification.where(null);
		if(StringUtils.isNotEmpty(vo.getPerformanceStatus())){
			Specification<CtrContract> status = WebUtil.buildSpecification("EQS_performanceStatus",vo.getPerformanceStatus());
			performanceStatus=Specification.where(performanceStatus).and(status);
		} else {
			Specification<CtrContract> status_S = WebUtil.buildSpecification("EQS_performanceStatus","S");
			Specification<CtrContract> status_P = WebUtil.buildSpecification("EQS_performanceStatus","P");
			performanceStatus=Specification.where(performanceStatus).or(status_S).or(status_P);
		}
		// 不是代采合同
		Specification<CtrContract> matchCreditFlg = WebUtil.buildSpecification("EQB_matchCreditFlg",true);
		if (spec != null){
			spe = Specification.where(spe).and(spec);
		}
		if (!vo.isAdmin()) {
			Specification<CtrContract> spec_userId = WebUtil.buildSpecification("EQL_matchUserId", vo.getUserId());
			if (vo.getUserId().equals(vo.getDeptLeaderId())){
				DeptSearchVo sysDeptSearchVo = new DeptSearchVo();
				sysDeptSearchVo.setUserId(vo.getUserId());
				SysDeptSdk dept = authOpenFacade.findDept(sysDeptSearchVo);
				Specification<CtrContract> spec_department = WebUtil.buildSpecification("EQL_deptId", dept.getDeptId());
				Specification<CtrContract> spec_department_userId = Specification.where(spec_userId).or(spec_department);
				spe = Specification.where(spe).and(spec_department_userId);
			}else{
				spe = Specification.where(spe).and(spec_userId);
			}
		}
		spe = Specification.where(spe).and(performanceStatus).and(contractType).and(matchCreditFlg).and(contractStatus).and(enterpriseId);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<?> query = cb.createQuery();
		Root<CtrContract> root = query.from(CtrContract.class);
		CriteriaQuery<?> cq = query.where(spe.toPredicate(root, query, cb)).multiselect(
				cb.sum(root.get("totalAmount")), cb.sum(root.get("dealedAmount")), cb.sum(root.get("confirmReceiveNumber")),
				cb.sum(root.get("breachAmount")));
		TypedQuery<?> tq = em.createQuery(cq);
		Object[] result = ((Object[]) tq.getSingleResult());
		CtrContract sum = new CtrContract();
		BigDecimal totalAmount = (BigDecimal) result[0];
		BigDecimal dealedAmount = (BigDecimal) result[1];
		BigDecimal confirmReceiveNumber = (BigDecimal) result[2];
		BigDecimal breachAmount = (BigDecimal) result[3];
		sum.setTotalAmount(totalAmount);
		sum.setDealedAmount(dealedAmount);
		sum.setConfirmReceiveNumber(confirmReceiveNumber);
		sum.setBreachAmount(breachAmount);
		return sum;
	}

	@Override
	public List<PmApprove> filterAutoSignWithPay(List<PmApprove> autoSignApproveList) {
		Long enterpriseId = autoSignApproveList.stream().map(PmApprove::getEnterpriseId).findAny().orElse(44L);
		PmProcess targetProcess = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_CODE_PAY, enterpriseId);
		Long processId = targetProcess.getId();
		List<Long> contractIdList = autoSignApproveList.stream()
				.filter(a -> Objects.equals(a.getProcessId(), processId))
				.filter(a -> a.getSubject().contains("全款"))
				.map(PmApprove::getContractId)
				.distinct().collect(Collectors.toList());
		if (CollectionUtils.isEmpty(contractIdList)) {
			return autoSignApproveList;
		}
		List<CtrContract> contractList = ctrContractDao.findByIds(contractIdList);
		if (CollectionUtils.isEmpty(contractList)) {
			return autoSignApproveList;
		}
		List<Long> specialApproveIds = contractList.stream()
				.filter(c -> StringUtils.equals(BasConstants.CONTRACT_TYPE_B, c.getContractType()))
				.filter(c -> Boolean.TRUE.equals(c.getSpecialChainFlag()))
				.filter(c -> StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, c.getCompanyName()))
				.filter(c -> StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, c.getOurCompanyName()))
				.map(CtrContract::getApproveId)
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(specialApproveIds)) {
			return autoSignApproveList;
		}
		List<CtrContract> resultContractList = ctrContractDao.findByApproveIdInAndContractType(specialApproveIds, BasConstants.CONTRACT_TYPE_B);
		if (CollectionUtils.isEmpty(resultContractList)) {
			return autoSignApproveList;
		}
		List<Long> paidApproveIds = resultContractList.stream()
				.filter(c -> c.getTotalAmount().compareTo(c.getDealedAmount()) > 0)
				.map(CtrContract::getApproveId)
				.collect(Collectors.toList());
		List<Long> paidContractIds = contractList.stream().filter(d -> paidApproveIds.contains(d.getApproveId())).map(CtrContract::getId).collect(Collectors.toList());
		return autoSignApproveList.stream().filter(p -> !(Objects.equals(p.getProcessId(), processId) && paidContractIds.contains(p.getContractId()))).collect(Collectors.toList());
	}
	
	@Override
	public DzdAgreement getDzdAgreement(ProtocolDocumentSearchVo searchVo) {
		DzdAgreement dzdAgreement = new DzdAgreement();
		String dzdCompanyName = searchVo.getDzdCompanyName();
		String ourCompanyName = searchVo.getOurCompanyName();
		Date dzDateBegin = searchVo.getDzDateBegin();
		Date dzDateEnd = searchVo.getDzDateEnd();
		
		if (StringUtils.isBlank(dzdCompanyName)) {
			return dzdAgreement;
		}
		if (StringUtils.isBlank(ourCompanyName)) {
			return dzdAgreement;
		}
		if (Objects.isNull(dzDateBegin)) {
			return dzdAgreement;
		}
		if (Objects.isNull(dzDateEnd)) {
			return dzdAgreement;
		}
		List<CtrContract> contractList = ctrContractDao.findByCompanyNameAndOurCompanyNameAndSealDate(dzdCompanyName, ourCompanyName, dzDateBegin, dzDateEnd);
		
		if (CollectionUtils.isEmpty(contractList)) {
			return dzdAgreement;
		}


		List<DzdAgreement.DzdAgreementDetail> dzdDetailList = new ArrayList<>();
		BigDecimal sumTotalAmount = BigDecimal.ZERO;
		BigDecimal sumDealedAmount = BigDecimal.ZERO;
		BigDecimal sumNeedReceiveAmount = BigDecimal.ZERO;
		String ourCompanyContact = "";
		for (CtrContract contract : contractList) {
			if (StringUtils.isBlank(ourCompanyContact)) {
				ourCompanyContact = contract.getMatchUserName();
			}
			DzdAgreement.DzdAgreementDetail dzdDetail = new DzdAgreement.DzdAgreementDetail();
			dzdDetail.setContractNo(contract.getContractNo());
			dzdDetail.setProductsName(contract.getProductsName());
			dzdDetail.setTotalNumber(contract.getTotalNumber());
			dzdDetail.setDealPrice(contract.getDealPrice());
			dzdDetail.setTotalAmount(contract.getTotalAmount());
			BigDecimal dealedAmount = contract.getDealedAmount();
			if (Objects.isNull(dealedAmount)) {
				dealedAmount = BigDecimal.ZERO;
			}
			dzdDetail.setDealedAmount(dealedAmount);
			dzdDetail.setNeedReceiveAmount(contract.getTotalAmount().subtract(dealedAmount));
			dzdDetail.setBreachAmount(contract.getBreachAmount());
			dzdDetail.setConfirmDate(contract.getConfirmDate());
			dzdDetail.setShippingDate(contract.getShippingDate());
			dzdDetailList.add(dzdDetail);

			sumTotalAmount = sumTotalAmount.add(contract.getTotalAmount());
			sumDealedAmount = sumDealedAmount.add(dealedAmount);
			sumNeedReceiveAmount = sumNeedReceiveAmount.add(contract.getTotalAmount().subtract(dealedAmount));
			
			
		}
		dzdAgreement.setDzdDetailList(dzdDetailList);
		dzdAgreement.setTotalAmountSum(sumTotalAmount);
		dzdAgreement.setNeedReceiveAmountSum(sumNeedReceiveAmount);
		dzdAgreement.setOurCompanyContact(ourCompanyContact);

		BsCompanyOur bsCompanyOur = BsCompanyOurUtil.getBsCompanyOur(BasConstants.ZG_ENTERPRISE_ID , ourCompanyName);
		if (Objects.isNull(bsCompanyOur)) {
			return dzdAgreement;
		}
		dzdAgreement.setDzdBankAccountName(ourCompanyName);
		dzdAgreement.setDzdBankName(bsCompanyOur.getCompanyBankName());
		dzdAgreement.setDzdBankAccountNo(bsCompanyOur.getCompanyCardId());
		
		return dzdAgreement;
	}
}
