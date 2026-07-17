package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BasContractRela;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IBasContractRelaService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bas/contractRela")
public class BasContractRelaApi extends BaseApi<BasContractRela> {
	@Autowired
	private IBasContractRelaService basContractRelaService;
	
	@Override
	public IBaseService<BasContractRela> getService() {
		return basContractRelaService;
	}
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo){
		basContractRelaService.updateFileId(vo.getId(), vo.getFileId());
	}
	
	@PostMapping("findSaleContractIdByBuyId")
	public String findSaleContractIdByBuyId(@RequestBody String buyContractId){
		return basContractRelaService.findSaleContractIdByBuyId(buyContractId);
	}
}

