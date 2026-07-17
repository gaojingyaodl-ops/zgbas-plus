package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.server.dao.BsCompanyCreditDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.service.IBsCompanyCreditService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class BsCompanyCreditServiceImpl extends BaseService<BsCompanyCredit> implements IBsCompanyCreditService {
    @Autowired
    private BsCompanyCreditDao bsCompanyCreditDao;
    @Autowired
    private IBsCompanyService companyService;
    @Resource
    private CtrContractDao ctrContractDao;

    @Override
    public BaseDao<BsCompanyCredit> getBaseDao() {
        return bsCompanyCreditDao;
    }

    /**
     * 根据企业ID、授信类型、有效状态 查询唯一授信额度数据
     *
     * @param companyId
     * @param creditType
     * @param enableFlg
     * @return
     */
    @Override
    public BsCompanyCredit findByCompanyIdAndCreditTypeAndEnableFlg(Long companyId, String creditType, Boolean enableFlg) {
        return bsCompanyCreditDao.findByCompanyIdAndCreditTypeAndEnableFlg(companyId, creditType, enableFlg);
    }

    /**
     * 根据临时额度失效日查询 授信额度信息
     *
     * @param temporaryExpiryDate
     * @return
     */
    @Override
    public List<BsCompanyCredit> findByTemporaryExpiryDateBefore(Date temporaryExpiryDate) {
        return bsCompanyCreditDao.findByTemporaryExpiryDateBefore(temporaryExpiryDate);
    }

    /**
     * 临时额度到期自动恢复
     */
    @Override
    @ServiceTransactional
    public void recoverTemporaryAmount() {
        List<BsCompanyCredit> companyCreditList = bsCompanyCreditDao.findByTemporaryExpiryDateBefore(new Date());
        if (CollectionUtils.isNotEmpty(companyCreditList)) {
            for (BsCompanyCredit companyCredit : companyCreditList) {
                companyCredit.setTemporaryAmount(BigDecimal.ZERO);
                companyCredit.setTemporaryExpiryDate(null);
                bsCompanyCreditDao.recoverTemporaryAmount(companyCredit.getId(), BigDecimal.ZERO, null);
            }
        }
    }

    /**
     * 初始化企业授信额度
     */
    @Override
    @ServiceTransactional
    public void initCompanyCredit() {
        BigDecimal defaultCreditAmount = BigDecimal.valueOf(500000);
        List<BsCompany> resultCompanyList = companyService.findByEnterpriseIdAndCompanyType(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_TYPE_COMPANYTYPE_I);
        List<BsCompany> piccTargetList = resultCompanyList.stream()
                .filter(c -> c.getPiccCreditAmount().compareTo(BigDecimal.ZERO) > 0)
                .filter(c -> Boolean.TRUE.equals(c.getEnableFlg())).collect(Collectors.toList());

        List<BsCompany> daDiTargetList = resultCompanyList.stream()
                .filter(c -> c.getDaDiCreditAmount().compareTo(BigDecimal.ZERO) > 0)
                .filter(c -> Boolean.TRUE.equals(c.getEnableFlg())).collect(Collectors.toList());

        List<BsCompany> basTargetList = resultCompanyList.stream()
                .filter(c -> Boolean.TRUE.equals(c.getEnableFlg())).collect(Collectors.toList());


        List<BsCompanyCredit> creditList = (List<BsCompanyCredit>) bsCompanyCreditDao.findAll();
        Map<String, BsCompanyCredit> creditMap = creditList.stream().collect(Collectors.toMap(c -> c.getCompanyId() + "_" + c.getCreditType(), c -> c));

        List<BsCompanyCredit> initCreditList = new ArrayList<>();
        piccTargetList.forEach(p -> {
            if (Objects.isNull(creditMap.get(p.getId() + "_" + BasConstants.CREDIT_TYPE_0))) {
                initCreditList.add(buildInitCreditEntity(p, BasConstants.CREDIT_TYPE_0, p.getPiccCreditAmount()));
            }
        });
        daDiTargetList.forEach(p -> {
            if (Objects.isNull(creditMap.get(p.getId() + "_" + BasConstants.CREDIT_TYPE_1))) {
                initCreditList.add(buildInitCreditEntity(p, BasConstants.CREDIT_TYPE_1, p.getDaDiCreditAmount()));
            }
        });
        basTargetList.forEach(p -> {
            if (Objects.isNull(creditMap.get(p.getId() + "_" + BasConstants.CREDIT_TYPE_9))) {
                initCreditList.add(buildInitCreditEntity(p, BasConstants.CREDIT_TYPE_9, defaultCreditAmount));
            }
        });
        bsCompanyCreditDao.saveAll(initCreditList);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateUsedCreditAmount(Long companyCreditId, BigDecimal creditAmount) {
        BsCompanyCredit entity = bsCompanyCreditDao.findEntityForUpdate(companyCreditId);
        if (Objects.isNull(entity)) {
            return;
        }
        entity.setUsedCreditAmount(creditAmount);
        bsCompanyCreditDao.save(entity);
    }

    @Override
    @ServiceTransactional
    public void syncHisCompanyCreditId() {
        List<CtrContract> hisCreditContract = ctrContractDao.findHisCreditContract();
        if (CollectionUtils.isEmpty(hisCreditContract)) {
            return;
        }
        List<BsCompanyCredit> creditList = (List<BsCompanyCredit>) bsCompanyCreditDao.findAll();
        Map<String, BsCompanyCredit> creditMap = creditList.stream().collect(Collectors.toMap(c -> c.getCompanyId() + "_" + c.getCreditType(), c -> c));

        hisCreditContract.forEach(c -> {
            String ourCompanyName = c.getOurCompanyName();
            Long companyId = c.getCompanyId();
            Long targetCompanyCreditId = null;
            if (StringUtils.equals(BasConstants.COMPANY_NAME_SDNH, ourCompanyName) && Objects.nonNull(creditMap.get(companyId + "_" + BasConstants.CREDIT_TYPE_1))) {
                // 山东能化抬头 使用大地额度
                targetCompanyCreditId = creditMap.get(companyId + "_" + BasConstants.CREDIT_TYPE_1).getId();
            } else if (Objects.nonNull(creditMap.get(companyId + "_" + BasConstants.CREDIT_TYPE_0))) {
                // 其它情况 默认使用人保额度
                targetCompanyCreditId = creditMap.get(companyId + "_" + BasConstants.CREDIT_TYPE_0).getId();
            } else if (Objects.nonNull(creditMap.get(companyId + "_" + BasConstants.CREDIT_TYPE_9))) {
                // 无人保额度 使用自主授信额度
                targetCompanyCreditId = creditMap.get(companyId + "_" + BasConstants.CREDIT_TYPE_9).getId();
            }
            if (Objects.nonNull(targetCompanyCreditId)) {
                c.setCompanyCreditId(targetCompanyCreditId);
            }
        });

        ctrContractDao.saveAll(hisCreditContract);
    }

    @Override
    @ServiceTransactional
    public void syncHisCreditUserAmount() {
        List<CtrContract> hisCreditUserContracts = ctrContractDao.findHisCreditUserAmount();
        if (CollectionUtils.isEmpty(hisCreditUserContracts)) {
            return;
        }
        Map<Long, BigDecimal> creditResultMap = hisCreditUserContracts.stream()
                .collect(Collectors.groupingBy(
                        CtrContract::getCompanyCreditId,
                        Collectors.mapping(
                                contract -> contract.getTotalAmount().subtract(contract.getDealedAmount()),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
        if (creditResultMap.isEmpty()){
            return;
        }
        ArrayList<Long> targetCompanyCreditIds = new ArrayList<>(creditResultMap.keySet());
        List<BsCompanyCredit> companyCreditList = bsCompanyCreditDao.findByIdIn(targetCompanyCreditIds);
        if (CollectionUtils.isEmpty(companyCreditList)) {
            return;
        }
        companyCreditList.forEach(c -> {
            if (Objects.nonNull(creditResultMap.get(c.getId()))){
                c.setUsedCreditAmount(creditResultMap.get(c.getId()));
            }
        });
        logger.info(JsonUtil.obj2Json(creditResultMap));
        bsCompanyCreditDao.saveAll(companyCreditList);
    }

    private BsCompanyCredit buildInitCreditEntity(BsCompany bsCompany, String creditType, BigDecimal creditAmount){
        BsCompanyCredit entity = new BsCompanyCredit();
        entity.setCompanyId(bsCompany.getId());
        entity.setEnterpriseId(bsCompany.getEnterpriseId());
        entity.setCreditAmount(creditAmount);
        entity.setCreditType(creditType);
        entity.setEnableFlg(true);
        entity.setCreatedUserId(0L);
        entity.setCreatedUserName("init");
        return entity;
    }


    @Override
    public BsCompanyCredit findByCompanyIdAndType(Long companyId, String creditType) {
        return bsCompanyCreditDao.findByCompanyIdAndType(companyId, creditType);
    }
    
    @Override
    public List<BsCompanyCredit> findByCompanyId(Long companyId) {
        return bsCompanyCreditDao.findByCompanyId(companyId);
    }
}
