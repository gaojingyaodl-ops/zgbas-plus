package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptEvaluateTotalSearch;
import com.spt.bas.report.client.vo.RptEvaluateTotalVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptEvaluateTotalMapper {
    List<RptEvaluateTotalVo> findPageEvaluateTotal(RptEvaluateTotalSearch vo);
}
