package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.vo.RptCtrContractUnDeliverySearchVo;
import com.spt.bas.report.client.vo.RptCtrContractUnDeliveryVo;
import com.spt.bas.report.server.dao.RptCtrContractUnDeliveryMapper;
import com.spt.bas.report.server.service.IRptCtrContractUnDeliveryService;
import com.spt.tools.core.date.DateOperator;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class RptCtrContractUnDeliveryServiceImpl implements IRptCtrContractUnDeliveryService {
    @Autowired
    private RptCtrContractUnDeliveryMapper contractUnDeliveryMapper;

    @Override
    public Page<RptCtrContractUnDeliveryVo> findUnDeliveryPage(RptCtrContractUnDeliverySearchVo vo) {
        List<RptCtrContractUnDeliveryVo> list = contractUnDeliveryMapper.findUnDeliveryPage(vo);
        Date now = DateOperator.truncDate(new Date());
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item -> {
                Long overdueDay = DateOperator.compareDays(DateOperator.truncDate(item.getDeliveryDateFrom()), now);
                item.setOverdueDay(Math.abs(overdueDay));
            });
        }
        Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
        Page<RptCtrContractUnDeliveryVo> pageVo = new PageImpl<>(list, pageable, vo.getCount());
        return pageVo;
    }
}
