package com.spt.bas.server.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.BsWarehouse;
import com.spt.tools.jpa.dao.BaseDao;

import javax.transaction.Transactional;

public interface BsWarehouseDao extends BaseDao<BsWarehouse> {

	@Query("from BsWarehouse where enterpriseId = ?1 and enableFlg=true")
	public List<BsWarehouse> findByEnterpriseId(Long enterpriseId);

	@Query("from BsWarehouse where  warehouseName= ?1")
	public List<BsWarehouse> findByWarehouseName(String bsWarehouse);

	@Query("from BsWarehouse where  warehouseName= ?1 and enterpriseId =?2")
	public List<BsWarehouse> findByWarehouseNameAndEnterpriseId(String warehouseName, Long enterpriseId);

	List<BsWarehouse> findByWxUserIdAndEnableFlgTrueAndEnterpriseIdOrderByCreatedDateDesc(Long wxUserId, Long enterpriseId);

	List<BsWarehouse> findByCompanyIdAndEnableFlgTrueAndEnterpriseIdOrderByCreatedDateDesc(Long companyId, Long enterpriseId);

	@Query("from BsWarehouse a where  a.companyId= ?1 order by a.createdDate desc")
	List<BsWarehouse> findByCompanyId(Long companyId);

	@Transactional
	@Modifying
	@Query("update BsWarehouse c set c.defaultFlg =?1 where c.id = ?2 ")
	void updateStatus(Boolean flg, Long id);

	BsWarehouse findByWxUserIdAndEnableFlgTrueAndEnterpriseIdAndId(Long wxUserId, Long warehouseId, Long enterpriseId);

	Integer countByWxUserIdAndEnableFlgTrueAndEnterpriseId(Long wxUserId, Long enterpriseId);

	Integer countByCompanyIdAndEnableFlgTrueAndEnterpriseId(Long companyId, Long enterpriseId);


	@Query(nativeQuery = true,value = "SELECT  *  FROM `t_bs_warehouse` WHERE id=?1")
     BsWarehouse findid(Long id);




}

