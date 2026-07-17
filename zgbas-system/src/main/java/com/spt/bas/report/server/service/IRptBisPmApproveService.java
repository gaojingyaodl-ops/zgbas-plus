package com.spt.bas.report.server.service;

import com.spt.bas.report.client.vo.RptBisSearchVo;
import com.spt.bas.report.client.vo.RptCtrContractSettlementDateSearchVo;
import com.spt.bas.report.client.vo.RptCtrContractSettlementDateVo;
import com.spt.tools.core.bean.RespVo;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/11/8 10:13
 */

public interface IRptBisPmApproveService {

    /**
     * 中光业务系统查询相关审批单数据接口
     *
     * @param searchVo 查询参数
     * @return 结果
     */
    RespVo<?> getPmApproveByUserId(RptBisSearchVo searchVo);

    List<RptCtrContractSettlementDateVo> getSettlementBusinessDate(RptCtrContractSettlementDateSearchVo searchVo);
}
