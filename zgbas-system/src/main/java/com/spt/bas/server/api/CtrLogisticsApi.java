package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrLogistics;
import com.spt.bas.client.vo.CtrLogisticsVo;
import com.spt.bas.server.service.ICtrLogisticsService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 */
@RestController
@RequestMapping(value = "ctr/logistics")
public class CtrLogisticsApi extends BaseApi<CtrLogistics> {
    @Autowired
    private ICtrLogisticsService ctrLogisticsService;

    @Override
    public IDataService<CtrLogistics> getService() {
        return ctrLogisticsService;
    }


    @PostMapping("saveLogistics")
    public void saveLogistics(@RequestBody CtrLogisticsVo ctrLogisticsVo){
        ctrLogisticsService.saveLogistics(ctrLogisticsVo);
    }

    @PostMapping("findByLogisticsNo")
    public List<CtrLogistics> findByLogisticsNo(@RequestBody String logisticsNo){
        return ctrLogisticsService.findByLogisticsNo(logisticsNo);
    }
}
