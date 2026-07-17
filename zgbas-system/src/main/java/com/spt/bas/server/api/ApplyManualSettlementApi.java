package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyManualSettlement;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyManualSettlementService;
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
 * @Date: Created in 2021-04-21 15:45
 */
@RestController
@RequestMapping(value = "apply/manualSettlement")
public class ApplyManualSettlementApi extends BaseApi<ApplyManualSettlement> {
    @Autowired
    private IApplyManualSettlementService applyManualSettlementService;

    @Override
    public IDataService<ApplyManualSettlement> getService() {
        return applyManualSettlementService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        applyManualSettlementService.updateFileId(vo.getId(), vo.getFileId());
    }
}
