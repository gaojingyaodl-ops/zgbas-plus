package com.spt.bas.report.client.vo;

import com.spt.bas.client.entity.BsDictData;
import lombok.Data;

import java.util.List;

/**
 * 人员成本图表查询VO
 */
@Data
public class RptPersonCostChartSearchVo {

    /**
     * 月份
     */
    private String searchMonth;

    private List<BsDictData> personCostChartBranceCdList;

    /**
     * 类型
     */
    private List<String> branchCdList;

}
