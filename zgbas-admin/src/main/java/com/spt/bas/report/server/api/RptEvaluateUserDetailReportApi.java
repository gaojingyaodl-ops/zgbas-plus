package com.spt.bas.report.server.api;

import com.spt.bas.report.client.vo.*;
import com.spt.bas.report.server.service.IRptEvaluateUserDetailReportService;
import com.spt.tools.core.json.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/20 11:30
 */
@RestController
@RequestMapping(value = "/evaluate/evaluateUserDetailClient")
public class RptEvaluateUserDetailReportApi {

    @Autowired
    private IRptEvaluateUserDetailReportService evaluateUserDetailReportService;

    /**
     * 查询考核人员的考核项
     * @param queryVo 查询参数
     * @return 考核项
     */
    @PostMapping("/getEvaluateUserDetailRemote")
    public Page<RptEvaluateUserDetailRemoteVo> selectEvaluateUserDetail(@RequestBody RptEvaluateUserDetailQueryVo queryVo){
        return evaluateUserDetailReportService.selectEvaluateUserDetail(queryVo);
    }

    /**
     * 批量更新
     * @param detailScoreListJson
     */
    @PostMapping("/updateList")
    public void updateList(@RequestParam(value ="detailScoreListJson")String detailScoreListJson){
        List<RptDetailScoreVo> detailScoreVos = JsonUtil.json2List(RptDetailScoreVo.class, detailScoreListJson);
        evaluateUserDetailReportService.updateList(detailScoreVos);
    }

    @PostMapping("/findEvaluateUserIdBySourceId")
    public Page<RptEvaluateUserVo> findEvaluateUserIdBySourceId(@RequestBody RptEvaluateUserSearchVo searchVo){
        return evaluateUserDetailReportService.findEvaluateUserIdBySourceId(searchVo);
    }

    /**
     * 根据evaluateUserIds 查询考核详情数据和指标数据
     * @param evaluateUserIds evaluateUserIds
     * @return 考核详情数据和指标数据
     */
    @PostMapping("/getDetailAndItemByEvaluateUserId")
    List<RptDetailAndItemRemoteVo> getDetailAndItemByEvaluateUserId(@RequestParam(value = "evaluateUserIds") String evaluateUserIds){
        return evaluateUserDetailReportService.getDetailAndItemByEvaluateUserId(evaluateUserIds);
    }


}
