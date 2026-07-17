package com.spt.bas.server.service;

import com.spt.bas.client.entity.ClaimBuyer;
import com.spt.tools.jpa.service.IBaseService;

public interface IClaimBuyerService extends IBaseService<ClaimBuyer> {

    ClaimBuyer findBycorpSerialNo(String corpSerialNo);

}
