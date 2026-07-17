package com.spt.bas.server.service.impl;


import com.google.common.base.Splitter;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.BusinessDeliveryExcelVo;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.ICtrContractOphisService;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = false)
public class CtrContractOphisServiceImpl extends BaseService<CtrContractOphis> implements ICtrContractOphisService {
	@Autowired
	private CtrContractDao contractDao;
	@Autowired
	private CtrContractOphisDao ctrContractOphisDao;
	@Autowired
	private ApplyDeliveryInDao applyDeliveryInDao;
	@Autowired
	private ApplyDeliveryOutDao applyDeliveryOutDao;
	@Autowired
	private ApplyConfirmReceiptDao applyConfirmReceiptDao;
	@Autowired
	private ApplyProductDetailDao applyProductDetailDao;

	@Override
	public BaseDao<CtrContractOphis> getBaseDao() {
		return ctrContractOphisDao;
	}

	@Override
	public Class<CtrContractOphis> getEntityClazz() {
		return CtrContractOphis.class;
	}

	@Override
	public CtrContractOphis findByCtrContractId(Long contractId) {
		return ctrContractOphisDao.findByCtrContractId(contractId);
	}

	@Override
	public CtrContractOphis findByApproveIdAndCtrContractId(Long approveId, Long contractId) {
		return ctrContractOphisDao.findFirstByApproveIdAndCtrContractIdOrderByCreatedDateDesc(approveId, contractId);
	}

	@Override
	public void addHis(String applyType, Long ctrContractId, PmApprove approve,Date happenDate) {
		CtrContractOphisRequest request=new CtrContractOphisRequest();
		request.setApplyType(applyType);
		request.setCtrContractId(ctrContractId);
		request.setCancel(false);
		request.setRemark(approve.getSubject());
		request.setCreateUserId(approve.getCreateUserId());
		request.setCreateUserName(approve.getCreateUserName());
		request.setApproveId(approve.getId());
		request.setProcessName(approve.getLastApproveUserName());
		request.setHappenDate(happenDate);
		request.setContractGroup("CTR");
		if (approve.getId() != null && ctrContractId != null) {
			CtrContractOphis oldHis = findByApproveIdAndCtrContractId(approve.getId(), ctrContractId);
			if (Objects.nonNull(oldHis)) {
				request.setId(oldHis.getId());
			}
		}
		addHis(request);
	}
	@Override
	public void addHisDcsx(String applyType,String contractStatus, Long contractId, PmApprove approve, Date happenDate) {
		CtrContractOphisRequest request=new CtrContractOphisRequest();
		request.setApplyType(applyType);
		request.setCtrContractId(contractId);
		request.setCancel(false);
		request.setRemark(approve.getSubject());
		request.setCreateUserId(approve.getCreateUserId());
		request.setCreateUserName(approve.getCreateUserName());
		request.setApproveId(approve.getId());
		request.setProcessName(approve.getLastApproveUserName());
		request.setHappenDate(happenDate);
		request.setContractGroup("DCSX");
		request.setContractStatus(contractStatus);
		request.setEnterpriseId(approve.getEnterpriseId());
		if (approve.getId() != null && contractId != null) {
			CtrContractOphis oldHis = findByApproveIdAndCtrContractId(approve.getId(), contractId);
			if (Objects.nonNull(oldHis)) {
				request.setId(oldHis.getId());
			}
		}
		addHis(request);
	}

	@Override
	public void addHisDcTp(String applyType,String contractStatus, Long contractId, PmApprove approve, Date happenDate) {
		CtrContractOphisRequest request=new CtrContractOphisRequest();
		request.setApplyType(applyType);
		request.setCtrContractId(contractId);
		request.setCancel(false);
		request.setRemark(approve.getSubject());
		request.setCreateUserId(approve.getCreateUserId());
		request.setCreateUserName(approve.getCreateUserName());
		request.setApproveId(approve.getId());
		request.setProcessName(approve.getLastApproveUserName());
		request.setHappenDate(happenDate);
		request.setContractGroup("DCTP");
		request.setContractStatus(contractStatus);
		request.setEnterpriseId(approve.getEnterpriseId());
		if (approve.getId() != null && contractId != null) {
			CtrContractOphis oldHis = findByApproveIdAndCtrContractId(approve.getId(), contractId);
			if (Objects.nonNull(oldHis)) {
				request.setId(oldHis.getId());
			}
		}
		addHis(request);
	}

