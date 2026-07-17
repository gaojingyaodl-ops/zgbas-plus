package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrLogistics;
import com.spt.bas.client.vo.CtrLogisticsVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/logistics",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrLogisticsClient extends BaseClient<CtrLogistics> {
    
    @PostMapping("saveLogistics")
    public void saveLogistics(@RequestBody CtrLogisticsVo ctrLogisticsVo);

    @PostMapping("findByLogisticsNo")
    List<CtrLogistics> findByLogisticsNo(@RequestBody String logisticsNo);
}

