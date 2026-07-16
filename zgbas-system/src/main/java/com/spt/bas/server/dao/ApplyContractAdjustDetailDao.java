package com.spt.bas.server.dao;

import java.util.List;

import com.spt.bas.client.entity.ApplyContractAdjustDetail;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplyContractAdjustDetailDao extends BaseDao<ApplyContractAdjustDetail> {

	List<ApplyContractAdjustDetail> findByContractAdjustId(Long contractAdjustId);

	ApplyContractAdjustDetail findByContractAdjustIdAndDetailType(Long contractAdjustId, String detailType);
}

