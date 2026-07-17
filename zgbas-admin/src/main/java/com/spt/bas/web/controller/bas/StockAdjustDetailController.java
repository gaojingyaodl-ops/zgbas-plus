package com.spt.bas.web.controller.bas;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.spt.bas.client.entity.StockAdjustDetail;
import com.spt.bas.client.remote.IStockAdjustDetailClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;

/**
 * 库存盘点
 *
 */
@Controller
@RequestMapping("/stock/adjustDetail")
public class StockAdjustDetailController extends SingleCrudControll<StockAdjustDetail, BaseVo>{

	@Autowired
	private IStockAdjustDetailClient stockAdjustDetailClient;
	@Override
	public BaseClient<StockAdjustDetail> getService() {
		return stockAdjustDetailClient;
	}

	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	@Override
	protected void preInsert(StockAdjustDetail e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
}
