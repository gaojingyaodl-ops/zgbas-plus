package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BasPay;
import com.spt.bas.client.vo.BasPayTicketVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IBasPayService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bas/pay")
public class BasPayApi extends BaseApi<BasPay> {
	@Autowired
	private IBasPayService basPayService;
	
	@Override
	public IBaseService<BasPay> getService() {
		return basPayService;
	}
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		basPayService.updateFileId(vo.getId(), vo.getFileId());
	}
	
	/**保存发票信息*/
	@PostMapping("saveTicket")
	public BasPay saveTicket(@RequestBody BasPayTicketVo vo) {
		return basPayService.saveTicket(vo);
	}

}

