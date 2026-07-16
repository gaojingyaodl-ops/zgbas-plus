package com.spt.bas.client.vo;

import java.io.File;

public class DownLoadFileVo {
    private String fileName;
    private String fileType;
    private Long fileId;
    private File targetFile;

    public DownLoadFileVo() {
    }

    public DownLoadFileVo(String fileName, Long fileId) {
        this.fileName = fileName;
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }
}
