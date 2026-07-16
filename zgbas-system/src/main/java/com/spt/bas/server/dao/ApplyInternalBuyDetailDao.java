package com.spt.bas.server.dao;

import java.util.List;

import com.spt.bas.client.entity.ApplyInternalBuyDetail;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplyInternalBuyDetailDao extends BaseDao<ApplyInternalBuyDetail> {

	List<ApplyInternalBuyDetail> findByApplyInternalBuyId(Long applyInternalBuyId);

	ApplyInternalBuyDetail findByApplyInternalBuyIdAndDetailType(Long applyInternalBuyId, String detailType);

	List<ApplyInternalBuyDetail> findByStockDetailId(Long stockDetailId);
}

