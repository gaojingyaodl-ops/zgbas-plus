package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.EvaluateAppeal;
import com.spt.bas.client.vo.EvaluateUserApproveWaitDealVo;
import com.spt.bas.server.dao.EvaluateAppealDao;
import com.spt.bas.server.service.IEvaluateAppealService;
import com.spt.bas.server.util.SMSUtils;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * 考核申诉
 */
@Component
@Slf4j
public class EvaluateAppealServiceImpl extends BaseService<EvaluateAppeal> implements IEvaluateAppealService {
    @Autowired
    private EvaluateAppealDao evaluateAppealDao;
    @Override
    public BaseDao<EvaluateAppeal> getBaseDao() {
        return evaluateAppealDao;
    }

    /**
     * 根据evaluateUserId查询投诉内容
     * @param evaluateUserId 考核人 id
     * @return 投诉相关信息
     */
    @Override
    public EvaluateAppeal findOneByEvaluateUserId(Long evaluateUserId) {
        Specification<EvaluateAppeal> userId = WebUtil.buildSpecification("EQL_evaluateUserId", evaluateUserId);
        Specification<EvaluateAppeal> spe = Specification.where(userId);
        return evaluateAppealDao.findOne(spe).orElse(new EvaluateAppeal());
    }

    /**
     * 申诉邮件发送
     * @param vo
     */
    @Override
    public void sendEmail(EvaluateUserApproveWaitDealVo vo) {

        try {
            // 发送邮件
            SMSUtils.setEvaluateAppealPushEmail(vo);
        } catch (Exception e) {
            logger.error("sendEvaluateUserEmail error", e);
        }
    }
}
