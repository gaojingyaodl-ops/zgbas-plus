package com.spt.bas.server.service;

import com.spt.bas.client.entity.RptBaseCost;
import com.spt.bas.client.vo.RptBaseCostVo;
import com.spt.tools.jpa.service.IBaseService;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IRptBaseCostService extends IBaseService<RptBaseCost> {

    List<String> initData(String fileId);

    void refreshUserEvectionCost(String baseDate);

    RptBaseCost findSumPage(Map<String, Object> searchParams);

    String getCostbaseByImportExcel(String fileId);

    List<RptBaseCost> findRptBaseCostByBaseDate(Date baseDate);
}
