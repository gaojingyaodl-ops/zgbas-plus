package com.spt.bas.server.service.impl;

import com.google.common.base.Splitter;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.InvalidTypeEnum;
import com.spt.bas.client.constant.OwnRegionEnum;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyInvalid;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.ApplyInvalidApproveVo;
import com.spt.bas.client.vo.ApplyInvalidDetailVo;
import com.spt.bas.client.vo.CtrConctractInvalidVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.ctr.service.ICtrContractInvalidService;
import com.spt.bas.server.dao.ApplyDcsxDao;
import com.spt.bas.server.dao.ApplyInvalidDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.service.IApplyInvalidService;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.util.SpringContextHolder;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 作废申请
 *
 * @Author MoonLight
 * @Date 2023/9/11 14:31
 * @Version 1.0
 */
@Component("applyInvalidService")
@Transactional(readOnly = true)
public class ApplyInvalidServiceImpl extends BaseService<ApplyInvalid> implements IApplyInvalidService, IPmService, IPmApproveListener {
    @Resource
    private ApplyInvalidDao applyInvalidDao;
    @Resource
    private CtrContractDao ctrContractDao;
    @Resource
    private PmApproveDao pmApproveDao;
    @Resource
    private PmProcessDao pmProcessDao;
    @Resource
    private ApplyDcsxDao applyDcsxDao;
    @Resource
    private IPmApproveService pmApproveService;
    @Resource
    private ICtrContractInvalidService ctrContractInvalidService;
    @Resource
    private IAuthOpenFacade authOpenFacade;

    @Override
    @ServerTransactional
    public BaseDao<ApplyInvalid> getBaseDao() {
        return applyInvalidDao;
    }

