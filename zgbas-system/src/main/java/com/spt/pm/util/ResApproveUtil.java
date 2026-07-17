package com.spt.pm.util;

import cn.hutool.core.util.NumberUtil;
import com.google.common.base.Splitter;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.SealUsageDCSX;
import com.spt.bas.client.remote.ISealUsageDCSXClient;
import com.spt.pm.cache.PmNodeCache;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.dao.PmProcessNodeDao;
import com.spt.pm.entity.*;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.*;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveStepFlowVo;
import com.spt.pm.vo.PmProcessConditionStepVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.InvalidParamException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.reflect.ReflectUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: gaojy
 * @create 2022/4/12 14:37
 * @version: 1.0
 * @description:
 */
@Component
public class ResApproveUtil {
    protected Logger logger = LoggerFactory.getLogger(ResApproveUtil.class);
    private static final String CONTRACT_ID = "contractId";
    private static final String SELL_CONTRACT_ID = "sellContractId";
    private static final String COMPANY_ID = "companyId";
    private static final String GET_CONTRACT_ID = "getContractId";
    private static final String GET_SELL_CONTRACT_ID = "getSellContractId";
    private static final String GET_COMPANY_ID = "getCompanyId";
    @Autowired
    private PmProcessNodeDao pmProcessNodeDao;
    @Autowired
    private IPmParseService parseService;
    @Autowired
    private ISealUsageDCSXClient sealUsageDcsxClient;
    @Autowired
    private IBsKeySequenceService keySequenceService;
    @Autowired
    private IPmApproveHistoryService historyService;
    @Autowired
    private IPmProcessNodeService pmProcessNodeService;
    @Autowired
    private IPmApproveStepService approveStepService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmApproveService pmApproveService;


    /**
     * 赊销预算、代采赊销预算发起时，判断客户企业等级如果是C类或D类，自动在第一个步骤添加审批步骤“签署连带责任保证书”审批人是发起的业务员
     *
     * @param process
     * @param bizEntity
     * @param lstStep
     */
    public void dealWithLiabilityGuarantee(PmProcess process, IPmEntity bizEntity, List<PmProcessStep> lstStep) {
        if (CollectionUtils.isEmpty(lstStep)) {
            return;
        }
        if (BasConstants.BS_CONFIG_FILTER_PROCESS_LIST.contains(process.getProcessCode())) {
            if (bizEntity instanceof ApplyMatch) {
                ApplyMatch match = (ApplyMatch) bizEntity;
                if (Boolean.TRUE.equals(match.getLiabilityFlg())) {
                    parseLiabilityStep(lstStep);
                }
            }
        }
    }

    /**
     * 设置审批主表参数
     *
     * @param startVo
     * @param bizEntity
     * @param process
     * @return
     */
    public PmApprove buildNewPmApprove(PmApproveSaveVo startVo, IPmEntity bizEntity, PmProcess process) throws ApplicationException {
        PmApprove approve = new PmApprove();
        approve.setApproveNo(getNextKey(startVo.getEnterpriseId()));
        approve.setBizId(bizEntity.getId());

        // 审批表新增两个字段contractId和companyId，用来检索和业务员相关审批单
        setRelationFields(approve, bizEntity);

        approve.setCreateUserName(startVo.getUserName());
        approve.setCreateUserId(startVo.getUserId());
        approve.setEnterpriseId(startVo.getEnterpriseId());
        approve.setDeptId(startVo.getDeptId());
        approve.setStartDate(new Date());
        approve.setProcessId(process.getId());
        approve.setProcessName(process.getProcessName());
        approve.setHideOut(StringUtils.isNotBlank(startVo.getHideOut()) ? startVo.getHideOut() : "0");
        approve.setTradeFlg(Boolean.TRUE.equals(startVo.getTradeFlg()) ? true : false);
        approve = pmApproveService.save(approve);
        return approve;
    }

