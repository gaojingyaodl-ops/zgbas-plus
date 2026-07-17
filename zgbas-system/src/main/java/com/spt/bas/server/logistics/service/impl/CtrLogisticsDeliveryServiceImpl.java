package com.spt.bas.server.logistics.service.impl;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDictDataSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.LogisticsEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.CtrLogisticsReqVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.dao.CtrLogisticsDao;
import com.spt.bas.server.dao.logistics.CtrLogisticsDeliveryDao;
import com.spt.bas.server.dao.logistics.CtrLogisticsDriverDao;
import com.spt.bas.server.logistics.service.ICtrLogisticsDeliveryService;
import com.spt.bas.server.logistics.service.ICtrLogisticsFileService;
import com.spt.bas.server.service.IApplyDcsxService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.sign.client.remote.ICfcaLogisticsClient;
import com.spt.sign.client.vo.AxqLogisticsVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 合同物流提货
 *
 * @Author MoonLight
 * @Date 2023/7/5 16:23
 * @Version 1.0
 */
@Component
public class CtrLogisticsDeliveryServiceImpl extends BaseService<CtrLogisticsDelivery> implements ICtrLogisticsDeliveryService {
    @Resource
    private CtrLogisticsDao ctrLogisticsDao;
    @Resource
    private CtrLogisticsDeliveryDao ctrLogisticsDeliveryDao;
    @Resource
    private CtrLogisticsDriverDao ctrLogisticsDriverDao;
    @Resource
    private ICfcaLogisticsClient cfcaLogisticsClient;
    @Resource
    private ICtrLogisticsFileService ctrLogisticsFileService;
    @Resource
    private PmProcessDao pmProcessDao;
    @Resource
    private IPmApproveService pmApproveService;
    @Resource
    private IAuthOpenFacade authOpenFacade;
    @Resource
    private IApplyDcsxService appplyDcsxService;
    @Resource
    private ICtrContractService ctrContractService;
    @Resource
    private BsCompanyDao bsCompanyDao;

    @Override
    public BaseDao<CtrLogisticsDelivery> getBaseDao() {
        return ctrLogisticsDeliveryDao;
    }

    /**
     * 创建指定类型物流单据-PDF(未签署版本)
     *
     * @param logisticsDeliveryId
     * @param logisticsEnum
     */
    @Override
    @ServerTransactional
    public boolean generateLogisticsPdfFile(Long logisticsDeliveryId, LogisticsEnum logisticsEnum) {
        // 合同物流提货信息
        CtrLogisticsDelivery delivery = ctrLogisticsDeliveryDao.findOne(logisticsDeliveryId);
        if (Objects.isNull(delivery) || Objects.isNull(delivery.getLogisticsId())) {
            logger.error("generateLogisticsPdfFile error, ctrLogisticsDelivery or logisticsId lose !");
            return false;
        }
        // 合同物流单信息
        CtrLogistics logistics = ctrLogisticsDao.findOne(delivery.getLogisticsId());
        if (Objects.isNull(logistics)) {
            logger.error("generateLogisticsPdfFile error, logistics lose !");
            return false;
        }
        // 合同物流提货司机信息
        List<CtrLogisticsDriver> driverList = ctrLogisticsDriverDao.findByLogisticsDeliveryId(logisticsDeliveryId);
        if (CollectionUtils.isEmpty(driverList)) {
            logger.error("generateLogisticsPdfFile error, logisticsDriver lose !");
            return false;
        }
        // 创建Cfca物流提货单据
        try {
            AxqLogisticsVo axqLogisticsVo = compositeRequestParam(logistics, delivery, driverList, logisticsEnum);
            axqLogisticsVo = cfcaLogisticsClient.axqLogistics(axqLogisticsVo);
            if (Boolean.TRUE.equals(axqLogisticsVo.getSuccessFlg())) {
                String cfcaContractNo = axqLogisticsVo.getCfcaContractNo();
                String cfcaFileId = axqLogisticsVo.getCfcaFileId();
                // 保存物流提货单据PDF附件ID
                CtrLogisticsFile logisticsFile = new CtrLogisticsFile(delivery, logisticsEnum, cfcaFileId, logistics.getLogisticsNo(), cfcaContractNo);
                ctrLogisticsFileService.saveLogisticsFile(logisticsFile);
                return true;
            }
        } catch (Exception e) {
            logger.error("generateLogisticsPdfFile error", e);
        }
        return false;
    }

