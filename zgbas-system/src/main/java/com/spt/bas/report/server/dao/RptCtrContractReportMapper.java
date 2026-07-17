package com.spt.bas.report.server.dao;

import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.ContractShowVo;
import com.spt.bas.report.client.entity.RptCtrContractReport;
import com.spt.bas.report.client.entity.RptCtrContractWarnReport;
import com.spt.bas.report.client.vo.*;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptCtrContractReportMapper {
	
	/**已付款未入库明细*/
	List<RptCtrContractReport> findNotDeliveryInPage(RptCtrContractReportSearchVo vo);
	
	/**已付款未入库明细合计*/
	RptCtrContractReport findNotDeliveryInPageSum(RptCtrContractReportSearchVo searchVo);
	
	/**已付款未入库明细*/
	List<RptCtrContractReport> findWasDeliveryInPage(RptCtrContractReportSearchVo vo);
	
	/**已付款未入库明细合计*/
	RptCtrContractReport findWasDeliveryInPageSum(RptCtrContractReportSearchVo searchVo);
	
	/**预售未采购明细*/
	List<RptCtrContractReport> findPreSellPage(RptCtrContractReportSearchVo searchVo);
	
	/**赊销收款明细表*/
	List<RptCtrContractReport> findSXReceivePage(RptCtrContractReportSearchVo searchVo);
	
	/**已收款未出库明细*/
	List<RptCtrContractReport> findReceiveAndNotOutPage(RptCtrContractReportSearchVo vo);
	
	/**已收款未出库明细合计*/
	RptCtrContractReport findReceiveAndNotOutPageSum(RptCtrContractReportSearchVo searchVo);

	/**毛利率查询*/
	List<RptCtrProfitVo> findProfitPage(RptCtrProfitSearchVo ctrProfitSearchVo);
	
	/**毛利率查询*/
	List<RptCtrTypeProfitVo> findTypeProfitPage(RptCtrProfitSearchVo ctrProfitSearchVo);
	List<RptCtrTypeProfitVo> getDcsxCapitalCost(RptCtrProfitSearchVo ctrProfitSearchVo);
	List<RptCtrTypeProfitVo> getCapitalCost(RptCtrProfitSearchVo ctrProfitSearchVo);

	/**查询业务成本统计表*/
	List<RptBaseCostVo> findRptBaseCostList(RptBaseCostSearchVo searchVo);

	/** 毛利率合计 */
	RptCtrProfitVo findProfitSum(RptCtrProfitSearchVo ctrProfitSearchVo);

	/** 查询部门 */
	List<Long> findProfitByDeptId(RptCtrProfitSearchVo searchVo);

	/** 赊销业务提成计算明细表 */
	List<RptCreditBusinessCommission> findCreditBusinessCommissionPage(RptCreditBusinessCommissionSearchVo searchVo);

	/** 默认利润计算参数 */
	String findCalculateParam(String configKey);

	/** 业务员利润计算配置表 */
	List<RptMatchProfitConfig> findMatchProfitConfig();

	List<RptCtrContractRptVo> findRptContractPage(ContractSearchVo searchVo);

	List<ContractShowVo> findIndexRptContractPage(ContractSearchVo searchVo);

	RptCtrContractRptVo findRptSumPageContract(ContractSearchVo searchVo);
	RptCtrContractRptVo findIndexRptSumPageContract(ContractSearchVo searchVo);

	List<RptCtrContractWarnReport> findRptContractWarnPage(RptCtrContractWarnSearchVo searchVo);

	RptCtrContractWarnReport findRptContractWarnSum(RptCtrContractWarnSearchVo searchVo);

	List<RptExportChainVo> mergeChainExport(RptExportSearchVo searchVo);

	List<RptBaseCostVo> findBaseCostGroupByDate(RptBaseCostSearchVo searchVo);
}