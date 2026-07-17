package com.spt.bas.server.service;

import com.spt.bas.client.entity.BizSign;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * @Author MoonLight
 * @Date 2024/10/28 14:17
 * @Version 1.0
 */
public interface IBizSignService extends IBaseService<BizSign> {

    void generateSign(BizSign bizSign) throws ApplicationException;

    void successBizSign(Long approveId);

    List<BizSign> getSignList(Long approveId);
}
