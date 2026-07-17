package com.spt.bas.server.service;

import com.spt.bas.client.entity.EvaluateUserDetail;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IEvaluateUserDetailService extends IBaseService<EvaluateUserDetail> {
    /**
     * 根据 ids 查询数据
     * @param ids ids
     * @return 数据
     */
    List<EvaluateUserDetail> selectEvaluateUserDetailByIds(String ids);

    /**
     * 根据 EvaluateUserId 查询数据
     * @param ids ids
     * @return 数据
     */
    List<EvaluateUserDetail> selectDetailByEvaluateUserId(String ids);
}
