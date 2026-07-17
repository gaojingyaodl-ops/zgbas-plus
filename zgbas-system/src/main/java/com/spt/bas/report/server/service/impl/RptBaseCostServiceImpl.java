package com.spt.bas.report.server.service.impl;

import com.spt.bas.client.vo.RptBaseCostVo;
import com.spt.bas.report.client.vo.RptBaseCostReportVo;
import com.spt.bas.report.server.dao.RptBaseCostMapper;
import com.spt.bas.report.server.service.IRptBaseCostService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lsj
 * @version 1.0.0
 * @date 2025/1/13 15:58
 */
@Service
public class RptBaseCostServiceImpl implements IRptBaseCostService {

    @Autowired
    private RptBaseCostMapper rptBaseCostMapper;

    /**
     *
     * @param rptBaseCostVo 查询参数
     * @return 结果
     */
    @Override
    public Page<RptBaseCostReportVo> findPage(RptBaseCostVo rptBaseCostVo) {

        String baseStartDate = rptBaseCostVo.getBaseStartDate();
        String baseEndDate = rptBaseCostVo.getBaseEndDate();
        List<RptBaseCostReportVo> content = rptBaseCostMapper.selectBaseCostPage(rptBaseCostVo);

        for (RptBaseCostReportVo report : content) {
            StringBuilder baseDate = new StringBuilder();
            if (StringUtils.isNotBlank(baseStartDate) && StringUtils.isNotBlank(baseEndDate) && StringUtils.equals(baseStartDate, baseEndDate)) {
                baseDate.append(baseStartDate);
            } else {
                if (StringUtils.isNotBlank(baseStartDate)) {
                    baseDate.append(baseStartDate);
                } else {
                    baseDate.append("-∞");
                }
                baseDate.append(" ~ ");
                if (StringUtils.isNotBlank(baseEndDate)) {
                    baseDate.append(baseEndDate);
                } else {
                    baseDate.append("∞");
                }
            }
            report.setBaseDate(baseDate.toString());
        }

        
        Pageable pageable = PageRequest.of(rptBaseCostVo.getPage() - 1, rptBaseCostVo.getRows());
        Page<RptBaseCostReportVo> pageVo = new PageImpl<>(content, pageable, rptBaseCostVo.getCount());
        return pageVo;
    }
    
    /**
     * 合计
     *
     * @return 合计
     */
    @Override
    public Map<String, Object> getTotal(RptBaseCostVo rptBaseCostVo) {
        RptBaseCostReportVo sumRptBaseCost = rptBaseCostMapper.selectBaseCostSum(rptBaseCostVo);
        Map<String, Object> result = new HashMap<>();
        if (Objects.nonNull(sumRptBaseCost)) {
            result.put("matchUserName","合计");
            result.put("wages",sumRptBaseCost.getWages());
            result.put("commission",sumRptBaseCost.getCommission());
            result.put("otherCost",sumRptBaseCost.getOtherCost());
            result.put("socialSecurity",sumRptBaseCost.getSocialSecurity());
            result.put("providentFund",sumRptBaseCost.getProvidentFund());
            result.put("evectionCost",sumRptBaseCost.getEvectionCost());
            result.put("totalCost",sumRptBaseCost.getTotalCost());
        }
        return result;
    }
}
