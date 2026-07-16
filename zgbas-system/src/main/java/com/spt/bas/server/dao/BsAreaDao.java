package com.spt.bas.server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.BsArea;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsAreaDao extends BaseDao<BsArea> {

	@Query(" FROM BsArea where grand=1 ORDER BY id ")
	public List<BsArea> findTopLevel();
	
	public List<BsArea> findByParentId(String parentId);
	
	public List<BsArea> findByCode(String code);
	
	@Query("from BsArea where code = ?1")
	public BsArea findByAreaCode(String code);
	
	public List<BsArea> findByGrand(Integer grand);
}