    /**
     * 生成出入库单据
     *
     * @param reqVo
     * @return
     */
    @Override
    @ServerTransactional
    public CtrLogisticsFile generateDeliveryFile(CtrLogisticsReqVo reqVo) throws ApplicationException {
        CtrLogistics ctrLogistics = this.verifyParams(reqVo);
        CtrLogisticsFile logisticsFile = null;
        LogisticsEnum logisticsEnum = reqVo.getLogisticsEnum();
        reqVo.setLogistics(ctrLogistics);
        AxqLogisticsVo vo = new AxqLogisticsVo();
        setLogisticsParam(reqVo);
        reqVo = logisticsEnum.compositeLogistics(reqVo);
        Map<String, String> paramMap = reqVo.getParamMap();
        vo.setParamMap(paramMap);
        vo.setOurCompanyName(reqVo.getSignCompanyName());
        vo.setLogisticsNo(reqVo.getLogisticsNo());
        vo.setTemplateCode(logisticsEnum.getLogisticsTemplate());
        vo.setContractNo(reqVo.getContractNo());
        try {
            vo = cfcaLogisticsClient.axqLogistics(vo);
            if (Boolean.TRUE.equals(vo.getSuccessFlg())) {
                String cfcaContractNo = vo.getCfcaContractNo();
                String cfcaFileId = vo.getCfcaFileId();
                // 保存物流提货单据PDF附件ID
                logisticsFile = new CtrLogisticsFile(ctrLogistics.getId(), cfcaFileId, cfcaContractNo, reqVo);
                logisticsFile = ctrLogisticsFileService.saveLogisticsFile(logisticsFile);
            }
        } catch (Exception e) {
            logger.error("generateDeliveryFile error", e);
        }
        return logisticsFile;
    }

    /**
     * 生成配送、提货单据并发起盖章申请
     * 1。上家配送-【配送单、资方配送单】
     * 2. 我司配送-【提货单、资方提货单、送货通知单】
     *
     * @param reqVo
     * @return
     * @throws ApplicationException
     */
    @Override
    @ServerTransactional
    public CtrLogisticsFile generateLogisticsSealUsage(CtrLogisticsReqVo reqVo) throws ApplicationException {
        CtrLogistics ctrLogistics = this.verifyParams(reqVo);
        CtrLogisticsFile logisticsFile = null;
        LogisticsEnum logisticsEnum = reqVo.getLogisticsEnum();
        reqVo.setLogistics(ctrLogistics);
        AxqLogisticsVo vo = new AxqLogisticsVo();
        setLogisticsParam(reqVo);
        CtrLogisticsReqVo ctrLogisticsReqVo = logisticsEnum.compositeLogistics(reqVo);
        Map<String, String> paramMap = ctrLogisticsReqVo.getParamMap();
        vo.setParamMap(paramMap);
        vo.setOurCompanyName(reqVo.getSignCompanyName());
        vo.setLogisticsNo(reqVo.getLogisticsNo());
        vo.setTemplateCode(logisticsEnum.getLogisticsTemplate());
        vo.setContractNo(StringUtils.isNotBlank(ctrLogistics.getSupplierNo()) ? ctrLogistics.getSupplierNo() : ctrLogistics.getBuyContractNo());
        try {
            vo = cfcaLogisticsClient.axqLogistics(vo);
            if (Boolean.TRUE.equals(vo.getSuccessFlg())) {
                String cfcaContractNo = vo.getCfcaContractNo();
                String cfcaFileId = vo.getCfcaFileId();
                // 保存物流提货单据PDF附件ID
                logisticsFile = new CtrLogisticsFile(reqVo.getDelivery(), cfcaFileId, cfcaContractNo, reqVo);
                logisticsFile = ctrLogisticsFileService.saveLogisticsFile(logisticsFile);

                PmApprove pmApprove = autoInitiatedSealUsage(ctrLogistics, logisticsFile, reqVo);
                logisticsFile.setApproveId(pmApprove.getId());
                logisticsFile.setApproveNo(pmApprove.getApproveNo());
                logisticsFile.setContractNo(reqVo.getContractNo());
                ctrLogisticsFileService.saveLogisticsFile(logisticsFile);
            }
        } catch (Exception e) {
            logger.error("generateDeliveryFile error", e);
        }
        return logisticsFile;
    }

