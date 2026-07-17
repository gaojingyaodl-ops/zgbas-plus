package com.spt.bas.client.remote;


import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsNotice;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/bsNotice",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IBsNoticeClient   extends BaseClient<BsNotice> {
    @RequestMapping("findLast")
     BsNotice findLast();
    @RequestMapping("findLimit5")
     List<BsNotice> findLimit5(@RequestBody String deptId);

    @RequestMapping("findLimit")
    List<BsNotice> findLimit();

}
