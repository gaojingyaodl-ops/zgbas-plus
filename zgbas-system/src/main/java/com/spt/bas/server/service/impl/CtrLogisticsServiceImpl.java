package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.dao.logistics.CtrLogisticsDeliveryDao;
import com.spt.bas.server.dao.logistics.CtrLogisticsDriverDao;
import com.spt.bas.server.service.*;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
public class CtrLogisticsServiceImpl extends BaseService<CtrLogistics> implements ICtrLogisticsService{

    @Autowired
    private CtrLogisticsDao logisticsDao;
    @Autowired
    private CtrLogisticsDeliveryDao logisticsDeliveryDao;
    @Autowired
    private CtrLogisticsDriverDao logisticsDriverDao;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Resource
    private ApplyDcsxDao applyDcsxDao;
    @Resource
    private CtrContractChainDao ctrContractChainDao;
    @Resource
    private CtrContractDao ctrContractDao;
    
    @Override
    public BaseDao<CtrLogistics> getBaseDao() {
        return logisticsDao;
    }

    @Override
    public void saveLogistics(CtrLogisticsVo ctrLogisticsVo) {
        if(Objects.isNull(ctrLogisticsVo) || Objects.isNull(ctrLogisticsVo.getId())) {
            return;
        }
        CtrLogistics logistics = logisticsDao.findOne(ctrLogisticsVo.getId());
        if(StringUtils.isNotBlank(ctrLogisticsVo.getWarehouseName())){
            logistics.setWarehouseId(ctrLogisticsVo.getWarehouseId());
            logistics.setWarehouseName(ctrLogisticsVo.getWarehouseName());
        }
        logistics.setSupplierNo(ctrLogisticsVo.getSupplierNo());
        if(StringUtils.isNotBlank(ctrLogisticsVo.getTakeDelieveryAddr())){
            logistics.setTakeDelieveryAddr(ctrLogisticsVo.getTakeDelieveryAddr());
        }
        if(StringUtils.isNotBlank(ctrLogisticsVo.getReceiveDeliveryAddr())){
            logistics.setReceiveDeliveryAddr(ctrLogisticsVo.getReceiveDeliveryAddr());
        }
        if (StringUtils.isNotBlank(ctrLogisticsVo.getBuyDeliveryType())) {
            logistics.setBuyDeliveryType(ctrLogisticsVo.getBuyDeliveryType());
            ctrContractDao.updateDeliveryType(logistics.getBuyContractId(),ctrLogisticsVo.getBuyDeliveryType());
        }
        if (StringUtils.isNotBlank(ctrLogisticsVo.getSellDeliveryType())) {
            logistics.setSellDeliveryType(ctrLogisticsVo.getSellDeliveryType());
            ctrContractDao.updateDeliveryType(logistics.getSellContractId(),ctrLogisticsVo.getSellDeliveryType());
        }
        logistics.setLogisticsDistance(ctrLogisticsVo.getLogisticsDistance());
        logistics.setUpdatedDate(new Date());
        
        // 提货信息保存 begin
        List<CtrLogisticsDeliveryVo> logisticsDeliveryList = ctrLogisticsVo.getLogisticsDeliveryList();

        // 保存物流单据明细
        if (CollectionUtils.isNotEmpty(logisticsDeliveryList)) {
            logistics.setLogisticsDeliveryNum((long) logisticsDeliveryList.size());
        } else {
            logistics.setLogisticsDeliveryNum(0L);
        }
        logisticsDao.save(logistics);

        List<CtrLogisticsDelivery> deliveryDBList = logisticsDeliveryDao.findByLogisticsId(ctrLogisticsVo.getId());
        if(CollectionUtils.isNotEmpty(logisticsDeliveryList)) {
            if(CollectionUtils.isNotEmpty(deliveryDBList)) {
                // 处理删除逻辑
                List<Long> deliveryIdList = deliveryDBList.stream().map(CtrLogisticsDelivery::getId).collect(Collectors.toList());
                List<Long> deliveryVoIdList = logisticsDeliveryList.stream().map(CtrLogisticsDeliveryVo::getId).collect(Collectors.toList());
                List<Long> differentIdList = new ArrayList<>();
                differentIdList.addAll(deliveryIdList.stream().filter(e -> !deliveryVoIdList.contains(e)).collect(Collectors.toList()));
                if(CollectionUtils.isNotEmpty(differentIdList)) {
                    logisticsDeliveryDao.deleteAllById(deliveryIdList);   
                }
            }
            for (CtrLogisticsDeliveryVo deliveryVo : logisticsDeliveryList) {
                deliveryVo.setLogisticsId(ctrLogisticsVo.getId());
                // 保存提货明细
                CtrLogisticsDelivery deliveryDB = logisticsDeliveryDao.findByLogisticsIdAndLogisticsCount(ctrLogisticsVo.getId(), deliveryVo.getLogisticsCount());
                if(Objects.nonNull(deliveryDB)) {
                    deliveryVo.setId(deliveryDB.getId());
                }
                CtrLogisticsDelivery delivery = new CtrLogisticsDelivery();
                BeanUtils.copyProperties(deliveryVo,delivery);
                CtrLogisticsDelivery logisticsDelivery = logisticsDeliveryDao.save(delivery);

                
                // 车辆信息保存 begin
                List<CtrLogisticsDriver> driverListDB = logisticsDriverDao.findByLogisticsIdAndLogisticsDeliveryId(ctrLogisticsVo.getId(), logisticsDelivery.getId());
                List<CtrLogisticsDriver> driverList = deliveryVo.getCtrLogisticsDriverList();
                if(CollectionUtils.isNotEmpty(driverList)) {
                    if(CollectionUtils.isNotEmpty(driverListDB)) {
                        List<Long> driverDBIdList = driverListDB.stream().map(CtrLogisticsDriver::getId).collect(Collectors.toList());
                        List<Long> driverVoIdList = driverList.stream().map(CtrLogisticsDriver::getId).collect(Collectors.toList());
                        List<Long> differentIdList = new ArrayList<>();
                        differentIdList.addAll(driverDBIdList.stream().filter(e -> !driverVoIdList.contains(e)).collect(Collectors.toList()));
                        if(CollectionUtils.isNotEmpty(differentIdList)) {
                            logisticsDriverDao.deleteAllById(differentIdList);
                        }
                    }
                    
                    for (CtrLogisticsDriver driverVo : driverList) {
                        driverVo.setLogisticsId(ctrLogisticsVo.getId());
                        driverVo.setLogisticsDeliveryId(logisticsDelivery.getId());
                        // 保存车辆信息明细
                        CtrLogisticsDriver driver = new CtrLogisticsDriver();
                        BeanUtils.copyProperties(driverVo,driver);
                        logisticsDriverDao.save(driver);
                    }
                }


            }
        }


    }

