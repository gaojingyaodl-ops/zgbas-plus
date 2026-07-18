package com.spt.bas.report.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptCtrContractOrverdur;
import com.spt.bas.report.server.service.IRptCtrContractOrverdurService;

@RestController
@RequestMapping(value = "/rpt/orver")
public class RptCtrContractOrverdurApi {
	@Autowired
	private IRptCtrContractOrverdurService ctrContractOrverdurService;
	
	@PostMapping("findPageOrverdur")
	public Page<RptCtrContractOrverdur> findPageOrverdur(@RequestBody RptCtrContractOrverdur vo){
		Page<RptCtrContractOrverdur> findPageOrverdur = ctrContractOrverdurService.findPageOrverdur(vo);
		return findPageOrverdur;
	}
	@PostMapping("findAllOrverdur")
	public List<RptCtrContractOrverdur> findAllOrverdur(@RequestBody RptCtrContractOrverdur vo) {
		return ctrContractOrverdurService.findAllOrverdur(vo);
	}
	@PostMapping("findPageTotal")
	public RptCtrContractOrverdur findPageTotal(@RequestBody RptCtrContractOrverdur vo){
		return ctrContractOrverdurService.findPageTotal(vo);
	}
}
