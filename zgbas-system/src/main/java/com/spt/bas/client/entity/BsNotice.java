package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 公告
 */
@Entity
@Table(name = "t_bs_notice")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsNotice extends IdEntity {

    /**
     * 发文公司
     */
    private String companyName;
    /**
     * 发文部门
     */
    private String sendDeptName;
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
    private String noticeCode;
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
    private String fileId;


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
