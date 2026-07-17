package com.spt.bas.server.service;

import com.spt.bas.client.entity.ClaimRecovery;
import com.spt.tools.jpa.service.IBaseService;

public interface IClaimRecoveryService extends IBaseService<ClaimRecovery> {
    ClaimRecovery findByCorpSerialNo(String corpSerialNo);
}
