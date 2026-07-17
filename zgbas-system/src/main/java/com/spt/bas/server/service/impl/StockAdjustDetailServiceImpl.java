package com.spt.bas.server.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockAdjust;
import com.spt.bas.client.entity.StockAdjustDetail;
import com.spt.bas.client.vo.StockAdjustAuditVo;
import com.spt.bas.client.vo.StockAdjustVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.StockAdjustDetailDao;
import com.spt.bas.server.service.IStockAdjustDetailService;
import com.spt.bas.server.stock.service.IStockDetailAdjustService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class StockAdjustDetailServiceImpl extends BaseService<StockAdjustDetail> implements IStockAdjustDetailService {
	@Autowired
	private StockAdjustDetailDao stockAdjustDetailDao;
	@Autowired
	private IStockDetailAdjustService stockDetailAdjustService;
	
	@Override
	public BaseDao<StockAdjustDetail> getBaseDao() {
		return stockAdjustDetailDao;
	}
	
	@Override
	public Class<StockAdjustDetail> getEntityClazz() {
		return StockAdjustDetail.class;
	}

	@Override
	@ServerTransactional
	public void saveAdjustDetail(StockAdjustVo vo, StockAdjust adjust) {
		for (StockAdjustDetail deail : vo.getLstInsert()) {
			deail.setStockAdjustId(adjust.getId());
			deail.setAdjustStatus(adjust.getAdjustStatus());
			deail.setBusinessNo(adjust.getBusinessNo());
			deail.setEnterpriseId(adjust.getEnterpriseId());
			stockAdjustDetailDao.save(deail);
		}
		for (StockAdjustDetail deail : vo.getLstUpdate()) {
			stockAdjustDetailDao.save(deail);
		}
		for (StockAdjustDetail deail : vo.getLstDelete()) {
			stockAdjustDetailDao.delete(deail);
		}
		
	}

	@Override
	@ServerTransactional
	public void updateStockDetailByAdjust(StockAdjustAuditVo vo) {
		List<StockAdjustDetail> adjustList = stockAdjustDetailDao.findByStockAdjustId(vo.getStockAdjustId());
		for (StockAdjustDetail adjust : adjustList) {
			
			//BigDecimal sysFrozenNumber = adjust.getSysFrozenNumber();
			BigDecimal sysAvailableNumber = adjust.getSysAvailableNumber();
			//BigDecimal realFrozenNumber = adjust.getRealFrozenNumber();
			BigDecimal realAvailableNumber = adjust.getRealAvailableNumber();
			
			//BigDecimal frozeDiff = sysFrozenNumber.subtract(realFrozenNumber);
			BigDecimal availDiff = sysAvailableNumber.subtract(realAvailableNumber);
			
			vo.setStockDetailId(adjust.getStockDetailId());
			/*if(frozeDiff.compareTo(BigDecimal.ZERO) !=0){
				vo.setDifferentNumber(frozeDiff);
				vo.setRealNumber(realFrozenNumber);
				vo.setType("F");
				stockDetailService.saveDetailAndHis(vo);
			}*/
			if(availDiff.compareTo(BigDecimal.ZERO) !=0){
				vo.setDifferentNumber(availDiff);
				vo.setRealNumber(realAvailableNumber);
				vo.setType("A");
				stockDetailAdjustService.saveDetailAndHis(vo);
			}
			
			adjust.setAdjustStatus(BasConstants.ADJUST_STATUS_D);
			stockAdjustDetailDao.save(adjust);
		}
	}
	
}