    public synchronized String getNextKey(Long enterpriseId){
        return keySequenceService.getNextKey(PmConstants.KEYSEQUENCE_CATEGORY_APPROVENO, enterpriseId);
    }

    /**
     * 驳回重新发起，清空审批人历史信息
     *
     * @param approve
     * @return
     */
    public PmApprove clearApproveStepUser(PmApprove approve) {
        approve.setLastApproveDate(null);
        approve.setLastApproveRemark(null);
        approve.setLastApproveUserId(null);
        approve.setLastApproveUserName(null);
        approve.setStartDate(new Date());
        return approve;
    }

    /**
     * 审批表新增两个字段contractId和companyId，用来检索和业务员相关审批单
     *
     * @param approve
     * @param bizEntity
     * @return
     */
    private PmApprove setRelationFields(PmApprove approve, IPmEntity bizEntity) {
        Long contractId = null;
        Long companyId = null;
        try {
            logger.info("startFlow=====bizEntity:{}", JsonUtil.obj2Json(bizEntity));
            if (bizEntity instanceof PmApproveContents) {
                PmApproveContents contents = (PmApproveContents) bizEntity;
                Map<String, Object> m = JsonUtil.json2Map(contents.getContents());
                if (m.get(CONTRACT_ID) != null) {
                    contractId = Long.parseLong(m.get(CONTRACT_ID) + "");
                }
                if (contractId == null && m.get(SELL_CONTRACT_ID) != null) {
                    contractId = Long.parseLong(m.get(SELL_CONTRACT_ID) + "");
                }
                if (m.get(COMPANY_ID) != null) {
                    companyId = Long.parseLong(m.get(COMPANY_ID) + "");
                }
            } else {
                if (ReflectUtils.isExistsProperty(bizEntity.getClass(), CONTRACT_ID)) {
                    Method getMethod = bizEntity.getClass().getMethod(GET_CONTRACT_ID);
                    contractId = (Long) getMethod.invoke(bizEntity);
                }
                if (contractId == null && ReflectUtils.isExistsProperty(bizEntity.getClass(), SELL_CONTRACT_ID)) {
                    Method getMethod = bizEntity.getClass().getMethod(GET_SELL_CONTRACT_ID);
                    contractId = (Long) getMethod.invoke(bizEntity);
                }
                if (ReflectUtils.isExistsProperty(bizEntity.getClass(), COMPANY_ID)) {
                    Method getMethod = bizEntity.getClass().getMethod(GET_COMPANY_ID);
                    companyId = (Long) getMethod.invoke(bizEntity);
                }
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        approve.setCompanyId(companyId);
        approve.setContractId(contractId);
        return approve;
    }

    /**
     * 生成-签署连带责任保证书审批步骤
     *
     * @param lstStep
     * @return
     */
    private void parseLiabilityStep(List<PmProcessStep> lstStep) {
        PmProcessStep liabilityStep = new PmProcessStep();
        PmProcessStep pmProcessStep = lstStep.stream().findFirst().orElse(null);
        if (Objects.isNull(pmProcessStep)) {
            return;
        }
        Long enterpriseId = pmProcessStep.getEnterpriseId();
        PmProcessNode liabilityNode = pmProcessNodeDao.findByNodeCodeAndEnterpriseId(PmConstants.PROCESS_NODE_LIABILITY_USER, enterpriseId);
        if (Objects.isNull(liabilityNode)) {
            return;
        }
        liabilityStep.setProcessId(pmProcessStep.getProcessId());
        liabilityStep.setConditionId(pmProcessStep.getConditionId());
        liabilityStep.setNodeId(liabilityNode.getId());
        liabilityStep.setStepName(liabilityNode.getNodeName());
        liabilityStep.setDispOrderNo(0L);
        liabilityStep.setBackFlg(true);
        liabilityStep.setEnableFlg(true);
        liabilityStep.setRepeatSkipFlg(false);
        liabilityStep.setEnterpriseId(pmProcessStep.getEnterpriseId());
        lstStep.add(0, liabilityStep);
    }

    /**
     * 验证必传参数
     *
     * @param startVo
     * @throws InvalidParamException
     */
    public void verifyApproveParam(PmApproveSaveVo startVo) throws InvalidParamException {
        if (startVo.getProcessId() == null || startVo.getProcessId() == 0) {
            throw new InvalidParamException("ProcessId");
        }
        if (startVo.getUserId() == null || startVo.getUserId() == 0) {
            throw new InvalidParamException("userId");
        }
        if (startVo.getBizEntityJson() == null) {
            throw new InvalidParamException("BizEntity");
        }
    }

    /**
     * 转换审批单提交Json表单数据
     *
     * @param startVo
     * @param process
     * @return
     */
    public IPmEntity parseIPmEntity(PmApproveSaveVo startVo, PmProcess process) {
        IPmEntity bizEntity = null;
        String entityName = process.getEntityName();
        if (StringUtils.isNotBlank(entityName)) {
            try {
                bizEntity = (IPmEntity) JsonUtil.json2Object(Class.forName(entityName), startVo.getBizEntityJson());
            } catch (Exception e) {
                logger.error("getBizEntity", e);
            }
        }
        // 企业嵌套id
        bizEntity.setEnterpriseId(startVo.getEnterpriseId());
        return bizEntity;
    }

    /**
     * 根据审批单提交内容获取审批流程
     *
     * @param startVo
     * @param bizEntity
     * @param process
     * @return
     * @throws ApplicationException
     */
    public PmProcessConditionStepVo getApproveSteps(PmApproveSaveVo startVo, IPmEntity bizEntity, PmProcess process,
                                                    Map<String, Object> conditionDefaultMap) throws ApplicationException {
        if (Objects.isNull(conditionDefaultMap) || conditionDefaultMap.isEmpty()) {
            conditionDefaultMap = new HashMap<>();
        }
        String processCode = process.getProcessCode();
        conditionDefaultMap.put("userId", startVo.getUserId());
        conditionDefaultMap.put("deptId", startVo.getDeptId());
        logger.info("conditionDefaultMap：{}", conditionDefaultMap);
        return parseService.getProcessStep(processCode, bizEntity, startVo.getEnterpriseId(), conditionDefaultMap);
    }


    /**
     * 生成审批发起申请流程
     *
     * @param startVo
     * @return
     */
    public PmApproveStep generateStartStep(PmApproveSaveVo startVo) {
        PmApproveStep stepStart = new PmApproveStep();
        stepStart.setApproveUserName(startVo.getUserName());
        stepStart.setApproveUserId(startVo.getUserId());
        stepStart.setStepName("发起申请");
        stepStart.setStepId(0L);
        stepStart.setNodeId(0L);
        stepStart.setApproveRemark(startVo.getAutoStartMessage());
        return stepStart;
    }

    /**
     * 生成自动发起并完成flowVo
     *
     * @param approve
     * @param startVo
     * @param step
     * @return
     */
    public PmApproveStepFlowVo generateStepFlowVo(PmApprove approve, PmApproveSaveVo startVo, PmApproveStep step) {
        PmApproveStepFlowVo flowVoNext = new PmApproveStepFlowVo();
        flowVoNext.setApproveId(approve.getId());
        flowVoNext.setApproveOpinion(PmConstants.APPROVE_OPINION_AGREE);
        flowVoNext.setApproveRemark("同意！");
        flowVoNext.setApproveStepId(step.getId());
        flowVoNext.setApproveUserId(startVo.getUserId());
        flowVoNext.setApproveUserName(Boolean.TRUE.equals(startVo.getAutoStartFlg()) ? "系统管理员" : startVo.getUserName());
        flowVoNext.setComplete(true);
        return flowVoNext;
    }

    /**
     * 生成自动同意flowVo
     *
     * @param approve
     * @param startVo
     * @param step
     * @return
     */
    public PmApproveStepFlowVo generateAutoAgreeStepFlowVo(PmApprove approve, PmApproveSaveVo startVo, PmApproveStep step) {
        PmApproveStepFlowVo flowVoNext = new PmApproveStepFlowVo();
        flowVoNext.setApproveId(approve.getId());
        flowVoNext.setApproveOpinion(PmConstants.APPROVE_OPINION_AGREE);
        flowVoNext.setApproveRemark("自动同意");
        flowVoNext.setApproveStepId(step.getId());
        flowVoNext.setApproveUserId(startVo.getUserId());
        flowVoNext.setApproveUserName(startVo.getUserName());
        return flowVoNext;
    }

    /**
     * 生成自动签审批Vo
     *
     * @param approve
     * @return
     */
    public PmApproveStepFlowVo generateAutoSignLimitVo(PmApprove approve) throws ApplicationException {
        if (Objects.isNull(approve.getAutoSignLimit())) {
            return null;
        }
        List<String> stepIdStr = Splitter.on(BasConstants.SEPARATE).omitEmptyStrings().splitToList(approve.getCurrApproveStepId());
        List<Long> stepIdList = stepIdStr.stream().filter(NumberUtil::isNumber).map(Long::valueOf).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(stepIdList)) {
            return null;
        }
        List<PmApproveStep> stepList = approveStepService.findStepByIds(stepIdList);
        PmApproveStep currStep = stepList.stream().filter(s-> Objects.nonNull(s.getAutoSignLimit())).min(Comparator.comparing(PmApproveStep::getAutoSignLimit)).orElse(null);
        if (Objects.nonNull(currStep)) {
            String userId = getNodeUserIds(currStep.getNodeId(), approve.getCreateUserId());
            userId = StringUtils.isNotBlank(userId) ? Splitter.on(BasConstants.SEPARATE).omitEmptyStrings().splitToList(userId).get(0) : "";
            PmApproveStepFlowVo flowVoNext = new PmApproveStepFlowVo();
            flowVoNext.setAutoSignFlg(true);
            flowVoNext.setApproveId(approve.getId());
            flowVoNext.setApproveOpinion(PmConstants.APPROVE_OPINION_AGREE);
            flowVoNext.setApproveRemark("同意。");
            flowVoNext.setApproveStepId(currStep.getId());
            flowVoNext.setApproveUserId(Long.valueOf(userId));
            SysUserSdk sysUserSdk = authOpenFacade.findUserById(Long.valueOf(userId));
            flowVoNext.setApproveUserName(Objects.nonNull(sysUserSdk) ? sysUserSdk.getNickName() : "");
            return flowVoNext;
        }
        return null;
    }

    /**
     * 添加代采赊销盖章操作历史日志
     *
     * @param startVo
     * @param bizEntity
     */
    public void addSealUsageHisLog(PmApproveSaveVo startVo, IPmEntity bizEntity) {
        if (bizEntity instanceof SealUsageDCSX) {
            sealUsageDcsxClient.addSealUsageUpdateHis(startVo);
        }
    }

    /**
     * 保存审批人重合系统自动跳过history记录
     *
     * @param approveSteps
     * @param approve
     */
    public void saveMergeStepHistory(List<PmApproveStep> approveSteps, PmApprove approve) {
        List<PmApproveStep> mergeStepList = approveSteps.stream().filter(s -> StringUtils.isNotBlank(s.getApproveOpinion())).collect(Collectors.toList());
        for (PmApproveStep mergeStep : mergeStepList) {
            historyService.addHistory(approve, mergeStep);
        }
    }

    /**
     * 获取审批节点审批人
     *
     * @param pmService
     * @param bizEntity
     * @param nodeId
     * @param createUserId
     * @return
     * @throws ApplicationException
     */
    public String getNodeUserIds(IPmService pmService, IPmEntity bizEntity, Long nodeId, Long createUserId) throws ApplicationException {
        Long matchUserId = null;
        String approveUserIds;
        if (Objects.nonNull(pmService) && Objects.nonNull(bizEntity)) {
            matchUserId = pmService.getMatchUserId(bizEntity);
        }
        if (matchUserId == null) {
            matchUserId = createUserId;
        }
        String nodeCode = PmNodeCache.getNodeCode(nodeId);
        if (PmConstants.PROCESS_NODE_START_USER.equals(nodeCode)){
            approveUserIds = String.valueOf(createUserId);
        } else if (PmConstants.PROCESS_NODE_BIZ_BUY_USER.equals(nodeCode)) {
            approveUserIds = String.valueOf(createUserId);
        } else if (PmConstants.PROCESS_NODE_LIABILITY_USER.equals(nodeCode)) {
            approveUserIds = PmConstants.SEPARATE + createUserId + PmConstants.SEPARATE;
        } else {
            approveUserIds = pmProcessNodeService.getNodeUserId(nodeId, matchUserId);
        }
        if (StringUtils.isNotBlank(approveUserIds) && !approveUserIds.startsWith(BasConstants.SEPARATE)){
            approveUserIds = BasConstants.SEPARATE + approveUserIds;
        }
        if (StringUtils.isNotBlank(approveUserIds) && !approveUserIds.endsWith(BasConstants.SEPARATE)){
            approveUserIds = approveUserIds + BasConstants.SEPARATE;
        }
        return approveUserIds;
    }

    /**
     * 获取审批节点审批人
     *
     * @param bizEntity
     * @param nodeId
     * @param createUserId
     * @return
     * @throws ApplicationException
     */
    public String getNodeUserIds(IPmEntity bizEntity, Long nodeId, Long createUserId) throws ApplicationException {
        return getNodeUserIds(null, bizEntity, nodeId, createUserId);
    }

    /**
     * 获取审批节点审批人
     *
     * @param nodeId
     * @param createUserId
     * @return
     * @throws ApplicationException
     */
    public String getNodeUserIds(Long nodeId, Long createUserId) throws ApplicationException {
        return getNodeUserIds(null, null, nodeId, createUserId);
    }

    /**
     * 设置审批单当前审批人，审批步骤ID
     *
     * @param approveSteps
     * @param pmService
     * @param bizEntity
     * @param approve
     * @throws ApplicationException
     */
    public PmApprove setCurrApproveStep(List<PmApproveStep> approveSteps, IPmService pmService, IPmEntity bizEntity, PmApprove approve) throws ApplicationException {
        PmApproveStep step = approveSteps.stream().filter(s -> StringUtils.isBlank(s.getApproveOpinion())).findFirst().orElse(null);
        List<PmApproveStep> currStepList = null;
        List<String> currStepIdList = new ArrayList<>();
        String approveUserIds = "";
        String currApproveStepId = "";
        Long autoSignLimit = step.getAutoSignLimit();
        String stepName = step.getStepName();
        if (Objects.nonNull(step) && StringUtils.isNotBlank(step.getStepGroup())) {
            currStepList = approveSteps.stream().filter(s -> StringUtils.isBlank(s.getApproveOpinion()) &&
                            Objects.equals(step.getDispOrderNo(), s.getDispOrderNo()) &&
                            StringUtils.equals(step.getStepGroup(), s.getStepGroup())).
                    collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(currStepList)) {
            for (PmApproveStep currStep : currStepList) {
                Long limit = currStep.getAutoSignLimit();
                approveUserIds = approveUserIds + this.getNodeUserIds(pmService, bizEntity, currStep.getNodeId(), approve.getCreateUserId());
                currStepIdList.add(currStep.getId().toString());
                autoSignLimit = (Objects.isNull(limit) || Objects.equals(0L, limit)) ? autoSignLimit : limit;
            }
            stepName = BsDictUtil.getValue(approve.getEnterpriseId(), BasConstants.DictType.DICT_STEP_GROUP_TYPE, step.getStepGroup());
            currApproveStepId = currStepIdList.stream().collect(Collectors.joining("|"));
        } else {
            approveUserIds = this.getNodeUserIds(pmService, bizEntity, step.getNodeId(), approve.getCreateUserId());
            currApproveStepId = step.getId().toString();
        }
        approveUserIds = approveUserIds.replace("||", BasConstants.SEPARATE);
        approve.setCurrApproverUserId(approveUserIds);
        approve.setCurrApproveStepId(currApproveStepId);
        approve.setCurrStepName(stepName);
        approve.setAutoSignLimit(autoSignLimit);
        return approve;
    }

    /**
     * 设置下一个审批人，审批步骤ID
     *
     * @param approveSteps
     * @param approve
     * @throws ApplicationException
     */
    public PmApprove setNextApproveStep(List<PmApproveStep> approveSteps, PmApprove approve) throws ApplicationException {
        PmApproveStep step = approveSteps.stream().filter(s -> StringUtils.isBlank(s.getApproveOpinion())).findFirst().orElse(null);
        if (Objects.isNull(step)){
            return approve;
        }
        List<String> currStepIdList = new ArrayList<>();
        String approveUserIds = "";
        String currApproveStepId = "";
        String stepName = step.getStepName();
        Long autoSignLimit = step.getAutoSignLimit();
        for (PmApproveStep currStep : approveSteps) {
            Long limit = currStep.getAutoSignLimit();
            approveUserIds = approveUserIds + this.getNodeUserIds(currStep.getNodeId(), approve.getCreateUserId());
            currStepIdList.add(currStep.getId().toString());
            autoSignLimit = (Objects.isNull(limit) || Objects.equals(0L, limit)) ? autoSignLimit : limit;
        }
        if (StringUtils.isNotBlank(step.getStepGroup())){
            stepName = BsDictUtil.getValue(approve.getEnterpriseId(), BasConstants.DictType.DICT_STEP_GROUP_TYPE, step.getStepGroup());
        }
        currApproveStepId = currStepIdList.stream().collect(Collectors.joining("|"));
        approveUserIds = approveUserIds.replace("||", BasConstants.SEPARATE);
        approve.setCurrApproverUserId(approveUserIds);
        approve.setCurrApproveStepId(currApproveStepId);
        approve.setCurrStepName(stepName);
        approve.setAutoSignLimit(autoSignLimit);
        return approve;
    }

    /**
     * 获取当前审批人所在审批步骤
     *
     * @param flowVo
     * @param approve
     * @return
     */
    public Long getCurrStep(PmApproveStepFlowVo flowVo, PmApprove approve) throws ApplicationException {
        String currApproverUserId = approve.getCurrApproverUserId();
        String currApproveStepId = approve.getCurrApproveStepId();
        Long approveUserId = flowVo.getApproveUserId();
        if (StringUtils.isBlank(currApproveStepId) || StringUtils.isBlank(currApproverUserId) || !currApproverUserId.contains(approveUserId.toString())) {
            return null;
        }
        List<String> stepIdStr = Splitter.on(BasConstants.SEPARATE).omitEmptyStrings().trimResults().splitToList(currApproveStepId);
        List<Long> stepIdList = stepIdStr.stream().map(Long::valueOf).collect(Collectors.toList());
        List<PmApproveStep> pmApproveSteps = approveStepService.findByApproveId(approve.getId());
        List<PmApproveStep> currStepList = pmApproveSteps.stream().
                filter(s -> StringUtils.isBlank(s.getApproveOpinion())).
                filter(s -> stepIdList.contains(s.getId())).
                collect(Collectors.toList());
        for (PmApproveStep pmApproveStep : currStepList) {
            String currUserId = getNodeUserIds(pmApproveStep.getNodeId(), approve.getCreateUserId());
            if (StringUtils.equals(currUserId, String.valueOf(approveUserId)) || currUserId.contains(String.valueOf(approveUserId))) {
                return pmApproveStep.getId();
            }
        }
        return CollectionUtils.isNotEmpty(currStepList) ? currStepList.get(0).getId() : null;
    }
}
