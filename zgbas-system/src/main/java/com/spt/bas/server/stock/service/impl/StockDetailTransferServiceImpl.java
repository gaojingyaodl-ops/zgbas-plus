package com.spt.bas.server.stock.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.entity.StockContractRela;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.util.ContractCfsUtil;
import com.spt.bas.client.vo.ContractCfs;
import com.spt.bas.server.dao.CtrContractRelaDao;
import com.spt.bas.server.dao.StockContractDao;
import com.spt.bas.server.dao.StockContractRelaDao;
import com.spt.bas.server.service.ICtrContractRelaService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.service.ICtrProductService;
import com.spt.bas.server.service.IStockDetailService;
import com.spt.bas.server.stock.service.IStockContractRelaService;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.bas.server.stock.service.IStockDetailTransferService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;

/**
 * 库存明细数据迁移
 * 
 * @author wlddh
 *
 */
@Component
public class StockDetailTransferServiceImpl implements IStockDetailTransferService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private ICtrContractService ctrContractService;
	@Autowired
	private ICtrProductService ctrProductService;
	@Autowired
	private IStockContractService stockContractService;
	@Autowired
	private IStockContractRelaService stockContractRelaService;
	@Autowired
	private CtrContractRelaDao ctrContractRelaDao;
	@Autowired
	private IStockDetailService stockDetailService;
	@Autowired
	private StockContractRelaDao stockContractRelaDao;
	@Autowired
	private StockContractDao stockContractDao;
	@Autowired
	private ICtrContractRelaService contractRelaService;
	
	@Override
	@ServiceTransactional
	public void clean(StockDetail detail) throws ApplicationException {
		stockContractDao.deleteById(detail.getStockContractId());
//		stockContractDao.deleteByBuyProductId(detail.getCtrProductId());
		//删除合同库存关联数据
		List<StockContractRela> lst = stockContractRelaDao.findByStockContractId(detail.getStockContractId());
//		List<StockContractRela> lst = stockContractRelaDao.findByRelaTypeAndCtrProductId(StockContractRela.RELATYPE_SELL,detail.getStockContractId());
		lst.forEach(r -> {
			stockContractRelaDao.delete(r);
		});
		detail.setStockContractId(null);
		stockDetailService.save(detail);
	}
	
	@Override
	@ServiceTransactional
	public void cleanByContractId(Long contractId) throws ApplicationException {
		List<StockContract> lstStock =  stockContractDao.findByBuyContractId(contractId);
		lstStock.forEach(s->{
			stockContractRelaDao.deleteByStockContractId(s.getId());
			stockContractDao.deleteById(s.getId());
		});
	}
	
	
	/**刷新合同库存关联表*/
	@Override
	@ServiceTransactional
	public void refreshRela(StockDetail detail) throws ApplicationException {
		if (StringUtils.isBlank(detail.getSellContractCfs())) {
			logger.info("sellContractCfs is null, detailId:{}", detail.getId());
			return;
		}
		if (detail.getAvailableNumber().compareTo(BigDecimal.ZERO) == 0
				&& detail.getFrozenNumber().compareTo(BigDecimal.ZERO) == 0
				&& detail.getDeliveryOutNumber().compareTo(BigDecimal.ZERO) == 0) {
			// 3个数量都=0 ，代表无效库存
			return;
		}
		StockContract  sc = addStockContract(detail);
		Long scId = sc.getId();
		//查询或新增合同库存
//		if (detail.getStockContractId() == null || detail.getStockContractId() == 0L) {
//			sc = addStockContract(detail);
//		} else {
//			sc = stockContractService.getEntity(detail.getStockContractId());
//			if (sc == null) {
//				sc = addStockContract(detail);
//			}
//		}
		detail.setStockContractId(scId);
		stockDetailService.save(detail);
		//删除合同库存关联数据
//		List<StockContractRela> lst= stockContractRelaDao.findByStockContractId(sc.getId());
//		lst.forEach(r->{
//			stockContractRelaDao.delete(r);
//		});
		//重新生成合同库存关联数据
		List<StockContractRela> lstRela = addStockContractRela(detail, sc);
		logger.info("stockDetailId:{},scId:{} ", detail.getId(), scId);
		CtrProduct product = ctrProductService.getEntity(detail.getCtrProductId());
		if (product != null) {
			logger.info("scId:{},productId:{} ", scId, product.getId());
			product.setStockContractId(scId);
			ctrProductService.save(product);
		}
		//删除合同关联数据
		Long buyContractId = Long.valueOf(detail.getBuyContractId());
		ctrContractRelaDao.deleteByBuyContractId(buyContractId);
		//重新生成合同关联数据
		lstRela.forEach(scr -> {
			if (scr.getCtrProductId() != null && scr.getCtrProductId() > 0) {
				CtrProduct pSell = ctrProductService.getEntity(scr.getCtrProductId());
				logger.info("scId:{},pSell:{}", scId, pSell.getId());
				contractRelaService.saveCtrContractReal(scId,pSell,scr.getRelaNum());
			} else {
				logger.info("contractId:{},scId:{},ctrProductId is null", buyContractId, scId);
			}
		});
	}

	private List<StockContractRela> addStockContractRela(StockDetail detail, StockContract  sc) throws ApplicationException {
		BigDecimal sellNum = BigDecimal.ZERO;
		List<ContractCfs> lstCfs = ContractCfsUtil.converList(detail.getSellContractCfs());
		logger.info("cfs:{},lstCfs:{}",detail.getSellContractCfs(),JsonUtil.obj2Json(lstCfs));
		Map<Long, StockContractRela> map =new HashMap<>();
		for (ContractCfs cfs : lstCfs) {
			StockContractRela relaVo = new StockContractRela();
			Map<String, Object> queryParams = new HashMap<>();
			Long contractId = Long.valueOf(cfs.getContractId());
			queryParams.put("EQL_ctrContractId", contractId);
			queryParams.put("EQS_productCd", detail.getProductCd());
			queryParams.put("EQS_brandNumber", detail.getBrandNumber());
			queryParams.put("EQL_factoryId", detail.getFactoryId());
			List<CtrProduct> lstProduct = ctrProductService.findEntityByParam(queryParams);
			CtrProduct product = null;
			if (lstProduct.size()==1) {
				product = lstProduct.get(0);
			}else if (lstProduct.size() > 0) {
				List<CtrProduct> lstDealNumber = new ArrayList<>();
				for(CtrProduct p :lstProduct) {
					if (p.getDealNumber().equals(cfs.getDealNumber())) {
						lstDealNumber.add(p);
					}
				}
				if (lstDealNumber.size() == 1) {
					product = lstDealNumber.get(0);
				} else {
					product = lstProduct.stream().filter(p -> p.getStockContractId() == null).findFirst().orElse(null);
					if (product == null) {
						product = lstProduct.get(0);
					}
				}
			}
			logger.info("stockContractId:{},lstProduct:{},product:{}",sc.getId(),lstProduct.size(),JsonUtil.obj2Json(product));
			CtrContract ctr = ctrContractService.getEntity(Long.valueOf(cfs.getContractId()));
			relaVo.setApproveId(ctr.getApproveId());
			relaVo.setContractId(Long.valueOf(cfs.getContractId()));
			if (product != null) {
				relaVo.setCtrProductId(product.getId());
			}
			relaVo.setEnterpriseId(detail.getEnterpriseId());
			relaVo.setRelaNum(cfs.getDealNumber());
			relaVo.setRelaType(StockContractRela.RELATYPE_SELL);
			relaVo.setStockContractId(sc.getId());
			relaVo.setCreatedDate(detail.getCreatedDate());
			relaVo = stockContractRelaService.saveDetailRela(relaVo);
			map.put(relaVo.getId(), relaVo);
			if (product != null) {
				product.setStockContractId(sc.getId());
				ctrProductService.save(product);
			}
			sellNum = sellNum.add(cfs.getDealNumber());
		}
		
		sc.setSellNumber(sellNum);
		stockContractService.save(sc);
		
		List<StockContractRela> lstRela =new ArrayList<>();
		lstRela.addAll(map.values());
		return lstRela;

	}

	private StockContract addStockContract(StockDetail detail) throws ApplicationException {
		StockContract sc = stockContractDao.findByBuyProductId(detail.getCtrProductId());
		BigDecimal detailSellNumber = detail.getFrozenNumber().add(detail.getDeliveryOutNumber());
		if (sc != null) {
			sc.setBuyNumber(sc.getBuyNumber().add(detail.getAvailableNumber()));
			sc.setSellNumber(sc.getSellNumber().add(detailSellNumber));
			sc.setDeliveryOutNumber(sc.getDeliveryOutNumber().add(detail.getDeliveryOutNumber()));
			sc.setDeliveryInNumber(sc.getDeliveryInNumber().add(detail.getDeliveryInNumber()));
		} else {
			sc = new StockContract();
			sc.setBrandNumber(detail.getBrandNumber());
			sc.setBuyContractId(Long.valueOf(detail.getBuyContractId()));
			sc.setBuyNumber(detail.getAvailableNumber());
			sc.setBuyProductId(detail.getCtrProductId());
			sc.setDealPrice(detail.getDealPrice());
			sc.setEnterpriseId(detail.getEnterpriseId());
			sc.setFactoryId(detail.getFactoryId());
			sc.setFactoryName(detail.getFactoryName());
			sc.setProductCd(detail.getProductCd());
			sc.setProductName(detail.getProductName());
			sc.setWarehouseId(detail.getWarehouseId());
			sc.setWarehouseName(detail.getWarehouseName());
			sc.setBizUserId(detail.getBizUserId());
			sc.setBizUserName(detail.getBizUserName());
			sc.setSellNumber(detailSellNumber);
			sc.setDeliveryInNumber(detail.getDeliveryInNumber());
			sc.setDeliveryOutNumber(detail.getDeliveryOutNumber());
			sc.setCreatedDate(detail.getCreatedDate());
		}
		return stockContractService.save(sc);
	}
}
