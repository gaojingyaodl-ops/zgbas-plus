package com.spt.bas.server.stock.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.StockDetailPresell;
import com.spt.bas.client.vo.BizUserInfor;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.service.IStockDetailPresellService;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.bas.server.stock.service.IStockDetailSellService;
import com.spt.tools.core.exception.ApplicationException;

/** 销售 */
@Component
@Transactional(readOnly = true)
public class StockDetailSellServiceImpl implements IStockDetailSellService {
	private Logger logger = LoggerFactory.getLogger(getClass());
//	@Autowired
//	private StockDetailDao stockDetailDao;
//	@Autowired
//	private IStockDetailHisService stockDetailHisService;
//	@Autowired
//	private IStockDetailRelaService stockDetailRelaService;
	@Autowired
	private IStockContractService stockContractService;
	@Autowired
	private IStockDetailPresellService stockDetailPresellService;
	
	/**
	 * <p>
	 * 销售：可用数量改成冻结数量
	 * </p>
	 * <p>
	 * 发起时，冻结数量，驳回时，减掉冻结数量
	 * </p>
	 */
	@ServerTransactional
	public void saveSell(StockDetailRequest request) throws ApplicationException {
		// 查询销售合同
//		List<StockDetail> detailList = find4Sell(request);
		if (!request.isBack()) {
			// 正常销售
			sell(request);
		} else {
			// 销售驳回，这里不是作废
			sellBack(request);
		}

	}

	private void sell(StockDetailRequest request) throws ApplicationException {
//		if (detailList == null) {
//			return;
//		}
//		Long contractId = request.getCtrContractId();
//		if (contractId != null && contractId > 0L) {
//			// 只有撮合情况下，合同id才不为空
//			stockContractService.saveSellComplete(request);
//		} else {
//		}
		stockContractService.saveSell(request);
//		BigDecimal dealNumber = request.getDealNumber();
//		for (StockDetail detail : detailList) {

//			BigDecimal availableNumber = detail.getAvailableNumber();
			// 销售申请的时候，不需要对应合同明细表
//			BigDecimal forzenNumber = detail.getFrozenNumber();
//			if (contractId != null && contractId > 0L) {
				// 只有撮合情况下，合同id才不为空
//				String oriSellId = detail.getSellContractId();
//				detail.setSellContractId(ContractCfsUtil.addContractId(oriSellId, contractId));
//				detail.setSellContractCfs(ContractCfsUtil.addContractCfs(detail.getSellContractCfs(),
//						String.valueOf(contractId), dealNumber));
//			}

//			if (availableNumber.compareTo(dealNumber) >= 0) {
//				detail.setAvailableNumber(availableNumber.subtract(dealNumber));
//				detail.setFrozenNumber(forzenNumber.add(dealNumber));
//				detail.setStockStatus(BasConstants.APPLY_TYPE_S);
//				detail = stockDetailDao.save(detail);
////				stockDetailHisService.saveSell(request, detail);
//				break;
//			} else {
				// 可用数量<卖出数量
//				detail.setAvailableNumber(BigDecimal.ZERO);
//				detail.setFrozenNumber(forzenNumber.add(availableNumber));
//				detail.setStockStatus(BasConstants.APPLY_TYPE_S);
//				dealNumber = dealNumber.subtract(availableNumber);
//				detail = stockDetailDao.save(detail);
//				stockDetailHisService.saveSell(request, detail);
//			}
//		}
	}

	private void sellBack(StockDetailRequest request) throws ApplicationException {
		// 销售驳回，这里不是作废
		stockContractService.saveSellBack(request);;
//		if (detailList == null) {
//			return;
//		}
//		BigDecimal forzenNumber = request.getDealNumber();
//		for (StockDetail detail : detailList) {
//
////			BigDecimal afterFrozenNumber = detail.getFrozenNumber();
//			// 冻结列表的数量>=要还原的数量
//			if (afterFrozenNumber.compareTo(forzenNumber) >= 0) {
//				BigDecimal oriFrozenNumber = afterFrozenNumber.subtract(forzenNumber);
//				detail.setAvailableNumber(detail.getAvailableNumber().add(forzenNumber));
////				detail.setFrozenNumber(oriFrozenNumber);
//
//			} else {
//				// 冻结列表的数量<要还原的数量
//				detail.setAvailableNumber(detail.getAvailableNumber().add(afterFrozenNumber));
////				detail.setFrozenNumber(BigDecimal.ZERO);
//				forzenNumber = forzenNumber.subtract(afterFrozenNumber);
//			}
//
//			detail = stockDetailDao.save(detail);
//			stockDetailHisService.deleteSell(detail, request);
//		}
	}

//	private List<StockDetail> find4Sell(StockDetailRequest request) {
//		Long contractId = request.getCtrContractId();
//		Map<String, Object> queryParams = new HashMap<String, Object>();
//		if (request.getLinkDetailId() != null) {
//			// query detailId !=null
//			queryParams.put("EQL_id", request.getLinkDetailId());
//		} else {
//			queryParams.put("EQS_productCd", request.getProductCd());// 商品类型
//			queryParams.put("EQL_factoryId", request.getFactoryId());// 厂商
//			queryParams.put("EQS_brandNumber", request.getBrandNumber());// 牌号
//			queryParams.put("EQS_warehouseName", request.getWarehouseName());// 仓库
//			// 如果有对应的采购合同ID
//			if (StringUtils.isNotBlank(request.getLinkContractId())) {
//				queryParams.put("EQS_buyContractId", request.getLinkContractId());
//			}
//			if (!request.isBack()) {
//				if (contractId == null || contractId == 0) {
//					queryParams.put("GTM_availableNumber", BigDecimal.ZERO);
//				}
//			} else {
//				// 解冻的情况
//				queryParams.put("GTM_frozenNumber", BigDecimal.ZERO);
//			}
//		}
//		// 获取唯一一条明细数据
//		Specification<StockDetail> spec = WebUtil.buildSpecification(queryParams);
//		Sort sort = new Sort(Direction.ASC, "createdDate");
//		List<StockDetail> detailList = this.stockDetailDao.findAll(spec, sort);
//		return detailList;
//	}

