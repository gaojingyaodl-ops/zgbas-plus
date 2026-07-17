package com.spt.bas.report.client.entity;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>
 *      合同操作 - 申请发货（出库） 传参
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-18 17:03
 */
public class RptApplyDeliveryOutPayload {
    /**
     * 合同号
     */
    @NotBlank(message = "合同编号不能为空")
    private String contractNo;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 交货的仓库地址
     */
    private String wareCompanyName;

    /**
     * 省市区
     */
    private String curAreaName;

    /**
     * 详细地址
     */
    private String areaName;

    private List<RptApplyDeliveryOutDetail> deliveryOutList;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getWareCompanyName() {
        return wareCompanyName;
    }

    public void setWareCompanyName(String wareCompanyName) {
        this.wareCompanyName = wareCompanyName;
    }

    public String getCurAreaName() {
        return curAreaName;
    }

    public void setCurAreaName(String curAreaName) {
        this.curAreaName = curAreaName;
    }

    public List<RptApplyDeliveryOutDetail> getDeliveryOutList() {
        return deliveryOutList;
    }

    public void setDeliveryOutList(List<RptApplyDeliveryOutDetail> deliveryOutList) {
        this.deliveryOutList = deliveryOutList;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
