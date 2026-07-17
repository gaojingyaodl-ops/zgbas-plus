//package com.spt.bas.server.service.impl;
//
//
//import com.spt.auth.sdk.cache.DictUtil;
//import com.spt.bas.client.constant.BasConstants;
//import com.spt.bas.client.entity.ApplyMatch;
//import com.spt.bas.client.entity.CtrContract;
//import com.spt.bas.client.entity.SyncData;
//import com.spt.bas.server.annotation.ServerTransactional;
//import com.spt.bas.server.dao.CtrContractDao;
//import com.spt.bas.server.dao.SyncDataDao;
//import com.spt.bas.server.service.ICtrContractService;
//import com.spt.sign.client.remote.ICfcaSignClient;
//import com.spt.sign.client.vo.AxqContractVo;
//import com.spt.sign.client.vo.CtrProductVo;
//import com.spt.tools.core.json.JsonUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 安心签接口
// */
//
//@Component("AxqService")
//@Transactional(readOnly = true)
//public class AxqServiceImpl implements IAxqService {
//    private Logger logger = LoggerFactory.getLogger(getClass());
//    @Autowired
//    private ICtrContractService ctrContractService;
//
//    @Autowired
//    private SyncDataDao syncDataDao;
//
//    @Autowired
//    private CtrContractDao contractDao;
//
//    @Resource
//    private ICfcaSignClient cfcaSignClient;
//
//    /**
//     * 采购合同安心签合同生成
//     *
//     * @param
//     */
//    @Override
//    @ServerTransactional
//    public void createBuyContractAxq(ApplyMatch applyMatch) {
//        if (StringUtils.isNotBlank(applyMatch.getFileId())) {
//            return;
//        }
//        List<CtrContract> byApproveId = ctrContractService.findByApproveId(applyMatch.getApproveId());
//        CtrContract contract = new CtrContract();
//        for (CtrContract ctrContract : byApproveId) {
//            if (ctrContract.getContractType().equals("B")) {
//                contract = ctrContract;
//            }
//        }
//        AxqContractVo s = new AxqContractVo();
//        BeanUtils.copyProperties(applyMatch, s);
//
//        // 查询productList todo 以后多品种情况下再调整
//        List<CtrProductVo> ctrProductVos = new ArrayList<>();
//        CtrProductVo ctrVo = new CtrProductVo();
//        ctrVo.setBrandNumber(applyMatch.getBrandNumber());
//        ctrVo.setDealNumber(contract.getTotalNumber()+"");
//        ctrVo.setDealPrice(contract.getDealPrice()+"");
//        ctrVo.setFactoryName(contract.getCompanyName());
//        ctrVo.setProductName(applyMatch.getProductName());
//        ctrVo.setTotalPrice(contract.getTotalAmount()+"");
//
//        //注意 （改）
//        s.setTemplateId("SPT_ORDER_CONTRACT");// 必填
//
//        s.setPriceTotalAll(String.valueOf(contract.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP)));
//      //  s.setAppCode(PropertiesUtil.getProperty(OfferConstants.PROJECT_CODE));
//        ctrProductVos.add(ctrVo);
//        s.setProductList(ctrProductVos);
//        s.setContractId(s.getContractNo());
//
//        mustAxq(s);
//
//        String payMode = contract.getPayType();
//        if (StringUtils.isNotBlank(payMode)) {
//            s.setPayMode(payMode);
//        } else {
//            s.setPayMode("");
//        }
//        s.setBuyerCompanyName(contract.getOurCompanyName());//必填 签约的企业必须在安心签开户过
//        s.setSellerCompanyName(contract.getCompanyName());//必填 签约的企业必须在安心签开户过
//
//        s.setBizContractId(contract.getContractNo());
//        // 质量标准
//        s.setProductQualityName(
//                DictUtil.getValue(BasConstants.DICT_TYPE_QUALITYSTANDDARD, applyMatch.getQualityStandard()));
//        //交货方式
//        s.setDeliveryTypeName(
//              DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, contract.getDeliveryType()));
//
//        s.setDealAmountCn(contract.getDealedAmount().toString());
//
//        s.setPriceTotal(String.valueOf(contract.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP)));
//
//        // 发送安心签
//        String fileId = sendAxqCreateContract(s, "createContract");
//        if (StringUtils.isNotBlank(fileId)) {
//            // 更新合同表合同附件
//            contractDao.updateBuyContentFileId(contract.getId(), fileId);
//        }
//
//    }
//
//
//    /**
//     * 销售合同安心签合同生成
//     *
//     * @param
//     */
//    @Override
//    @ServerTransactional
//    public void createSellContractAxq(ApplyMatch applyMatch) {
//        if (StringUtils.isNotBlank(applyMatch.getFileId())) {
//            return;
//        }
//        List<CtrContract> byApproveId = ctrContractService.findByApproveId(applyMatch.getApproveId());
//        CtrContract contract = new CtrContract();
//        for (CtrContract ctrContract : byApproveId) {
//            if (ctrContract.getContractType().equals("S")) {
//                contract = ctrContract;
//            }
//        }
//        AxqContractVo s = new AxqContractVo();
//        BeanUtils.copyProperties(applyMatch, s);
//        // 查询productList
//        List<CtrProductVo> ctrProductVos = new ArrayList<>();
//        CtrProductVo ctrVo = new CtrProductVo();
//        ctrVo.setBrandNumber(applyMatch.getBrandNumber());
//        ctrVo.setDealNumber(contract.getTotalNumber()+"");
//        ctrVo.setDealPrice(contract.getDealPrice()+"");
//        ctrVo.setFactoryName(contract.getCompanyName());
//        ctrVo.setProductName(applyMatch.getProductName());
//        ctrVo.setTotalPrice(contract.getTotalAmount()+"");
//        //注意 （改）
//        s.setTemplateId("SPT_ORDER_CONTRACT");// 必填
//
//        s.setPriceTotalAll(String.valueOf(contract.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP)));
//
//        ctrProductVos.add(ctrVo);
//        s.setProductList(ctrProductVos);
//
//        s.setContractId(s.getContractNo());
//
//        mustAxq(s);
//
//        String payMode = contract.getPayType();
//        if (StringUtils.isNotBlank(payMode)) {
//            s.setPayMode(payMode);
//        } else {
//            s.setPayMode("");
//        }
//
//
//         s.setBuyerCompanyName(contract.getOurCompanyName());//必填 签约的企业必须在安心签开户过
//         s.setSellerCompanyName(contract.getCompanyName());//必填 签约的企业必须在安心签开户过
//
//        s.setBizContractId(contract.getContractNo());
//        // 质量标准
//        s.setProductQualityName(
//                DictUtil.getValue(BasConstants.DICT_TYPE_QUALITYSTANDDARD, applyMatch.getQualityStandard()));
//        //交货方式
//        s.setDeliveryTypeName(
//                DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, contract.getDeliveryType()));
//
//
//        s.setDealAmountCn(contract.getDealedAmount().toString());
//
//        s.setPriceTotal(String.valueOf(contract.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP)));
//       s.setContractNo(contract.getContractNo());
//       s.setContractTimeStr(contract.getContractTime().toString());
//       s.setDealNumber(contract.getTotalNumber().toString());
//       s.setDealAmountCn(contract.getTotalAmount().toString());
//       s.setSellerName(contract.getContactName());
//       s.setSellerPhoneNumber(contract.getContactPhone());
//       s.setBuyerPhoneNumber(contract.getDeliveryPhone());
//       s.setUnitDealPrice(contract.getDealPrice().toString());
//        // 发送安心签
//        String fileId = sendAxqCreateContract(s, "createContract");
//        if (StringUtils.isNotBlank(fileId)) {
//            // 合同合同表合同附件
//            contractDao.updateSellFileId(contract.getId(), fileId);
//        }
//
//    }
//
//    /**
//     * 收货证明安心签生成
//     *
//     * @param approveId 收货确认审批id
//     */
//    @Override
//    public void createReceiptCertAxq(Long approveId) {
//
//    }
//
//    // ====================================================================================
//
//    /**
//     * 安心签合同创建
//     * @param s
//     * @param name
//     * @return fileId 附件id
//     */
//    private String sendAxqCreateContract(AxqContractVo s, String name) {
//        String fileId = "";
//        AxqContractVo reAxq = cfcaSignClient.createContract(s);
//        logger.info(name + "==contractNo=" + s.getContractNo());
//        if (null != reAxq && "60000000".equals(reAxq.getRetCode())) {
//            // 根据合同编号保存合同 ID
//            logger.info("安心签 pdf >>>>FileId=" + reAxq.getFileId());
//            fileId = reAxq.getFileId();
//        } else {
//            sendFailOper("signClient", s, name, "offContractService");
//            logger.info(name + " error contractNo:{},projectCode:{},retCode:{},msg:{} ", s.getContractNo(),
//                    reAxq.getProjectCode(), reAxq.getRetCode(), reAxq.getRetMessage());
//        }
//        return fileId;
//    }
//
//
//
//    public void sendFailOper(String url, Object param, String dataType, String serviceName) {
//        SyncData syncData = new SyncData();
//        syncData.setUrl(url);
//        syncData.setData(JsonUtil.obj2Json(param));
//        syncData.setDataType(dataType);
//        syncData.setServiceName(serviceName);
//        syncData.setStatus("0");
//        syncData.setDataCount(0L);
//        syncDataDao.save(syncData);
//    }
//
//
//    /**
//     * 安心签必填字段
//     *
//     * @param s
//     * @return
//     */
//    private AxqContractVo mustAxq(AxqContractVo s) {
//        s.setAppCode(BasConstants.APP_CODE);// 必填
//        // 默认
//        s.setBuyerSignType(BasConstants.CONTRACT_SEAL_TYPE_UK_SIGN);// 必填 默认
//        s.setSellerSignType(BasConstants.CONTRACT_SEAL_TYPE_UK_SIGN);// 必填 默认
//        s.setBuyerIsCheckProjectCode(BasConstants.CONTRACT_SEAL_NOT_SEND_PWD);// 必填
//        // 默认
//        s.setSellerIsCheckProjectCode(BasConstants.CONTRACT_SEAL_NOT_SEND_PWD);// 必填
//        // 默认
//        s.setSignLocation(BasConstants.CONTRACT_SIGN_LOCATION);// 必填 默认
//        s.setBuyerSignLocation(BasConstants.CONTRACT_BUYER_SIGN_LOCATION);// 必填
//        // 默认
//        s.setSellerSignLocation(BasConstants.CONTRACT_SELLER_SIGN_LOCATION);// 必填
//        // 默认
//        s.setBuyerLocation("10.2.2.3");
//        s.setSellerLocation("11.22.33.66");
//
//        return s;
//    }
//}
