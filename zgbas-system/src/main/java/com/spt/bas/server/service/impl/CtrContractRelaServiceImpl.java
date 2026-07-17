package com.spt.bas.server.service.impl;

import com.google.common.base.Splitter;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.util.ContractCfsUtil;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.CtrContractRelaDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 保存合同-采购销售关系
 * 两种情况：a.先采购后销售，销售时选择库存明细stockDetailId不为空。b.先销售后采购，采购时选择的是预售的库存明细，保存了销售的合同明细id
 *
 * @author wlddh
 *
 */
@Component
@Transactional(readOnly = true)
public class CtrContractRelaServiceImpl extends BaseService<CtrContractRela> implements ICtrContractRelaService {
	@Autowired
	private CtrContractRelaDao ctrContractRelaDao;
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private IStockContractService stockContractService;
	@Autowired
	private ICtrProductService productService;
	@Autowired
	private ICtrContractSettlementService ctrContractSettlementService;
	@Autowired
	private IBudgetSettlementService budgetSettlementService;
	@Autowired
	private ICtrContractOphisService ctrContractOphisService;

	@Override
	public BaseDao<CtrContractRela> getBaseDao() {
		return ctrContractRelaDao;
	}

	@Override
	public Class<CtrContractRela> getEntityClazz() {
		return CtrContractRela.class;
	}

	/**a.先采购后销售，销售时选择库存明细stockDetailId不为空*/
	@Override
	@ServerTransactional
	public void saveCtrContractReal(Long stockContractId,CtrProduct sellProduct,BigDecimal dealNumber) {
		// 销售合同
		CtrContract sellContract = ctrContractDao.findOne(sellProduct.getCtrContractId());
		// 该条销售明细对应的合同库存
		if (stockContractId==null) {
			stockContractId = sellProduct.getStockContractId();
		}
		StockContract stockContract = stockContractService.getEntity(stockContractId);
//		StockDetail stockDetail = stockDetailService.getEntity(stockDetailId);
		// 对应采购合同
		CtrContract buyContract = ctrContractDao.findOne(Long.valueOf(stockContract.getBuyContractId()));
		// 确定该销售合同明细对应的采购合同的明细
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("EQS_productCd", sellProduct.getProductCd());
//		params.put("EQL_factoryId", sellProduct.getFactoryId());
//		params.put("EQS_brandNumber", sellProduct.getBrandNumber());
//		List<Long> lstSdId = new ArrayList<>();
//		lstSdId.add(stockDetailId);
//		if (stockDetail.getLinkStockDetailId() != null) {
//			lstSdId.add(stockDetail.getLinkStockDetailId());
//		}
//		params.put("INL_stockDetailId", lstSdId);
//		params.put("EQL_ctrContractId", buyContract.getId());
		// 若为现货销售此处不能唯一确定该销售明细的来源（同一合同存在多条相同品名牌号厂商情况下），未解决
		// if(sellProduct.getProductAttr().equals(BasConstants.STOCK_PRODUCT_ATTR_P)){
		// params.put("EQS_warehouseName", stockDetail.getWarehouseName());
		// }
//		List<CtrProduct> lstBuy = productService.findEntityByParam(params);
//		StockContractRela rela = stockContractRelaService.findSellByCtrProductId(sellProduct.getId());
//		if (rela!=null) {
			CtrProduct buyProduct = productService.getEntity(stockContract.getBuyProductId());
			CtrContractRela rela = new CtrContractRela();
			BeanUtils.copyProperties(sellProduct, rela);
			rela.setId(0L);
			rela.setCreatedDate(null);
			rela.setUpdatedDate(null);
			rela.setDealNumber(dealNumber);
			rela.setBuyContractId(buyContract.getId());
			rela.setBuyProductId(buyProduct.getId());
			rela.setBuyPrice(buyProduct.getDealPrice());
			rela.setBuyUserId(buyContract.getMatchUserId());
			rela.setBuyUserName(buyContract.getMatchUserName());
			rela.setBuyDate(buyContract.getContractTime());
			rela.setBuyCompanyId(buyContract.getCompanyId());
			rela.setBuyCompanyName(buyContract.getCompanyName());
			rela.setSellContractId(sellContract.getId());
			rela.setSellDate(sellContract.getContractTime());
			rela.setSellPrice(sellProduct.getDealPrice());
			rela.setSellProductId(sellProduct.getId());
			rela.setSellUserId(sellContract.getMatchUserId());
			rela.setSellUserName(sellContract.getMatchUserName());
			rela.setSellCompanyId(sellContract.getCompanyId());
			rela.setSellCompanyName(sellContract.getCompanyName());
			rela.setStockContractId(stockContractId);
			ctrContractRelaDao.save(rela);
//		}
		ctrContractSettlementService.saveSettlement(buyProduct, sellProduct);
        budgetSettlementService.saveSettlement(buyProduct, sellProduct);

	}

