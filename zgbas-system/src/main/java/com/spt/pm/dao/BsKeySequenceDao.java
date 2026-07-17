package com.spt.pm.dao;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;

import com.spt.pm.entity.BsKeySequence;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsKeySequenceDao extends BaseDao<BsKeySequence> {
	BsKeySequence findByEnterpriseIdAndKeyCategory(Long enterpriseId, String keyCategory);
	
	BsKeySequence findByKeyCategory(String keyCategory);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<BsKeySequence> findById(Long id);
	
	
}
