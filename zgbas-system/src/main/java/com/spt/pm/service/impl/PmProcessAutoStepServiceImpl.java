package com.spt.pm.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.dao.PmProcessAutoStepDao;
import com.spt.pm.dao.PmProcessNodeDao;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.entity.PmProcessAutoStep;
import com.spt.pm.entity.PmProcessStep;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.service.IPmProcessAutoStepService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.util.ResConditionParser;
import com.spt.pm.vo.PmProcessConditionStepVo;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.wltea.expression.ExpressionToken;
import org.wltea.expression.datameta.Variable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 额外条件节点配置
 *
 * @Author: gaojy
 * @create 2022/2/8 15:25
 * @version: 1.0
 * @description:
 */
@Component
public class PmProcessAutoStepServiceImpl extends BaseService<PmProcessAutoStep> implements IPmProcessAutoStepService {
    @Autowired
    private PmProcessAutoStepDao pmProcessAutoStepDao;
    @Autowired
    private PmProcessNodeDao processNodeDao;
    @Autowired
    private IPmProcessService pmProcessService;

    @Override
    public BaseDao<PmProcessAutoStep> getBaseDao() {
        return pmProcessAutoStepDao;
    }

    @Override
    public Sort getDefaultSort() {
        return Sort.by(Sort.Direction.ASC, "dispOrderNo");
    }

    /**
     * 查询符合条件的动态条件
     *
     * @return 初步过滤出符合条件的动态条件配置
     */
    @Override
    public List<PmProcessAutoStep> getMeetTheCriteriaAutoStep(IPmEntity pmEntity, Long processId, Long contractId, Map<String, Object> mapDefault) {
        List<PmProcessAutoStep> meetTheCriteriaAutoStepList = new ArrayList<>();
        //根据流程ID、条件ID获取所有有效动态条件节点
        List<PmProcessAutoStep> autoStepList = pmProcessAutoStepDao.findProcessAutoStepList(processId, contractId);
        PmProcess pmProcess = pmProcessService.getEntity(processId);
        if (CollectionUtils.isNotEmpty(autoStepList)) {
            try {
                //验证条件表达式
                autoStepList.forEach(autoStep -> {
                    String conditionValue = autoStep.getConditionValue();
                    // 额外条件表达式为空 则代表匹配所有条件
                    if (StringUtils.isBlank(conditionValue) || StringUtils.equalsIgnoreCase("DEFAULT",conditionValue)) {
                        meetTheCriteriaAutoStepList.add(autoStep);
                    } else {
                        List<ExpressionToken> expressionTokenList = ResConditionParser.getVars(conditionValue);
                        Map<String, Object> param = new HashMap<>();
                        expressionTokenList.forEach(t -> {
                            Variable var = t.getVariable();
                            Object varVal = ResConditionParser.getVarValue(var.getVariableName(), pmEntity, mapDefault, pmProcess);
                            param.put(var.getVariableName(), varVal);
                        });
                        if (ResConditionParser.validCondition(conditionValue, param)) {
                            meetTheCriteriaAutoStepList.add(autoStep);
                        }
                    }
                });
            } catch (Exception e) {
                logger.error("getMeetTheCriteriaAutoStep error", e);
            }
        }
        return meetTheCriteriaAutoStepList;
    }

    /**
     * 处理额外条件节点逻辑
     * <p>
     * 条件节点就是根据配置的条件，自动往流程里面添加/删除符合条件的步骤
     * conditionType为A 添加
     * 判断“条件id、参照节点、其他条件”是否符合，如果符合，把“节点、节点负责人”添加到“偏移量”指定的位置
     * conditionType为S 删除
     * 判断“条件id、参照节点、其他条件”是否符合，如果符合，把对应的“节点”删除掉
     * <p>
     * 判断逻辑：
     * 条件id如果为空，代表匹配所有条件记录
     * 节点审批人如果没有配置，则根据节点逻辑取审批人，如果节点需要用户填写审批人，则以用户传入的值为准
     * <p>
     * 流程合并逻辑：
     * 条件节点必须自动去除重复节点，相同名称的节点，保留步骤级别较大的节点
     * 如果自动添加的条件节点在固定步骤节点中已经存在，就不要再添加
     */
    @Override
    public PmProcessConditionStepVo dealWithAutoStepList(PmProcessConditionStepVo vo) {
        List<PmProcessStep> steps = vo.getSteps();
        if (CollectionUtils.isEmpty(steps)) {
            return vo;
        }

        List<Long> stepNodeIdList = steps.stream().map(PmProcessStep::getNodeId).collect(Collectors.toList());
        List<PmProcessAutoStep> autoSteps = getMeetTheCriteriaAutoStep(vo.getPmEntity(), vo.getProcessId(), vo.getConditionId(), vo.getMapDefault());
        autoSteps = autoSteps.stream().sorted(Comparator.comparing(PmProcessAutoStep::getConditionType)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(autoSteps)) {
            for (PmProcessAutoStep autoStep : autoSteps) {
                Long referNodeId = autoStep.getReferNodeId();
                if (StringUtils.equals(BasConstants.CONDITION_TYPE_A, autoStep.getConditionType())) {
                    //添加动态条件节点
                    PmProcessStep processStep;
                    if (Objects.isNull(referNodeId)) {
                        processStep = steps.stream().max(Comparator.comparing(PmProcessStep::getDispOrderNo)).orElse(null);
                    } else {
                        processStep = steps.stream().filter(s -> referNodeId.equals(s.getNodeId())).findFirst().orElse(null);
                    }

                    if (Objects.nonNull(processStep) && !stepNodeIdList.contains(autoStep.getAutoNodeId())) {
                        PmProcessStep pmProcessStep = parseProcessStepVo(processStep, autoStep);
                        steps.add(pmProcessStep);
                    }
                } else if (Objects.nonNull(referNodeId)){
                    //移除动态条件节点
                    steps.removeIf(pmProcessStepVo -> referNodeId.equals(pmProcessStepVo.getNodeId()));
                }
            }
            //去除重复节点
            //steps = steps.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PmProcessStep::getNodeId))), ArrayList::new));
        }
        steps = steps.stream().sorted(Comparator.comparing(PmProcessStep::getDispOrderNo)).collect(Collectors.toList());
        vo.setSteps(steps);
        return vo;
    }

    private PmProcessStep parseProcessStepVo(PmProcessStep processStep, PmProcessAutoStep processAutoStep) {
        PmProcessStep step = new PmProcessStep();
        step.setId(processStep.getId() + processAutoStep.getId());
        step.setProcessId(processStep.getProcessId());
        step.setEnterpriseId(processStep.getEnterpriseId());
        step.setConditionId(processStep.getConditionId());
        step.setBackFlg(processStep.getBackFlg());
        step.setNodeId(processAutoStep.getAutoNodeId());
        step.setEnableFlg(true);
        step.setStepName(processNodeDao.findOne(processAutoStep.getAutoNodeId()).getNodeName());
        step.setDispOrderNo(processStep.getDispOrderNo() + processAutoStep.getAutoOffSet());
        step.setRepeatSkipFlg(processStep.getRepeatSkipFlg());
        step.setAutoSignLimit(processAutoStep.getAutoSignLimit());
        if (Objects.equals(0L, processAutoStep.getAutoOffSet()) && StringUtils.isNotBlank(processStep.getStepGroup())){
            step.setStepGroup(processStep.getStepGroup());
            step.setDispOrderNo(processStep.getDispOrderNo());
        }
        return step;
    }
}