    @Override
    public CtrLogistics addLogisticsParams(List<CtrContract> contractList,List<CtrContractChain> contractChainList,List<ApplyCtrDCSX> dcsxList){
        CtrLogistics ctrLogistics = null;
        String chainTradeChain = "";
        if(CollectionUtils.isNotEmpty(contractChainList)) {
            List<CtrContractChain> sortedChainList = contractChainList.stream()
                    .sorted(Comparator.comparing(CtrContractChain::getContractNo))
                    .collect(Collectors.toList());
            for (CtrContractChain chain : sortedChainList) {
                chainTradeChain += ">" + chain.getCompanyName();
            }
        }

        String chainTradeDcsx = "";
        if(CollectionUtils.isNotEmpty(dcsxList)) {
            List<ApplyCtrDCSX> filteredList = dcsxList.stream()
                    .filter(dcsx -> dcsx.getBusinessType().equals(BasConstants.BUSINESS_TYPE_ZY_BB_C))
                    .sorted(Comparator.comparing(ApplyCtrDCSX::getContractNo))
                    .collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(filteredList)) {
                for (ApplyCtrDCSX applyCtrDCSX : filteredList) {
                    chainTradeDcsx += ">" + applyCtrDCSX.getOurCompanyName();
                }
            }
        }

