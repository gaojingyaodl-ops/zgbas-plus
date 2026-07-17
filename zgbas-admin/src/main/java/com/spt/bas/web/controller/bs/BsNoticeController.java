package com.spt.bas.web.controller.bs;


import com.hsoft.file.sdk.constant.FileConstant;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.util.Base64Utility;
import com.hsoft.file.sdk.vo.FileRespVo;
import com.hsoft.file.sdk.vo.FileUploadBase64Request;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysDictDataSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsNotice;
import com.spt.bas.client.entity.FileRecord;
import com.spt.bas.client.remote.IApplyInvoiceReceivedClient;
import com.spt.bas.client.remote.IBsNoticeClient;
import com.spt.bas.client.remote.IFileRecordClient;
import com.spt.bas.client.vo.BsNoticeVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.StringUtils;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/bs/bsNotice")
public class BsNoticeController extends PageController<BsNotice, BaseVo> {
    @Autowired
    private IBsNoticeClient bsNoticeClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IApplyInvoiceReceivedClient invoiceReceivedClient;

    private String prefix = "system/user/profile";

    @Autowired
    private FileRemote fileRemote;

    @Autowired
    private IFileRecordClient fileRecordClient;

    @Override
    public BaseClient<BsNotice> getService() {
        return bsNoticeClient;
    }

