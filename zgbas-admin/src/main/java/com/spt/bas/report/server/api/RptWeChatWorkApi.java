package com.spt.bas.report.server.api;

import com.spt.bas.client.vo.WeChatWorkVo;
import com.spt.bas.report.server.service.IRptWeChatWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/we/chat/work")
public class RptWeChatWorkApi {
	@Autowired
	private IRptWeChatWorkService weChatWorkService;
	
	@PostMapping("pushWeChatWorkLeaderboard")
	public void pushWeChatWorkLeaderboard(@RequestBody WeChatWorkVo vo){
		weChatWorkService.pushWeChatWorkLeaderboard(vo);
	}

	@PostMapping("pushWeChantWorkLeaderboardForCustomerDevelop")
	public void pushWeChantWorkLeaderboardForCustomerDevelop(@RequestBody WeChatWorkVo vo){
		weChatWorkService.pushWeChantWorkLeaderboardForCustomerDevelop(vo);
	}
}
