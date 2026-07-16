package com.spt.bas.client.vo;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/9/11 16:27
 */

public class FactMenuVo {

    /**
     * 菜单名字
     */
    private String name;

    private String icon;

    /**
     * 地址
     */
    private String url;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
