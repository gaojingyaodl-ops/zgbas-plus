package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractLoading;
import com.spt.bas.client.entity.CtrContractLoadingDetail;
import com.spt.bas.server.dao.CtrContractLoadingDao;
import com.spt.bas.server.service.ICtrContractLoadingService;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmApproveSearchVo;
import com.spt.sign.client.entity.SignContract;
import com.spt.sign.client.remote.ICfcaSignClient;
import com.spt.sign.client.vo.AxqContractVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 提货单
 *
 * @Author: gaojy
 * @create 2022/3/16 9:29
 * @version: 1.0
 * @description:
 */
@Component
@Transactional(readOnly = true)
public class CtrContractLoadingServiceImpl extends BaseService<CtrContractLoading> implements ICtrContractLoadingService {

    @Autowired
    private CtrContractLoadingDao ctrContractLoadingDao;
    @Autowired
    private ICfcaSignClient cfcaSignClient;
    @Autowired
    private PmApproveDao pmApproveDao;
    @Autowired
    private PmProcessDao pmProcessDao;

    @Override
    public BaseDao<CtrContractLoading> getBaseDao() {
        return ctrContractLoadingDao;
    }

    /**
     * 创建提货单电子合同
     * @param loadingId
     * @return
     */
    @Override
    @ServiceTransactional
    public CtrContractLoading axqLoadingBill(Long loadingId) throws ApplicationException {
        CtrContractLoading ctrContractLoading = ctrContractLoadingDao.findOne(loadingId);
        String sealUsageApproveNo = ctrContractLoading.getSealUsageApproveNo();
        if (!StringUtils.equals(BasConstants.COMPANY_NAME_FTK, ctrContractLoading.getOurCompanyName())){
            if (StringUtils.isBlank(sealUsageApproveNo)){
                throw new ApplicationException("请先补充关联盖章审批单号!");
            }
            PmApprove pmApprove = pmApproveDao.findByApproveNo(sealUsageApproveNo);
            if (Objects.isNull(pmApprove)){
                throw new ApplicationException("无效的盖章审批单号!");
            }
//            if (!StringUtils.equals(BasConstants.APPROVE_STATUS_D, pmApprove.getStatus())){
//                throw new ApplicationException("该盖章审批还未审批完!");
//            }
        }
        AxqContractVo axqContractVo = this.convertLoadingBill(ctrContractLoading);
        logger.info("axqLoadingBill axqContractVo:{}", JsonUtil.obj2Json(axqContractVo));
        axqContractVo = cfcaSignClient.axqLoadingBill(axqContractVo);
        if (StringUtils.equals("60000000", axqContractVo.getRetCode())) {
            ctrContractLoading.setCfcaContractNo(axqContractVo.getContractNo());
            ctrContractLoading.setShortUrl(axqContractVo.getShortUrl());
            return ctrContractLoadingDao.save(ctrContractLoading);
        } else {
            throw new ApplicationException(axqContractVo.getRetMessage());
        }
    }

