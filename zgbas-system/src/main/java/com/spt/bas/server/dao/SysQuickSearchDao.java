package com.spt.bas.server.dao;

import com.spt.bas.client.entity.SysQuickSearch;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SysQuickSearchDao extends BaseDao<SysQuickSearch> {

	@Query(" FROM SysQuickSearch where moduleUrl=?1 and userId=?2 ORDER BY id ")
	List<SysQuickSearch> findListByUserIdAndModuleUrl(String moduleUrl,Long userId);
	
}
