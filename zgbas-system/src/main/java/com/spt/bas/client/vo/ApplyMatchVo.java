package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.ApplyMatchChain;
import com.spt.bas.client.entity.ApplyProductDetail;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class ApplyMatchVo extends ApplyMatch {

    private static final long serialVersionUID = -4684343778890078826L;

    /**
     * 部门简码
     */
    private String deptAbbr;

    /**
     * 业务ID
     */
    private Long bizId;

    /**
     * 交货方式
     */
    private String deliveryMode;

    /**
     * 配送方式
     */
    private String deliveryType;

    /**
     * 删除list
     */
    private String removeArrStr;

    /**
     * 撮合基本信息
     */
    private String contentStr;

    /**
     * 撮合采购+销售基本信息
     */
    private List<ApplyMatchDetailVo> contentJSON;
    /**
     * 付全款时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;


    /**
     * 当前登录人的id
     */
    private Long applyUserId;

    /**
     * 买方信息
     */
    private ApplyMatchDetailVo buyInfo;

    /**
     * 卖方信息
     */
    private ApplyMatchDetailVo sellInfo;

    /**
     * 创建人
     */
    private Long createdUserId;
    
    /**
     * 销售方企业等级
     */
    private String sellCompanyGrade;
    private List<ApplyMatchDetailVo> lstInsert;
    private List<ApplyMatchDetailVo> lstUpdate;
    private List<ApplyMatchDetailVo> lstDelete;

    private List<ApplyMatchChain> chainLstInsert;
    private List<ApplyMatchChain> chainLstUpdate;
    private List<ApplyMatchChain> chainLstDelete;


    public Date getPayFullTime() {
        return payFullTime;
    }

    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public List<ApplyMatchDetailVo> getLstInsert() {
        return lstInsert;
    }

    public void setLstInsert(List<ApplyMatchDetailVo> lstInsert) {
        this.lstInsert = lstInsert;
    }

    public List<ApplyMatchDetailVo> getLstUpdate() {
        return lstUpdate;
    }

    public void setLstUpdate(List<ApplyMatchDetailVo> lstUpdate) {
        this.lstUpdate = lstUpdate;
    }

    public List<ApplyMatchDetailVo> getLstDelete() {
        return lstDelete;
    }

    public void setLstDelete(List<ApplyMatchDetailVo> lstDelete) {
        this.lstDelete = lstDelete;
    }

    public List<ApplyMatchDetailVo> getContentJSON() {
        return contentJSON;
    }

    public void setContentJSON(List<ApplyMatchDetailVo> contentJSON) {
        this.contentJSON = contentJSON;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public String getRemoveArrStr() {
        return removeArrStr;
    }

    public void setRemoveArrStr(String removeArrStr) {
        this.removeArrStr = removeArrStr;
    }

    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }

    @Override
    public Class<?> getSubClass() {
        return ApplyProductDetail.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setBatchSub(List<?> lstInsert, List<?> lstUpdate, List<?> lstDelete) {
        setLstInsert((List<ApplyMatchDetailVo>) lstInsert);
        setLstUpdate((List<ApplyMatchDetailVo>) lstUpdate);
        setLstDelete((List<ApplyMatchDetailVo>) lstDelete);
    }

    public String getDeptAbbr() {
        return deptAbbr;
    }

    public void setDeptAbbr(String deptAbbr) {
        this.deptAbbr = deptAbbr;
    }
    public Long getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
    }


    public ApplyMatchDetailVo getBuyInfo() {
        return buyInfo;
    }

    public void setBuyInfo(ApplyMatchDetailVo buyInfo) {
        this.buyInfo = buyInfo;
    }

    public ApplyMatchDetailVo getSellInfo() {
        return sellInfo;
    }

    public void setSellInfo(ApplyMatchDetailVo sellInfo) {
        this.sellInfo = sellInfo;
    }

    public String getSellCompanyGrade() {
        return sellCompanyGrade;
    }

    public void setSellCompanyGrade(String sellCompanyGrade) {
        this.sellCompanyGrade = sellCompanyGrade;
    }

    public List<ApplyMatchChain> getChainLstInsert() {
        return chainLstInsert;
    }

    public void setChainLstInsert(List<ApplyMatchChain> chainLstInsert) {
        this.chainLstInsert = chainLstInsert;
    }

    public List<ApplyMatchChain> getChainLstUpdate() {
        return chainLstUpdate;
    }

    public void setChainLstUpdate(List<ApplyMatchChain> chainLstUpdate) {
        this.chainLstUpdate = chainLstUpdate;
    }

    public List<ApplyMatchChain> getChainLstDelete() {
        return chainLstDelete;
    }

    public void setChainLstDelete(List<ApplyMatchChain> chainLstDelete) {
        this.chainLstDelete = chainLstDelete;
    }

    @Override
    public void setChainBatchSub(List<?> lstInsert, List<?> lstUpdate, List<?> lstDelete) {
        setChainLstInsert((List<ApplyMatchChain>) lstInsert);
        setChainLstUpdate((List<ApplyMatchChain>) lstUpdate);
        setChainLstDelete((List<ApplyMatchChain>) lstDelete);
    }

    @Override
    public Long getCreatedUserId() {
        return createdUserId;
    }

    @Override
    public void setCreatedUserId(Long createdUserId) {
        this.createdUserId = createdUserId;
    }
}
