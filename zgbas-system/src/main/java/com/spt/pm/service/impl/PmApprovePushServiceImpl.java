package com.spt.pm.service.impl;

import com.hsoft.push.sdk.remote.PushClientHttp;
import com.hsoft.push.sdk.vo.PushRequest;
import com.hsoft.push.sdk.vo.PushResponse;
import com.hsoft.push.sdk.vo.PushTarget;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.dao.PmApprovePushDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.dao.PmProcessPushDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApprovePush;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.entity.PmProcessPush;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApprovePushService;
import com.spt.pm.util.ResConditionParser;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.util.SpringContextHolder;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.wltea.expression.ExpressionToken;
import org.wltea.expression.datameta.Variable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 审批推送记录表
 *
 * @Author: gaojy
 * @create 2022/4/26 11:18
 * @version: 1.0
 * @description:
 */
@Component
@Transactional(readOnly = true)
public class PmApprovePushServiceImpl extends BaseService<PmApprovePush> implements IPmApprovePushService {
    @Autowired
    private PmApprovePushDao approvePushDao;
    @Autowired
    private PmProcessPushDao pmProcessPushDao;
    @Autowired
    private PmProcessDao processDao;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private PushClientHttp pushRemote;

    @Override
    public BaseDao<PmApprovePush> getBaseDao() {
        return approvePushDao;
    }


    /**
     * 执行系统自动推送逻辑
     *
     * @param approve
     */
    @Override
    @ServiceTransactional
    public void addSysPush(PmApprove approve) {
        // 审批完成后执行系统推送
        if (!StringUtils.equals(PmConstants.APPROVE_STATUS_D, approve.getStatus())) {
            return;
        }

        // 查询推送条件
        List<PmProcessPush> pushConditionList = pmProcessPushDao.findPushCondition(approve.getProcessId(), approve.getEnterpriseId());
        if (CollectionUtils.isEmpty(pushConditionList)) {
            return;
        }

        // 验证推送条件
        List<PmProcessPush> processPushList = parseConditionValue(approve, pushConditionList);
        if (CollectionUtils.isEmpty(processPushList)) {
            return;
        }

        List<PmApprovePush> approvePushList = new ArrayList<>();
        processPushList.forEach(push -> {
            PmApprovePush approvePush = new PmApprovePush();
            approvePush.setApproveId(approve.getId());
            approvePush.setApproveNo(approve.getApproveNo());
            approvePush.setProcessId(approve.getProcessId());
            approvePush.setPushToUserId(push.getUserId());
            approvePush.setPushToUserName(push.getUserName());
            approvePush.setEnterpriseId(approve.getEnterpriseId());
            approvePush.setPushType(BasConstants.PUSH_TYPE_1);
            approvePush.setRemark("审批完成，系统自动推送!");
            approvePushList.add(approvePush);
        });
        approvePushDao.saveAll(approvePushList);
        sendApprovePushEmail(approve, approvePushList);
    }

    private void sendApprovePushEmail(PmApprove approve, List<PmApprovePush> approvePushList){
        List<Long> pushUserIdList = approvePushList.stream().map(PmApprovePush::getPushToUserId).distinct().collect(Collectors.toList());
        List<SysUserSdk> sysUserList = authOpenFacade.findByUserIds(pushUserIdList);
        List<String> emailList = sysUserList.stream().map(SysUserSdk::getEmail).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(emailList)) {
            logger.info("sendApprovePushEmail emailList is empty!");
            return;
        }
        PushRequest req = new PushRequest();
        req.setModule("S");
        req.setPushType("setApprovePushEmail");
        req.setSubmitUserId("sys");
        List<PushTarget> lst = new ArrayList<>();
        emailList.forEach(e -> lst.add(new PushTarget(null, null, e)));
        req.setTargets(lst);
        Map<String, Object> param = new HashMap<>();
        param.put("approveNo", approve.getApproveNo());
        param.put("processName", approve.getProcessName());
        param.put("title", approve.getSubject());
        param.put("matchUserName", approve.getCreateUserName());
        param.put("applyDate", DateOperator.formatDate(approve.getCreatedDate()));
        req.setParam(param);
        PushResponse send = pushRemote.send(req);
        logger.info(JsonUtil.obj2Json(send));
    }

    /**
     * 验证条件表达式是否通过
     *
     * @param approve
     * @param pushConditionList
     * @return
     */
    private List<PmProcessPush> parseConditionValue(PmApprove approve, List<PmProcessPush> pushConditionList) {
        List<PmProcessPush> resultList = new ArrayList<>();
        PmProcess process = processDao.findOne(approve.getProcessId());
        IPmService pmService = SpringContextHolder.getBean(process.getEntityService());
        IPmEntity pmEntity = pmService.getEntity(approve.getBizId());
        if (Objects.isNull(pmEntity)) {
            return resultList;
        }
        Map<String, Object> defaultMap = new HashMap<>();
        defaultMap.put("userId", approve.getCreateUserId());
        defaultMap.put("deptId", approve.getDeptId());
        for (PmProcessPush pushCondition : pushConditionList) {
            String conditionValue = pushCondition.getConditionValue();
            if (StringUtils.isBlank(conditionValue)) {
                resultList.add(pushCondition);
            } else {
                List<ExpressionToken> lstToken = ResConditionParser.getVars(conditionValue);
                if (CollectionUtils.isNotEmpty(lstToken)) {
                    Map<String, Object> param = new HashMap<>();
                    lstToken.forEach(t -> {
                        Variable var = t.getVariable();
                        Object varVal = ResConditionParser.getVarValue(var.getVariableName(), pmEntity, defaultMap, process);
                        param.put(var.getVariableName(), varVal);
                    });
                    if (ResConditionParser.validCondition(conditionValue, param)) {
                        resultList.add(pushCondition);
                    }
                }
            }
        }
        return resultList;
    }
}
