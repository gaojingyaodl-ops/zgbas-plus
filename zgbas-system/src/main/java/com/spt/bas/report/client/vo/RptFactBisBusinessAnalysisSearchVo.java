package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 经营分析查询参数
 */
@Data
public class RptFactBisBusinessAnalysisSearchVo {
    /**
     * 合同ID
     */
    private Long id;

    /**
     * 部门ID列表
     */
    private List deptIdList;

    /**
     * 区域编码列表
     */
    private List<String> branchCdList;

    /**
     * 用户ID列表
     */
    private List userIdList;

    /**
     * 月份查询参数 ：2026-03
     */
    private String month;

    /**
     * 月份列表
     */
    private List<String> monthList;
    
    /**
     * 销售合同签订开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sealStartDate;

    /**
     * 销售合同签订结束时间
     */ 
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone= "GMT+08:00")
    private Date sealEndDate;

    /**
     * 赊销标识
     */
    private Boolean matchCreditFlg;

    /**
     * 指标类型
     */
    private String targetType;

    /**
     * 年度
     */
    private String year;

    /**
     * 季度
     */
    private String quarter;

}
