package com.spt.bas.server.api.sign;

import com.spt.bas.client.entity.BizSign;
import com.spt.bas.server.service.IBizSignService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "biz/sign")
public class BizSignApi extends BaseApi<BizSign> {
    @Resource
    private IBizSignService bizSignService;
    @Override
    public IDataService<BizSign> getService() {
        return bizSignService;
    }

    @PostMapping(value = "getBizSignList")
    public List<BizSign> getBizSignList(@RequestBody Long approveId){
        return bizSignService.getSignList(approveId);
    }
}
