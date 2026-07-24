package com.spt.bas.purchase.wx.server.api;

import com.spt.bas.client.entity.ApplyEntrust;
import com.spt.bas.client.remote.ICompanyOrderClient;
import com.spt.bas.client.vo.CompanyOrderResVo;
import com.spt.bas.purchase.wx.client.entity.CompanyUser;
import com.spt.bas.purchase.wx.client.payload.AuthFaceRecognition;
import com.spt.bas.purchase.wx.client.vo.CompanyOnLineApplyVo;
import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.Status;
import com.spt.bas.purchase.wx.server.payload.UploadBase64Request;
import com.spt.bas.purchase.wx.server.service.IUserInfoService;
import com.spt.bas.purchase.wx.server.service.impl.UserService;
import com.spt.bas.purchase.wx.server.util.UploadHelper;
import com.spt.bas.purchase.wx.server.vo.LicenseVo;
import com.spt.bas.purchase.wx.server.vo.UploadFileVo;
import com.spt.tools.core.bean.RespVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 */
@RestController
@RequestMapping(value = "purchase/open")
public class WxOpenApi extends BaseApi<CompanyUser> {

    @Autowired
    private UserService userService;
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private UploadHelper uploadHelper;
    @Autowired
    private ICompanyOrderClient companyOrderClient;
    
    
    @Override
    public IDataService<CompanyUser> getService() {
        return userService;
    }

    @RequestMapping(value = "saveApplyOnLineData")
    public RespVo<CompanyUser> saveApplyOnLineData(@RequestBody CompanyOnLineApplyVo vo){
        return userService.saveApplyOnLineData(vo);
    }

    /**
     * 扫描工商营业执照
     *
     * @param fileRequest
     * @param req
     * @return
     */
    @ApiOperation(value = "扫描工商营业执照")
    @PostMapping("/uploadAndOcrLicenseCode")
    @ApiOperationSupport(order = 6)
    public ApiResult uploadAndOcrLicenseCode(@Valid @RequestBody UploadBase64Request fileRequest, HttpServletRequest req) {
        return ApiResult.ofSuccess(userInfoService.uploadAndOcrLicenseCode(fileRequest, req));
    }

    /**
     * 附件上传
     * @return
     */
    @ApiOperation(value = "附件上传")
    @PostMapping("/uploadFile")
    @ApiOperationSupport(order = 10)
    public ApiResult uploadFile(@Valid @RequestBody UploadBase64Request fileRequest, HttpServletRequest req){
        return ApiResult.ofSuccess(uploadHelper.uploadBase64(fileRequest, req));
    }

    /**
     * 发起委托授权申请
     * @return
     */
    @ApiOperation(value = "浙塑网站发起委托授权申请")
    @PostMapping("/applyEntrustCms")
    @ApiOperationSupport(order = 15)
    public ApiResult applyEntrustCms(@RequestBody ApplyEntrust entrust, HttpServletRequest req) {
        userInfoService.applyEntrustCms(entrust);
        return ApiResult.ofSuccess();
    }

    /**
     * 视频人脸识别认证
     *
     * @return
     */
    @ApiOperation(value = "2.13.视频人脸识别认证")
    @PostMapping("/authFaceRecognition")
    @ApiOperationSupport(order = 72)
    public ApiResult authFaceRecognition(@RequestBody @Valid AuthFaceRecognition authFaceRecognition)  {
        Map map = null;
        try {
            map = userInfoService.authFaceRecognition(authFaceRecognition);
        } catch (Exception e) {
            logger.error("视频人脸识别认证异常", e);
            throw new BaseException(Status.ERROR);
        }
        return ApiResult.ofSuccess(map);
    }

    /**
     * 查询交易记录
     *
     * @return
     */
    @ApiOperation(value = "5.1.查询交易记录")
    @PostMapping("/findCompanyOrder")
    @ApiOperationSupport(order = 90)
    public ApiResult findCompanyOrder(@RequestBody String minute)  {
        return ApiResult.ofSuccess(companyOrderClient.findCompanyOrder(minute));
    }

    
}
