package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.entity.BsWarehouseAddr;
import com.spt.bas.client.vo.BsWarehouseAddrVo;
import com.spt.bas.server.service.IBsWarehouseAddrService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/warehouseAddr")
public class BsWarehouseAddrApi extends BaseApi<BsWarehouseAddr> {
	@Autowired
	private IBsWarehouseAddrService bsWarehouseAddrService;
	
	@Override
	public IBaseService<BsWarehouseAddr> getService() {
		return bsWarehouseAddrService;
	}
	
	@PostMapping("saveAddr")
	public void saveAddr(@RequestBody BsWarehouseAddrVo vo) throws ApplicationException{
		bsWarehouseAddrService.saveBatchAddr(vo.getLstInsert(), vo.getLstUpdate(), vo.getLstDelete());
	}
	
	@PostMapping("countWarehouse")
	Long countWarehouse(BsWarehouseAddr addr){
		return bsWarehouseAddrService.countWarehouse(addr);
	}
	
	@PostMapping("findWarehouseAddr")
	BsWarehouseAddr findWarehouseAddr(@RequestBody BsWarehouse wh){
		return bsWarehouseAddrService.findWarehouseAddr(wh);
	}
	
	@PostMapping("findAllWarehouseAddr")
	List<BsWarehouseAddr> findAllWarehouseAddr(){
		return bsWarehouseAddrService.findAllWarehouseAddr();
	}
	
	@PostMapping("findWarehouseAddrDesc")
	List<BsWarehouseAddr> findWarehouseAddrDesc(@RequestBody Long warehouseId){
		return bsWarehouseAddrService.findWarehouseAddrDesc(warehouseId);
	}
	
}

