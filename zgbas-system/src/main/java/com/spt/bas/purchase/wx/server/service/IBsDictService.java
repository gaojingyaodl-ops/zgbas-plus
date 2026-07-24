/**
 *
 */
package com.spt.bas.purchase.wx.server.service;

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
    BsDictType save(BsDictType entity);

    void deleteData(Long id);

    boolean existDictTypeCd(String dictTypeCd, String dictTypeOld, Long enterpriseId);

    void saveDatas(List<BsDictData> insertedRecords,
                   List<BsDictData> updatedRecords, List<BsDictData> deletedRecords,
                   Long dictTypeId);

    List<BsDictData> loadDatasByTypeCd(String dictTypeCd, Long enterpriseId);

    void saveOurCompany(SysEnterpriseSdk enterprise);
}
