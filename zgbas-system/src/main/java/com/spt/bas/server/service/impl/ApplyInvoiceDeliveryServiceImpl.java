package com.spt.bas.server.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInvoiceDelivery;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyInvoiceDeliveryDao;
import com.spt.bas.server.service.IApplyInvoiceDeliveryService;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * <p>
 *  发票寄送
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-20 21:46
 */
@Component("applyInvoiceDeliveryService")
public class ApplyInvoiceDeliveryServiceImpl extends BaseService<ApplyInvoiceDelivery>
        implements IApplyInvoiceDeliveryService, IPmService, IPmApproveListener {

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("applyInvoiceService-%d").build();
    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(
            1,
            1,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    private static final ScheduledExecutorService SCHEDULED_POOL = new ScheduledThreadPoolExecutor(
            8,namedThreadFactory,new ThreadPoolExecutor.AbortPolicy());

    @Autowired
    private ApplyInvoiceDeliveryDao applyInvoiceDeliveryDao;
    @Autowired
    private PmProcessDao pmProcessDao;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmApproveService approveService;

    /**
     * 发起审批
     *
     * @param approve
     */
    @Override
    public void doStepIn(PmApprove approve) throws ApplicationException {

    }

    /**
     * 执行审批步骤
     *
     * @param approve
     * @param nextStep
     */
    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {

        }
    }

    /**
     * 审批驳回
     *
     * @param approve
     * @param nextStep
     */
    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {

    }

    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyInvoiceDelivery applyInvoiceDelivery = (ApplyInvoiceDelivery) pmEntity;
            return save(applyInvoiceDelivery);
        }
        return null;
    }

    /**
     * 标题
     *
     * @param pmEntity
     */
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        ApplyInvoiceDelivery entity = (ApplyInvoiceDelivery) pmEntity;
        String invoiceNumber = entity.getInvoiceNumber();
        invoiceNumber = StringUtils.isEmpty(invoiceNumber) ? "" : invoiceNumber;
        String companyName = RuleUtil.companyNameSubString(entity.getCompanyName());
        String subject = SubjectUtil.formatSubject(entity.getContractNo(), companyName, invoiceNumber);
        return subject;
    }

    @Override
    public BaseDao<ApplyInvoiceDelivery> getBaseDao() {
        return applyInvoiceDeliveryDao;
    }

    /**
     * 发起发票寄送申请
     *
     * @param applyInvoiceDelivery
     */
    @Override
    @ServerTransactional
    public void startInvoiceDelivery(ApplyInvoiceDelivery applyInvoiceDelivery) {
        // THREAD_POOL.execute(() -> parseInvoiceDelivery(applyInvoiceDelivery));
    }

//    private void parseInvoiceDelivery(ApplyInvoiceDelivery applyInvoiceDelivery) {
//        SCHEDULED_POOL.schedule(() -> {
//            try {
//                PmApproveSaveVo startVo = new PmApproveSaveVo();
//                startVo.setMode(BasConstants.APPROVE_STATUS_A);
//                startVo.setStatus(BasConstants.APPROVE_STATUS_A);
//                startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
//
//                PmProcess process = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_INVOICE_DELIVERY, BasConstants.ZG_ENTERPRISE_ID);
//
//                if (process == null) {
//                    throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
//                }
//                SysUserSdk userById = authOpenFacade.findUserById(applyInvoiceDelivery.getApplyUserId());
//                startVo.setDeptId(userById.getDeptId());
//                startVo.setUserId(userById.getUserId());
//                startVo.setUserName(userById.getNickName());
//                startVo.setProcessId(process.getId());
//                startVo.setAutoStartFlgReal(true);
//                startVo.setAutoStartMessage("代采赊销开票完成，自动发起发票寄送");
//                startVo.setApproveId(0L);
//                String bizEntityJson = JsonUtil.obj2Json(applyInvoiceDelivery);
//                startVo.setBizEntityJson(bizEntityJson);
//                approveService.startFlow(startVo);
//            } catch (Exception e) {
//                logger.info("parseInvoiceDelivery errow",e);
//            }
//        }, 5, TimeUnit.SECONDS);
//    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applyInvoiceDeliveryDao.updateFileId(id, fileId);
    }
}
