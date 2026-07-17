package com.spt.pm.service.impl;

import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.dao.PmProcessConditionDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.dao.PmProcessNodeDao;
import com.spt.pm.dao.PmProcessStepDao;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.entity.PmProcessCondition;
import com.spt.pm.entity.PmProcessNode;
import com.spt.pm.entity.PmProcessStep;
import com.spt.pm.service.IPmProcessNodeService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Transactional(readOnly = true)
public class PmProcessServiceImpl extends BaseService<PmProcess> implements IPmProcessService {
    @Autowired
    private PmProcessDao pmProcessDao;
    @Autowired
    private PmProcessConditionDao conditionDao;
    @Autowired
    private IPmProcessNodeService processNodeService;
    @Autowired
    private PmProcessNodeDao pmProcessNodeDao;
    @Autowired
    private PmProcessStepDao processStepDao;


    @Override
    public BaseDao<PmProcess> getBaseDao() {
        return pmProcessDao;
    }

    @Override
    public Class<PmProcess> getEntityClazz() {
        return PmProcess.class;
    }


    @Override
    public List<PmProcess> findAccess(PmProcessSearchVo searchVo) {
        if (StringUtils.isNotBlank(searchVo.getProcessGroup())) {
            return pmProcessDao.findAccess(searchVo.getEnterpriseId(), searchVo.getUserId(), searchVo.getProcessGroup());
        }
        return pmProcessDao.findAccess(searchVo.getEnterpriseId(), searchVo.getUserId());
    }

    @Override
    public PmProcess findByProcessCode(PmProcessSearchVo searchVo) {
        return pmProcessDao.findByProcessCodeAndEnterpriseId(searchVo.getProcessCode(), searchVo.getEnterpriseId());
    }

    @Override
    protected Sort getDefaultSort() {
        Order o1 = new Order(Direction.ASC, "dispOrderNo");
        Order o2 = new Order(Direction.DESC, "id");

        Sort sort = Sort.by(o1, o2);
        return sort;
    }

    @Override
    @ServerTransactional
    public void delete(Long id) throws ApplicationException {
        conditionDao.deleteByProcessId(id);
        super.delete(id);
    }

    @Override
    public List<PmProcess> findByEnterpriseId(PmProcessSearchVo searchVo) {
        Long enterpriseId = searchVo.getEnterpriseId();

        if (Objects.isNull(searchVo.getViewFlg())) {
            return pmProcessDao.findByEnterpriseIdAndEnableFlgTrue(enterpriseId);
        }
        return pmProcessDao.findByEnterpriseIdAndEnableFlgTrueAndViewFlg(enterpriseId, searchVo.getViewFlg());
    }

    @Override
    @ServerTransactional
    public void initProcess(Long enterpriseId) {
        String sql = "{call up_init_process(?)}";
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(0, String.valueOf(enterpriseId));
        boolean processFlag = commonDao.executeStoreProcedure(sql, map);

        //初始化流程节点
        boolean nodeFlag = processNodeService.initProcessNode(enterpriseId);

        //分别给每个流程添加流程条件和流程步骤
        if (processFlag && nodeFlag) {
            saveProcessConditionAndStep(enterpriseId);
        }
    }

    private void saveProcessConditionAndStep(Long enterpriseId) {
        List<PmProcess> processList = pmProcessDao.findByEnterpriseIdAndEnableFlgTrue(enterpriseId);
        for (PmProcess process : processList) {
            Long processId = process.getId();

            PmProcessCondition condition = new PmProcessCondition();
            condition.setConditionName("DEFAULT");
            condition.setDispOrderNo(1L);
            condition.setEnableFlg(true);
            condition.setEnterpriseId(enterpriseId);
            condition.setProcessId(processId);
            condition = conditionDao.save(condition);

            //添加流程步骤
            Long conditionId = condition.getId();
            PmProcessStep step = new PmProcessStep();
            step.setProcessId(processId);
            step.setConditionId(conditionId);
            //默认添加总经理审批节点
            PmProcessNode processNode = pmProcessNodeDao.findByNodeCodeAndEnterpriseId("bs_manager", enterpriseId);
            step.setNodeId(processNode.getId());
            step.setStepName(processNode.getNodeName());
            step.setDispOrderNo(1L);
            step.setBackFlg(true);
            step.setEnableFlg(true);
            step.setEnterpriseId(enterpriseId);
            processStepDao.save(step);
        }
    }

    @Override
    public Long findStartUserByProcess(PmProcess process) throws ApplicationException {
        Long userId = 0L;
        if (process != null) {
            List<PmProcessCondition> lstCondition = conditionDao.findAllEnable(process.getId());
            if (lstCondition.size() == 1) {
                PmProcessCondition conditonType = lstCondition.get(0);
                List<PmProcessStep> stepsTmp = processStepDao.findByConditionId(conditonType.getId());
                if (stepsTmp.size() == 1) {
                    PmProcessStep step = stepsTmp.get(0);
                    String nodeUserId = processNodeService.getNodeUserId(step.getNodeId(), null);
                    String[] userIds = nodeUserId.split("\\|");
                    userId = Long.valueOf(userIds[1]);
                }
            }
        }
        return userId;
    }

    @Override
    public String initPmProcessList() {
        return pmProcessDao.initPmProcessList();
    }

    @Override
    public List<PmProcess> findByEnterpriseIdAndEnableFlgTrue(Long enterpriseId) {
        return pmProcessDao.findByEnterpriseIdAndEnableFlgTrue(enterpriseId);
    }
}

