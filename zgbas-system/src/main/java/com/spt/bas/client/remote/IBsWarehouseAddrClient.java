package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.entity.BsWarehouseAddr;
import com.spt.bas.client.vo.BsWarehouseAddrVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/warehouseAddr",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsWarehouseAddrClient extends BaseClient<BsWarehouseAddr> {
	@PostMapping("saveAddr")
	void saveAddr(@RequestBody BsWarehouseAddrVo vo) throws ApplicationException;
	
	@PostMapping("countWarehouse")
	Long countWarehouse(BsWarehouseAddr addr);
	
	@PostMapping("findWarehouseAddr")
	BsWarehouseAddr findWarehouseAddr(@RequestBody BsWarehouse wh);
	
	@PostMapping("findAllWarehouseAddr")
	List<BsWarehouseAddr> findAllWarehouseAddr();
	
	@PostMapping("findWarehouseAddrDesc")
	List<BsWarehouseAddr> findWarehouseAddrDesc(@RequestBody Long warehouseId);
	
	
}

