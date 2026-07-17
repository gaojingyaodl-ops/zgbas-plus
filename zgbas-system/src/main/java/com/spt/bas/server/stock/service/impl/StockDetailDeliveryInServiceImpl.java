package com.spt.bas.server.stock.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.StockDetailDeleveryInResp;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.StockDetailDao;
import com.spt.bas.server.service.IStockDetailHisService;
import com.spt.bas.server.service.IStockDetailService;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.bas.server.stock.service.IStockDetailDeliveryInService;
import com.spt.tools.core.collection.CollectionUtil;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
/** 入库 */
@Component
@Transactional(readOnly = true)
public class StockDetailDeliveryInServiceImpl implements IStockDetailDeliveryInService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private IStockDetailService stockDetailService;
	@Autowired
	private StockDetailDao stockDetailDao;
	@Autowired
	private IStockDetailHisService stockDetailHisService;
	@Autowired
	private IStockContractService stockContractService;
	/**
	 * 入库：在途库存改成现货库存
	 */
	@Override
	@ServerTransactional
	public StockDetailDeleveryInResp saveDeliveryIn(StockDetailRequest request) throws ApplicationException {
		// 商品入库：在途库存 改成 现货库存，并修改仓库名称
		// 查询采购合同对应的库存明细
		StockDetailDeleveryInResp reps = new StockDetailDeleveryInResp();
		List<StockDetail> detailList = stockDetailService.findDetailList(request, BasConstants.OPE_BUYSELL_B, false);
		if (detailList == null || detailList.size() == 0) {
			logger.warn("没找到采购合同对应的库存明细记录，request:{}", JsonUtil.obj2Json(request));
			throw new ApplicationException("没找到采购合同对应的库存明细记录");
		}
		// 当前入库数量
		StockDetail detailOld = detailList.get(0);
		String newWarehouseName = request.getWarehouseNameNew();
		String oldWarehouseName = request.getWarehouseName();
//		String buyContractId = request.getCtrContractId()+"";
		// 部分入库,新增现货数据
		StockDetail detailNew = null;

		if (!request.isBack()) {
			// 正常入库
			if (detailList.size() >= 2) {
				// 一条在途，多条现货
				int old_cnt = 0;
				int new_cnt = 0;
				for (StockDetail stockDetail : detailList) {
					if (newWarehouseName.equals(stockDetail.getWarehouseName())) {
						detailNew = stockDetail;
						new_cnt++;
					}
					if (oldWarehouseName.equals(stockDetail.getWarehouseName())
							&& StringUtils.equals(stockDetail.getProductAttr(), BasConstants.STOCK_PRODUCT_ATTR_P)) {
						detailOld = stockDetail;
						old_cnt++;
					}
				}
				if (new_cnt > 1) {
					Optional<StockDetail> op = detailList.stream()
							.filter(d -> newWarehouseName.equals(d.getWarehouseName()))
							.sorted((d1, d2) -> Math.negateExact(d1.getId().compareTo(d2.getId()))).findFirst();
					if (op.isPresent()) {
						detailNew = op.get();
					}
//					CollectionUtil.sortList(detailList, "id");
//					for (StockDetail stockDetail : detailList) {
//						if (newWarehouseName.equals(stockDetail.getWarehouseName())) {
//							detailNew = stockDetail;
//							break;
//						}
//					}
				}
				
				if (old_cnt > 1) {
					for (StockDetail stockDetail : detailList) {
						if (stockDetail.getId().equals(request.getLinkDetailId())) {
							detailOld = stockDetail;
							break;
						}
					}
				}
			}
			reps = saveDeliveryIn(request, detailOld, detailNew);
		} else {
			// 入库撤回
			if (detailList.size() >= 2) {
				// 一条在途，多条现货
				int old_cnt = 0;
				int new_cnt = 0;
				for (StockDetail stockDetail : detailList) {
					if (newWarehouseName.equals(stockDetail.getWarehouseName())) {
						detailNew = stockDetail;
						new_cnt++;
					}
					if (oldWarehouseName.equals(stockDetail.getWarehouseName())
							&& StringUtils.equals(stockDetail.getProductAttr(), BasConstants.STOCK_PRODUCT_ATTR_N)) {
						detailOld = stockDetail;
						old_cnt++;
					}
				}

				if (new_cnt > 1) {
					CollectionUtil.sortList(detailList, "id");
					for (StockDetail stockDetail : detailList) {
						if (newWarehouseName.equals(stockDetail.getWarehouseName())) {
							detailNew = stockDetail;
							break;
						}
					}
				}
				if (old_cnt > 1) {
					for (StockDetail stockDetail : detailList) {
						if (stockDetail.getId().equals(request.getLinkDetailId())) {
							detailOld = stockDetail;
							break;
						}
					}
				}
			}
			reps = cancelDeliveryIn(request, detailOld, detailNew);
		}
		return reps;

	}

	/** 正常入库 */
	private StockDetailDeleveryInResp saveDeliveryIn(StockDetailRequest request, StockDetail detailOld, StockDetail detailNew)
			throws ApplicationException {
		stockContractService.saveDeliveryIn(request);
		StockDetailDeleveryInResp reps = new StockDetailDeleveryInResp();
		// 当前入库数量
		BigDecimal curDeliveryInNumber = request.getDealNumber();
		String newWarehouseName = request.getWarehouseNameNew();
//		BigDecimal oriFrozenNew = BigDecimal.ZERO;
		BigDecimal oriRealNew = BigDecimal.ZERO;
//		BigDecimal oriFrozenOld = detailOld.getFrozenNumber();
		BigDecimal oriRealOld = detailOld.getAvailableNumber();
		if (detailNew != null) {
//			oriFrozenNew = detailNew.getFrozenNumber();
			oriRealNew = detailNew.getAvailableNumber();
		}
		// 正常入库
//		if (curDeliveryInNumber.compareTo(detailOld.getAvailableNumber()) > 0) {
//			logger.warn("原仓库库存数量不足，无法入库，request:{},detailOld:{}", JsonUtil.obj2Json(request),
//					JsonUtil.obj2Json(detailOld));
//			throw new ApplicationException("原仓库库存数量不足，无法入库");
//		}
		if (detailNew == null && curDeliveryInNumber.compareTo(detailOld.getAvailableNumber()) == 0
				&& detailOld.getDeliveryInNumber().compareTo(BigDecimal.ZERO) == 0) {
			// 全部入库，并且之前未入库过，直接修改原明细记录
			detailOld.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_N);
			detailOld.setDeliveryInNumber(detailOld.getDeliveryInNumber().add(curDeliveryInNumber));
			detailOld.setWarehouseName(newWarehouseName);
			detailOld.setWarehouseId(request.getWarehouseIdNew());
			detailOld.setApplyNo(request.getApplyNo());
			detailOld.setWarehouseBatchNo(request.getWarehouseBatchNo());
			detailOld.setWarehousePosition(request.getWarehousePosition());
//			detailOld.setStockId(request.getOtherStockId());
			if (request.getSpotType() == null) {
				request.setStockType(BasConstants.DICT_TYPE_STOCKTYPE_QH);
			}
			detailOld.setStockType(request.getStockType());
			detailOld.setSpotType(request.getSpotType());
			detailOld.setWrapSpecs(request.getWrapSpecs());
			detailOld.setWarehousePos(request.getWarehousePos());
			detailOld.setWarehouseAddr(request.getWarehouseAddr());
			detailOld.setDeliveryInApplyId(request.getApplyId());
			detailOld = stockDetailDao.save(detailOld);

			if (request.getMatchBl()) {
				stockDetailHisService.saveDeliveryIn(detailOld, request, BasConstants.STOCK_NUMBER_ADD,
						request.isBack());
			} else {
				StockDetail detailTmp = new StockDetail();
				BeanUtils.copyProperties(detailOld, detailTmp);
//				detailTmp.setFrozenNumber(BigDecimal.ZERO);
				detailTmp.setAvailableNumber(BigDecimal.ZERO);
				stockDetailHisService.saveDeliveryIn(detailTmp, request, BasConstants.STOCK_NUMBER_SUB,
						request.isBack());
				stockDetailHisService.saveDeliveryIn(detailOld, request, BasConstants.STOCK_NUMBER_ADD,
						request.isBack());
			}
			reps.setStockDetailId(detailOld.getId());
//			reps.setFrozenNumber(detailOld.getFrozenNumber());
			reps.setRealNumber(detailOld.getAvailableNumber());
		} else {
			// 部分入库
			if (detailNew == null) {
				// 第一次部分入库，入库完成后，会存在一条在途库存，一条现货库存
				detailNew = new StockDetail();
				BeanUtils.copyProperties(detailOld, detailNew);
				detailNew.setCreatedDate(null);
				detailNew.setUpdatedDate(null);
				detailNew.setAvailableNumber(BigDecimal.ZERO);
				
				//detailNew.setDeliveryOutNumber(BigDecimal.ZERO);
				//detailNew.setDeliveryInNumber(BigDecimal.ZERO);
				
//				detailNew.setFrozenNumber(BigDecimal.ZERO);
				detailNew.setId(null);
//				detailNew.setStockId(request.getOtherStockId());
				detailNew.setApplyNo(request.getApplyNo());
//				detailNew.setSellContractCfs(null);
//				detailNew.setSellContractId(null);
				if (detailOld.getLinkStockDetailId() != null && detailOld.getLinkStockDetailId() > 0) {
					detailNew.setLinkStockDetailId(detailOld.getLinkStockDetailId());
				} else {
					detailNew.setLinkStockDetailId(detailOld.getId());
				}
				if (request.getSpotType() == null) {
					request.setStockType(BasConstants.DICT_TYPE_STOCKTYPE_QH);
				}
				detailNew.setStockType(request.getStockType());
				detailNew.setSpotType(request.getSpotType());
				detailNew.setWrapSpecs(request.getWrapSpecs());
				detailNew.setWarehousePos(request.getWarehousePos());
				detailNew.setWarehouseAddr(request.getWarehouseAddr());
				detailNew.setDeliveryInApplyId(request.getApplyId());
			} else {
				// 不是第一次部分入库，已有现货库存明细，若无在途的sellContractId,则需要添加进去
			}
//			addSellContractId(detailOld, detailNew, curDeliveryInNumber);

			// 原冻结数量
//			BigDecimal oriFrozenNumber = detailOld.getFrozenNumber();
			// 入库数量>=在途的冻结数量，把在途明细的冻结数量累计到现货明细上,超出的入库数量，从可用数量中扣除
//			if (curDeliveryInNumber.compareTo(oriFrozenNumber) >= 0) {
//				// 冻结数量全部入库，销售合同id要跟着一起迁移
////				detailNew.setFrozenNumber(detailNew.getFrozenNumber().add(oriFrozenNumber));
//				BigDecimal moreReal = curDeliveryInNumber.subtract(oriFrozenNumber);
//				detailNew.setAvailableNumber(detailNew.getAvailableNumber().add(moreReal));
//				detailNew.setDeliveryInNumber(detailNew.getDeliveryInNumber().add(curDeliveryInNumber));
//				detailNew.setStockType(request.getStockType());
//				if (BasConstants.DICT_TYPE_STOCKTYPE_QH.equals(request.getStockType())) {
//					detailNew.setSpotType(null);
//				} else {
//					detailNew.setSpotType(request.getSpotType());
//				}
////				detailNew.setSellContractId(ContractCfsUtil.addContractId(null,buyContractId));
//				detailOld.setAvailableNumber(detailOld.getAvailableNumber().subtract(moreReal));
////				detailOld.setFrozenNumber(BigDecimal.ZERO);
////				detailOld.setSellContractId(ContractCfsUtil.removeContractId(detailOld.getSellContractId(),buyContractId));
//				reps.setFrozenNumber(oriFrozenNumber);
//				reps.setRealNumber(moreReal);
//			} else {
				// 入库数量<冻结数量，把本地操作的入库数量，直接累计到现货明细上，并从在途明细中扣除相应的冻结数量
//				detailNew.setFrozenNumber(detailNew.getFrozenNumber().add(curDeliveryInNumber));
				// detailN现货可用数量不变
				detailNew.setAvailableNumber(detailNew.getAvailableNumber().add(curDeliveryInNumber));// 入库数量
				detailNew.setDeliveryInNumber(detailNew.getDeliveryInNumber().add(curDeliveryInNumber));// 入库数量
//				detailOld.setFrozenNumber(oriFrozenNumber.subtract(curDeliveryInNumber));
				if (detailNew.getDeliveryInNumber().compareTo(detailNew.getAvailableNumber()) > 0) {
					detailNew.setDeliveryInNumber(curDeliveryInNumber);
					detailNew.setDeliveryOutNumber(BigDecimal.ZERO);
				}

				detailOld.setAvailableNumber(detailOld.getAvailableNumber().subtract(curDeliveryInNumber));
				
				reps.setFrozenNumber(curDeliveryInNumber);
//			}

			detailNew.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_N);
			detailNew.setWarehouseName(newWarehouseName);
			detailNew.setWarehouseId(request.getWarehouseIdNew());
			stockDetailDao.save(detailNew);
			stockDetailDao.save(detailOld);
			// 保存库存明细流水
