package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BillInfoRequest;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyAccount;
import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.vo.BsCompanyVo;
import com.spt.bas.client.vo.BsWarehouseVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BsCompanyAccountDao;
import com.spt.bas.server.dao.BsWarehouseDao;
import com.spt.bas.server.service.IBsCompanyAccountService;
import com.spt.bas.server.service.IBsWarehouseService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Transactional(readOnly = true)
public class BsCompanyAccountServiceImpl extends BaseService<BsCompanyAccount> implements IBsCompanyAccountService{


	@Autowired
	private BsCompanyAccountDao bsCompanyAccountDao;
	@Autowired
	private IBsWarehouseService bsWarehouseService;

	@Autowired
	private BsWarehouseDao BsWarehouseDao;
	@Override
	public BaseDao<BsCompanyAccount> getBaseDao() {
		return bsCompanyAccountDao;
	}
	@Override
	public void updateDefaultFlg(Long id, Boolean flg) {
		bsCompanyAccountDao.updateDefaultFlg(id, flg);
	}

	@Override
	@ServerTransactional
	public void saveBatch(List<BsCompanyAccount> insertedRecords, List<BsCompanyAccount> updatedRecords, List<BsCompanyAccount> deletedRecords,BsCompany company) throws ApplicationException  {
		List<BsCompanyAccount> records = new ArrayList<BsCompanyAccount>();
		for (BsCompanyAccount entity : insertedRecords) {
			entity.setCompanyId(company.getId());
			entity.setEnterpriseId(company.getEnterpriseId());
			records.add(entity);
		}
		for (BsCompanyAccount entity : updatedRecords) {
			records.add(entity);
		}
		for(BsCompanyAccount entity:records){
			boolean defaultFlg = entity.getDefaultFlg();
			List<BsCompanyAccount> list = bsCompanyAccountDao.findByCompanyId(entity.getCompanyId());
			if(defaultFlg==true){
				for(BsCompanyAccount account:list){
					bsCompanyAccountDao.updateDefaultFlg(account.getId(), false);
					if ( entity.getId()!=null && account.getId().longValue() == entity.getId().longValue()) {
						bsCompanyAccountDao.updateData(account.getId(),entity.getDefaultFlg(),entity.getBankName(),entity.getBankAccount(),entity.getTaxNo());
					}else{
						getBaseDao().save(entity);
					}
				}

			}else{
				List<BsCompanyAccount> list2 = bsCompanyAccountDao.findByCompanyId(entity.getCompanyId());
				if(entity.getId()!=null) {
					for (BsCompanyAccount bsCompanyAccount : list2) {
						if (bsCompanyAccount.getId().longValue() == entity.getId().longValue()) {
							getBaseDao().save(entity);
							break;
						}
					}
				}else{
					getBaseDao().save(entity);
				}
				boolean defaultFlg2 =false;
				if(list2.size()>0){
					for (BsCompanyAccount vo : list2) {
						if(vo.getDefaultFlg()==true){
							defaultFlg2=true;
							return;
						}
					}
					if(defaultFlg2==false){
						BsCompanyAccount warehouse = list.get(0);
						warehouse.setDefaultFlg(true);
						entity = getBaseDao().save(warehouse);
					}
				}
			}
			List<BsCompanyAccount> list2 = bsCompanyAccountDao.findByCompanyId(entity.getCompanyId());
			if(list==null||list.size()<=0){
				entity.setDefaultFlg(true);
			getBaseDao().save(entity);
			}

		}
		for (BsCompanyAccount entity : deletedRecords) {
			delete(entity.getId());
		}
	}

