package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 议短申请单
 */
@Entity
@Table(name = "t_apply_disuss")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyDiscuss extends IdEntity implements IPmEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = 5825635722784260786L;
	/**
	 * 采购合同ID
	 */
	private Long buyContractId;
	/**
	 * 采购合同编号
	 */
	private String buyContractNo;
	/**
	 * 销售合同ID
	 */
	private Long sellContractId;
	/**
	 * 销售合同编号
	 */
	private String sellContractNo;
	/**
	 * 数量
	 */
	private BigDecimal dealNumber;

	/**
	 * 溢短装后数量
	 */
	private BigDecimal dealNumberB;
	/**
	 * 采购总价
	 */
	private BigDecimal buyTotalAmount;
	/**
	 * 销售总价
	 */
	private BigDecimal sellTotalAmount;
	/**
	 * 服务费
	 */
	private BigDecimal serviceAmount;
	/**
	 * 运输费损益
	 */
	private BigDecimal transportAmount = BigDecimal.ZERO;
	/**
	 * 仓储费损益
	 */
	private BigDecimal warehouseAmount = BigDecimal.ZERO;
	/**
	 * 附件ID
	 */
	private String fileId;
	/**
	 * 备注
	 */
	private	String	remark;
	/**
	 * 申请状态		N-新增，A-审批中，B-驳回，D-完成
	 */
	private	String	status;
	/**
	 * 审批ID
	 */
	private	Long approveId;
	/**
	 * 审批编号
	 */
	private	String	approveNo;

	/**
	 * 采购单价
	 */
	private BigDecimal buyUnitPrice;
	/**
	 * 销售单价
	 */
	private BigDecimal sellUnitPrice;

	/**
	 *部门Id
	 */
	private Long deptId;

	public Long getBuyContractId() {
		return buyContractId;
	}
	public void setBuyContractId(Long buyContractId) {
		this.buyContractId = buyContractId;
	}
	public String getBuyContractNo() {
		return buyContractNo;
	}
	public void setBuyContractNo(String buyContractNo) {
		this.buyContractNo = buyContractNo;
	}
	public Long getSellContractId() {
		return sellContractId;
	}
	public void setSellContractId(Long sellContractId) {
		this.sellContractId = sellContractId;
	}
	public String getSellContractNo() {
		return sellContractNo;
	}
	public void setSellContractNo(String sellContractNo) {
		this.sellContractNo = sellContractNo;
	}
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public BigDecimal getBuyTotalAmount() {
		return buyTotalAmount;
	}
	public void setBuyTotalAmount(BigDecimal buyTotalAmount) {
		this.buyTotalAmount = buyTotalAmount;
	}
	public BigDecimal getSellTotalAmount() {
		return sellTotalAmount;
	}
	public void setSellTotalAmount(BigDecimal sellTotalAmount) {
		this.sellTotalAmount = sellTotalAmount;
	}
	public BigDecimal getServiceAmount() {
		return serviceAmount;
	}
	public void setServiceAmount(BigDecimal serviceAmount) {
		this.serviceAmount = serviceAmount;
	}
	public BigDecimal getTransportAmount() {
		return transportAmount;
	}
	public void setTransportAmount(BigDecimal transportAmount) {
		this.transportAmount = transportAmount;
	}
	public BigDecimal getWarehouseAmount() {
		return warehouseAmount;
	}
	public void setWarehouseAmount(BigDecimal warehouseAmount) {
		this.warehouseAmount = warehouseAmount;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getApproveId() {
		return approveId;
	}
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}
	public String getApproveNo() {
		return approveNo;
	}
	public void setApproveNo(String approveNo) {
		this.approveNo = approveNo;
	}

	public BigDecimal getDealNumberB() {
		return dealNumberB;
	}

	public void setDealNumberB(BigDecimal dealNumberB) {
		this.dealNumberB = dealNumberB;
	}

	public BigDecimal getBuyUnitPrice() {
		return buyUnitPrice;
	}

	public void setBuyUnitPrice(BigDecimal buyUnitPrice) {
		this.buyUnitPrice = buyUnitPrice;
	}

	public BigDecimal getSellUnitPrice() {
		return sellUnitPrice;
	}

	public void setSellUnitPrice(BigDecimal sellUnitPrice) {
		this.sellUnitPrice = sellUnitPrice;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
}
