package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.tools.jpa.service.IBaseService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface IBsCompanyCreditService extends IBaseService<BsCompanyCredit> {
    BsCompanyCredit findByCompanyIdAndType(Long companyId, String creditType);
    
    List<BsCompanyCredit> findByCompanyId(Long companyId);

    /**
     * 根据企业ID、授信类型、有效状态 查询唯一授信额度数据
     *
     * @param companyId
     * @param creditType
     * @param enableFlg
     * @return
     */
    BsCompanyCredit findByCompanyIdAndCreditTypeAndEnableFlg(Long companyId, String creditType, Boolean enableFlg);

    /**
     * 根据临时额度失效日查询 授信额度信息
     *
     * @param temporaryExpiryDate
     * @return
     */
    List<BsCompanyCredit> findByTemporaryExpiryDateBefore(Date temporaryExpiryDate);

    /**
     * 临时额度到期自动恢复
     */
    void recoverTemporaryAmount();

    /**
     * 初始化企业授信额度
     */
    void initCompanyCredit();

    void updateUsedCreditAmount(Long companyCreditId, BigDecimal creditAmount);

    void syncHisCompanyCreditId();

    void syncHisCreditUserAmount();
}
