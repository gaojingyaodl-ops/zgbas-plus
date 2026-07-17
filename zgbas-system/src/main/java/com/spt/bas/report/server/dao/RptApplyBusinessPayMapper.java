package com.spt.bas.report.server.dao;


import com.spt.bas.report.client.entity.RptApplyBusinessPayVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;
@MyBatisDao
public interface RptApplyBusinessPayMapper {
    List<RptApplyBusinessPayVo> findPageContract(RptApplyBusinessPayVo applyBusinessPayVo);

    List<RptApplyBusinessPayVo> selectUserEvectionCost(String baseDate);
}