    /**
     * 刷新提货单状态
     * @param loadingId
     * @return
     */
    @Override
    @ServiceTransactional
    public CtrContractLoading refreshLoadingBillStatus(Long loadingId) {
        CtrContractLoading load = ctrContractLoadingDao.findOne(loadingId);
        if (Objects.isNull(load) || StringUtils.isBlank(load.getCfcaContractNo())) {
            return load;
        }
        String cfcaContractNo = load.getCfcaContractNo();
        try {
            Map map = cfcaSignClient.successContract(cfcaContractNo);
            logger.info("refreshLoadingBillStatus,retMap:{}", map);
            String retCode = (String) map.get("retCode");
            if (!StringUtils.equals("60000000", retCode)) {
                return load;
            }
            SignContract signContract = cfcaSignClient.findByContractNo(cfcaContractNo);
            if (Objects.nonNull(signContract) && StringUtils.equals("1", signContract.getContractState())) {
                String fileId = signContract.getDownloadPath();
                if (Objects.isNull(fileId)) {
                    Map contractFileId = cfcaSignClient.getContractFileId(cfcaContractNo);
                    fileId = (String) contractFileId.get("fileId");
                    if (StringUtils.isNotBlank(fileId)) {
                        load.setFileId(fileId);
                        load.setSignDate(signContract.getUpdatedDate());
                        return ctrContractLoadingDao.save(load);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("refreshLoadingBillStatus error", e);
        }
        return load;
    }

    @Override
    @ServiceTransactional
    public CtrContractLoading refreshLoadingBillByContractNo(String contractNo) {
        CtrContractLoading ctrContractLoading = ctrContractLoadingDao.findByCfcaContractNo(contractNo);
        if (Objects.isNull(ctrContractLoading)){
            return null;
        }
        return refreshLoadingBillStatus(ctrContractLoading.getId());
    }

    @Override
    public List<PmApprove> findSealUsageApprove(PmApproveSearchVo searchVo) {
        PmProcess sealUsage = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_SEAL_USAGE, searchVo.getEnterpriseId());
        PmProcess businessSealUsage = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_SEAL_USAGE_BUSINESS, searchVo.getEnterpriseId());
        PmProcess sealUsageDcsx = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.APPLY_SEAL_USAGE_DCSX, searchVo.getEnterpriseId());
        PmProcess sealUsageDcTp = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.APPLY_SEAL_USAGE_DCTP, searchVo.getEnterpriseId());
        Map<String, Object> searchParam = new HashMap<>();
        searchParam.put("INS_processId", new String[]{sealUsage.getId().toString(), businessSealUsage.getId().toString(), sealUsageDcsx.getId().toString(), sealUsageDcTp.getId().toString()});
        searchParam.put("LIKES_approveNo", searchVo.getSearchKey());
        searchParam.put("INS_status", new String[]{BasConstants.APPROVE_STATUS_A, BasConstants.APPROVE_STATUS_D});
        Specification<PmApprove> spec = WebUtil.buildSpecification(searchParam);
        PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), Sort.by(Sort.Direction.DESC, "id"));
        Page<PmApprove> page = pmApproveDao.findAll(spec, pageRequest);
        if (CollectionUtils.isNotEmpty(page.getContent())) {
            return page.getContent();
        }
        return null;
    }

    private AxqContractVo convertLoadingBill(CtrContractLoading load) throws ApplicationException{
        AxqContractVo axqContractVo = new AxqContractVo();
        String billType = load.getBillType();
        String templateCode;
        if (StringUtils.equals(BasConstants.LOADING_BILL_TYPE_T,billType)){
            // 生成提货单
            templateCode = BsDictUtil.getValue(load.getEnterpriseId(), BasConstants.LOADING_OUR_COMPANY_TEMPLATE, load.getOurCompanyName());
        }else{
            // 生成配送单
            templateCode = BsDictUtil.getValue(load.getEnterpriseId(), BasConstants.LOADING_DELIVERY_OUR_COMPANY_TEMPLATE, load.getOurCompanyName());
        }
        if (StringUtils.isBlank(templateCode)) {
            throw new ApplicationException(load.getOurCompanyName() + "：合同模板缺失!");
        }
        axqContractVo.setTemplateId(templateCode);
        String contractNo = load.getContractNo();
        axqContractVo.setOrderNo(load.getDeliveryNo());
        axqContractVo.setContractNo(contractNo);
        axqContractVo.setOurCompanyName(load.getOurCompanyName());
        axqContractVo.setCompanyName(load.getCompanyName());
        axqContractVo.setProductName(load.getProductName());
        axqContractVo.setBrandNumber(load.getBrandNumber());
        axqContractVo.setFactoryName(load.getFactoryName());
        axqContractVo.setDealNumber(String.valueOf(load.getDealNumber()));
        axqContractVo.setNumberUnit(load.getNumberUnit());
        axqContractVo.setRemark(load.getRemark());
        axqContractVo.setDriverName(load.getDriverName());
        axqContractVo.setPlateNumber(load.getPlateNumber());
        axqContractVo.setDriverCardNo(load.getDriverCardNo());
        axqContractVo.setContactAddr(load.getContactAddress());
        axqContractVo.setContactName(load.getContactName());
        axqContractVo.setContactPhone(StringUtils.isBlank(load.getDriverPhone()) ? load.getContactPhone() : load.getDriverPhone());
        axqContractVo.setDeliveryDateStr(DateUtil.format(load.getLoadingDate(), DatePattern.CHINESE_DATE_PATTERN));
        axqContractVo.setAppCode(BasConstants.APP_CODE);
        axqContractVo.setBuyerSignType(BasConstants.CONTRACT_SEAL_TYPE_UK_SIGN);
        axqContractVo.setSellerSignType(BasConstants.CONTRACT_SEAL_TYPE_UK_SIGN);
        axqContractVo.setBuyerIsCheckProjectCode(BasConstants.CONTRACT_SEAL_NOT_SEND_PWD);
        axqContractVo.setSellerIsCheckProjectCode(BasConstants.CONTRACT_SEAL_NOT_SEND_PWD);
        axqContractVo.setSignLocation(BasConstants.CONTRACT_SIGN_LOCATION);
        axqContractVo.setBuyerSignLocation(BasConstants.CONTRACT_BUYER_SIGN_LOCATION);
        axqContractVo.setSellerSignLocation(BasConstants.CONTRACT_SELLER_SIGN_LOCATION);
        axqContractVo.setBuyerLocation("10.2.2.3");
        axqContractVo.setSellerLocation("11.22.33.66");
        Map<String, String> paramMap = new HashMap<>();
        List<CtrContractLoadingDetail> loadingDetails = load.getLoadingDetails();
        for (int i = 0; i < loadingDetails.size(); i++) {
            String index = i == 0 ? "" : String.valueOf(i);
            paramMap.put("productName" + index, loadingDetails.get(i).getProductName());
            paramMap.put("factoryName" + index, loadingDetails.get(i).getFactoryName());
            paramMap.put("brandNumber" + index, loadingDetails.get(i).getBrandNumber());
            paramMap.put("dealNumber" + index, loadingDetails.get(i).getDealNumber().setScale(4)+ "");
            paramMap.put("numberUnit" + index, loadingDetails.get(i).getNumberUnit());
            paramMap.put("plateNumber" + index, loadingDetails.get(i).getPlateNumber());
            paramMap.put("driverName" + index, loadingDetails.get(i).getDriverName());
            paramMap.put("driverCardNo" + index, loadingDetails.get(i).getDriverCardNo());
        }
        axqContractVo.setLoadingMap(paramMap);
        return axqContractVo;
    }
}
