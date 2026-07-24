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
// Phase 8 (D-P8-01 minimal fix, behavior-equivalent): explicit bean name disambiguates this from
// com.spt.bas.web.controller.FileController (@Controller, /file/*). Both classes share the simple
// name FileController → Spring would derive the same default bean name "fileController" →
// ConflictingBeanDefinitionException on context load. The source avoided it via module-isolated
// scans (basWx app scanned only its package); the monolith's broad com.spt scan requires an
// explicit name. Route spaces are disjoint (/wx/file/* here vs /file/* in the web controller),
// so both stay active as distinct beans — mirrors the P5 precedent for RptBaseCostApi
// (@RestController("reportRptBaseCostApi")). No business-semantic change.
@RestController("wxFileController")
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
