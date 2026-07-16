package com.spt.bas.client.vo;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.LogisticsEnum;
import com.spt.bas.client.entity.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author MoonLight
 * @Date 2023/7/10 15:54
 * @Version 1.0
 */
public class CtrLogisticsReqVo implements Serializable {
    private static final long serialVersionUID = -1L;
    private CtrLogistics logistics;
    private CtrLogisticsDelivery delivery;
    private List<CtrLogisticsDriver> driverList;
    private ApplyDeliveryIn applyDeliveryIn;
    private ApplyDeliveryOut applyDeliveryOut;
    private ApplyCtrDCSX applyCtrDCSX;
    private BigDecimal currNumber = BigDecimal.ZERO;
    private BigDecimal currAmount = BigDecimal.ZERO;
    private Long logisticsDeliveryId;
    private Long contractId;
    private String contractNo;
    private LogisticsEnum logisticsEnum;
    private String bizUserName;
    private Long bizUserId;
    private String bizUserPhone;
    private String bizTypeCode;
    private Boolean fundFlg = false;
    private String signCompanyName;
    private String companyName;
    private String logisticsNo;
    private String sealType = BasConstants.CFCA_SEAL_TYPE.SEAL_TYPE_LGS;
    private Map<String, String> paramMap = new HashMap<>(16);
    public CtrLogisticsReqVo() {
    }

    public CtrLogisticsReqVo(ApplyDeliveryIn applyDeliveryIn, BigDecimal currNumber, BigDecimal currAmount, LogisticsEnum logisticsEnum) {
        this.applyDeliveryIn = applyDeliveryIn;
        this.currNumber = currNumber;
        this.currAmount = currAmount;
        this.logisticsEnum = logisticsEnum;
    }

    public CtrLogisticsReqVo(CtrLogistics logistics, ApplyDeliveryIn applyDeliveryIn, BigDecimal currNumber, BigDecimal currAmount, LogisticsEnum logisticsEnum) {
        this.logistics = logistics;
        this.applyDeliveryIn = applyDeliveryIn;
        this.currNumber = currNumber;
        this.currAmount = currAmount;
        this.logisticsEnum = logisticsEnum;
    }

    public CtrLogisticsReqVo(CtrLogistics logistics, CtrLogisticsDelivery delivery, List<CtrLogisticsDriver> driverList) {
        this.logistics = logistics;
        this.delivery = delivery;
        this.driverList = driverList;
    }

    public CtrLogisticsReqVo(CtrLogistics logistics, CtrLogisticsDelivery delivery, List<CtrLogisticsDriver> driverList, ApplyDeliveryIn applyDeliveryIn) {
        this.logistics = logistics;
        this.delivery = delivery;
        this.driverList = driverList;
        this.applyDeliveryIn = applyDeliveryIn;
    }

    public CtrLogisticsReqVo(CtrLogistics logistics, CtrLogisticsDelivery delivery, List<CtrLogisticsDriver> driverList, ApplyDeliveryOut applyDeliveryOut) {
        this.logistics = logistics;
        this.delivery = delivery;
        this.driverList = driverList;
        this.applyDeliveryOut = applyDeliveryOut;
    }

    public CtrLogisticsReqVo(CtrLogistics logistics, CtrLogisticsDelivery delivery, List<CtrLogisticsDriver> driverList, ApplyDeliveryIn applyDeliveryIn, ApplyDeliveryOut applyDeliveryOut) {
        this.logistics = logistics;
        this.delivery = delivery;
        this.driverList = driverList;
        this.applyDeliveryIn = applyDeliveryIn;
        this.applyDeliveryOut = applyDeliveryOut;
    }

    public CtrLogisticsReqVo(CtrLogistics logistics, CtrLogisticsDelivery delivery, List<CtrLogisticsDriver> driverList, ApplyDeliveryIn applyDeliveryIn, BigDecimal currNumber, BigDecimal currAmount) {
        this.logistics = logistics;
        this.delivery = delivery;
        this.driverList = driverList;
        this.applyDeliveryIn = applyDeliveryIn;
        this.currNumber = currNumber;
        this.currAmount = currAmount;
    }

