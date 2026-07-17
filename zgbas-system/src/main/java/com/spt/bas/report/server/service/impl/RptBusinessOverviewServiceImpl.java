package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.entity.RptBusinessOverview;
import com.spt.bas.report.client.vo.RptBusinessOverviewSearchVo;
import com.spt.bas.report.server.dao.RptBusinessOverviewMapper;
import com.spt.bas.report.server.service.IRptBusinessOverviewService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RptBusinessOverviewServiceImpl implements IRptBusinessOverviewService {
    
    @Autowired
    private RptBusinessOverviewMapper businessOverviewMapper;

    @Override
    public List<RptBusinessOverview> findBusinessOverviewList(RptBusinessOverviewSearchVo searchVo) {
        List<RptBusinessOverview> businessOverviewList = businessOverviewMapper.findBusinessOverviewList(searchVo);
        Boolean businessZlPerm = searchVo.getBusinessZlPerm();
        if (CollectionUtils.isNotEmpty(businessOverviewList)) {
            for (RptBusinessOverview businessOverview : businessOverviewList) {
                if (businessZlPerm) {
                    if (StringUtils.equals("H", businessOverview.getStatisticalType())) {
                        businessOverview.setDcsxNum("");
                    }
                    if (StringUtils.equals("L", businessOverview.getStatisticalType())) {
                        businessOverview.setBuyNum("");
                        businessOverview.setDcsxNum("");
                    }
                }
            }
        }
        
        return businessOverviewList;
    }
}
