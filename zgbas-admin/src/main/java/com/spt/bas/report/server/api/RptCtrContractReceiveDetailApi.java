package com.spt.bas.report.server.api;

import com.spt.bas.report.client.vo.RptContractDateSearchVo;
import com.spt.bas.report.client.vo.RptContractDateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptCtrContractReceiveDetailReport;
import com.spt.bas.report.client.vo.RptReceiveDetailSearchVo;
import com.spt.bas.report.server.service.IRptCtrContractReceiveDetailService;

import java.util.List;

@RestController
@RequestMapping(value = "/rpt/ReceiveDetail")
public class RptCtrContractReceiveDetailApi {
	@Autowired
	private IRptCtrContractReceiveDetailService ctrContractReceiveDetailService;
	
	@PostMapping("findPageReceiveDetail")
	public Page<RptCtrContractReceiveDetailReport> findPageReceiveDetail(@RequestBody RptReceiveDetailSearchVo vo){
		return ctrContractReceiveDetailService.findPageReceiveDetail(vo);
	}
	
	@PostMapping("findPageReceiveDetailSum")
	public RptCtrContractReceiveDetailReport findPageReceiveDetailSum(@RequestBody RptReceiveDetailSearchVo searchVo){
		return ctrContractReceiveDetailService.findPageReceiveDetailSum(searchVo);

	}

	@PostMapping("selectSellReceiveDateList")
	public List<RptContractDateVo> selectSellReceiveDateList(@RequestBody RptContractDateSearchVo searchVo) {
		return ctrContractReceiveDetailService.selectSellReceiveDateList(searchVo);
	}
}
