package com.spt.bas.report.server.service;

import com.spt.bas.report.client.vo.RptUserRoiResultVo;
import com.spt.bas.report.client.vo.RptUserRoiVo;

import java.util.List;
import java.util.Map;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/21 15:58
 */

public interface IRptUserRoiService {
    List<RptUserRoiResultVo> findPage(RptUserRoiVo vo);

    List<RptUserRoiResultVo> getUserRoiResultVoList(RptUserRoiVo vo);

    /**
     * 合计
     *
     * @return 合计
     */
    Map<String, Object> getTotal(RptUserRoiVo userRoiVo);

}
