package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


/**
 * 中银导入Excel
 */
@Data
public class ZhongYinExcelVo {

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 中银额度
     */
    private String zhongYinCreditAmount;


    /**
     * 中银审批日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date zhongYinApproveDate;

    
}
