package com.spt.bas.server.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.bas.client.entity.BsCompanyVisit;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.dao.BsCompanyVisitDao;
import com.spt.bas.server.service.IBsCompanyCreditService;
import com.spt.bas.server.service.IBsCompanyVisitService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveRetrieveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @Author 田起立
 * @Date 2024/5/31 17:10
 * @Description:
 */
@Component("bsApproveCompanyVisitService")
@Transactional(readOnly = true)
public class BsCompanyVisitServiceImpl  extends BaseService<BsCompanyVisit> implements IBsCompanyVisitService, IPmService, IPmApproveListener {
    @Autowired
    private BsCompanyVisitDao bsCompanyVisitDao;
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Resource
    private IBsCompanyCreditService bsCompanyCreditService;

    @Override
    public Class<BsCompanyVisit> getEntityClazz() {
        return BsCompanyVisit.class;
    }

    @Override
    public void doStepIn(PmApprove approve) throws ApplicationException {
        BsCompanyVisit bsCompanyVisit= bsCompanyVisitDao.findOne(approve.getBizId());
        bsCompanyVisit.setStatus("A");
        bsCompanyVisitDao.save(bsCompanyVisit);
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            BsCompanyVisit bsCompanyVisit = bsCompanyVisitDao.findOne(approve.getBizId());
            bsCompanyVisit.setStatus("D");
            bsCompanyVisit.setApproveId(approve.getId());
            bsCompanyVisit.setApproveNo(approve.getApproveNo());
            save(bsCompanyVisit);
            // 同步企业访厂信息
            BsCompany company = bsCompanyDao.findOne(bsCompanyVisit.getCompanyId());
            company.setAccessReportUploadDate(bsCompanyVisit.getCreatedDate());
            company.setAccessReportFlg(true);
            company.setAccessReportId(bsCompanyVisit.getFileId());
            company.setAccessReportApproveId(bsCompanyVisit.getApproveId());
            bsCompanyDao.save(company);
            // 更新人保风控额度
            BsCompanyCredit companyCredit = bsCompanyCreditService.findByCompanyIdAndCreditTypeAndEnableFlg(bsCompanyVisit.getCompanyId(), BasConstants.CREDIT_TYPE_0, true);
            if (Objects.nonNull(companyCredit) && NumberUtil.isNumber(bsCompanyVisit.getRecommendedAmount())){
                companyCredit.setRiskAmount(new BigDecimal(bsCompanyVisit.getRecommendedAmount()));
                bsCompanyCreditService.save(companyCredit);
            }
        }
    }

    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        BsCompanyVisit bsCompanyVisit = bsCompanyVisitDao.findOne(approve.getBizId());
        bsCompanyVisit.setStatus("B");
        bsCompanyVisit.setApproveId(approve.getId());
        bsCompanyVisit.setApproveNo(approve.getApproveNo());
        save(bsCompanyVisit);
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        BsCompanyVisit bsCompanyVisit = bsCompanyVisitDao.findOne(vo.getBizId());
        bsCompanyVisit.setStatus("N");
        save(bsCompanyVisit);
    }

    @Override
    public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        BsCompanyVisit bsCompanyVisit = bsCompanyVisitDao.findOne(vo.getBizId());
        bsCompanyVisit.setStatus("N");
        save(bsCompanyVisit);
    }

    @Override
    public BaseDao<BsCompanyVisit> getBaseDao() {
        return bsCompanyVisitDao;
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            BsCompanyVisit entity = (BsCompanyVisit) pmEntity;
            return save(entity);
        }
        return null;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        BsCompanyVisit entity = (BsCompanyVisit) pmEntity;
        return entity.getCompanyName()+","+entity.getRiskControlSurveyUserName();
    }

    @Override
    public BsCompanyVisit getCompanyVisitById(Long id) {
       return bsCompanyVisitDao.findOne(id);
    }
}
