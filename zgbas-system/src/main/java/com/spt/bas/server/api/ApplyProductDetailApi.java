package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.vo.ApplyDeliveryApplyIdVo;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "/apply/productDetail")
public class ApplyProductDetailApi extends BaseApi<ApplyProductDetail> {
	@Autowired
	private IApplyProductDetailService applyProductDetailService;
	
	@Override
	public IBaseService<ApplyProductDetail> getService() {
		return applyProductDetailService;
	}
	@PostMapping("findApplyId")
	List<ApplyProductDetail> findApplyId(@RequestBody ApplyDeliveryApplyIdVo vo){
		return applyProductDetailService.findApplyId(vo);
	}
	@PostMapping("findByProductName")
	public List<ApplyProductDetail> findByProductName(@RequestParam(value = "productName") String productName)
	{
		return applyProductDetailService.findByProductName(productName);

	}
}

