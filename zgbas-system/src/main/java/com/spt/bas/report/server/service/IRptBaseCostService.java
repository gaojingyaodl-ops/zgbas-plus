package com.spt.bas.report.server.service;

import com.spt.bas.client.vo.RptBaseCostVo;
import com.spt.bas.report.client.vo.RptBaseCostReportVo;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * @author lsj
 * @version 1.0.0
 * @date 2025/01/13 15:58
 */
public interface IRptBaseCostService {
    Page<RptBaseCostReportVo> findPage(RptBaseCostVo rptBaseCostVo);

    /**
     * 合计
     *
     * @return 合计
     */
    Map<String, Object> getTotal(RptBaseCostVo rptBaseCostVo);

}
