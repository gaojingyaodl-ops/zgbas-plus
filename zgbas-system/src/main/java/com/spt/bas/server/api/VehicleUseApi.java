package com.spt.bas.server.api;

import com.spt.bas.client.entity.VehicleUse;
import com.spt.bas.client.vo.VehicleUseVo;
import com.spt.bas.server.service.IVehicleUseService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "vehicle/use")
public class VehicleUseApi extends BaseApi<VehicleUse> {
	@Autowired
	private IVehicleUseService vehicleUseService;

	@Override
	public IBaseService<VehicleUse> getService() {
		return vehicleUseService;
	}

	@RequestMapping(value = "findVehiclePage")
	public Page<VehicleUse> findVehiclePage(@RequestBody VehicleUseVo vehicleUseVo) {
		return vehicleUseService.findVehiclePage(vehicleUseVo);
	}
}

