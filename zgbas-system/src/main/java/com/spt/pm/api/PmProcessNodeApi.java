package com.spt.pm.api;

import com.spt.pm.entity.PmProcessNode;
import com.spt.pm.service.IPmProcessNodeService;
import com.spt.pm.vo.PmProcessNodeRefVo;
import com.spt.pm.vo.PmProcessNodeRespVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "pm/processNode")
public class PmProcessNodeApi extends BaseApi<PmProcessNode> {
	@Autowired
	private IPmProcessNodeService pmProcessNodeService;

	@Override
	public IBaseService<PmProcessNode> getService() {
		return pmProcessNodeService;
	}

	@RequestMapping(value = "findNodeList")
	public List<PmProcessNode> findNodeList(@RequestBody Long enterpriseId){
		return pmProcessNodeService.findNodeList(enterpriseId);
	}

    @RequestMapping(value = "findNodePage")
    Page<PmProcessNodeRespVo> findNodePage(@RequestBody PageSearchVo pageSearchVo) {
        return pmProcessNodeService.findNodePage(pageSearchVo);
    }

}

