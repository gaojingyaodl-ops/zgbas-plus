package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.BsArea;
import com.spt.bas.client.vo.CompanyAreaVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IBsAreaService extends IBaseService<BsArea>{

	// 查询顶级市区
	List<BsArea> findTopLevel();
	//按parent_id查询出下级地区
	List<BsArea> findByparentId(String pid);
	//查询是否有上级地区
	List<BsArea> findByCode(String code);
	
	public CompanyAreaVo getAreaVo(Long id);


    List<BsArea> getAllArea();
}
