package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.entity.BsWarehouseAddr;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;
public interface IBsWarehouseAddrService extends IBaseService<BsWarehouseAddr> {
	void saveBatchAddr(List<BsWarehouseAddr> insertedAddr, List<BsWarehouseAddr> updatedAddr,
                       List<BsWarehouseAddr> deletedAddr) throws ApplicationException;

	void saveBatch(List<BsWarehouseAddr> lstInsert, List<BsWarehouseAddr> lstUpdate, List<BsWarehouseAddr> lstDelete,
                   BsWarehouse warehouse);

	Long countWarehouse(BsWarehouseAddr addr);

	BsWarehouseAddr findWarehouseAddr(BsWarehouse wh);

	List<BsWarehouseAddr> findAllWarehouseAddr();

	List<BsWarehouseAddr> findWarehouseAddrDesc(Long warehouseId);

}

