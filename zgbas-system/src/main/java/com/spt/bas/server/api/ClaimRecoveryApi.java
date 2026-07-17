package com.spt.bas.server.api;

import com.spt.bas.client.entity.ClaimRecovery;
import com.spt.bas.server.service.IClaimRecoveryService;
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
@RequestMapping(value = "bs/claimRecovery")
public class ClaimRecoveryApi extends BaseApi<ClaimRecovery> {
    @Autowired
    private IClaimRecoveryService claimRecoveryService;

    @Override
    public IDataService<ClaimRecovery> getService() {
        return claimRecoveryService;
    }

    @PostMapping("findBycorpSerialNo")
    ClaimRecovery findBycorpSerialNo(@RequestBody String corpSerialNo){
        return claimRecoveryService.findByCorpSerialNo(corpSerialNo);
    }
}
