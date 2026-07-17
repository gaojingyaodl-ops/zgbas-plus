package com.spt.bas.client.remote;

import java.util.List;
import java.util.Optional;

import com.spt.bas.client.vo.WarehouseUpdate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.vo.BsWarehouseAddrVo;
import com.spt.bas.client.vo.BsWarehouseSearchVo;
import com.spt.bas.client.vo.BsWarehouseVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME
		+ "/bs/warehouse", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IBsWarehouseClient extends BaseClient<BsWarehouse> {

	@PostMapping("findByEnterpriseId")
	public List<BsWarehouse> findByEnterpriseId(@RequestBody Long enterpriseId);

	@PostMapping("countWarehouse")
	public Long countWarehouse(@RequestBody BsWarehouse warehouse);

	@PostMapping("saveAddr")
	public void saveAddr(@RequestBody BsWarehouseAddrVo vo) throws ApplicationException;

	@PostMapping("queryBsWarehouseName")
	public List<BsWarehouse> queryBsWarehouseName(@RequestBody BsWarehouse bs);

	@PostMapping("findList")
	public List<BsWarehouse> findList(@RequestBody BsWarehouseSearchVo vo);

	@PostMapping("findWarehouses")
	public PageDown<BsWarehouse> findWarehouses(@RequestBody PageSearchVo searchVo);

	@PostMapping("findByWarehouseNameAndEnterpriseId")
	public List<BsWarehouse> findByWarehouseNameAndEnterpriseId(@RequestBody BsWarehouse bs);

	@PostMapping("findWarehouseByCompanyId")
	List<BsWarehouseVo> findWarehouseByCompanyId(@RequestBody Long companyId);

	@PostMapping("findByCompanyIdAddr")
	List<BsWarehouse> findByCompanyIdAddr(@RequestBody Long companyId);

	@PostMapping("findByWxUserId")
	List<BsWarehouse> findByWxUserId(@RequestBody Long wxUserId);

	@PostMapping("findByCompanyId")
	List<BsWarehouse> findByCompanyId(@RequestBody Long companyId);

	@PostMapping("findByWxUserIdAndWarehouseId")
	BsWarehouse findByWxUserIdAndWarehouseId(@RequestParam("wxUserId") Long wxUserId, @RequestParam("warehouseId") Long warehouseId);

	@PostMapping("countByWxUserId")
	Integer countByWxUserId(@RequestBody Long wxUserId);

	@PostMapping("countByCompanyId")
	Integer countByCompanyId(@RequestBody Long companyId);

	@PostMapping("findid")
	BsWarehouse findid(@RequestBody Long id);

	@PostMapping("findByBusiness")
	List<BsWarehouse> findByBusiness(@RequestBody BsWarehouseSearchVo vo);

}
