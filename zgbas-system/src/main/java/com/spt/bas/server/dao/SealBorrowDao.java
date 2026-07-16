package com.spt.bas.server.dao;

import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.SealBorrow;

public interface SealBorrowDao extends BaseDao<SealBorrow> {

	@Query("from SealBorrow s where now() >= s.endDate and s.sealStatus = 'D'")
	public List<SealBorrow> findSealBorrowOverdue();
}

