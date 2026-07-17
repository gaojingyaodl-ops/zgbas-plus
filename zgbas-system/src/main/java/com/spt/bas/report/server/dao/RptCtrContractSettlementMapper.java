package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptContractSettlementVo;
import com.spt.bas.report.client.vo.*;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/4/25 16:34
 */
@MyBatisDao
public interface RptCtrContractSettlementMapper {

    List<RptContractSettlementVo> findCtrContractSettlementList(RptContractSettlementSearchVo vo);
    RptContractSettlementVo findCtrContractSettlementSum(RptContractSettlementSearchVo vo);

    /**
     * 查询业务员考核表
     * @param searchParam 查询参数
     * @return 业务员数据
     */
    List<RptAssessmentResultVo> selectAssessment(RptAssessmentSearch searchParam);

    /**
     * 业绩提成
     *
     * @param query 查询参数
     * @return 业绩提成统计
     */
    RptIndexCommonVo getPerformanceCommission(RptIndexReportQuery query);
}
