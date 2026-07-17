package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.spt.bas.client.entity.BsProductTypeAccess;
import com.spt.bas.client.vo.BsProductTypeAccessSaveVo;
import com.spt.bas.server.service.IBsProductTypeAccessService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/productTypeAccess")
public class BsProductTypeAccessApi extends BaseApi<BsProductTypeAccess> {
	@Autowired
	private IBsProductTypeAccessService bsProductTypeAccessService;
	
	@Override
	public IBaseService<BsProductTypeAccess> getService() {
		return bsProductTypeAccessService;
	}
	@PostMapping("findByEnterpriseId")
	public List<BsProductTypeAccess> findByEnterpriseId(@RequestBody Long enterpriseId){
		return bsProductTypeAccessService.findByEnterpriseId(enterpriseId);
	}
	
	@PostMapping("saveAccess")
	public void saveAccess(@RequestBody BsProductTypeAccessSaveVo vo) throws ApplicationException {
		bsProductTypeAccessService.saveAccess(vo.getEnterpriseId(), vo.getProductCds());
	}
	
	@PostMapping("countByProductCdAndEnterpriseId")
	public void countByProductCdAndEnterpriseId(@RequestBody BsProductTypeAccess vo){
		bsProductTypeAccessService.countByProductCdAndEnterpriseId(vo);
	}

	@GetMapping("reFresh")
	public void reFreshCache(){
		bsProductTypeAccessService.reFreshCache();
	}
	
}

