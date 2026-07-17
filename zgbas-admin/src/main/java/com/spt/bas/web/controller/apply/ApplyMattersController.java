package com.spt.bas.web.controller.apply;

import com.hsoft.file.sdk.constant.FileConstant;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.util.Base64Utility;
import com.hsoft.file.sdk.vo.FileRespVo;
import com.hsoft.file.sdk.vo.FileUploadBase64Request;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyLogisticsAdjust;
import com.spt.bas.client.entity.ApplyMatters;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.FileRecord;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 申请-事项申请
 */
@Controller
@RequestMapping(value = "/apply/matters")
public class ApplyMattersController extends PageController<ApplyMatters, BaseVo> {
    
    @Autowired
    private IApplyMattersClient applyMattersClient;
    @Autowired
    private FileRemote fileRemote;
    @Autowired
    private IFileRecordClient fileRecordClient;
    @Value("${file.show.url}")
    private String fileShowUrl;

    @Override
    public BaseClient<ApplyMatters> getService() {
        return applyMattersClient;
    }
    
    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        ApplyMatters entity = getEntity(id);
        if (entity.getId() == null) {
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_MATTERS_TYPE);
        EasyTreeNode nodes = EasyTreeUtil2.getMattersTree(listByCategory, true);
        model.addAttribute("mattersTypeTree", JsonUtil.obj2Json(nodes.getChildren()));
        model.addAttribute("ownRegionType", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_OWN_REGION)));
        model.addAttribute("fileShowUrl", fileShowUrl);
        //业务类型
        return "apply/applyMatters";
    }

    
    @ModelAttribute("preload")
    public ApplyMatters getEntity(@RequestParam(value = "id", required = false) Long id) {
        ApplyMatters entity = new ApplyMatters();
        entity.setStatus(BasConstants.APPROVE_STATUS_N);
        if (id != null && id != 0L) {
            entity = getService().getEntity(id);
        }
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
            applyMattersClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }
}
