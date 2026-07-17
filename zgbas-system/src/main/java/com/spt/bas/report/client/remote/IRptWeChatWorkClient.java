package com.spt.bas.report.client.remote;

import com.spt.bas.client.vo.WeChatWorkVo;
import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/we/chat/work",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptWeChatWorkClient {

	@PostMapping("pushWeChatWorkLeaderboard")
	void pushWeChatWorkLeaderboard(@RequestBody WeChatWorkVo vo);

	@PostMapping("pushWeChantWorkLeaderboardForCustomerDevelop")
	void pushWeChantWorkLeaderboardForCustomerDevelop(@RequestBody WeChatWorkVo vo);
}
