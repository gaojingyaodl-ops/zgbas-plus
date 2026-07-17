package com.spt.bas.server.service;


import com.spt.bas.client.entity.PenaltyInterest;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;


/**
 * @Author: wm
 * @create  2022/06/08 10:22
 * @version: 1.0
 * @description:
 */
public interface IPenaltyInterestService extends IBaseService<PenaltyInterest> {

    void updateInterStatus(String interestStatus,Long bizId);

    List<String> findContractNoByCompanyId(String companyId);
}
