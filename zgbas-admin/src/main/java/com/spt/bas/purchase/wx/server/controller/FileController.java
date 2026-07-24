package com.spt.bas.purchase.wx.server.controller;

import com.hsoft.file.sdk.vo.FileRespVo;
import com.hsoft.file.sdk.vo.FileUploadBase64Request;
import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.payload.UploadBase64Request;
import com.spt.bas.purchase.wx.server.util.OcrUtils;
import com.spt.bas.purchase.wx.server.util.UploadHelper;
import com.spt.bas.purchase.wx.server.vo.UploadFileVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-22 13:39
 */
@RestController
@RequestMapping(value = "/wx/file")
@Api(tags = "上传接口")
@ApiSort(value = 4)
public class FileController {

    @Autowired
    private UploadHelper uploadHelper;

    /**
     * 通用上传base64
     *
     * @param
     * @return
     */
    @ApiOperation(value = "通用上传base64")
    @PostMapping("/uploadBase64")
    public ApiResult uploadBase64(@RequestBody UploadBase64Request fileRequest, HttpServletRequest req) {
        UploadFileVo fileRespVo = uploadHelper.uploadBase64(fileRequest, req);
        return ApiResult.ofMessage("上传成功", fileRespVo);
    }

}
