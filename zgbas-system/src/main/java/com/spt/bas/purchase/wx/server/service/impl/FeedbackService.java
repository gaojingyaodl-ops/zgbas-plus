package com.spt.bas.purchase.wx.server.service.impl;

import com.spt.bas.client.entity.Feedback;
import com.spt.bas.server.dao.FeedbackDao;
import com.spt.bas.purchase.wx.server.service.IFeedbackService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 *    意见反馈
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-15 10:14
 */
@Component
public class FeedbackService extends BaseService<Feedback> implements IFeedbackService {

    @Autowired
    private FeedbackDao feedbackDao;

    @Override
    public BaseDao<Feedback> getBaseDao() {
        return feedbackDao;
    }
}
