package com.spt.bas.server.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.BsWarehouseAddr;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsWarehouseAddrDao extends BaseDao<BsWarehouseAddr> {

	List<BsWarehouseAddr> findByWarehouseId(Long id);
	@Transactional
	@Modifying
	@Query("update BsWarehouseAddr b set b.defaultFlg =?2 where b.id=?1 ")
	void updateDefaultFlg(Long id, Boolean flg);

	@Transactional
	@Modifying
	void deleteByWarehouseId(Long warehouseId);

	@Query("select b from BsWarehouseAddr b where b.warehouseId=?1 and b.defaultFlg=?2 ")
	List<BsWarehouseAddr> queryDefaultFlg(Long id, boolean b);

	@Query("from BsWarehouseAddr b where b.warehouseAddr != ''")
	List<BsWarehouseAddr> findAllWarehouseAddr();

	@Query("from BsWarehouseAddr b where b.warehouseId = ?1 order by b.defaultFlg desc")
	List<BsWarehouseAddr> findWarehouseAddrDesc(Long warehouseId);

}

