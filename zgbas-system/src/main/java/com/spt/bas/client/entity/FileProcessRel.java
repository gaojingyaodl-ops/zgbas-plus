package com.spt.bas.client.entity;

import com.spt.tools.core.annotation.LogEntityName;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * <p>
 * 附件-流程关系表
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-12-02 13:54
 */
@Entity
@Table(name = "t_file_process_rel")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
@LogEntityName("附件流程关系表")
public class FileProcessRel extends IdEntity {

    /**
     * 流程code
     */
    private String processCode;

    /**
     * 附件类型id
     */
    private Long fileTypeId;

    /**
     * 附件类型
     */
    private String fileTypeName;

    /**
     * 排序号
     */
    private String orderNo;

    public Long getFileTypeId() {
        return fileTypeId;
    }

    public void setFileTypeId(Long fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    public String getFileTypeName() {
        return fileTypeName;
    }

    public void setFileTypeName(String fileTypeName) {
        this.fileTypeName = fileTypeName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

}
