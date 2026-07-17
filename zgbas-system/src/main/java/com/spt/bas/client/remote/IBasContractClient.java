package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.vo.BasContractExistVo;
import com.spt.bas.client.vo.BasContractVo;
import com.spt.bas.client.vo.ContractOpVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bas/contract",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBasContractClient extends BaseClient<BasContract> {
	@PostMapping("existContractNo")
	public boolean existContractNo(@RequestBody BasContractExistVo vo);
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo);
	
//	@PostMapping("updateContractStatusByFond")
//	public void updateContractStatusByFond(@RequestBody BasContract contract);
//	
//	@PostMapping("updateContractStatusByBill")
//	public void updateContractStatusByBill(@RequestBody BasContract contract);
	
	@PostMapping("doContractOp")
	public void doContractOp(@RequestBody ContractOpVo opVo);
	
	@PostMapping("findPageVo")
	public PageDown<BasContractVo> findPageVo(@RequestBody PageSearchVo queryVo);
	
	@PostMapping("findPageByContQuery")
	public PageDown<BasContract> findPageByContQuery(@RequestBody PageSearchVo searchVo);
	
	@PostMapping(value="findByContractRelaId")
	public List<BasContract> findByContractRelaId(@RequestBody Long contractRealaId);

	@PostMapping(value = "oneselfProcurementBudget")
	public void oneselfProcurementBudget(BasContractVo commonVo);
}

