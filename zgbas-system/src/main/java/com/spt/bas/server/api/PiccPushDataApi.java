package com.spt.bas.server.api;

import com.spt.bas.client.entity.PiccPushData;
import com.spt.bas.server.service.IPiccPushDataService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  PICC推送报文接口
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-03-04 15:28
 */
@RestController
@RequestMapping(value = "bs/piccPushData")
public class PiccPushDataApi extends BaseApi<PiccPushData> {
    @Autowired
    private IPiccPushDataService piccPushDataService;

    @Override
    public IDataService<PiccPushData> getService() {
        return piccPushDataService;
    }


    /**
     * 赊销发起记录
     * @param contractNo
     * @return
     */
    @PostMapping("findContractNoSx")
    public PiccPushData findContractNoSx(@RequestParam String contractNo) {
        return piccPushDataService.findContractNoSx(contractNo);
    }

    /**
     * 回款发起记录
     * @param contractNo
     * @return
     */
    @PostMapping("findContractNoRecovery")
    public PiccPushData findContractNoRecovery(@RequestParam String contractNo) {
        return piccPushDataService.findContractNoRecovery(contractNo);
    }

}
