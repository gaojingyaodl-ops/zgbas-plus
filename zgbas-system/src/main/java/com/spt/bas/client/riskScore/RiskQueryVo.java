package com.spt.bas.client.riskScore;

import com.spt.bas.client.entity.BsCompany;
import lombok.Data;

@Data
public class RiskQueryVo {
    private String companyName;
    //企业信用代码
    private String companyCreditNo;
    private BsCompany bsCompany;
}
