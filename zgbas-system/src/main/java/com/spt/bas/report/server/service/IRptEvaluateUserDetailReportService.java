package com.spt.bas.report.server.service;

import com.spt.bas.report.client.vo.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/20 11:31
 */

public interface IRptEvaluateUserDetailReportService {

    /**
     * 查询考核人员的考核项
     * @param evaluateId 考核人员 id
     * @return 考核项
     */
    Page<RptEvaluateUserDetailRemoteVo> selectEvaluateUserDetail(RptEvaluateUserDetailQueryVo queryVo);

    /**
     * 批量更新
     * @param detailScoreList
     */
    void updateList(List<RptDetailScoreVo> detailScoreList);

    Page<RptEvaluateUserVo> findEvaluateUserIdBySourceId(RptEvaluateUserSearchVo searchVo);

    /**
     * 根据evaluateUserIds 查询考核详情数据和指标数据
     * @param evaluateUserIds evaluateUserIds
     * @return 考核详情数据和指标数据
     */
    List<RptDetailAndItemRemoteVo> getDetailAndItemByEvaluateUserId(String evaluateUserIds);
}
