package com.spt.bas.server.service;

import java.util.List;
import java.util.Optional;

import com.spt.bas.client.vo.WarehouseUpdate;
import org.springframework.data.domain.Page;

import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.vo.BsWarehouseAddrVo;
import com.spt.bas.client.vo.BsWarehouseSearchVo;
import com.spt.bas.client.vo.BsWarehouseVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.web.bind.annotation.RequestBody;

public interface IBsWarehouseService extends IBaseService<BsWarehouse> {
	public List<BsWarehouse> findByEnterpriseId(Long enterpriseId);

	public Long countWarehousewarehouse(BsWarehouse warehouse);

	public void saveBsWarehouseAddrVo(BsWarehouseAddrVo vo)throws ApplicationException;

	public List<BsWarehouse> findByWarehouseName(String warehouseName);

	public List<BsWarehouse> findList(BsWarehouseSearchVo vo);

	public Page<BsWarehouse> findWarehouses(PageSearchVo searchVo);

	public List<BsWarehouse> findByWarehouseNameAndEnterpriseId(BsWarehouse warehouse);

	List<BsWarehouseVo> findWarehouseByCompanyId(Long companyId);

	List<BsWarehouseVo> findByCompanyId(Long companyId);

	List<BsWarehouse> findByCompanyIdAddr(Long companyId);

	List<BsWarehouse> findByWxUserId(Long wxUserId, Long enterpriseId);

	BsWarehouse findByWxUserIdAndWarehouseId(Long wxUserId, Long enterpriseId, Long warehouseId);

	Integer countByWxUserId(Long wxUserId, Long enterpriseId);

	Integer countByCompanyId(Long companyId);

	BsWarehouse findid(Long id);

	List<BsWarehouse> findByBusiness(BsWarehouseSearchVo vo);
}

