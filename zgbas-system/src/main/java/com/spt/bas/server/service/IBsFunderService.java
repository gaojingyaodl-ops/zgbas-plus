package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsFunder;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IBsFunderService extends IBaseService<BsFunder> {
    List<BsFunder> findAllByUserId(Long userId);
}
