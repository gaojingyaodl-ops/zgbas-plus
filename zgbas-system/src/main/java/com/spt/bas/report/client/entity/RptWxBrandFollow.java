package com.spt.bas.report.client.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-13 15:40
 */
@TableName("t_wx_brand_follow")
public class RptWxBrandFollow {
    private static final long serialVersionUID = 745033694654561869L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField(fill = FieldFill.INSERT)
    private Date createdDate;
    @TableField(fill = FieldFill.UPDATE)
    private Date updatedDate;
    /**
     * 牌号id
     */
    private Long brandId;
    /**
     * 品类cd
     */
    private String productCd;
    /**
     * 微信用户id
     */
    private Long wxUserId;

    private Long productTypeId;

    private String brandName;

    private String productName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(Long productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getProductCd() {
        return productCd;
    }

    public void setProductCd(String productCd) {
        this.productCd = productCd;
    }

    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
    }

}
