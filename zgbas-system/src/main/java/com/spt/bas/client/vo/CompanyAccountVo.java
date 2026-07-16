package com.spt.bas.client.vo;

import java.util.List;

import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyAccount;
import com.spt.bas.client.entity.BsWarehouse;

public class CompanyAccountVo extends BsCompany {
    /**
     *
     */
    private static final long serialVersionUID = 3015788834728277149L;

    private String bankName;//开户行
    private String bankAccount;//开户账号
    private String taxNo;//税号
    private String createUserName;//创建人
    private Boolean permittedFlg = false;//风控编辑权限


    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系电话
     */
    private String contactPhone;


    private String warehouseAddr;//地址


    private String address;

    private List<BsCompanyAccount> lstInsert;
    private List<BsCompanyAccount> lstUpdate;
    private List<BsCompanyAccount> lstDelete;

    private List<BsWarehouseVo> wlstInsert;
    private List<BsWarehouseVo> wlstUpdate;
    private List<BsWarehouseVo> wlstDelete;

    @Override
    public String getContactName() {
        return contactName;
    }

    @Override
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public String getContactPhone() {
        return contactPhone;
    }

    @Override
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getWarehouseAddr() {
        return warehouseAddr;
    }

    public void setWarehouseAddr(String warehouseAddr) {
        this.warehouseAddr = warehouseAddr;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    @Override
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public Boolean getPermittedFlg() {
        return permittedFlg;
    }

    public void setPermittedFlg(Boolean permittedFlg) {
        this.permittedFlg = permittedFlg;
    }

    public List<BsCompanyAccount> getLstInsert() {
        return lstInsert;
    }

    public void setLstInsert(List<BsCompanyAccount> lstInsert) {
        this.lstInsert = lstInsert;
    }

    public List<BsCompanyAccount> getLstUpdate() {
        return lstUpdate;
    }

    public void setLstUpdate(List<BsCompanyAccount> lstUpdate) {
        this.lstUpdate = lstUpdate;
    }

    public List<BsCompanyAccount> getLstDelete() {
        return lstDelete;
    }

    public void setLstDelete(List<BsCompanyAccount> lstDelete) {
        this.lstDelete = lstDelete;
    }

    public Class<?> getSubClass() {
        return BsCompanyAccount.class;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public List<BsWarehouseVo> getWlstInsert() {
        return wlstInsert;
    }

    public void setWlstInsert(List<BsWarehouseVo> wlstInsert) {
        this.wlstInsert = wlstInsert;
    }

    public List<BsWarehouseVo> getWlstUpdate() {
        return wlstUpdate;
    }

    public void setWlstUpdate(List<BsWarehouseVo> wlstUpdate) {
        this.wlstUpdate = wlstUpdate;
    }

    public List<BsWarehouseVo> getWlstDelete() {
        return wlstDelete;
    }

    public void setWlstDelete(List<BsWarehouseVo> wlstDelete) {
        this.wlstDelete = wlstDelete;
    }

    public void setBatchSub(List<BsCompanyAccount> lstInsert, List<BsCompanyAccount> lstUpdate, List<BsCompanyAccount> lstDelete) {
        setLstInsert((List<BsCompanyAccount>) lstInsert);
        setLstUpdate((List<BsCompanyAccount>) lstUpdate);
        setLstDelete((List<BsCompanyAccount>) lstDelete);
    }

    public void setWbatchSub(List<BsWarehouseVo> wlstInsert, List<BsWarehouseVo> wlstUpdate, List<BsWarehouseVo> wlstDelete) {
        setWlstInsert((List<BsWarehouseVo>) wlstInsert);
        setWlstUpdate((List<BsWarehouseVo>) wlstUpdate);
        setWlstDelete((List<BsWarehouseVo>) wlstDelete);
    }
}
