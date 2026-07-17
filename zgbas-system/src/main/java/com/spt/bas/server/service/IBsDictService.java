/**
 *
 */
package com.spt.bas.server.service;

import com.spt.auth.sdk.entity.SysEnterpriseSdk;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.BsDictType;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * @author huangjian
 *
 */
public interface IBsDictService extends IBaseService<BsDictType> {
	public BsDictType save(BsDictType entity);

	public void deleteData(Long id);

	public boolean existDictTypeCd(String dictTypeCd, String dictTypeOld, Long enterpriseId);

	public void saveDatas(List<BsDictData> insertedRecords,
                          List<BsDictData> updatedRecords, List<BsDictData> deletedRecords,
                          Long dictTypeId);

	public List<BsDictData> loadDatasByTypeCd(String dictTypeCd, Long enterpriseId);

	public void saveOurCompany(SysEnterpriseSdk enterprise);
}
