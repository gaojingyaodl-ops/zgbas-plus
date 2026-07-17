package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsMatchProfitsConfig;
import com.spt.bas.client.entity.BsProductConfig;
import com.spt.bas.client.vo.BsProductConfigVo;
import com.spt.bas.server.service.IBsMatchProfitsConfigService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: gaojy
 * @create 2022/2/8 14:13
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping(value = "bs/matchConfig")
public class BsMatchProfitsConfigApi extends BaseApi<BsMatchProfitsConfig> {
    @Autowired
    private IBsMatchProfitsConfigService matchProfitsConfigService;

    @Override
    public IBaseService<BsMatchProfitsConfig> getService() {
        return matchProfitsConfigService;
    }
    @RequestMapping("findByMathUserId")
    public List<BsMatchProfitsConfig> findByMathUserId(@RequestBody Long mathUserId) {
       return matchProfitsConfigService.findByMathUserId(mathUserId);
    }
}
