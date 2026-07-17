package com.spt.bas.server.service;

import com.spt.bas.client.entity.BasManual;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IBasManualService extends IBaseService<BasManual> {
    List<BasManual> findAllEnable();
}