    /**
     * 执行审批步骤
     *
     * @param approve
     * @param nextStep
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (!StringUtils.equalsIgnoreCase(BasConstants.APPROVE_STATUS_D, approve.getStatus())) {
            return;
        }
        ApplyInvalid entity = applyInvalidDao.findOne(approve.getBizId());
        List<Long> invalidApproveIdList = getInvalidApproveIdList(entity);
        boolean contractInvalidFlg = false;
        if (StringUtils.equals(InvalidTypeEnum.CC.getInvalidTypeCode(), entity.getInvalidType())) {
            ApplyInvalid queryEntity = new ApplyInvalid();
            queryEntity.setContractTailNo(entity.getContractTailNo());
            queryEntity.setBudgetApproveId(entity.getBudgetApproveId());
            queryEntity.setEnterpriseId(entity.getEnterpriseId());
            List<ApplyInvalidApproveVo> invalidApproveList = queryInvalidApproveList(queryEntity);
            invalidApproveIdList = invalidApproveList.stream().map(ApplyInvalidApproveVo::getApproveId).filter(Objects::nonNull).collect(Collectors.toList());
            contractInvalidFlg = true;
        }
        invalidApproveIdList.forEach(t -> {
            try {
                pmApproveService.doWithdraw(assembleWithdrawVo(approve, t));
            } catch (ApplicationException e) {
                logger.error("listener doWithdraw error -------------------------------", e);
            }
        });

        if (Boolean.TRUE.equals(contractInvalidFlg)) {
            Long invalidContractId = Objects.isNull(entity.getSellContractId()) ? entity.getBuyContractId() : entity.getSellContractId();
            CtrConctractInvalidVo vo = new CtrConctractInvalidVo(invalidContractId, approve.getCreateUserId(), approve.getCreateUserName());
            ctrContractInvalidService.invalidTheContract(vo);
        }
    }

    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        ApplyInvalid entity = null;
        if (pmEntity instanceof ApplyInvalid) {
            entity = (ApplyInvalid) pmEntity;
            Long contractId = Objects.isNull(entity.getSellContractId()) ? entity.getBuyContractId() : entity.getSellContractId();
            CtrContract contract = ctrContractDao.findOne(contractId);
            if (Objects.nonNull(contract)){
                SysDeptSdk sysDeptSdk = authOpenFacade.findDeptById(contract.getDeptId());
                entity.setOwnRegion(Objects.nonNull(sysDeptSdk) && Objects.nonNull(OwnRegionEnum.getRegionEnumByName(sysDeptSdk.getDeptName()))
                        ? Objects.requireNonNull(OwnRegionEnum.getRegionEnumByName(sysDeptSdk.getDeptName())).getRegionCode()
                        : "");
            }
            return applyInvalidDao.save(entity);
        }
        return entity;
    }

    /**
     * 标题
     *
     * @param pmEntity
     * @param pmProcess
     */
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        String subject = "";
        if (pmEntity != null) {
            ApplyInvalid entity = (ApplyInvalid) pmEntity;
            String contractTailNo = entity.getContractTailNo();
            String invalidType = entity.getInvalidType();
            String invalidRemark = entity.getInvalidRemark();
            String invalidTypeStr = BsDictUtil.getValue(entity.getEnterpriseId(), BasConstants.DICT_INVALID_TYPE, invalidType);
            subject = SubjectUtil.formatSubject(contractTailNo, invalidTypeStr, invalidRemark);
        }
        return subject;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applyInvalidDao.updateFileId(id, fileId);
    }

    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    @ServerTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
    }

    @Override
    public ApplyInvalidDetailVo queryInvalidDetail(ApplyInvalid applyInvalid) {
        Long budgetApproveId = applyInvalid.getBudgetApproveId();
        String contractTailNo = applyInvalid.getContractTailNo();
        List<CtrContract> contractList;
        List<String> tradeChainList = new ArrayList<>();
        if (StringUtils.isNotBlank(contractTailNo)) {
            contractList = ctrContractDao.findByContractNoLikes(contractTailNo);
            if (CollectionUtils.isNotEmpty(contractList) && contractList.size() == 1) {
                contractList = ctrContractDao.findByApproveId(contractList.get(0).getApproveId());
            }
        } else {
            contractList = ctrContractDao.findByApproveId(budgetApproveId);
        }
        if (CollectionUtils.isEmpty(contractList)) {
            return null;
        }
        Long ctrContractId;
        Long approveId = 0L;
        List<Long> contractIdList = new ArrayList<>();
        ApplyInvalidDetailVo invalidDetails = new ApplyInvalidDetailVo();
        CtrContract sellContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_TYPE_S, c.getContractType())).findFirst().orElse(null);
        CtrContract buyContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_TYPE_B, c.getContractType())).findFirst().orElse(null);
        invalidDetails.setSellContract(sellContract);
        invalidDetails.setBuyContract(buyContract);
        if (Objects.nonNull(buyContract)) {
            ctrContractId = buyContract.getId();
            approveId = buyContract.getApproveId();
            contractIdList.add(ctrContractId);
            tradeChainList.add(buyContract.getCompanyName());
            tradeChainList.add(buyContract.getOurCompanyName());
        }
        if (Objects.nonNull(sellContract)) {
            ctrContractId = sellContract.getId();
            approveId = sellContract.getApproveId();
            contractIdList.add(ctrContractId);
            tradeChainList.add(sellContract.getOurCompanyName());
            tradeChainList.add(sellContract.getCompanyName());
        }
        ApplyCtrDCSX applyCtrDcsx = applyDcsxDao.findByDCSXApproveId(approveId);
        if (Objects.nonNull(applyCtrDcsx)) {
            contractIdList.add(applyCtrDcsx.getId());
        }
        tradeChainList = tradeChainList.stream().distinct().collect(Collectors.toList());
        invalidDetails.setTradeChain(String.join(">", tradeChainList));
        invalidDetails.setInvalidTypeList(filterInvalidType(contractIdList, applyInvalid.getEnterpriseId(), approveId, contractTailNo));
        return invalidDetails;
    }

    @Override
    public List<ApplyInvalidApproveVo> queryInvalidApproveList(ApplyInvalid applyInvalid) {
        List<ApplyInvalidApproveVo> invalidApproveList = new ArrayList<>();
        Long budgetApproveId = applyInvalid.getBudgetApproveId();
        String invalidApproveIds = applyInvalid.getInvalidApproveIds();
        Long enterpriseId = applyInvalid.getEnterpriseId();
        String contractTailNo = applyInvalid.getContractTailNo();
        if (Objects.isNull(budgetApproveId)) {
            return invalidApproveList;
        }
        if (StringUtils.isNotBlank(invalidApproveIds)) {
            List<String> approveIdsList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(invalidApproveIds);
            List<Long> queryApproveIds = approveIdsList.stream().filter(StringUtils::isNotBlank).map(Long::valueOf).collect(Collectors.toList());
            List<PmApprove> targetApproveList = pmApproveDao.findByIds(queryApproveIds);
            if (CollectionUtils.isNotEmpty(targetApproveList)) {
                return parseInvalidList(targetApproveList, enterpriseId);
            }
        }
        List<Long> contractIdList = new ArrayList<>();
        List<CtrContract> contractList = ctrContractDao.findByApproveId(budgetApproveId);
        ApplyCtrDCSX applyCtrDcsx = applyDcsxDao.findByDCSXApproveId(budgetApproveId);
        if (CollectionUtils.isNotEmpty(contractList)) {
            contractList.forEach(c -> contractIdList.add(c.getId()));
        }
        if (Objects.nonNull(applyCtrDcsx)) {
            contractIdList.add(applyCtrDcsx.getId());
        }
        String invalidType = applyInvalid.getInvalidType();
        invalidApproveList = queryInvalidApproveList(contractIdList, enterpriseId, budgetApproveId, contractTailNo);
        if (StringUtils.isNotBlank(invalidType) && CollectionUtils.isNotEmpty(invalidApproveList)) {
            invalidApproveList = invalidApproveList.stream().filter(t -> StringUtils.equals(t.getInvalidTypeCode(), invalidType)).collect(Collectors.toList());
        }
        return invalidApproveList.stream().filter(t-> StringUtils.isNotBlank(t.getInvalidTypeCode())).collect(Collectors.toList());
    }

    /**
     * 根据合同ID，查询可作废的审批单列表
     *
     * @param contractIds
     * @param enterpriseId
     * @return
     */
    @Override
    public List<ApplyInvalidApproveVo> queryInvalidApproveList(List<Long> contractIds, Long enterpriseId, Long budgetApproveId, String contractTailNo) {
        List<ApplyInvalidApproveVo> resultList = new ArrayList<>();
        List<PmApprove> canInvalidList = pmApproveDao.findCanInvalidList(contractIds, contractTailNo);
        if (CollectionUtils.isEmpty(canInvalidList)) {
            return resultList;
        }
        if (Objects.nonNull(budgetApproveId)) {
            PmApprove approve = pmApproveDao.findOne(budgetApproveId);
            if (Objects.nonNull(approve) && StringUtils.equals(BasConstants.APPROVE_STATUS_D, approve.getStatus())) {
                canInvalidList.add(approve);
            }
        }
        resultList = parseInvalidList(canInvalidList, enterpriseId);
        return resultList;
    }

    private List<ApplyInvalidApproveVo> parseInvalidList(List<PmApprove> canInvalidList, Long enterpriseId) {
        List<ApplyInvalidApproveVo> resultList = new ArrayList<>();
        Map<Long, String> processMap = getProcessMap(enterpriseId);
        canInvalidList.forEach(c -> {
            ApplyInvalidApproveVo invalid = new ApplyInvalidApproveVo();
            invalid.setApproveId(c.getId());
            invalid.setApproveNo(c.getApproveNo());
            invalid.setContractId(c.getContractId());
            invalid.setProcessId(c.getProcessId());
            invalid.setProcessCode(processMap.get(c.getProcessId()));
            invalid.setProcessName(c.getProcessName());
            invalid.setStartUserName(c.getCreateUserName());
            invalid.setStartDate(c.getCreatedDate());
            invalid.setSubject(c.getSubject());
            InvalidTypeEnum invalidTypeEnum = InvalidTypeEnum.getInvalidTypeEnumByProcessCode(invalid.getProcessCode());
            if (Objects.nonNull(invalidTypeEnum)) {
                invalid.setInvalidTypeCode(invalidTypeEnum.getInvalidTypeCode());
            }
            if (BasConstants.CC_PROCESS_List.contains(invalid.getProcessCode())) {
                invalid.setInvalidTypeCode(InvalidTypeEnum.CC.getInvalidTypeCode());
            }
            resultList.add(invalid);
        });
        return resultList;
    }

    private Map<Long, String> getProcessMap(Long enterpriseId) {
        List<PmProcess> processList = pmProcessDao.findByEnterpriseIdAndEnableFlgTrue(enterpriseId);
        return processList.stream().collect(Collectors.toMap(PmProcess::getId, PmProcess::getProcessCode, (a, b) -> b));
    }

    private List<String> filterInvalidType(List<Long> contractIdList, Long enterpriseId, Long budgetApproveId, String contractTailNo) {
        List<ApplyInvalidApproveVo> invalidApproveList = queryInvalidApproveList(contractIdList, enterpriseId, budgetApproveId, contractTailNo);
        return invalidApproveList.stream()
                .filter(t -> StringUtils.isNotBlank(t.getInvalidTypeCode()))
                .map(ApplyInvalidApproveVo::getInvalidTypeCode)
                .distinct()
                .collect(Collectors.toList());
    }

    private IPmApproveListener getInvalidListener(List<Long> invalidApproveIdList) {
        Long invalidApproveId = invalidApproveIdList.stream()
                .findAny()
                .orElse(null);
        if (Objects.isNull(invalidApproveId) || invalidApproveId == 0L) {
            return null;
        }
        PmApprove invalidApprove = pmApproveDao.findOne(invalidApproveId);
        PmProcess process = pmProcessDao.findOne(invalidApprove.getProcessId());
        IPmService pmService = SpringContextHolder.getBean(process.getEntityService());
        return getListenerService(process, pmService);
    }

    private List<Long> getInvalidApproveIdList(ApplyInvalid entity) {
        return Splitter.on(BasConstants.COMMA)
                .omitEmptyStrings()
                .splitToList(entity.getInvalidApproveIds())
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    public IPmApproveListener getListenerService(PmProcess process, IPmService pmService) {
        IPmApproveListener listener = null;
        if (pmService instanceof IPmApproveListener) {
            listener = (IPmApproveListener) pmService;
        }
        if (StringUtils.isNotBlank(process.getListenerService())) {
            listener = SpringContextHolder.getBean(process.getListenerService());
        }
        return listener;
    }

    private PmApproveWithdrawVo assembleWithdrawVo(PmApprove approve, Long invalidApproveId) {
        PmApproveWithdrawVo vo = new PmApproveWithdrawVo();
        PmApprove invalidApprove = pmApproveDao.findOne(invalidApproveId);
        vo.setBizId(invalidApprove.getBizId());
        vo.setUserId(approve.getCreateUserId());
        vo.setUserName(approve.getCreateUserName());
        vo.setApproveId(invalidApprove.getId());
        return vo;
    }
}
