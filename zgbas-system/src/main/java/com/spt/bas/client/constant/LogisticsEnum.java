package com.spt.bas.client.constant;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.CtrLogisticsReqVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.number.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 物流单类型枚举类
 *
 * @Author MoonLight
 * @Date 2023/7/5 16:52
 * @Version 1.0
 */
public enum LogisticsEnum {
    LADING("T", "LADING_V3", "提货单", "TH") {
        @Override
        public CtrLogisticsReqVo compositeLogistics(CtrLogisticsReqVo reqVo) {
            CtrLogistics logistics = reqVo.getLogistics();
            CtrLogisticsDelivery delivery = reqVo.getDelivery();
            reqVo.setSignCompanyName(reqVo.getLogistics().getBuyOurCompanyName());
            reqVo.setCompanyName(logistics.getSupplierName());
            reqVo.setLogisticsNo(getLogisticsNo(this, reqVo));
            reqVo.setContractNo(logistics.getBuyContractNo());

            Map<String, String> resultMap = compositeProductDetail(reqVo, this);
            String takeDelieveryAddr = StringUtils.isNotBlank(logistics.getTakeDelieveryAddr()) ? logistics.getTakeDelieveryAddr() : "";
            String remark = StringUtils.isNotBlank(delivery.getRemark()) ? delivery.getRemark() : "";
            if (StringUtils.isNotBlank(takeDelieveryAddr)){
                remark = takeDelieveryAddr + "提货;" + remark;
            }
            resultMap.put("remark", remark);
            reqVo.setParamMap(resultMap);
            return reqVo;
        }
    },
    DISTRIBUTION("P", "DISTRIBUTION_V3", "配送委托单", "TH") {
        @Override
        public CtrLogisticsReqVo compositeLogistics(CtrLogisticsReqVo reqVo) {
            CtrLogistics logistics = reqVo.getLogistics();
            CtrLogisticsDelivery delivery = reqVo.getDelivery();
            String bizUserName = logistics.getMatchUserName();
            Map<String, String> paramMap = compositeProductDetail(reqVo, this);
            paramMap.put("dealNumber", NumberUtil.formatNumber(delivery.getLogisticsNumber(), "#.###"));
            paramMap.put("masterPorter", StringUtils.isNotBlank(bizUserName) ? bizUserName.substring(0, 1) + "经理" : "");
            paramMap.put("masterPhone", logistics.getMatchUserPhone());
            paramMap.put("takeDeliveryAddr", logistics.getReceiveDeliveryAddr());
            reqVo.setParamMap(paramMap);
            reqVo.setSignCompanyName(logistics.getBuyOurCompanyName());
            reqVo.setCompanyName(logistics.getSupplierName());
            reqVo.setLogisticsNo(getLogisticsNo(this, reqVo));
            reqVo.setContractNo(logistics.getSellContractNo());
            return reqVo;
        }
    },
    FUND_LADING("FT", "LADING_V3", "资方提货单", "ZZ") {
        @Override
        public CtrLogisticsReqVo compositeLogistics(CtrLogisticsReqVo reqVo) {
            ApplyCtrDCSX entity = reqVo.getApplyCtrDCSX();
            CtrLogistics logistics = reqVo.getLogistics();
            CtrLogisticsDelivery delivery = reqVo.getDelivery();
            reqVo.setSignCompanyName(entity.getOurCompanyName());
            reqVo.setCompanyName(entity.getCompanyName());
            reqVo.setLogisticsNo(entity.getContractNo());
            reqVo.setContractNo(entity.getContractNo());

            Map<String, String> paramMap = compositeProductDetail(reqVo, this);
            paramMap.put("buyOurCompanyName", entity.getOurCompanyName());
            paramMap.put("supplierName", entity.getCompanyName());
            paramMap.put("supplierNo", entity.getContractNo());
            String takeDelieveryAddr = StringUtils.isNotBlank(logistics.getTakeDelieveryAddr()) ? logistics.getTakeDelieveryAddr() : "";
            String remark = StringUtils.isNotBlank(delivery.getRemark()) ? delivery.getRemark() : "";
            if (StringUtils.isNotBlank(takeDelieveryAddr)){
                remark = takeDelieveryAddr + "提货;" + remark;
            }
            paramMap.put("remark", remark);
            reqVo.setParamMap(paramMap);
            return reqVo;
        }
    },
    FUND_DISTRIBUTION("FP", "DISTRIBUTION_V3", "资方配送委托单", "ZZ") {
        @Override
        public CtrLogisticsReqVo compositeLogistics(CtrLogisticsReqVo reqVo) {
            CtrLogisticsDelivery delivery = reqVo.getDelivery();
            CtrLogistics logistics = reqVo.getLogistics();
            ApplyCtrDCSX entity = reqVo.getApplyCtrDCSX();
            String bizUserName = logistics.getMatchUserName();
            Map<String, String> paramMap = compositeProductDetail(reqVo, this);
            paramMap.put("dealNumber", NumberUtil.formatNumber(delivery.getLogisticsNumber(), "#.###"));
            paramMap.put("masterPorter", StringUtils.isNotBlank(bizUserName) ? bizUserName.substring(0, 1) + "经理" : "");
            paramMap.put("masterPhone", logistics.getMatchUserPhone());
            paramMap.put("buyOurCompanyName", entity.getOurCompanyName());
            paramMap.put("takeDeliveryAddr", logistics.getReceiveDeliveryAddr());
            paramMap.put("supplierName", entity.getCompanyName());
            paramMap.put("supplierNo", entity.getContractNo());
            reqVo.setParamMap(paramMap);
            reqVo.setSignCompanyName(entity.getOurCompanyName());
            reqVo.setCompanyName(entity.getCompanyName());
            reqVo.setLogisticsNo(entity.getContractNo());
            reqVo.setContractNo(entity.getContractNo());
            return reqVo;
        }
    },
    GOODS_SIGNATURE("S", "GOODS_SIGNATURE_V3", "货物签收单", "ZZ") {
        @Override
        public CtrLogisticsReqVo compositeLogistics(CtrLogisticsReqVo reqVo) {
            reqVo.setParamMap(compositeProductDetail(reqVo, this));
            return reqVo;
        }
    },
    DELIVERY_NOTE("N", "DELIVERY_NOTE_V3", "送货通知单", "WL") {
        @Override
        public CtrLogisticsReqVo compositeLogistics(CtrLogisticsReqVo reqVo) {
            reqVo.setParamMap(compositeProductDetail(reqVo, this));
            reqVo.setSignCompanyName(reqVo.getLogistics().getBuyOurCompanyName());
            reqVo.setLogisticsNo(getLogisticsNo(this, reqVo));
            return reqVo;
        }
    },
    DELIVERY_IN("I", "DELIVERY_IN_V3", "入库单", "RK") {
        @Override
        public CtrLogisticsReqVo compositeLogistics(CtrLogisticsReqVo reqVo) {
            ApplyDeliveryIn entity = reqVo.getApplyDeliveryIn();
            CtrLogistics logistics = reqVo.getLogistics();
            Map<String, String> resultMap = compositeProductDetail(reqVo, this);
            resultMap.put("logisticsNo", entity.getApplyNo());
            resultMap.put("ourCompanyName", logistics.getBuyOurCompanyName());
            resultMap.put("productNames", logistics.getProductNames());
            resultMap.put("loadingDate", DateOperator.formatDate(entity.getWarehouseInDate(), "yyyy年MM月dd日"));
            resultMap.put("dealNumber", NumberUtil.formatNumber(reqVo.getCurrNumber(), "#.###"));
            resultMap.put("totalAmount", NumberUtil.formatNumber(reqVo.getCurrNumber().multiply(logistics.getBuyDealPrice()), "#.###"));
            resultMap.put("documenter", reqVo.getBizUserName());
            resultMap.put("remark", entity.getRemark());
            // 货物所在地：自提、我司配送时为界面输入的货物起运地，上家配送时显示上家仓库；
            if (StringUtils.equals(BasConstants.DICT_TYPE_DELIVERY_P1, entity.getDeliveryType())) {
                resultMap.put("deliveryAddr", "上家仓库");
            } else {
                resultMap.put("deliveryAddr", entity.getDeliveryAddr().replaceAll("/", "") + entity.getContactAddr());
            }
            reqVo.setSignCompanyName(reqVo.getLogistics().getBuyOurCompanyName());
            reqVo.setCompanyName(entity.getCompanyName());
            reqVo.setLogisticsNo(entity.getApplyNo());
            reqVo.setContractNo(logistics.getBuyContractNo());
            reqVo.setParamMap(resultMap);
            return reqVo;
        }
    },
    DELIVERY_OUT("O", "DELIVERY_OUT_V3", "出库单", "CK") {
        @Override
        public CtrLogisticsReqVo compositeLogistics(CtrLogisticsReqVo reqVo) {
            ApplyDeliveryOut entity = reqVo.getApplyDeliveryOut();
            CtrLogistics logistics = reqVo.getLogistics();
            Map<String, String> resultMap = compositeProductDetail(reqVo, this);
            resultMap.put("logisticsNo", entity.getApplyNo());
            resultMap.put("sellOurCompanyName", logistics.getBuyOurCompanyName());
            resultMap.put("companyName", logistics.getSellOurCompanyName());
            resultMap.put("productNames", logistics.getProductNames());
            if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, logistics.getSellOurCompanyName())){
                resultMap.put("sellContractNo", logistics.getSellContractNo().replace("SPTS", "SPTX").replace("KCS", "KCX").replace("XYS", "XYX"));
            }
            resultMap.put("loadingDate", DateOperator.formatDate(entity.getWarehouseOutDate(), "yyyy年MM月dd日"));
            resultMap.put("dealNumber", NumberUtil.formatNumber(reqVo.getCurrNumber(), "#.###"));
            resultMap.put("totalAmount", NumberUtil.formatNumber(reqVo.getCurrNumber().multiply(logistics.getSellDealPrice()), "#.###"));
            resultMap.put("documenter", reqVo.getBizUserName());
            resultMap.put("remark", entity.getRemark());
            reqVo.setParamMap(resultMap);
            reqVo.setSignCompanyName(logistics.getBuyOurCompanyName());
            reqVo.setCompanyName(logistics.getSellOurCompanyName());
            reqVo.setLogisticsNo(entity.getApplyNo());
            reqVo.setContractNo(resultMap.get("sellContractNo"));
            return reqVo;
        }
    },
    SHIPPING_FILE("SF", "SHIPPING_FILE_V3", "发货单", "FH") {
        @Override
        public CtrLogisticsReqVo compositeLogistics(CtrLogisticsReqVo reqVo) {
            ApplyDeliveryOut entity = reqVo.getApplyDeliveryOut();
            CtrLogistics logistics = reqVo.getLogistics();
            Map<String, String> resultMap = compositeProductDetail(reqVo, this);
            resultMap.put("logisticsNo", entity.getApplyNo());
            resultMap.put("sellOurCompanyName", logistics.getSellOurCompanyName());
            resultMap.put("companyName", logistics.getCompanyName());
            resultMap.put("productNames", logistics.getProductNames());
//            if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, logistics.getSellOurCompanyName())){
//                resultMap.put("sellContractNo", logistics.getSellContractNo().replace("SPTS", "SPTX").replace("KCS", "KCX").replace("XYS", "XYX"));
//            }
            resultMap.put("loadingDate", DateOperator.formatDate(entity.getWarehouseOutDate(), "yyyy年MM月dd日"));
            resultMap.put("dealNumber", NumberUtil.formatNumber(reqVo.getCurrNumber(), "#.###"));
            resultMap.put("totalAmount", NumberUtil.formatNumber(reqVo.getCurrNumber().multiply(logistics.getSellDealPrice()), "#.###"));
            resultMap.put("documenter", reqVo.getBizUserName());
            resultMap.put("remark", entity.getRemark());
            reqVo.setParamMap(resultMap);
            reqVo.setSignCompanyName(logistics.getSellOurCompanyName());
            reqVo.setCompanyName(logistics.getCompanyName());
            reqVo.setLogisticsNo(entity.getApplyNo());
            reqVo.setContractNo(resultMap.get("sellContractNo"));
            return reqVo;
        }
    };

    private final String logisticsCode;
    private final String logisticsTemplate;
    private final String logisticsName;
    private final String logisticsPrefix;

    public abstract CtrLogisticsReqVo compositeLogistics(CtrLogisticsReqVo reqVo);

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public String getLogisticsTemplate() {
        return logisticsTemplate;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public String getLogisticsPrefix() {
        return logisticsPrefix;
    }

    LogisticsEnum(String logisticsCode, String logisticsTemplate, String logisticsName, String logisticsPrefix) {
        this.logisticsCode = logisticsCode;
        this.logisticsTemplate = logisticsTemplate;
        this.logisticsName = logisticsName;
        this.logisticsPrefix = logisticsPrefix;
    }

    /**
     * 通用-组装物流单据基础字段数据
     *
     * @param reqVo
     * @param logisticsEnum
     * @return
     */
    private static Map<String, String> compositeProductDetail(CtrLogisticsReqVo reqVo, LogisticsEnum logisticsEnum) {
        CtrLogistics logistics = reqVo.getLogistics();
        CtrLogisticsDelivery delivery = reqVo.getDelivery();
        List<CtrLogisticsDriver> driverList = reqVo.getDriverList();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("businessNo", logistics.getLogisticsNo());
        paramMap.put("supplierNo", StringUtils.isBlank(logistics.getSupplierNo()) ? logistics.getLogisticsNo() : logistics.getSupplierNo());
        paramMap.put("productName", logistics.getProductNames());
        paramMap.put("buyContractNo", logistics.getBuyContractNo());
        paramMap.put("sellContractNo", logistics.getSellContractNo());
        paramMap.put("totalNumber", NumberUtil.formatNumber(logistics.getDealNumber(), "#.###"));
        paramMap.put("buyTotalAmount", NumberUtil.formatNumber(logistics.getBuyTotalAmount(), "#.###"));
        paramMap.put("sellTotalAmount", NumberUtil.formatNumber(logistics.getSellTotalAmount(), "#.###"));
        paramMap.put("buyDealPrice", NumberUtil.formatNumber(logistics.getBuyDealPrice(), "#.###"));
        paramMap.put("sellDealPrice", NumberUtil.formatNumber(logistics.getSellDealPrice(), "#.###"));
        paramMap.put("buyOurCompanyName", logistics.getBuyOurCompanyName());
        paramMap.put("sellOurCompanyName", logistics.getSellOurCompanyName());
        paramMap.put("supplierName", logistics.getSupplierName());
        paramMap.put("companyName", logistics.getCompanyName());
        paramMap.put("buyDeliveryType", BsDictUtil.getValue(logistics.getEnterpriseId(), BasConstants.DICT_DELIVERYTYPETEXT, logistics.getBuyDeliveryType()));
        paramMap.put("sellDeliveryType", BsDictUtil.getValue(logistics.getEnterpriseId(), BasConstants.DICT_DELIVERYTYPETEXT, logistics.getSellDeliveryType()));
        paramMap.put("takeDeliveryAddr", logistics.getTakeDelieveryAddr());
        paramMap.put("receiveDeliveryAddr", logistics.getReceiveDeliveryAddr());
        paramMap.put("matchUserName", logistics.getMatchUserName());
        paramMap.put("matchUserPhone", logistics.getMatchUserPhone());
        paramMap.put("productNames", logistics.getProductNames());
        if (Objects.nonNull(delivery)) {
            paramMap.put("logisticsNo", getLogisticsNo(logisticsEnum, reqVo));
            paramMap.put("loadingDate", DateOperator.formatDate(delivery.getLogisticsDate(), "yyyy年MM月dd日"));
            paramMap.put("masterPorter", delivery.getMasterPorter());
            paramMap.put("masterPhone", delivery.getMasterPhone());
            paramMap.put("remark", delivery.getRemark());
        }
        for (int i = 0; i < 5; i++) {
            String indexPrefix = i == 0 ? "" : String.valueOf(i);
            if (CollectionUtils.isEmpty(driverList) || driverList.size() <= i) {
                paramMap.put("productNames" + indexPrefix, "");
                paramMap.put("dealNumber" + indexPrefix, "");
                paramMap.put("numberUnit" + indexPrefix, "");
                paramMap.put("driverName" + indexPrefix, "");
                paramMap.put("plateNumber" + indexPrefix, "");
                paramMap.put("driverCardNo" + indexPrefix, "");
                // 联系方式保密判断
                paramMap.put("contactPhone" + indexPrefix, "");
            } else {
                CtrLogisticsDriver driver = driverList.get(i);
                paramMap.put("productNames" + indexPrefix, logistics.getProductNames());
                paramMap.put("dealNumber" + indexPrefix, NumberUtil.formatNumber(driver.getLogisticsNumber(), "#.###"));
                paramMap.put("numberUnit" + indexPrefix, BasConstants.NUMBER_UNIT_DUN);
                paramMap.put("driverName" + indexPrefix, driver.getDriverName());
                paramMap.put("plateNumber" + indexPrefix, driver.getPlateNumber());
                paramMap.put("driverCardNo" + indexPrefix, driver.getDriverCardNo());
                // 联系方式保密判断
                paramMap.put("contactPhone" + indexPrefix, StringUtils.equals("1", delivery.getPhoneProtect()) ? "—" : driver.getContactPhone());
            }
        }
        return paramMap;
    }

    private static String getLogisticsNo(LogisticsEnum logisticsEnum, CtrLogisticsReqVo reqVo) {
        if (Objects.nonNull(reqVo.getLogistics()) && Objects.nonNull(reqVo.getDelivery())) {
            return logisticsEnum.getLogisticsPrefix() + reqVo.getLogistics().getLogisticsNo() + "-" + reqVo.getDelivery().getLogisticsCount();
        }
        return "";
    }
}
