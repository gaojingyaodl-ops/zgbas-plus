package com.spt.bas.web.controller.system;

import com.hsoft.file.sdk.constant.FileConstant;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.util.Base64Utility;
import com.hsoft.file.sdk.vo.FileRespVo;
import com.hsoft.file.sdk.vo.FileUploadBase64Request;
import com.hsoft.file.sdk.vo.FileUploadBase64Request.Base64DataVo;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.UserChangeVo;
import com.spt.auth.sdk.vo.UserLoginVo;
import com.spt.bas.client.entity.FileRecord;
import com.spt.bas.client.remote.IFileRecordClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.web.util.RenderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 个人信息 业务处理
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SysProfileController.class);

    private String prefix = "system/user/profile";

    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Autowired
    private FileRemote fileRemote;

    @Autowired
    private IFileRecordClient fileRecordClient;

    /**
     * 个人信息
     */
    @GetMapping()
    public String profile(ModelMap mmap) {
        SysUserSdk user = authOpenFacade.findUserById(ShiroUtil.getCurrentUserId());
        mmap.put("user", user);
        return prefix + "/profile";
    }

    @GetMapping("/checkPassword")
    @ResponseBody
    public boolean checkPassword(String password) {
        UserLoginVo vo = new UserLoginVo();
        vo.setPassword(password);
        vo.setUserId(ShiroUtil.getCurrentUserId());
        if (authOpenFacade.isPwdEqual(vo)) {
            return true;
        }
        return false;
    }

    @GetMapping("/resetPwd")
    public String resetPwd(ModelMap mmap) {
        SysUserSdk user = authOpenFacade.findUserById(ShiroUtil.getCurrentUserId());
        mmap.put("user", user);
        return prefix + "/resetPwd";
    }

    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    @ResponseBody
    public AjaxResult resetPwd(String oldPassword, String newPassword) {
        UserLoginVo vo = new UserLoginVo();
        vo.setPassword(oldPassword);
        vo.setUserId(ShiroUtil.getCurrentUserId());
        if (!authOpenFacade.isPwdEqual(vo)) {
            return error("修改密码失败，旧密码错误");
        }
        if (StringUtils.equals(oldPassword, newPassword)) {
            return error("新密码不能与旧密码相同");
        }

        UserChangeVo changeVo = new UserChangeVo();
        changeVo.setUserId(ShiroUtil.getCurrentUserId());
        changeVo.setPassword(newPassword);
        authOpenFacade.updateUser(changeVo);
        return success();
    }

    /**
     * 修改用户
     */
    @GetMapping("/edit")
    public String edit(ModelMap mmap) {
        SysUserSdk user = authOpenFacade.findUserById(ShiroUtil.getCurrentUserId());
        mmap.put("user", user);
        return prefix + "/edit";
    }

    /**
     * 修改头像
     */
    @GetMapping("/avatar")
    public String avatar(ModelMap mmap) {
        SysUserSdk user = authOpenFacade.findUserById(ShiroUtil.getCurrentUserId());
        mmap.put("user", user);
        return prefix + "/avatar";
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    @ResponseBody
    public AjaxResult update(SysUserSdk user) {
        SysUserSdk currentUser = authOpenFacade.findUserById(ShiroUtil.getCurrentUserId());
        currentUser.setNickName(user.getNickName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        authOpenFacade.saveUser(currentUser);
        return success();
    }

    /**
     * 更改头像
     *
     * @param vo
     * @param response
     */
    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            SysUserSdk user = authOpenFacade.findUserById(vo.getId());
            user.setAvatar(vo.getFileId());
            authOpenFacade.saveUser(user);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    private String uploadFile(MultipartFile file, HttpServletRequest req) {
        FileUploadBase64Request fileRequest = new FileUploadBase64Request();
        String[] allTypes = {".jpg", ".gif", ".png", ".bmp", ".pdf", ".docx", ".doc"};
        fileRequest.setAllowTypes(allTypes);
        fileRequest.setFilePath("bas/");
        fileRequest.setServerName(req.getServerName());
        fileRequest.setAppCode(PropertiesUtil.getProperty(FileConstant.FILE_BUCKET));
        //将上传的文件转为base64
        List<Base64DataVo> dataList = new ArrayList<>();
        Base64DataVo dataVo = new Base64DataVo();
        try {
            dataVo.setFileName("header.png");
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
    private void saveFileRecord(String fileId, List<Base64DataVo> dataList) {
        FileRecord fileRecord = new FileRecord();
        if (org.apache.commons.lang3.StringUtils.isEmpty(fileId)) {
            return;
        }
        fileRecord.setFileId(fileId);
        fileRecord.setFileName(dataList.get(0).getFileName());
        fileRecordClient.save(fileRecord);
    }

    /**
     * 保存头像
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PostMapping("/updateAvatar")
    @ResponseBody
    public AjaxResult updateAvatar(@RequestParam("avatarfile") MultipartFile file, HttpServletRequest req) {
        SysUserSdk currentUser = authOpenFacade.findUserById(ShiroUtil.getCurrentUserId());
        try {
            String fileId = uploadFile(file, req);
            currentUser.setAvatar(fileId);
            authOpenFacade.saveUser(currentUser);
            return success();
        } catch (Exception e) {
            log.error("修改头像失败！", e);
            return error(e.getMessage());
        }
    }
}
