package com.spt.bas.report.server.service;

import com.spt.bas.report.client.vo.RptSummaryResultVo;
import com.spt.bas.report.client.vo.RptSummaryRoiResultVo;
import com.spt.bas.report.client.vo.RptUserRoiVo;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/4/11 16:41
 */

public interface IRptSummaryRoiService {
    /**
     * 查询每页的数据
     *
     * @param vo 查询参数
     * @return 结果
     */
    RptSummaryResultVo findPage(RptUserRoiVo vo);

    List<RptSummaryRoiResultVo> getSummaryRoiResult(RptUserRoiVo vo);
}
