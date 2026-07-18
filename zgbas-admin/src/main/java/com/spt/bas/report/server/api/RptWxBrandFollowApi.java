package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptWxBrandFollow;
import com.spt.bas.report.client.entity.RptWxBrandUpdate;
import com.spt.bas.report.client.vo.RptFollowBrandVo;
import com.spt.bas.report.server.service.IRptWxBrandFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  牌号关注服务
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-13 16:32
 */
@RestController
@RequestMapping(value = "/wx/wxBrandFollow")
public class RptWxBrandFollowApi {
    @Autowired
    private IRptWxBrandFollowService iWxBrandFollowService;

    /**
     * 查询企业关注的品种牌号的个数
     * @param userId
     * @return
     */
    @PostMapping("/queryUserAttentNum")
    public int queryUserAttentNum(@RequestBody Long userId) {
        return iWxBrandFollowService.queryUserAttentNum(userId);
    }

    /**
     * 企业修改关注品种
     * @param wxBrandUpdate
     */
    @PostMapping("/updateUserAttent")
    public void updateUserAttent(@RequestBody RptWxBrandUpdate wxBrandUpdate){
        iWxBrandFollowService.updateUserAttent(wxBrandUpdate);
    }

    /**
     * 查询企业关注的品种牌号的列表
     * @param userId
     * @return
     */
    @PostMapping("/queryUserAttentList")
    public List<RptFollowBrandVo> queryUserAttentList(@RequestBody Long userId) {
        return iWxBrandFollowService.queryUserAttentList(userId);
    }

    /**
     * 企业删除关注品种
     * @param brandId
     * @param userId
     */
    @PostMapping("/deleteUserAttent")
    public void deleteUserAttent(Long brandId, Long userId) {
        iWxBrandFollowService.deleteUserAttent(brandId, userId);
    }

    /**
     * 5. 企业添加关注品种
     *
     * @param wxBrandFollow
     * @return
     */
    @PostMapping("/addUserAttent")
    public void addUserAttent(@RequestBody RptWxBrandFollow wxBrandFollow) {
        iWxBrandFollowService.addUserAttent(wxBrandFollow);
    }
}
