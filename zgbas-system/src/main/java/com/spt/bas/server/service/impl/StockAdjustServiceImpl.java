package com.spt.bas.server.service.impl;

import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockAdjust;
import com.spt.bas.client.vo.StockAdjustAuditVo;
import com.spt.bas.client.vo.StockAdjustVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.StockAdjustDao;
import com.spt.bas.server.dao.StockAdjustDetailDao;
import com.spt.bas.server.service.IStockAdjustDetailService;
import com.spt.bas.server.service.IStockAdjustService;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class StockAdjustServiceImpl extends BaseService<StockAdjust> implements IStockAdjustService {
	@Autowired
	private StockAdjustDao stockAdjustDao;
	@Autowired
	private IBsKeySequenceService keySequenceService;
	@Autowired
	private IStockAdjustDetailService stockAdjustDetailService;
	@Autowired
	private StockAdjustDetailDao stockAdjustDetailDao;
	
	@Override
	public BaseDao<StockAdjust> getBaseDao() {
		return stockAdjustDao;
	}
	
	@Override
	public Class<StockAdjust> getEntityClazz() {
		return StockAdjust.class;
	}

	@Override
	@ServerTransactional
	public void saveAdjust(StockAdjustVo vo) {
		StockAdjust adjust =null;
		if(vo.getId()==0){
			adjust = new StockAdjust();
			BeanUtils.copyProperties(vo, adjust);
			adjust.setAdjustDate(new Date());
			adjust.setAdjustStatus(BasConstants.ADJUST_STATUS_N);
			adjust.setBusinessNo(keySequenceService.getNextKey(BasConstants.KEYSEQUENCE_CATEGORY_BUSINESSNOPD,vo.getEnterpriseId()));
			adjust = stockAdjustDao.save(adjust);
		}else{
			adjust = stockAdjustDao.findOne(vo.getId());
		}
		//保存盘点明细
		stockAdjustDetailService.saveAdjustDetail(vo, adjust);
	}
	
	@Override
	@ServerTransactional
	public void delete(Long id) {
		stockAdjustDetailDao.deleteByStockAdjustId(id);
		stockAdjustDao.delete(id);
	}

	@Override
	@ServerTransactional
	public void audit(StockAdjustAuditVo vo) {
		StockAdjust adjust = stockAdjustDao.findOne(vo.getStockAdjustId());
		adjust.setAdjustStatus(BasConstants.ADJUST_STATUS_D);
		stockAdjustDao.save(adjust);
		stockAdjustDetailService.updateStockDetailByAdjust(vo);
	}
	
}

