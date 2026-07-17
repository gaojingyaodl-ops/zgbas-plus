package com.spt.bas.report.client.entity;

import java.math.BigDecimal;



/**
 * 
 * 付款明细（也可用收款明细）
 *
 */
public class RptCtrContractReceiveDetailReport extends RptCtrContractPayAndReceiveReport {
	private BigDecimal payedAmount; // 已收金额
	private String deptName; //事业部名称
	private BigDecimal dealAmount; // 开票金额
	private BigDecimal overdueAmount;//逾期金额(计算：如果逾期，返回合同总价-已收金额，反之0)
	private String exitContractText; //电子合同内容
	//合计字段
	private BigDecimal sumBilledAmount;//统计已收金额
	private BigDecimal sumOverdueAmount;//统计逾期金额
	
	
	public BigDecimal getPayedAmount() {
		return payedAmount;
	}
	public void setPayedAmount(BigDecimal payedAmount) {
		this.payedAmount = payedAmount;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public BigDecimal getDealAmount() {
		return dealAmount;
	}
	public void setDealAmount(BigDecimal dealAmount) {
		this.dealAmount = dealAmount;
	}
	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}
	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}
	public String getExitContractText() {
		return exitContractText;
	}
	public void setExitContractText(String exitContractText) {
		this.exitContractText = exitContractText;
	}
	public BigDecimal getSumBilledAmount() {
		return sumBilledAmount;
	}
	public void setSumBilledAmount(BigDecimal sumBilledAmount) {
		this.sumBilledAmount = sumBilledAmount;
	}
	public BigDecimal getSumOverdueAmount() {
		return sumOverdueAmount;
	}
	public void setSumOverdueAmount(BigDecimal sumOverdueAmount) {
		this.sumOverdueAmount = sumOverdueAmount;
	}
	
	
	
	
	
}