	/**
	 * 自营销售出库后自动关联
	 *
	 * @param productDetailList
	 * @param sellContract
	 */
	@Override
	@ServerTransactional
	public void saveCtrContractReal3(PmApprove pmApprove, List<ApplyProductDetail> productDetailList, CtrContract sellContract) {
		if (!StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_XS, sellContract.getBusinessType())) {
			return;
		}
		List<CtrContractRela> contractReal = ctrContractRelaDao.findBySellContractId(sellContract.getId());
		if (CollectionUtils.isNotEmpty(contractReal)) {
			BigDecimal realNumber = contractReal.stream().map(CtrContractRela::getDealNumber).reduce(BigDecimal.ZERO, BigDecimal::add);
			if (realNumber.compareTo(sellContract.getTotalNumber()) >= 0) {
				return;
			}
		}
		productDetailList = productDetailList.stream().filter(r -> Objects.nonNull(r.getStockContractId())).collect(Collectors.toList());

		for (ApplyProductDetail productDetail : productDetailList) {
			StockContract stockContract = stockContractService.getEntity(productDetail.getStockContractId());
			CtrContract buyContract = ctrContractDao.findOne(stockContract.getBuyContractId());
			CtrProduct sellProduct = productService.getEntity(productDetail.getCtrProductId());
			CtrProduct buyProduct = productService.getEntity(stockContract.getBuyProductId());

			CtrContractRela rela = new CtrContractRela();
			BeanUtils.copyProperties(sellProduct, rela);
			rela.setId(0L);
			rela.setCreatedDate(null);
			rela.setUpdatedDate(null);
			rela.setDealNumber(productDetail.getCurNumber());
			rela.setBuyContractId(buyContract.getId());
			rela.setBuyProductId(buyProduct.getId());
			rela.setBuyPrice(buyProduct.getDealPrice());
			rela.setBuyUserId(buyContract.getMatchUserId());
			rela.setBuyUserName(buyContract.getMatchUserName());
			rela.setBuyDate(buyContract.getContractTime());
			rela.setBuyCompanyId(buyContract.getCompanyId());
			rela.setBuyCompanyName(buyContract.getCompanyName());
			rela.setSellContractId(sellContract.getId());
			rela.setSellDate(sellContract.getContractTime());
			rela.setSellPrice(sellProduct.getDealPrice());
			rela.setSellProductId(sellProduct.getId());
			rela.setSellUserId(sellContract.getMatchUserId());
			rela.setSellUserName(sellContract.getMatchUserName());
			rela.setSellCompanyId(sellContract.getCompanyId());
			rela.setSellCompanyName(sellContract.getCompanyName());
			rela.setStockContractId(buyProduct.getStockContractId());
			ctrContractRelaDao.save(rela);

			String sellIds = ContractCfsUtil.addContractId(buyContract.getLinkContractId(), sellContract.getId());
			buyContract.setLinkContractId(sellIds);

			String buyIds = ContractCfsUtil.addContractId(sellContract.getLinkContractId(), buyContract.getId());
			sellContract.setLinkContractId(buyIds);

			ctrContractDao.save(buyContract);
			ctrContractDao.save(sellContract);

			List<String> buyIdStrList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(buyIds);
			List<Long> buyIdList = buyIdStrList.stream().map(Long::valueOf).collect(Collectors.toList());
			ctrContractOphisService.addHis(sellContract, pmApprove, buyIdList);
		}
	}


	@Override
	@ServerTransactional
	public void saveCtrContractRealMS(Long buyContractId,Long sellProductId) {
		//采购合同
		CtrContract buyContract = ctrContractDao.findOne(buyContractId);
		CtrProduct buyProduct;
		List<CtrProduct> byContractId = productService.findByContractId(buyContractId);
		if (!byContractId.isEmpty()) {
			buyProduct = byContractId.get(0);
			//对应销售合同
			CtrProduct sellProduct = productService.getEntity(sellProductId);
			CtrContract sellContract = ctrContractDao.findOne(sellProduct.getCtrContractId());

			CtrContractRela rela = new CtrContractRela();

			BeanUtils.copyProperties(sellProduct, rela);
			rela.setId(0L);
			rela.setCreatedDate(null);
			rela.setUpdatedDate(null);

			rela.setDealNumber(buyProduct.getDealNumber());
			rela.setBuyContractId(buyContract.getId());
			rela.setBuyProductId(buyProduct.getId());
			rela.setBuyPrice(buyProduct.getDealPrice());
			rela.setBuyUserId(buyContract.getMatchUserId());
			rela.setBuyUserName(buyContract.getMatchUserName());
			rela.setBuyDate(buyContract.getContractTime());
			rela.setBuyCompanyId(buyContract.getCompanyId());
			rela.setBuyCompanyName(buyContract.getCompanyName());
			rela.setSellContractId(sellContract.getId());
			rela.setSellDate(sellContract.getContractTime());
			rela.setSellPrice(sellProduct.getDealPrice());
			rela.setSellProductId(sellProduct.getId());
			rela.setSellUserId(sellContract.getMatchUserId());
			rela.setSellUserName(sellContract.getMatchUserName());
			rela.setSellCompanyId(sellContract.getCompanyId());
			rela.setSellCompanyName(sellContract.getCompanyName());
			rela.setStockContractId(buyProduct.getStockContractId());
			ctrContractRelaDao.save(rela);
			ctrContractSettlementService.saveSettlement(buyProduct, sellProduct);
			budgetSettlementService.saveSettlement(buyProduct, sellProduct);
		}
	}

	/** b.先销售后采购，采购时选择的是预售的库存明细，保存了销售的合同明细id */
	@Override
	@ServerTransactional
	public void saveCtrContractReal2(CtrProduct buyProduct,Long sellProductId){
		//采购合同
		CtrContract buyContract = ctrContractDao.findOne(buyProduct.getCtrContractId());

//		StockDetailPresell presell = stockDetailPresellService.findByCtrProductId(sellProductId);
		//对应销售合同
		CtrProduct sellProduct = productService.getEntity(sellProductId);
		CtrContract sellContract = ctrContractDao.findOne(sellProduct.getCtrContractId());


		CtrContractRela rela = new CtrContractRela();

		BeanUtils.copyProperties(sellProduct, rela);
		rela.setId(0L);
		rela.setCreatedDate(null);
		rela.setUpdatedDate(null);

		rela.setDealNumber(buyProduct.getDealNumber());
		rela.setBuyContractId(buyContract.getId());
		rela.setBuyProductId(buyProduct.getId());
		rela.setBuyPrice(buyProduct.getDealPrice());
		rela.setBuyUserId(buyContract.getMatchUserId());
		rela.setBuyUserName(buyContract.getMatchUserName());
		rela.setBuyDate(buyContract.getContractTime());
		rela.setBuyCompanyId(buyContract.getCompanyId());
		rela.setBuyCompanyName(buyContract.getCompanyName());
		rela.setSellContractId(sellContract.getId());
		rela.setSellDate(sellContract.getContractTime());
		rela.setSellPrice(sellProduct.getDealPrice());
		rela.setSellProductId(sellProduct.getId());
		rela.setSellUserId(sellContract.getMatchUserId());
		rela.setSellUserName(sellContract.getMatchUserName());
		rela.setSellCompanyId(sellContract.getCompanyId());
		rela.setSellCompanyName(sellContract.getCompanyName());
		rela.setStockContractId(buyProduct.getStockContractId());
		ctrContractRelaDao.save(rela);
		ctrContractSettlementService.saveSettlement(buyProduct, sellProduct);
		budgetSettlementService.saveSettlement(buyProduct, sellProduct);
	}

	/** 合同作废 */
	@Override
	@ServerTransactional
	public void invalidContract(CtrContract contract) {
		if (BasConstants.CONTRACT_TYPE_B.equals(contract.getContractType())) {
			ctrContractRelaDao.deleteByBuyContractId(contract.getId());
		} else {
			ctrContractRelaDao.deleteBySellContractId(contract.getId());
		}

	}
	/**根据采购合同id，查对应的销售合同；根据销售合同id，查对应的采购合同*/
	@Override
	public Long countRela(Long contractId,String contractType) {
		Long cnt= 0L;
		if (BasConstants.CONTRACT_TYPE_B.equals(contractType)) {
			cnt = ctrContractRelaDao.countByBuyContractId(contractId);
		}else {
			cnt = ctrContractRelaDao.countBySellContractId(contractId);
		}
		return cnt;
	}

	/**
	 * 通过sellContractId获取合同关联关系
	 *
	 * @param contractId
	 * @return
	 */
	@Override
	public CtrContractRela getRelaBySellContractId(Long contractId) {
		List<CtrContractRela> rela = ctrContractRelaDao.findBySellContractId(contractId);
		if (!rela.isEmpty()) {
			return rela.get(0);
		}
		return null;
	}
}
