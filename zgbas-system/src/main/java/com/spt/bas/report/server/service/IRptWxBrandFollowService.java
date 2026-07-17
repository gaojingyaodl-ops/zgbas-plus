package com.spt.bas.report.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spt.bas.report.client.entity.RptWxBrandFollow;
import com.spt.bas.report.client.entity.RptWxBrandUpdate;
import com.spt.bas.report.client.vo.RptFollowBrandVo;

import java.util.List;

public interface IRptWxBrandFollowService extends IService<RptWxBrandFollow> {
    /**
     * 查询企业关注的品种牌号的个数
     * @param userId
     * @return
     */
    int queryUserAttentNum(Long userId);

    /**
     * 企业修改关注品种
     * @param wxBrandUpdate
     */
    void updateUserAttent(RptWxBrandUpdate wxBrandUpdate);

    /**
     * 查询企业关注的品种牌号的列表
     * @return
     */
    List<RptFollowBrandVo> queryUserAttentList(Long userId);

    /**
     * 企业删除关注品种
     * @param brandId
     * @param userId
     */
    void deleteUserAttent(Long brandId, Long userId);

    /**
     * 企业添加关注品种
     * @param wxBrandFollow
     */
    void addUserAttent(RptWxBrandFollow wxBrandFollow);

}
