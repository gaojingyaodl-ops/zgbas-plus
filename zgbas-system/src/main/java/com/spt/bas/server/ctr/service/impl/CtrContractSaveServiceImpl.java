package com.spt.bas.server.ctr.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.CreditFlowEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.util.ContractCfsUtil;
import com.spt.bas.client.vo.BizUserInfor;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.EnterpriseCache;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.*;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.bas.server.stock.service.StockDetailFacade;
import com.spt.bas.server.util.DeptUtils;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class CtrContractSaveServiceImpl implements ICtrContractSaveService {
	private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
	private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private CtrProductDao ctrProductDao;
	@Autowired
	private ICtrProductService productService;
	@Autowired
	private StockDetailFacade stockDetailFacade;
	@Autowired
	private StockContractDao stockContractDao;
	@Autowired
	private StockContractRelaDao stockContractRelaDao;
	@Autowired
	private IStockContractService stockContractService;
	@Autowired
	private ICtrContractOphisService contractHisService;
	@Autowired
	private ICtrContractTextService contractTextService;
	@Autowired
	private ICtrContractApplyService contractApplyService;
	@Autowired
	private ICtrContractRelaService contractRelaService;
	@Autowired
	private IBasBrandService brandService;
	@Autowired
	private IBsCompanyService bsCompanyService;
	@Autowired
	private IApplyProductDetailService applyProductDetailService;
	@Autowired
	private ApplyBuyDao applyBuyDao;
	@Autowired
	private IApplySellService applySellService;
	@Autowired
	private IBsCompanyCreditFlowService companyCreditFlowService;
	@Resource
	private DeptUtils deptUtils;
	@Autowired
	private IBsProductTypeClient productTypeClient;
	@Resource
	private ICtrContractUpdateService ctrContractUpdateService;

	@Override
	public CtrContract saveInterContract(CtrContract entity, List<ApplyProductDetail> list, PmApprove approve,
										 CtrContract oContract) throws ApplicationException {
		entity.setId(0L);
		entity.setCreatedDate(null);
		entity.setUpdatedDate(null);
		entity.setContractStatus(BasConstants.CONTRACTSTATUS_B);// 已签约
		entity.setProductStatus(BasConstants.PRODUCT_STATUS_P);// 新增合同为在途
		entity.setStatus(BasConstants.APPROVE_STATUS_D);
		entity.setContractType(oContract.getContractType());
		entity.setSource(BasConstants.APPLY_TYPE_F);
		entity.setContractTime(new Date());
		entity.setCompanyId(oContract.getCompanyId());
		entity.setCompanyName(oContract.getCompanyName());
		entity.setOurCompanyName(oContract.getOurCompanyName());
		entity.setDeliveryType(oContract.getDeliveryType());
		entity.setPayType(oContract.getPayType());
		entity.setDeliveryMode(oContract.getDeliveryMode());
		entity.setDeliveryAddr(oContract.getDeliveryAddr());
		entity.setDeliveryPhone(oContract.getDeliveryPhone());
		entity.setFileId(oContract.getFileId());
		entity.setRemark(null);
		entity.setBondAmount(oContract.getBondAmount());
		entity.setTransportAmount(oContract.getTransportAmount());
		entity.setWarehouseAmount(oContract.getWarehouseAmount());
		entity.setContractAttr(oContract.getContractAttr());
		entity.setProductsName(oContract.getProductsName());
		entity.setBillFlg(false);
		entity.setDeptId(oContract.getDeptId());
		entity.setPayBondTime(oContract.getPayBondTime());
		entity.setPayFullTime(oContract.getPayFullTime());
		entity.setContactName(oContract.getContactName());
		entity.setContactPhone(oContract.getContactPhone());
		entity.setContactAddr(oContract.getContactAddr());
		entity = ctrContractDao.save(entity);
		List<CtrProduct> lstProduct = new ArrayList<>();
		// 保存商品信息表
		for (ApplyProductDetail appProd : list) {
			// 保存合同商品明细
			CtrProduct ctrProd = new CtrProduct();
			BeanUtils.copyProperties(appProd, ctrProd);
			ctrProd.setCtrContractId(entity.getId());
			ctrProd.setEnterpriseId(entity.getEnterpriseId());
			ctrProd.setId(0L);
			ctrProd.setRemainNumber(appProd.getDealNumber());
			ctrProd.setTotalPrice(appProd.getDealNumber().multiply(appProd.getDealPrice()));
			ctrProd.setQualityStandard(entity.getQualityStandard());
			ctrProd = productService.save(ctrProd);
			lstProduct.add(ctrProd);
		}
		contractHisService.addHis(entity.getSource(), entity.getId(), approve,null);
		// 生成一条CtrContractApply记录
		contractApplyService.saveCtrContractApply(entity.getId(), entity.getEnterpriseId());

		saveContractText(entity, lstProduct, null);

		return entity;
	}

	@Override
	@ServerTransactional
	public CtrContract saveContract(CtrContract entity, List<ApplyProductDetail> list, PmApprove approve)
			throws ApplicationException {
		return saveContract(entity, list, approve, null, null);
	}

	@Override
	@ServerTransactional
	public CtrContract saveContract(CtrContract entity, List<ApplyProductDetail> list, PmApprove approve, List<Long> lstBuyId) throws ApplicationException {
		return saveContract(entity, list, approve, lstBuyId, null);
	}

	@Override
	@ServerTransactional
	public CtrContract saveContract(CtrContract entity, List<ApplyProductDetail> list, PmApprove approve,
									List<Long> lstBuyId, ApplyMatchDetail matchDetail) throws ApplicationException {
		// 保存合同主表
		entity.setId(0L);
		entity.setCreatedDate(null);
		entity.setUpdatedDate(null);
		entity.setBusinessNo(entity.getContractNo()); // 业务编号 同步为合同编号
		entity.setContractTime(new Date());
		entity.setContractStatus(BasConstants.CONTRACTSTATUS_B);//已审批
		entity.setProductStatus(BasConstants.PRODUCT_STATUS_P);// 新增合同为在途
		entity.setStatus(BasConstants.APPROVE_STATUS_D);
		entity.setApproveId(approve.getId());
		if (entity.getLongFlg() == null) {
			entity.setLongFlg(false);
		}
		// 计算合同总价
		BigDecimal totalAmount = BigDecimal.ZERO;
		BigDecimal totalNumber = BigDecimal.ZERO;// 合同总数量
		StringBuilder productsName = new StringBuilder();
		List<CtrProduct> lstProduct = new ArrayList<>();
		for (ApplyProductDetail appProd : list) {
			totalNumber = totalNumber.add(appProd.getDealNumber());
		}
		entity.setTotalNumber(totalNumber);
		
		setDeptId(entity, approve, matchDetail);
		entity = ctrContractDao.save(entity);
		boolean equals=StringUtils.equals(entity.getBusinessType(),BasConstants.BUSINESS_TYPE_ZY_CG);
		boolean equals1=StringUtils.equals(entity.getBusinessType(),BasConstants.BUSINESS_TYPE_ZY_XS);
		List<BsProductType>allProductAlAndHg=productTypeClient.findAllProductAlAndHg();
		List<String>collect=allProductAlAndHg.stream().map(s->s.getTypeName()).collect(Collectors.toList());
		String join=StringUtils.join(",",collect);
		for(ApplyProductDetail appProd:list){
			totalAmount=totalAmount.add(appProd.getDealNumber().multiply(appProd.getDealPrice()));
            //保存合同商品明细
			if(equals==true||equals1==true){
				if(join.indexOf(appProd.getProductName())>0){
					appProd.setBrandNumber("");
				}
			}
			CtrProduct ctrProd=saveCtrProduct(entity,appProd);
			entity.setProductsName(ctrProd.getProductName());
            //拼凑合同主表的货名
			genProductsName(productsName,ctrProd);
			lstProduct.add(ctrProd);
			BizUserInfor userInfo=new BizUserInfor();
			userInfo.setBizUserId(approve.getCreateUserId());
			userInfo.setBizUserName(approve.getCreateUserName());
			userInfo.setApproveId(approve.getId());
			userInfo.setApproveNo(approve.getApproveNo());
			if (BasConstants.APPLY_TYPE_S.equals(entity.getSource())) {
				// 销售申请单,在发起申请时，就已经冻结数量，这里无需冻结
				entity.setSealFlg(true);
				saveSell(entity, appProd, ctrProd, userInfo);
			} else if (BasConstants.APPLY_TYPE_B.equals(entity.getSource())) {
				saveBuy(ctrProd, userInfo);
			} else if (BasConstants.APPLY_TYPE_MB.equals(entity.getSource())
					|| BasConstants.APPLY_TYPE_RB.equals(entity.getSource())) {
				// 撮合采购,进口采购
				entity.setSealFlg(false);
			} else if (BasConstants.APPLY_TYPE_MS.equals(entity.getSource())
					|| BasConstants.APPLY_TYPE_RS.equals(entity.getSource())) {
				// 撮合销售，进口销售
				entity.setSealFlg(false);
			} else if (BasConstants.APPLY_TYPE_L.equals(entity.getSource())) {
				// 预售
				entity.setSealFlg(true);
				stockDetailFacade.savePresell(ctrProd, userInfo);
			} else if (BasConstants.APPLY_TYPE_A.equals(entity.getSource())) {
				// 预售采购
				entity.setSealFlg(true);
				savePresellBuy(entity, appProd, ctrProd, userInfo);
			}

			// 添加牌号
			brandService.saveBrand(appProd.getProductCd(), appProd.getBrandNumber(), appProd.getEnterpriseId());
			if (lstBuyId != null && !lstBuyId.isEmpty()) {
				Long buyContractId = lstBuyId.get(0);
				saveContractRela(buyContractId, ctrProd);
			}else{
				// 保存合同-采购销售关系
				// 两种情况：a.先采购后销售，销售时选择库存明细stockDetailId不为空。b.先销售后采购，采购时选择的是预售的库存明细，保存了销售的合同明细id
				saveContractRela(entity.getSource(), appProd, ctrProd);
			}
		}

		// 保存合同主表 优先级高，避免被上面的方法覆盖
		if (Objects.nonNull(entity.getVirtualContractId()) && StringUtils.isNotBlank(entity.getVirtualContractNo())){
			entity.setHideOut("1");
			entity.setSealFlg(true);
			entity.setStatus(BasConstants.CONTRACTSTATUS_D);
			entity.setContractStatus(BasConstants.CONTRACTSTATUS_D);
			entity.setContractStatusWx(BasConstants.CONTRACT_STATUS_O);
			entity.setDealedAmount(entity.getTotalAmount());
			entity.setWarehouseNumber(entity.getTotalNumber());
			entity.setBilledAmount(entity.getTotalAmount());
			entity.setSealDate(new Date());
		}

		String products = productsName.toString();
		if (StringUtils.isNotBlank(products)) {
			entity.setProductsName(products.substring(0, products.length() - 1));
		}
		BsCompany bsCompany = bsCompanyService.getEntity(entity.getCompanyId());
		entity.setOnLineFlg(Objects.nonNull(bsCompany) && Boolean.TRUE.equals(bsCompany.getOnLineFlg()) && StringUtils.equals(BasConstants.CONTRACTTYPE_SELL, entity.getContractType()));
		// 销售合同订立 更新公司授信金额
		if (BasConstants.CONTRACTTYPE_SELL.equals(entity.getContractType()) && Boolean.TRUE.equals(entity.getMatchCreditFlg())) {
			companyCreditFlowService.updateUsedCreditAmount(approve, entity.getCompanyCreditId(), entity.getTotalAmount(), CreditFlowEnum.AD);
		}
		dealWithContractDateSuffix(entity);
		// 更新 背靠背业务 销售合同(与采购管家用户的合同) 初始状态
		setContractStatusWx(entity);
		entity = ctrContractDao.save(entity);

		// 保存合同状态历史
		contractHisService.addHis(entity.getSource(), entity.getId(), approve,null);
		// 生成一条CtrContractApply记录
		contractApplyService.saveCtrContractApply(entity.getId(), entity.getEnterpriseId());
		// 生成关联合同记录
		contractHisService.addHis(entity,approve,lstBuyId);

		saveContractText(entity, lstProduct, matchDetail);
		refreshVirtual(entity);
		return entity;
	}

	private void dealWithContractDateSuffix(CtrContract entity){
		// 付全款日期payFullTime时间设定为23.59，便于决算
		if (entity.getPayFullTime() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(entity.getPayFullTime().getTime());
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			entity.setPayFullTime(cal.getTime());
		}
		if (entity.getDeliveryDateTo() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(entity.getDeliveryDateTo().getTime());
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			entity.setDeliveryDateTo(cal.getTime());
		}
		entity.setAppointPayFullTime(entity.getPayFullTime());
	}

	/**
	 * 更新 背靠背业务 销售合同(与采购管家用户的合同) 初始状态
	 * @param entity
	 * @return
	 */
	private void setContractStatusWx(CtrContract entity) {
		if (BasConstants.BUSINESS_TYPE_ZY_BB.equals(entity.getBusinessType())
				&& BasConstants.CONTRACT_TYPE_S.equals(entity.getContractType())) {
			entity.setContractStatusWx(BasConstants.CONTRACT_STATUS_N);
		}
	}

	/**
	 * 设置合同业务员及部门ID参数
	 *
	 * @param entity   合同
	 * @param approve
	 * @param matchDetail
	 */
	private void setDeptId(CtrContract entity, PmApprove approve, ApplyMatchDetail matchDetail) {
		Long userId = approve.getCreateUserId();
		String userName = approve.getCreateUserName();
		boolean virtualFlag = false;
		if (Objects.nonNull(matchDetail)) {
			userId = matchDetail.getMatchUserId();
			userName = matchDetail.getMatchUserName();
			virtualFlag = Objects.nonNull(matchDetail.getStockVirtualId());
			entity.setReceiptArrivedFlg(matchDetail.getReceiptArrivedFlg());
		}
		SysDeptSdk sysDept = deptUtils.getDeptByUserIdAndDeptType(virtualFlag ? approve.getCreateUserId() : userId, PmConstants.NODE_TYPE_DEPT);
		logger.info("getDeptByUserId userId:{}", userId);
		if (Objects.nonNull(sysDept)) {
			entity.setDeptId(sysDept.getDeptId());
			logger.info("getDeptByUserId userId:{}, deptId:{}, deptName:{}", userId, sysDept.getDeptId(), sysDept.getDeptName());
		}
		entity.setMatchUserId(userId);
		entity.setMatchUserName(userName);
		entity.setOwningRegion(deptUtils.getOwningRegion(sysDept));
	}


	@Override
	@ServerTransactional
	public void refreshContractText(String idStr) throws ApplicationException {
		String[] ids = idStr.split("\\|");
		for (int i = 0; i < ids.length; i++) {
			if (StringUtils.isNotBlank(ids[i])) {
				Long id = Long.parseLong(ids[i]);
				CtrContract contract = ctrContractDao.findOne(id);
				if (contract != null) {
					// 删除电子合同
					CtrContractText text = contractTextService.findByContractIdAndContractType(id,contract.getContractType());
					if (text != null) {
						contractTextService.delete(text.getId());
					}
					// 刷新合同指令
					contractTextService.saveContractText(contract);
				}
			}
		}
	}

	@Override
	@ServerTransactional
	public void deleteContract(Long contractId) throws ApplicationException {
		// 删除合同明细
		List<CtrProduct> productList = productService.findByContractId(contractId);
		if (productList != null && productList.size() > 0) {
			for (CtrProduct ctrProduct : productList) {
				productService.delete(ctrProduct.getId());
			}
		}
		// 删除合同历史
		CtrContractOphis his = contractHisService.findByCtrContractId(contractId);
		if (his != null) {
			contractHisService.delete(his.getId());
		}
		// 删除电子合同
		CtrContract contract = ctrContractDao.findOne(contractId);
		CtrContractText text = contractTextService.findByContractIdAndContractType(contractId,contract.getContractType());
		if (text != null) {
			contractTextService.delete(text.getId());
		}
		ctrContractDao.delete(contractId);
	}

	private CtrProduct saveCtrProduct(CtrContract entity, ApplyProductDetail appProd) throws ApplicationException {
		CtrProduct ctrProd = new CtrProduct();
		BeanUtils.copyProperties(appProd, ctrProd);
		ctrProd.setCtrContractId(entity.getId());
		ctrProd.setEnterpriseId(entity.getEnterpriseId());
		ctrProd.setId(0L);
		ctrProd.setQualityStandard(entity.getQualityStandard());
		ctrProd.setRemainNumber(appProd.getDealNumber());
		ctrProd.setTotalPrice(appProd.getDealNumber().multiply(appProd.getDealPrice()));
		ctrProd = productService.save(ctrProd);
		String source = entity.getSource();
		if (Objects.nonNull(entity.getWarehouseNumber())){
			ctrProd.setRemainNumber(entity.getWarehouseNumber());
			ctrProd.setWarehouseNumber(entity.getWarehouseNumber());
		}
		if (BasConstants.APPLY_TYPE_MS.equals(source) || BasConstants.APPLY_TYPE_RS.equals(source)
				|| BasConstants.APPLY_TYPE_MB.equals(source) || BasConstants.APPLY_TYPE_RB.equals(source)) {
			appProd.setCtrProductId(ctrProd.getId());
			applyProductDetailService.save(appProd);
		}
		return ctrProd;
	}

	private void saveSell(CtrContract entity, ApplyProductDetail appProd, CtrProduct ctrProd, BizUserInfor userInfo)
			throws ApplicationException {
		// 更新库存明细的合同字段
		StockDetailRequest request = StockDetailRequest.build(ctrProd);
		request.setApplyId(appProd.getApplyId());
		request.setApproveId(userInfo.getApproveId());
		request.setStockContractId(appProd.getStockContractId());
		stockDetailFacade.saveSellComplete(request);

		// 更销售合同对应的采购合同Id,同时更新采购合同对应的销售合同id
		String buyContractIds = entity.getLinkContractId();
		StockContract detail = stockContractService.getEntity(appProd.getStockContractId());
		buyContractIds = ContractCfsUtil.addContractId(buyContractIds, detail.getBuyContractId());
		CtrContract buyContract = ctrContractDao.findOne(detail.getBuyContractId());
		buyContract.setLinkContractId(ContractCfsUtil.addContractId(buyContract.getLinkContractId(), entity.getId()));
		ctrContractDao.save(buyContract);
		entity.setLinkContractId(buyContractIds);

	}

	private void saveBuy(CtrProduct ctrProd, BizUserInfor userInfo) throws ApplicationException {

		StockDetailRequest request = StockDetailRequest.build(ctrProd);
		request.setApplyType(BasConstants.APPLY_TYPE_B);
		request.setApplyId(userInfo.getApproveId());
		// 保存库存明细数据
		StockDetail detail = stockDetailFacade.saveBuy(request, userInfo);
		ctrProd.setStockContractId(detail.getStockContractId());
	}

	private void savePresellBuy(CtrContract entity, ApplyProductDetail appProd, CtrProduct ctrProd,
								BizUserInfor userInfo) throws ApplicationException {
		userInfo.setSellProductId(appProd.getCtrProductId());// 预售合同明细Id
		List<Long> sellContractIds = ContractCfsUtil.getContractId(entity.getLinkContractId());
		for (Long sellContractId : sellContractIds) {
			userInfo.setSellContractId(sellContractId);
			// 保存预售合同的关联Id
			CtrContract sellContract = ctrContractDao.findOne(sellContractId);
			sellContract.setLinkContractId(ContractCfsUtil.addContractId(sellContract.getLinkContractId(), entity.getId()));
		}
		StockDetailRequest request = StockDetailRequest.build(ctrProd);
		request.setApplyType(entity.getSource());
		request.setApplyId(userInfo.getApproveId());
		// 保存库存明细数据
		StockDetail detail = stockDetailFacade.saveBuy(request, userInfo);
		ctrProd.setStockContractId(detail.getStockContractId());
		request.setStockContractId(detail.getStockContractId());
		request.setCtrProductId(userInfo.getSellProductId());
		request.setCtrContractId(userInfo.getSellContractId());
		request.setApproveId(userInfo.getApproveId());//这里存的是预售采购流程id
		stockDetailFacade.saveSellComplete(request);
		// 更新预售商品合同库存ID
		if (userInfo.getSellProductId() != null) {
			CtrProduct sellCtrProduct = ctrProductDao.findOne(userInfo.getSellProductId());
			sellCtrProduct.setStockContractId(detail.getStockContractId());
			ctrProductDao.save(sellCtrProduct);

		}
	}

	/** 保存采购销售对应表 */
	private void saveContractRela(String source, ApplyProductDetail appProd, CtrProduct ctrProd) {
		// 保存合同-采购销售关系
		// 两种情况：a.先采购后销售，销售时选择库存明细stockDetailId不为空。b.先销售后采购，采购时选择的是预售的库存明细，保存了销售的合同明细id
		Long stockContractId = appProd.getStockContractId();
		if (BasConstants.APPLY_TYPE_S.equals(source)) {
			// 单独销售
			contractRelaService.saveCtrContractReal(stockContractId,ctrProd,ctrProd.getDealNumber());
		} else if (BasConstants.APPLY_TYPE_MS.equals(source) || BasConstants.APPLY_TYPE_RS.equals(source)) {
			// 撮合、进口代理
			contractRelaService.saveCtrContractReal(stockContractId,ctrProd,ctrProd.getDealNumber());
		} else if (BasConstants.APPLY_TYPE_A.equals(source)) {
			// 预售采购
			Long ctrProductId = appProd.getCtrProductId();
			if (ctrProductId != null) {
				contractRelaService.saveCtrContractReal2(ctrProd, ctrProductId);
			}
		}
	}

	/**
	 * 代采 白条专用
	 * @param buyContractId
	 * @param sellCtrProd
	 */
	private void saveContractRela(Long buyContractId, CtrProduct sellCtrProd) {
		contractRelaService.saveCtrContractRealMS(buyContractId, sellCtrProd.getId());
	}

	@Override
	@ServerTransactional
	public void refreshRela(String buyContractNo) {
		CtrContract contract = ctrContractDao.findByContractNo(buyContractNo);
		contractRelaService.invalidContract(contract);
		if (contract.getContractStatus().equals(BasConstants.CONTRACTSTATUS_C)) {
			//合同已作废
			return;
		}
		List<StockContract> lstStock = stockContractDao.findByBuyContractId(contract.getId());
		lstStock.forEach(sc -> {
			List<StockContractRela> lstRela = stockContractRelaDao
					.findByRelaTypeAndStockContractId(StockContractRela.RELATYPE_SELL, sc.getId());
			lstRela.forEach(scr -> {
				if (scr.getCtrProductId() != null && scr.getCtrProductId() > 0) {
					CtrProduct pSell = ctrProductDao.findOne(scr.getCtrProductId());
					logger.info("scId:{},pSell:{}", sc.getId(), pSell.getId());
					contractRelaService.saveCtrContractReal(sc.getId(), pSell, scr.getRelaNum());
				} else {
					logger.info("contractId:{},scId:{},ctrProductId is null", contract.getId(), sc.getId());
				}
			});
		});
	}


	private void refreshVirtual(CtrContract entity) {
		SCHEDULED_POOL.schedule(() -> {
			try {
				if (Objects.nonNull(entity.getVirtualContractId())){
					CtrContract targetContract = ctrContractDao.findOne(entity.getVirtualContractId());
					ctrContractUpdateService.refreshVirtualContract(targetContract);
				}
			}catch (Exception e){
				logger.error("contractNo:{}", entity.getContractNo());
				logger.error("refreshVirtual error", e);
			}
		},10, TimeUnit.SECONDS);
	}

	private void saveContractText(CtrContract entity, List<CtrProduct> lstProduct, ApplyMatchDetail matchDetails) {
		threadPool.execute(() -> {
			// 生成电子合同
			try {
				contractTextService.saveContractText(entity, lstProduct, matchDetails);

			} catch (ApplicationException e) {
				logger.error("生成电子合同异常", e);
			}
		});

	}

	private void genProductsName(StringBuilder productsName, CtrProduct ctrProd) {
		// 化工行业货名：品名/仓库/单价
		if (EnterpriseCache.isHGIndustry(ctrProd.getEnterpriseId())) {
			if (StringUtils.isNotBlank(ctrProd.getProductName())) {
				productsName.append(ctrProd.getProductName()).append("/");
			}
			if (StringUtils.isNotBlank(ctrProd.getWarehouseName())) {
				productsName.append(ctrProd.getWarehouseName()).append("/");
			}
			productsName.append(ctrProd.getDealPrice()).append(",");
		} else {
			// 塑料行业
			if(StringUtils.isNotBlank(ctrProd.getProductName())){
				productsName.append(ctrProd.getProductName()).append("/");
			}
			if (StringUtils.isNotBlank(ctrProd.getBrandNumber())){
				productsName.append(ctrProd.getBrandNumber()).append("/");
				if (StringUtils.isNotBlank(ctrProd.getFactoryName())) {
					productsName.append(ctrProd.getFactoryName()).append(",");
				}
			} else {
				if(StringUtils.isNotBlank(ctrProd.getWarehouseName())){
					productsName.append(ctrProd.getWarehouseName()).append(",");
				}
			}
		}
	}

	/**
	 * 更新账期条款赊销合同的付全款时间
	 */
	@Override
	@ServerTransactional
	public void refrshPayFullTime(Long contractId) {
		try {
			CtrContract contract = ctrContractDao.findOne(contractId);
			CtrContractApply contractApply = contractApplyService.findByContractId(contractId);
			ApplySell applySell = applySellService.findByContractId(contractId);
			BigDecimal confirmReceiveNumber = contract.getConfirmReceiveNumber();//确认收货数量
			BigDecimal totalNumber = contract.getTotalNumber();//合同总数量
			Date confirmDate = contract.getConfirmDate();//确认收货时间
			BigDecimal warehouseNumber = contract.getWarehouseNumber();//已出/入库数量
			String businessType = contract.getBusinessType();//业务类型
			Date payFullTime = contract.getPayFullTime();//付全款时间
			Date realWarehoseDate = contractApply.getRealWarehoseDate();//最后出库时间
			String payModeCode = applySell.getPayModeCode();
			Integer creditDays = applySell.getCreditDays();
			Date full_time = null;
			//已全部出库且付全款时间为空的赊销合同
			if (warehouseNumber.compareTo(totalNumber) == 0 && payFullTime == null
					&& StringUtils.equals(BasConstants.BUSINESS_TYPE_SX_SX, businessType)
					&& StringUtils.equals(BasConstants.DELIVERY_MODE_SX, payModeCode)) {
				// 判断是否已全部确认收货
				Date addDays = DateOperator.addDays(realWarehoseDate, 3);
				if (confirmReceiveNumber.compareTo(totalNumber) == 0) {
					if (confirmDate.after(addDays)) {
						full_time = DateOperator.addDays(realWarehoseDate, creditDays);
					}else {
						full_time = DateOperator.addDays(confirmDate, creditDays);
					}
				} else if(new Date().after(addDays)){
					full_time = DateOperator.addDays(realWarehoseDate, creditDays);
				}
			}
			if (full_time != null) {
				logger.info("payFullTime=="+full_time);
				contract.setPayFullTime(full_time);
				ctrContractDao.save(contract);
			}
		} catch (Exception e) {
			logger.error("更新账期条款赊销合同异常,ctrContractId:"+contractId);
		}
	}
}
