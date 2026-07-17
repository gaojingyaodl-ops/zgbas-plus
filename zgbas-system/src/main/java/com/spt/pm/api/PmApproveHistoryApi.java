package com.spt.pm.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveHistory;
import com.spt.pm.service.IPmApproveHistoryService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "pm/approveHistory")
public class PmApproveHistoryApi extends BaseApi<PmApproveHistory> {
	@Autowired
	private IPmApproveHistoryService pmApproveHistoryService;
	
	@Override
	public IBaseService<PmApproveHistory> getService() {
		return pmApproveHistoryService;
	}
	
	@PostMapping("findByApproveId")
	public List<PmApproveHistory> findByApproveId(@RequestBody Long approveId){
		return pmApproveHistoryService.findByApproveId(approveId);
	}
	
	@PostMapping("findByApproveIdOrProcessId")
	public List<PmApproveHistory> findByApproveIdOrProcessId(@RequestBody PmApprove pmApprove){
		return pmApproveHistoryService.findByApproveIdOrProcessId(pmApprove.getId(), pmApprove.getProcessId(), pmApprove.getEnterpriseId());
	}
}

