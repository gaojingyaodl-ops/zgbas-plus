package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsNotice;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;


public interface IBsNoticeService extends IBaseService<BsNotice> {

    BsNotice findLast();

    List<BsNotice> findLimit5( String deptId);
    List<BsNotice> findLimit();
}
