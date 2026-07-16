package com.spt.bas.client.vo.risk;

import lombok.Data;

@Data
public class QueryRiskScoreDetailVo {
    private Long companyId;
    private String scoreItem;
    private String scoreCompanyType;
}
