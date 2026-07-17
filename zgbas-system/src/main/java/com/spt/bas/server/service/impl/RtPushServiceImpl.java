package com.spt.bas.server.service.impl;

import cn.hutool.core.thread.ExecutorBuilder;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.rtVo.*;
import com.spt.bas.server.dao.BsDictDataDao;
import com.spt.bas.server.rt.RtApi;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.json.JsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 融拓数据接口对接
 *
 * @Author: gaojy
 * @create 2022/4/11 11:11
 * @version: 1.0
 * @description:
 */
@Component
@Transactional(readOnly = true)
public class RtPushServiceImpl {
    ExecutorService executor = ExecutorBuilder.create()
            .setCorePoolSize(4)
            .setMaxPoolSize(10)
            .setWorkQueue(new LinkedBlockingQueue<>(100))
            .build();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RtApi rtApi;
    @Autowired
    private BsDictDataDao bsDictDataDao;

    /**
     * 推送企业信息至融拓
     *
     * @param bsCompany
     */
    public void pushCompanyToRt(BsCompany bsCompany) {
        if (Boolean.FALSE.equals(getSwitch())){
            return;
        }
        executor.execute(() -> {
            logger.info("pushCompanyToRt 推送企业信息至融拓 companyName:{}", bsCompany.getCompanyName());
            RtCompanyReq rtCompanyReq = new RtCompanyReq();
            // 0-核心企业 1-供应商 2-经销商 3-金融机构
            rtCompanyReq.setBusineseType(StringUtils.equals("W", bsCompany.getSupplierRating()) ? "1" : "2");
            rtCompanyReq.setName(bsCompany.getCompanyName());
            rtCompanyReq.setOrgCode(bsCompany.getCompanyCreditNo());
            rtCompanyReq.setPhonenumber(StringUtils.isNotBlank(bsCompany.getCompanyPhone()) ? bsCompany.getCompanyPhone() : bsCompany.getContactPhone());
            RtResp<RtCompanyResp> rtCompanyRespRtResp = rtApi.pushCompanyToRt(rtCompanyReq);
            logger.info("pushCompanyToRt result :{}", JsonUtil.obj2Json(rtCompanyRespRtResp));
        });
    }

    /**
     * 推送确认收货信息至融拓
     *
     * @param confirmReceipt
     */
    public void pushConfirmToRt(ApplyConfirmReceipt confirmReceipt, PmApprove approve, List<ApplyProductDetail> productList) {
        if (Boolean.FALSE.equals(getSwitch())){
            return;
        }
        executor.execute(() -> {
            logger.info("pushConfirmToRt 推送确认收货信息至融拓 contractNo:{}", confirmReceipt.getContractNo());
            RtConfirmReq rtConfirmReq = new RtConfirmReq();
            BeanUtils.copyProperties(confirmReceipt, rtConfirmReq);
            // 0-核心管理系统;1-采购管家小程序
            rtConfirmReq.setApplySource(Objects.nonNull(confirmReceipt.getWxUserId()) ? "1" : "0");
            rtConfirmReq.setCreator(approve.getCreateUserId());
            rtConfirmReq.setCreatorName(approve.getCreateUserName());

            if (CollectionUtils.isNotEmpty(productList)) {
                for (ApplyProductDetail productDetail : productList) {
                    rtConfirmReq.setProductName(productDetail.getProductName());
                    rtConfirmReq.setBrandNumber(productDetail.getBrandNumber());
                    rtConfirmReq.setFactoryName(productDetail.getFactoryName());
                    rtConfirmReq.setDealNumber(productDetail.getDealNumber());
                    rtConfirmReq.setDealPrice(productDetail.getDealPrice());
                    rtConfirmReq.setCurNumber(productDetail.getCurNumber());
                }
            }
            RtResp<T> tRtResp = rtApi.pushConfirmToRt(rtConfirmReq);
            logger.info("pushConfirmToRt result:{}", JsonUtil.obj2Json(tRtResp));
        });
    }

