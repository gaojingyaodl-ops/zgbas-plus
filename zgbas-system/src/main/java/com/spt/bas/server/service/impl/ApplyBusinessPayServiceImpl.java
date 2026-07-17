package com.spt.bas.server.service.impl;

import com.google.common.base.Splitter;
import com.hsoft.push.sdk.remote.PushClientHttp;
import com.hsoft.push.sdk.vo.PushRequest;
import com.hsoft.push.sdk.vo.PushResponse;
import com.hsoft.push.sdk.vo.PushTarget;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.LitigationCaseFeeEnum;
import com.spt.bas.client.entity.ApplyBusinessPay;
import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.LitigationCase;
import com.spt.bas.client.remote.IApplyDeliveryOutClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.ILitigationCaseClient;
import com.spt.bas.client.vo.ApplyBusinessPayVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyBusinessPayDao;
import com.spt.bas.server.service.IApplyBusinessPayService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmApproveStepService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveRetrieveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component("applyBusinessPayService")
@Transactional(readOnly = true)
public class ApplyBusinessPayServiceImpl extends BaseService<ApplyBusinessPay> implements IApplyBusinessPayService, IPmService, IPmApproveListener {
    private static final ExecutorService THREAD_POOL = Executors.newSingleThreadExecutor();
    @Autowired
    private ApplyBusinessPayDao applyBusinessPayDao;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IApplyBusinessPayService applyBusinessPayService;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private IApplyDeliveryOutClient applyDeliveryOutClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private ILitigationCaseClient litigationCaseClient;
    @Resource
    private PushClientHttp pushRemote;
    @Resource
    private IPmApproveStepService pmApproveStepService;

    @Override
    public ApplyBusinessPay findByApproveId(Long id) {

        return  applyBusinessPayDao.findByApproveId(id);
    }
    @Override
    public BaseDao<ApplyBusinessPay> getBaseDao() {
        return applyBusinessPayDao;
    }

    @Override
    public Class<ApplyBusinessPay> getEntityClazz() {
        return ApplyBusinessPay.class;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applyBusinessPayDao.updateFileId(id, fileId);
    }

