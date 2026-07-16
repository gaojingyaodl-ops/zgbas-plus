package com.spt.bas.client.vo;

import com.spt.bas.client.entity.CtrContract;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProtocolDocumentContractVo extends CtrContract {

    private String productCd;
    private String productName;
    private String brandNumber;
    private BigDecimal unPayOverdueAmount;

    private String realPayDateStr = "";
    private String overdueLateFees = "";
    private BigDecimal overdueLateFeeSum;

    private String accountName = "";
    private String bankName = "";
    private String bankAccount = "";

    private String ourCompanyNo;
    private String companyNo;



}
