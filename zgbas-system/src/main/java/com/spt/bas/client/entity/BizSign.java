package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * 业务签署表
 * @Author MoonLight
 * @Date 2024/10/28 11:08
 * @Version 1.0
 */
@Entity
@Data
@Table(name = "t_biz_sign")
@EqualsAndHashCode(callSuper = true)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BizSign extends IdEntity {
    private static final long serialVersionUID = -4585938036433676599L;

    /**
     * 审批单ID
     */
    private Long approveId;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 签署原文附件ID
     */
    private String signFileId;

    /**
     * 签署原文附件名称
     */
    private String signFileName;

    /**
     * 签署状态 N-未签署；D-已签署
     */
    private String signStatus;

    /**
     * 安心签合同号
     */
    private String cfcaContractNo;

    /**
     * 签署完成附件ID
     */
    private String cfcaFileId;

    /**
     * 签署明细
     */
    private List<BizSignDetail> bizSignDetailList;

    @Transient
    public List<BizSignDetail> getBizSignDetailList() {
        return bizSignDetailList;
    }
}
