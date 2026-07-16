package com.spt.bas.client.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractPiccInsuranceVo {

    private Long id;
    private String contractNo;
    private BigDecimal insuranceRate;
    private BigDecimal insuranceAmount;
    private Boolean insuranceFlag;
    private String ourCompanyName;
    private BigDecimal totalAmount;
    private Long approveId;

}
