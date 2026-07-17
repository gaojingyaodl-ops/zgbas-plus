package com.spt.bas.report.server.dao;

import com.spt.bas.client.entity.EvaluateUserDetail;
import com.spt.bas.report.client.vo.*;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/20 11:37
 */
@MyBatisDao
public interface RptEvaluateUserDetailReportMapper {

    /**
     * 查询考核人员的考核项
     * @param queryVo 查询参数
     * @return 考核项
     */
    List<RptEvaluateUserDetailRemoteVo> selectEvaluateUserDetail(RptEvaluateUserDetailQueryVo queryVo);

    /**
     * 更新
     * @param detailScoreVo
     */
    void updateDetailScoreVo(RptDetailScoreVo detailScoreVo);

    List<RptEvaluateUserVo> findEvaluateUserIdBySourceId(RptEvaluateUserSearchVo searchVo);

    List<EvaluateUserDetail> findDetailByEvaluateUserIds(List<Long> evaluateIds, String up);

    List<RptDetailAndItemRemoteVo> getDetailAndItemByEvaluateUserId(List<Long> list);
}
