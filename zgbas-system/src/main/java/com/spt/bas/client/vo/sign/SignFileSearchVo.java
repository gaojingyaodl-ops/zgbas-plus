package com.spt.bas.client.vo.sign;

import com.spt.tools.core.bean.PageSearchVo;

/**
 * @author liuzhenwei
 * 签署文件列表查询Vo
 */
public class SignFileSearchVo extends PageSearchVo {
    //文件名称
    private String fileName;
    //状态
    private String signStatus;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(String signStatus) {
        this.signStatus = signStatus;
    }
}
