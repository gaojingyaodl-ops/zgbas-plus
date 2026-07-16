package com.spt.bas.client.vo.protocol;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.date.DateOperator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 文件协议-合同取消协议
 *
 * @Author MoonLight
 * @Date 2024/5/27 16:27
 * @Version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RescissionAgreement {
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
     * 厂商
     */
    private String factoryName;

    /**
     * 合同数量
     */
    private String totalNumber;

    /**
     * 合同单价
     */
    private String dealPrice;

    /**
     * 合同金额
     */
    private String totalAmount;

    /**
     * 取消协议日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date protocolDate;

    private String protocolDateStr;

    /**
     * 退款金额
     */
    private String refundAmount;

    public String getContractDateStr() {
        return DateOperator.formatDate(contractDate, "yyyy年MM月dd日");
    }

    public String getProtocolDateStr() {
        return DateOperator.formatDate(protocolDate, "yyyy年MM月dd日");
    }
}
