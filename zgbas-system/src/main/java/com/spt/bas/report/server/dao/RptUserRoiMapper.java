package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.vo.RptUserRoiResultVo;
import com.spt.bas.report.client.vo.RptUserRoiVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/21 15:59
 */
@MyBatisDao
public interface RptUserRoiMapper {

    /**
     * 查询人员ROI报表
     *
     * @return 结果
     */
    List<RptUserRoiResultVo> findPage(RptUserRoiVo userRoiVo);

}
