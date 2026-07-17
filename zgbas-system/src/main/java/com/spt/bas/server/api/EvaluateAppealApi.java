package com.spt.bas.server.api;

import com.spt.bas.client.entity.EvaluateAppeal;
import com.spt.bas.client.vo.EvaluateUserApproveWaitDealVo;
import com.spt.bas.server.service.IEvaluateAppealService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/25 15:47
 */
@RestController
@RequestMapping(value = "evaluate/appeal")
public class EvaluateAppealApi extends BaseApi<EvaluateAppeal> {
    @Autowired
    private IEvaluateAppealService evaluateAppealService;

    @Override
    public IDataService<EvaluateAppeal> getService() {
        return evaluateAppealService;
    }

    /**
     * 根据evaluateUserId查询投诉内容
     * @param evaluateUserId 考核人 id
     * @return 投诉相关信息
     */
    @PostMapping("/findOneByEvaluateUserId")
    public EvaluateAppeal findOneByEvaluateUserId(@RequestParam(value = "evaluateUserId") Long evaluateUserId){
        return evaluateAppealService.findOneByEvaluateUserId(evaluateUserId);
    }
    /**
     * 申诉邮件发送
     * @param vo
     */
    @PostMapping("/sendAppealEmail")
    void sendEmail(@RequestBody EvaluateUserApproveWaitDealVo vo){
        evaluateAppealService.sendEmail(vo);
    }
}
