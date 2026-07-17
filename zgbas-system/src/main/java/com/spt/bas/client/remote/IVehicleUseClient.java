package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SealBorrow;
import com.spt.bas.client.entity.VehicleUse;
import com.spt.bas.client.vo.SealBorrowSearchVo;
import com.spt.bas.client.vo.VehicleUseVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/vehicle/use", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IVehicleUseClient extends BaseClient<VehicleUse> {

    @RequestMapping(value = "findVehiclePage")
    public PageDown<VehicleUse> findVehiclePage(@RequestBody VehicleUseVo vehicleUseVo);
}
