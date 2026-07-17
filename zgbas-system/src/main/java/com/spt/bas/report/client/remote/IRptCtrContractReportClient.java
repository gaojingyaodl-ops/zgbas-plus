package com.spt.bas.report.client.remote;

import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.ContractShowVo;
import com.spt.bas.client.vo.DcsxShowVo;
import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCtrContractReport;
import com.spt.bas.report.client.entity.RptCtrContractWarnReport;
import com.spt.bas.report.client.vo.*;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ReportConstant.SERVER_NAME,path= "spt-bas-report/ctr/contractReport",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptCtrContractReportClient {
	
	@PostMapping("findNotDeliveryInPage")
	PageDown<RptCtrContractReport> findNotDeliveryInPage(@RequestBody RptCtrContractReportSearchVo searchVo);
	
	@PostMapping("findNotDeliveryInPageSum")
	RptCtrContractReport findNotDeliveryInPageSum(@RequestBody RptCtrContractReportSearchVo searchVo);
	
	@PostMapping("findPreSellPage")
	PageDown<RptCtrContractReport> findPreSellPage(@RequestBody RptCtrContractReportSearchVo searchVo);
	
	@PostMapping("findSXReceivePage")
	PageDown<RptCtrContractReport> findSXReceivePage(@RequestBody RptCtrContractReportSearchVo searchVo);

	@PostMapping("findProfitPage")
	PageDown<RptCtrProfitVo> findProfitPage(@RequestBody RptCtrProfitSearchVo searchVo);
	
	@PostMapping("findProfitSum")
	RptCtrProfitVo findProfitSum(@RequestBody RptCtrProfitSearchVo searchVo);

	@PostMapping("findTypeProfitPage")
	PageDown<RptCtrTypeProfitVo> findTypeProfitPage(@RequestBody RptCtrProfitSearchVo searchVo);
	
	@PostMapping("findTypeProfitSum")
	RptCtrTypeProfitVo findTypeProfitSum(@RequestBody RptCtrProfitSearchVo searchVo);

	@PostMapping("findProfitByDeptId")
	List<Long> findProfitByDeptId(@RequestBody RptCtrProfitSearchVo searchVo);

	@PostMapping("findRptContractPage")
	PageDown<RptCtrContractRptVo> findRptContractPage(@RequestBody ContractSearchVo searchVo);

	@PostMapping("findRptSumPageContract")
	RptCtrContractRptVo findRptSumPageContract(@RequestBody ContractSearchVo searchVo);
	
	@PostMapping("findIndexRptContractPage")
	PageDown<ContractShowVo> findIndexRptContractPage(@RequestBody ContractSearchVo searchVo);

	@PostMapping("findIndexRptSumPageContract")
	RptCtrContractRptVo findIndexRptSumPageContract(@RequestBody ContractSearchVo searchVo);

	@PostMapping("findRptContractWarnPage")
	PageDown<RptCtrContractWarnReport> findRptContractWarnPage(@RequestBody RptCtrContractWarnSearchVo searchVo);

	@PostMapping("findRptContractWarnSum")
	RptCtrContractWarnReport findRptContractWarnSum(@RequestBody RptCtrContractWarnSearchVo searchVo);

	@PostMapping("mergeChainExport")
    List<RptExportChainVo> mergeChainExport(@RequestBody List<DcsxShowVo> dcsxShowVoList);
}
