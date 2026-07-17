package com.spt.bas.server.service;

import com.spt.bas.client.entity.SealBorrow;
import com.spt.bas.client.entity.VehicleUse;
import com.spt.bas.client.vo.SealBorrowSearchVo;
import com.spt.bas.client.vo.VehicleUseVo;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;


public interface IVehicleUseService extends IBaseService<VehicleUse> {
    public Page<VehicleUse> findVehiclePage(VehicleUseVo vehicleUseVo);

}

