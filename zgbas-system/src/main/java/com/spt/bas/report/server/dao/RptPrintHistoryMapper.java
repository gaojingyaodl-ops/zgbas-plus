package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptPrintHistoryReport;
import com.spt.tools.mybatis.annotation.MyBatisDao;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/16 10:54
 */
@MyBatisDao
public interface RptPrintHistoryMapper {
    /**
     * 添加审批打印记录
     *
     * @param printHistoryReport 打印信息
     * @return 打印次数
     */
    void addPrintHistory(RptPrintHistoryReport printHistoryReport);

    Integer selectPrintCountByApproveNo(String approveNo);
}
