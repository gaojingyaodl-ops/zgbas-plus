/**
 *
 */
package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.client.entity.BsDictType;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

/**
 * @author huangjian
 *
 */
public interface BsDictTypeDao extends BaseDao<BsDictType> {
	@Query(value="select count(*) from BsDictType m where dictTypeCd=?1 and dictTypeCd <> ?2 and enterpriseId=?3")
	public Long existDictTypeCd(String dictTypeCd, String dictTypeOld, Long enterpriseId);
}
