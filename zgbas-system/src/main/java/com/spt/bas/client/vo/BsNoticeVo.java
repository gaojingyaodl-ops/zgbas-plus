package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.jpa.vo.IdEntity;
import org.omg.CosNaming.IstringHelper;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

public class BsNoticeVo extends PageSearchVo {


    private String  editFlag;
    private    Long id;

    private    Long pid;
    /**
     * 发文公司
     */
    private String companyName ;
    /**
     * 发文部门
     */
    private String  sendDeptName;
    /**
     * 接收部门名称
     */
    private String receiveDeptName;
    /**
     * 接收部门id
     */
    private String receiveDeptId;
    /**
     * 公告编号
     */
    private String  noticeCode;
    /**
     * 公告标题
     */
    private String subjuect;
    /**
     * 年份
     */
    private String year;
    /**
     * 序号
     */
    private Long sequenceNo;
    /**
     * 公告内容
     */
    private String content;
    /**
     * 附件id
     */
    private String   fileId;

    private String files;

    protected Date createdDate;
    protected Date updatedDate;
    @JsonFormat(
            pattern = "yyyy-MM-dd ",
            timezone = "GMT+08:00"
    )
    @Column(
            updatable = false
    )
    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @JsonFormat(
            pattern = "yyyy-MM-dd ",
            timezone = "GMT+08:00"
    )
    public Date getUpdatedDate() {
        return this.updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getEditFlag() {
        return editFlag;
    }

    public void setEditFlag(String editFlag) {
        this.editFlag = editFlag;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSendDeptName() {
        return sendDeptName;
    }

    public void setSendDeptName(String sendDeptName) {
        this.sendDeptName = sendDeptName;
    }

    public String getReceiveDeptName() {
        return receiveDeptName;
    }

    public void setReceiveDeptName(String receiveDeptName) {
        this.receiveDeptName = receiveDeptName;
    }

    public String getReceiveDeptId() {
        return receiveDeptId;
    }

    public void setReceiveDeptId(String receiveDeptId) {
        this.receiveDeptId = receiveDeptId;
    }

    public String getNoticeCode() {
        return noticeCode;
    }

    public void setNoticeCode(String noticeCode) {
        this.noticeCode = noticeCode;
    }

    public String getSubjuect() {
        return subjuect;
    }

    public void setSubjuect(String subjuect) {
        this.subjuect = subjuect;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Long getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(Long sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
