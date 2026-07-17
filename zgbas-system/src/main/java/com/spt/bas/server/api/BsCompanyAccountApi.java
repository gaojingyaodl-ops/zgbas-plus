package com.spt.bas.server.api;

import java.util.List;

import com.spt.bas.client.entity.BillInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsCompanyAccount;
import com.spt.bas.client.vo.BsCompanyVo;
import com.spt.bas.server.service.IBsCompanyAccountService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;

@RestController
@RequestMapping(value = "bs/companyAccount")
public class BsCompanyAccountApi extends BaseApi<BsCompanyAccount>{

	@Autowired
	private IBsCompanyAccountService bsCompanyAccountService;

	@Override
	public IBaseService<BsCompanyAccount> getService() {
		return bsCompanyAccountService;
	}
	@PostMapping("updateDefaultFlg")
	void updateDefaultFlg(Long id, Boolean flg){
		bsCompanyAccountService.updateDefaultFlg(id,flg);
	}

	@PostMapping("findByCompanyId")
	List<BsCompanyAccount> findByCompanyId(@RequestBody Long companyId){
		return bsCompanyAccountService.queryCompanyAccount(companyId);
	}

	@PostMapping("findCompanyAccountFlg")
	public List<BsCompanyAccount> findCompanyAccountFlg(@RequestBody BsCompanyVo vo){
		return bsCompanyAccountService.findCompanyAccountFlg(vo);
	}

	@PostMapping("findDefaultAccount")
	BsCompanyAccount findDefaultAccount(@RequestBody BsCompanyVo vo) {
		return bsCompanyAccountService.findDefaultAccount(vo);
	}

	/**
	 * 添加企业发票信息
	 * @param billInfoRequest
	 */
	@PostMapping("addBillsInfo")
	void addBillsInfo(@RequestBody BillInfoRequest billInfoRequest){
		bsCompanyAccountService.addBillsInfo(billInfoRequest);
	}

	/**
	 * 添加企业银行信息
	 * @param billInfoRequest
	 */
	@PostMapping("addBankInfo")
	void addBankInfo(@RequestBody BillInfoRequest billInfoRequest) {
		bsCompanyAccountService.addBankInfo(billInfoRequest);
	}

	@PostMapping("findid")
	BsCompanyAccount findid(@RequestBody Long id){
	return 	bsCompanyAccountService.findid(id);
	}

	@PostMapping("findByCompanyIdTrue")
	 List<BsCompanyAccount> findByCompanyIdTrue(@RequestBody Long companyId) {
		return bsCompanyAccountService.findByCompanyId(companyId);
	 }

}