    @Override
    @ServerTransactional
    public CtrLogisticsFile successLogisticsPdfFile(String ourCompanyName, String sealType, Long logisticsFileId, Long approveId, String approveNo) {
        logger.info("ourCompanyName:{}, sealType:{}, logisticsFileId:{},approveNo:{}", ourCompanyName, sealType, logisticsFileId, approveNo);
        CtrLogisticsFile entity = ctrLogisticsFileService.getEntity(logisticsFileId);
        if (Objects.isNull(entity)) {
            return null;
        }
        String oldFileId = entity.getFileId();
        try {
            AxqLogisticsVo vo = new AxqLogisticsVo();
            vo.setCfcaContractNo(entity.getCfcaContractNo());
            vo.setOurCompanyName(StringUtils.isNotBlank(entity.getSignCompanyName()) ? entity.getSignCompanyName(): ourCompanyName);
            vo.setSignType(StringUtils.isNotBlank(entity.getSealType()) ? entity.getSealType(): sealType);
            vo.setSignLocation("buyerSignLocation");
            vo = cfcaLogisticsClient.axqSignLogistics(vo);
            logger.info("approveNo:{}, signFlg:{}", approveNo, vo.getSuccessFlg());
            if (Boolean.TRUE.equals(vo.getSuccessFlg())) {
                String cfcaFileId = vo.getCfcaFileId();
                cfcaFileId = cfcaFileId.endsWith(BasConstants.COMMA) ? cfcaFileId : cfcaFileId + BasConstants.COMMA;
                entity.setFileId(cfcaFileId);
                entity.setOldFileId(oldFileId);
                entity.setApproveNo(approveNo);
                entity.setApproveId(approveId);
                entity.setSignFlg(true);
                entity = ctrLogisticsFileService.save(entity);
            }
        } catch (Exception e) {
            logger.error("successLogisticsPdfFile error", e);
        }
        return entity;
    }

    @Override
    public Map<String, String> exportExcelTemplate(CtrLogisticsReqVo reqVo) throws ApplicationException {
        CtrLogistics ctrLogistics = this.verifyParams(reqVo);
        LogisticsEnum logisticsEnum = reqVo.getLogisticsEnum();
        reqVo.setLogistics(ctrLogistics);
        setLogisticsParam(reqVo);
        CtrLogisticsReqVo ctrLogisticsReqVo = logisticsEnum.compositeLogistics(reqVo);
        return ctrLogisticsReqVo.getParamMap();
    }

    @Override
    public CtrLogisticsDelivery findByLogisticsIdAndLogisticsCount(Long logisticsId, String logisticsCount) {
        return ctrLogisticsDeliveryDao.findByLogisticsIdAndLogisticsCount(logisticsId, logisticsCount);
    }

    @Override
    public List<CtrLogisticsDelivery> findByLogisticsId(Long logisticsId) {
        return ctrLogisticsDeliveryDao.findByLogisticsId(logisticsId);
    }

    private CtrLogistics verifyParams(CtrLogisticsReqVo reqVo) throws ApplicationException {
        Long contractId = reqVo.getContractId();
        if (Objects.isNull(contractId)) {
            throw new ApplicationException("参数缺失，无法生成单据!");
        }
        CtrLogistics ctrLogistics = ctrLogisticsDao.findByContractId(contractId);
        if (Objects.isNull(ctrLogistics)) {
            throw new ApplicationException("物流单据数据缺失，无法生成单据!");
        }
        return ctrLogistics;
    }

