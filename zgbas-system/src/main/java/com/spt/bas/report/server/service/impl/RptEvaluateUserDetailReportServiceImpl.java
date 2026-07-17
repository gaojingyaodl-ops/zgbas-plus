package com.spt.bas.report.server.service.impl;

import com.spt.bas.client.entity.EvaluateUserDetail;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.report.server.dao.RptEvaluateUserDetailReportMapper;
import com.spt.bas.report.server.service.IRptEvaluateUserDetailReportService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/20 11:32
 */

@Service
public class RptEvaluateUserDetailReportServiceImpl implements IRptEvaluateUserDetailReportService {

    @Autowired
    private RptEvaluateUserDetailReportMapper evaluateUserDetailReportDao;

    /**
     * 查询考核人员的考核项
     * @param queryVo 查询参数
     * @return 考核项
     */
    @Override
    public Page<RptEvaluateUserDetailRemoteVo> selectEvaluateUserDetail(RptEvaluateUserDetailQueryVo queryVo) {
        Pageable pageable = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
        List<RptEvaluateUserDetailRemoteVo> result = evaluateUserDetailReportDao.selectEvaluateUserDetail(queryVo);
        if(!"0".equals(queryVo.getType())){
            // hr 评分
            List<RptEvaluateUserDetailRemoteVo> collect = result.stream().filter(e -> null != e.getDetailScore()).collect(Collectors.toList());
            return new PageImpl<>(collect, pageable, queryVo.getCount());
        }
        return new PageImpl<>(result, pageable, queryVo.getCount());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateList(List<RptDetailScoreVo> detailScoreList) {
        for (RptDetailScoreVo detailScoreVo : detailScoreList) {
            evaluateUserDetailReportDao.updateDetailScoreVo(detailScoreVo);
        }
    }

    @Override
    public List<RptDetailAndItemRemoteVo> getDetailAndItemByEvaluateUserId(String evaluateUserIds) {
        List<Long> evaluateUserList = Arrays.stream(evaluateUserIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(evaluateUserList)){
            return Collections.emptyList();
        }
        return evaluateUserDetailReportDao.getDetailAndItemByEvaluateUserId(evaluateUserList);
    }

    @Override
    public Page<RptEvaluateUserVo> findEvaluateUserIdBySourceId(RptEvaluateUserSearchVo searchVo) {
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        List<RptEvaluateUserVo> result = evaluateUserDetailReportDao.findEvaluateUserIdBySourceId(searchVo);
        // 过滤待评分人id
        List<Long> evaluateIds = result.stream().map(RptEvaluateUserVo::getId).distinct().collect(Collectors.toList());
        // 查出对应待评分人的评分上级
        List<EvaluateUserDetail> evaluateUserDetails = evaluateUserDetailReportDao.findDetailByEvaluateUserIds(evaluateIds,"UP");
        return new PageImpl<>(result, pageable, searchVo.getCount());
    }
}
