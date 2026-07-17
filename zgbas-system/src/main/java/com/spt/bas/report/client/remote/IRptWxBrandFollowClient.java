package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptWxBrandFollow;
import com.spt.bas.report.client.entity.RptWxBrandUpdate;
import com.spt.bas.report.client.vo.RptFollowBrandVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 牌号关注服务
 */
@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/wx/wxBrandFollow",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptWxBrandFollowClient {

    /**
     * 查询企业关注的品种牌号的个数
     * @param wxUserId
     * @return
     */
    @PostMapping(value = "queryUserAttentNum")
    int queryUserAttentNum(Long wxUserId);

    /**
     * 企业修改关注品种
     * @param wxBrandUpdate
     */
    @PostMapping(value = "updateUserAttent")
    void updateUserAttent(RptWxBrandUpdate wxBrandUpdate);

    /**
     * 查询企业关注的品种牌号的列表
     * @return
     */
    @PostMapping(value = "queryUserAttentList")
    List<RptFollowBrandVo> queryUserAttentList(Long wxUserId);

    /**
     * 企业删除关注品种
     *
     * @return
     */
    @PostMapping(value = "deleteUserAttent")
    void deleteUserAttent(@RequestParam("brandId") Long type, @RequestParam("userId") Long userId);

    /**
     * 企业添加关注品种
     * @return
     */
    @PostMapping(value = "addUserAttent")
    void addUserAttent(RptWxBrandFollow wxBrandFollow);

}
