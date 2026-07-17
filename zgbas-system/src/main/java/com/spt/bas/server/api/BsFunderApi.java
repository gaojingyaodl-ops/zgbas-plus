package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsFunder;
import com.spt.bas.server.service.IBsFunderService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "bs/funder")
public class BsFunderApi extends BaseApi<BsFunder> {
	@Autowired
	private IBsFunderService bsFunderService;

	@Override
	public IBaseService<BsFunder> getService() {
		return bsFunderService;
	}

	@PostMapping(value = "findAllByUserId")
	public List<BsFunder> findAllByUserId(@RequestBody Long userId){
		return bsFunderService.findAllByUserId(userId);
	}

}

