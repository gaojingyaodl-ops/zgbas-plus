    package com.spt.bas.report.server.service.impl;


import com.spt.bas.report.client.entity.RptApplyBusinessPayVo;
import com.spt.bas.report.server.dao.RptApplyBusinessPayMapper;
import com.spt.bas.report.server.service.IRptApplyBusinessPayService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Service
public class RptApplyBusinessPayServiceImpl implements IRptApplyBusinessPayService {

    @Autowired
    private RptApplyBusinessPayMapper applyBusinessPayMapper;

    @Override
    public Page<RptApplyBusinessPayVo> findPageContract(RptApplyBusinessPayVo searchVo) {
        List<RptApplyBusinessPayVo> list = applyBusinessPayMapper.findPageContract(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        List<RptApplyBusinessPayVo> voList = new ArrayList<RptApplyBusinessPayVo>();
        int i = 0;
        for (RptApplyBusinessPayVo ctr : list) {
            i++;
            RptApplyBusinessPayVo vo=new RptApplyBusinessPayVo();
            BeanUtils.copyProperties(ctr, vo);
            vo.setPairId(Long.valueOf(i));
            voList.add(vo);
        }
        Page<RptApplyBusinessPayVo> page = new PageImpl<>(voList, pageable, searchVo.getCount());
        return page;
    }

    @Override
    public List<RptApplyBusinessPayVo> selectUserEvectionCost(String baseDate) {
        return applyBusinessPayMapper.selectUserEvectionCost(baseDate);
    }
}
