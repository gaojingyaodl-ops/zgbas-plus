package com.spt.bas.purchase.wx.server.controller;

import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.payload.BrandRequest;
import com.spt.bas.purchase.wx.server.util.UserHelper;
import com.spt.bas.report.client.entity.RptWxBrandFollow;
import com.spt.bas.report.client.entity.RptWxBrandUpdate;
import com.spt.bas.report.client.remote.IRptWxBrandFollowClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 关注
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-13 11:37
 */
@RestController
@RequestMapping(value = "/wx/userattent")
@Api(tags = "微信小程序关注相关接口")
@ApiSort(value = 3)
public class UserAttentController extends BaseController {

    @Autowired
    private IRptWxBrandFollowClient wxBrandFollowClient;

    /**
     * 1.询企业关注的品种牌号的个数
     *
     * @return
     */
    @ApiOperation(value = "查询企业关注的品种牌号的个数")
    @PostMapping("/queryUserAttentNum")
    @ApiOperationSupport(order = 1)
    public ApiResult queryUserAttentNum() {
        return ApiResult.ofSuccess(returnMap("number", wxBrandFollowClient.queryUserAttentNum(UserHelper.getCurUserId())));
    }

    /**
     * 2.查询企业关注的品种牌号的列表
     *
     * @return
     */
    @ApiOperation(value = "查询企业关注的品种牌号的列表")
    @PostMapping("/queryUserAttentList")
    @ApiOperationSupport(order = 2)
    public ApiResult queryUserAttentList() {
        return ApiResult.ofSuccess(wxBrandFollowClient.queryUserAttentList(UserHelper.getCurUserId()));
    }

    /**
     * 3.企业修改关注品种
     *
     * @return
     */
    @ApiOperation(value = "企业修改关注品种")
    @PostMapping("/updateUserAttent")
    @ApiOperationSupport(order = 3)
    public ApiResult updateUserAttent(@RequestBody RptWxBrandUpdate wxBrandUpdate) {
        wxBrandUpdate.setUserId(UserHelper.getCurUserId());
        wxBrandFollowClient.updateUserAttent(wxBrandUpdate);
        return ApiResult.ofSuccess();
    }

    /**
     * 4.企业删除关注品种
     *
     * @param brandRequest
     * @return
     */
    @ApiOperation(value = "企业删除关注品种")
    @PostMapping("/deleteUserAttent")
    @ApiOperationSupport(order = 4)
    public ApiResult deleteUserAttent(@RequestBody BrandRequest brandRequest) {
        wxBrandFollowClient.deleteUserAttent(brandRequest.getBrandId(), UserHelper.getCurUserId());
        return ApiResult.ofSuccess();
    }

    /**
     * 5. 企业添加关注品种
     *
     * @param wxBrandFollow
     * @return
     */
    @ApiOperation(value = "企业添加关注品种")
    @PostMapping("/addUserAttent")
    @ApiOperationSupport(order = 5)
    public ApiResult addUserAttent(@RequestBody RptWxBrandFollow wxBrandFollow) {
        wxBrandFollow.setWxUserId(UserHelper.getCurUserId());
        wxBrandFollowClient.addUserAttent(wxBrandFollow);
        return ApiResult.ofSuccess();
    }

    /**
     * 4.查询企业未关注的热门品种牌号列表
     *
     * @return
     */
    @ApiOperation(value = "查询企业未关注的热门品种牌号列表(未实现)")
    @PostMapping("/getNewBrandList")
    @ApiOperationSupport(order = 99)
    public ApiResult getNewBrandList() {
        return ApiResult.ofSuccess();
    }


}
