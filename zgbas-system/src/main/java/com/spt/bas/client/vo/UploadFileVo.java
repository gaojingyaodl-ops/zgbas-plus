package com.spt.bas.client.vo;


public class UploadFileVo {
    
    private String fileId;

    public UploadFileVo() {
    }

    public UploadFileVo(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
