package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ApproveWaitSearchVo extends PageSearchVo {

    //事项类型
    private String dealType;
    //摘要
    private String subject;
    //已读状态
    private String readFlg;
    //责任人ID
    private String relaUserId;
    //部门ID
    private Long relaDeptId;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date createdDate;

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getReadFlg() {
        return readFlg;
    }

    public void setReadFlg(String readFlg) {
        this.readFlg = readFlg;
    }



    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getRelaUserId() {
        return relaUserId;
    }

    public void setRelaUserId(String relaUserId) {
        this.relaUserId = relaUserId;
    }

    public Long getRelaDeptId() {
        return relaDeptId;
    }

    public void setRelaDeptId(Long relaDeptId) {
        this.relaDeptId = relaDeptId;
    }

    public ApproveWaitSearchVo() {
    }

    public ApproveWaitSearchVo(String relaUserId) {
        this.relaUserId = relaUserId;
    }
}
