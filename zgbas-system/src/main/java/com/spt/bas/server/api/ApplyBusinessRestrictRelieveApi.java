package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyBusinessRestrictRelieve;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyBusinessRestrictRelieveService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/apply/businessRestrictRelieve")
public class ApplyBusinessRestrictRelieveApi extends BaseApi<ApplyBusinessRestrictRelieve> {

    @Autowired
    private IApplyBusinessRestrictRelieveService applyBusinessRestrictRelieveService;

    @Override
    public IDataService<ApplyBusinessRestrictRelieve> getService() {
        return applyBusinessRestrictRelieveService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        applyBusinessRestrictRelieveService.updateFileId(vo.getId(), vo.getFileId());
    }
}
