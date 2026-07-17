package com.spt.bas.report.server.service;

import com.spt.bas.report.client.vo.RptAssessmentResultVo;
import com.spt.bas.report.client.vo.RptAssessmentSearch;
import org.springframework.data.domain.Page;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/4/25 15:53
 */

public interface IRptAssessmentService {
    /**
     * 查询月度业务员考核表
     * @param selectParam 查询参数
     * @return
     */
    Page<RptAssessmentResultVo> selectAssessment(RptAssessmentSearch selectParam);

    /**
     * 查询业务员季度考核表
     * @param selectParam 查询参数
     * @return 返回业务员季度考核表
     */
    Page<RptAssessmentResultVo> selectAssessmentQuarterOrYear(RptAssessmentSearch selectParam);

}
