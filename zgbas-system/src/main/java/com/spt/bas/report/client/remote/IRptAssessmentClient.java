package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.RptAssessmentResultVo;
import com.spt.bas.report.client.vo.RptAssessmentSearch;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/4/25 15:49
 */

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/Assessment",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptAssessmentClient extends BaseClient<RptAssessmentResultVo> {

    /**
     * 查询月度业务员考核表
     * @param selectParam 查询参数
     * @return 月度业务员考核数据
     */
    @PostMapping("/selectAssessmentMonth")
    PageDown<RptAssessmentResultVo> selectAssessmentMonth(@RequestBody RptAssessmentSearch selectParam);

    /**
     * 查询业务员季度考核表
     * @param selectParam 查询参数
     * @return 返回业务员季度考核表
     */
    @PostMapping("/selectAssessmentQuarter")
    PageDown<RptAssessmentResultVo> selectAssessmentQuarter(@RequestBody RptAssessmentSearch selectParam);

    /**
     * 查询业务员年度考核表
     * @param selectParam 查询参数
     * @return 返回业务员年度考核表
     */
    @PostMapping("/selectAssessmentYear")
    PageDown<RptAssessmentResultVo> selectAssessmentYear(@RequestBody RptAssessmentSearch selectParam);
}
