package com.spt.bas.server.stock.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.ApplyContractAdjustRequestVo;
import com.spt.bas.client.vo.ApplySellWarehouseVo;
import com.spt.bas.client.vo.StockAdjustAuditVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.StockDetailDao;
import com.spt.bas.server.service.IStockDetailHisService;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.bas.server.stock.service.IStockDetailAdjustService;
import com.spt.tools.core.exception.ApplicationException;

/** 库存调整 */
@Component
@Transactional(readOnly = true)
public class StockDetailAdjustServiceImpl implements IStockDetailAdjustService {
	@Autowired
	private StockDetailDao stockDetailDao;
	@Autowired
	private IStockDetailHisService stockDetailHisService;
	@Autowired
	private IStockContractService stockContractService;

	@ServerTransactional
	public StockContract[] updateByContractAdjust(ApplyContractAdjustRequestVo vo)
			throws ApplicationException {
//		StockDetail detail = stockDetailDao.findOne(stockDetailId);
		// 原合同明细数量
		BigDecimal oldDealNumber = vo.getOldDealNumber();
		// 调整明细数量
		BigDecimal newDealNumber = vo.getNewDealNumber();
		// 差值
		BigDecimal diffNumber = newDealNumber.subtract(oldDealNumber);

		
		String operationType = "";
		if (vo.getContractType().equals(BasConstants.CONTRACT_TYPE_B)) {
			List<StockDetail> lstDetail = stockDetailDao.findByStockContractId(vo.getOldStockContractId());
			for(StockDetail detail: lstDetail) {
				BigDecimal availableNumber = detail.getAvailableNumber().add(diffNumber);
				//detail.setAvailableNumber(availableNumber);
				// 调大时需要考虑是不是一次性全部入库的（这种情况没有在途明细）
				if (diffNumber.compareTo(BigDecimal.ZERO) > 0) {
					// 新增在途库存明细记录，可用数量=diffNumber
					if (detail.getProductAttr().equals(BasConstants.STOCK_PRODUCT_ATTR_N)) {
						StockDetail detailTemp = new StockDetail();
						BeanUtils.copyProperties(detail, detailTemp);
						detail = detailTemp;

						detail.setId(0L);
//						detail.setFrozenNumber(BigDecimal.ZERO);
						detail.setDeliveryInNumber(BigDecimal.ZERO);
						detail.setDeliveryOutNumber(BigDecimal.ZERO);
						detail.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_P);
//						detail.setSellContractId(null);
//						detail.setSellContractCfs(null);
						// 仓库
						detail.setWarehouseId(0L);
						detail.setWarehouseName(vo.getWarehouseName());
						detail.setAvailableNumber(diffNumber);
						// 查询原采购合同对应的库存主表id
						ApplySellWarehouseVo searchVo = new ApplySellWarehouseVo();
						searchVo.setProductCode(detail.getProductCd());
						searchVo.setBrandNumber(detail.getBrandNumber());
						searchVo.setFactoryId(detail.getFactoryId());
						searchVo.setWarehouseName(vo.getWarehouseName());
						searchVo.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_P);
						searchVo.setEnterpriseId(detail.getEnterpriseId());
//						List<Stock> stockList = stockService.findStockByVo(searchVo);
//						detail.setStockId(stockList.get(0).getId());

					} else {
						detail.setAvailableNumber(availableNumber);
						detail.setDealPrice(vo.getDealPrice());
					}
					operationType = BasConstants.OPERATE_TYPE_CA;
					stockDetailDao.save(detail);
					stockDetailHisService.saveAdjust(detail, diffNumber.abs(), operationType, vo.getApplyId(), vo.getContractId());
					break;
				} else {
					// 数量调小
//					if(detail.getDeliveryInNumber().compareTo(newDealNumber)>0){
//						logger.info("采购合同调整,stockDetailId:{},deliveryInNumber:{},newDealNumber:{}", stockDetailId,
//								detail.getDeliveryInNumber(), newDealNumber);
//						throw new ApplicationException("调整后的明细数量不能小于已入库数量，已入库数量为：" + detail.getDeliveryInNumber() + "吨！");
//					}else{
					if (detail.getProductAttr().equals(BasConstants.STOCK_PRODUCT_ATTR_P)) {
						detail.setAvailableNumber(availableNumber);
					}
					if (availableNumber.compareTo(BigDecimal.ZERO)<0) {
						detail.setAvailableNumber(BigDecimal.ZERO);
						diffNumber = availableNumber;
					}
					detail.setDealPrice(vo.getDealPrice());
//					}
					operationType = BasConstants.OPERATE_TYPE_CS;
				}
				
				stockDetailDao.save(detail);

				// 添加库存明细流水
				stockDetailHisService.saveAdjust(detail, diffNumber.abs(), operationType, vo.getApplyId(), vo.getContractId());
			}
			
			
			
		} else {
//			diffNumber = vo.getDealNumber();
//			if (vo.getIsback()) {
////				detail.setSellContractId(ContractCfsUtil.removeContractId(detail.getSellContractId(), vo.getContractId() + ""));
////				detail.setSellContractCfs(ContractCfsUtil.removeContractCfs(detail.getSellContractCfs(),vo.getContractId() + "", oldDealNumber));
//				detail.setFrozenNumber(detail.getFrozenNumber().subtract(oldDealNumber));
//				detail.setAvailableNumber(detail.getAvailableNumber().add(oldDealNumber));
//				operationType = BasConstants.OPERATE_TYPE_CA;
//			} else {
////				detail.setSellContractId(ContractCfsUtil.addContractId(detail.getSellContractId(), vo.getContractId() + ""));
////				detail.setSellContractCfs(ContractCfsUtil.addContractCfs(detail.getSellContractCfs(), vo.getContractId() + "", diffNumber));
//				// 调大时需要考虑库存中可用数量是否充足；调小时需要考虑调整数量是否小于出库数量
//				if (diffNumber.compareTo(BigDecimal.ZERO) > 0) {
//					if (detail.getAvailableNumber().compareTo(diffNumber) >= 0) {
//						detail.setAvailableNumber(detail.getAvailableNumber().subtract(diffNumber));
//						detail.setFrozenNumber(detail.getFrozenNumber().add(diffNumber));
//					} else {
//						throw new ApplicationException("可用数量不足！");
//					}
//					operationType = BasConstants.OPERATE_TYPE_CS;
//				} else {
////					if(detail.getDeliveryOutNumber().compareTo(newDealNumber)>0){
////						logger.info("销售合同调整,stockDetailId:{},deliveryOutNumber:{},newDealNumber:{}", stockDetailId, detail.getDeliveryOutNumber(), newDealNumber);
////						throw new ApplicationException("调整后的明细数量不能小于已出库数量，已出库数量为：" + detail.getDeliveryOutNumber() + "吨！");
////					}else{
//					if (detail.getProductAttr().equals(BasConstants.STOCK_PRODUCT_ATTR_N)) {
//						detail.setAvailableNumber(detail.getAvailableNumber().subtract(diffNumber));
//						detail.setFrozenNumber(detail.getFrozenNumber().add(diffNumber));
//					} else {
//						// 在途销售
//						// 采购合同入库后把在途的冻结数量迁移到现货的冻结数量，所以若在途冻结减到0时从现货的冻结里边减
//						if (detail.getFrozenNumber().compareTo(diffNumber.negate()) > 0) {
//							detail.setAvailableNumber(detail.getAvailableNumber().subtract(diffNumber));
//							detail.setFrozenNumber(detail.getFrozenNumber().add(diffNumber));
//						} else {
//							BigDecimal moreNumber = diffNumber.negate().subtract(detail.getFrozenNumber());
//							detail.setAvailableNumber(detail.getFrozenNumber());
//							detail.setFrozenNumber(BigDecimal.ZERO);
//
//							// 查询现货明细
//							List<StockDetail> NDetailList = stockDetailDao.findByLinkStockDetailId(detail.getId());
//							for (StockDetail stockDetail : NDetailList) {
//								BigDecimal forzenNumber = stockDetail.getFrozenNumber();
//								BigDecimal availableNumber = stockDetail.getAvailableNumber();
//								if (forzenNumber.compareTo(moreNumber) > 0) {
//									stockDetail.setFrozenNumber(forzenNumber.subtract(moreNumber));
//									stockDetail.setAvailableNumber(availableNumber.add(moreNumber));
//
//									stockDetail = stockDetailDao.save(stockDetail);
//									stockDetailHisService.saveAdjust(stockDetail, moreNumber,
//											BasConstants.OPERATE_TYPE_CA, vo.getApplyId(), vo.getContractId());
//									vo.setOperationType(BasConstants.OPERATE_TYPE_CA);
//									vo.setDealNumber(moreNumber.negate());
////									stockService.updateByContractAdjust(vo, stockDetail.getStockId());
//									diffNumber = diffNumber.add(moreNumber);
//									break;
//								} else {
//									stockDetail.setFrozenNumber(BigDecimal.ZERO);
//									stockDetail.setAvailableNumber(forzenNumber);
//
//									moreNumber = moreNumber.subtract(forzenNumber);
//
//									stockDetail = stockDetailDao.save(stockDetail);
//									stockDetailHisService.saveAdjust(stockDetail, forzenNumber,
//											BasConstants.OPERATE_TYPE_CA, vo.getApplyId(), vo.getContractId());
//									vo.setOperationType(BasConstants.OPERATE_TYPE_CA);
//									vo.setDealNumber(forzenNumber.negate());
////									stockService.updateByContractAdjust(vo, stockDetail.getStockId());
//
//									diffNumber = diffNumber.add(forzenNumber);
//								}
//							}
//
//						}
//					}
//
////					}
//					operationType = BasConstants.OPERATE_TYPE_CA;
//				}
//			}
		}
		

		// 更新库存主表
//		vo.setOperationType(operationType);
//		vo.setDealNumber(diffNumber);
//		stockService.updateByContractAdjust(vo, detail.getStockId());

		return stockContractService.saveAdjust(vo);
	}

	@Override
	@ServerTransactional
	public void saveDetailAndHis(StockAdjustAuditVo vo) {
		StockDetail detail = stockDetailDao.findOne(vo.getStockDetailId());
		BigDecimal realNumber = vo.getRealNumber();
		BigDecimal diffNumber = vo.getDifferentNumber();
//		String type = vo.getType();
//		if (type.equals("F")) {
//			detail.setFrozenNumber(realNumber);
//		} else {
			detail.setAvailableNumber(realNumber);
//		}
		detail = stockDetailDao.save(detail);

		String operationType;
		if (diffNumber.compareTo(BigDecimal.ZERO) > 0) {
			operationType = BasConstants.OPERATE_TYPE_AS;
		} else {
			operationType = BasConstants.OPERATE_TYPE_AA;
		}
		stockDetailHisService.saveAdjust(detail, diffNumber.abs(), operationType, null, null);

//		// 更新库存数据
//		stockService.updateByAdjust(vo, detail, operationType);
	}
}
