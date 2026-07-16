package com.spt.bas.client.vo;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/5 16:30
 */
@Data
public class ApplyQuotaVo extends IdEntity implements IPmEntity {
    /**
     * 风控审批额度（元）
     */
    private BigDecimal creditAmount;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     *  区分是额度审批还是临时提额申请，Q 额度审批，T 临时提额申请
     */
    private String type;
    /**
     * 授信类别
     */
    private String creditType;
    /**
     * 授信类别名称
     */
    private String creditTypeName;
    /**
     * 临时额度
     */
    private BigDecimal temporaryAmount;
    /**
     * 有效天数
     */
    private Integer validDays;

    /**
     * 备注
     */
    private String remark;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵
     */
    private String nickName;

    private Long approveId;

    private String status;

    private Long enterpriseId;
    /**
     * 风控额度
     */
    private BigDecimal riskAmount;
}
