package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.vo.RptCtrContractFinanceSearch;
import com.spt.bas.report.client.vo.RptCtrContractFinanceVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptCtrContractFinanceVoMapper {
    List<RptCtrContractFinanceVo> findContractFinancePage(RptCtrContractFinanceSearch vo);
}
