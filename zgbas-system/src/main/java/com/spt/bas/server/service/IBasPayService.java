package com.spt.bas.server.service;

import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BasPay;
import com.spt.bas.client.vo.BasPayTicketVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IBasPayService extends IBaseService<BasPay> {

	BasPay newEntity(BasContract contract);

	void updateFileId(Long id, String fileId);

	/**保存发票信息*/
	BasPay saveTicket(BasPayTicketVo vo);

	void payNotice(BasPay pay, Long userId);
}

