package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsContractTemplate;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IBsContractTemplateService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "bs/contractTemplate")
public class BsContractTemplateApi extends BaseApi<BsContractTemplate> {
	@Autowired
	private IBsContractTemplateService contractTemplateService;

	@Override
	public IBaseService<BsContractTemplate> getService() {
		return contractTemplateService;
	}

	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		contractTemplateService.updateFileId(vo.getId(), vo.getFileId());
	}

	@PostMapping("findByTemplateTagAndEnterpriseId")
	public BsContractTemplate findByTemplateTagAndEntrpriseId(@RequestBody BsContractTemplate template){
		return contractTemplateService.findByTemplateTagAndEnterpriseId(template);
	}

	@PostMapping("findByContractTypeAndEnterpriseId")
	public List<BsContractTemplate> findByContractTypeAndEnterpriseId(@RequestBody BsContractTemplate template){
		return contractTemplateService.findByContractTypeAndEnterpriseId(template);
	}

	@PostMapping("findByContractsell")
	public List<BsContractTemplate> findByContractsell(@RequestBody BsContractTemplate template){
		return contractTemplateService.findByContractsell(template);
	}

	@PostMapping("findByIdAndEnterpriseId")
	public BsContractTemplate findByIdAndEnterpriseId(@RequestBody BsContractTemplate template) {
		return contractTemplateService.findByIdAndEnterpriseId(template);
	}

}
