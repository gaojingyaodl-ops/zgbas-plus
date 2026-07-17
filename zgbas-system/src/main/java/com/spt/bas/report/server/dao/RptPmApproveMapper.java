package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.vo.*;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/7/7 13:46
 */
@MyBatisDao
public interface RptPmApproveMapper {
    /**
     * 查询当前审批人是自己的
     * @param query 查询参数
     * @return 结果
     */
    Integer getApproveCount(RptIndexReportQuery query);

    List<RptBisPmApproveResVo> getPmApproveByParam(RptBisSearchVo searchVo);

    List<RptCtrContractSettlementDateVo> getSettlementBusinessDate(RptCtrContractSettlementDateSearchVo searchVo);
}
