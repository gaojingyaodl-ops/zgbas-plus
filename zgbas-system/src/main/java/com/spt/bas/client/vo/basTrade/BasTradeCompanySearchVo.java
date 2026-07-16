package com.spt.bas.client.vo.basTrade;

import lombok.Data;

@Data
public class BasTradeCompanySearchVo {
    private Long enterpriseId;
    private Long companyId;
    private String companyName;
    private Long matchUserId;
    private Long sharedUserId;
    private String companyType;		// 客户类型
}
