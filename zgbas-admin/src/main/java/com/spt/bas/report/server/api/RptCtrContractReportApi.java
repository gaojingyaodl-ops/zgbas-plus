package com.spt.bas.report.server.api;

import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.ContractShowVo;
import com.spt.bas.client.vo.DcsxShowVo;
import com.spt.bas.report.client.entity.RptCtrContractReport;
import com.spt.bas.report.client.entity.RptCtrContractWarnReport;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.report.server.service.IRptCtrContractReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/ctr/contractReport")
public class RptCtrContractReportApi {
	@Autowired
	private IRptCtrContractReportService ctrContractReportService;

	@PostMapping("findNotDeliveryInPage")
	public Page<RptCtrContractReport> findNotDeliveryInPage(@RequestBody RptCtrContractReportSearchVo searchVo){
		return ctrContractReportService.findNotDeliveryInPage(searchVo);
	}

	@PostMapping("findNotDeliveryInPageSum")
	public RptCtrContractReport findNotDeliveryInPageSum(@RequestBody RptCtrContractReportSearchVo searchVo) {
		return ctrContractReportService.findNotDeliveryInPageSum(searchVo);
	}

	@PostMapping("findPreSellPage")
	public Page<RptCtrContractReport> findPreSellPage(@RequestBody RptCtrContractReportSearchVo searchVo){
		return ctrContractReportService.findPreSellPage(searchVo);
	}

	@PostMapping("findSXReceivePage")
	public Page<RptCtrContractReport> findSXReceivePage(@RequestBody RptCtrContractReportSearchVo searchVo){
		return ctrContractReportService.findSXReceivePage(searchVo);
	}

	@PostMapping("findProfitPage")
	public Page<RptCtrProfitVo> findProfitPage(@RequestBody RptCtrProfitSearchVo searchVo){
		return ctrContractReportService.findProfitPage(searchVo);
	}

	@PostMapping("findProfitSum")
	public RptCtrProfitVo findProfitSum(@RequestBody RptCtrProfitSearchVo searchVo){
		return ctrContractReportService.findProfitSum(searchVo);
	}

	@PostMapping("findTypeProfitPage")
	public Page<RptCtrTypeProfitVo> findTypeProfitPage(@RequestBody RptCtrProfitSearchVo searchVo){
		return ctrContractReportService.findTypeProfitPage(searchVo);
	}

	@PostMapping("findTypeProfitSum")
	public RptCtrTypeProfitVo findTypeProfitSum(@RequestBody RptCtrProfitSearchVo searchVo){
		return ctrContractReportService.findTypeProfitSum(searchVo);
	}

	@PostMapping("findProfitByDeptId")
	public List<Long> findProfitByDeptId(@RequestBody RptCtrProfitSearchVo searchVo){
		return ctrContractReportService.findProfitByDeptId(searchVo);
	}

	@PostMapping("findRptContractPage")
	public Page<RptCtrContractRptVo> findRptContractPage(@RequestBody ContractSearchVo searchVo){
		return ctrContractReportService.findRptContractPage(searchVo);
	}
	
	@PostMapping("findIndexRptContractPage")
	public Page<ContractShowVo> findIndexRptContractPage(@RequestBody ContractSearchVo searchVo){
		return ctrContractReportService.findIndexRptContractPage(searchVo);
	}

	@PostMapping("findRptSumPageContract")
	public RptCtrContractRptVo findRptSumPageContract(@RequestBody ContractSearchVo searchVo){
		return ctrContractReportService.findRptSumPageContract(searchVo);
	}
	
	@PostMapping("findIndexRptSumPageContract")
	public RptCtrContractRptVo findIndexRptSumPageContract(@RequestBody ContractSearchVo searchVo){
		return ctrContractReportService.findIndexRptSumPageContract(searchVo);
	}

	@PostMapping("findRptContractWarnPage")
	public Page<RptCtrContractWarnReport> findRptContractWarnPage(@RequestBody RptCtrContractWarnSearchVo searchVo) {
		return ctrContractReportService.findRptContractWarnPage(searchVo);
	}

	@PostMapping("findRptContractWarnSum")
	public RptCtrContractWarnReport findRptContractWarnSum(@RequestBody RptCtrContractWarnSearchVo searchVo) {
		return ctrContractReportService.findRptContractWarnSum(searchVo);
	}

	@PostMapping("mergeChainExport")
	public List<RptExportChainVo> mergeChainExport(@RequestBody List<DcsxShowVo> dcsxShowVoList){
		return ctrContractReportService.mergeChainExport(dcsxShowVoList);
	}
}

