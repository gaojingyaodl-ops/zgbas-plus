/**
 *
 */
package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.client.entity.BsDictData;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * @author huangjian
 *
 */
public interface BsDictDataDao extends BaseDao<BsDictData> {
	@Query(value = "from BsDictData d where d.dictType.dictTypeCd=?1 and d.dictCd=?2 and d.enterpriseId=?3 ")
	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
	public BsDictData loadDictDataByCd(String dictTypeCd, String dictCd, Long enterpriseId);

	@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
	@Query(value = "from BsDictData d where d.enableFlg=true and d.dictType.dictTypeCd=?1 and d.enterpriseId=?2 order by dispOrderNo")
	public List<BsDictData> loadDatasByTypeCd(String dictTypeCd, Long enterpriseId);

	public List<BsDictData> findByDictTypeId(Long dictTypeId);

}
