package com.spt.bas.client.vo;

import java.io.File;

/**
 * @Author MoonLight
 * @Date 2023/8/21 9:32
 * @Version 1.0
 */
public class DownLoadContractRespVo {
    private File zipFile;

    private String message;

    private Boolean successFlg = true;

    public DownLoadContractRespVo() {
    }

    public DownLoadContractRespVo(String message, Boolean successFlg) {
        this.message = message;
        this.successFlg = successFlg;
    }

    public File getZipFile() {
        return zipFile;
    }

    public void setZipFile(File zipFile) {
        this.zipFile = zipFile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccessFlg() {
        return successFlg;
    }

    public void setSuccessFlg(Boolean successFlg) {
        this.successFlg = successFlg;
    }
}
