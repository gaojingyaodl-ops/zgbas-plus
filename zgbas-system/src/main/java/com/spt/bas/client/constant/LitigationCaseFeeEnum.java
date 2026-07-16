package com.spt.bas.client.constant;

public enum LitigationCaseFeeEnum {
    ATTORNEY_FEE("attorneyFee", "律师费"),
    PROCESSING_FEE("processingFee", "案件受理费"),
    PRESERVATION_FEE("preservationFee", "保全费"),
    LIABILITY_FEE("liabilityFee", "诉责险");
    private String code;
    private String name;

    LitigationCaseFeeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static LitigationCaseFeeEnum getEnumByCode(String code) {
        if (code == null) {
            return null;
        }
        for (LitigationCaseFeeEnum litigationCaseFeeEnum : LitigationCaseFeeEnum.values()) {
            if (litigationCaseFeeEnum.code.equals(code)) {
                return litigationCaseFeeEnum;
            }
        }
        return null;
    }
}