    public CtrLogisticsReqVo(CtrLogistics logistics, CtrLogisticsDelivery delivery, List<CtrLogisticsDriver> driverList, ApplyDeliveryOut applyDeliveryOut, BigDecimal currNumber, BigDecimal currAmount) {
        this.logistics = logistics;
        this.delivery = delivery;
        this.driverList = driverList;
        this.applyDeliveryOut = applyDeliveryOut;
        this.currNumber = currNumber;
        this.currAmount = currAmount;
    }

    public CtrLogisticsReqVo(CtrLogistics logistics, CtrLogisticsDelivery delivery, List<CtrLogisticsDriver> driverList, ApplyDeliveryIn applyDeliveryIn, ApplyDeliveryOut applyDeliveryOut, BigDecimal currNumber, BigDecimal currAmount) {
        this.logistics = logistics;
        this.delivery = delivery;
        this.driverList = driverList;
        this.applyDeliveryIn = applyDeliveryIn;
        this.applyDeliveryOut = applyDeliveryOut;
        this.currNumber = currNumber;
        this.currAmount = currAmount;
    }

    public CtrLogistics getLogistics() {
        return logistics;
    }

    public void setLogistics(CtrLogistics logistics) {
        this.logistics = logistics;
    }

    public CtrLogisticsDelivery getDelivery() {
        return delivery;
    }

    public void setDelivery(CtrLogisticsDelivery delivery) {
        this.delivery = delivery;
    }

    public List<CtrLogisticsDriver> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<CtrLogisticsDriver> driverList) {
        this.driverList = driverList;
    }

    public ApplyDeliveryIn getApplyDeliveryIn() {
        return applyDeliveryIn;
    }

    public void setApplyDeliveryIn(ApplyDeliveryIn applyDeliveryIn) {
        this.applyDeliveryIn = applyDeliveryIn;
    }

    public ApplyDeliveryOut getApplyDeliveryOut() {
        return applyDeliveryOut;
    }

    public void setApplyDeliveryOut(ApplyDeliveryOut applyDeliveryOut) {
        this.applyDeliveryOut = applyDeliveryOut;
    }

    public ApplyCtrDCSX getApplyCtrDCSX() {
        return applyCtrDCSX;
    }

    public void setApplyCtrDCSX(ApplyCtrDCSX applyCtrDCSX) {
        this.applyCtrDCSX = applyCtrDCSX;
    }

    public BigDecimal getCurrNumber() {
        return currNumber;
    }

    public void setCurrNumber(BigDecimal currNumber) {
        this.currNumber = currNumber;
    }

    public BigDecimal getCurrAmount() {
        return currAmount;
    }

    public void setCurrAmount(BigDecimal currAmount) {
        this.currAmount = currAmount;
    }

    public Long getLogisticsDeliveryId() {
        return logisticsDeliveryId;
    }

    public void setLogisticsDeliveryId(Long logisticsDeliveryId) {
        this.logisticsDeliveryId = logisticsDeliveryId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public LogisticsEnum getLogisticsEnum() {
        return logisticsEnum;
    }

    public void setLogisticsEnum(LogisticsEnum logisticsEnum) {
        this.logisticsEnum = logisticsEnum;
    }

    public String getBizUserName() {
        return bizUserName;
    }

    public void setBizUserName(String bizUserName) {
        this.bizUserName = bizUserName;
    }

    public Long getBizUserId() {
        return bizUserId;
    }

    public void setBizUserId(Long bizUserId) {
        this.bizUserId = bizUserId;
    }

    public String getBizUserPhone() {
        return bizUserPhone;
    }

    public void setBizUserPhone(String bizUserPhone) {
        this.bizUserPhone = bizUserPhone;
    }

    public String getBizTypeCode() {
        return bizTypeCode;
    }

    public void setBizTypeCode(String bizTypeCode) {
        this.bizTypeCode = bizTypeCode;
    }

    public Boolean getFundFlg() {
        return fundFlg;
    }

    public void setFundFlg(Boolean fundFlg) {
        this.fundFlg = fundFlg;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public String getSignCompanyName() {
        return signCompanyName;
    }

    public void setSignCompanyName(String signCompanyName) {
        this.signCompanyName = signCompanyName;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSealType() {
        return sealType;
    }

    public void setSealType(String sealType) {
        this.sealType = sealType;
    }
}
