package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.entity.PmProcessNode;
import com.spt.pm.vo.PmProcessNodeRefVo;
import com.spt.pm.vo.PmProcessNodeRespVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/pm/processNode", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IPmProcessNodeClient extends BaseClient<PmProcessNode> {

    @RequestMapping(value = "findNodeList")
    List<PmProcessNode> findNodeList(@RequestBody Long enterpriseId);

    @RequestMapping(value = "findNodePage")
    PageDown<PmProcessNodeRespVo> findNodePage(@RequestBody PageSearchVo pageSearchVo);
}

