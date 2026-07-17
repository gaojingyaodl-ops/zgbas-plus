package com.spt.bas.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.ApplyImportDetailVo;
import com.spt.bas.client.vo.ApplyImportVo;
import com.spt.bas.client.vo.ApplyProductDetailSaveVo;
import com.spt.bas.client.vo.CompanyAccountVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.dao.ApplyImportDao;
import com.spt.bas.server.dao.ApplyImportDetailDao;
import com.spt.bas.server.dao.ApplyProductDetailDao;
import com.spt.bas.server.service.IApplyImportService;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
/** 代理开证 */
@Component("applyImportService")
@Transactional(readOnly = true)
public class ApplyImportServiceImpl extends BaseService<ApplyImport>
		implements IApplyImportService, IPmService, IPmApproveListener {
	@Autowired
	private ApplyImportDao applyImportDao;
	@Autowired
	private IBsCompanyService bsCompanyService;
	@Autowired
	private ApplyImportDetailDao applyImportDetailDao;
	@Autowired
	private ICtrContractService contractService;
	@Autowired
	private ICtrContractSaveService contractSaveService;
	// @Autowired
	// private IStockService stockService;
	@Autowired
	private IApplyProductDetailService productDetailService;
	@Autowired
	private ApplyProductDetailDao applyProductDetailDao;
	@Value("${credit.contract.switch}")
	private Boolean creditSwitch;
	@Override
	public BaseDao<ApplyImport> getBaseDao() {
		return applyImportDao;
	}
	@Override
	public Class<ApplyImport> getEntityClazz() {
		return ApplyImport.class;
	}
	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		applyImportDao.updateFileId(id, fileId);
	}
	@Override
	@ServerTransactional
	public void doStepIn(PmApprove approve) throws ApplicationException {
		// if (creditSwitch) {
		// ApplyImport applyImport=applyImportDao.findOne(approve.getBizId());
		// List<ApplyImportDetail>
		// matchList=applyImportDetailDao.findByApplyImportId(applyImport.getId());
		// String deliveryMode = null;
		// Long companyId = 0L;
		// for(ApplyImportDetail importDetail:matchList){
		// if(importDetail.getContractType().equals(BasConstants.APPLY_TYPE_S)){
		// deliveryMode = importDetail.getDeliveryMode();
		// companyId = importDetail.getCompanyId();
		// }
		// }
		// // 判断企业授信额度 更新审批中的授信额度
		// BsCompany bsCompany = bsCompanyService.getCompanyCredit(companyId);
		// BigDecimal totalCredit = bsCompany.getTotalCreditAmount() == null ?
		// BigDecimal.ZERO : bsCompany.getTotalCreditAmount();
		//// BigDecimal usedCredit = bsCompany.getUsedCreditAmount() == null ?
		// BigDecimal.ZERO : bsCompany.getUsedCreditAmount();
		// BigDecimal approveCredit = bsCompany.getApproveCreditAmount() == null
		// ? BigDecimal.ZERO : bsCompany.getApproveCreditAmount();
		//// BigDecimal remainAmount =
		// totalCredit.subtract(usedCredit).subtract(approveCredit);
		// if (StringUtils.equals(deliveryMode, BasConstants.DELIVERY_MODE_SX))
		// {
		//// if (remainAmount.compareTo(applyImport.getSellAmount()) < 0) {
		//// throw new ApplicationException("该企业剩余可用授信额度不足！！！");
		//// }
		// if (totalCredit.compareTo(BigDecimal.ZERO) <= 0) {
		// logger.error("totalCredit is zero！companyId：{} ", bsCompany.getId());
		//// throw new ApplicationException("该企业未开通授信！！！");
		// }
		// BigDecimal realApproveCredit =
		// approveCredit.add(applyImport.getSellAmount());
		// bsCompany.setApproveCreditAmount(realApproveCredit);
		// bsCompanyService.save(bsCompany);
		// }
		// }
	}
	@Override
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			// 获得撮合信息
			ApplyImport applyImport = applyImportDao.findOne(approve.getBizId());
			String ourCompanyName = applyImport.getOurCompanyName();
			// 获得撮合明细，先采购，后销售
			List<ApplyImportDetail> matchList = applyImportDetailDao.findByApplyImportId(applyImport.getId());
			// 采购合同Id获取，保存在销售合同里，销售合同可以找到对应的采购合同ID
			List<Long> lstBuyId = new ArrayList<>();
			List<Long> lstSellId = new ArrayList<>();
			int buyI = 0, sellI = 0;
			for (ApplyImportDetail importDetail : matchList) {
				// 预付定金
				BigDecimal bondAmmount = BigDecimal.ZERO;
				CtrContract contract = new CtrContract();
				BeanUtils.copyProperties(importDetail, contract);
				contract.setId(null);
				if (importDetail.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
					bondAmmount = importDetail.getReceiveBondAmount();
					contract.setPayBondTime(importDetail.getReceiveBondTime());
					contract.setRemark(importDetail.getReceiveRemark());
					contract.setPayType(importDetail.getReceiveType());
					sellI++;
					contract.setSource(BasConstants.APPLY_TYPE_RS);
					// 如果是销售合同，contract的payTime为收款时间
					contract.setPayFullTime(importDetail.getReceiveFullTime());
					// 添加赊销合同标识
					if (StringUtils.equals(importDetail.getDeliveryMode(), BasConstants.DELIVERY_MODE_SX)) {
						contract.setCreditFlg(true);
					}
				} else {
					bondAmmount = importDetail.getPayBondAmount();
					contract.setRemark(importDetail.getPayRemark());
					contract.setSource(BasConstants.APPLY_TYPE_RB);
					buyI++;
				}
				contract.setDeliveryAddr(importDetail.getObjectivePort());
				contract.setWarehouseAmount(importDetail.getWarehouseCost());
				contract.setTransportAmount(importDetail.getTransportCost());
				contract.setContractType(importDetail.getContractType());
				contract.setFileId(applyImport.getFileId());
				contract.setTransportAmount(importDetail.getTransportCost());
				contract.setWarehouseAmount(importDetail.getWarehouseCost());
				contract.setOurCompanyName(ourCompanyName); // 我方企业抬头
				contract.setDeliveryType(BasConstants.COMPANY_STATUS_Z);
				contract.setQualityStandard(BasConstants.QUALITY_Y);
				contract.setBusinessType(applyImport.getBusinessType());
				contract.setBondAmount(importDetail.getPayBondAmount());
				contract.setRemark(importDetail.getPayRemark());
				contract.setExtraTerm(applyImport.getPayCondition());
				contract.setPayBondTime(applyImport.getBondTime());
				contract.setPayFullTime(applyImport.getFullTime());
				contract.setContractAttr(applyImport.getContractAttr());
				contract.setForeignContractNo(applyImport.getForeignContractNo());
				contract.setDeliveryDateFrom(applyImport.getArrivalTime());
				contract.setDeliveryDateTo(applyImport.getArrivalTime());
				// 获得商品明细
				List<ApplyProductDetail> productList = productDetailService.findApplyDetail(importDetail.getId(),
						BasConstants.APPLY_TYPE_R);
				contract.setBondAmount(bondAmmount);
				// 配送地址
				// contract.setDeliveryAddr(match.getShippingAddr());
				contract = contractSaveService.saveContract(contract, productList, approve, lstBuyId);
				// 记录合同id
				importDetail.setContractId(contract.getId());
				if (importDetail.getContractType().equals(BasConstants.APPLY_TYPE_B)) {
					lstBuyId.add(contract.getId());
				} else {
					lstSellId.add(contract.getId());
				}
				applyImportDetailDao.save(importDetail);
			}
			// ---关联合同id
			if (sellI == 1) {
				Long sellContractId = lstSellId.get(0);
				CtrContract sell = contractService.getEntity(sellContractId);
				sell.setLinkContractId("," + Joiner.on(",").join(lstBuyId) + ",");
				for (int i = 0; i < buyI; i++) {
					CtrContract buy = contractService.getEntity(lstBuyId.get(i));
					buy.setLinkContractId("," + sellContractId + ",");
				}
			} else if (buyI == 1) {
				Long buyContractId = lstBuyId.get(0);
				CtrContract buy = contractService.getEntity(buyContractId);
				buy.setLinkContractId("," + Joiner.on(",").join(lstSellId) + ",");
				for (int i = 0; i < buyI; i++) {
					CtrContract sell = contractService.getEntity(lstSellId.get(i));
					sell.setLinkContractId("," + buyContractId + ",");
				}
			}
			// BizUserInfor userInfor = new BizUserInfor();
			// userInfor.setBizUserId(approve.getCreateUserId());
			// userInfor.setBizUserName(approve.getCreateUserName());
			// userInfor.setApproveId(applyImport.getId());
			// userInfor.setApproveNo(approve.getApproveNo());
			// 对撮合业务的采购进行自动入库。一定要在前代码更新在途销售数据后执行如下代码
			// for(Long contractId: lstBuyId) {
			// //获得商品明细
			// List<CtrProduct> lstProd =
			// ctrProductService.findByContractId(contractId);
			//// stockService.deliveryInStockByMatch(lstProd,contractId,userInfor);
			//
			// for(CtrProduct prod : lstProd) {
			// StockDetailRequest request = StockDetailRequest.build(prod);
			// // 采购
			// request.setApplyType(BasConstants.APPLY_TYPE_I);
			//// request.setContractType(BasConstants.CONTRACT_TYPE_B);
			// request.setMatchBl(true);
			// request.setCtrContractId(contractId);
			// request.setApplyId(null);
			// request.setStockType(BasConstants.DICT_TYPE_STOCKTYPE_XH);
			// request.setSpotType(BasConstants.DICT_TYPE_SPOTTYPE_W);
			// stockDetailFacade.saveDeliveryIn(request);
			// }
			//
			// CtrContract contract = contractService.getEntity(contractId);
			// contractUpdateService.addWarehouseNumber(contractId,
			// contract.getTotalNumber(),approve.getApproveNo());
			// contractOphisService.addHis(BasConstants.APPLY_TYPE_I,
			// contractId, approve);
			// }
		}
	}
	@Override
	@ServerTransactional
	public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
		if (creditSwitch) {
			ApplyImport applyImport = applyImportDao.findOne(approve.getBizId());
			List<ApplyImportDetail> matchList = applyImportDetailDao.findByApplyImportId(applyImport.getId());
			String deliveryMode = null;
			Long companyId = 0L;
			for (ApplyImportDetail importDetail : matchList) {
				if (importDetail.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
					deliveryMode = importDetail.getDeliveryMode();
					companyId = importDetail.getCompanyId();
				}
			}
			BsCompany bsCompany = bsCompanyService.getEntity(companyId);
			BigDecimal approveCredit = bsCompany.getApproveCreditAmount() == null ? BigDecimal.ZERO
					: bsCompany.getApproveCreditAmount();
			if (StringUtils.equals(deliveryMode, BasConstants.DELIVERY_MODE_SX)) {
				BigDecimal realApproveCredit = approveCredit.subtract(applyImport.getSellAmount());
				bsCompany.setApproveCreditAmount(realApproveCredit);
				bsCompanyService.save(bsCompany);
			}
		}
	}
	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		// 标题显示规则：[品名/牌号/公司，品名/牌号/公司] - [品名/牌号/公司，品名/牌号/公司] 数量
		ApplyImport vo = (ApplyImport) pmEntity;
		Long matchId = vo.getId();
		List<ApplyImportDetail> list = applyImportDetailDao.findByApplyImportId(matchId);
		StringBuffer strBuf = new StringBuffer();
		StringBuffer strBuf2 = new StringBuffer();
		strBuf.append("[");
		strBuf2.append("[");
		BigDecimal totalNumber = new BigDecimal(0);
		for (ApplyImportDetail detail : list) {
			Long applyId = detail.getId();
			List<ApplyProductDetail> productlist = productDetailService.findApplyDetail(applyId,
					BasConstants.APPLY_TYPE_R);
			for (ApplyProductDetail product : productlist) {
				String contractyType = detail.getContractType();
				BigDecimal number = product.getDealNumber();
				totalNumber = totalNumber.add(number);
				String productCd = product.getProductCd();
				String productName = product.getProductName();
				String brandNumber = product.getBrandNumber();
				Long companyId = detail.getCompanyId();
				BsCompany company = bsCompanyService.getEntity(companyId);
				String companyName = null;
				if (company != null) {
					companyName = company.getCompanyName();
				}
				if (contractyType.equals("S")) {
					if (strBuf.length() > 1) {
						if (productCd.indexOf("SL") > 0) {
							strBuf.append("," + productName + "/" + brandNumber + "/" + companyName);
						} else {
							strBuf.append("," + productName + "/" + companyName);
						}
					} else {
						if (productCd.indexOf("SL") > 0) {
							strBuf.append(productName + "/" + brandNumber + "/" + companyName);
						} else {
							strBuf.append(productName + "/" + companyName);
						}
					}
				} else {
					if (strBuf2.length() > 1) {
						if (productCd.indexOf("SL") > 0) {
							strBuf2.append("," + productName + "/" + brandNumber + "/" + companyName);
						} else {
							strBuf2.append("," + productName + "/" + companyName);
						}
					} else {
						if (productCd.indexOf("SL") > 0) {
							strBuf2.append(productName + "/" + brandNumber + "/" + companyName);
						} else {
							strBuf2.append(productName + "/" + companyName);
						}
					}
				}
			}
		}
		String totalNumberStr = NumberUtil.formatNumber(totalNumber, "#.###");
		strBuf.append("]-");
		strBuf2.append("]");
		strBuf2.append(totalNumberStr);
		strBuf.append(strBuf2);
		return strBuf.toString();
	}
	/**
	 * 进口代理业务，删除审批
	 */
	@Override
	@ServerTransactional
	public void delete(Long id) {
		if (id != null && id > 0l) {
			applyImportDetailDao.deleteByApplyImportId(id);
			applyImportDao.delete(id);
		}
	}
	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		// TODO Auto-generated method stub
	}
	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		ApplyImport entity = new ApplyImport();
		if (pmEntity instanceof ApplyImportVo) {
			ApplyImportVo vo = (ApplyImportVo) pmEntity;
			ApplyProductDetailSaveVo pvo = new ApplyProductDetailSaveVo();
			// copy 数据
			BeanUtils.copyProperties(vo, entity);
			// 1.保存进口代理主表信息
			entity = applyImportDao.save(entity);
			Long applyImportId = entity.getId();
			Long enterpriseId = entity.getEnterpriseId();
			BigDecimal buyAmount = BigDecimal.ZERO;
			BigDecimal sellAmount = BigDecimal.ZERO;
			BigDecimal buyDealPrice = BigDecimal.ZERO;
			BigDecimal sellDealPrice = BigDecimal.ZERO;
			// 获得进口代理明细表 及商品明细
			for (ApplyImportDetailVo list : vo.getLstInsert()) {
				// 进口代理明细
				ApplyImportDetail detail = new ApplyImportDetail();
				BeanUtils.copyProperties(list, detail);
				if (detail.getId() == null) {
					// 生成合同号
					String contractNo = BasBusinessUtil.composeContractNo(vo.getEnterpriseId(), vo.getDeptAbbr(),
							detail.getContractType());
					detail.setContractNo(contractNo);
				}
				// 采购
				if (detail.getContractType().equals(BasConstants.CONTRACTTYPE_BUY)) {
					detail.setPayBondTime(vo.getBondTime());
					detail.setPayFullTime(vo.getFullTime());
				} else {
					detail.setReceiveBondTime(vo.getBondTime());
					detail.setReceiveFullTime(vo.getFullTime());
				}
				// 到货时间
				if (null != vo.getArrivalTime()) {
					detail.setArrivalTime(vo.getArrivalTime());
				}
				// 获得供货商id
				Long companyId = list.getCompanyId();
				// 供货商详情
				CompanyAccountVo company = bsCompanyService.findCompanyAccountVo(companyId);
				// 账户
				detail.setCompanyAccount(company.getBankAccount());
				// 银行
				detail.setCompanyBank(company.getBankName());
				// 企业名称
				detail.setCompanyName(company.getCompanyName());
				// 联系电话
				// matchDetail.setContactPhone(company.getContactPhone());
				// 联系人
				detail.setContactName(company.getContactName());
				// 地址
				// matchDetail.setContactAddr(company.getAddress());
				// 关联主表
				detail.setApplyImportId(applyImportId);
				detail.setEnterpriseId(enterpriseId);
				// 2.保存进口代理明细表
				detail = applyImportDetailDao.save(detail);
				pvo.setApplyType(BasConstants.APPLY_TYPE_R);
				pvo.setApplyId(detail.getId());
				pvo.setEnterpriseId(enterpriseId);
				// 保存进口代理明细表
				List<ApplyProductDetail> insertList = list.getLstInsert();
				for (ApplyProductDetail proDetail : insertList) { // 新增
					proDetail.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_P);
					// 3.保存商品明细表
				}
				productDetailService.saveDetailBatch(list.getLstInsert(), list.getLstUpdate(), list.getLstDelete(),
						pvo);
				// 重新计算收款比例 付款比例
				List<ApplyProductDetail> productArr = productDetailService.findApplyDetail(detail.getId(),
						BasConstants.APPLY_TYPE_R);
				BigDecimal payRate = BigDecimal.ZERO;
				BigDecimal receiveRate = BigDecimal.ZERO;
				BigDecimal totalPrice = BigDecimal.ZERO;
				for (ApplyProductDetail ProDetail : productArr) {
					totalPrice = totalPrice.add(ProDetail.getTotalPrice());
					if (BasConstants.CONTRACT_TYPE_B.equals(detail.getContractType())) {
						buyDealPrice = buyDealPrice.add(ProDetail.getDealPrice());
					} else {
						sellDealPrice = sellDealPrice.add(ProDetail.getDealPrice());
					}
				}
				if (BasConstants.CONTRACT_TYPE_S.equals(detail.getContractType())) {
					receiveRate = detail.getReceiveBondAmount().divide(totalPrice, 4, BigDecimal.ROUND_HALF_UP);
					detail.setReceiveRate(receiveRate);
					sellAmount = sellAmount.add(totalPrice);
				} else {
					payRate = detail.getPayBondAmount().divide(totalPrice, 4, BigDecimal.ROUND_HALF_UP);
					detail.setPayRate(payRate);
					buyAmount = buyAmount.add(totalPrice);
				}
				// 保存设置的比例
				applyImportDetailDao.save(detail);
			}
			if (vo.getRemoveArrStr() != null) {
				// 解析数组
				List<Long> removeList = JSON.parseArray(vo.getRemoveArrStr(), Long.class);
				for (Long id : removeList) {
					// 删除供应商信息以及关联的商品信息
					applyImportDao.delete(id);
					applyProductDetailDao.deleteDetail(id, BasConstants.APPLY_TYPE_R);
				}
			}
			entity.setBuyAmount(buyAmount);
			entity.setSellAmount(sellAmount);
			entity.setGrossProfit(sellAmount.subtract(buyAmount));// 合同毛利
			entity.setDifferPrice(sellDealPrice.subtract(buyDealPrice));// 差价
			applyImportDao.save(entity);
		} else {
			entity = (ApplyImport) pmEntity;
			entity = applyImportDao.save(entity);
		}
		return entity;
	}
}