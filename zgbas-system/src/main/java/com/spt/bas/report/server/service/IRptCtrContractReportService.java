package com.spt.bas.report.server.service;

import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.ContractShowVo;
import com.spt.bas.client.vo.DcsxShowVo;
import com.spt.bas.report.client.entity.RptCtrContractReport;
import com.spt.bas.report.client.entity.RptCtrContractWarnReport;
import com.spt.bas.report.client.vo.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IRptCtrContractReportService {
	/** 已付款未入库明细 */
	Page<RptCtrContractReport> findNotDeliveryInPage(RptCtrContractReportSearchVo vo);
	
	/** 已付款未入库明细合计 */
	RptCtrContractReport findNotDeliveryInPageSum(RptCtrContractReportSearchVo searchVo);
	
	/** 预售未采购明细 */
	Page<RptCtrContractReport> findPreSellPage(RptCtrContractReportSearchVo searchVo);
	
	/** 赊销收款明细表*/
	Page<RptCtrContractReport> findSXReceivePage(RptCtrContractReportSearchVo vo);

	/** 毛利率查询*/
	Page<RptCtrProfitVo> findProfitPage(RptCtrProfitSearchVo ctrProfitSearchVo);

	/** 毛利率合计*/
	RptCtrProfitVo findProfitSum(RptCtrProfitSearchVo ctrProfitSearchVo);
	
	/** 毛利率查询*/
	Page<RptCtrTypeProfitVo> findTypeProfitPage(RptCtrProfitSearchVo ctrProfitSearchVo);

	/** 毛利率合计*/
	RptCtrTypeProfitVo findTypeProfitSum(RptCtrProfitSearchVo ctrProfitSearchVo);

	/** 查询部门 */
	List<Long> findProfitByDeptId(RptCtrProfitSearchVo searchVo);

	/** 查询提成计算默认参数 */
	RptCalCulateParam findCalculateParam(String configKey);

	/** 查询保费费率计算参数 */
	List<RptCalculateInsuranceRates> findCalculateInsuranceRates(String configKey);

	/** 业务员利润计算配置表 */
	List<RptMatchProfitConfig> findMatchProfitConfig();

	/** 赊销业务提成计算明细表 */
	Page<RptCreditBusinessCommission> findCreditBusinessCommissionPage(RptCreditBusinessCommissionSearchVo searchVo);

	Page<RptCtrContractRptVo> findRptContractPage(ContractSearchVo searchVo);
	Page<ContractShowVo> findIndexRptContractPage(ContractSearchVo searchVo);

	RptCtrContractRptVo findRptSumPageContract(ContractSearchVo searchVo);
	RptCtrContractRptVo findIndexRptSumPageContract(ContractSearchVo searchVo);

	/**
	 * 逾期预警合同列表查询
	 * @param searchVo
	 * @return
	 */
	Page<RptCtrContractWarnReport> findRptContractWarnPage(RptCtrContractWarnSearchVo searchVo);

	/**
	 * 逾期预警合同列表合计
	 * @param searchVo
	 * @return
	 */
	RptCtrContractWarnReport findRptContractWarnSum(RptCtrContractWarnSearchVo searchVo);

	List<RptExportChainVo> mergeChainExport(List<DcsxShowVo> dcsxShowVoList);
}