	@Override
	public void saveBatchAddr(List<BsWarehouseVo> insertedRecords, List<BsWarehouseVo> updatedRecords, List<BsWarehouseVo> deletedRecords, BsCompany company) throws ApplicationException {
		List<BsWarehouse> records = new ArrayList<BsWarehouse>();
		String  insFlg="a";
		for (BsWarehouseVo entity : insertedRecords) {
			BsWarehouse bsWarehouse=new BsWarehouse();
			bsWarehouse.setCompanyId(company.getId());
			bsWarehouse.setEnterpriseId(company.getEnterpriseId());
			bsWarehouse.setDefaultFlg(entity.getwDefaultFlg());
			bsWarehouse.setWarehouseAddr(entity.getWarehouseAddr());
			bsWarehouse.setContactPhone(entity.getwContactPhone());
			bsWarehouse.setContactName(entity.getContactName());
			bsWarehouse.setAreaCode(entity.getAreaCode());
			records.add(bsWarehouse);
		}
		for (BsWarehouseVo entity : updatedRecords) {
			insFlg="b";
			BsWarehouse bsWarehouse2=new BsWarehouse();
			bsWarehouse2.setId(entity.getId());
			bsWarehouse2.setCompanyId(company.getId());
			bsWarehouse2.setEnterpriseId(company.getEnterpriseId());
			bsWarehouse2.setDefaultFlg(entity.getwDefaultFlg());
			bsWarehouse2.setWarehouseAddr(entity.getWarehouseAddr());
			bsWarehouse2.setContactPhone(entity.getwContactPhone());
			bsWarehouse2.setContactName(entity.getContactName());
			bsWarehouse2.setAreaCode(entity.getAreaCode());
			records.add(bsWarehouse2);
		}
		List<BsWarehouse> list = BsWarehouseDao.findByCompanyId(company.getId());
		for (BsWarehouse record : records) {
			boolean defaultFlg = record.getDefaultFlg();
			if(defaultFlg==true) {
				if (StringUtils.equals(insFlg, "b")) {
					for (BsWarehouse vo : list) {
						if (vo.getId().longValue() == record.getId().longValue()) {
							vo.setDefaultFlg(true);
							BsWarehouseDao.updateStatus(true, vo.getId());
						} else {
							vo.setDefaultFlg(false);
							BsWarehouseDao.updateStatus(false, vo.getId());
						}
					}
				}
				} else {
					if (StringUtils.equals(insFlg, "b")) {
						for (BsWarehouse vo : list) {
							if (vo.getId().longValue() == record.getId().longValue()) {
								vo.setDefaultFlg(false);
								BsWarehouseDao.updateStatus(false, record.getId());
							}
						}
					}
			}
			if(StringUtils.equals(insFlg,"a")){
				for (BsWarehouse record2 : records) {
						   if(record2.getDefaultFlg()==true){
							   for (BsWarehouse vo : list) {
								   vo.setDefaultFlg(false);
								   BsWarehouseDao.updateStatus(false,vo.getId());
							   }
						   }
				BsWarehouseDao.save(record);
			   }
			}
		}
		if (StringUtils.equals(insFlg, "b")) {
			boolean defaultFlg2 =false;
			if(list.size()>0){
				for (BsWarehouse vo : list) {
					if(vo.getDefaultFlg()==true){
						defaultFlg2=true;
						return;
					}
				}
				if(defaultFlg2==false){
					BsWarehouse warehouse = list.get(0);
					warehouse.setDefaultFlg(true);
					BsWarehouseDao.save(warehouse);
				}
			}
		}

		for (BsWarehouseVo entity : deletedRecords) {
			bsWarehouseService.delete(entity.getId());
		}

	}

	@Override
	public List<BsCompanyAccount> queryCompanyAccount(Long companyId) {
		return bsCompanyAccountDao.queryCompanyAccount(companyId);
	}

	@Override
	@Transactional
	public void verifyCompanyAccount(BsCompanyAccount companyAccount) throws ApplicationException {
		Long companyId = companyAccount.getCompanyId();
		String bankAccount = companyAccount.getBankAccount();
		String bankName = companyAccount.getBankName();
		if (companyId != null && bankAccount != null) {
			Boolean existBankAccount = isExistBankAccount(companyId, bankAccount,bankName);
			if (!existBankAccount) {
				//保存并设为默认账户
				companyAccount.setDefaultFlg(true);
				BsCompanyAccount save = this.save(companyAccount);
				//更改原默认账户为非默认
				bsCompanyAccountDao.updateAccountDefaultFlg(companyId, save.getId());
			}
		}

	}