	public void cancelSellProduct(CtrProduct product) throws ApplicationException {

		stockContractService.cancelSell(product);
		
//		String ctrContractId = product.getCtrContractId() + "";
//		BigDecimal dealNumber = product.getDealNumber();

//		List<StockDetail> detailList = stockDetailDao.findByIdOrLinkId(product.getStockDetailId());

//		for (StockDetail detail : detailList) {
//
//			logger.info("作废销售合同:{},找到库存明细:{}", ctrContractId, JsonUtil.obj2Json(detail));
//
//			// 根据明细中的实际销售数量进行撤回
//			BigDecimal fixNumber = ContractCfsUtil.removeContractCfs(detail, ctrContractId, dealNumber);
////			stockDetailRelaService.deleteDetailRela(detail, product.getCtrContractId(), dealNumber,
////					StockDetailRela.RELATYPE_SELL);
//			if (fixNumber.compareTo(BigDecimal.ZERO) > 0) {
//				StockDetailCancelSellResp resp = new StockDetailCancelSellResp();
//				resp.setCtrContractId(product.getCtrContractId());
//				resp.setCtrProductId(product.getId());
//				resp.setStockDetailId(detail.getId());
////				resp.setStockId(detail.getStockId());
//				resp.setDealNumber(fixNumber);
//				lstResp.add(resp);
//				BigDecimal frozenNumber = detail.getFrozenNumber();
//				if (frozenNumber.compareTo(fixNumber) >= 0) {
//					BigDecimal curFrozenNumber = frozenNumber.subtract(fixNumber);
//					detail.setFrozenNumber(curFrozenNumber);
//					detail.setAvailableNumber(detail.getAvailableNumber().add(fixNumber));
//					dealNumber = dealNumber.subtract(fixNumber);
//				} else {
//					// 这种情况理论上不存在，除非出现bug
//					// 该条明细的冻结数量<作废的数量
//					detail.setFrozenNumber(BigDecimal.ZERO);
//					detail.setAvailableNumber(detail.getAvailableNumber().add(frozenNumber));
//					dealNumber = dealNumber.subtract(frozenNumber);
//				}
//				stockDetailDao.save(detail);
//				// 更新历史数据
////				this.stockDetailHisService.insertHisByCancel(detail, fixNumber, product, BasConstants.APPLY_TYPE_S);
//			}
//		}
	}

	/**
	 * 销售合同完成时调用，修改库存明细对应的销售合同id
	 */
	@ServerTransactional
	public void saveSellComplete(StockDetailRequest request) throws ApplicationException {

		stockContractService.saveSellComplete(request);
//		BigDecimal dealNumber = request.getDealNumber();
		// 销售申请中，如果采购入库了，有可能把之前冻结的申请中数量拆分成多条明细
//		List<StockDetail> detailList = stockDetailDao.findByIdOrLinkId(detailId);
//		for (StockDetail detail : detailList) {
//
//			if (dealNumber.compareTo(BigDecimal.ZERO) <= 0) {
//				break;
//			}
//			BigDecimal fixNumber = dealNumber;
//			BigDecimal frozenNumber = detail.getFrozenNumber();
//			if (dealNumber.compareTo(frozenNumber) > 0) {
//				// 冻结数量数量<卖出数量 dealNumber =
//				fixNumber = frozenNumber;
//			}
//			dealNumber = dealNumber.subtract(fixNumber);
//			StockDetail detail = this.getEntity(detailId);
//			String oriSellId = detail.getSellContractId();
//			String contractId = request.getCtrContractId() + "";
//			detail.setSellContractId(ContractCfsUtil.addContractId(oriSellId, contractId));
//			detail.setSellContractCfs(
//					ContractCfsUtil.addContractCfs(detail.getSellContractCfs(), contractId, fixNumber));
//			detail = stockDetailDao.save(detail);
//			stockDetailHisService.updateContractId(detailId, request.getApplyId(), request.getCtrContractId(),
//					BasConstants.APPLY_TYPE_S);
//		}
	}
	
	@Override
	public void savePresell(CtrProduct product, BizUserInfor userInfor) throws ApplicationException {
		stockDetailPresellService.savePresellDetail(product, userInfor);
	}
	
	@Override
	public void cancelPresell(CtrProduct product) throws ApplicationException {
		StockDetailPresell presell = stockDetailPresellService.findByCtrProductId(product.getId());
		stockDetailPresellService.delete(presell.getId());
	}
	
}
