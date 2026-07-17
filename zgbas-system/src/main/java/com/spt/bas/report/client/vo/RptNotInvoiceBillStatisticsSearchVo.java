package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


@Data
public class RptNotInvoiceBillStatisticsSearchVo extends PageSearchVo {

    private Long id;

    /**
     * 我方抬头
     */
    private String ourCompanyName;
    private List<String> ourCompanyNameList;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 化工业务员ID集合
     */
    private List<Long> hgMatchUserIdList;

    /**
     * 资金方权限
     */
    private Boolean funderFlg = false;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 查询付全款时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellPayFullDateStart;

    /**
     * 查询付全款时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellPayFullDateEnd;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
