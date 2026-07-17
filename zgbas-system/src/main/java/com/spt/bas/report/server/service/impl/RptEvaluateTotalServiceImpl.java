package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.entity.RptEvaluateTotalSearch;
import com.spt.bas.report.client.vo.RptEvaluateTotalVo;
import com.spt.bas.report.server.dao.RptEvaluateTotalMapper;
import com.spt.bas.report.server.service.IRptEvaluateTotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RptEvaluateTotalServiceImpl implements IRptEvaluateTotalService {
    @Autowired
    private RptEvaluateTotalMapper evaluateTotalMapper;

    /**
     * 查询考评信息
     * @param vo
     * @return
     */
    @Override
    public Page<RptEvaluateTotalVo> findPageEvaluateTotal(RptEvaluateTotalSearch vo) {
        List<RptEvaluateTotalVo> list = evaluateTotalMapper.findPageEvaluateTotal(vo);
        Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
        Page<RptEvaluateTotalVo> pageVo = new PageImpl<>(list, pageable, vo.getCount());
        return pageVo;
    }
}
