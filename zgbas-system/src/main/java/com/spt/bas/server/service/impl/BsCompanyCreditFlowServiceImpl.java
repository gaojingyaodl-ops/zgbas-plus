package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.CreditFlowEnum;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.bas.client.entity.BsCompanyCreditFlow;
import com.spt.bas.server.dao.BsCompanyCreditDao;
import com.spt.bas.server.dao.BsCompanyCreditFlowDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.IBsCompanyCreditFlowService;
import com.spt.bas.server.service.IBsCompanyCreditService;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

@Component
@Transactional(readOnly = true)
public class BsCompanyCreditFlowServiceImpl extends BaseService<BsCompanyCreditFlow> implements IBsCompanyCreditFlowService {
    private final ConcurrentHashMap<Long, Object> locks = new ConcurrentHashMap<>();
    @Resource
    private BsCompanyCreditDao companyCreditDao;
    @Resource
    private IBsCompanyCreditService companyCreditService;
    @Resource
    private BsCompanyCreditFlowDao bsCompanyCreditFlowDao;
    @Resource
    private BsCompanyDao bsCompanyDao;

    @Override
    public BaseDao<BsCompanyCreditFlow> getBaseDao() {
        return bsCompanyCreditFlowDao;
    }

    @Override
    public Class<BsCompanyCreditFlow> getEntityClazz() {
        return BsCompanyCreditFlow.class;
    }

    /**
     * 更新客户审批占用额度
     *
     * @param approveNo  审批单编号
     * @param company    客户
     * @param flowAmount 变动金额
     * @param flowEnum   流水类型
     */
    @Override
    @ServerTransactional
    public void updateApproveCreditAmount(String approveNo, BsCompany company, BigDecimal flowAmount, CreditFlowEnum flowEnum) {
        BsCompanyCreditFlow flow = new BsCompanyCreditFlow();
        flow.setCompanyId(company.getId());
        flow.setCompanyName(company.getCompanyName());
        flow.setFlowType(flowEnum.getFlowCode());
        flow.setFlowName(flowEnum.getFlowName());
        flow.setApproveNo(approveNo);

        flow.setBeforeUsedCreditAmount(company.getUsedCreditAmount());
        flow.setAfterUsedCreditAmount(company.getUsedCreditAmount());
        flow.setUsedFlowAmount(BigDecimal.ZERO);

        flow.setBeforeApproveCreditAmount(company.getApproveCreditAmount());
        flow.setAfterApproveCreditAmount(company.getApproveCreditAmount().add(flowAmount));
        flow.setApproveFlowAmount(flowAmount);

        bsCompanyCreditFlowDao.save(flow);
        bsCompanyDao.updateApproveCreditAmount(flow.getCompanyId(), flow.getAfterApproveCreditAmount());
    }

    /**
     * 更新客户已使用额度
     *
     * @param approveNo  审批单编号
     * @param company    客户
     * @param flowAmount 变动金额
     * @param flowEnum   流水类型
     */
    @Override
    @ServerTransactional
    public void updateUsedCreditAmount(String approveNo, BsCompany company, BigDecimal flowAmount, CreditFlowEnum flowEnum) {
        BsCompanyCreditFlow flow = new BsCompanyCreditFlow();
        flow.setCompanyId(company.getId());
        flow.setCompanyName(company.getCompanyName());
        flow.setFlowType(flowEnum.getFlowCode());
        flow.setFlowName(flowEnum.getFlowName());
        flow.setApproveNo(approveNo);

        flow.setBeforeUsedCreditAmount(company.getUsedCreditAmount());
        flow.setAfterUsedCreditAmount(company.getUsedCreditAmount().add(flowAmount));
        flow.setUsedFlowAmount(flowAmount);

        flow.setBeforeApproveCreditAmount(company.getApproveCreditAmount());
        flow.setAfterApproveCreditAmount(company.getApproveCreditAmount());
        flow.setApproveFlowAmount(BigDecimal.ZERO);

        bsCompanyCreditFlowDao.save(flow);
        bsCompanyDao.updateUsedCreditAmount(flow.getCompanyId(), flow.getAfterUsedCreditAmount());
    }

    /**
     * 更新客户已使用额度
     *
     * @param approve         审批单
     * @param companyCreditId 授信额度ID
     * @param flowAmount      变动金额
     * @param flowEnum        流水类型
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateUsedCreditAmount(PmApprove approve, Long companyCreditId, BigDecimal flowAmount, CreditFlowEnum flowEnum) {
        BsCompanyCredit companyCredit = companyCreditDao.findEntityForUpdate(companyCreditId);
        if (Objects.isNull(companyCredit)) {
            return;
        }
        logger.info("-----已使用额度:{}, 流水金额：{}", companyCredit.getUsedCreditAmount(), flowAmount);
        BsCompany company = bsCompanyDao.findOne(companyCredit.getCompanyId());
        BsCompanyCreditFlow flow = new BsCompanyCreditFlow();
        flow.setApproveId(approve.getId());
        flow.setApproveNo(approve.getApproveNo());
        flow.setCompanyId(companyCredit.getCompanyId());
        flow.setCompanyCreditId(companyCredit.getId());
        flow.setCompanyName(company.getCompanyName());
        flow.setFlowType(flowEnum.getFlowCode());
        flow.setFlowName(flowEnum.getFlowName());
        flow.setCreditType(companyCredit.getCreditType());

        flow.setBeforeUsedCreditAmount(companyCredit.getUsedCreditAmount());
        flow.setAfterUsedCreditAmount(companyCredit.getUsedCreditAmount().add(flowAmount));
        flow.setUsedFlowAmount(flowAmount);
        bsCompanyCreditFlowDao.save(flow);
        companyCreditService.updateUsedCreditAmount(companyCredit.getId(), flow.getAfterUsedCreditAmount());
    }
}

