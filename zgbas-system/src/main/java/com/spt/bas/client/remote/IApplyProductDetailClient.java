package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.vo.ApplyDeliveryApplyIdVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/productDetail",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IApplyProductDetailClient extends BaseClient<ApplyProductDetail> {
	
	@PostMapping("findApplyId")
	List<ApplyProductDetail> findApplyId(@RequestBody ApplyDeliveryApplyIdVo vo);

	@PostMapping("findByProductName")
	List<ApplyProductDetail> findByProductName(@RequestParam(value = "productName") String productName);

}

