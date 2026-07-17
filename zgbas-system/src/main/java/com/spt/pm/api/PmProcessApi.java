package com.spt.pm.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.pm.entity.PmProcess;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "pm/process")
public class PmProcessApi extends BaseApi<PmProcess> {
	@Autowired
	private IPmProcessService pmProcessService;
	
	@Override
	public IBaseService<PmProcess> getService() {
		return pmProcessService;
	}
	
	@PostMapping("findByProcessCode")
	public PmProcess findByProcessCode(@RequestBody PmProcessSearchVo searchVo) {
		return pmProcessService.findByProcessCode(searchVo);
	}
	
	@PostMapping("findAccess")
	public List<PmProcess> findAccess(@RequestBody PmProcessSearchVo searchVo){
		return pmProcessService.findAccess(searchVo);
	}
	
	@PostMapping(value = "getAllProcess")
	public EasyTreeNode getAllProcess(@RequestBody PmProcessSearchVo searchVo){
		EasyTreeNode root = getRoot("All Process");
		List<PmProcess> processList = pmProcessService.findByEnterpriseId(searchVo);
		for (PmProcess process : processList) {
			EasyTreeNode node = new EasyTreeNode();
			node.setId(String.valueOf(process.getId()));
			node.setText(process.getProcessName());
			root.getChildren().add(node);
		}
		return root;
	}
	
	private EasyTreeNode getRoot(String name) {
		EasyTreeNode root = new EasyTreeNode();
		root.setId("0");
		root.setText(name);
		root.setState(EasyTreeNode.STATE_OPEN);
		return root;
	}
	
	@PostMapping(value = "findByEnterpriseId")
	public List<PmProcess> findByEnterpriseId(@RequestBody PmProcessSearchVo searchVo){
		return pmProcessService.findByEnterpriseId(searchVo);
	}
	
	@PostMapping(value = "findStartUserByProcess")
	public Long findStartUserByProcess(@RequestBody PmProcess process) throws ApplicationException{
		return pmProcessService.findStartUserByProcess(process);
	}
	
	@PostMapping(value = "findByEnterpriseIdAndEnableFlgTrue")
	public List<PmProcess> findByEnterpriseIdAndEnableFlgTrue(@RequestBody Long enterpriseId){
		return pmProcessService.findByEnterpriseIdAndEnableFlgTrue(enterpriseId);
	}

	// 获取流程配置
	@PostMapping(value = "initPmProcessList")
	public  String initPmProcessList(){
		return pmProcessService.initPmProcessList();
	}
}

