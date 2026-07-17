package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 业务经理工作台合同统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RptWorkbenchContractStatist {

    /**
     * 标题Code
     */
    private String labelCode;
    
    /**
     * 标题Name
     */
    private String labelName;

    /**
     * 数量
     */
    private Long count;

    /**
     * 待收金额合计（待收本金+待收违约金）
     */
    private BigDecimal noReceiveTotalAmount;

    public RptWorkbenchContractStatist(String labelCode, String labelName, Long count) {
        this.labelCode = labelCode;
        this.labelName = labelName;
        this.count = count;
    }
    
}
