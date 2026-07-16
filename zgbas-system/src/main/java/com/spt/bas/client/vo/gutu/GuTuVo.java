package com.spt.bas.client.vo.gutu;

/**
 * 估图Vo
 * @Author MoonLight
 * @Date 2024/2/18 16:44
 * @Version 1.0
 */
public class GuTuVo {
    private int pageType;

    private String tokenKeyLink;

    public int getPageType() {
        return pageType;
    }

    public void setPageType(int pageType) {
        this.pageType = pageType;
    }

    public String getTokenKeyLink() {
        return tokenKeyLink;
    }

    public void setTokenKeyLink(String tokenKeyLink) {
        this.tokenKeyLink = tokenKeyLink;
    }

    public GuTuVo() {
    }

    public GuTuVo(int pageType) {
        this.pageType = pageType;
    }
}
