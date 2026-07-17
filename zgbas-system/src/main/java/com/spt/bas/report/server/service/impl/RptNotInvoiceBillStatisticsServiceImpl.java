package com.spt.bas.report.server.service.impl;

import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.report.client.entity.RptNotInvoiceBillStatistics;
import com.spt.bas.report.client.vo.RptFunderVo;
import com.spt.bas.report.client.vo.RptNotInvoiceBillStatisticsSearchVo;
import com.spt.bas.report.server.dao.RptNotInvoiceBillStatisticsMapper;
import com.spt.bas.report.server.service.IRptNotInvoiceBillStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class RptNotInvoiceBillStatisticsServiceImpl implements IRptNotInvoiceBillStatisticsService {

    @Autowired
    private RptNotInvoiceBillStatisticsMapper rptNotInvoiceBillStatisticsMapper;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    
    
    /**
     * 未开票明细分页查询
     *
     */
    @Override
    public Page<RptNotInvoiceBillStatistics> findRptNotInvoiceBillStatisticsPage(RptNotInvoiceBillStatisticsSearchVo searchVo) {
        List<RptNotInvoiceBillStatistics> NotInvoiceBillStatisticsList = new ArrayList<>();
        List<String> ourCompanyNameList = new ArrayList<>();
        if(searchVo.getFunderFlg()) {
            // 根据当前用户查询资金方管理数据
            RptFunderVo funderVo = rptNotInvoiceBillStatisticsMapper.selectFunderByUserId(searchVo.getUserId());
            if(Objects.nonNull(funderVo)) {
                String companyNames = funderVo.getCompanyNames();
                String[] split = companyNames.split(",");
                for (String companyName : split) {
                    ourCompanyNameList.add(companyName);
                }
            } else {
                Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
                Page<RptNotInvoiceBillStatistics> pageVo = new PageImpl<>(NotInvoiceBillStatisticsList, pageable, searchVo.getCount());
                return pageVo;
            }

        }
        searchVo.setOurCompanyNameList(ourCompanyNameList);
        
        List<RptNotInvoiceBillStatistics> list = rptNotInvoiceBillStatisticsMapper.findRptNotInvoiceBillStatisticsPage(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptNotInvoiceBillStatistics> pageVo = new PageImpl<>(list, pageable, searchVo.getCount());
        return pageVo;
    }

    @Override
    public RptNotInvoiceBillStatistics findRptNotInvoiceBillStatisticsSum(RptNotInvoiceBillStatisticsSearchVo searchVo) {
        List<String> ourCompanyNameList = new ArrayList<>();
        if(searchVo.getFunderFlg()) {
            // 根据当前用户查询资金方管理数据
            RptFunderVo funderVo = rptNotInvoiceBillStatisticsMapper.selectFunderByUserId(searchVo.getUserId());
            if(Objects.nonNull(funderVo)) {
                String companyNames = funderVo.getCompanyNames();
                String[] split = companyNames.split(",");
                for (String companyName : split) {
                    ourCompanyNameList.add(companyName);
                }
            } else {
                
                return new RptNotInvoiceBillStatistics();
            }

        }
        searchVo.setOurCompanyNameList(ourCompanyNameList);
        return rptNotInvoiceBillStatisticsMapper.findRptNotInvoiceBillStatisticsSum(searchVo);
    }
}
