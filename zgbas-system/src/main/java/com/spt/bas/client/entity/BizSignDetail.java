package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 业务签署详情表
 * @Author MoonLight
 * @Date 2024/10/28 11:29
 * @Version 1.0
 */
@Entity
@Data
@Table(name = "t_biz_sign_detail")
@EqualsAndHashCode(callSuper = true)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BizSignDetail extends IdEntity {
    private static final long serialVersionUID = 8066258377144873464L;

    /**
     * 关联业务签署主表ID
     */
    private Long bizSignId;

    /**
     * 签署企业名称
     */
    private String signCompanyName;

    /**
     * 签署关键字
     */
    private String signKeyWord;

    /**
     * 签署印章类型
     */
    private String signSealType = "CTR";

    /**
     * 签署印章高度
     */
    private String signImageHeight = "150";

    /**
     * 签署印章宽度
     */
    private String signImageWidth = "150";

    /**
     * 签署位置X轴偏移量
     */
    private String signOffsetCoordX = "20";

    /**
     * 签署位置Y轴偏移量
     */
    private String signOffsetCoordY = "-30";
}
