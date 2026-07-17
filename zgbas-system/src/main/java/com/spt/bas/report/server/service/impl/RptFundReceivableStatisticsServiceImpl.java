package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.entity.RptFundReceivableStatistics;
import com.spt.bas.report.client.vo.RptFundReceivableStatisticsVo;
import com.spt.bas.report.server.dao.RptFundReceivableStatisticsMapper;
import com.spt.bas.report.server.service.IRptFundReceivableStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RptFundReceivableStatisticsServiceImpl implements IRptFundReceivableStatisticsService {
    @Autowired
    private RptFundReceivableStatisticsMapper fundReceivableStatisticsMapper;

    @Override
    public Page<RptFundReceivableStatistics> findPage(RptFundReceivableStatisticsVo searchVo) {
        List<RptFundReceivableStatistics> fundReceivableStatisticsList = fundReceivableStatisticsMapper.findPage(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptFundReceivableStatistics> pageVo = new PageImpl<>(fundReceivableStatisticsList, pageable, searchVo.getCount());
        return pageVo;
    }
}
