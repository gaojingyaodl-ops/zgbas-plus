package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyInternalBuyDetail;
import com.spt.bas.server.service.IApplyInternalBuyDetailService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "apply/internalBuyDetail")
public class ApplyInternalBuyDetailApi extends BaseApi<ApplyInternalBuyDetail> {
	@Autowired
	private IApplyInternalBuyDetailService applyInternalBuyDetailService;
	
	@Override
	public IBaseService<ApplyInternalBuyDetail> getService() {
		return applyInternalBuyDetailService;
	}
	
	@PostMapping("findByApplyInternalBuyId")
	public List<ApplyInternalBuyDetail> findByApplyInternalBuyId(@RequestBody Long interId){
		return applyInternalBuyDetailService.findByApplyInternalBuyId(interId);
	}
	
	@PostMapping("findByStockDetailId")
	public List<ApplyInternalBuyDetail> findByStockDetailId(@RequestBody Long stockDetailId){
		return applyInternalBuyDetailService.findByStockDetailId(stockDetailId);
	}
}

