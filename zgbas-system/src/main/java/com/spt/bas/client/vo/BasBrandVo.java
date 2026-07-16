package com.spt.bas.client.vo;

import com.spt.bas.client.entity.BasBrand;
import com.spt.pm.inter.IPmEntity;

public class BasBrandVo extends BasBrand implements IPmEntity {


    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public void setStatus(String status) {

    }
}
