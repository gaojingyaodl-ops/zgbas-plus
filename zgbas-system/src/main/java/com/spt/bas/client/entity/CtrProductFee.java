package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 合同商品费用表
 */
@Entity
@Table(name = "t_ctr_product_fee")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CtrProductFee extends IdEntity{
	private static final long serialVersionUID = -574746358659912337L;
	private Long contractId;		//合同Id
	private Long productId;			//商品Id
	private Long applyDeliveryId;	//提货单Id
	private BigDecimal ccFeeXs = BigDecimal.ZERO;	//仓储-销售仓储费
	private BigDecimal ccFeeCg = BigDecimal.ZERO;	//仓储-采购仓储费
	private BigDecimal ccFeeRuku = BigDecimal.ZERO;	//仓储-入库/货转费
	private BigDecimal ccFeeQt = BigDecimal.ZERO;	//仓储-仓储杂费
	private BigDecimal ccFeeRate;//仓储-仓储费率
	private String ccUserXs;	//仓储-销仓储费承担人
	private String ccUserCg;	//仓储-采仓储费承担人
	private String ccUserRuku;	//仓储-入库货权承担人
	private String ccUserQt;	//仓储-仓储杂费承担人
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date ccFeeDate;			//仓储-仓储费用日期
	private BigDecimal wlFeeYs= BigDecimal.ZERO;	//物流-运输费
	private BigDecimal wlFeeZc= BigDecimal.ZERO;	//物流-装车费
	private BigDecimal wlFeeDfcc= BigDecimal.ZERO;	//物流-代采仓储费
	private BigDecimal wlFeeQt= BigDecimal.ZERO;	//物流-物流杂费
	private BigDecimal wlFeeRate;	//物流-物流费率
	private String wlUserYs;	//物流-运输装车承担人
	private String wlUserDfcc;	//物流-代付仓储承担人
	private String wlUserQt;	//物流-物流杂费承担人
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date wlFeeDate;		//物流-物流费用日期
	private String fileId;		//附件Id
	private String remark;		//备注
	private Long enterpriseId;	//企业账套Id
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public BigDecimal getCcFeeXs() {
		return ccFeeXs;
	}
	public void setCcFeeXs(BigDecimal ccFeeXs) {
		this.ccFeeXs = ccFeeXs;
	}
	public BigDecimal getCcFeeCg() {
		return ccFeeCg;
	}
	public void setCcFeeCg(BigDecimal ccFeeCg) {
		this.ccFeeCg = ccFeeCg;
	}
	public BigDecimal getCcFeeRuku() {
		return ccFeeRuku;
	}
	public void setCcFeeRuku(BigDecimal ccFeeRuku) {
		this.ccFeeRuku = ccFeeRuku;
	}
	public BigDecimal getCcFeeQt() {
		return ccFeeQt;
	}
	public void setCcFeeQt(BigDecimal ccFeeQt) {
		this.ccFeeQt = ccFeeQt;
	}
	public BigDecimal getCcFeeRate() {
		return ccFeeRate;
	}
	public void setCcFeeRate(BigDecimal ccFeeRate) {
		this.ccFeeRate = ccFeeRate;
	}
	public String getCcUserXs() {
		return ccUserXs;
	}
	public void setCcUserXs(String ccUserXs) {
		this.ccUserXs = ccUserXs;
	}
	public String getCcUserCg() {
		return ccUserCg;
	}
	public void setCcUserCg(String ccUserCg) {
		this.ccUserCg = ccUserCg;
	}
	public String getCcUserRuku() {
		return ccUserRuku;
	}
	public void setCcUserRuku(String ccUserRuku) {
		this.ccUserRuku = ccUserRuku;
	}
	public String getCcUserQt() {
		return ccUserQt;
	}
	public void setCcUserQt(String ccUserQt) {
		this.ccUserQt = ccUserQt;
	}
	public Date getCcFeeDate() {
		return ccFeeDate;
	}
	public void setCcFeeDate(Date ccFeeDate) {
		this.ccFeeDate = ccFeeDate;
	}
	public BigDecimal getWlFeeYs() {
		return wlFeeYs;
	}
	public void setWlFeeYs(BigDecimal wlFeeYs) {
		this.wlFeeYs = wlFeeYs;
	}
	public BigDecimal getWlFeeZc() {
		return wlFeeZc;
	}
	public void setWlFeeZc(BigDecimal wlFeeZc) {
		this.wlFeeZc = wlFeeZc;
	}
	public BigDecimal getWlFeeDfcc() {
		return wlFeeDfcc;
	}
	public void setWlFeeDfcc(BigDecimal wlFeeDfcc) {
		this.wlFeeDfcc = wlFeeDfcc;
	}
	public BigDecimal getWlFeeQt() {
		return wlFeeQt;
	}
	public void setWlFeeQt(BigDecimal wlFeeQt) {
		this.wlFeeQt = wlFeeQt;
	}
	public BigDecimal getWlFeeRate() {
		return wlFeeRate;
	}
	public void setWlFeeRate(BigDecimal wlFeeRate) {
		this.wlFeeRate = wlFeeRate;
	}
	public String getWlUserYs() {
		return wlUserYs;
	}
	public void setWlUserYs(String wlUserYs) {
		this.wlUserYs = wlUserYs;
	}
	public String getWlUserDfcc() {
		return wlUserDfcc;
	}
	public void setWlUserDfcc(String wlUserDfcc) {
		this.wlUserDfcc = wlUserDfcc;
	}
	public String getWlUserQt() {
		return wlUserQt;
	}
	public void setWlUserQt(String wlUserQt) {
		this.wlUserQt = wlUserQt;
	}
	public Date getWlFeeDate() {
		return wlFeeDate;
	}
	public void setWlFeeDate(Date wlFeeDate) {
		this.wlFeeDate = wlFeeDate;
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
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Long getApplyDeliveryId() {
		return applyDeliveryId;
	}
	public void setApplyDeliveryId(Long applyDeliveryId) {
		this.applyDeliveryId = applyDeliveryId;
	}
	
}
