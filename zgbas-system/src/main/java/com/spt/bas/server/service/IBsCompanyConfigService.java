package com.spt.bas.server.service;


import com.spt.bas.client.entity.BsCompanyConfig;
import com.spt.tools.jpa.service.IBaseService;


/**
 * @Author: gaojy
 * @create 2022/4/2 11:02
 * @version: 1.0
 * @description:
 */
public interface IBsCompanyConfigService extends IBaseService<BsCompanyConfig> {

    BsCompanyConfig findByBsCompanyIdAndMatchUserId(Long bsCompanyId, Long matchUserId);

    BsCompanyConfig findConfigByCompanyId(Long bsCompanyId);
}
