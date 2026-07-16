package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 *  权益知情表
 * </p>
 *
 * @Author: zhaowenwen
 * @Date: Created in 2023-01-11 11:35
 */
@Entity
@Table(name = "t_bs_text_content")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsTextContent extends IdEntity {

    /**
     * 文本类型
     */
    private String textType;

    /**
     * 文本类型名称
     */
    private String typeName;

    /**
     * 版本
     */
    private String version;

    /**
     * 文本内容
     */
    private String content;

    /**
     * 是否有效
     */
    private Boolean enableFlg;

    public String getTextType() {
        return textType;
    }

    public void setTextType(String textType) {
        this.textType = textType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }
}
