package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2026/2/27 10:09
 */
public class RptOpenCompanyCreditQueryVo {
    /**
     * 服务费合同开始执行的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date openTime;

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public RptOpenCompanyCreditQueryVo() {
    }

    public RptOpenCompanyCreditQueryVo(Date openTime) {
        this.openTime = openTime;
    }
}
