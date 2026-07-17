package com.spt.pm.service;

import com.spt.pm.entity.PmProcessAutoStep;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.vo.PmProcessConditionStepVo;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * 额外条件节点配置
 * @Author: gaojy
 * @create 2022/2/8 15:21
 * @version: 1.0
 * @description:
 */
public interface IPmProcessAutoStepService extends IBaseService<PmProcessAutoStep> {
    /**
     * 查询符合条件的动态条件
     * @return
     */
    List<PmProcessAutoStep> getMeetTheCriteriaAutoStep(IPmEntity pmEntity, Long processId, Long conditionId, Map<String, Object> mapDefault);

    /**
     * 处理动态条件节点
     */
    PmProcessConditionStepVo dealWithAutoStepList(PmProcessConditionStepVo conditionStepVo);
}
