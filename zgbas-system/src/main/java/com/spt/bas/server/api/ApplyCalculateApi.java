package com.spt.bas.server.api;

import com.spt.bas.server.service.IApplyCalculateService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyCalculate;
import com.spt.bas.client.vo.ApplyCalculateDetailVo;
import com.spt.bas.client.vo.ApplyCalculateFlowVo;


@RestController
@RequestMapping(value = "apply/calculate")
public class ApplyCalculateApi extends BaseApi<ApplyCalculate> {
	@Autowired
	private IApplyCalculateService applyCalculateService;
	
	@Override
	public IBaseService<ApplyCalculate> getService() {
		return applyCalculateService;
	}
	
	@PostMapping("saveDetail")
	public void saveDetail(@RequestBody List<ApplyCalculateDetailVo> detailList) throws ApplicationException{
		applyCalculateService.saveDetail(detailList);
	}
	
	@PostMapping("doCalculate")
	public void doCalculate(@RequestBody ApplyCalculateFlowVo flowVo) throws ApplicationException{
		applyCalculateService.doCalculate(flowVo);
	}
	
	@PostMapping("findByImportId")
	public ApplyCalculate findByImportId(@RequestBody ApplyCalculate calculate) {
		return applyCalculateService.findByImportId(calculate);
	}
	
}

