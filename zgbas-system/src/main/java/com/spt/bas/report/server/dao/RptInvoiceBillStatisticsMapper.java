package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptInvoiceBillStatistics;
import com.spt.bas.report.client.vo.RptInvoiceBillStatisticsVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptInvoiceBillStatisticsMapper {

    List<RptInvoiceBillStatistics> findInvoiceBill(RptInvoiceBillStatisticsVo searchVo);

    List<RptInvoiceBillStatistics> findInvoiceReceiveBill(RptInvoiceBillStatisticsVo searchVo);
}
