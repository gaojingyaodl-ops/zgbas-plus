package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 投诉记录表
 * @Author: gaojy
 * @create 2022/5/23 15:02
 * @version: 1.0
 * @description:
 */
@Entity
@Table(name = "t_ass_complaints")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AssComplaints extends IdEntity {
    private static final long serialVersionUID = 1414017392194470003L;

    /**
     * 投诉类型
     * 0-人员投诉，1-部门投诉，2-事项投诉
     */
    private String complaintsType;

    /**
     * 投诉人ID
     */
    private Long fromUserId;

    /**
     * 投诉人名称
     */
    private String fromUserName;

    /**
     * 被投诉人ID
     */
    private Long toUserId;

    /**
     * 被投诉人部门ID
     */
    private Long toUserDeptId;

    /**
     * 被投诉人名称
     */
    private String toUserName;

    /**
     * 被投诉部门ID
     */
    private Long toDeptId;

    /**
     * 被投诉部门名称
     */
    private String toDeptName;

    /**
     * 投诉事项
     */
    private String subject;

    /**
     * 投诉内容
     */
    private String content;

    /**
     * 状态
     * 0-未处理;1-已处理
     */
    private String status;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    public String getComplaintsType() {
        return complaintsType;
    }

    public void setComplaintsType(String complaintsType) {
        this.complaintsType = complaintsType;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Long getToUserDeptId() {
        return toUserDeptId;
    }

    public void setToUserDeptId(Long toUserDeptId) {
        this.toUserDeptId = toUserDeptId;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public Long getToDeptId() {
        return toDeptId;
    }

    public void setToDeptId(Long toDeptId) {
        this.toDeptId = toDeptId;
    }

    public String getToDeptName() {
        return toDeptName;
    }

    public void setToDeptName(String toDeptName) {
        this.toDeptName = toDeptName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public AssComplaints() {
    }
}
