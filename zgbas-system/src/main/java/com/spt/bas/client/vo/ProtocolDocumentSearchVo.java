package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.entity.CtrContract;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 协议文件查询VO
 */
@Data
public class ProtocolDocumentSearchVo{
    
    /**
     * 客户名称
     */
    private String dzdCompanyName;
    
    /**
     * 供应商名称
     */
    private String ourCompanyName;
    
    /**
     * 对账日期-开始
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dzDateBegin;

    /**
     * 对账日期-结束
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dzDateEnd;

}
