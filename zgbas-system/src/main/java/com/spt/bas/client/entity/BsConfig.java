package com.spt.bas.client.entity;

import com.spt.tools.core.annotation.LogEntityName;
import com.spt.tools.core.annotation.LogField;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @Author: gaojy
 * @create 2021/12/13 10:56
 * @version: 1.0
 * @description:
 */
@Entity
@LogEntityName("业务开关配置")
@Table(name = "t_bs_config")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsConfig extends IdEntity {

    private static final long serialVersionUID = 9168088299153770215L;
    /**
     * 我方抬头
     */
    @LogField("我方抬头")
    private String ourCompanyName;

    /**
     * 代采赊销单位
     * 0-范太克，1-南方经贸
     */
    @LogField("代采赊销单位")
    private String sxCompany;

    /**
     * 资金来源
     * OUR-自有全额，CZCB-稠州银行
     */
    @LogField("资金来源")
    private String fundSource;

    /**
     * 赊销模式
     * 0-不限，BL-保理，PT-全额
     */
    @LogField("赊销模式")
    private String contractModel;

    /**
     * 下游客户授信类别
     * 0-人保，1-大地，9-自主
     */
    @LogField("下游客户授信类别")
    private String creditType;

    /**
     * 是否有效
     * 0-无效，1-有效
     */
    @LogField("是否有效")
    private Boolean enableFlg = false;

    /**
     * 可用额度
     */
    @LogField("可用额度")
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 企业账套ID
     */
    @LogField("企业账套ID")
    private Long enterpriseId;

    /**
     * 业务员ID
     */
    @LogField("更新业务员ID")
    private String matchUserId;

    /**
     * 业务员姓名
     */
    @LogField("更新业务员")
    private String matchUserName;

    /**
     * 备注
     */
    @LogField("备注")
    private String remark;

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public String getSxCompany() {
        return sxCompany;
    }

    public void setSxCompany(String sxCompany) {
        this.sxCompany = sxCompany;
    }

    public String getFundSource() {
        return fundSource;
    }

    public void setFundSource(String fundSource) {
        this.fundSource = fundSource;
    }

    public String getContractModel() {
        return contractModel;
    }

    public void setContractModel(String contractModel) {
        this.contractModel = contractModel;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public String getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(String matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public BsConfig() {
    }

    public BsConfig(String ourCompanyName, String sxCompany, String fundSource, String contractModel, Boolean enableFlg, Long enterpriseId, String matchUserId, String matchUserName, String remark) {
        this.ourCompanyName = ourCompanyName;
        this.sxCompany = sxCompany;
        this.fundSource = fundSource;
        this.contractModel = contractModel;
        this.enableFlg = enableFlg;
        this.enterpriseId = enterpriseId;
        this.matchUserId = matchUserId;
        this.matchUserName = matchUserName;
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "BsConfig{" +
                "ourCompanyName='" + ourCompanyName + '\'' +
                ", sxCompany='" + sxCompany + '\'' +
                ", fundSource='" + fundSource + '\'' +
                ", contractModel='" + contractModel + '\'' +
                ", creditType='" + creditType + '\'' +
                ", enableFlg=" + enableFlg +
                ", enterpriseId=" + enterpriseId +
                ", matchUserId='" + matchUserId + '\'' +
                ", matchUserName='" + matchUserName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
