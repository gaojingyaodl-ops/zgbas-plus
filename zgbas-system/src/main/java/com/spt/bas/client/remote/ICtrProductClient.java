package com.spt.bas.client.remote;

import java.util.Date;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.vo.CtrProductSearchVo;
import com.spt.bas.client.vo.CtrProductVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/product",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrProductClient extends BaseClient<CtrProduct> {

	@PostMapping("findByOutCtrContractId")
	public List<CtrProduct> findByOutCtrContractId(@RequestBody Long ctrContractId);

	@PostMapping("findProductList")
	public PageDown<CtrProductVo> findProductList(@RequestBody CtrProductSearchVo searchVo);

	@PostMapping("findConfirmProductList")
	public PageDown<CtrProductVo> findConfirmProductList(@RequestBody CtrProductSearchVo searchVo);

	@PostMapping("findList")
	public List<CtrProductVo> findList(@RequestBody PageSearchVo pageSearchVo);

	@PostMapping("findListWithNoStock")
	public List<CtrProductVo> findListWithNoStock(@RequestBody PageSearchVo pageSearchVo);

	@PostMapping("findMinDeliveryDateByProductId")
	public Date findMinDeliveryDateByProductId(@RequestBody List<Long> productList);
}

