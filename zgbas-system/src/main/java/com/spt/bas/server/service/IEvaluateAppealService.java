package com.spt.bas.server.service;

import com.spt.bas.client.entity.EvaluateAppeal;
import com.spt.bas.client.vo.EvaluateUserApproveWaitDealVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IEvaluateAppealService extends IBaseService<EvaluateAppeal> {
    /**
     * 根据evaluateUserId查询投诉内容
     * @param evaluateUserId 考核人 id
     * @return 投诉相关信息
     */
    EvaluateAppeal findOneByEvaluateUserId(Long evaluateUserId);

    /**
     * 申诉邮件发送
     * @param vo
     */
    void sendEmail(EvaluateUserApproveWaitDealVo vo);
}
