package com.spt.bas.server.api.fund;

import com.spt.bas.client.entity.ApplyFundRecharge;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyFundRechargeService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author MoonLight
 * @Date 2024/7/15 9:33
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "apply/fundRecharge")
public class ApplyFundRechargeApi extends BaseApi<ApplyFundRecharge> {

    @Resource
    private IApplyFundRechargeService applyFundRechargeService;

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        applyFundRechargeService.updateFileId(vo.getId(), vo.getFileId());
    }

    @Override
    public IBaseService<ApplyFundRecharge> getService() {
        return applyFundRechargeService;
    }
}
