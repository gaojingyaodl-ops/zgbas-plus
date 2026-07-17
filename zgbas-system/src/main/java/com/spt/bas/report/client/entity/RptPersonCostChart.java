package com.spt.bas.report.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 人保成本图表
 */

@Data
public class RptPersonCostChart extends IdEntity {


    /** 所属区域CD */
    private String branchCd;

    /** 所属区域名称 */
    private String branchName;

    /** 工资 */
    private BigDecimal wages = BigDecimal.ZERO;

    /** 提成绩效 */
    private BigDecimal commission = BigDecimal.ZERO;

    /** 五险一金 （社保 + 公积金） */
    private BigDecimal socialSecurity = BigDecimal.ZERO;

    /** 出差报销费用 */
    private BigDecimal evectionCost = BigDecimal.ZERO;

    /** 其它费用 */
    private BigDecimal otherCost = BigDecimal.ZERO;

    /**
     * 人数
     */
    private Integer personCount = 0;

    /**
     * 图表数据分类
     */
    private List<String> rawDataTitle;

    /**
     * x轴 标签
     */
    private List<String> xData;

    /**
     *  图表总标题日期
     */
    private String topTitleDate;

    
    
}
