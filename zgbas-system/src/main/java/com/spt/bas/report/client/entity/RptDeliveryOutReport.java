package com.spt.bas.report.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
/**
 * 实际出库明细报表实体
 */
public class RptDeliveryOutReport extends PageSearchVo{
	private int id;					//库存明细ID
	private String contractNo;		//采购合同账号
	private String contractAttr;	//类型
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date contractTime;		//采购日期
	private String productName;		//品名
	private String brandNumber;		//牌号
	private String factoryName;		//厂商
	private String companyName;		//供方单位
	private BigDecimal totalNumber = BigDecimal.ZERO;	//采购数量
	private BigDecimal deliveryInNumber = BigDecimal.ZERO;//入库数量
	private BigDecimal deliveryOutNumber = BigDecimal.ZERO;//出库数量
	private BigDecimal surplusNumber = BigDecimal.ZERO;//结余数量
	private String warehouseNo;		//入库单号
	private String warehouse;		//入库仓库
	private String matchUserName;	//业务员
	private Long deptId;			//所属事业部
	private String deptName;		//事业部名称
	private Long enterpriseId;		//企业账套ID
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date beginTime;			//条件查询采购日期开始
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date endTime;			//条件查询采购日期结束
	private String ourCompanyName;// 我方企业名称
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getContractAttr() {
		return contractAttr;
	}
	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
	}
	public Date getContractTime() {
		return contractTime;
	}
	public void setContractTime(Date contractTime) {
		this.contractTime = contractTime;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getBrandNumber() {
		return brandNumber;
	}
	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}
	public String getFactoryName() {
		return factoryName;
	}
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public BigDecimal getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}
	public BigDecimal getDeliveryInNumber() {
		return deliveryInNumber;
	}
	public void setDeliveryInNumber(BigDecimal deliveryInNumber) {
		this.deliveryInNumber = deliveryInNumber;
	}
	public BigDecimal getDeliveryOutNumber() {
		return deliveryOutNumber;
	}
	public void setDeliveryOutNumber(BigDecimal deliveryOutNumber) {
		this.deliveryOutNumber = deliveryOutNumber;
	}
	public BigDecimal getSurplusNumber() {
		return surplusNumber;
	}
	public void setSurplusNumber(BigDecimal surplusNumber) {
		this.surplusNumber = surplusNumber;
	}
	public String getWarehouseNo() {
		return warehouseNo;
	}
	public void setWarehouseNo(String warehouseNo) {
		this.warehouseNo = warehouseNo;
	}
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	public String getMatchUserName() {
		return matchUserName;
	}
	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	
	
}