    /**
     * 推送赊销预算信息至融拓
     *
     * @param contract
     */
    public void pushContractToRt(CtrContract contract) {
        if (Boolean.FALSE.equals(getSwitch())){
            return;
        }
        executor.execute(() -> {
            logger.info("pushContractToRt 推送赊销预算信息至融拓 contractNo:{}", contract.getContractNo());
            RtContractReq rtContractReq = new RtContractReq();
            BeanUtils.copyProperties(contract, rtContractReq);
            rtContractReq.setFileId(StringUtils.equals(BasConstants.CONTRACT_TYPE_S, contract.getContractType()) ?
                    contract.getSellContentFileId() : contract.getBuyContentFileId());
            rtContractReq.setCreator(contract.getMatchUserId());
            rtContractReq.setCreatorName(contract.getMatchUserName());
            RtResp<T> tRtResp = rtApi.pushContractToRt(rtContractReq);
            logger.info("pushContractToRt  result:{}", JsonUtil.obj2Json(tRtResp));
        });
    }

    /**
     * 推送付款信息至融拓
     *
     * @param applyPay
     * @param approve
     */
    public void pushPayToRt(ApplyPay applyPay, PmApprove approve) {
        if (Boolean.FALSE.equals(getSwitch())){
            return;
        }
        executor.execute(() -> {
            logger.info("pushPayToRt 推送付款信息至融拓 contractNo:{}", applyPay.getContractNo());
            RtPayReq rtPayReq = new RtPayReq();
            BeanUtils.copyProperties(applyPay, rtPayReq);
            rtPayReq.setCreator(approve.getCreateUserId());
            rtPayReq.setCreatorName(approve.getCreateUserName());
            RtResp<T> tRtResp = rtApi.pushPayToRt(rtPayReq);
            logger.info("pushPayToRt result:{}", JsonUtil.obj2Json(tRtResp));
        });
    }

    /**
     * 推送收款信息至融拓
     *
     * @param applyReceive
     * @param approve
     */
    public void pushReceiveToRt(ApplyReceive applyReceive, PmApprove approve) {
        if (Boolean.FALSE.equals(getSwitch())){
            return;
        }
        executor.execute(() -> {
            logger.info("pushReceiveToRt 推送收款信息至融拓 contractNo:{}", applyReceive.getContractNo());
            RtReceiveReq rtReceiveReq = new RtReceiveReq();
            BeanUtils.copyProperties(applyReceive, rtReceiveReq);
            rtReceiveReq.setCreator(approve.getCreateUserId());
            rtReceiveReq.setCreatorName(approve.getCreateUserName());
            RtResp<T> tRtResp = rtApi.pushReceiveToRt(rtReceiveReq);
            logger.info("pushReceiveToRt result:{}", JsonUtil.obj2Json(tRtResp));
        });
    }

    /**
     * 推送入库信息至融拓
     *
     * @param deliveryIn
     * @param approve
     */
    public void pushDeliveryInToRt(ApplyDeliveryIn deliveryIn, PmApprove approve, List<ApplyProductDetail> productList) {
        if (Boolean.FALSE.equals(getSwitch())){
            return;
        }
        executor.execute(() -> {
            logger.info("pushDeliveryInToRt 推送入库信息至融拓 contractNo:{}", deliveryIn.getContractNo());
            RtDeliveryInReq rtDeliveryInReq = new RtDeliveryInReq();
            BeanUtils.copyProperties(deliveryIn, rtDeliveryInReq);
            rtDeliveryInReq.setCreator(approve.getCreateUserId());
            rtDeliveryInReq.setCreatorName(approve.getCreateUserName());
            if (CollectionUtils.isNotEmpty(productList)) {
                for (ApplyProductDetail productDetail : productList) {
                    rtDeliveryInReq.setProductName(productDetail.getProductName());
                    rtDeliveryInReq.setBrandNumber(productDetail.getBrandNumber());
                    rtDeliveryInReq.setFactoryName(productDetail.getFactoryName());
                    rtDeliveryInReq.setDealNumber(productDetail.getDealNumber());
                    rtDeliveryInReq.setDealPrice(productDetail.getDealPrice());
                    rtDeliveryInReq.setCurNumber(productDetail.getCurNumber());
                }
            }
            RtResp<T> tRtResp = rtApi.pushDeliveryInToRt(rtDeliveryInReq);
            logger.info("pushDeliveryInToRt result:{}", JsonUtil.obj2Json(tRtResp));
        });
    }

