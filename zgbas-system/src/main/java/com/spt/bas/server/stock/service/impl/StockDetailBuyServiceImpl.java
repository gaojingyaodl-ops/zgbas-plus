package com.spt.bas.server.stock.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.entity.StockContractRela;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.entity.StockDetailPresell;
import com.spt.bas.client.vo.BizUserInfor;
import com.spt.bas.client.vo.CtrConctractInvalidVo;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.StockDetailDao;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.service.IStockDetailHisService;
import com.spt.bas.server.service.IStockDetailPresellService;
import com.spt.bas.server.stock.service.IStockContractRelaService;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.bas.server.stock.service.IStockDetailBuyService;
import com.spt.bas.server.util.StringUtility;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.tools.core.exception.ApplicationException;

/** 采购 */
@Component
@Transactional(readOnly = true)
public class StockDetailBuyServiceImpl implements IStockDetailBuyService {
	@Autowired
	private StockDetailDao stockDetailDao;
	@Autowired
	private IStockDetailHisService stockDetailHisService;
	@Autowired
	private IBsKeySequenceService keySequenceService;
	@Autowired
	private ICtrContractService contractService;
	@Autowired
	private IStockContractService stockContractService;
	@Autowired
	private IStockDetailPresellService stockDetailPresellService;
	@Autowired
	private IStockContractRelaService stockContractRelaService;
	@Override
	@ServerTransactional
	public StockDetail saveBuy(StockDetailRequest request, BizUserInfor userInfor) throws ApplicationException {
		request.setBizUserId(userInfor.getBizUserId());
		request.setBizUserName(userInfor.getBizUserName());
		StockDetail detail = new StockDetail();
		detail.setBusinessNo(keySequenceService.getNextKey(BasConstants.KEYSEQUENCE_CATEGORY_STOCKDETAILNO,
				request.getEnterpriseId()));
		BeanUtils.copyProperties(request, detail);

		// 保存库存明细来源
		detail.setSource(request.getApplyType());

		BigDecimal dealNumber = request.getDealNumber();
		detail.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_P);// 采购合同，默认在途库存
		detail.setDeliveryInNumber(BigDecimal.ZERO);// 采购合同，默认入库数量0
		detail.setBuyContractId(StringUtility.o2s(request.getCtrContractId()));
//		detail.setStockId(request.getStockId());
		detail.setEnterpriseId(request.getEnterpriseId());
		detail.setId(null);
		detail.setBizUserId(userInfor.getBizUserId());
		detail.setBizUserName(userInfor.getBizUserName());
		detail.setWrapSpecs(request.getWrapSpecs());
		detail.setWarehousePos(request.getWarehousePos());
		detail.setWarehouseAddr(request.getWarehouseAddr());
		// 采购合同一生成，库存类型默认是期货库存
		detail.setStockType(BasConstants.DICT_TYPE_STOCKTYPE_QH);

		CtrContract entity = contractService.getEntity(request.getCtrContractId());
		if (entity != null) {
			detail.setBuyCompanyId(entity.getCompanyId());
			detail.setBuyCompanyName(entity.getCompanyName());
		}
		detail = stockDetailDao.save(detail);
		if (request.getApplyType().equals(BasConstants.APPLY_TYPE_B)) {
//			detail.setFrozenNumber(BigDecimal.ZERO);// 采购合同，默认冻结数量0，可用数量=采购数量
			detail.setAvailableNumber(dealNumber);
		} else if (request.getApplyType().equals(BasConstants.APPLY_TYPE_MB)
				|| request.getApplyType().equals(BasConstants.APPLY_TYPE_RB)) {
//			detail.setFrozenNumber(dealNumber);// 撮合采购/进口采购，默认冻结数量=采购数量，冻结数量=0
			detail.setAvailableNumber(dealNumber);
		} else {// 预售采购
//			detail.setFrozenNumber(dealNumber);
			detail.setAvailableNumber(dealNumber);
//			detail.setSellContractId(ContractCfsUtil.addContractId(detail.getSellContractId(), request.getSellContractId()));
//			detail.setSellContractCfs(ContractCfsUtil.addContractCfs(detail.getSellContractCfs(),String.valueOf(request.getSellContractId()), dealNumber));
			// 添加一条销售流水
			stockDetailHisService.saveSell(request, detail);
		}
		detail.setStockStatus(request.getApplyType());
		// 保存库存明细流水
		stockDetailHisService.saveBuy(detail, request);
		StockContract sc = stockContractService.saveBuy(request);
		detail.setStockContractId(sc.getId());
		if (BasConstants.APPLY_TYPE_A.equals(request.getApplyType())) {
			// 预售采购，更新预售库存明细
			stockDetailPresellService.updatePresellDetail(dealNumber, userInfor.getSellProductId());
		}
		
