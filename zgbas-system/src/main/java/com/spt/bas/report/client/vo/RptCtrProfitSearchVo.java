package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Author: gaojy
 * @create 2021/12/23 16:18
 * @version: 1.0
 * @description:
 */
public class RptCtrProfitSearchVo extends PageSearchVo {

    /**
     * 查询类型 T-业务类型统计; U-业务员统计
     */
    private String searchType;

    /**
     * 事业部
     */
    private List<Long> deptId;

    /**
     * 我所负责的部门列表
     */
    private List<Long> myDeptIdList;

    /**
     * 业务员
     */
    private Long matchUserId;

    /**
     * 业务类型
     */
    private String businessCode;

    /**
     * 查询合同时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTimeStart;

    /**
     * 查询合同时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTimeEnd;

    private Long  enterpriseId;

    /**
     * 不区分业务类型 Y：N
     */
    private String noBusinessType = "N";

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public List<Long> getDeptId() {
        return deptId;
    }

    public void setDeptId(List<Long> deptId) {
        this.deptId = deptId;
    }

    public List<Long> getMyDeptIdList() {
        return myDeptIdList;
    }

    public void setMyDeptIdList(List<Long> myDeptIdList) {
        this.myDeptIdList = myDeptIdList;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public Date getContractTimeStart() {
        return contractTimeStart;
    }

    public void setContractTimeStart(Date contractTimeStart) {
        this.contractTimeStart = contractTimeStart;
    }

    public Date getContractTimeEnd() {
        return contractTimeEnd;
    }

    public void setContractTimeEnd(Date contractTimeEnd) {
        this.contractTimeEnd = contractTimeEnd;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public RptCtrProfitSearchVo() {
    }

    public String getNoBusinessType() {
        return noBusinessType;
    }

    public void setNoBusinessType(String noBusinessType) {
        this.noBusinessType = noBusinessType;
    }
}
