package com.spt.bas.report.server.service;

import com.spt.bas.report.client.vo.RptCtrContractFinanceSearch;
import com.spt.bas.report.client.vo.*;
import org.springframework.data.domain.Page;


public interface IRptCtrContractFinanceService {
    /**
     * 合同列表
     *
     * @param vo 多查询条件
     */
    Page<RptCtrContractFinanceVo> findContractFinancePage(RptCtrContractFinanceSearch vo);


}
