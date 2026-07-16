package com.spt.bas.client.vo.rtVo;


/**
 * 融拓推送企业信息Vo
 *
 * @Author: gaojy
 * @create 2022/4/8 10:47
 * @version: 1.0
 * @description:
 */
public class RtCompanyReq extends RtBaseReq {

    /**
     * 0 核心企业 1 供应商 2 经销商 3 金融机构
     */
    private String busineseType;

    /**
     * 企业名称
     */
    private String name;

    /**
     * 信用代码
     */
    private String orgCode;

    /**
     * 管理员手机号
     */
    private String phonenumber;

    /**
     * 业务场景 id
     */
    private String sceneId;

    /**
     * 业务场景系统标识
     */
    private String sceneKey;

    public String getBusineseType() {
        return busineseType;
    }

    public void setBusineseType(String busineseType) {
        this.busineseType = busineseType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneKey() {
        return sceneKey;
    }

    public void setSceneKey(String sceneKey) {
        this.sceneKey = sceneKey;
    }
}
