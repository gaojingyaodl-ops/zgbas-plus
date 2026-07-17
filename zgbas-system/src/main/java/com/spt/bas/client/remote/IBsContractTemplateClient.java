package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsContractTemplate;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/contractTemplate",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsContractTemplateClient extends BaseClient<BsContractTemplate> {

	@PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);

	@PostMapping("findByTemplateTagAndEnterpriseId")
    BsContractTemplate findByTemplateTagAndEnterpriseId(@RequestBody BsContractTemplate template);

	@PostMapping("findByContractTypeAndEnterpriseId")
    List<BsContractTemplate> findByContractTypeAndEnterpriseId(@RequestBody BsContractTemplate template);

	@PostMapping("findByContractsell")
    List<BsContractTemplate> findByContractsell(@RequestBody BsContractTemplate template);

	@PostMapping("findByIdAndEnterpriseId")
	BsContractTemplate findByIdAndEnterpriseId(@RequestBody BsContractTemplate template);


}

