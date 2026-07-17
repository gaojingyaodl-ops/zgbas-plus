package com.spt.bas.server.ctr.service.impl;


import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrServiceContract;
import com.spt.bas.server.ctr.service.ICtrServiceContractService;
import com.spt.bas.server.dao.CtrServiceContractDao;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.service.ICtrContractTextService;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Transactional(readOnly = true)
public class CtrServiceContractServiceImpl extends BaseService<CtrServiceContract> implements ICtrServiceContractService {
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    @Autowired
    private CtrServiceContractDao ctrServiceContractDao;
    @Autowired
    private IBsKeySequenceService bsKeySequenceService;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private ICtrContractTextService ctrContractTextService;
    @Autowired
    private ICtrContractService ctrContractService;

    @Override
    public BaseDao<CtrServiceContract> getBaseDao() {
        return ctrServiceContractDao;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        ctrServiceContractDao.updateFileId(id,fileId);
    }

    @Override
    @ServerTransactional
    public CtrServiceContract addServiceContract(CtrContract sellContract, ApplyMatchDetail detail) throws ApplicationException {
        CtrServiceContract entity = new CtrServiceContract();
        String serviceType = detail.getServiceType();
        if (!StringUtils.equals(BasConstants.SERVICE_TYPE_N,serviceType)){
            String nextKey = bsKeySequenceService.getNextKey(BasConstants.KEY_SERVICE_NO, sellContract.getEnterpriseId());
            BsCompany company = bsCompanyService.getEntity(sellContract.getCompanyId());
            entity.setCtrContractId(sellContract.getId());
            entity.setServiceContractNo(nextKey);
            entity.setMatchUserId(sellContract.getMatchUserId());
            entity.setMatchUserName(sellContract.getMatchUserName());
            entity.setDeptId(sellContract.getDeptId());
//            BigDecimal serviceAmount = sellContract.getTotalAmount().multiply(company.getRate()).multiply(BigDecimal.valueOf(company.getCreditCycle()));
            BigDecimal serviceAmount = detail.getServiceAmount();
            entity.setTotalAmount(serviceAmount);
            entity.setRate(company.getRate());
            entity.setInterestAmount(BigDecimal.ZERO);
            entity.setReceiveInterestAmount(BigDecimal.ZERO);
            entity.setInterestRate(company.getInterestRate());
            entity.setContractStartTime(sellContract.getContractTime());
            entity.setContractEndTime(sellContract.getContractEndTime());
//            entity.setCreditCycle(company.getCreditCycle());
            entity.setCreditCycle(detail.getCreditDays());
            entity.setRateType(company.getRateType());
            entity.setPayType(sellContract.getPayType());
            entity.setCompanyId(sellContract.getCompanyId());
            entity.setCompanyName(sellContract.getCompanyName());
            entity.setOurCompanyName(detail.getServiceOurCompanyName());
            entity.setStatus(sellContract.getStatus());
            entity.setCtrTemplateFileId(detail.getServiceContentTemplateId());
            entity.setBsTemplateContractId(detail.getServiceTemplateId());
            entity.setEnterpriseId(detail.getEnterpriseId());
            entity.setFileId(detail.getServiceContentTemplateId());
            entity.setApproveId(sellContract.getApproveId());
            entity.setBilledAmount(BigDecimal.ZERO);
            entity.setDealedAmount(BigDecimal.ZERO);
            if (StringUtils.isNotBlank(detail.getServiceContentTemplateId())){
                entity.setContractTemplate("U");
            }
            entity = this.save(entity);
        }
        sellContract.setServiceAmount(entity.getTotalAmount());
        sellContract.setServiceContractId(entity.getId());
        ctrContractService.save(sellContract);
        //生成服务电子合同
        saveServiceText(entity);
        return entity;
    }

    private void saveServiceText(CtrServiceContract serviceContract){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ctrContractTextService.saveServiceText(serviceContract);
                } catch (ApplicationException e) {
                    logger.error("生成服务电子合同异常", e);
                }
            }
        });
    }

}
