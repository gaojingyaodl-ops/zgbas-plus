package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptCountOfMatchUserReport;
import com.spt.bas.report.client.entity.RptMatchUserReport;
import com.spt.bas.report.client.payload.MatchUser;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

/**
 * 业务员统计
 * @author shengong
 */
@MyBatisDao
public interface RptRiskMatchUserReportMapper {
    /**
     * 业务员业务统计信息
     * @param matchUser
     * @return
     */
    List<RptMatchUserReport> getMatchUserList(MatchUser matchUser);

    /**
     * 业务员业务统计信息（footer）
     * @param matchUser
     * @return
     */
    RptCountOfMatchUserReport countMatchUserList(MatchUser matchUser);

}
