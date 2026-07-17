package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsSupplyInfo;
import com.spt.bas.server.service.IBsSupplyInfoService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *     企业补充资料信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-28 12:00
 */
@RestController
@RequestMapping(value = "bs/supplyInfo")
public class BsSupplyInfoApi extends BaseApi<BsSupplyInfo> {

    @Autowired
    private IBsSupplyInfoService bsSupplyInfoService;

    @Override
    public IDataService<BsSupplyInfo> getService() {
        return bsSupplyInfoService;
    }

    @PostMapping(value = "findByWxUserId")
    public BsSupplyInfo findByWxUserId(@RequestBody Long wxUserId) {
        return bsSupplyInfoService.findByWxUserId(wxUserId);
    }

    @PostMapping(value = "findByCompanyId")
    public BsSupplyInfo findByCompanyId(@RequestBody Long companyId) {
        return bsSupplyInfoService.findByCompanyId(companyId);
    }
}
