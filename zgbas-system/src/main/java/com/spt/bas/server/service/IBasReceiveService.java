package com.spt.bas.server.service;

import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BasReceive;
import com.spt.bas.client.vo.BasReceiveVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IBasReceiveService extends IBaseService<BasReceive> {
	
	//保存收款信息
	public void saveReceive(BasContract bs);

	public void saveStatus(BasReceive receive);
	
	BasReceive findByReceiveVo(BasReceiveVo vo);
	
}