        if(CollectionUtils.isNotEmpty(contractList)) {
            ctrLogistics = new CtrLogistics();
            String buyCompanyName = "";
            String buyOurCompanyName = "";
            String sellCompanyName = "";
            String sellOurCompanyName = "";
            Boolean matchCreditFlg = true;
            String businessTypeDcsx = "";
            for (CtrContract contract : contractList) {
                if(StringUtils.endsWith(contract.getContractType(),"B")) {
                    buyCompanyName = contract.getCompanyName();
                    buyOurCompanyName = contract.getOurCompanyName();
                    matchCreditFlg = contract.getMatchCreditFlg();
                    businessTypeDcsx = contract.getBusinessTypeDcsx();
                    ctrLogistics.setLogisticsNo(contract.getContractNo().replaceAll("\\D", ""));
                    ctrLogistics.setDealNumber(contract.getTotalNumber());
                    ctrLogistics.setProductNames(contract.getProductsName());
                    ctrLogistics.setSupplierName(contract.getCompanyName());
                    ctrLogistics.setBuyContractId(contract.getId());
                    ctrLogistics.setBuyContractNo(contract.getContractNo());
                    ctrLogistics.setBuyDealPrice(contract.getDealPrice());
                    ctrLogistics.setBuyTotalAmount(contract.getTotalAmount());
                    ctrLogistics.setBuyDeliveryType(contract.getDeliveryType());
                    ctrLogistics.setBuyDeliveryDate(contract.getDeliveryDateTo());
                    ctrLogistics.setApproveId(contract.getApproveId());
                    ctrLogistics.setMatchCreditFlg(contract.getMatchCreditFlg());
                    ctrLogistics.setBuyOurCompanyName(contract.getOurCompanyName());
                } else if(StringUtils.endsWith(contract.getContractType(),"S")){
                    sellCompanyName = contract.getCompanyName();
                    sellOurCompanyName = contract.getOurCompanyName();
                    matchCreditFlg = contract.getMatchCreditFlg();
                    businessTypeDcsx = contract.getBusinessTypeDcsx();
                    ctrLogistics.setLogisticsNo(contract.getContractNo().replaceAll("\\D", ""));
                    ctrLogistics.setCompanyName(contract.getCompanyName());
                    ctrLogistics.setSellContractId(contract.getId());
                    ctrLogistics.setSellContractNo(contract.getContractNo());
                    ctrLogistics.setSellDealPrice(contract.getDealPrice());
                    ctrLogistics.setSellTotalAmount(contract.getTotalAmount());
                    ctrLogistics.setSellDeliveryType(contract.getDeliveryType());
                    ctrLogistics.setSellDeliveryDate(contract.getDeliveryDateTo());
                    ctrLogistics.setEnterpriseId(contract.getEnterpriseId());
                    ctrLogistics.setApproveId(contract.getApproveId());
                    ctrLogistics.setMatchCreditFlg(contract.getMatchCreditFlg());
                    ctrLogistics.setSellOurCompanyName(contract.getOurCompanyName());
                    ctrLogistics.setMatchUserId(contract.getMatchUserId());
                    ctrLogistics.setMatchUserName(contract.getMatchUserName());
                    SysUserSdk user = authOpenFacade.findUserById(contract.getMatchUserId());
                    if(Objects.nonNull(user)) {
                        // 业务员手机号
                        ctrLogistics.setMatchUserPhone(user.getPhonenumber());
                        ctrLogistics.setDeptId(user.getDeptId());
                    }
                }

            }
            String tradeChain = "";
            if(matchCreditFlg) {
                if(businessTypeDcsx.contains(BasConstants.DICT_TYPE_FILE_TYPE_DCSX)) {
                    // 代采赊销
                    tradeChain = buyCompanyName + ">" + buyOurCompanyName + chainTradeDcsx + ">" + sellOurCompanyName + ">" + sellCompanyName;
                } else {
                    // 赊销
                    tradeChain = buyCompanyName + ">" + buyOurCompanyName + ">" + sellCompanyName;
                }
            } else {
                // 代采
                tradeChain = buyCompanyName + ">" + buyOurCompanyName + chainTradeChain + ">" + sellCompanyName;
            }

            ctrLogistics.setTradeChain(tradeChain);
            ctrLogistics.setEnableFlg(true);
        }

        return ctrLogistics;
    }

    @Override
    @ServiceTransactional
    public CtrLogistics initLogistics(String contractNo) {
        CtrContract contract = ctrContractDao.findByContractNo(contractNo);
        if (Objects.isNull(contract)) {
            return null;
        }
        CtrLogistics entity = logisticsDao.findByContractId(contract.getId());
        if (Objects.nonNull(entity)) {
            return entity;
        }
        List<CtrContract> contractList = ctrContractDao.findByApproveId(contract.getApproveId());
        // 查询是否存在中间链条
        List<CtrContractChain> contractChainList = ctrContractChainDao.findByApproveId(contract.getApproveId());
        // 代采赊销中间链条
        List<ApplyCtrDCSX> dcsxList = applyDcsxDao.findByApproveId(contract.getApproveId());
        // 设置物流单据保存参数
        CtrLogistics ctrLogistics = this.addLogisticsParams(contractList, contractChainList, dcsxList);
        if (Objects.nonNull(ctrLogistics)) {
            // 添加物流单据
            logisticsDao.save(ctrLogistics);
        }
        return ctrLogistics;
    }

    @Override
    public List<CtrLogistics> findByLogisticsNo(String logisticsNo) {
        return logisticsDao.findByLogisticsNo(logisticsNo);
    }

    @Override
    public List<CtrLogistics> getByBuyContractNo(String buyContractNo) {
        return logisticsDao.findByBuyContractNo(buyContractNo);
    }

    @Override
    public List<CtrLogistics> getBySellContractNo(String sellContractNo) {
        return logisticsDao.findBySellContractNo(sellContractNo);
    }

    @Override
    @ServiceTransactional
    public void invalidLogistics(String contractNo) {
        logisticsDao.invalidLogistics(contractNo);
    }
}

