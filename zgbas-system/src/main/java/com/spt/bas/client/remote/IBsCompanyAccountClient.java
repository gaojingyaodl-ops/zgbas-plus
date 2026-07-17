package com.spt.bas.client.remote;

import java.util.List;

import com.spt.bas.client.entity.BillInfoRequest;
import com.spt.bas.client.entity.BsWarehouse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyAccount;
import com.spt.bas.client.vo.BsCompanyVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/companyAccount",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsCompanyAccountClient extends BaseClient<BsCompanyAccount>{
	@PostMapping("updateDefaultFlg")
	void updateDefaultFlg(@RequestBody Long id, @RequestParam("flg") Boolean flg);

	@PostMapping("findByCompanyId")
	List<BsCompanyAccount> findByCompanyId(@RequestBody Long companyId);

	@PostMapping("findCompanyAccountFlg")
	List<BsCompanyAccount> findCompanyAccountFlg(@RequestBody BsCompanyVo vo);

	@PostMapping("findDefaultAccount")
	BsCompanyAccount findDefaultAccount(@RequestBody BsCompanyVo vo);

	@PostMapping("findByCompanyIdTrue")
	 List<BsCompanyAccount> findByCompanyIdTrue(@RequestBody Long companyId) ;

	/**
	 * 添加发票
	 * @param billInfoRequest
	 */
	@PostMapping("addBillsInfo")
	void addBillsInfo(@RequestBody BillInfoRequest billInfoRequest);

	/**
	 * 添加银行信息
	 * @param billInfoRequest
	 */
	@PostMapping("addBankInfo")
	void addBankInfo(@RequestBody BillInfoRequest billInfoRequest);

	@PostMapping("findid")
	BsCompanyAccount findid(@RequestBody Long id);

}
