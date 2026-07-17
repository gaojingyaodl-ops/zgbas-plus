package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.BillInfoRequest;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyAccount;
import com.spt.bas.client.vo.BsCompanyVo;
import com.spt.bas.client.vo.BsWarehouseVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.web.bind.annotation.RequestBody;

public interface IBsCompanyAccountService extends IBaseService<BsCompanyAccount>{

	void updateDefaultFlg(Long id, Boolean flg);

	List<BsCompanyAccount> queryCompanyAccount(Long companyId);

	void saveBatch(List<BsCompanyAccount> insertedRecords, List<BsCompanyAccount> updatedRecords, List<BsCompanyAccount> deletedRecords, BsCompany company) throws ApplicationException;

	void saveBatchAddr(List<BsWarehouseVo> insertedRecords, List<BsWarehouseVo> updatedRecords, List<BsWarehouseVo> deletedRecords, BsCompany company) throws ApplicationException;

	void verifyCompanyAccount(BsCompanyAccount companyAccount) throws ApplicationException;

	List<BsCompanyAccount> findCompanyAccountFlg(BsCompanyVo vo);

	BsCompanyAccount findDefaultAccount(BsCompanyVo vo);

	/**
	 * 添加企业发票信息
	 * @param billInfoRequest
	 */
	void addBillsInfo(BillInfoRequest billInfoRequest);

	/**
	 * 添加企业银行信息
	 * @param billInfoRequest
	 */
	void addBankInfo(BillInfoRequest billInfoRequest);

	BsCompanyAccount findid( Long id);

	List<BsCompanyAccount> findByCompanyId(Long companyId);

}