    @Override
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyBusinessPay applyBusinessPay = applyBusinessPayDao.findOne(approve.getBizId());
        // 适配案件诉讼费用申请
        updateLitigationCase(approve,applyBusinessPay,BasConstants.APPROVE_STATUS_A);
    }

    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApprove byApproveNo = pmApproveService.findByApproveNo(approve.getApproveNo());
            ApplyBusinessPay entity = applyBusinessPayService.findByApproveId(byApproveNo.getId());
            if(entity.getContractList()!=null){
                String[] title = entity.getContractList().split(",");
                for (String s : title) {
//                     CtrContract byContractNoV2 = ctrContractClient.findByContractNoV2(s);
//                     List<CtrContract> byApproveId = ctrContractService.findByApproveId(byContractNoV2.getApproveId());
//                    for (CtrContract contract : byApproveId) {
//                        contract.setFreightSettlement("1");
//                    }
                     Long aLong = Long.valueOf(s);
                    ApplyDeliveryOut byApplyNo = applyDeliveryOutClient.findEntity(aLong);
                    CtrContract byContractNoV2 = ctrContractClient.findByContractNoV2(byApplyNo.getContractNo());
                    if (StringUtils.equals("S", byContractNoV2.getContractType())) {
                        byApplyNo.setFreightSettlement("1");
                        applyDeliveryOutClient.save(byApplyNo);
                    }
                }
            }
            updateLitigationFee(approve, entity);
            // 适配案件诉讼费用申请
            updateLitigationCase(approve,entity,BasConstants.APPROVE_STATUS_D);
            // 发送审批完成通知
            notifyUserCompleted(approve);
        }
    }

    /**
     * 修改合同中诉讼费,律师费,保全费
     * @param approve
     * @param entity
     */
    private void updateLitigationFee(PmApprove approve, ApplyBusinessPay entity){
        try {
            PmProcess process = pmProcessService.getEntity(approve.getProcessId());
            if (Objects.nonNull(process) && StringUtils.equals(BasConstants.PROCESS_APPLY_OPERATING_BUSINESS_PAY, process.getProcessCode())){
                String costType = entity.getCostType();
                BigDecimal dealAmount = entity.getDealAmount();
                Long contractId = entity.getContractId();
                CtrContract ctrContract = ctrContractService.getEntity(contractId);
                if (contractId != null) {
                    if (StringUtils.equals("14", costType)) {
                        // 诉讼费
                        ctrContract.setLitigationFees(dealAmount);
                    } else if (StringUtils.equals("15", costType)) {
                        // 保全费
                        ctrContract.setSecurityFees(dealAmount);
                    } else if (StringUtils.equals("16", costType)) {
                        // 律师费
                        ctrContract.setLegalFees(dealAmount);
                    }
                    ctrContractClient.save(ctrContract);
                }
            }
        }catch (Exception e){
            logger.error("updateLitigationFee error:{}", e);
        }
    }

    @Override
    @ServerTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        ApplyBusinessPay entity = applyBusinessPayService.findByApproveId(vo.getApproveId());
        if(entity.getContractList()!=null){
            String[] title = entity.getContractList().split(",");
            for (String s : title) {
                Long aLong = Long.valueOf(s);
                ApplyDeliveryOut byApplyNo = applyDeliveryOutClient.findEntity(aLong);
                CtrContract byContractNoV2 = ctrContractClient.findByContractNoV2(byApplyNo.getContractNo());
                if (StringUtils.equals("S", byContractNoV2.getContractType())) {
                    byApplyNo.setFreightSettlement(null);
                    applyDeliveryOutClient.save(byApplyNo);
                }
            }
        }
        // 适配案件诉讼费用申请
        PmApprove approve = pmApproveService.getEntity(vo.getApproveId());
        updateLitigationCase(approve,entity,BasConstants.APPROVE_STATUS_C);
    }
    /** 审批驳回 */
    @Override
    @ServerTransactional
    public void  doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyBusinessPay entity = applyBusinessPayService.findByApproveId(approve.getId());
        if(entity.getContractList()!=null){
            String[] title = entity.getContractList().split(",");
            for (String s : title) {
                Long aLong = Long.valueOf(s);
                ApplyDeliveryOut byApplyNo = applyDeliveryOutClient.findEntity(aLong);
                CtrContract byContractNoV2 = ctrContractClient.findByContractNoV2(byApplyNo.getContractNo());
                if (StringUtils.equals("S", byContractNoV2.getContractType())) {
                    byApplyNo.setFreightSettlement(null);
                    applyDeliveryOutClient.save(byApplyNo);
                }

            }
        }
        // 适配案件诉讼费用申请
        updateLitigationCase(approve,entity,BasConstants.APPROVE_STATUS_B);
    }


    /** 审批追回 */
    @Override
    @ServerTransactional
    public void  doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        ApplyBusinessPay entity = applyBusinessPayService.findByApproveId(vo.getApproveId());
        if(entity.getContractList()!=null){
            String[] title = entity.getContractList().split(",");
            for (String s : title) {
                Long aLong = Long.valueOf(s);
                ApplyDeliveryOut byApplyNo = applyDeliveryOutClient.findEntity(aLong);
                CtrContract byContractNoV2 = ctrContractClient.findByContractNoV2(byApplyNo.getContractNo());
                if (StringUtils.equals("S", byContractNoV2.getContractType())) {
                    byApplyNo.setFreightSettlement(null);
                    applyDeliveryOutClient.save(byApplyNo);
                }

            }
        }
        // 适配案件诉讼费用申请
        PmApprove approve = pmApproveService.getEntity(vo.getApproveId());
        updateLitigationCase(approve,entity,BasConstants.APPROVE_STATUS_E);
    };

    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyBusinessPay entity = (ApplyBusinessPay) pmEntity;
            System.out.println("saveEntity : " + entity);
            PmApprove entity1 = pmApproveService.getEntity(entity.getApproveId());
            if(entity1 != null){
                //SysDept deptByUserId = adminOpenFacade.findDeptByUserId(entity1.getCreateUserId());
                SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity1.getCreateUserId());
                entity.setDeptId(deptByUserId.getDeptId());
            }
            return save(entity);
        }
        return null;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyBusinessPay businessPay = (ApplyBusinessPay) pmEntity;
            BigDecimal dealAmount = businessPay.getDealAmount();
            String applyUserName = businessPay.getApplyUserName();
            String companyName = businessPay.getCompanyName();
            String costType = businessPay.getCostType();
            String processCode = process.getProcessCode();
            String receiveCompanyName = StringUtils.isNotBlank(businessPay.getReceiveCompanyName()) ? businessPay.getReceiveCompanyName() : "";
            String costValue = null;
            if (StringUtils.equalsIgnoreCase(BasConstants.PROCESS_APPLY_OPERATING_BUSINESS_PAY, processCode)) {
                // 经营费用申请
                // 收款方 金额 费用类别 我方 备注
                costValue = DictUtil.getValue(BasConstants.DICT_OPERATING_COST_TYPE, costType);
                costValue = StringUtils.isBlank(costValue) ? "" : costValue;
//                String companyNameStr = DictUtil.getValue(BasConstants.DICT_TYPE_CUSTOMER_NAME, companyName);
//                companyNameStr = StringUtils.isNotBlank(companyNameStr) ? companyNameStr : "";
//                String remark = StringUtils.isBlank(businessPay.getRemark()) ? "" :businessPay.getRemark();
//                return String.format("%s", businessPay.getCarrier() + "[" + dealAmount + " " + costValue + " "+businessPay.getContractList()+" " + companyNameStr + "]");
//                String receiveCompanyName = StringUtils.isBlank(businessPay.getReceiveCompanyName()) ? "" : businessPay.getReceiveCompanyName();
//                  return String.format("%s", receiveCompanyName + "[" + dealAmount + " " + costValue + " " + companyNameStr + " " + remark + "]");
            } else if (StringUtils.equalsIgnoreCase(BasConstants.PROCESS_APPLY_MANAGE_BUSINESS_PAY, processCode)) {
                // 管理费用申请
                costValue = DictUtil.getValue(BasConstants.DICT_MANAGE_COST_TYPE, costType);
            } else if (StringUtils.equalsIgnoreCase(BasConstants.PROCESS_APPLY_REFUND_BUSINESS_PAY, processCode)) {
                // 业务退款申请
                costValue = DictUtil.getValue(BasConstants.DICT_REFUND_COST_TYPE, costType);
            }
            costValue = StringUtils.isBlank(costValue) ? "" : costValue;
            String companyNameStr = DictUtil.getValue(BasConstants.DICT_TYPE_CUSTOMER_NAME, companyName);
            companyNameStr = StringUtils.isNotBlank(companyNameStr) ? companyNameStr : "";
            String companyName1 = RuleUtil.companyNameSubString(receiveCompanyName);
            String companyName2 = RuleUtil.companyNameSubString(companyNameStr);
            String subject = StringUtils.isNotBlank(businessPay.getSubject()) ? businessPay.getSubject() : "";
            if(StringUtils.isNotBlank(costValue)){
                return SubjectUtil.formatSubject(costValue,SubjectUtil.formatMoney(dealAmount,RuleUtil.monetaryUnit),companyName1,subject,companyName2);
            }else{
                return SubjectUtil.formatSubject(SubjectUtil.formatMoney(dealAmount,RuleUtil.monetaryUnit),companyName1 ,subject,companyName2);
            }
        }
        return null;
    }

    @Override
    public Page<ApplyBusinessPayVo> findPageContract(ApplyBusinessPayVo queryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Specification<ApplyBusinessPay> spe = WebUtil.buildSpecification(queryVo.getSearchParams());
        PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows(), sort);//分页
        Page<ApplyBusinessPay> page = applyBusinessPayDao.findAll(spe,pageRequest);
        List<ApplyBusinessPay> content = page.getContent();
        List<ApplyBusinessPayVo> voList = new ArrayList<ApplyBusinessPayVo>();
        int i = 0;
        for (ApplyBusinessPay ctr : content) {
            i++;
            ApplyBusinessPayVo vo=new ApplyBusinessPayVo();
            BeanUtils.copyProperties(ctr, vo);
            String value = DictUtil.getValue(BasConstants.DICT_BUSINESS_DEPT_TYPE, ctr.getBelogDept());
            String companyName = BsCompanyOurUtil.getValue(ctr.getEnterpriseId(),ctr.getCompanyName());
            vo.setCompanyName(companyName);
            vo.setBelogDept(value);
            vo.setPairId(Long.valueOf(i));
            voList.add(vo);
        }
        // sort属性无法反序列化，下面代码重新组装page对象，去掉sort属性
        PageRequest pageRequest_new = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
        Page<ApplyBusinessPayVo> pageVo = new PageImpl<>(voList, pageRequest_new, page.getTotalElements());
        return pageVo;
    }

    /**
     * 处理诉讼案件
     * @param approve
     * @param entity
     * @param status
     */
    @Transactional
    public void updateLitigationCase(PmApprove approve, ApplyBusinessPay entity,String status) {
        // 如果关联的合同ids，nos 不为空，说明从诉讼案件发起的申请，修改诉讼案件对应金额的申请状态
        if (StringUtils.isNotEmpty(entity.getLinkContractIds()) && StringUtils.isNotEmpty(entity.getLinkContractNos())) {
            String caseId = entity.getLitigationCaseId();
            String feeType = entity.getLitigationCaseType();
            LitigationCase litigationCase = litigationCaseClient.getEntity(Long.valueOf(caseId));
            LitigationCaseFeeEnum enumByCode = LitigationCaseFeeEnum.getEnumByCode(feeType);
            switch (enumByCode) {
                case ATTORNEY_FEE:
                    litigationCase.setAttorneyApproveStatus(status);
                    litigationCase.setAttorneyApproveId(approve.getId());
                    break;
                case PROCESSING_FEE:
                    litigationCase.setProcessingApproveStatus(status);
                    litigationCase.setProcessingApproveId(approve.getId());
                    break;
                case PRESERVATION_FEE:
                    litigationCase.setPreservationApproveStatus(status);
                    litigationCase.setPreservationApproveId(approve.getId());
                    break;
                case LIABILITY_FEE:
                    litigationCase.setLiabilityApproveStatus(status);
                    litigationCase.setLiabilityApproveId(approve.getId());
                    break;
            }
            litigationCaseClient.save(litigationCase);
            // 审批完成，费用按合同金额占比拆分，分别存入合同中
            if (status.equals(BasConstants.APPROVE_STATUS_D)) {
                String linkContractIds = litigationCase.getLinkContractIds();
                List<String> contractIdList = Arrays.asList(linkContractIds.split(","));
                if (CollectionUtils.isNotEmpty(contractIdList)) {
                    List<CtrContract> contractList = new ArrayList<>();
                    contractIdList.forEach(it -> {
                        CtrContract ctrContract = ctrContractClient.getEntity(Long.valueOf(it));
                        contractList.add(ctrContract);
                    });
                    if (CollectionUtils.isNotEmpty(contractList)) {
                        // 费用按照合同金额占比拆分，存入合同对应的费用中
                        BigDecimal totalAmountSum = contractList.stream().map(CtrContract::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        contractList.forEach(it -> {
                            BigDecimal proportion = it.getTotalAmount().divide(totalAmountSum, 4, RoundingMode.HALF_UP);
                            BigDecimal proportionAmount = entity.getDealAmount().multiply(proportion).setScale(3, RoundingMode.HALF_UP);
                            switch (enumByCode) {
                                case ATTORNEY_FEE:
                                    // 律师费
                                    BigDecimal legalFees= it.getLegalFees()==null?BigDecimal.ZERO:it.getLegalFees();
                                    BigDecimal sumLegalFees= legalFees.add(proportionAmount);
                                    it.setLegalFees(sumLegalFees);
                                    break;
                                case PRESERVATION_FEE:
                                    // 保全费
                                    BigDecimal securityFees= it.getSecurityFees()==null?BigDecimal.ZERO:it.getSecurityFees();
                                    BigDecimal sumSecurityFees= securityFees.add(proportionAmount);
                                    it.setSecurityFees(sumSecurityFees);
                                    break;
                                case PROCESSING_FEE:
                                case LIABILITY_FEE:
                                    // 诉讼费
                                    BigDecimal litigationFees= it.getLitigationFees()==null?BigDecimal.ZERO:it.getLitigationFees();
                                    BigDecimal sumLitigationFees = litigationFees.add(proportionAmount);
                                    it.setLitigationFees(sumLitigationFees);
                                    break;
                            }
                            ctrContractClient.save(it);
                        });
                    }
                }
            }
        }
    }

    /**
     * 费用单审批完成，推送邮件通知给申请人及审批人
     * @param approve 审批单
     */
    private void notifyUserCompleted(PmApprove approve) {
        THREAD_POOL.execute(() -> {
            List<PmApproveStep> approveStepList = pmApproveStepService.findByApproveId(approve.getId());
            Set<Long> lstUserIds = approveStepList.stream().map(PmApproveStep::getApproveUserId).filter(Objects::nonNull).collect(Collectors.toSet());
            lstUserIds.add(approve.getCreateUserId());
            lstUserIds.add(approve.getLastApproveUserId());
            for (Long userId : lstUserIds) {
                SysUserSdk sysUser = authOpenFacade.findUserById(userId);
                if (Objects.isNull(sysUser)) {
                    continue;
                }
                if (Objects.nonNull(sysUser.getDept()) && StringUtils.isNotBlank(sysUser.getDept().getDeptName()) && sysUser.getDept().getDeptName().contains("财务")){
                    continue;
                }
                if (StringUtils.isNotBlank(sysUser.getEmail())) {
                    PushRequest req = new PushRequest();
                    req.setBusinessId(approve.getApproveNo());
                    req.setModule("S");
                    req.setPushType("basApproveCompletedNotify");
                    req.setSubmitUserId("sys");
                    List<PushTarget> lst = new ArrayList<>();
                    lst.add(new PushTarget(String.valueOf(userId), sysUser.getPhonenumber(), sysUser.getEmail()));
                    req.setTargets(lst);
                    Map<String, Object> param = new HashMap<>();
                    param.put("approveNo", approve.getApproveNo());
                    param.put("processName", approve.getProcessName());
                    param.put("subject", approve.getSubject());
                    String lastApproveDate ="";
                    if (approve.getLastApproveDate() != null) {
                        lastApproveDate = DateOperator.formatDate(approve.getLastApproveDate(), true);
                    }
                    param.put("lastApproveDate", lastApproveDate);
                    param.put("lastApproveUserName", approve.getLastApproveUserName());
                    param.put("lastApproveRemark", approve.getLastApproveRemark());
                    req.setParam(param);
                    logger.info("notifyUserCompleted : " + JsonUtil.obj2Json(req));
                    try {
                        if (StringUtils.equals(sysUser.getEmail(), "wangwei@totrade.cn")) {
                            logger.info("跳过邮件wangwei@totrade.cn");
                            continue;
                        }
                        PushResponse send = pushRemote.send(req);
                        logger.info("send : " + JsonUtil.obj2Json(send));
                    } catch (Exception e) {
                        logger.error("notifyUser error", e);
                    }
                }
            }
        });
    }
}
