package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 开票管理 VO
 */
@Data
public class RptInvoiceBill extends IdEntity {

	private Long id;
	/**
	 * 企业名称
	 */
	private String companyName;

	/**
	 * 我方抬头
	 */
	private String ourCompanyName;

	/**
	 * 合同编号
	 */
	private String contractNo;

	private String buyContentFileId;
	private String sellContentFileId;
	private String fileId;
	private String contractType;
	private String businessType;


	/**
	 * 签订日
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date contractTime;

	/**
	 * 品种
	 */
	private String productName;

	/**
	 * 品名
	 */
	private String productCd;

	/**
	 * 牌号
	 */
	private String brandNumber;

	/**
	 * 数量
	 */
	private BigDecimal dealNumber;

	/**
	 * 单价
	 */
	private BigDecimal dealPrice;

	/**
	 * 合同总价
	 */
	private BigDecimal totalAmount;

	/**
	 * 申请日期
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date applyDate;

	/**
	 * 开票日期
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date invoiceDate;

	/**
	 * 开票金额
	 */
	private BigDecimal invoiceAmount;

	/**
	 * 开票备注
	 */
	private String remark;

	/**
	 * 发票号码
	 */
	private String invoiceNo;


	/**
	 * 开票申请编号
	 */
	private String approveNo;

	/**
	 * 审批状态
	 */
	private String approveStatus;
	private String approveStatusName;

	/**
	 * 当前审批人
 	 */
	private String currApproveUserId;
	private String currApproveUserName;

	/**
	 * 中游合同号
	 */
	private String dcsxContractNo;

	/**
	 * 中游开票状态
	 */
	private String dcsxInvoiceStatus;

	/**
	 * 中游合同附件
	 */
	private String dcsxContractFileId;

	/**
	 * 开户银行
	 */
	private String bankName;

	/**
	 * 银行账号
	 */
	private String bankAccount;

	/**
	 * 税号
	 */
	private String taxNo;

	/**
	 * 公司地址
	 */
	private String address;
	/**
	 * 公司电话
	 */
	private String companyPhone;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
