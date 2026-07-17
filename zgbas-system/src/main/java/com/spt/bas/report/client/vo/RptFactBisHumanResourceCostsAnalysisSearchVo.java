package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 人力成本分析查询参数
 */
@Data
public class RptFactBisHumanResourceCostsAnalysisSearchVo {
    /**
     * 合同ID
     */
    private Long id;

    /**
     * 部门id列表
     */
    private List<Long> deptIdList;

    /**
     * 用户id列表
     */
    private List<Long> userIdList;

    /**
     * 去除用户id列表
     */
    private List<Long> notUserIdList;

    /**
     * 前台部门id列表
     */
    private List<Long> frontDeptIdList;

    /**
     * 中台部门id列表
     */
    private List<Long> middleDeptIdList;

    /**
     * 后台部门id列表
     */
    private List<Long> backDeptIdList;

    /**
     * 区域编码列表
     */
    private List<String> branchCdList;
    
    /**
     * 月份查询参数 ：2026-03
     */
    private String month;

    /**
     * 月份列表
     */
    private List<String> monthList;
    
    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date startDate;

    /**
     * 结束时间
     */ 
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone= "GMT+08:00")
    private Date endDate;

}
