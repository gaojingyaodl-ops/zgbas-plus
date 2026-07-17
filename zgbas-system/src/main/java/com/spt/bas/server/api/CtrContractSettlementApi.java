package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.client.entity.CtrContractSettlementCommission;
import com.spt.bas.client.vo.BudgetSettlementOphisSearchVo;
import com.spt.bas.client.vo.BudgetSettlementOphisVo;
import com.spt.bas.server.service.ICtrContractSettlementCommissionService;
import com.spt.bas.server.service.ICtrContractSettlementService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping(value = "bs/contractSettlement")
public class CtrContractSettlementApi extends BaseApi<CtrContractSettlement> {
	@Autowired
	private ICtrContractSettlementService ctrContractSettlementService;
	@Autowired
	private ICtrContractSettlementCommissionService commissionService;

	@Override
	public IBaseService<CtrContractSettlement> getService() {
		return ctrContractSettlementService;
	}

	@PostMapping(value = "markSettlement")
	public void markSettlement(@RequestBody List<Long> settlementIds){
		ctrContractSettlementService.markSettlement(settlementIds);
	}

	@PostMapping(value = "refreshSettlement")
	public void refreshSettlement(@RequestBody List<Long> settlementIds) throws ExecutionException, InterruptedException {
		ctrContractSettlementService.refreshSettlement(settlementIds);
	}

	@PostMapping(value = "updateSettlementOphis")
	public void updateSettlementOphis(@RequestBody BudgetSettlementOphisVo ophisVo){
		ctrContractSettlementService.updateSettlementOphis(ophisVo);
	}

	@PostMapping(value = "sumPageSettlement")
	public CtrContractSettlement sumPageSettlement(@RequestBody PageSearchVo searchVo){
		return ctrContractSettlementService.sumPageSettlement(searchVo);
	}
	// 修改汇总标识
	@PostMapping(value = "updateSettleTotalFlg")
	public void updateSettleTotalFlg(@RequestBody List<Long> settlementId){
		ctrContractSettlementService.updateSettleTotalFlg(settlementId);
	}

	@PostMapping("findIndexPage")
	Page<CtrContractSettlement> findIndexPage(@RequestBody BudgetSettlementOphisSearchVo searchVo){
		return ctrContractSettlementService.findIndexPage(searchVo);
	}

	@PostMapping("sumIndexPage")
	CtrContractSettlement sumIndexPage(@RequestBody BudgetSettlementOphisSearchVo searchVo){
		return ctrContractSettlementService.sumIndexPage(searchVo);
	}

	@PostMapping("findSettlementDetail")
	public List<CtrContractSettlementCommission> findSettlementDetail(@RequestBody Long settlementId){
		return commissionService.findSettlementCommissionList(settlementId);
	}

	@PostMapping("finalAccount")
	public void finalAccount(@RequestBody CtrContractSettlement settlement) throws ExecutionException, InterruptedException {
		ctrContractSettlementService.finalAccount(settlement);
	}
}

