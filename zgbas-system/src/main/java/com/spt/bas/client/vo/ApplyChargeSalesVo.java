package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApplyChargeSales;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.ApplyProductDetail;

import java.util.List;

public class ApplyChargeSalesVo extends ApplyChargeSales {

    private static final long serialVersionUID = -4684343778890078826L;
    private String deptAbbr;        //部门简码
    private Long bizId;

    private String deliveryMode;        //交货方式
    private String deliveryType;        //配送方式

    private String removeArrStr;        //删除list
    private String contentStr;            //撮合基本信息

    private List<ApplyMatchDetailVo> contentJSON;//撮合采购+销售基本信息

    private Long applyUserId;//当前登录人的id


    private ApplyMatchDetailVo buyInfo;//买方
    private ApplyMatchDetailVo sellInfo;//卖方



    private List<ApplyMatchDetailVo> lstInsert;
    private List<ApplyMatchDetailVo> lstUpdate;
    private List<ApplyMatchDetailVo> lstDelete;


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


}
