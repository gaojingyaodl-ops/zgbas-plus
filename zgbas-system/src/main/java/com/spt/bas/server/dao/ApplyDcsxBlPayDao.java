package com.spt.bas.server.dao;


import com.spt.bas.client.entity.ApplyCtrContractFactor;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplyDcsxBlPayDao extends BaseDao<ApplyPay> {

    ApplyCtrContractFactor findByApproveId(Long approveid);



}

