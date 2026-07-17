package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.client.entity.ApplyLogisticsAdjust;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.ApplyLogisticsAdjustVo;
import com.spt.bas.client.vo.ApplyProductDetailSaveVo;
import com.spt.bas.server.dao.ApplyDeliveryOutDao;
import com.spt.bas.server.dao.ApplyLogisticsAdjustDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveHistory;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveHistoryService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component("applyLogisticsAdjustService")
@Transactional
public class ApplyLogisticsAdjustServiceImpl extends BaseService<ApplyLogisticsAdjust> implements IApplyLogisticsAdjustService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyLogisticsAdjustDao applyLogisticsAdjustDao;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private IPmApproveHistoryService pmApprovevHistoryService;
    @Autowired
    private ICtrContractOphisService ctrContractOphisService;
    @Autowired
    private ApplyDeliveryOutDao applyDeliveryOutDao;
    @Autowired
    private IApplyProductDetailService productDetailService;
    @Override
    public BaseDao<ApplyLogisticsAdjust> getBaseDao() {
        return applyLogisticsAdjustDao;
    }
    @Override
    public Class<ApplyLogisticsAdjust> getEntityClazz() {
        return ApplyLogisticsAdjust.class;
    }
    
    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyLogisticsAdjust entity = applyLogisticsAdjustDao.findOne(approve.getBizId());
            Long contractId = entity.getContractId();
            Long deliveryOutId = entity.getDeliveryOutId();
            CtrContract contract = ctrContractService.getEntity(contractId);
            if (contract != null) {
                Long approveId = contract.getApproveId();
                List<PmApproveHistory> historys = pmApprovevHistoryService.findByApproveId(approve.getId());
                if (!historys.isEmpty()) {
                    PmApproveHistory pmApproveHistory = historys.get(historys.size() - 1);
                    pmApproveHistory.setApproveRemark("物流调整");
                    pmApproveHistory.setApproveId(approveId);
                    pmApprovevHistoryService.save(pmApproveHistory);
                }
                BigDecimal stevedorage2 = entity.getStevedorage2();
                BigDecimal transportCost2 = entity.getTransportCost2();
                BigDecimal warehouseCost2 = entity.getWarehouseCost2();
                BigDecimal otherFee2 = entity.getOtherFee2();
                contract.setCarrier(entity.getCarrier());
                
                if(deliveryOutId != null) {
                    BigDecimal stevedorage = entity.getStevedorage();
                    BigDecimal transportCost = entity.getTransportCost();
                    BigDecimal warehouseCost = entity.getWarehouseCost();

                    ApplyDeliveryOut deliveryOut = applyDeliveryOutDao.findOne(deliveryOutId);
                    Boolean deliveryOutUpdateFlg = true;
                    if(Objects.isNull(deliveryOut)) {
                        deliveryOutUpdateFlg = false;
                        deliveryOut = new ApplyDeliveryOut();
                    }

                    deliveryOut.setCarrier(entity.getCarrier());
                    if(stevedorage2 != null) {
                        contract.setStevedorage(stevedorage2.subtract(stevedorage).add(contract.getStevedorage()));
                        deliveryOut.setStevedorage(stevedorage2);
                    }
                    if(transportCost2 != null) {
                        contract.setTransportAmount(transportCost2.subtract(transportCost).add(contract.getTransportAmount()));
                        deliveryOut.setTransportAmount(transportCost2);
                    }
                    if(warehouseCost2 != null) {
                        contract.setWarehouseAmount(warehouseCost2.subtract(warehouseCost).add(contract.getWarehouseAmount()));
                        deliveryOut.setWarehouseAmount(warehouseCost2);
                    }
                    if(otherFee2 != null) {
                        deliveryOut.setOtherFee(otherFee2);
                    }
                    
                    // 修改出库申请单
                    if(deliveryOutUpdateFlg) {
                        applyDeliveryOutDao.save(deliveryOut);
                    }
                    
                } else {
                    // 历史审批单保留逻辑
                    if(stevedorage2 != null) {
                        contract.setStevedorage(stevedorage2);
                    }
                    if(transportCost2 != null) {
                        contract.setTransportAmount(transportCost2);
                    }
                    if(warehouseCost2 != null) {
                        contract.setWarehouseAmount(warehouseCost2);
                    }
                }

                // 修改合同表数据
                ctrContractService.save(contract);

                ctrContractOphisService.addHis(BasConstants.APPLY_TYPE_LA, contract.getId(), approve, new Date());
                
            }
        }
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        ApplyLogisticsAdjust applyLogisticsAdjust;
        ApplyProductDetailSaveVo saveVo = new ApplyProductDetailSaveVo();
        saveVo.setApplyType(BasConstants.APPLY_TYPE_LA);
        saveVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        if(pmEntity instanceof ApplyLogisticsAdjustVo) {
            ApplyLogisticsAdjustVo vo = (ApplyLogisticsAdjustVo) pmEntity;
            applyLogisticsAdjust = new ApplyLogisticsAdjust();
            BeanUtils.copyProperties(vo,applyLogisticsAdjust);

            ///物流调整申请选着货品时没有可选着库存信息
            for (ApplyProductDetail detail : vo.getLstInsert()) {
                if (!checkIsExistDelivery(detail.getApplyDeliveryOutId())) {
                    throw new ApplicationException("出库校验出错，请重新选择可物流调整的库存信息");
                }
            }
            
            applyLogisticsAdjust = applyLogisticsAdjustDao.save(applyLogisticsAdjust);
            saveVo.setApplyId(applyLogisticsAdjust.getId());
            productDetailService.saveDetailBatch(vo.getLstInsert(), vo.getLstUpdate(), vo.getLstDelete(), saveVo);
            
        } else {
            ApplyLogisticsAdjust entity = (ApplyLogisticsAdjust) pmEntity;
            applyLogisticsAdjust = applyLogisticsAdjustDao.save(entity);
            saveVo.setApplyId(entity.getId());
            productDetailService.saveBatchEnterpriseId(saveVo);
        }
        return applyLogisticsAdjust;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        if (pmEntity != null) {
            ApplyLogisticsAdjust entity = (ApplyLogisticsAdjust) pmEntity;
            String sellContractNo = entity.getContractNo();
            String productsName = entity.getProductsName();
            String carrier = entity.getCarrier();
            String companyName = entity.getCompanyName();
            String companyName1 = RuleUtil.companyNameSubString(companyName);
            String subject = SubjectUtil.formatSubject(sellContractNo,companyName1,carrier,entity.getRemark());
            return subject;
        }
        return null;
    }

    /**
     * 校验是否出库是否正常
     *
     * @param applyDeliveryOutId
     *
     * @return false 不正常 true 正常
     */
    private Boolean checkIsExistDelivery(Long applyDeliveryOutId) {
        ApplyDeliveryOut entity = applyDeliveryOutDao.findOne(applyDeliveryOutId);
        if (entity == null
                || !BasConstants.APPROVE_STATUS_D.equals(entity.getStatus())) {
            return false;
        }
        return true;
    }

    
}
