package com.spt.bas.server.api;

import com.spt.bas.client.entity.ClaimBuyer;
import com.spt.bas.server.service.IClaimBuyerService;
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
 * @Date: Created in 2021-03-03 11:09
 */
@RestController
@RequestMapping(value = "bs/claimBuyer")
public class ClaimBuyerApi extends BaseApi<ClaimBuyer> {

    @Autowired
    private IClaimBuyerService claimBuyerService;

    @Override
    public IDataService<ClaimBuyer> getService() {
        return claimBuyerService;
    }

    @PostMapping("findBycorpSerialNo")
    ClaimBuyer findBycorpSerialNo(@RequestBody String corpSerialNo){
        return claimBuyerService.findBycorpSerialNo(corpSerialNo);
    }
}
