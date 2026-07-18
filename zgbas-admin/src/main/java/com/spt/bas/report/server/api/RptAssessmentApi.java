package com.spt.bas.report.server.api;

import com.spt.bas.report.client.vo.RptAssessmentResultVo;
import com.spt.bas.report.client.vo.RptAssessmentSearch;
import com.spt.bas.report.server.service.IRptAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/4/25 15:52
 */
@RestController
@RequestMapping(value = "/rpt/Assessment")
public class RptAssessmentApi {

    @Autowired
    private IRptAssessmentService assessmentService;

    /**
     * 查询月度业务员考核表
     * @param selectParam 查询参数
     * @return 查询结果
     */
    @PostMapping("/selectAssessmentMonth")
    public Page<RptAssessmentResultVo> selectAssessmentMonth(@RequestBody RptAssessmentSearch selectParam){
        return assessmentService.selectAssessment(selectParam);
    }

    /**
     * 查询业务员季度考核表
     * @param selectParam 查询参数
     * @return 返回业务员季度考核表
     */
    @PostMapping("/selectAssessmentQuarter")
    public Page<RptAssessmentResultVo> selectAssessmentQuarter(@RequestBody RptAssessmentSearch selectParam){
        return assessmentService.selectAssessmentQuarterOrYear(selectParam);
    }

    /**
     * 查询业务员年度考核表
     * @param selectParam 查询参数
     * @return 返回业务员年度考核表
     */
    @PostMapping("/selectAssessmentYear")
    public Page<RptAssessmentResultVo> selectAssessmentYear(@RequestBody RptAssessmentSearch selectParam){
        return assessmentService.selectAssessmentQuarterOrYear(selectParam);
    }
}
