package com.spt.bas.server.api;


import com.spt.bas.client.entity.SealUsageDCSX;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.client.vo.SealUsageDcsxVo;
import com.spt.bas.server.service.ISealUsageDCSXService;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "seal/usageDCSX")
public class SealUsageDCSXApi extends BaseApi<SealUsageDCSX> {

    @Autowired
    private ISealUsageDCSXService sealUsageDCSXService;

    @Override
    public IDataService<SealUsageDCSX> getService() {
        return sealUsageDCSXService;
    }

    @RequestMapping(value = "updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        sealUsageDCSXService.updateFileId(vo.getId(), vo.getFileId());
    }

    @PostMapping(value = "addSealUsageUpdateHis")
    public void addSealUsageUpdateHis(@RequestBody PmApproveSaveVo startVo) {
        sealUsageDCSXService.addSealUsageUpdateHis(startVo);
    }
    
    @PostMapping(value = "updateCfcaContractNo")
    void updateCfcaContractNo(@RequestBody SealUsageDcsxVo entity){
        sealUsageDCSXService.updateCfcaContractNo(entity);
    }
}
