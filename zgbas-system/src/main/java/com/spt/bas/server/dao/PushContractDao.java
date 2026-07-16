package com.spt.bas.server.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.PushContract;
import com.spt.tools.jpa.dao.BaseDao;

public interface PushContractDao extends BaseDao<PushContract> {

	@Query("from PushContract where pushType = ?1 and targetCode = ?2 and pushFlg = false")
	List<PushContract> findByPushTypeAndTargetCode(String pushType, String targetCode);

	@Transactional
	@Modifying
	void deleteByPushContractNo(String pushContractNo);

	List<PushContract> findByPushContractNo(String pushContractNo);
}

