package com.spt.bas.server.api;

import java.util.List;
import java.util.Optional;

import com.spt.bas.client.constant.BasConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.vo.BsWarehouseAddrVo;
import com.spt.bas.client.vo.BsWarehouseSearchVo;
import com.spt.bas.client.vo.BsWarehouseVo;
import com.spt.bas.server.service.IBsWarehouseService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/warehouse")
public class BsWarehouseApi extends BaseApi<BsWarehouse> {
	@Autowired
	private IBsWarehouseService bsWarehouseService;

	@Override
	public IBaseService<BsWarehouse> getService() {
		return bsWarehouseService;
	}
	@PostMapping("findByEnterpriseId")
	public List<BsWarehouse> findByEnterpriseId(@RequestBody Long enterpriseId){
		return bsWarehouseService.findByEnterpriseId(enterpriseId);
	}

	@PostMapping("countWarehouse")
	public Long countWarehouse(@RequestBody BsWarehouse warehouse){
		return bsWarehouseService.countWarehousewarehouse(warehouse);
	}

	@PostMapping("saveAddr")
	public void saveAddr(@RequestBody BsWarehouseAddrVo vo) throws ApplicationException{
		bsWarehouseService.saveBsWarehouseAddrVo(vo);
	}

	@PostMapping("queryBsWarehouseName")
	public List<BsWarehouse> queryBsWarehouseName(@RequestBody BsWarehouse bs){
		return bsWarehouseService.findByWarehouseName(bs.getWarehouseName());
	}

	@PostMapping("findList")
	public List<BsWarehouse> findList(@RequestBody BsWarehouseSearchVo vo){
		return bsWarehouseService.findList(vo);
	}

	@PostMapping("findWarehouses")
	public Page<BsWarehouse> findWarehouses(@RequestBody PageSearchVo searchVo){
		return bsWarehouseService.findWarehouses(searchVo);
	}

	@PostMapping("findByWarehouseNameAndEnterpriseId")
	public List<BsWarehouse> findByWarehouseNameAndEnterpriseId(@RequestBody BsWarehouse bs){
		return bsWarehouseService.findByWarehouseNameAndEnterpriseId(bs);
	}

	@PostMapping("findByWxUserId")
	public List<BsWarehouse> findByWxUserId(@RequestBody Long wxUserId){
		return bsWarehouseService.findByWxUserId(wxUserId, BasConstants.ZG_ENTERPRISE_ID);
	}

	@PostMapping("findByWxUserIdAndWarehouseId")
	public BsWarehouse findByWxUserIdAndWarehouseId(Long wxUserId, Long warehouseId){
		return bsWarehouseService.findByWxUserIdAndWarehouseId(wxUserId, BasConstants.ZG_ENTERPRISE_ID, warehouseId);
	}

	@PostMapping("countByWxUserId")
	public Integer countByWxUserId(@RequestBody Long wxUserId){
		return bsWarehouseService.countByWxUserId(wxUserId, BasConstants.ZG_ENTERPRISE_ID);
	}


	@PostMapping("findWarehouseByCompanyId")
	public List<BsWarehouseVo> findWarehouseByCompanyId(@RequestBody Long id) {
		return bsWarehouseService.findWarehouseByCompanyId(id);
	}

	@PostMapping("findByCompanyId")
	public List<BsWarehouseVo> findByCompanyId(@RequestBody Long companyId) {
		return bsWarehouseService.findByCompanyId(companyId);
	}

	@PostMapping("countByCompanyId")
	public Integer countByCompanyId(@RequestBody Long companyId){
		return bsWarehouseService.countByCompanyId(companyId);
	}

	@PostMapping("findid")
	public BsWarehouse findid(@RequestBody Long id){
		return bsWarehouseService.findid(id);
	}

	@PostMapping("findByCompanyIdAddr")
	List<BsWarehouse> findByCompanyIdAddr(@RequestBody Long companyId){
		return bsWarehouseService.findByCompanyIdAddr(companyId);
	}

	/**
	 * 通过业务查找仓库地址
	 * @param vo
	 * @return
	 */
	@PostMapping("findByBusiness")
	List<BsWarehouse> findByBusiness(@RequestBody BsWarehouseSearchVo vo){
		return bsWarehouseService.findByBusiness(vo);
	}
}

