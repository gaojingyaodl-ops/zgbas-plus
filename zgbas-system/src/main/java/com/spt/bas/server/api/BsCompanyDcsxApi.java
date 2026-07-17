package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.server.service.IBsCompanyDcsxService;
import com.spt.bas.server.service.IPiccInsuranceImportService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/bs/companyDcsx")
public class BsCompanyDcsxApi extends BaseApi<BsCompanyDcsx> {
	@Resource
	private IBsCompanyDcsxService bsCompanyDcsxService;
	@Resource
	private IPiccInsuranceImportService piccInsuranceImportService;

	@Override
	public IDataService<BsCompanyDcsx> getService() {
		return bsCompanyDcsxService;
	}
	
	@PostMapping("findByCompanyName")
	public BsCompanyDcsx findByCompanyName(@RequestParam("companyName") String companyName){
		return bsCompanyDcsxService.findByCompanyName(companyName);
	}

	@PostMapping("findByCompanyCd")
	public BsCompanyDcsx findByCompanyCd(@RequestParam("companyCd") String companyCd){
		return bsCompanyDcsxService.findByCompanyCd(companyCd);
	}

	@PostMapping("getCompanyConfigMap")
	public Map<String, BsCompanyDcsx> getCompanyConfigMap(){
		return bsCompanyDcsxService.getCompanyConfigMap();
	}

	@PostMapping("findDcsxCompanyList")
	public List<BsCompanyDcsx> findDcsxCompanyList(){
		return bsCompanyDcsxService.findDcsxCompanyList();
	}

	@PostMapping(value = "importPiccInsuranceExcel")
	public List<String> importPiccInsuranceExcel(@RequestBody String fileId){
		return piccInsuranceImportService.initPiccInsuranceData(fileId);
	}

}