	@Override
	public void addHis(CtrContractOphisRequest request) {
		if (!StringUtils.equals(request.getContractGroup(),"DCSX") && !StringUtils.equals(request.getContractGroup(),"DCTP")){
			CtrContract entity = contractDao.findOne(request.getCtrContractId());
			if (Objects.nonNull(entity)) {
				request.setEnterpriseId(entity.getEnterpriseId());
				request.setContractStatus(entity.getContractStatus());
			}
		}
		StringBuilder hisMsg = new StringBuilder();
		if (StringUtils.isNotBlank(request.getApplyType())) {
			String applyTypeName = DictUtil.getValue(BasConstants.DICT_TYPE_APPLYTYPE, request.getApplyType());
			if (StringUtils.equals("DCTP",request.getContractGroup())) {
				applyTypeName = applyTypeName.replaceAll("代采赊销","托盘中游");
			}
			if (StringUtils.isNotBlank(applyTypeName)){
				hisMsg.append("[").append(applyTypeName).append("]");
			}
		}
		if(request.isCancel()) {
			hisMsg.append("[作废]");
			request.setHappenDate(null);
		}
		hisMsg.append(request.getRemark());
		CtrContractOphis his = new CtrContractOphis();
		his.setContractGroup(request.getContractGroup());
		his.setEnterpriseId(request.getEnterpriseId());
		his.setCtrContractId(request.getCtrContractId());
		his.setCreateUserId(request.getCreateUserId());
		his.setCreateUserName(request.getCreateUserName());
		his.setContractStatus(request.getContractStatus());
		his.setApproveId(request.getApproveId());
		his.setProcessName(request.getProcessName());
		his.setRemark(hisMsg.toString());
		his.setHappenDate(request.getHappenDate());
		his.setId(request.getId());
		ctrContractOphisDao.save(his);
	}

