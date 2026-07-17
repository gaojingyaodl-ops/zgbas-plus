package com.spt.bas.server.api;

import com.spt.bas.server.service.IBsCompanyIndustryService;
import com.spt.bas.server.util.BsCompanyIndustryUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsCompanyIndustry;


@RestController
@RequestMapping(value = "bs/industry")
public class BsCompanyIndustryApi extends BaseApi<BsCompanyIndustry> {
	@Autowired
	private IBsCompanyIndustryService bsCompanyIndustryService;
	
	@Override
	public IBaseService<BsCompanyIndustry> getService() {
		return bsCompanyIndustryService;
	}
	
	@PostMapping("getAllIndustryTree")
	public List<EasyTreeNode> getAllIndustryTree(){
		return BsCompanyIndustryUtil.getAllTree();
	}
	
}

