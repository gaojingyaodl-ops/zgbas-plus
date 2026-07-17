package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.EvaluateAppeal;
import com.spt.bas.client.vo.EvaluateUserApproveWaitDealVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/25 15:36
 */

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/evaluate/appeal",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IEvaluateAppealClient extends BaseClient<EvaluateAppeal> {

    @PostMapping("/findOneByEvaluateUserId")
    EvaluateAppeal findOneByEvaluateUserId(@RequestParam(value = "evaluateUserId") Long evaluateUserId);

    /**
     * 申诉邮件发送
     * @param vo
     */
    @PostMapping("/sendAppealEmail")
    void sendEmail(@RequestBody EvaluateUserApproveWaitDealVo vo);
}
