package com.spt.bas.server.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.Stock;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.entity.StockFlow;
import com.spt.bas.client.vo.CtrConctractInvalidVo;
import com.spt.bas.client.vo.StockAdjustAuditVo;
import com.spt.bas.client.vo.StockDetailMoveVo;
import com.spt.bas.client.vo.StockFlowVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.StockFlowDao;
import com.spt.bas.server.service.IStockFlowService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class StockFlowServiceImpl extends BaseService<StockFlow> implements IStockFlowService {
	@Autowired
	private StockFlowDao stockFlowDao;
	@Autowired
	private CtrContractDao contractDao;
	/*@Autowired
	private ApplyDeliveryInDao applyDeliveryInDao;
	@Autowired
	private ApplyDeliveryOutDao applyDeliveryOutDao;*/
	@Override
	public BaseDao<StockFlow> getBaseDao() {
		return stockFlowDao;	
	}
	
	@Override
	public Class<StockFlow> getEntityClazz() {
		return StockFlow.class;
	}

	@Override
	@ServerTransactional
	public void saveStockFlow(StockFlowVo sfVo, Stock stock) {
		StockFlow flow = new StockFlow();
//		flow.setAddSub(addSub);
//		flow.setApplyId();
//		flow.setBrandNumber(stock.getBrandNumber());
//		flow.setContractId(contractId);
//		flow.setCreatedUserName(createdUserName);
//		flow.setCreatedUserId(createdUserId);
//		flow.setDealNumber(dealNumber);
//		flow.setDealPrice(dealPrice);
		BeanUtils.copyProperties(sfVo, flow);
		BeanUtils.copyProperties(stock, flow);
		flow.setStockId(stock.getId());
		flow.setRestRealNumber(stock.getRealNumber());
		flow.setRestFrozenNumber(stock.getFrozenNumber());
		flow.setCreatedDate(new Date());
		flow.setUpdatedDate(new Date());
		flow.setId(null);
		stockFlowDao.save(flow);
	}

	@Override
	@ServerTransactional
	public void updateContractId(Long applyId,Long contractId,String operationType) {
		List<StockFlow> flowList = stockFlowDao.findByApplyIdAndOperationType(applyId,operationType);
		for(StockFlow flow:flowList){
			flow.setContractId(contractId);
			stockFlowDao.save(flow);
		}
	}
	@Override
	@ServerTransactional
	public void deleteOnBack(Long applyId,String operationType) {
		stockFlowDao.deleteByApplyIdAndOperationType(applyId,operationType);
		
	}

	@ServerTransactional
	@Override
	public void insert2Flow(Stock oldStock, Stock newStock, StockDetail entity,StockDetailMoveVo vo) {
		BigDecimal totalNumber = vo.getMoveRealNumber();
		StockFlow flow1 = new StockFlow();
		BeanUtils.copyProperties(oldStock, flow1);
		flow1.setContractId(Long.valueOf(entity.getBuyContractId()));
		flow1.setAddSub(BasConstants.STOCK_NUMBER_SUB);
		flow1.setDealNumber(totalNumber);
		flow1.setStockId(oldStock.getId());
		flow1.setRestFrozenNumber(oldStock.getFrozenNumber());
		flow1.setRestRealNumber(oldStock.getRealNumber());
		flow1.setCreatedDate(new Date());
		flow1.setUpdatedDate(new Date());
		flow1.setId(null);
		flow1.setCreatedUserId(vo.getCurUserId());
		flow1.setCreatedUserName(vo.getCurUserName());
		flow1.setOperationType(BasConstants.OPERATE_TYPE_MS);
		stockFlowDao.save(flow1);
		
		StockFlow flow2 = new StockFlow();
		BeanUtils.copyProperties(flow1, flow2);
		flow2.setAddSub(BasConstants.STOCK_NUMBER_ADD);
		flow2.setOperationType(BasConstants.OPERATE_TYPE_MA);
		flow2.setRestFrozenNumber(newStock.getFrozenNumber());
		flow2.setRestRealNumber(newStock.getRealNumber());
		flow2.setStockId(newStock.getId());
		flow2.setWarehouseName(vo.getWarehouseName());
		flow2.setId(0l);
		stockFlowDao.save(flow2);
	}
	// 库存流水添加合同编号字段及出入库单号
	@Override
	public Page<StockFlowVo> findPageVo(PageSearchVo queryVo) {
		Page<StockFlow>  page = findPage(queryVo);
		List<StockFlowVo> voList = new ArrayList<StockFlowVo>();
		for (StockFlow flow : page.getContent()) {
			//String applyNo = "";
			StockFlowVo vo = new StockFlowVo();
			BeanUtils.copyProperties(flow, vo);
			if(flow.getContractId()!=null  && flow.getApplyId()!=null){
				CtrContract ctr = contractDao.findOne(Long.valueOf(flow.getContractId()));
				vo.setContractNo(ctr.getBusinessNo());
				/*if(ctr.getContractType().equals(BasConstants.CONTRACTTYPE_BUY)){
					ApplyDeliveryIn applyDeliveryIn = applyDeliveryInDao.findOne(flow.getApplyId());
					if(applyDeliveryIn!=null){
						applyNo = applyDeliveryIn.getApplyNo();
					}
				}else if(ctr.getContractType().equals(BasConstants.CONTRACTTYPE_SELL)){
					ApplyDeliveryOut applyDeliveryOut = applyDeliveryOutDao.findOne(flow.getApplyId());
					if(applyDeliveryOut!=null){
						applyNo = applyDeliveryOut.getApplyNo();
					}
				}*/
			}
			//vo.setDeliveryOutNum(applyNo);
			voList.add(vo);
		}
		PageRequest pageRequestNew = PageRequest.of(queryVo.getPage()-1, queryVo.getRows());
		Page<StockFlowVo> pageVo=new PageImpl<>(voList, pageRequestNew, page.getTotalElements());
		return pageVo;
	}
	
	@Override
	@ServerTransactional
	public void cancelContract(Stock stock,BigDecimal fixNumber, CtrConctractInvalidVo vo,Long contractId,String applyType) {
		//新增撤销的库存流水
		List<StockFlow> list = stockFlowDao.findByContractIdAndOperationType(contractId, applyType);
		if(list!=null&&list.size()>0){
			StockFlow entity = list.get(0);
			StockFlow flow = new StockFlow();
			BeanUtils.copyProperties(entity, flow);
			Date date = new Date();
			flow.setCreatedDate(date);
			flow.setUpdatedDate(date);
			flow.setId(null);
			flow.setCreatedUserId(vo.getUserId());
			flow.setCreatedUserName(vo.getUserName());
			if(BasConstants.APPLY_TYPE_S.equals(applyType) || BasConstants.APPLY_TYPE_L.equals(applyType)){
				//销售撤销
				flow.setOperationType(BasConstants.OPERATE_TYPE_SC);
			}else{
				//采购撤销
				flow.setOperationType(BasConstants.OPERATE_TYPE_BC);
			}
			flow.setWarehouseName(stock.getWarehouseName());
			flow.setWarehouseId(flow.getWarehouseId());
			flow.setDealNumber(fixNumber);
			flow.setStockId(stock.getId());
			flow.setRestRealNumber(stock.getRealNumber());
			flow.setRestFrozenNumber(stock.getFrozenNumber());
			flow.setAddSub(BasConstants.STOCK_NUMBER_SUB);
			stockFlowDao.save(flow);
		}
	}

	@Override
	@ServerTransactional
	public void saveAdjust(Stock stock,StockDetail detail, String operationType,StockAdjustAuditVo vo) {
		StockFlow flow = new StockFlow();
		BeanUtils.copyProperties(stock, flow);
		flow.setStockId(stock.getId());
		flow.setRestRealNumber(stock.getRealNumber());
		flow.setRestFrozenNumber(stock.getFrozenNumber());
		flow.setCreatedDate(new Date());
		flow.setUpdatedDate(new Date());
		flow.setId(null);
		flow.setOperationType(operationType);
		if(operationType.equals(BasConstants.OPERATE_TYPE_AA)){
			flow.setAddSub(BasConstants.STOCK_NUMBER_ADD);
		}else{
			flow.setAddSub(BasConstants.STOCK_NUMBER_SUB);
		}
		flow.setDealNumber(vo.getDifferentNumber().abs());
		flow.setCreatedUserId(vo.getUserId());
		flow.setCreatedUserName(vo.getUserName());
		stockFlowDao.save(flow);
	}
	
	
	
}

