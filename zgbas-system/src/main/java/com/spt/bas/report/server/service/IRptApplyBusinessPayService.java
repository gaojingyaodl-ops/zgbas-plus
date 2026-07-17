package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptApplyBusinessPayVo;
import com.spt.tools.data.vo.PageDown;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IRptApplyBusinessPayService {

    Page<RptApplyBusinessPayVo> findPageContract(RptApplyBusinessPayVo searchVo);

    List<RptApplyBusinessPayVo> selectUserEvectionCost(String baseDate);
}
