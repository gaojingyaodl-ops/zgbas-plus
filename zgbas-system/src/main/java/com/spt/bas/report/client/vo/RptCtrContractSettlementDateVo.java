package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 结算单结算时间（取值：最后审批时间）查询响应参数
 *
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2025/5/20 17:30
 */
@Data
public class RptCtrContractSettlementDateVo {

    /**
     * 审批ID
     */
    private Long approveId;

    /**
     * 销售合同ID
     */
    private Long sellContractId;

    /**
     * 采购合同ID
     */
    private Long buyContractId;

    /**
     * 收票最终审批时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date receiveBillApproveDate;

    /**
     * 收款最终审批时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date receiveApproveDate;

    /**
     * 确认收货最终审批时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date confirmReceiptApproveDate;
}
