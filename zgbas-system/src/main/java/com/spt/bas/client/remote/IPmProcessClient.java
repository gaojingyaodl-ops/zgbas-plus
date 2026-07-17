package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/pm/process",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IPmProcessClient extends BaseClient<PmProcess> {
	@PostMapping("findAccess")
	public List<PmProcess> findAccess(@RequestBody PmProcessSearchVo searchVo);
	
	@PostMapping(value = "getAllProcess")
	public EasyTreeNode getAllProcess(@RequestBody PmProcessSearchVo searchVo);
	
	@PostMapping(value = "findByEnterpriseId")
	public List<PmProcess> findByEnterpriseId(@RequestBody PmProcessSearchVo searchVo);
	
	@PostMapping("findByProcessCode")
	public PmProcess findByProcessCode(@RequestBody PmProcessSearchVo searchVo);
	
	@PostMapping(value = "findStartUserByProcess")
	public Long findStartUserByProcess(@RequestBody PmProcess process);
	
	@PostMapping(value = "findByEnterpriseIdAndEnableFlgTrue")
	public List<PmProcess> findByEnterpriseIdAndEnableFlgTrue(@RequestBody Long enterpriseId);
	@PostMapping(value = "initPmProcessList")
	public  String initPmProcessList();
}

