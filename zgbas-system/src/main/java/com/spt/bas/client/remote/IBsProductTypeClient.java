package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsProductType;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/productType",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsProductTypeClient extends BaseClient<BsProductType> {

	@PostMapping("findAllProductTree")
	List<EasyTreeNode> findAllProductTree(@RequestBody Long enterpriseId);
	
	@PostMapping("findAllProductTreeSelect")
	List<EasyTreeNode> findAllProductTreeSelect(@RequestBody Long enterpriseId);

	@PostMapping("push2Hq")
	String push2Hq(@RequestBody Long enterpriseId);
	
	@PostMapping("findProductTypeCode")
	public BsProductType findProductTypeCode(@RequestBody String typeCode);

	@PostMapping("findAllByEnterpriseId")
	List<BsProductType> findAllByEnterpriseId(@RequestBody Long enterpriseId);

	@PostMapping("findAllProductAlAndHg")
	List<BsProductType> findAllProductAlAndHg();

}

