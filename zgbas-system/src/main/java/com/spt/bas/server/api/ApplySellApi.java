package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplySell;
import com.spt.bas.client.vo.ApplySellVo;
import com.spt.bas.client.vo.ApproveFormPrintVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplySellService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "apply/sell")
public class ApplySellApi extends BaseApi<ApplySell> {
	@Autowired
	private IApplySellService applySellService;

	@Override
	public IBaseService<ApplySell> getService() {
		return applySellService;
	}

	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo){
		applySellService.updateFileId(vo.getId(), vo.getFileId());
	}

	@PostMapping("findByContractId")
	public ApplySell findByContractId(@RequestBody Long contractId){
		return applySellService.findByContractId(contractId);
	}

	@PostMapping("printApplySell")
	public ApproveFormPrintVo printApplySell(@RequestBody Long applyId){
		return applySellService.printApplySell(applyId);
	}

/*	@PostMapping("findWarehouse")
	public List<Stock> findWarehouse(@RequestBody ApplySellWarehouseVo vo){
		return stockService.findWarehouse(vo);
	}

	@PostMapping("findDealNumber")
	public List<Stock> findDealNumber(@RequestBody ApplySellWarehouseVo vo){
		return stockService.findDealNumber(vo);
	}	*/


	@PostMapping(value = "applySell")
	public void applySell(@RequestBody ApplySellVo applySellVo) throws ApplicationException {
		applySellService.applySell(applySellVo);
	}
}

