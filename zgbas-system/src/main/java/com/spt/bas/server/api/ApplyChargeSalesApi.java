package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.vo.BsBankVo;
import com.spt.bas.server.service.ApplyChargeSalesService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "apply/chargeSales")
public class ApplyChargeSalesApi extends BaseApi<ApplyMatch> {
    @Autowired
    ApplyChargeSalesService applyChargeSalesService;

    @Override
    public IDataService<ApplyMatch> getService() {
        return applyChargeSalesService;
    }

    @PostMapping(value = "getSpecialBank")
    public BsBankVo getSpecialBank(@RequestBody Long enterpriseId){
        return applyChargeSalesService.getSpecialBank(enterpriseId);
    }
    
    @PostMapping(value = "parseCtrDcsxByApproveId")
    public ApplyCtrDCSX parseCtrDcsxByApproveId(@RequestBody Long approveId){
        return applyChargeSalesService.parseCtrDcsxByApproveId(approveId);
    }
}
