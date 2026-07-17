package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BasDelivery;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IBasDeliveryService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bas/delivery")
public class BasDeliveryApi extends BaseApi<BasDelivery> {
	@Autowired
	private IBasDeliveryService basDeliveryService;
	
	@Override
	public IBaseService<BasDelivery> getService() {
		return basDeliveryService;
	}
	
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		basDeliveryService.updateFileId(vo.getId(), vo.getFileId());
	}
}

