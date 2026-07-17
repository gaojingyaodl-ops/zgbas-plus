package com.spt.bas.server.service;

import com.spt.bas.client.vo.DeptNode;
import com.spt.bas.client.vo.SysInitRequestVo;

import java.util.List;

public interface ISysInitService {
	
	void SystemInit(SysInitRequestVo sysVo);
	
	void createUser(SysInitRequestVo sysVo);

	/**
	 * 企业完整组织结构
	 * @return
	 */
	List<DeptNode> getDeptTree();


}
