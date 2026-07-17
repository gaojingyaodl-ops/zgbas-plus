package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.entity.RptNotBillStatistics;
import com.spt.bas.report.client.vo.RptFunderVo;
import com.spt.bas.report.client.vo.RptNotBillStatisticsSearchVo;
import com.spt.bas.report.server.dao.RptNotBillStatisticsMapper;
import com.spt.bas.report.server.service.IRptNotBillStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class RptNotBillStatisticsServiceImpl implements IRptNotBillStatisticsService {

    @Autowired
    private RptNotBillStatisticsMapper rptNotBillStatisticsMapper;
    
    /**
     * 未收票明细分页查询
     *
     */
    @Override
    public Page<RptNotBillStatistics> findRptNotBillStatisticsPage(RptNotBillStatisticsSearchVo searchVo) {
        List<RptNotBillStatistics> NotBillStatisticsList = new ArrayList<>();
        List<String> ourCompanyNameList = new ArrayList<>();
        if(searchVo.getFunderFlg()) {
            // 根据当前用户查询资金方管理数据
            RptFunderVo funderVo = rptNotBillStatisticsMapper.selectFunderByUserId(searchVo.getUserId());
            if(Objects.nonNull(funderVo)) {
                String companyNames = funderVo.getCompanyNames();
                String[] split = companyNames.split(",");
                for (String companyName : split) {
                    ourCompanyNameList.add(companyName);
                }
            } else {
                Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
                Page<RptNotBillStatistics> pageVo = new PageImpl<>(NotBillStatisticsList, pageable, searchVo.getCount());
                return pageVo;
            }

        }
        searchVo.setOurCompanyNameList(ourCompanyNameList);
        
        List<RptNotBillStatistics> list = rptNotBillStatisticsMapper.findRptNotBillStatisticsPage(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptNotBillStatistics> pageVo = new PageImpl<>(list, pageable, searchVo.getCount());
        return pageVo;
    }
    

    @Override
    public RptNotBillStatistics findRptNotBillStatisticsSum(RptNotBillStatisticsSearchVo searchVo) {
        List<String> ourCompanyNameList = new ArrayList<>();
        if(searchVo.getFunderFlg()) {
            // 根据当前用户查询资金方管理数据
            RptFunderVo funderVo = rptNotBillStatisticsMapper.selectFunderByUserId(searchVo.getUserId());
            if(Objects.nonNull(funderVo)) {
                String companyNames = funderVo.getCompanyNames();
                String[] split = companyNames.split(",");
                for (String companyName : split) {
                    ourCompanyNameList.add(companyName);
                }
            } else {
                
                return new RptNotBillStatistics();
            }

        }
        searchVo.setOurCompanyNameList(ourCompanyNameList);
        return rptNotBillStatisticsMapper.findRptNotBillStatisticsSum(searchVo);
    }
}
