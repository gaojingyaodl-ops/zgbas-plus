package com.spt.quartz.task;


import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.tools.core.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * qzh
 * 违约企业重置赊销额度及准入
 * 2021/10/21
 *
 * <p>Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.DefaultingEnterpriseTask}.
 * Bean name {@code "defaultingEnterpriseTask"} aligns with {@code sys_job.invoke_target}
 * short name {@code defaultingEnterpriseTask.defaultingEnterpriseTask}.
 */
@Component("defaultingEnterpriseTask")
public class DefaultingEnterpriseTask {
    private Logger logger = LoggerFactory.getLogger(BudgetSettlementTask.class);
    @Autowired
    private IBsCompanyService companyService;
    @Autowired
    private ICtrContractService ctrContractService;

//    @SneakyThrows
//    @Scheduled(cron = "0 0 23 * * ? ")
    public void defaultingEnterpriseTask() throws ApplicationException {
        logger.info("违约企业重置赊销-定时任务启动====>");
        List<CtrContract> ctrContractBreach = ctrContractService.findCtrContractBreach();
        ctrContractBreach.sort(Comparator.comparing(CtrContract::getCompanyId));
        Iterator<CtrContract> it = ctrContractBreach.iterator();
        Long next=null ;
        while (it.hasNext()) {
            Long   next2 = it.next().getCompanyId();
             if (next2.equals(next)){
                it.remove();
             }
            next=next2;
        }
        for (CtrContract contractBreach : ctrContractBreach) {
            BsCompany bsCompany = companyService.findCompany(contractBreach.getCompanyId());
            bsCompany.setTotalCreditAmount(BigDecimal.ZERO);
            bsCompany.setUsedCreditAmount(BigDecimal.ZERO);
            bsCompany.setApproveCreditAmount(BigDecimal.ZERO);
            bsCompany.setCreditRating("B");//黑名单
            companyService.save(bsCompany);
        }
    }


}
