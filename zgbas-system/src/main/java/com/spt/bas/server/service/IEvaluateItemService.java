package com.spt.bas.server.service;

import com.spt.bas.client.entity.EvaluateItem;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IEvaluateItemService extends IBaseService<EvaluateItem> {
    /**
     * 根据id批量查询数据
     * @param evaluateItemIds id，用逗号分割
     * @return
     */
    List<EvaluateItem> selectEvaluateItemsByIds(String evaluateItemIds);
}
