package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.*;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/20 11:26
 */
@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/evaluate/evaluateUserDetailClient",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptEvaluateUserDetailRemoteClient {

    /**
     * 查询人员的考核项
     * @param queryVo 查询参数
     * @return 考核项
     */
    @PostMapping("/getEvaluateUserDetailRemote")
    PageDown<RptEvaluateUserDetailRemoteVo> selectEvaluateUserDetail(@RequestBody RptEvaluateUserDetailQueryVo queryVo);

    @PostMapping("/updateList")
    void updateList(@RequestParam(value ="detailScoreListJson")String detailScoreListJson);

    @PostMapping("/findEvaluateUserIdBySourceId")
    PageDown<RptEvaluateUserVo> findEvaluateUserIdBySourceId(@RequestBody RptEvaluateUserSearchVo searchVo);


    /**
     * 根据evaluateUserIds 查询考核详情数据和指标数据
     * @param evaluateUserIds evaluateUserIds
     * @return 考核详情数据和指标数据
     */
    @PostMapping("/getDetailAndItemByEvaluateUserId")
    List<RptDetailAndItemRemoteVo> getDetailAndItemByEvaluateUserId(@RequestParam(value = "evaluateUserIds") String evaluateUserIds);
}
