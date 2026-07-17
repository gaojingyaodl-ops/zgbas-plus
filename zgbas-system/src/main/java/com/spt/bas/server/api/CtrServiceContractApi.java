package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrServiceContract;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.ctr.service.ICtrServiceContractService;
import com.spt.bas.server.dao.CtrServiceContractDao;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "ctr/service")
public class CtrServiceContractApi extends BaseApi<CtrServiceContract> {
	@Autowired
	private ICtrServiceContractService ctrServiceContractService;
	@Autowired
	private CtrServiceContractDao serviceContractDao;

	@Override
	public IBaseService<CtrServiceContract> getService() {
		return ctrServiceContractService;
	}
	
	@RequestMapping(value = "updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		ctrServiceContractService.updateFileId(vo.getId(),vo.getFileId());
	}

	@RequestMapping(value = "findByCtrContract")
	public CtrServiceContract findByCtrContract(@RequestBody Long ctrContractId){
		return serviceContractDao.findByCtrContractId(ctrContractId);
	}


}

