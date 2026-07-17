package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyMatters;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyMattersService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/apply/matters")
public class ApplyMattersApi extends BaseApi<ApplyMatters> {

    @Autowired
    private IApplyMattersService applyMattersService;
    
    @Override
    public IDataService<ApplyMatters> getService() {
        return applyMattersService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo){
        applyMattersService.updateFileId(vo.getId(), vo.getFileId());
    }
}
