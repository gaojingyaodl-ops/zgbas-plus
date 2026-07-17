package com.spt.bas.web.controller.apply;

import com.spt.bas.client.entity.ApplyMatchChain;
import com.spt.bas.client.remote.IApplyMatchChainClient;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 代采赊销中间链条表
 * @Author: gaojy
 * @create 2022/9/14 13:49
 * @version: 1.0
 * @description:
 */
@Controller
@RequestMapping(value = "/apply/matchChain")
public class ApplyMatchChainController extends PageController<ApplyMatchChain, BaseVo> {
    @Autowired
    private IApplyMatchChainClient applyMatchChainClient;

    @Override
    public BaseClient<ApplyMatchChain> getService() {
        return applyMatchChainClient;
    }

    @Override
    protected Map<String, String> getDefaultOrder() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("serialNumber", "asc");
        return map;
    }
}
