package com.spt.bas.server.api;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.vo.CtrProductSearchVo;
import com.spt.bas.client.vo.CtrProductVo;
import com.spt.bas.server.service.ICtrProductService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "ctr/product")
public class CtrProductApi extends BaseApi<CtrProduct> {
	@Autowired
	private ICtrProductService ctrProductService;

	@Override
	public IBaseService<CtrProduct> getService() {
		return ctrProductService;
	}

	@PostMapping("findByOutCtrContractId")
	public List<CtrProduct> findByOutCtrContractId(@RequestBody Long ctrContractId){
		return ctrProductService.findByOutCtrContractId(ctrContractId);
	}

	@PostMapping("findProductList")
	public Page<CtrProductVo> findProductList(@RequestBody CtrProductSearchVo searchVo){
		return ctrProductService.findProductList(searchVo);
	}

	@PostMapping("findConfirmProductList")
	public Page<CtrProductVo> findConfirmProductList(@RequestBody CtrProductSearchVo searchVo){
		return ctrProductService.findConfirmProductList(searchVo);
	}

	@PostMapping("findList")
	public List<CtrProductVo> findList(@RequestBody PageSearchVo pageSearchVo) throws Exception {
		return ctrProductService.findList(pageSearchVo);
	}

	@PostMapping("findListWithNoStock")
	public List<CtrProductVo> findListWithNoStock(@RequestBody PageSearchVo pageSearchVo) throws Exception {
		return ctrProductService.findListWithNoStock(pageSearchVo);
	}

	@PostMapping("findMinDeliveryDateByProductId")
	public Date findMixDeliveryDateByProductId(@RequestBody List<Long> productList) {
		return ctrProductService.findMinDeliveryDateByProductId(productList);
	}
}

