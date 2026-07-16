package com.spt.pm.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;

public class PmProcessNodeRespVo {
    private Long id;
    private String nodeCode; // 节点代码
    private String nodeName; // 节点名称
    private String nodeUserId; // 节点负责人,多人用|隔开
    private String nodeType; // 节点类型:U-人员，D-部门，G-组
    private String remark; // 备注
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg; // 是否有效
    private Long enterpriseId;//企业帐套ID
    private Date createdDate;
    private Date updatedDate;
    private String refInfo;
    private String refInfoJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeUserId() {
        return nodeUserId;
    }

    public void setNodeUserId(String nodeUserId) {
        this.nodeUserId = nodeUserId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
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

    public String getRefInfo() {
        return refInfo;
    }

    public void setRefInfo(String refInfo) {
        this.refInfo = refInfo;
    }

    public String getRefInfoJson() {
        return refInfoJson;
    }

    public void setRefInfoJson(String refInfoJson) {
        this.refInfoJson = refInfoJson;
    }

    public PmProcessNodeRespVo() {
    }
}
