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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyDeliveryIn;
import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.Stock;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.entity.StockDetailHis;
import com.spt.bas.client.vo.StockDetailHisVo;
import com.spt.bas.client.vo.StockDetailMoveVo;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.bas.server.dao.ApplyDeliveryInDao;
import com.spt.bas.server.dao.ApplyDeliveryOutDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.StockDetailHisDao;
import com.spt.bas.server.service.IStockDetailHisService;
import com.spt.bas.server.util.StringUtility;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class StockDetailHisServiceImpl extends BaseService<StockDetailHis> implements IStockDetailHisService {
	@Autowired
	private StockDetailHisDao stockDetailHisDao;
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private ApplyDeliveryOutDao applyDeliveryOutDao;
	@Autowired
	private ApplyDeliveryInDao applyDeliveryInDao;
	@Override
	public BaseDao<StockDetailHis> getBaseDao() {
		return stockDetailHisDao;
	}
	
	@Override
	public Class<StockDetailHis> getEntityClazz() {
		return StockDetailHis.class;
	}
	
	@Override
	@ServiceTransactional
	public void updateContractId(Long detailId,Long applyId, Long contractId,String operationType) {
		List<StockDetailHis> lisHis = stockDetailHisDao.findDetailHis(detailId,applyId,operationType);
		for(StockDetailHis his:lisHis){
			his.setContractId(String.valueOf(contractId));
			stockDetailHisDao.save(his);
		}
	}
	
	@Override
	public void saveBuy(StockDetail detail,StockDetailRequest request) throws ApplicationException {
		if(detail!=null){
			StockDetailHis his = new StockDetailHis();
			BeanUtils.copyProperties(detail,his);
			his.setProductAttr(BasConstants.STOCK_STATUS_P);
			his.setStockDetailId(detail.getId());
			his.setContractId(StringUtility.o2s(request.getCtrContractId()));
			his.setDealNumber(request.getDealNumber());
			his.setApplyId(request.getApplyId());
			his.setPreFrozenNumber(request.getPreFrozenNumber());
			his.setPreRealNumber(request.getPreRealNumber());
//			his.setWarehouseRemain(stock.getRealNumber());
//			his.setWarehouseFrozenRemain(stock.getFrozenNumber());
			if (request.getApplyType().equals(BasConstants.APPLY_TYPE_B) || request.getApplyType().equals(BasConstants.APPLY_TYPE_MB)|| request.getApplyType().equals(BasConstants.APPLY_TYPE_RB)){
				his.setRealRemainNumber(detail.getAvailableNumber());
				his.setRemainFrozenNumber(BigDecimal.ZERO);
			}else{
				his.setRealRemainNumber(BigDecimal.ZERO);//剩余可用0
//				his.setRemainFrozenNumber(detail.getFrozenNumber());
			}
			his.setOperationType(request.getApplyType());
			saveStockDetail(his);
		}
	}

	@Override
	public void saveDeliveryIn(StockDetail detail,StockDetailRequest request,String bizType,boolean isBack) throws ApplicationException {
		if(detail!=null){
			StockDetailHis his = new StockDetailHis();
			BeanUtils.copyProperties(detail, his);
			his.setDealNumber(request.getDealNumber());
			his.setApplyId(request.getApplyId());
			his.setRealRemainNumber(detail.getAvailableNumber());//该条detail下的剩余可用
//			his.setRemainFrozenNumber(detail.getFrozenNumber());
			his.setPreFrozenNumber(request.getPreFrozenNumber());
			his.setPreRealNumber(request.getPreRealNumber());
			if (isBack) {
				//入库撤回
				if(BasConstants.STOCK_NUMBER_SUB.equals(bizType)){
					//减少新仓库库存
					his.setOperationType(BasConstants.OPERATE_TYPE_IC);
				}else{
					//增加原仓库库存
					his.setWarehouseName(request.getWarehouseName());
					his.setWarehouseId(request.getWarehouseId());
					his.setOperationType(BasConstants.OPERATE_TYPE_IB);//
				}
			}else {
				//正常入库
				if(BasConstants.STOCK_NUMBER_SUB.equals(bizType)){
					//减少原仓库库存
					his.setWarehouseName(request.getWarehouseName());
					his.setWarehouseId(request.getWarehouseId());
					his.setOperationType(BasConstants.OPERATE_TYPE_IS);
				}else{
					//增加新仓库库存
					his.setOperationType(BasConstants.APPLY_TYPE_I);//
				}
				
			}
			
			his.setStockDetailId(detail.getId());
			his.setContractId(detail.getBuyContractId());
			saveStockDetail(his);
		}
	}

	@Override
	public void saveSell(StockDetailRequest request,StockDetail detail) throws ApplicationException {
		if(request!=null){
//			if(request.getCtrContractId()!=null && request.getCtrContractId() > 0){
//				String sellContractId =String.valueOf(request.getCtrContractId());
//				Map<String, Object> queryParams = new HashMap<String, Object>();
//				queryParams.put("EQS_productCd", request.getProductCd());// 商品类型
//				queryParams.put("EQL_factoryId", request.getFactoryId());// 厂商
//				queryParams.put("EQS_brandNumber", request.getBrandNumber());// 牌号
//				queryParams.put("EQS_operationType", BasConstants.APPLY_TYPE_S);
//				queryParams.put("EQS_warehouseName", request.getWarehouseName());
//				queryParams.put("EQM_remainFrozenNumber", request.getDealNumber());// 销售的冻结数量
//				// 获取唯一一条明细数据
//				Specification<StockDetailHis> spec = WebUtil.buildSpecification(queryParams);
//				Sort sort = new Sort(Direction.DESC, "createdDate");
//				List<StockDetailHis> list = this.stockDetailHisDao.findAll(spec, sort);
//				if(list!=null&&list.size()>0){
//					for(StockDetailHis his:list){
//						if(StringUtils.isBlank(his.getContractId())){
//							his.setContractId(sellContractId);
//							save(his);
//							break;
//						}
//					}
//				}
//				
//			}else{
				//如果是预售采购，则需要生成一条预售的流水
				String applyType = request.getApplyType();
				if(applyType.equals(BasConstants.APPLY_TYPE_A)){
					applyType = BasConstants.APPLY_TYPE_L;
				}
				StockDetailHis his = new StockDetailHis();
				BeanUtils.copyProperties(detail,his);
				his.setContractId(StringUtility.o2s(request.getCtrContractId()));
				his.setRealRemainNumber(detail.getAvailableNumber());//剩余可用0
//				his.setRemainFrozenNumber(detail.getFrozenNumber());
				his.setOperationType(applyType);
				his.setProductAttr(detail.getProductAttr());
				his.setStockDetailId(detail.getId());
				his.setDealNumber(request.getDealNumber());
				his.setApplyId(request.getApplyId());
				his.setPreFrozenNumber(request.getPreFrozenNumber());
				his.setPreRealNumber(request.getPreRealNumber());
//				his.setWarehouseRemain(stock.getRealNumber());
//				his.setWarehouseFrozenRemain(stock.getFrozenNumber());
				saveStockDetail(his);
//			}
			
		}
	}
	
	@Override
	@ServiceTransactional
	public void deleteSell(StockDetail detail, StockDetailRequest request) {
		// TODO Auto-generated method stub
//		Map<String,Object> queryParams = new HashMap<String,Object>();
//		queryParams.put("EQS_productCd", request.getProductCd());//商品类型
//		queryParams.put("EQL_factoryId", request.getFactoryId());//厂商
//		queryParams.put("EQS_brandNumber", request.getBrandNumber());//牌号
//		queryParams.put("EQS_operationType", BasConstants.APPLY_TYPE_S);//卖
//		//queryParams.put("EQL_stockId", detail.getStockId());
//		queryParams.put("EQL_stockDetailId", detail.getId());
//		queryParams.put("EQM_dealNumber", request.getDealNumber());
//		Specification<StockDetailHis> spec = WebUtil.buildSpecification(queryParams);
//		Sort sort=new Sort(Direction.DESC, "createdDate");
//		List<StockDetailHis> detailList = this.stockDetailHisDao.findAll(spec, sort);
		List<StockDetailHis> lisHis = stockDetailHisDao.findDetailHis(detail.getId(),request.getApplyId(),BasConstants.APPLY_TYPE_S);
		stockDetailHisDao.deleteAll(lisHis);
//		if(lisHis!=null&&lisHis.size()>0){
//			this.stockDetailHisDao.delete(detailList.get(0).getId());
//		}
	}

	@Override
	@ServiceTransactional
	public void saveDeliveryOut(StockDetail detail, StockDetailRequest request,String operateType) throws ApplicationException {
		if(detail!=null){
			StockDetailHis his = new StockDetailHis();
			//PropertyUtils.copyProperties(his, detail);
			BeanUtils.copyProperties(detail,his);
//			his.setRemainFrozenNumber(detail.getFrozenNumber());//剩余冻结
			his.setDealNumber(request.getDealNumber());
			his.setOperationType(operateType);//BasConstants.APPLY_TYPE_O
			his.setContractId(request.getCtrContractId()+"");
			his.setProductAttr(detail.getProductAttr());
			his.setStockDetailId(detail.getId());
			his.setApplyId(request.getApplyId());
			his.setPreFrozenNumber(request.getPreFrozenNumber());
			his.setPreRealNumber(request.getPreRealNumber());
//			his.setStockId(request.getStockId());
			his.setRealRemainNumber(detail.getAvailableNumber());//剩余可用
//			his.setWarehouseRemain(request.getStockRemainNumber());
//			his.setWarehouseFrozenRemain(request.getStockRemainFrozen());
			saveStockDetail(his);
		}
	}

	@Override
	public void insertHisByCancel(StockDetail detail, BigDecimal fixNumber, CtrProduct product,String applyType) throws ApplicationException {
		// TODO Auto-generated method stub
		StockDetailHis his = new StockDetailHis();
		BeanUtils.copyProperties(detail,his);
		if(BasConstants.APPLY_TYPE_S.equals(applyType)){
			//销售撤销
			his.setOperationType(BasConstants.OPERATE_TYPE_SC);
		}else{
			//采购撤销
			his.setOperationType(BasConstants.OPERATE_TYPE_BC);
		}
		his.setRealRemainNumber(detail.getAvailableNumber());//剩余可用0
//		his.setRemainFrozenNumber(detail.getFrozenNumber());
		his.setContractId(product.getCtrContractId()+"");
		his.setProductAttr(product.getProductAttr());
		his.setDealNumber(fixNumber);
		his.setStockDetailId(detail.getId());
		//TODO 
//		his.setPreFrozenNumber(request.getPreFrozenNumber());
//		his.setPreRealNumber(request.getPreRealNumber());
//		his.setStockId(stock.getId());
//		his.setWarehouseRemain(stock.getRealNumber());
//		his.setWarehouseFrozenRemain(stock.getFrozenNumber());
		saveStockDetail(his);
		
	}
	
	private void saveStockDetail(StockDetailHis his) {
		Date date = new Date();
		his.setCreatedDate(date);
		his.setUpdatedDate(date);
		his.setId(null);
		stockDetailHisDao.save(his);
	}

	@ServiceTransactional
	@Override
	public void saveMoveHis(StockDetail detail, StockDetailMoveVo changeVo,String type) {
		StockDetailHis his = new StockDetailHis();
		BeanUtils.copyProperties(detail,his);
		his.setContractId(detail.getBuyContractId());
		his.setRealRemainNumber(detail.getAvailableNumber());
//		his.setRemainFrozenNumber(detail.getFrozenNumber());
		his.setDealNumber(changeVo.getMoveRealNumber());
		his.setStockDetailId(detail.getId());
//		his.setWarehouseRemain(detail.getAvailableNumber());
//		his.setWarehouseFrozenRemain(detail.getFrozenNumber());
		his.setOperationType(type);
		his.setOperationDate(changeVo.getMoveDate());
		his.setRemark(changeVo.getRemark());
		saveStockDetail(his);
	}

	@Override
	@ServiceTransactional
	public void savePresell(StockDetail detail, CtrProduct product, Stock stock) throws ApplicationException {
		if(detail!=null){
			StockDetailHis his = new StockDetailHis();
			BeanUtils.copyProperties(detail,his);
			his.setRealRemainNumber(detail.getAvailableNumber());
//			his.setRemainFrozenNumber(detail.getFrozenNumber());
			his.setOperationType(BasConstants.APPLY_TYPE_L);
			his.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_P);
			his.setStockDetailId(detail.getId());
			his.setDealNumber(product.getDealNumber());
//			his.setWarehouseRemain(detail.getAvailableNumber());
//			his.setWarehouseFrozenRemain(detail.getFrozenNumber());
			his.setRestPresellNumber(detail.getPresellNumber());
			his.setContractId(product.getCtrContractId()+"");
			saveStockDetail(his);
		}
	}

	@Override
	public Page<StockDetailHisVo> findPageVo(PageSearchVo searchVo) {
		Sort sort = Sort.by(Direction.DESC, "id");
		PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);
		Specification<StockDetailHis> spe = WebUtil.buildSpecification(searchVo.getSearchParams());
		Page<StockDetailHis> page = getBaseDao().findAll(spe, pageRequest);
		List<StockDetailHisVo> voList = new ArrayList<>();
		for (StockDetailHis stockDetailHis : page.getContent()) {
			String applyNo = "";
			StockDetailHisVo vo = new StockDetailHisVo();
			BeanUtils.copyProperties(stockDetailHis, vo);
			if (StringUtility.isNotBlank(stockDetailHis.getContractId())) {
				CtrContract ctr = ctrContractDao.findOne(Long.valueOf(stockDetailHis.getContractId()));
				vo.setContractNo(ctr.getBusinessNo());
				if(ctr.getContractType().equals(BasConstants.CONTRACT_TYPE_B) && stockDetailHis.getApplyId()!=null){
					ApplyDeliveryIn applyDeliveryIn = applyDeliveryInDao.findOne(stockDetailHis.getApplyId());
					if(applyDeliveryIn!=null){
						applyNo = applyDeliveryIn.getApplyNo();
					}
				}else if(ctr.getContractType().equals(BasConstants.CONTRACT_TYPE_S) && stockDetailHis.getApplyId()!=null){
					ApplyDeliveryOut applyDeliveryOut = applyDeliveryOutDao.findOne(stockDetailHis.getApplyId());
					if(applyDeliveryOut!=null){
						applyNo = applyDeliveryOut.getApplyNo();
					}
				}
			}
			vo.setApplyNo(applyNo);
			voList.add(vo);
		}
		PageRequest pageRequest_new = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
		Page<StockDetailHisVo> pageVo = new PageImpl<>(voList, pageRequest_new, page.getTotalElements());
		return pageVo;
	}

	@Override
	@ServiceTransactional
	public void saveAdjust(StockDetail detail, BigDecimal diff,String operationType,Long applyId,Long contractId) {
		StockDetailHis his = new StockDetailHis();
		BeanUtils.copyProperties(detail,his);
		his.setContractId(detail.getBuyContractId());
		his.setRealRemainNumber(detail.getAvailableNumber());
//		his.setRemainFrozenNumber(detail.getFrozenNumber());
		his.setDealNumber(diff);
		his.setStockDetailId(detail.getId());
		his.setOperationType(operationType);
		his.setApplyId(applyId);
		if(contractId!=null){
			his.setContractId(contractId+"");
		}
		saveStockDetail(his);
		
	}
	
}

