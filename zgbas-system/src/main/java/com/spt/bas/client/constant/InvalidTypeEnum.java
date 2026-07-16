package com.spt.bas.client.constant;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作废申请枚举类
 *
 * @Author MoonLight
 * @Date 2023/9/12 15:22
 * @Version 1.0
 */
public enum InvalidTypeEnum {
    CC("CC", "CC", "合同作废"),
    CP("CP", BasConstants.PROCESS_CODE_PAY, "采购付款作废"),
    CI("CI", BasConstants.PROCESS_CODE_IN, "采购入库作废"),

    CE("CE", BasConstants.PROCESS_APPLY_INRECEIVED, "采购收票作废"),
    CR("CR", BasConstants.PROCESS_APPLY_RECEIVE, "销售收款作废"),
    CO("CO", BasConstants.PROCESS_CODE_OUT, "销售出库作废"),

    CV("CV", BasConstants.PROCESS_CTR_INVOICE, "销售开票作废"),
    CM("CM", BasConstants.PROCESS_APPLY_CONFIRM_RECEIPT, "确认收货作废"),
    CPD("CPD", BasConstants.PROCESS_CODE_DCSX_PAY, "代采赊销付款作废"),
    CRD("CRD", BasConstants.PROCESS_APPLY_RECEIVE_DCSX, "代采赊销收款作废"),
    CED("CED", BasConstants.PROCESS_APPLY_DCSXINRECEIVED, "代采赊销收票作废"),
    CVD("CVD", BasConstants.PROCESS_CTR_DCSXINVOICE, "代采赊销开票作废"),
    CMD("CMD", BasConstants.PROCESS_APPLY_CONFIRM_RECEIPT_DCSX, "代采赊销确认收货作废");

    private final String invalidTypeCode;

    private final String invalidProcessCode;

    private final String invalidTypeName;

    public String getInvalidTypeCode() {
        return invalidTypeCode;
    }

    public String getInvalidProcessCode() {
        return invalidProcessCode;
    }

    public String getInvalidTypeName() {
        return invalidTypeName;
    }

    InvalidTypeEnum(String invalidTypeCode, String invalidProcessCode, String invalidTypeName) {
        this.invalidTypeCode = invalidTypeCode;
        this.invalidProcessCode = invalidProcessCode;
        this.invalidTypeName = invalidTypeName;
    }

    /**
     * 获取所有类型的作废枚举类型Code
     *
     * @return 所有类型的作废枚举类型Code
     */
    public static List<String> getInvalidProcessCodeList() {
        return Arrays.asList(InvalidTypeEnum.values()).stream().map(InvalidTypeEnum::getInvalidProcessCode).collect(Collectors.toList());
    }

    /**
     * 根据作废类型获取作废枚举
     *
     * @param invalidTypeCode 作废类型
     * @return 作废申请枚举
     */
    public static InvalidTypeEnum getInvalidTypeEnumByTypeCode(String invalidTypeCode) {
        for (InvalidTypeEnum target : InvalidTypeEnum.values()) {
            if (target.getInvalidTypeCode().equals(invalidTypeCode)) {
                return target;
            }
        }
        return null;
    }

    /**
     * 根据流程代码获取作废枚举
     *
     * @param processCode 流程代码
     * @return 作废申请枚举
     */
    public static InvalidTypeEnum getInvalidTypeEnumByProcessCode(String processCode) {
        for (InvalidTypeEnum target : InvalidTypeEnum.values()) {
            if (target.getInvalidProcessCode().equals(processCode)) {
                return target;
            }
        }
        return null;
    }
}
