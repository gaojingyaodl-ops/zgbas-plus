package com.spt.bas.client.vo;

/**
 * <p>
 *  图片上传base64请求参数
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-24 10:05
 */
public class UploadBase64Request {
    /**
     * 文件名
     */
    private String fileName;

    /**
     * 图片base64文件编码
     */
    private String base64Data;

    /**
     * 图片类型
     */
    private String cardType;

    /**
     * 方向
     */
    private String direction;

    /**
     * 是否登录
     */
    private Boolean loginFlg = true;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBase64Data() {
        return base64Data;
    }

    public void setBase64Data(String base64Data) {
        this.base64Data = base64Data;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Boolean getLoginFlg() {
        return loginFlg;
    }

    public void setLoginFlg(Boolean loginFlg) {
        this.loginFlg = loginFlg;
    }
}
