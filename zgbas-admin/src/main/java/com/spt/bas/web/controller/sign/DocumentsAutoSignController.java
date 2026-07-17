package com.spt.bas.web.controller.sign;


import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SealUsage;
import com.spt.bas.client.entity.SignFile;
import com.spt.bas.client.entity.SignFileUser;
import com.spt.bas.client.remote.IPmApproveClient;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.client.remote.ISignFileClient;
import com.spt.bas.client.remote.ISignFileUserClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.sign.client.entity.EnterpriseAccount;
import com.spt.sign.client.entity.SignTransactor;
import com.spt.sign.client.remote.IEnterpriseAccountClient;
import com.spt.sign.client.remote.ISignTransactorClient;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 上传文件自动签署
 */
@Controller
@RequestMapping("documents/autoSign")
public class DocumentsAutoSignController extends SingleCrudControll<SignFile, BaseVo> {
    @Autowired
    private ISignFileUserClient signFileUserClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Resource
    private IEnterpriseAccountClient enterpriseAccountClient;
    @Resource
    private ISignTransactorClient signTransactorClient;
    @Resource
    private ISignFileClient signFileClient;
    @Autowired
    private IPmApproveClient pmApproveClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;

    @Override
    public BaseClient<SignFile> getService() {
        return signFileClient;
    }

    @RequestMapping(value = "")
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute("companyNameJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        return "apply/auto-sign-file";
    }

    /**
     * 跳转到详情页
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id) {
        SignFile entity;
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new SignFile();
            SysUserSdk user = authOpenFacade.findUserById(ShiroUtil.getCurrentUserId());
            entity.setCreator(Objects.nonNull(user) ? user.getNickName() : "");
            model.addAttribute("defaultDate", new Date());
        }
        model.addAttribute("entity", entity);
        model.addAttribute("entityid", id);
        // 印章-公司名称
        model.addAttribute("companyNameJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CUSTOMER_NAME)));
        // 签署印章类型
        model.addAttribute("signTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.SIGN_TYPE)));
        return "apply/auto-sign-file-detail";
    }

    @RequestMapping(value = "detail2/{id}")
    public String detail2(Model model, @PathVariable("id") Long id) {
        if (id == 0) {
            SignFile byAllLimit = signFileClient.findByAllLimit();
            id = byAllLimit.getId();
        }
        SignFile entity;
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new SignFile();
            SysUserSdk user = authOpenFacade.findUserById(ShiroUtil.getCurrentUserId());
            entity.setCreator(Objects.nonNull(user) ? user.getNickName() : "");
            model.addAttribute("defaultDate", new Date());
        }
        model.addAttribute("entity", entity);
        model.addAttribute("entityid", id);
        // 印章-公司名称
        model.addAttribute("companyNameJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CUSTOMER_NAME)));
        // 签署印章类型
        model.addAttribute("signTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.SIGN_TYPE)));
        return "apply/auto-sign-file-detail";
    }

    @ModelAttribute("preload")
    public SignFile getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                SignFile entity = new SignFile();
                entity.setId(0L);
                return entity;
            }
        }
        return null;
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            if (Objects.nonNull(vo.getId()) && vo.getId() > 0) {
                signFileClient.updateFileId(vo);
            }
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }


    //自动生成文件签署
    @RequestMapping(value = "generateDocumentSign/{id}", method = RequestMethod.POST)
    public void generateDocumentSign(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            PmApproveContents approveContents = pmApproveContentsClient.getEntity(id);
            SealUsage sealUsage = JsonUtil.json2Object(SealUsage.class, approveContents.getContents());
            SignFile entity = new SignFile();
            if (StringUtils.isNotBlank(approveContents.getFileId())) {
                entity.setFileId(approveContents.getFileId());
            } else {
                RenderUtil.renderFailure("失败", response);
                return;
            }
            entity.setFileName(approveContents.getSubject());
            entity.setCreator(ShiroUtil.getCurrentUserName());
            entity.setSignStatus("N");
            entity.setFile("签署文件");
            entity.setEnableFlg(true);
            PmApprove approveNoByApproveId = pmApproveClient.findApproveNoByApproveId(approveContents.getApproveId());
            entity.setSealUsageApproveNo(approveNoByApproveId.getApproveNo());
            entity.setSealUsageApproveId(approveNoByApproveId.getId());
            entity = getService().save(entity);
            List<SignFileUser> lstDeleted = new ArrayList<>();
            List<SignFileUser> lstUpdated = new ArrayList<>();
            List<SignFileUser> lstInsert = new ArrayList<>();
            SignFileUser signFileUser = new SignFileUser();
            String value = BsCompanyOurUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, sealUsage.getCompanyName());
            signFileUser.setCompanyName(value);
            String s = sealUsage.getSealType();
            if (sealUsage.getSealType().equals("TS")) {
                s = "CTR";
            }
            if (sealUsage.getSealType().equals("CS")) {
                s = "OFC";
            }
            if (sealUsage.getSealType().equals("WS")) {
                s = "LGS";
            }
            signFileUser.setSignType(s);
            signFileUser.setStatus("N");
            List<SignTransactor> signTransactor = new ArrayList<>();
            EnterpriseAccount companyName = enterpriseAccountClient.getByCompanyName(value);
            if (Objects.nonNull(companyName)) {
                signTransactor = signTransactorClient.findSignTransactorByuserId(companyName.getUserId());
                signFileUser.setSignPhone(signTransactor.get(0).getMobilePhone());
                signFileUser.setSignName(signTransactor.get(0).getTransactorName());
            }
            lstInsert.add(signFileUser);
            BatchSaveVo<SignFileUser> batchSaveVo = new BatchSaveVo<>();
            batchSaveVo.setDeletedRecords(lstDeleted);
            batchSaveVo.setUpdatedRecords(lstUpdated);
            batchSaveVo.setInsertedRecords(lstInsert);
            signFileUserClient.saveDatas(batchSaveVo, entity.getId());
            List<SignFileUser> signFileUserList = signFileUserClient.findSignFileUserBySignId(entity.getId());
            SignFile signFile = signFileClient.getEntity(entity.getId());
            String companyNames = signFileUserList.stream().map(SignFileUser::getCompanyName).collect(Collectors.joining("|"));
            signFile.setCompanyNames(companyNames);
            signFileClient.save(signFile);
            //生成短链接
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("文件签署发起失败:", e);
            RenderUtil.renderFailure("失败", response);
        }
    }
}
