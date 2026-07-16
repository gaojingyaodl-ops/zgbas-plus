package com.spt.bas.client.vo.rtVo;


/**
 * @Author: gaojy
 * @create 2022/4/8 17:41
 * @version: 1.0
 * @description:
 */
public class RtBaseReq {

    private Long trxId;
    /**
     * 附件ID
     */
    private String fileId;

    /**
     * 文件类型ID
     */
    private Long fileTypeId;

    /**
     * 创建人
     */
    private Long creator;

    /**
     * 创建人姓名
     */
    private String creatorName;

    public Long getTrxId() {
        return trxId;
    }

    public void setTrxId(Long trxId) {
        this.trxId = trxId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Long getFileTypeId() {
        return fileTypeId;
    }

    public void setFileTypeId(Long fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
