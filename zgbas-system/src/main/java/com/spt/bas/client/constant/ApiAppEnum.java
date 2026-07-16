package com.spt.bas.client.constant;

/**
 * 第三方系统应用枚举类
 * @Author: gaojy
 * @create 2022/2/21 9:35
 * @version: 1.0
 * @description:
 */
public enum ApiAppEnum {

    ZY("ZY", "则一物流"),
    WFQ("WFQ", "微风企"),
    CFCA("CFCA", "安心签"),
    JINXIN("JINXIN","金信"),
    RT("RT","融拓");

    private final String code;
    private final String name;

    ApiAppEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
