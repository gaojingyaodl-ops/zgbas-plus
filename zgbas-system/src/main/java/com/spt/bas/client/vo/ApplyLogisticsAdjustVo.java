package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApplyLogisticsAdjust;
import com.spt.bas.client.entity.ApplyProductDetail;

import java.util.List;

/**
 * 
 */
public class ApplyLogisticsAdjustVo extends ApplyLogisticsAdjust {

    private static final long serialVersionUID = -3504580629327160695L;
    private Long bizId;
    private String deptAbbr;
    private Long applyUserId;
    private List<ApplyProductDetailVo> productJSON;

    private List<ApplyProductDetail> lstInsert;
    private List<ApplyProductDetail> lstUpdate;
    private List<ApplyProductDetail> lstDelete;
    

    public List<ApplyProductDetail> getLstInsert() {
        return lstInsert;
    }

    public void setLstInsert(List<ApplyProductDetail> lstInsert) {
        this.lstInsert = lstInsert;
    }

    public List<ApplyProductDetail> getLstUpdate() {
        return lstUpdate;
    }

    public void setLstUpdate(List<ApplyProductDetail> lstUpdate) {
        this.lstUpdate = lstUpdate;
    }

    public List<ApplyProductDetail> getLstDelete() {
        return lstDelete;
    }

    public void setLstDelete(List<ApplyProductDetail> lstDelete) {
        this.lstDelete = lstDelete;
    }

    @Override
    public Class<?> getSubClass() {
        return ApplyProductDetail.class;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
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

    public List<ApplyProductDetailVo> getProductJSON() {
        return productJSON;
    }

    public void setProductJSON(List<ApplyProductDetailVo> productJSON) {
        this.productJSON = productJSON;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setBatchSub(List<?> lstInsert, List<?> lstUpdate, List<?> lstDelete) {
        setLstInsert((List<ApplyProductDetail>) lstInsert);
        setLstUpdate((List<ApplyProductDetail>) lstUpdate);
        setLstDelete((List<ApplyProductDetail>) lstDelete);
    }

}