    /**
     * 推送出库信息至融拓
     *
     * @param deliveryOut
     * @param approve
     */
    public void pushDeliveryOutToRt(ApplyDeliveryOut deliveryOut, PmApprove approve, List<ApplyProductDetail> productList) {
        if (Boolean.FALSE.equals(getSwitch())){
            return;
        }
        executor.execute(() -> {
            logger.info("pushDeliveryOutToRt 推送出库信息至融拓 contractNo:{}", deliveryOut.getContractNo());
            RtDeliveryOutReq rtDeliveryOutReq = new RtDeliveryOutReq();
            BeanUtils.copyProperties(deliveryOut, rtDeliveryOutReq);
            rtDeliveryOutReq.setCreator(approve.getCreateUserId());
            rtDeliveryOutReq.setCreatorName(approve.getCreateUserName());
            if (CollectionUtils.isNotEmpty(productList)) {
                for (ApplyProductDetail productDetail : productList) {
                    rtDeliveryOutReq.setProductName(productDetail.getProductName());
                    rtDeliveryOutReq.setBrandNumber(productDetail.getBrandNumber());
                    rtDeliveryOutReq.setFactoryName(productDetail.getFactoryName());
                    rtDeliveryOutReq.setDealNumber(productDetail.getDealNumber());
                    rtDeliveryOutReq.setDealPrice(productDetail.getDealPrice());
                    rtDeliveryOutReq.setCurNumber(productDetail.getCurNumber());
                }
            }
            RtResp<T> tRtResp = rtApi.pushDeliveryOutToRt(rtDeliveryOutReq);
            logger.info("pushDeliveryOutToRt result:{}", JsonUtil.obj2Json(tRtResp));
        });
    }

    /**
     * 推送收票信息至融拓
     *
     * @param applyInvoiceReceived
     * @param approve
     */
    public void pushInvoiceReceiveToRt(ApplyInvoiceReceived applyInvoiceReceived, PmApprove approve) {
        if (Boolean.FALSE.equals(getSwitch())){
            return;
        }
        executor.execute(() -> {
            logger.info("pushInvoiceReceiveToRt 推送收票信息至融拓 contractNo:{}", applyInvoiceReceived.getContractNo());
            RtInvoiceReceiveReq rtInvoiceReceiveReq = new RtInvoiceReceiveReq();
            BeanUtils.copyProperties(applyInvoiceReceived, rtInvoiceReceiveReq);
            rtInvoiceReceiveReq.setCreator(approve.getCreateUserId());
            rtInvoiceReceiveReq.setCreatorName(approve.getCreateUserName());
            RtResp<T> tRtResp = rtApi.pushInvoiceReceiveToRt(rtInvoiceReceiveReq);
            logger.info("pushInvoiceReceiveToRt result:{}", JsonUtil.obj2Json(tRtResp));
        });
    }

    /**
     * 推送开票信息至融拓
     *
     * @param applyInvoice
     * @param approve
     */
    public void pushInvoiceToRt(ApplyInvoice applyInvoice, PmApprove approve) {
        if (Boolean.FALSE.equals(getSwitch())){
            return;
        }
        executor.execute(() -> {
            logger.info("pushInvoiceToRt 推送开票信息至融拓 contractNo:{}", applyInvoice.getContractNo());
            RtInvoiceReq rtInvoiceReq = new RtInvoiceReq();
            BeanUtils.copyProperties(applyInvoice, rtInvoiceReq);
            rtInvoiceReq.setCreator(approve.getCreateUserId());
            rtInvoiceReq.setCreatorName(approve.getCreateUserName());
            RtResp<T> tRtResp = rtApi.pushInvoiceToRt(rtInvoiceReq);
            logger.info("pushInvoiceToRt result:{}", JsonUtil.obj2Json(tRtResp));
        });
    }

    /**
     * 融拓业务推送控制开关
     *
     * @return
     */
    private boolean getSwitch() {
        BsDictData configSwitch = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, "rtSwitch", BasConstants.ZG_ENTERPRISE_ID);
        if (Objects.isNull(configSwitch) || !StringUtils.equalsIgnoreCase("true", configSwitch.getDictName())) {
            logger.info("融拓业务推送控制开关:{},中止推送。", false);
            return false;
        }
        logger.info("融拓业务推送控制开关:{},执行推送。", true);
        return true;
    }
}
