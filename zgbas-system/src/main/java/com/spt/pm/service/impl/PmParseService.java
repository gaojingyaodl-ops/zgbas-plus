/**
 * 
 */
package com.spt.pm.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.dao.PmProcessConditionDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.dao.PmProcessStepDao;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.entity.PmProcessCondition;
import com.spt.pm.entity.PmProcessStep;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.service.IPmParseService;
import com.spt.pm.service.IPmProcessAutoStepService;
import com.spt.pm.util.ResConditionParser;
import com.spt.pm.vo.PmProcessConditionStepVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wltea.expression.ExpressionToken;
import org.wltea.expression.datameta.Variable;

import java.util.*;

/**
 * @author wlddh
 *
 */
@Component
public class PmParseService implements IPmParseService {
	@Autowired
	private PmProcessConditionDao conditionDao;
	@Autowired
	private PmProcessStepDao processStepDao;
	@Autowired
	private PmProcessDao processDao;
	@Autowired
	private IPmProcessAutoStepService processAutoStepService;
	// [TAPD ID1001454]  代采赊销付款/业务付款申请，如果配置时限自动签署，必须判断从余额扣款才能自动签署
	private static final List<String> AUTO_SIGN_SPECIAL_PROCESS = new ArrayList<String>(2){{
		add(BasConstants.PROCESS_CODE_PAY);
		add(BasConstants.PROCESS_CODE_DCSX_PAY);
	}};
	private static final String SPECIAL_CONDITION = "payMode != \"F\"";
	private static final String SPECIAL_CONDITION2 = "ourCompanyName in {苏州高新供应链管理有限公司} && companyName in {青岛中光亿云供应链管理有限公司}";
	private Logger logger = LoggerFactory.getLogger(PmParseService.class);

	/**
	 * 根据表单内容取得审批的路径
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public PmProcessConditionStepVo getProcessStep(String processCode, IPmEntity entity, Long enterpriseId, Map<String, Object> mapDefault){
		PmProcessConditionStepVo vo = new PmProcessConditionStepVo();
		PmProcess process = processDao.findByProcessCodeAndEnterpriseId(processCode, enterpriseId);
		if (Objects.nonNull(process)) {
			List<PmProcessCondition> lstCondition = conditionDao.findAllEnable(process.getId());
			if (lstCondition.size() == 1) {
				PmProcessCondition conditionType = lstCondition.get(0);
				List<PmProcessStep> stepsTmp = processStepDao.findByConditionId(conditionType.getId());
				vo.setSteps(stepsTmp);
				vo.setConditionId(conditionType.getId());
			} else {
				PmProcessCondition condition = null;
				for (PmProcessCondition conditionType : lstCondition) {
					String conditionValue = conditionType.getConditionValue();
					if (PmConstants.CONDITION_DEFAULT.equals(conditionValue) || StringUtils.isBlank(conditionValue)) {
						condition = conditionType;
						break;
					}
					if (validCondition(conditionValue, entity, mapDefault, process)) {
						condition = conditionType;
						break;
					}
				}
				if (condition != null) {
					List<PmProcessStep> stepsTmp = processStepDao.findByConditionId(condition.getId());
					vo.setSteps(stepsTmp);
					vo.setConditionId(condition.getId());
				}
			}
			vo.setMapDefault(mapDefault);
			vo.setProcessId(process.getId());
			vo.setPmEntity(entity);
			vo = processAutoStepService.dealWithAutoStepList(vo);
			dealWithSpecialAutoSign(vo, entity, mapDefault, process);
		} else {
			logger.error("process is null:{}", processCode);
		}
		return vo;
	}

	/**
	 * 代采赊销付款/业务付款申请，如果配置时限自动签署，必须判断从余额扣款才能自动签署
	 * @param vo
	 * @param entity
	 * @param mapDefault
	 * @param process
	 */
	private void dealWithSpecialAutoSign(PmProcessConditionStepVo vo, IPmEntity entity, Map<String, Object> mapDefault, PmProcess process) {
		if (validCondition(SPECIAL_CONDITION2, entity, mapDefault, process)) {
			vo.setSpecialAutoSignFlg(true);
		} else if (AUTO_SIGN_SPECIAL_PROCESS.contains(process.getProcessCode()) && validCondition(SPECIAL_CONDITION, entity, mapDefault, process)) {
			vo.setSpecialAutoSignFlg(false);
		}
	}

	private boolean validCondition(String conditionValue, IPmEntity entity, Map<String, Object> mapDefault, PmProcess process) {
		Map<String, Object> param = new HashMap<>();
		List<ExpressionToken> lstToken = ResConditionParser.getVars(conditionValue);
		if (CollectionUtils.isEmpty(lstToken)){
			return false;
		}
		lstToken.forEach(t -> {
			Variable var = t.getVariable();
			Object varVal = ResConditionParser.getVarValue(var.getVariableName(), entity, mapDefault, process);
			param.put(var.getVariableName(), varVal);
		});
		return ResConditionParser.validCondition(conditionValue, param);
	}
}
