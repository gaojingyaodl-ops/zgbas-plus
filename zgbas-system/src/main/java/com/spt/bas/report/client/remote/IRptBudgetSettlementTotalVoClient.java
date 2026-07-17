package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.RptBudgetSettlementTotalVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/budget/settlementTotal",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptBudgetSettlementTotalVoClient {

	@PostMapping("findSettlementTotalList")
	public List<RptBudgetSettlementTotalVo> findSettlementTotalList(@RequestBody RptBudgetSettlementTotalVo vo);

	@PostMapping("findDCTotalList")
	public RptBudgetSettlementTotalVo findDCTotalList(@RequestBody RptBudgetSettlementTotalVo vo);

	@PostMapping("findSettleListId")
	public List<Long> findSettleListId(@RequestBody RptBudgetSettlementTotalVo vo);

}