//			request.setPreFrozenNumber(oriFrozenOld);
			request.setPreRealNumber(oriRealOld);
			stockDetailHisService.saveDeliveryIn(detailOld, request, BasConstants.STOCK_NUMBER_SUB, request.isBack());
//			request.setPreFrozenNumber(oriFrozenNew);
			request.setPreRealNumber(oriRealNew);
			stockDetailHisService.saveDeliveryIn(detailNew, request, BasConstants.STOCK_NUMBER_ADD, request.isBack());
			reps.setStockDetailId(detailNew.getId());
		}
		return reps;
	}

	/** 入库撤回 */
	private StockDetailDeleveryInResp cancelDeliveryIn(StockDetailRequest request, StockDetail detailOld,
			StockDetail detailNew) throws ApplicationException {
		stockContractService.cancelDeliveryIn(request);
		StockDetailDeleveryInResp reps = new StockDetailDeleveryInResp();
		// 当前入库数量
		BigDecimal curDeliveryInNumber = request.getDealNumber();
		String newWarehouseName = request.getWarehouseNameNew();
		String oldWarehouseName = request.getWarehouseName();
//		BigDecimal oriFrozenNew = BigDecimal.ZERO;
		BigDecimal oriRealNew = BigDecimal.ZERO;
//		BigDecimal oriFrozenOld = detailOld.getFrozenNumber();
		BigDecimal oriRealOld = detailOld.getAvailableNumber();
		if (detailNew != null) {
//			oriFrozenNew = detailNew.getFrozenNumber();
			oriRealNew = detailNew.getAvailableNumber();
		}
		// 入库撤回
//		if (curDeliveryInNumber.compareTo(detailOld.getAvailableNumber().add(detailOld.getFrozenNumber())) > 0) {
//			logger.warn("原库存已出库，无法作废入库，request:{},detailOld:{}", JsonUtil.obj2Json(request),
//					JsonUtil.obj2Json(detailOld));
//			throw new ApplicationException("原库存已出库，无法作废入库");
//		}
		// 入库撤回，还原入库数据
		// 现货数据改成在途数据，仓库名称改成旧仓库
		if (curDeliveryInNumber.equals(detailOld.getAvailableNumber())) {
			// 如果是全部入库，直接还原仓库名称和属性就可以
			// 入库撤回
			detailOld.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_P);
			detailOld.setDeliveryInNumber(BigDecimal.ZERO);
			detailOld.setWarehouseName(oldWarehouseName);
			detailOld.setWarehouseId(request.getWarehouseId());
			detailOld.setStockId(request.getStockId());
			detailOld.setApplyNo(null);
			detailOld.setWarehouseBatchNo(null);
			detailOld.setWarehousePosition(null);
			detailOld.setStockType(BasConstants.DICT_TYPE_STOCKTYPE_QH);
			detailOld.setSpotType(null);
			detailOld = stockDetailDao.save(detailOld);
			reps.setStockDetailId(detailOld.getId());
//			reps.setFrozenNumber(detailOld.getFrozenNumber());
			reps.setRealNumber(curDeliveryInNumber);
			stockDetailHisService.saveDeliveryIn(detailOld, request, BasConstants.STOCK_NUMBER_ADD, request.isBack());
		} else {
			// 若第一次入库数量为0，不生成新的库存明细 detailNew=null
			if (detailNew != null) {
//				addSellContractId(detailNew, detailOld, curDeliveryInNumber);

				// 如果是部分入库，根据实际数量修改新旧仓库数量
				// 原冻结数量
//				BigDecimal oriFrozenNumber = detailOld.getFrozenNumber();
//				BigDecimal oriAvailableNumber = detailOld.getAvailableNumber();
//				BigDecimal newFrozenNumber = detailNew.getFrozenNumber();
//				BigDecimal newAvailableNumber = detailNew.getAvailableNumber();
				// 入库数量>=现货的冻结数量，把现货明细的冻结数量累计到在途明细上
				// 入库数量<现货的冻结数量，先扣除冻结数量,再扣除可用数量
//				if (curDeliveryInNumber.compareTo(newFrozenNumber) >= 0) {
//					detailNew.setFrozenNumber(BigDecimal.ZERO);
//					BigDecimal moreReal = curDeliveryInNumber.subtract(newFrozenNumber);
//					detailNew.setAvailableNumber(newAvailableNumber.subtract(moreReal));
//					detailNew.setDeliveryInNumber(detailNew.getDeliveryInNumber().subtract(curDeliveryInNumber));
////					detailNew.setSellContractId(ContractCfsUtil.removeContractId(detailNew.getSellContractId(),buyContractId));
//					detailOld.setFrozenNumber(oriFrozenNumber.add(newFrozenNumber));
//					detailOld.setAvailableNumber(oriAvailableNumber.add(moreReal));
////					detailOld.setSellContractId(ContractCfsUtil.addContractId(detailOld.getSellContractId(),buyContractId));
//
//					reps.setFrozenNumber(newFrozenNumber);
//					reps.setRealNumber(moreReal);
//				} else {
					// 入库数量<冻结数量，把本地操作的入库数量，直接累计到现货明细上，并从在途明细中扣除相应的冻结数量
//					detailNew.setFrozenNumber(newFrozenNumber.subtract(curDeliveryInNumber));
					// detailN现货可用数量不变
					detailNew.setDeliveryInNumber(detailNew.getDeliveryInNumber().subtract(curDeliveryInNumber));// 入库数量
//					detailOld.setFrozenNumber(oriFrozenNumber.add(curDeliveryInNumber));
					reps.setFrozenNumber(curDeliveryInNumber);
//				}
				reps.setStockDetailId(detailNew.getId());

				// detailNew.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_P);
				detailNew.setWarehouseName(newWarehouseName);
				detailNew.setWarehouseId(request.getWarehouseIdNew());
				stockDetailDao.save(detailNew);
				stockDetailDao.save(detailOld);
				// 保存库存明细流水
//				request.setPreFrozenNumber(oriFrozenOld);
				request.setPreRealNumber(oriRealOld);
				stockDetailHisService.saveDeliveryIn(detailOld, request, BasConstants.STOCK_NUMBER_ADD,
						request.isBack());
//				request.setPreFrozenNumber(oriFrozenNew);
				request.setPreRealNumber(oriRealNew);
				stockDetailHisService.saveDeliveryIn(detailNew, request, BasConstants.STOCK_NUMBER_SUB,
						request.isBack());

			}
		}
		return reps;
	}

