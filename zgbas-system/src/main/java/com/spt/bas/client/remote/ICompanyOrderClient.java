package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.vo.CompanyOrderResVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/company/order", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)  
public interface ICompanyOrderClient {

    /**
     * 查询交易记录
     * @param minute
     * @return
     */
    @PostMapping("findCompanyOrder")
    List<CompanyOrderResVo> findCompanyOrder(@RequestBody String minute);

}

