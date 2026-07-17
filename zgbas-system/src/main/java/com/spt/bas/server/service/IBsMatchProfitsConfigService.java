package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsMatchProfitsConfig;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * @Author: gaojy
 * @create 2022/2/8 14:10
 * @version: 1.0
 * @description:
 */
public interface IBsMatchProfitsConfigService extends IBaseService<BsMatchProfitsConfig> {
    public List<BsMatchProfitsConfig> findByMathUserId(Long mathUserId);
}
