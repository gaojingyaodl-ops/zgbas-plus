package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyDiscuss;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyDiscussService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "apply/discuss")
public class ApplyDiscussApi extends BaseApi<ApplyDiscuss> {
    @Autowired
    private IApplyDiscussService applyDiscussService;

    @Override
    public IBaseService<ApplyDiscuss> getService() {
        return applyDiscussService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        applyDiscussService.updateFileId(vo.getId(), vo.getFileId());
    }
}

