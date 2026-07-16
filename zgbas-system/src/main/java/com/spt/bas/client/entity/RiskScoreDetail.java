package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 合同物流单
 */
@Entity
@Table(name = "t_risk_score_detail")
public class RiskScoreDetail extends IdEntity {
    private static final long serialVersionUID = -2898405933249777971L;
    
    private Long companyId; // 企业 id
    private String companyName; // 企业名称
    private String scoreType; // 评分类别
    private String scoreTypeName; //评分类别名称
    private String scoreItem; // 评分细项
    private Integer score; // 得分
    private String remark; // 备注

    private String scoreCompanyType;//评分企业分类（C-客户，S-供应商）

    @Transient
    private Boolean isSave = false;

    /**
     * 是否手动修改标识
     */
    private Boolean handUpdateFlg = false;
    
    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public String getScoreTypeName() {
        return scoreTypeName;
    }

    public void setScoreTypeName(String scoreTypeName) {
        this.scoreTypeName = scoreTypeName;
    }

    public String getScoreItem() {
        return scoreItem;
    }

    public void setScoreItem(String scoreItem) {
        this.scoreItem = scoreItem;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getScoreCompanyType() {
        return scoreCompanyType;
    }

    public void setScoreCompanyType(String scoreCompanyType) {
        this.scoreCompanyType = scoreCompanyType;
    }

    public Boolean getSave() {
        return isSave;
    }

    public void setSave(Boolean save) {
        isSave = save;
    }

    public Boolean getHandUpdateFlg() {
        return handUpdateFlg;
    }

    public void setHandUpdateFlg(Boolean handUpdateFlg) {
        this.handUpdateFlg = handUpdateFlg;
    }
}
