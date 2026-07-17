package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyEntrust;
import com.spt.bas.server.service.IApplyEntrustService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/7/27 15:24
 */
@RestController
@RequestMapping(value = "/api/applyEntrust")
public class ApplyEntrustApi extends BaseApi<ApplyEntrust> {

    @Autowired
    private IApplyEntrustService applyEntrustService;

    @Override
    public IDataService<ApplyEntrust> getService() {
        return applyEntrustService;
    }

    /**
     * 根据公司名称查询是否用经办人
     * 不能根据companyId 查询,历史数据中没有保存companyId
     * @param companyName 公司名词
     * @return true-已经绑定过，false-没有绑定过
     */
    @PostMapping("/findIsHaveEntrustUserByCompanyName")
    Boolean findIsHaveEntrustUserByCompanyName(@RequestParam(value = "companyName",required = false) String companyName){
        return applyEntrustService.findIsHaveEntrustUserByCompanyName(companyName);
    }
}
