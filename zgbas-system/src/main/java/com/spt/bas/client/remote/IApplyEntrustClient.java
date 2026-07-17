package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyEntrust;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/7/27 15:21
 */
@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME+"/api/applyEntrust", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyEntrustClient extends BaseClient<ApplyEntrust> {

    /**
     * 根据公司名称查询是否用经办人
     * 不能根据companyId 查询,历史数据中没有保存companyId
     *
     * @param companyName 公司名词
     * @return true-已经绑定过，false-没有绑定过
     */
    @PostMapping("/findIsHaveEntrustUserByCompanyName")
    Boolean findIsHaveEntrustUserByCompanyName(@RequestParam(value = "companyName",required = false) String companyName);
}
