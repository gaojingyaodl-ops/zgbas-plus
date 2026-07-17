package com.spt.bas.report.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.report.client.entity.RptPersonCostChart;
import com.spt.bas.report.client.vo.RptPersonCostChartSearchVo;
import com.spt.bas.report.server.dao.RptPersonCostChartMapper;
import com.spt.bas.report.server.service.IRptPersonCostChartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class RptPersonCostChartServiceImpl implements IRptPersonCostChartService {

    private static final Logger log = LoggerFactory.getLogger(RptPersonCostChartServiceImpl.class);
    @Autowired
    private RptPersonCostChartMapper personCostChartMapper;



    @Override
    public List<RptPersonCostChart> personCostChartDataList(RptPersonCostChartSearchVo searchVo) {

        List<RptPersonCostChart> personCostChartList = new ArrayList<>();
        String searchMonth = searchVo.getSearchMonth();
        Date dateTime = DateUtil.parseDate(searchMonth+"-01");
        String topTitleDate = DateUtil.format(dateTime, "yyyy年MM月");
        List<BsDictData> personCostChartBranceCdList = searchVo.getPersonCostChartBranceCdList();
        if (CollUtil.isNotEmpty(personCostChartBranceCdList)) {
            personCostChartBranceCdList.forEach(dictData -> {
                String dictCd = dictData.getDictCd();
                List<String> branchCdList = Arrays.asList(dictCd.split(","));
                searchVo.setBranchCdList(branchCdList);
                RptPersonCostChart personCostChartData = personCostChartMapper.getPersonCostChartData(searchVo);
                personCostChartData.setBranchName(dictData.getDictName());
                personCostChartData.setTopTitleDate(topTitleDate);
                personCostChartList.add(personCostChartData);
            });
        } else {
            RptPersonCostChart personCostChartData = personCostChartMapper.getPersonCostChartData(searchVo);
            personCostChartData.setBranchName("全部");
            personCostChartData.setTopTitleDate(topTitleDate);
            personCostChartList.add(personCostChartData);
        }

        return personCostChartList;
    }
}
