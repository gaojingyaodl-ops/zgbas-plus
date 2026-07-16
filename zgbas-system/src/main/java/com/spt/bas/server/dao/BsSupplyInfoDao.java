package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsSupplyInfo;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsSupplyInfoDao extends BaseDao<BsSupplyInfo> {

    BsSupplyInfo findByWxUserId(Long wxUserId);

    BsSupplyInfo findByCompanyId(Long companyId);

}
