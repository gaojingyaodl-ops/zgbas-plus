package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsIncreaseInfo;
import com.spt.bas.server.service.IBsIncreaseInfoService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-27 09:50
 */
@RestController
@RequestMapping(value = "bs/increaseInfo")
public class BsIncreaseInfoApi extends BaseApi<BsIncreaseInfo> {

    @Autowired
    private IBsIncreaseInfoService bsIncreaseInfoService;

    @Override
    public IDataService<BsIncreaseInfo> getService() {
        return bsIncreaseInfoService;
    }

    @PostMapping("findByCompanyId")
    BsIncreaseInfo findByCompanyId(@RequestBody Long companyId) {
        return bsIncreaseInfoService.findByCompanyId(companyId);
    }

}