//	private void addSellContractId(StockDetail detailOld, StockDetail detailNew, BigDecimal curNumber) {
//
//		String pSellId = detailOld.getSellContractId();
//		if (StringUtils.isNotBlank(pSellId)) {
//			String nSellId = "";
//			String nSellIdStr = ",";
//			if (detailNew.getSellContractId() != null) {
//				nSellIdStr = detailNew.getSellContractId();
//			}
//			List<ContractCfs> lstOld = ContractCfsUtil.converList(detailOld.getSellContractCfs());
//			List<ContractCfs> lstNew = ContractCfsUtil.converList(detailNew.getSellContractCfs());
//			for (Iterator<ContractCfs> itOld = lstOld.iterator(); itOld.hasNext();) {
//				ContractCfs pVo = itOld.next();
//				if (curNumber.compareTo(BigDecimal.ZERO) == 0) {
//					break;
//				}
//
//				BigDecimal inNumber = BigDecimal.ZERO;// 本单入库数量
//				if (pVo.getDealNumber() != null && pVo.getDealNumber().compareTo(curNumber) > 0) {
//					inNumber = curNumber;
//					pVo.setDealNumber(pVo.getDealNumber().subtract(curNumber));
//					curNumber = BigDecimal.ZERO;
//				} else {
//					inNumber = pVo.getDealNumber();
//					curNumber = curNumber.subtract(pVo.getDealNumber());
//					itOld.remove();
//					detailOld.setSellContractId(
//							ContractCfsUtil.removeContractId(detailOld.getSellContractId(), pVo.getContractId()));
//				}
//
//				boolean flg = false;
//				for (Iterator<ContractCfs> itNew = lstNew.iterator(); itNew.hasNext();) {
//					ContractCfs nVo = itNew.next();
//					if (pVo.getContractId().equals(nVo.getContractId())) {
//						nVo.setDealNumber(nVo.getDealNumber().add(inNumber));
//						flg = true;
//						break;
//					}
//				}
//				if (!flg) {
//					// 如果没找到，新增一条记录
//					nSellId = ContractCfsUtil.addContractCfs(lstNew, pVo.getContractId(), inNumber);
//					nSellIdStr = ContractCfsUtil.addContractId(nSellIdStr, pVo.getContractId());
//				}
//			}
//
//			detailOld.setSellContractCfs(ContractCfsUtil.toString(lstOld));
//			detailNew.setSellContractCfs(ContractCfsUtil.toString(lstNew));
//			if (StringUtils.isNotBlank(nSellId)) {
//				detailNew.setSellContractId(nSellIdStr);
////				detailNew.setSellContractCfs(nSellId);
//			}
//		}
//	}

}
