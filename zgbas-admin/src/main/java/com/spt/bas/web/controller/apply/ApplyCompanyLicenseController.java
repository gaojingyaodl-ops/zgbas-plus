package com.spt.bas.web.controller.apply;

import com.hsoft.file.sdk.constant.FileConstant;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.util.Base64Utility;
import com.hsoft.file.sdk.vo.FileRespVo;
import com.hsoft.file.sdk.vo.FileUploadBase64Request;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.OwnRegionEnum;
import com.spt.bas.client.entity.ApplyCompanyLicense;
import com.spt.bas.client.entity.BsCompanyOur;
import com.spt.bas.client.entity.CompanyLicense;
import com.spt.bas.client.entity.FileRecord;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 公司证照申请管理controller
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/20 10:31
 */
@Controller
@RequestMapping(value = "/apply/companyLicense")
public class ApplyCompanyLicenseController extends PageController<ApplyCompanyLicense, BaseVo> {


    @Autowired
    private FileRemote fileRemote;
    @Autowired
    private IFileRecordClient fileRecordClient;
    @Autowired
    private IApplyCompanyLicenseClient applyCompanyLicenseClient;
    @Autowired
    private IBsCompanyOurClient bsCompanyOurClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Autowired
    private ICompanyLicenseClient companyLicenseClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public BaseClient<ApplyCompanyLicense> getService() {
        return applyCompanyLicenseClient;
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        model.addAttribute("processCode", processCode);
        ApplyCompanyLicense entity = getEntity(id);
        model.addAttribute("entity", Objects.nonNull(entity) ? entity : new ApplyCompanyLicense());
        // 文件类型
        model.addAttribute("companyLicenseFileTypeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.COMPANY_LICENSE_FILE_TYPE)));
        model.addAttribute("companyLicenseUseTypeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.COMPANY_LICENSE_USER_TYPE)));
        List<BsCompanyOur> companyOurs = bsCompanyOurClient.findAll();
        if (CollectionUtils.isNotEmpty(companyOurs)) {
            List<Map<String, Object>> companyList = companyOurs.stream().filter(e -> Boolean.TRUE.equals(e.getEnableFlg())).map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("companyName", e.getCompanyName());
                map.put("id", e.getId());
                map.put("companyCode", e.getCompanyCd());
                map.put("companyAbbr", e.getCompanyAbbr());
                return map;
            }).collect(Collectors.toList());
            String s = JsonUtil.obj2Json(companyList);
            model.addAttribute("companyListJson", s);
        } else {
            model.addAttribute("companyListJson", new ArrayList<>());
        }

        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());

        model.addAttribute("psv", permissionVo);
        //业务类型
        return "apply/applyCompanyLicense";
    }


    @ModelAttribute("preload")
    public ApplyCompanyLicense getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null && !Objects.equals(id, 0L)) {
            return applyCompanyLicenseClient.getEntity(id);
        }

        ApplyCompanyLicense entity = new ApplyCompanyLicense();
        entity.setApplyDate(new Date());
        entity.setStatus(BasConstants.APPROVE_STATUS_N);
        entity.setApplyUserName(ShiroUtil.getCurrentUserName());
        entity.setApplyUserId(ShiroUtil.getCurrentUserId());
        entity.setDeptId(ShiroUtil.getDeptId());
        SysDeptSdk sysDeptSdk = authOpenFacade.findDeptById(ShiroUtil.getDeptId());
        entity.setOwnRegion(Objects.nonNull(sysDeptSdk) && Objects.nonNull(OwnRegionEnum.getRegionEnumByName(sysDeptSdk.getDeptName()))
                ? Objects.requireNonNull(OwnRegionEnum.getRegionEnumByName(sysDeptSdk.getDeptName())).getRegionCode()
                : "");
        entity.setId(0L);
        return entity;
    }


    @PostMapping("uploadFile")
    @ResponseBody
    private String uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest req) {
        FileUploadBase64Request fileRequest = new FileUploadBase64Request();
        String[] allTypes = {".jpg", ".gif", ".png", ".bmp", ".pdf", ".docx", ".doc"};
        fileRequest.setAllowTypes(allTypes);
        fileRequest.setFilePath("bas/");
        fileRequest.setServerName(req.getServerName());
        fileRequest.setAppCode(PropertiesUtil.getProperty(FileConstant.FILE_BUCKET));
        //将上传的文件转为base64
        List<FileUploadBase64Request.Base64DataVo> dataList = new ArrayList<>();
        FileUploadBase64Request.Base64DataVo dataVo = new FileUploadBase64Request.Base64DataVo();
        try {
            dataVo.setFileName(file.getOriginalFilename());
            dataVo.setBase64Data(Base64Utility.base64Encode(file.getBytes()));
            dataList.add(dataVo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileRequest.setDataList(dataList);
        FileRespVo fileRespVo = fileRemote.uploadBase64(fileRequest);
        String fileId = fileRespVo.getFileId();
        // 去除","
        if (fileId.indexOf(",") > 0) {
            fileId = fileId.split(",")[0];
        }
        try {
            saveFileRecord(fileId, dataList);
        } catch (Exception e) {
            logger.error("上传附件,保存fileRecord失败", e);
        }
        return fileId;

    }
    /**
     * 附件上传，保存fileRecord
     *
     * @param dataList
     */
    private void saveFileRecord(String fileId, List<FileUploadBase64Request.Base64DataVo> dataList) {
        FileRecord fileRecord = new FileRecord();
        if (org.apache.commons.lang3.StringUtils.isEmpty(fileId)) {
            return;
        }
        fileRecord.setFileId(fileId);
        fileRecord.setFileName(dataList.get(0).getFileName());
        fileRecordClient.save(fileRecord);
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo,
                             HttpServletResponse response) {
        try {
            applyCompanyLicenseClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    /**
     *
     * @return
     */
    @RequestMapping("/downloadPic")
    public String downloadPic(ApplyCompanyLicense license,
                              Model model) {
        ApplyCompanyLicense entity = getEntity(license.getId());
        List<CompanyLicense> licenseList = applyCompanyLicenseClient.downloadPicUrl(entity);
        model.addAttribute("companyName", entity.getCompanyName());
        model.addAttribute("licenseList", licenseList);
        return "apply/downloadPic";
    }

}
