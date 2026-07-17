package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyInvalid;
import com.spt.bas.client.vo.ApplyInvalidApproveVo;
import com.spt.bas.client.vo.ApplyInvalidDetailVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyInvalidService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "apply/invalid")
public class ApplyInvalidApi extends BaseApi<ApplyInvalid> {
    @Autowired
    private IApplyInvalidService applyInvalidService;

    @Override
    public IBaseService<ApplyInvalid> getService() {
        return applyInvalidService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        applyInvalidService.updateFileId(vo.getId(), vo.getFileId());
    }

    @PostMapping("queryInvalidDetail")
    public ApplyInvalidDetailVo queryInvalidDetail(@RequestBody ApplyInvalid applyInvalid) {
        return applyInvalidService.queryInvalidDetail(applyInvalid);
    }

    @PostMapping("queryInvalidApproveList")
    public List<ApplyInvalidApproveVo> queryInvalidApproveList(@RequestBody ApplyInvalid applyInvalid){
        return applyInvalidService.queryInvalidApproveList(applyInvalid);
    }
}

