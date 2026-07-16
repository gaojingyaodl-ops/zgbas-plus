package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApplyCtrDCSX;
import lombok.Data;

@Data
public class ProtocolDocumentDcsxVo extends ApplyCtrDCSX {
    private String productsName;

    private String accountName;
    private String bankName;
    private String bankAccount;
    private String ourCompanyNo;
    private String companyNo;
}
