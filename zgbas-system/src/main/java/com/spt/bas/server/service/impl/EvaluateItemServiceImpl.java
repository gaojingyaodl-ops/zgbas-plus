package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.EvaluateItem;
import com.spt.bas.server.dao.EvaluateItemDao;
import com.spt.bas.server.service.IEvaluateItemService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 考核条目
 */
@Component
@Slf4j
public class EvaluateItemServiceImpl extends BaseService<EvaluateItem> implements IEvaluateItemService {
    @Autowired
    private EvaluateItemDao evaluateItemDao;
    @Override
    public BaseDao<EvaluateItem> getBaseDao() {
        return evaluateItemDao;
    }

    @Override
    public List<EvaluateItem> selectEvaluateItemsByIds(String evaluateItemIds) {
        List<EvaluateItem> result = new ArrayList<>();
        if(StringUtils.isNotBlank(evaluateItemIds)){
            List<Long> idList = Arrays.stream(evaluateItemIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
            Iterable<EvaluateItem> evaluateUserDetails = evaluateItemDao.findAllById(idList);
            evaluateUserDetails.forEach(result::add);
        }
        return result;
    }
}