	public Boolean isExistBankAccount(Long companyId, String bankAccount,String bankName) {
		Boolean exist = false;
		List<BsCompanyAccount> accountList = bsCompanyAccountDao.findByCompanyIdAndBankAccountAndBankName(companyId, bankAccount, bankName);
		if (!accountList.isEmpty()) {
			exist = true;
		}
		return exist;
	}

	/**
	 * 根据企业ID获取企业银行账号信息
	 */
	@Override
	public List<BsCompanyAccount> findCompanyAccountFlg(BsCompanyVo vo) {
		List<BsCompanyAccount> list = bsCompanyAccountDao.findCompanyAccountFlg(vo.getId(), vo.getEnterpriseId());
		return list;
	}

	@Override
	public BsCompanyAccount findDefaultAccount(BsCompanyVo vo) {
		return bsCompanyAccountDao.findDefaultAccount(vo.getId(), vo.getEnterpriseId());
	}

	/**
	 * 添加企业发票信息
	 *
	 * @param billInfoRequest
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addBillsInfo(BillInfoRequest billInfoRequest) {
		BsCompanyAccount bsCompanyAccount = new BsCompanyAccount();
		if (billInfoRequest.getCompanyId() != null) {
			setAllDefault(billInfoRequest.getCompanyId());
		}
		if (billInfoRequest.getAccountId() != null) {
			bsCompanyAccount = getEntity(billInfoRequest.getAccountId());
		}
		// 忽略null值
		BeanUtils.copyProperties(billInfoRequest, bsCompanyAccount, getNullPropertyNames(billInfoRequest));
		bsCompanyAccount.setDefaultFlg(true);
		bsCompanyAccount.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		bsCompanyAccountDao.save(bsCompanyAccount);
	}

	/**
	 * 添加企业银行信息
	 *
	 * @param billInfoRequest
	 */
	@Override
	@ServiceTransactional
	public void addBankInfo(BillInfoRequest billInfoRequest) {
		BsCompanyAccount bsCompanyAccount = new BsCompanyAccount();
		if (billInfoRequest.getCompanyId() != null) {
			setAllDefault(billInfoRequest.getCompanyId());
		}
		if (billInfoRequest.getAccountId() != null) {
			bsCompanyAccount = getEntity(billInfoRequest.getAccountId());
		}
		bsCompanyAccount.setAccountName(billInfoRequest.getAccountName());
		bsCompanyAccount.setBankAccount(billInfoRequest.getBankAccount());
		bsCompanyAccount.setBankName(billInfoRequest.getBankName());
		bsCompanyAccount.setDefaultFlg(true);
		bsCompanyAccount.setCompanyId(billInfoRequest.getCompanyId());
		bsCompanyAccount.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		bsCompanyAccountDao.save(bsCompanyAccount);
	}

	@Override
	public BsCompanyAccount findid(Long id) {
		return bsCompanyAccountDao.findid(id);
	}

	@Override
	public List<BsCompanyAccount> findByCompanyId(Long companyId) {
		return bsCompanyAccountDao.findByCompanyId(companyId);
	}

	private void setAllDefault(Long companyId) {
		List<BsCompanyAccount> byCompanyId = bsCompanyAccountDao.findByCompanyIdAndDefaultFlgTrue(companyId);
		for (BsCompanyAccount companyAccount : byCompanyId) {
			companyAccount.setDefaultFlg(false);
		}
		try {
			saveBatch(new ArrayList<>(), byCompanyId, new ArrayList<>());
		} catch (ApplicationException e) {
			logger.error("addBankInfo 设置其余default为false");
		}
	}

	public static String[] getNullPropertyNames (Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<>();
		for(java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) {
				emptyNames.add(pd.getName());
			}
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}
}
