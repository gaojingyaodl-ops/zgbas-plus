package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsProductType;
import com.spt.bas.client.entity.BsProductTypeAccess;
import com.spt.bas.server.service.IBsProductTypeAccessService;
import com.spt.bas.server.service.IBsProductTypeService;
import com.spt.bas.server.util.ProductTypeUtility;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "bs/productType")
public class BsProductTypeApi extends BaseApi<BsProductType> {
	@Autowired
	private IBsProductTypeService bsProductTypeService;
	@Autowired
	private IBsProductTypeAccessService productTypeAccessService;
	
	@Override
	public IBaseService<BsProductType> getService() {
		return bsProductTypeService;
	}
	
	@PostMapping("findAllProductTree")
	List<EasyTreeNode> findAllProductTree(@RequestBody Long enterpriseId){
		return ProductTypeUtility.getAllTree(enterpriseId);
	}
	
//	@PostMapping("findAllProductTreeSelect")
//	List<EasyTreeNode> findAllProductTreeSelect(@RequestBody List<BsProductTypeAccess> lstSelect){
//		return ProductTypeUtility.getAllTree(lstSelect);
//	}
	@PostMapping("findAllProductTreeSelect")
	List<EasyTreeNode> findAllProductTreeSelect(@RequestBody Long enterpriseId){
		List<BsProductTypeAccess> lstSelect = productTypeAccessService.findByEnterpriseId(enterpriseId);
		return ProductTypeUtility.getAllTree(lstSelect);
	}

	@PostMapping("findProductTypeCode")
	public BsProductType findProductTypeCode(@RequestBody String typeCode){
		return bsProductTypeService.findProductTypeCode(typeCode);
	}

	@PostMapping("findAllByEnterpriseId")
	public List<BsProductType> findAllByEnterpriseId(@RequestBody Long enterpriseId){
		return bsProductTypeService.findAllByEnterpriseId(enterpriseId);
	}

	@PostMapping("findAllProductAlAndHg")
	List<BsProductType> findAllProductAlAndHg(){
		return bsProductTypeService.findAllProductAlAndHg();
	}

}

