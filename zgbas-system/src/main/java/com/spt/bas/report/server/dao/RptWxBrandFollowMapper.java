package com.spt.bas.report.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spt.bas.report.client.entity.RptWxBrandFollow;
import com.spt.bas.report.client.vo.RptFollowBrandVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptWxBrandFollowMapper extends BaseMapper<RptWxBrandFollow> {
    int queryUserAttentNum(Long userId);

//    RptWxBrandFollow findByUserId(Long userId);

    int deleteByUserId(Long userId);

    List<RptFollowBrandVo> queryUserAttentList(Long userId);

}
