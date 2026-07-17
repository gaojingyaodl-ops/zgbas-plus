package com.spt.bas.server.stock.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.entity.StockDetailRela;
import com.spt.bas.client.entity.StockMoveDetail;
import com.spt.bas.client.vo.StockDetailMoveVo;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BsWarehouseDao;
import com.spt.bas.server.dao.StockDetailDao;
import com.spt.bas.server.dao.StockMoveDetailDao;
import com.spt.bas.server.service.IStockDetailHisService;
import com.spt.bas.server.service.IStockDetailService;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.bas.server.stock.service.IStockDetailDeliveryOutService;
import com.spt.bas.server.stock.service.IStockDetailRelaService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.persistence.WebUtil;

/** 出库 */
@Component
@Transactional(readOnly = true)
public class StockDetailDeliveryOutServiceImpl implements IStockDetailDeliveryOutService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private IStockDetailService stockDetailService;
	@Autowired
	private StockDetailDao stockDetailDao;
	@Autowired
	private IStockDetailHisService stockDetailHisService;
//	@Autowired
//	private IStockService stockService;
	@Autowired
	private StockMoveDetailDao stockMoveDetailDao;
	@Autowired
	private BsWarehouseDao bsWarehouseDao;
	@Autowired
	private IStockDetailRelaService stockDetailRelaService;
	@Autowired
	private IStockContractService stockContractService;

	/**
	 * 出库，扣除冻结数量
	 */
	@Override
	@ServerTransactional
	public void saveDeliveryOut(StockDetailRequest request) throws ApplicationException {
		// 查询销售合同对应的库存明细
//		List<StockDetail> detailList = stockDetailService.findDetailList(request, BasConstants.OPE_BUYSELL_S, true);

//		if (detailList != null && detailList.size() > 0) {
			StockDetail detail = stockDetailService.getEntity(request.getLinkDetailId());
//			StockContractRela rela = stockContractRelaService.findStockContractId(request.getCtrContractId(), StockContractRela.RELATYPE_SELL, detail.getStockContractId());
//			BigDecimal cfsNumber =rela.getRelaNum();// 最大可出数量
//			BigDecimal cfsNumber = ContractCfsUtil.getContractCfsNumber(detail, request.getCtrContractId() + "");// 最大可出数量
//			BigDecimal frozenNumber = detail.getFrozenNumber();
			String operateType = BasConstants.APPLY_TYPE_O;
			if (!request.isBack()) {
				// 正常出库
				stockContractService.saveDeliveryOut(request);
//				if (cfsNumber.compareTo(request.getDealNumber()) < 0) {
//					throw new ApplicationException("库存明细出库数量不足");
//				}
				//已入库未出库数量
				BigDecimal remainNum =	detail.getDeliveryInNumber().subtract(detail.getDeliveryOutNumber());
				if (remainNum.compareTo(request.getDealNumber()) < 0) {
					throw new ApplicationException("库存明细已入库数量不足");
				}
//				frozenNumber = frozenNumber.subtract(request.getDealNumber());
				// 更新出库数量
				detail.setDeliveryOutNumber(detail.getDeliveryOutNumber().add(request.getDealNumber()));
				saveRela(request, detail.getId());
			} else {
				// 出库作废
				stockContractService.cancelDeliveryOut(request);
				operateType = BasConstants.OPERATE_TYPE_OB;
//				frozenNumber = frozenNumber.add(request.getDealNumber());
				detail.setDeliveryOutNumber(detail.getDeliveryOutNumber().subtract(request.getDealNumber()));
				stockDetailRelaService.deleteDetailRela(detail, request.getCtrContractId(), request.getDealNumber(),
						StockDetailRela.RELATYPE_OUT);
			}
//			detail.setFrozenNumber(frozenNumber);
//			if (frozenNumber.compareTo(BigDecimal.ZERO) > 0) {
//				detail.setStockStatus(BasConstants.STOCK_STATUS_PO);
//			} else if (frozenNumber.compareTo(BigDecimal.ZERO) == 0) {
//				detail.setStockStatus(BasConstants.STOCK_STATUS_O);
//			} else {
//				logger.warn("request:{}", JsonUtil.obj2Json(request));
//				logger.warn("frozenNumber:{},detail:{}", frozenNumber, JsonUtil.obj2Json(detail));
//				if (!request.isBack()) {
//					throw new ApplicationException("库存明细出库数量不足");
//				}
//			}
			stockDetailDao.save(detail);
			// 保存库存明细流水
			stockDetailHisService.saveDeliveryOut(detail, request, operateType);
//		} 
//	else {
//			if (request.getDealNumber().compareTo(BigDecimal.ZERO) > 0) {
//				logger.warn("request:{}", JsonUtil.obj2Json(request));
//				throw new ApplicationException("库存明细异常");
//			}
//		}

	}

	/** 添加库存关联记录 */
	private void saveRela(StockDetailRequest request, Long stockDetailId) throws ApplicationException {
		StockDetailRela relaVo = new StockDetailRela();
		relaVo.setApproveId(request.getApproveId());
		relaVo.setContractId(request.getCtrContractId());
		relaVo.setEnterpriseId(request.getEnterpriseId());
		relaVo.setRelaNum(request.getDealNumber());
		relaVo.setRelaType(StockDetailRela.RELATYPE_OUT);
		relaVo.setStockDetailId(stockDetailId);
		stockDetailRelaService.saveDetailRela(relaVo);
	}

	/**
	 * 移库，现货库存才能移库
	 */
	@Override
	@ServerTransactional
	public void changeWarehouse(StockDetailMoveVo changeVo) {
		logger.info("[移库] changeVo:{}",JsonUtil.obj2Json(changeVo));
		// 目标仓库名称
		BsWarehouse targetWarehouse = bsWarehouseDao.findOne(changeVo.getTargetWarehouseId());
		changeVo.setWarehouseName(targetWarehouse.getWarehouseName());
		StockDetail entity = stockDetailDao.findOne(changeVo.getOriginalDetailId());
		// 更新库存
//		Stock newStock = stockService.updateStock(entity, changeVo);
		// 更新原库存明细及库存历史
		entity.setAvailableNumber(entity.getAvailableNumber().subtract(changeVo.getMoveRealNumber()));
		entity.setDeliveryInNumber(entity.getDeliveryInNumber().subtract(changeVo.getMoveRealNumber()));
		// entity.setFrozenNumber(entity.getFrozenNumber().subtract(changeVo.getMoveFrozenNumber()));
		entity = stockDetailDao.save(entity);
		stockDetailHisService.saveMoveHis(entity, changeVo, BasConstants.OPERATE_TYPE_MS);

		// 更新移库的库存明细及库存历史
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("EQS_productCd", entity.getProductCd());// 商品类型
		queryParams.put("EQL_factoryId", entity.getFactoryId());// 厂商
		queryParams.put("EQS_brandNumber", entity.getBrandNumber());// 牌号
		queryParams.put("EQS_buyContractId", entity.getBuyContractId());
		queryParams.put("EQS_warehouseName", targetWarehouse.getWarehouseName());
		queryParams.put("EQS_productAttr", "N");//现货
		Specification<StockDetail> spec = WebUtil.buildSpecification(queryParams);
		StockDetail detail = stockDetailDao.findOne(spec).orElse(null);
		if (detail != null) {
			detail.setAvailableNumber(detail.getAvailableNumber().add(changeVo.getMoveRealNumber()));
//			detail.setFrozenNumber(detail.getFrozenNumber().add(changeVo.getMoveFrozenNumber()));
			detail.setDeliveryInNumber(detail.getDeliveryInNumber().add(changeVo.getMoveRealNumber()));
		} else {
			detail = new StockDetail();
			BeanUtils.copyProperties(entity, detail);
			detail.setId(0l);
			detail.setAvailableNumber(changeVo.getMoveRealNumber());
			detail.setDeliveryInNumber(changeVo.getMoveRealNumber());
			detail.setDeliveryOutNumber(BigDecimal.ZERO);
			detail.setCreatedDate(null);
			detail.setUpdatedDate(null);
//			detail.setFrozenNumber(changeVo.getMoveFrozenNumber());
//			detail.setStockId(newStock.getId());
			detail.setWarehouseName(targetWarehouse.getWarehouseName());
		}
		detail.setWarehouseAddr(changeVo.getWarehouseAddrs());
		detail.setApplyNo(changeVo.getApplyNo());
		detail.setSpotType(changeVo.getSpotType());
		//detail.setStockType(changeVo.getMoveType());
		detail = stockDetailDao.save(detail);
		stockDetailHisService.saveMoveHis(detail, changeVo, BasConstants.OPERATE_TYPE_MA);

		// 添加一条移库明细
		addStockMoveDetail(entity, detail, changeVo);

	}

	private void addStockMoveDetail(StockDetail original, StockDetail target, StockDetailMoveVo changeVo) {
		StockMoveDetail stockMoveDetail = new StockMoveDetail();
		stockMoveDetail.setOriginalDetailId(changeVo.getOriginalDetailId());
		stockMoveDetail.setOriginalWarehouseId(original.getWarehouseId());
		stockMoveDetail.setOriginalWarehouseName(original.getWarehouseName());
		stockMoveDetail.setMoveRealNumber(changeVo.getMoveRealNumber());
		stockMoveDetail.setTargetWarehouseId(changeVo.getTargetWarehouseId());
		stockMoveDetail.setTargetWarehouseName(changeVo.getWarehouseName());
		stockMoveDetail.setTargetDetailId(target.getId());
		stockMoveDetail.setMatchUserId(changeVo.getCurUserId());
		stockMoveDetail.setMatchUserName(changeVo.getCurUserName());
		stockMoveDetail.setEnterpriseId(original.getEnterpriseId());
		stockMoveDetailDao.save(stockMoveDetail);
	}

}
