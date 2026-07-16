package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.entity.ApplyConfirmReceipt;
import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.client.entity.ApplyProductDetail;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-10 17:54
 */
public class ApplyConfirmReceiptVo extends ApplyConfirmReceipt {

    private static final long serialVersionUID = -3504580629327160693L;
    private Long bizId;
    private String deptAbbr;
    private Long applyUserId;
    private Long contractMatchUserId;
    private String deliveryOutNo;
    private List<ApplyProductDetailVo> productJSON;
    private List<ApplyDeliveryOut> applyDeliveryOuts;


    private List<ApplyProductDetail> lstInsert;
    private List<ApplyProductDetail> lstUpdate;
    private List<ApplyProductDetail> lstDelete;
    /**
     * 实际到货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmReceiptDate;

    public List<ApplyDeliveryOut> getApplyDeliveryOuts() {
        return applyDeliveryOuts;
    }

    public void setApplyDeliveryOuts(List<ApplyDeliveryOut> applyDeliveryOuts) {
        this.applyDeliveryOuts = applyDeliveryOuts;
    }

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

    public String getDeliveryOutNo() {
        return deliveryOutNo;
    }

    public void setDeliveryOutNo(String deliveryOutNo) {
        this.deliveryOutNo = deliveryOutNo;
    }

    public Long getContractMatchUserId() {
        return contractMatchUserId;
    }

    public void setContractMatchUserId(Long contractMatchUserId) {
        this.contractMatchUserId = contractMatchUserId;
    }
}
