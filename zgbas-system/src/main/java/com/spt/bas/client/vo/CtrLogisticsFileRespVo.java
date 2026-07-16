package com.spt.bas.client.vo;

import org.apache.commons.lang3.StringUtils;

public class CtrLogisticsFileRespVo {

    /**
     * 提货单
     */
    private String ladingFileId;
    
    /**
     * 配送委托单
     */
    private String distributionFileId;
    
    /**
     * 资方提货单
     */
    private String fundLadingFileId;
    
    /**
     * 资方配送委托单
     */
    private String fundDistributionFileId;
    
    /**
     * 货物签收单
     */
    private String goodsSignatureFileId;
    
    /**
     * 送货通知单
     */
    private String deliveryNoteFileId;

    /**
     * 提货单-对应盖章申请审批ID
     */
    private Long ladingApproveId;

    /**
     * 配送委托单-对应盖章申请审批ID
     */
    private Long distributionApproveId;

    /**
     * 资方提货单-对应盖章申请审批ID
     */
    private Long fundLadingApproveId;

    /**
     * 资方配送委托单-对应盖章申请审批ID
     */
    private Long fundDistributionApproveId;

    public String getLadingFileId() {
        return ladingFileId;
    }

    public void setLadingFileId(String ladingFileId) {
        this.ladingFileId = ladingFileId;
    }

    public String getDistributionFileId() {
        return distributionFileId;
    }

    public void setDistributionFileId(String distributionFileId) {
        this.distributionFileId = distributionFileId;
    }

    public String getFundLadingFileId() {
        return fundLadingFileId;
    }

    public void setFundLadingFileId(String fundLadingFileId) {
        this.fundLadingFileId = fundLadingFileId;
    }

    public String getFundDistributionFileId() {
        return fundDistributionFileId;
    }

    public void setFundDistributionFileId(String fundDistributionFileId) {
        this.fundDistributionFileId = fundDistributionFileId;
    }

    public String getGoodsSignatureFileId() {
        return goodsSignatureFileId;
    }

    public void setGoodsSignatureFileId(String goodsSignatureFileId) {
        this.goodsSignatureFileId = goodsSignatureFileId;
    }

    public String getDeliveryNoteFileId() {
        return deliveryNoteFileId;
    }

    public void setDeliveryNoteFileId(String deliveryNoteFileId) {
        this.deliveryNoteFileId = deliveryNoteFileId;
    }

    public Long getLadingApproveId() {
        return ladingApproveId;
    }

    public void setLadingApproveId(Long ladingApproveId) {
        this.ladingApproveId = ladingApproveId;
    }

    public Long getDistributionApproveId() {
        return distributionApproveId;
    }

    public void setDistributionApproveId(Long distributionApproveId) {
        this.distributionApproveId = distributionApproveId;
    }

    public Long getFundLadingApproveId() {
        return fundLadingApproveId;
    }

    public void setFundLadingApproveId(Long fundLadingApproveId) {
        this.fundLadingApproveId = fundLadingApproveId;
    }

    public Long getFundDistributionApproveId() {
        return fundDistributionApproveId;
    }

    public void setFundDistributionApproveId(Long fundDistributionApproveId) {
        this.fundDistributionApproveId = fundDistributionApproveId;
    }

    public String getAllFileId() {
        String fileId = "";
        if (StringUtils.isNotBlank(ladingFileId)) {
            fileId += ladingFileId.endsWith(",") ? ladingFileId : ladingFileId + ",";
        }
        if (StringUtils.isNotBlank(distributionFileId)) {
            fileId += distributionFileId.endsWith(",") ? distributionFileId : distributionFileId + ",";
        }
        if (StringUtils.isNotBlank(fundLadingFileId)) {
            fileId += fundLadingFileId.endsWith(",") ? fundLadingFileId : fundLadingFileId + ",";
        }
        if (StringUtils.isNotBlank(fundDistributionFileId)) {
            fileId += fundDistributionFileId.endsWith(",") ? fundDistributionFileId : fundDistributionFileId + ",";
        }
        if (StringUtils.isNotBlank(goodsSignatureFileId)) {
            fileId += goodsSignatureFileId.endsWith(",") ? goodsSignatureFileId : goodsSignatureFileId + ",";
        }
        if (StringUtils.isNotBlank(deliveryNoteFileId)) {
            fileId += deliveryNoteFileId.endsWith(",") ? deliveryNoteFileId : deliveryNoteFileId + ",";
        }
        return fileId;
    }
}
