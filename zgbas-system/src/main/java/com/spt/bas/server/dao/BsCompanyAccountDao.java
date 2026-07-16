package com.spt.bas.server.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.BsCompanyAccount;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsCompanyAccountDao extends BaseDao<BsCompanyAccount> {


	@Transactional
	@Modifying
	@Query("update BsCompanyAccount c set c.defaultFlg =?2 where c.id=?1 ")
	void updateDefaultFlg(Long id, Boolean flg);

	@Transactional
	@Modifying
	@Query("update BsCompanyAccount c set c.defaultFlg =?2,c.bankName=?3,c.bankAccount=?4,c.taxNo=?5 where c.id=?1 ")
	void updateData(Long id, Boolean flg,String bankName,String bankAccount,String taxNo);


	@Query("select c from BsCompanyAccount c  where  c.companyId=?1 order by c.createdDate desc ")
	List<BsCompanyAccount> findByCompanyId(Long companyId);

	@Query("select c from BsCompanyAccount c where c.defaultFlg = true and c.companyId=?1 ")
	List<BsCompanyAccount> queryCompanyAccount(Long companyId);

	@Transactional
	@Modifying
	void deleteByCompanyId(Long companyId);

	List<BsCompanyAccount> findByCompanyIdAndBankAccountAndBankName(Long companyId, String bankAccount, String bankName);

	@Transactional
	@Modifying
	@Query("update BsCompanyAccount c set c.defaultFlg=false where c.companyId=?1 and c.id<>?2")
	void updateAccountDefaultFlg(Long company, Long accountId);

	@Query("from BsCompanyAccount b where b.companyId = ?1 and b.enterpriseId = ?2 order by b.defaultFlg desc")
	List<BsCompanyAccount> findCompanyAccountFlg(Long id, Long enterpriseId);

	@Query("from BsCompanyAccount b where b.companyId = ?1 and b.enterpriseId = ?2 and b.defaultFlg = true")
	BsCompanyAccount findDefaultAccount(Long id, Long enterpriseId);

	List<BsCompanyAccount> findByCompanyIdAndDefaultFlgTrue(Long companyId);

	@Query("from BsCompanyAccount b where b.id = ?1 ")
	BsCompanyAccount findid( Long id);

}
