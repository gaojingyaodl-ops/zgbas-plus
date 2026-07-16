package com.spt.bas.client.constant;

/**
 * 区域枚举类
 *
 * @Author MoonLight
 * @Date 2023/8/3 15:18
 * @Version 1.0
 */
public enum OwnRegionEnum {
    REGION_HB("HB", "华北事业部"),
    REGION_HN("HN", "华南事业部"),
    REGION_HD("HD", "华东事业部"),
    REGION_HZ("HZ", "华中事业部"),
    REGION_HG("HG", "化工品事业部"),
    REGION_HF("HF", "第一业务部"),
    REGION_NB("NB", "宁波事业部"),
    REGION_KH("KH", "改性塑料事业部"),
    REGION_JX("JX", "莒县事业部"),
    REGION_HNN("HNN", "河南网塑");

    /**
     * 区域代码
     */
    private final String regionCode;

    /**
     * 区域名称
     */
    private final String regionName;

    public String getRegionCode() {
        return regionCode;
    }

    public String getRegionName() {
        return regionName;
    }

    OwnRegionEnum(String regionCode, String regionName) {
        this.regionCode = regionCode;
        this.regionName = regionName;
    }

    public static OwnRegionEnum getRegionEnumByCode(String regionCode) {
        for (OwnRegionEnum target : OwnRegionEnum.values()) {
            if (target.regionCode.equals(regionCode)) {
                return target;
            }
        }
        return null;
    }

    public static OwnRegionEnum getRegionEnumByName(String regionName) {
        for (OwnRegionEnum target : OwnRegionEnum.values()) {
            if (target.regionName.equals(regionName)) {
                return target;
            }
        }
        return null;
    }
}
