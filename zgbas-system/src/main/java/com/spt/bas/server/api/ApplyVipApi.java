package com.spt.bas.server.api;
import com.spt.bas.client.entity.ApplyVip;
import com.spt.bas.server.service.IApplyVipService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;


@RestController
@RequestMapping(value = "apply/vip")
public class ApplyVipApi extends BaseApi<ApplyVip> {


   @Resource
    private IApplyVipService applyVipService;

    @Override
    public IDataService<ApplyVip> getService() {
        return applyVipService;
    }
}
