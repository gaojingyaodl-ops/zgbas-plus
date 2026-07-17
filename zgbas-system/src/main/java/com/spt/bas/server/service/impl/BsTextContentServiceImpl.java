package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsTextContent;
import com.spt.bas.server.dao.BsTextContentDao;
import com.spt.bas.server.service.IBsTextContentService;
import com.spt.tools.jpa.dao.BaseDao;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.spt.tools.jpa.service.BaseService;

import java.util.List;


@Component
public class BsTextContentServiceImpl extends BaseService<BsTextContent> implements IBsTextContentService {

    @Autowired
    private BsTextContentDao bsTextContentDao;

    @Override
    public BaseDao<BsTextContent> getBaseDao() {
        return bsTextContentDao;
    }

    @Override
    public BsTextContent findNewTextContentByType(String textType) {
        List<BsTextContent> bsTextContentList = bsTextContentDao.findNewTextContentByType(textType);
        if (CollectionUtils.isNotEmpty(bsTextContentList)) {
            return bsTextContentList.get(0);
        }
        return null;
    }
}
