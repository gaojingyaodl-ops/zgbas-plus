package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyBusinessRestrictRelieve;
import com.spt.bas.client.entity.BusinessRestrictRelieve;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.ApplyBusinessRestrictRelieveDao;
import com.spt.bas.server.dao.BusinessRestrictRelieveDao;
import com.spt.bas.server.service.IApplyBusinessRestrictRelieveService;
import com.spt.bas.server.service.IBusinessRestrictRelieveService;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Component
@Transactional(readOnly = true)
@Slf4j
public class BusinessRestrictRelieveServiceImpl extends BaseService<BusinessRestrictRelieve> implements IBusinessRestrictRelieveService {
    @Autowired
    private BusinessRestrictRelieveDao businessRestrictRelieveDao;

    @Override
    public BaseDao<BusinessRestrictRelieve> getBaseDao() {
        return businessRestrictRelieveDao;
    }

    @Override
    public Class<BusinessRestrictRelieve> getEntityClazz() {
        return BusinessRestrictRelieve.class;
    }


    @Override
    public void updateUsableCount(Long id, Integer usableCount) {
        businessRestrictRelieveDao.updateUsableCount(id, usableCount);
    }

    @Override
    public BusinessRestrictRelieve findByCompanyIdAndAndUserId(Long companyId, Long userId) {
        return businessRestrictRelieveDao.findByCompanyIdAndAndUserId(companyId, userId);
    }

    /**
     * 重置可用次数
     */
    @Override
    @ServerTransactional
    public void resetUsableCount() {
        businessRestrictRelieveDao.resetUsableCount();
    }
}
