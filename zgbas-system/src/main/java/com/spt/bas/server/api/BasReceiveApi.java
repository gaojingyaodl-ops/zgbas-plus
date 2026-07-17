package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BasReceive;
import com.spt.bas.client.vo.BasReceiveVo;
import com.spt.bas.server.service.IBasReceiveService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bas/receive")
public class BasReceiveApi extends BaseApi<BasReceive> {
	@Autowired
	private IBasReceiveService basReceiveService;
	@Override
	public IBaseService<BasReceive> getService() {
		return basReceiveService;
	}
	
	@PostMapping("saveStatus")
	public void saveStatus(@RequestBody BasReceive receive){
		basReceiveService.saveStatus(receive);
	}
	
	@PostMapping("findByReceiveVo")
	BasReceive findByReceiveVo(@RequestBody BasReceiveVo vo){
		return basReceiveService.findByReceiveVo(vo);
	}
	
}

