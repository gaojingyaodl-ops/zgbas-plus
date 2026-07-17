package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyIndustry;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/industry",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsCompanyIndustryClient extends BaseClient<BsCompanyIndustry> {

	@PostMapping("getAllIndustryTree")
	public List<EasyTreeNode> getAllIndustryTree();
}