    private CtrLogisticsReqVo setLogisticsParam(CtrLogisticsReqVo reqVo) throws ApplicationException {
        CtrLogistics ctrLogistics = reqVo.getLogistics();
        CtrLogisticsDelivery logisticsDelivery = ctrLogisticsDeliveryDao.findOne(reqVo.getLogisticsDeliveryId());
        List<CtrLogisticsDriver> driverList = ctrLogisticsDriverDao.findByLogisticsDeliveryId(reqVo.getLogisticsDeliveryId());
        reqVo.setDelivery(logisticsDelivery);
        reqVo.setDriverList(driverList);
        if (Objects.isNull(reqVo.getCurrNumber())){
            reqVo.setCurrNumber(logisticsDelivery.getLogisticsNumber());
        }
        if (Boolean.TRUE.equals(reqVo.getFundFlg())) {
            CtrContract contract = ctrContractService.getEntity(reqVo.getContractId());
            ApplyCtrDCSX applyCtrDcsx = appplyDcsxService.findByDCSXApproveId(contract.getApproveId());
            if (Objects.isNull(applyCtrDcsx)) {
                throw new ApplicationException("参数缺失，无法生成单据!");
            }
            reqVo.setApplyCtrDCSX(applyCtrDcsx);
        }
        SysUserSdk sysUser = authOpenFacade.findUserById(reqVo.getBizUserId());
        reqVo.setBizUserPhone(Objects.nonNull(sysUser) ? sysUser.getPhonenumber() : "");
        BsCompany bsCompany = bsCompanyDao.findByCompanyName(ctrLogistics.getSupplierName()).stream().filter(BsCompany::getEnableFlg).findFirst().orElse(null);
        if (Objects.nonNull(bsCompany) && Objects.nonNull(bsCompany.getLogisticsSealType())){
            reqVo.setSealType(bsCompany.getLogisticsSealType());
        }
        return reqVo;
    }

    private AxqLogisticsVo compositeRequestParam(CtrLogistics logistics, CtrLogisticsDelivery delivery, List<CtrLogisticsDriver> driverList, LogisticsEnum logisticsEnum) {
        AxqLogisticsVo vo = new AxqLogisticsVo();
        CtrLogisticsReqVo reqVo = logisticsEnum.compositeLogistics(new CtrLogisticsReqVo(logistics, delivery, driverList));
        vo.setParamMap(reqVo.getParamMap());
        vo.setOurCompanyName(reqVo.getSignCompanyName());
        vo.setCompanyName(reqVo.getCompanyName());
        vo.setTemplateCode(logisticsEnum.getLogisticsTemplate());
        vo.setContractNo(StringUtils.isNotBlank(logistics.getSupplierNo()) ? logistics.getSupplierNo() : reqVo.getLogisticsNo());
        vo.setLogisticsNo(reqVo.getLogisticsNo());
        return vo;
    }

    private PmApprove autoInitiatedSealUsage(CtrLogistics logistics, CtrLogisticsFile logisticsFile, CtrLogisticsReqVo reqVo) throws ApplicationException {
        PmProcess sealUsageProcess = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_SEAL_USAGE, logistics.getEnterpriseId());
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        SealUsage usage = new SealUsage();
        usage.setApplyUserId(reqVo.getBizUserId());
        usage.setApplyUserName(reqVo.getBizUserName());
        List<SysDictDataSdk> sysDictDataSdkList = DictUtil.getListByCategory(BasConstants.DICT_TYPE_CUSTOMER_NAME);
        for (SysDictDataSdk entity : sysDictDataSdkList) {
            if (StringUtils.equals(entity.getDictName(), reqVo.getSignCompanyName())) {
                usage.setCompanyName(entity.getDictCd());
                break;
            }
        }
        usage.setCustomerName(reqVo.getCompanyName());
        usage.setTotalAmount(logistics.getSellDealPrice().multiply(reqVo.getCurrNumber()).setScale(2, RoundingMode.HALF_UP));
        usage.setSealType("WS");
        usage.setSealDate(new Date());
        usage.setContractNo(reqVo.getContractNo());
        usage.setFileType("TH");
        usage.setFileName(reqVo.getLogisticsEnum().getLogisticsName());
        usage.setStampType("DZQ");
        usage.setFileId(logisticsFile.getFileId());
        usage.setLogisticsFileId(logisticsFile.getId());
        usage.setRemark("从物流单据发起盖章申请！");

        String entityJson = JsonUtil.obj2Json(usage);
        startVo.setBizEntityJson(entityJson);
        startVo.setProcessId(sealUsageProcess.getId());
        startVo.setApproveId(0L);
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setUserId(reqVo.getBizUserId());
        startVo.setUserName(reqVo.getBizUserName());
        startVo.setEnterpriseId(logistics.getEnterpriseId());
        startVo.setAutoStartMessage("从物流单据发起盖章申请！");
        startVo.setAutoStartFlgReal(true);
        return pmApproveService.startFlow(startVo);
    }
}
