package com.spt.bas.client.vo.protocol;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.date.DateOperator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author 田起立
 * @Date 2024/6/5 16:59
 * @Description: 合同补充协议对象
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SupplementaryAgreement {
    /**
     * 协议编号
     */
    private String protocolNo;

    /**
     * 供方
     */
    private String targetCompanyName;

    /**
     * 需方
     */
    private String ourCompanyName;

    /**
     * 签订日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractDate;

    private String contractDateStr;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 商品规格
     */
    private String productName;

    /**
     * 变更牌号
     */
    private String brandNumber;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 变更合同数量
     */
    private BigDecimal alterTotalNumber;

    /**
     * 合同单价
     */
    private BigDecimal dealPrice;

    /**
     * 变更单价
     */
    private BigDecimal alterDealPrice;

    /**
     * 合同金额
     */
    private BigDecimal totalAmount;

    /**
     * 变更合同总额
     */
    private BigDecimal alterTotalAmount;

    /**
     * 交货方式
     */
    private String deliveryMode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 取消协议日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date protocolDate;

    private String protocolDateStr;

    public String getContractDateStr() {
        return DateOperator.formatDate(contractDate, "yyyy年MM月dd日");
    }

    public String getProtocolDateStr() {
        return DateOperator.formatDate(protocolDate, "yyyy年MM月dd日");
    }

    /**
     * 是否自动发起中游合同补充协议
     */
    private Boolean autoDcsxSupAgreementFlg;

    /**
     * 审批完成自动更新合同数据
     */
    private Boolean autoRefreshContractFlg;

    /**
     * 补充条款
     */
    private String extraTermBk;
    private String extraTermTk;
}