    @RequestMapping("/bsNoticeIndex")
    public String bsNotice(Model model, HttpServletRequest request) {
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_NOTICE_ADDANDEDIT.getPermissionCode()) || ShiroUtil.isPermitted(PermissionEnum.PERM_ADMIN_NEW.getPermissionCode())) {
            model.addAttribute("editFlag", "1");
        } else {
            model.addAttribute("editFlag", "2");
        }
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
                BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.ISSUIG_COMPANY)));
        model.addAttribute("announcementDeptTypeJson", JsonUtil.obj2Json(
                DictUtil.getListByCategory(BasConstants.ANNOUNCEMENT_DEPT_TYPE)));
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
        model.addAttribute("matchDeptNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        List<SysDictDataSdk> listByCategory = DictUtil.getListByCategory(BasConstants.ANNOUNCEMENT_DEPT_TYPE);
        model.addAttribute("announcementDeptFwTypeJson", JsonUtil.obj2Json(listByCategory));
        return "bs/BsNotice";
    }

    @RequestMapping(value = "pageList", method = RequestMethod.POST)
    public void pageList(PageSearchVo bsNoticeVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(bsNoticeVo, request);
        Long deptId = ShiroUtil.getDeptId();
        boolean permitted = ShiroUtil.isPermitted(PermissionEnum.PERM_NOTICE_ADDANDEDIT.getPermissionCode()) || ShiroUtil.isPermitted(PermissionEnum.PERM_ADMIN_NEW.getPermissionCode());
        if (Boolean.FALSE.equals(permitted)) {
            bsNoticeVo.getSearchParams().put("LIKES_receiveDeptId", deptId);
        }
        logger.info("searchVo : " + JsonUtil.obj2Json(bsNoticeVo));
        Map<String, Object> footer = new HashMap<>();
        PageDown<BsNotice> page = bsNoticeClient.findPage(bsNoticeVo);
        PageDown<BsNoticeVo> page2 = new PageDown<>();
        List<BsNoticeVo> content1 = new ArrayList<>();
        List<BsNotice> content2 = page.getContent();
        for (BsNotice bsNotice : content2) {
            BsNoticeVo vo = new BsNoticeVo();
            if (Boolean.TRUE.equals(permitted)) {
                //管理员可编辑公告
                vo.setEditFlag("1");
            }
            BeanUtils.copyProperties(bsNotice, vo);
            content1.add(vo);
        }
        page2.setContent(content1);
        int i = 0;
        List<BsNoticeVo> content = page2.getContent();
        for (BsNoticeVo bsNotice : content) {
            i++;
            String companyName = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.ISSUIG_COMPANY, bsNotice.getCompanyName());
            String sendDeptName = DictUtil.getValue(BasConstants.ANNOUNCEMENT_DEPT_TYPE, bsNotice.getSendDeptName());
            bsNotice.setCompanyName(companyName);
            bsNotice.setSendDeptName(sendDeptName);
            bsNotice.setPid((long) i);
        }
        JsonEasyUI.renderJson(response, page2, null, footer);
    }

    @RequestMapping(value = "show/{id}")
    public String show(@PathVariable("id") Long id, Model model, HttpServletResponse response) {
        BsNotice entity;
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        }
        return "bs/BsNoticeInsertShow";
    }

    @RequestMapping(value = "detail/{id}")
    public void detail(@PathVariable("id") Long id, HttpServletResponse response) {
        RenderUtil.renderJson(getEntity(id), response);
    }

    @RequestMapping(value = "insert/{id}")
    public String insert(@PathVariable("id") Long id, Model model, HttpServletResponse response) {
        BsNotice entity;
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new BsNotice();
            entity.setId(0l);
        }
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
                BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.ISSUIG_COMPANY)));
        model.addAttribute("announcementDeptTypeJson", JsonUtil.obj2Json(
                DictUtil.getListByCategory(BasConstants.ANNOUNCEMENT_DEPT_TYPE)));
        model.addAttribute("entity", entity);
        //发文部门
        List<SysDictDataSdk> listByCategory = DictUtil.getListByCategory(BasConstants.ANNOUNCEMENT_DEPT_TYPE);
        model.addAttribute("announcementDeptFwTypeJson", JsonUtil.obj2Json(listByCategory));
        // 获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo();
        deptSearchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        EasyTreeNode deptNodes = EasyTreeUtil2.getDeptTree(deptList, false,true);
        model.addAttribute("deptNameTree", JsonUtil.obj2Json(deptNodes.getChildren()));

        return "bs/BsNoticeInsert";
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo,
                             HttpServletResponse response) {
        try {
            invoiceReceivedClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
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

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public void save(BsNoticeVo bsNotice, HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
        String content = bsNotice.getContent();
        String fileid = "";
        List<String> idList = Arrays.stream(bsNotice.getFileId().split(",")).map(String::valueOf).collect(Collectors.toList());
        if (idList.get(0).equals("")) {
            idList.remove(0);
        }
        for (String s : idList) {
            if (StringUtils.equals(fileid, "") && content.contains(s) == true) {
                fileid = s;
            } else {
                if (content.contains(s) == true) {
                    fileid = fileid + "," + s;
                }
            }
        }
        bsNotice.setFileId(fileid);
        String year = bsNotice.getYear();
        if (StringUtils.isEmpty(year)) {
            Calendar date = Calendar.getInstance();
            year = String.valueOf(date.get(Calendar.YEAR));
            bsNotice.setYear(year);
        }
        BsNotice bsNotice1 = new BsNotice();
        if (bsNotice.getId() != null && bsNotice.getId() != 0) {
            bsNotice1 = getEntity(bsNotice.getId());
        }
        BeanUtils.copyProperties(bsNotice, bsNotice1);
        String result = "success";
        try {
            bsNoticeClient.save(bsNotice1);
        } catch (Exception e) {
            result = "失敗";
        }

        RenderUtil.renderText(result, response);
    }

    @RequestMapping("delete2/{id}")
    public void delete2(@PathVariable("id") Long id, HttpServletResponse response) {
        String result = "success";
        if (id != null && id != 0) {
            try {
                getService().delete(id);
            } catch (Exception e) {
                result = "失敗";
            }
        }
        RenderUtil.renderText(result, response);
    }

    @ModelAttribute("preload")
    public BsNotice getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0)
                return getService().getEntity(id);
            else {
                BsNotice entity = new BsNotice();
                entity.setId(0l);
                return entity;
            }
        }
        return null;
    }

}