	/**
	 * 追加上下家合同关联记录
	 */
	@Override
	@ServerTransactional
	public void addHis(CtrContract contract, PmApprove approve, List<Long> lstBuyId) {
		CtrContractOphis his = new CtrContractOphis();
		his.setEnterpriseId(contract.getEnterpriseId());
		his.setCreateUserId(approve.getCreateUserId());
		his.setCreateUserName(approve.getCreateUserName());
		his.setContractStatus(contract.getContractStatus());
		his.setHappenDate(null);
		//his.setCtrContractId(entity.getId());
		//his.setApproveId(approve.getId());
		//his.setProcessName(approve.getProcessName());
		//his.setRemark(approve.getSubject());

		String contractType = contract.getContractType();
		String source = contract.getSource();
		String linkContractId = contract.getLinkContractId();
		String message = "";
		List<Long> linkContractIds = null;
		try {
			if (StringUtils.isNotBlank(linkContractId) || lstBuyId != null) {
				if (lstBuyId != null) {
					linkContractIds = lstBuyId;
				}else {
					linkContractIds = Splitter.on(",").omitEmptyStrings().splitToList(linkContractId).stream()
							.map(m -> Long.valueOf(m)).collect(Collectors.toList());
				}
				if (StringUtils.equals(BasConstants.CONTRACTTYPE_SELL, contractType)
						|| StringUtils.equals(BasConstants.APPLY_TYPE_A, source)) {
					for (Long contractId : linkContractIds) {
						CtrContract ctrContract = contractDao.findOne(contractId);
						String contractNo = ctrContract.getContractNo();
						his.setId(0L);
						his.setCtrContractId(contractId);
						his.setContractGroup("CTR");
						his.setRemark("[关联合同号]"+"["+contract.getContractNo()+"]");
						ctrContractOphisDao.save(his);
						message = message + contractNo + "|";
					}
					if (message.length()>0) {
						message = message.substring(0, message.length()-1);
					}
					his.setId(0L);
					his.setCtrContractId(contract.getId());
					his.setContractGroup("CTR");
					his.setRemark("[关联合同号]"+"["+message+"]");
					ctrContractOphisDao.save(his);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.info("追加合同上下家关联记录异常,contractId:{}",contract.getId());
		}

	}

	/**
	 * 添加合同盖章追回记录
	 * @param contract
	 * @param approve
	 */
	@Override
	@ServerTransactional
	public void addRollBackHis(CtrContract contract, PmApprove approve) {
		if (Objects.isNull(contract) || Objects.isNull(approve)){
			return;
		}
		CtrContractOphis his = new CtrContractOphis();
		try {
			his.setId(0L);
			his.setEnterpriseId(contract.getEnterpriseId());
			his.setCreateUserId(approve.getCreateUserId());
			his.setCreateUserName(approve.getCreateUserName());
			his.setContractStatus(contract.getContractStatus());
			his.setHappenDate(new Date());
			his.setApproveId(approve.getId());
			his.setCtrContractId(contract.getId());
			his.setContractGroup("CTR");
			his.setRemark("[合同盖章追回]"+"["+contract.getContractNo()+"]");
			ctrContractOphisDao.save(his);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.info("合同盖章追回,contractId:{}",contract.getId());
		}

	}
	/**
	 * 添加合同盖章发起记录
	 *
	 */
	@Override
	@ServerTransactional
	public void addSealStartHis(PmApprove approve) {
		if (Objects.isNull(approve)){
			return;
		}
		CtrContractOphis his = new CtrContractOphis();
		try {
			Long contractId = approve.getContractId();
			CtrContract contract = contractDao.findOne(contractId);
			Long approveId = approve.getId();
			his.setId(0L);
			his.setEnterpriseId(contract.getEnterpriseId());
			his.setCreateUserId(approve.getCreateUserId());
			his.setCreateUserName(approve.getCreateUserName());
			his.setContractStatus(contract.getContractStatus());
			his.setHappenDate(new Date());
			his.setApproveId(approveId);
			his.setCtrContractId(contract.getId());
			his.setContractGroup("CTR");
			his.setRemark("[合同盖章发起]"+"["+contract.getContractNo()+"]");
			ctrContractOphisDao.save(his);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.info("合同盖章发起,contractId:{}",approve.getContractId());
		}
	}

	@Override
	public void addRejectHis(CtrContract contract, PmApprove approve) {
		if (Objects.isNull(contract) || Objects.isNull(approve)){
			return;
		}
		CtrContractOphis his = new CtrContractOphis();
		try {
			his.setId(0L);
			his.setEnterpriseId(contract.getEnterpriseId());
			his.setCreateUserId(approve.getCreateUserId());
			his.setCreateUserName(approve.getCreateUserName());
			his.setContractStatus(contract.getContractStatus());
			his.setHappenDate(new Date());
			his.setApproveId(approve.getId());
			his.setCtrContractId(contract.getId());
			his.setContractGroup("CTR");
			his.setRemark("[合同盖章驳回]"+"["+contract.getContractNo()+"]");
			ctrContractOphisDao.save(his);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.info("合同盖章驳回,contractId:{}",contract.getId());
		}
	}

	/**
	 * 获取业务提货审批单导出参数
	 * @param approveId
	 * @return
	 */
	@Override
	public BusinessDeliveryExcelVo getBusinessDelivery(Long approveId) {
		if (Objects.isNull(approveId) || approveId == 0L){
			return null;
		}
		BusinessDeliveryExcelVo excelVo = new BusinessDeliveryExcelVo();
		List<BusinessDeliveryExcelVo.ExcelDetail> excelDetailList = new ArrayList<>();
		List<CtrContract> contractList = contractDao.findByApproveId(approveId);
		CtrContract sellContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_TYPE_S, c.getContractType())).findFirst().orElse(null);
		CtrContract buyContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_TYPE_B, c.getContractType())).findFirst().orElse(null);
		List<Long> contractIdList = contractList.stream().map(CtrContract::getId).collect(Collectors.toList());
		Map<String, Object> searchParams = new HashMap<>();
		searchParams.put("INL_ctrContractId", contractIdList);
		searchParams.put("NEQS_contractGroup", "DCSX");
		Sort sort = Sort.by(Sort.Direction.ASC, "createdDate");
		Specification<CtrContractOphis> specification = WebUtil.buildSpecification(searchParams);
		List<CtrContractOphis> ophisList = ctrContractOphisDao.findAll(specification, sort);

		excelVo.setProductNames(sellContract.getProductsName());
		excelVo.setDeliveryName(buyContract.getCompanyName());
		excelVo.setMatchUserName(sellContract.getMatchUserName());

		BusinessDeliveryExcelVo.ExcelDetail detail = new BusinessDeliveryExcelVo.ExcelDetail();
		detail.setExcelDate(buyContract.getContractTime());
		detail.setSubject("订货");
		detail.setContractNo(buyContract.getContractNo());
		detail.setDeliveryInNumber(buyContract.getTotalNumber());
		detail.setDeliveryNumber(BigDecimal.ZERO);
		detail.setDeliveryNoNumber(buyContract.getTotalNumber());
		excelDetailList.add(detail);
		BusinessDeliveryExcelVo.ExcelDetail excelDetail;
		BigDecimal totalNumber = sellContract.getTotalNumber();
		BigDecimal deliveryOutNumber = BigDecimal.ZERO;
		for (CtrContractOphis ophis : ophisList) {
			String remark = ophis.getRemark();
			excelDetail = new BusinessDeliveryExcelVo.ExcelDetail();
			if (remark.startsWith("[入库]")){
				ApplyDeliveryIn deliveryIn = applyDeliveryInDao.findByApplyId(ophis.getApproveId());
				ApplyProductDetail deliveryInDetail = applyProductDetailDao.findByApplyIdAndApplyType(deliveryIn.getId(), BasConstants.APPLY_TYPE_I);
				excelDetail.setExcelDate(deliveryIn.getWarehouseInDate());
				excelDetail.setSubject("业务入库");
				excelDetail.setCompanyName(deliveryIn.getCompanyName());
				excelDetail.setContractNo(deliveryIn.getContractNo());
				excelDetail.setDeliveryInNumber(deliveryInDetail.getCurNumber());
				excelDetail.setDeliveryNumber(BigDecimal.ZERO);
				excelDetail.setDeliveryNoNumber(deliveryInDetail.getCurNumber());
				excelDetailList.add(excelDetail);
			}else if (remark.startsWith("[出库]")){
				ApplyDeliveryOut deliveryOut = applyDeliveryOutDao.findEntity(ophis.getApproveId());
				ApplyProductDetail deliveryOutDetail = applyProductDetailDao.findByApplyIdAndApplyType(deliveryOut.getId(), BasConstants.APPLY_TYPE_O);
				deliveryOutNumber = deliveryOutNumber.add(deliveryOutDetail.getCurNumber());
				String type = DictUtil.getValue(BasConstants.DICT_TYPE_DELIVERYOUT_TYPE, deliveryOut.getWarehouseOutType());
				excelDetail.setExcelDate(deliveryOut.getWarehouseOutDate());
				excelDetail.setSubject(type);
				excelDetail.setCompanyName(deliveryOut.getCompanyName());
				excelDetail.setContractNo(deliveryOut.getContractNo());
				excelDetail.setDeliveryInNumber(BigDecimal.ZERO);
				excelDetail.setDeliveryNumber(deliveryOutDetail.getCurNumber());
				excelDetail.setDeliveryNoNumber(totalNumber.subtract(deliveryOutNumber));
				excelDetailList.add(excelDetail);
			}else if (remark.startsWith("[收货确认]")){
				ApplyConfirmReceipt confirmReceipt = applyConfirmReceiptDao.findByApproveId(ophis.getApproveId());
				ApplyProductDetail confirmReceiptDetail = applyProductDetailDao.findByApplyIdAndApplyType(confirmReceipt.getId(), BasConstants.APPLY_TYPE_G);
				excelDetail.setExcelDate(confirmReceipt.getConfirmReceiptDate());
				excelDetail.setSubject("中转单");
				excelDetail.setCompanyName(confirmReceipt.getCompanyName());
				excelDetail.setContractNo(confirmReceipt.getContractNo());
				excelDetail.setDeliveryInNumber(BigDecimal.ZERO);
				excelDetail.setDeliveryNumber(confirmReceiptDetail.getCurNumber());
				excelDetail.setDeliveryNoNumber(BigDecimal.ZERO);
				excelDetailList.add(excelDetail);
			}
		}
		excelVo.setExcelDetailList(excelDetailList);
		return excelVo;
	}

//	@Override
//	public void updateContractStatusByContractId(Long ctrContractId, String contractStatus) {
//		ctrContractOphisDao.updateContractStatusByContractId(ctrContractId,contractStatus);
//
//	}
}

