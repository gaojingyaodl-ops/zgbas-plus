package com.spt.bas.purchase.wx.server.common;

/**
 * 证件类型
 */
public enum CardType {

    ID_CARD("0", "身份证"),

    HK_AND_MACAO_PASS("1", "港澳通行证"),

    PASSPORT("2", "护照"),

    TAIWAN_PASS("3", "台胞证"),

    BUSINESS_LICENSE("4", "营业执照"),

    VEHICLE("5", "行驶证"),

    DRIVER_LICENSE("6", "驾驶证"),

    HOUSEHOLD_REGISTER("7", "户口页"),

    BANK_CARD("8", "银行卡");

    /**
     * 类型
     */
    private String cardType;

    /**
     * 名字
     */
    private String typeName;

    CardType(String cardType, String typeName) {
        this.cardType = cardType;
        this.typeName = typeName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return String.format("Status:{cardType=%s, typeName=%s}", getCardType(), getTypeName());
    }
}
