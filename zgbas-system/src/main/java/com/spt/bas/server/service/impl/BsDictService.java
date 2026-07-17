/**
 *
 */
package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysEnterpriseSdk;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.BsDictType;
import com.spt.bas.server.dao.BsDictDataDao;
import com.spt.bas.server.dao.BsDictTypeDao;
import com.spt.bas.server.service.IBsDictService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 数据字典
 *
 * @author huangjian
 *
 */
@Component
// 默认将类中的所有public函数纳入事务管理.
@Transactional(readOnly = true)
public class BsDictService extends BaseService<BsDictType> implements IBsDictService {
	@Autowired
	private BsDictTypeDao dictTypeDao;
	@Autowired
	private BsDictDataDao dictDataDao;

	@Transactional(readOnly = false)
	public BsDictType save(BsDictType entity) {
//		entity.setCreatedDate(new Date());
//		entity.setUpdatedDate(new Date());
		return dictTypeDao.save(entity);
	}

	@Override
	public BaseDao<BsDictType> getBaseDao() {
		return dictTypeDao;
	}

	@Transactional(readOnly = false)
	public void deleteData(Long id) {
		dictDataDao.delete(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(Long id) {

		List<BsDictData> list = dictDataDao.findByDictTypeId(id);
		dictDataDao.deleteAll(list);
		dictTypeDao.delete(id);
	}


	private void saveData(BsDictData data, BsDictType dictType) {
		data.setDictType(dictType);
		data.setEnterpriseId(dictType.getEnterpriseId());
		data.setUpdatedDate(new Date());
		data.setCreatedDate(new Date());
		dictDataDao.save(data);
	}

	public boolean existDictTypeCd(String dictTypeCd, String dictTypeOld,Long companyId) {
		long cnt = dictTypeDao.existDictTypeCd(dictTypeCd, dictTypeOld,companyId);
		if (cnt == 0) {
			return false;
		}
		return true;
	}

	@Transactional(readOnly = false)
	public void saveDatas(List<BsDictData> insertedRecords,
			List<BsDictData> updatedRecords, List<BsDictData> deletedRecords,
			Long dictTypeId) {
		BsDictType dictType = getEntity(dictTypeId);
		for (BsDictData dictData : insertedRecords) {
			saveData(dictData, dictType);
		}
		for (BsDictData dictData : updatedRecords) {
			saveData(dictData, dictType);
		}
		dictDataDao.deleteAll(deletedRecords);
	}

	@Override
	public Class<BsDictType> getEntityClazz() {
		return BsDictType.class;
	}

	public BsDictData loadDictDataByCd(String dictTypeCd, String dictCd,Long companyId) {
		BsDictData dictData = dictDataDao.loadDictDataByCd(dictTypeCd, dictCd,companyId);
		return dictData;
	}

	public List<BsDictData> loadDatasByTypeCd(String dictTypeCd,Long companyId) {
		List<BsDictData> datas = dictDataDao.loadDatasByTypeCd(dictTypeCd,companyId);
		return datas;
	}

	@Override
	@Transactional(readOnly = false)
	public void saveOurCompany(SysEnterpriseSdk enterprise) {
		Long enterpriseId = enterprise.getId();

		BsDictType dictType = new BsDictType();
		dictType.setDictTypeCd("ourCompany");
		dictType.setDictTypeName("我方企业");
		dictType.setEnableFlg(true);
		dictType.setDispOrderNo(new BigDecimal(1));
		dictType.setEnterpriseId(enterpriseId);
		dictType = dictTypeDao.save(dictType);

		//保存字典数据
		BsDictData data = new BsDictData();
		data.setDictCd(enterprise.getCode());
		data.setDictName(enterprise.getName());
		data.setDictType(dictType);
		data.setDispOrderNo(new BigDecimal(1));
		data.setEnableFlg(true);
		data.setEnterpriseId(enterpriseId);
		dictDataDao.save(data);
	}
}
