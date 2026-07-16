package com.spt.bas.server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.CtrProductFee;
import com.spt.tools.jpa.dao.BaseDao;

public interface CtrProductFeeDao extends BaseDao<CtrProductFee> {
	
	CtrProductFee findByApplyDeliveryIdAndEnterpriseId(Long applyDeliveryId, Long enterpriseId);
	
//	@Query("SELECT SUM(f.ccFeeXs),SUM(f.ccFeeCg),SUM(f.ccFeeRuku),SUM(f.ccFeeQt),SUM(f.wlFeeYs),SUM(f.wlFeeZc),"
//			+ "SUM(f.wlFeeDfcc),SUM(f.wlFeeQt) FROM CtrProductFee f,ApplyDeliveryOut o WHERE f.contractId = ?1 "
//			+ "AND f.applyDeliveryId = o.id AND o.status = 'D'")
//	List<Object[]> findSumAmount(Long contractId);
	
	@Query("SELECT f FROM CtrProductFee f,ApplyDelivery a WHERE f.contractId = ?1 AND f.applyDeliveryId = a.id AND a.operation = '0'")
	List<CtrProductFee> findByContractId(Long contractId);
}