		return detail;
	}

	public void cancelBuyProduct(CtrProduct product) throws ApplicationException {
		cancelBuyContract(product, true);
	}

	public void cancelDeliveryInAndBuyProduct(CtrProduct product) throws ApplicationException {
		BigDecimal dealNumber = product.getDealNumber();
		List<StockDetail> detailList = stockDetailDao.findByBuyContractId(product.getCtrContractId() + "");
		if (detailList != null && detailList.size() > 0) {
			for (StockDetail detail : detailList) {
				long cntSell = stockContractRelaService.countRela(detail.getStockContractId(), StockContractRela.RELATYPE_SELL);
				if (cntSell > 0) {
					throw new ApplicationException("该采购已存在销售申请");
				}
				BigDecimal availableNumber = detail.getAvailableNumber();
				if (availableNumber.compareTo(dealNumber) >= 0) {
					BigDecimal avail = availableNumber.subtract(dealNumber);
					detail.setAvailableNumber(avail);
					detail.setDeliveryInNumber(avail);
					dealNumber = BigDecimal.ZERO;

				} else {
					dealNumber = dealNumber.subtract(availableNumber);
					detail.setAvailableNumber(BigDecimal.ZERO);
					detail.setDeliveryInNumber(BigDecimal.ZERO);
				}
				stockDetailDao.save(detail);
				// 更新历史数据
				this.stockDetailHisService.insertHisByCancel(detail, dealNumber, product, BasConstants.APPLY_TYPE_B);
				if (dealNumber.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}
		}
		stockContractService.cancelBuy(product);
	}

	private void cancelBuyContract(CtrProduct product, boolean valida) throws ApplicationException {
		BigDecimal dealNumber = product.getDealNumber();
		List<StockDetail> detailList = stockDetailDao.findByBuyContractId(product.getCtrContractId() + "");
		
		//预售采购作废,需还原预售库存明细的已采购数量
		StockContractRela rela = stockContractRelaService.findSellByStockContractId(product.getStockContractId());
		if (rela != null) {
			Long sellCtrProductId = rela.getCtrProductId();
			StockDetailPresell stockDetailPresell = stockDetailPresellService.findByCtrProductId(sellCtrProductId);
			if (stockDetailPresell != null) {
				BigDecimal buyedNumber = stockDetailPresell.getBuyedNumber();
				BigDecimal realBuyedNumber = buyedNumber.subtract(dealNumber);
				stockDetailPresell.setBuyedNumber(realBuyedNumber);
				stockDetailPresellService.save(stockDetailPresell);
			}
		}
		
		if (detailList != null && detailList.size() > 0) {
			for (StockDetail detail : detailList) {
				if (valida) {
					long cntSell = stockContractRelaService.countRela(detail.getStockContractId(), StockContractRela.RELATYPE_SELL);
					if (cntSell > 0) {
						throw new ApplicationException("该采购已存在销售申请");
					} else if (detail.getDeliveryInNumber().compareTo(BigDecimal.ZERO) > 0) {
						throw new ApplicationException("该采购已存在入库申请");
					}
				}
				BigDecimal availableNumber = detail.getAvailableNumber();
				if (availableNumber.compareTo(dealNumber) >= 0) {
					detail.setAvailableNumber(availableNumber.subtract(dealNumber));
					dealNumber = BigDecimal.ZERO;
				} else {
					dealNumber = dealNumber.subtract(availableNumber);
					detail.setAvailableNumber(BigDecimal.ZERO);
				}

				stockDetailDao.save(detail);
				// 更新历史数据
				this.stockDetailHisService.insertHisByCancel(detail, dealNumber, product, BasConstants.APPLY_TYPE_B);

				if (dealNumber.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}

			}
		}
		stockContractService.cancelBuy(product);
	}

	@Override
	public void cancelPreBuyContract(CtrProduct product, CtrConctractInvalidVo vo) throws ApplicationException {
		cancelBuyContract(product, false);
	}

}
