package com.spt.bas.server.api;

import com.spt.bas.client.vo.DeptNode;
import com.spt.bas.client.vo.SysInitRequestVo;
import com.spt.bas.server.service.ISysInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "sys/init")
public class SysInitApi {

	@Autowired
	private ISysInitService sysInitService;

	@PostMapping("systemInit")
	public void systemInit(@RequestBody SysInitRequestVo sysVo){
		sysInitService.SystemInit(sysVo);
	}

	@PostMapping("createUser")
	public void createUser(@RequestBody SysInitRequestVo sysVo){
		sysInitService.createUser(sysVo);
	}

	@PostMapping("getDeptTree")
	public List<DeptNode> getDeptTree() {
		return sysInitService.getDeptTree();
	}
}
