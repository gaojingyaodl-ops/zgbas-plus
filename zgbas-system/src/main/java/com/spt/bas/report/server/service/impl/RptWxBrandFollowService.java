package com.spt.bas.report.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spt.bas.report.client.entity.RptWxBrandFollow;
import com.spt.bas.report.client.entity.RptWxBrandUpdate;
import com.spt.bas.report.client.vo.RptFollowBrandVo;
import com.spt.bas.report.server.dao.RptWxBrandFollowMapper;
import com.spt.bas.report.server.service.IRptWxBrandFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-13 16:34
 */
@Component
public class RptWxBrandFollowService extends ServiceImpl<RptWxBrandFollowMapper,RptWxBrandFollow> implements IRptWxBrandFollowService{
    @Autowired
    private RptWxBrandFollowMapper wxBrandFollowMapper;

    /**
     * 查询企业关注的品种牌号的个数
     *
     * @param userId
     * @return
     */
    @Override
    public int queryUserAttentNum(Long userId) {
        QueryWrapper<RptWxBrandFollow> query = new QueryWrapper<>();
        query.eq("wx_user_id", userId);
        return count(query);
    }

    /**
     * 企业修改关注品种
     *
     * @param wxBrandUpdate
     */
    @Override
    @Transactional
    public void updateUserAttent(RptWxBrandUpdate wxBrandUpdate) {
        Long userId = wxBrandUpdate.getUserId();
        // 先删除用户该品种下所有关注的牌号 再保存
        if (userId!= null) {
            QueryWrapper queryWrapper = new QueryWrapper();
            remove((Wrapper<RptWxBrandFollow>) queryWrapper.eq("wx_user_id", wxBrandUpdate.getUserId()));
        }

        List<RptWxBrandFollow> brandLists = wxBrandUpdate.getBrandLists();

        for (RptWxBrandFollow brandList : brandLists) {
            brandList.setWxUserId(userId);
        }

        // 批量保存
        saveBatch(brandLists);
    }

    /**
     * 查询企业关注的品种牌号的列表
     *
     * @return
     */
    @Override
    public List<RptFollowBrandVo> queryUserAttentList(Long userId) {
        return wxBrandFollowMapper.queryUserAttentList(userId);
    }

    /**
     * 企业删除关注品种
     *
     * @param brandId
     * @param userId
     */
    @Override
    public void deleteUserAttent(Long brandId, Long userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("brand_id", brandId);
        queryWrapper.eq("wx_user_id", userId);
        remove(queryWrapper);
    }

    /**
     * 企业添加关注品种
     *
     * @param wxBrandFollow
     */
    @Override
    public void addUserAttent(RptWxBrandFollow wxBrandFollow) {
        save(wxBrandFollow);
    }
}
