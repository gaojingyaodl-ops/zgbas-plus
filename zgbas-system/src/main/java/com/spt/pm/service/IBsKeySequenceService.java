package com.spt.pm.service;

import com.spt.pm.entity.BsKeySequence;
import com.spt.tools.jpa.service.IBaseService;

public interface IBsKeySequenceService extends IBaseService<BsKeySequence> {

	String getNextKey(String Category, Long enterpriseId);

	String getNextKey(String category, Long enterpriseId, String deptAbbr);
	
	boolean initKeySequence(String prefix, Long enterpriseId);

}
