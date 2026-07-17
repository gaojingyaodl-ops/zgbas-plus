package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyBusinessPay;
import com.spt.bas.client.vo.ApplyBusinessPayVo;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.CtrContractDeliveryVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyBusinessPayService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "business/pay")
public class ApplyBusinessPayApi extends BaseApi<ApplyBusinessPay> {
	@Autowired
	private IApplyBusinessPayService applyBusinessPayService;

	@Override
	public IBaseService<ApplyBusinessPay> getService() {
		return applyBusinessPayService;
	}

	@RequestMapping(value = "updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		applyBusinessPayService.updateFileId(vo.getId(),vo.getFileId());
	}

	@PostMapping("findPageContract")
	Page<ApplyBusinessPayVo> findPageContract(@RequestBody ApplyBusinessPayVo queryVo){
		return	applyBusinessPayService.findPageContract(queryVo);
	}


}

