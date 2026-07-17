package com.spt.bas.web.controller.sign;


import com.google.common.base.Splitter;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.FileRecord;
import com.spt.bas.client.entity.SealUsage;
import com.spt.bas.client.entity.SignFile;
import com.spt.bas.client.entity.SignFileUser;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.client.vo.sign.SignFileSearchVo;
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
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("documents/sign")
public class SignOnlineController extends SingleCrudControll<SignFile, BaseVo> {

    @Autowired
    private ISignFileClient iSignFileClient;
    @Autowired
    private ISignFileUserClient signFileUserClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Resource
    private IEnterpriseAccountClient enterpriseAccountClient;
    @Resource
    private  ISignTransactorClient signTransactorClient;
    @Resource
    private  ISignFileClient signFileClient;
    @Autowired
    private IPmApproveClient  pmApproceClient;
    @Autowired
    private IFileRecordClient fileRecordClient;

    @Autowired
    private  IPmApproveContentsClient pmApproveContentsClient;

    @Override
    public BaseClient<SignFile> getService() {
        return iSignFileClient;
    }

    @RequestMapping(value = "")
    public String index(Model model, HttpServletRequest request){
        model.addAttribute("companyNameJson", JsonUtil.obj2Json(
                BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        return "apply/sign-file";
    }
    @RequestMapping(value = "index2")
    public String index2(Model model, HttpServletRequest request){
        model.addAttribute("companyNameJson", JsonUtil.obj2Json(
                BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        return "apply/sign-file";
    }
    @RequestMapping(value = "findPageSignFile")
    public void findPageSignFile(SignFileSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        // 查看所有文件签署权限
        boolean viewAllFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_VIEW_ALL_SIGN_FILE.getPermissionCode());
        if (Boolean.FALSE.equals(viewAllFlg)){
            Map<String, Object> searchParams = searchVo.getSearchParams();
            searchParams = Objects.isNull(searchParams) ? new HashMap<>() : searchParams;
            searchParams.put("EQS_creator", ShiroUtil.getCurrentUserName());
        }
        PageDown<SignFile> pageSignFile = iSignFileClient.findPageSignFile(searchVo);
        JsonEasyUI.renderJson(response, pageSignFile);
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
        model.addAttribute("entity",entity);
        model.addAttribute("entityid",id);
        // 印章-公司名称
        model.addAttribute("companyNameJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CUSTOMER_NAME)));
        // 签署印章类型
        model.addAttribute("signTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BasConstants.SIGN_TYPE)));

        return "apply/sign-file-detail";
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

    private void saveDatas(HttpServletRequest request, Long dictTypeId) {
        try {
            List<SignFileUser> lstDeleted = JsonEasyUI.getDeletedRecords(SignFileUser.class, request);
            List<SignFileUser> lstInsert = JsonEasyUI.getInsertRecords(SignFileUser.class, request);
            List<SignFileUser> lstUpdated = JsonEasyUI.getUpdatedRecords(SignFileUser.class, request);
            BatchSaveVo<SignFileUser> batchSaveVo = new BatchSaveVo<>();
            batchSaveVo.setDeletedRecords(lstDeleted);
            batchSaveVo.setInsertedRecords(lstInsert);
            batchSaveVo.setUpdatedRecords(lstUpdated);
            signFileUserClient.saveDatas(batchSaveVo, dictTypeId);

            List<SignFileUser> signFileUserList = signFileUserClient.findSignFileUserBySignId(dictTypeId);
            SignFile entity = iSignFileClient.getEntity(dictTypeId);
            String companyNames = signFileUserList.stream().map(SignFileUser::getCompanyName).collect(Collectors.joining("|"));
            entity.setCompanyNames(companyNames);
            iSignFileClient.save(entity);
        } catch (Exception e) {
            logger.error("dictData save error!", e);
        }
    }

    //保存文件
    @RequestMapping(value = "saveSign", method = RequestMethod.POST)
    public void saveSign(@Valid SignFile entity, HttpServletRequest request, HttpServletResponse response) {
        try {
            entity = getService().save(entity);
            saveDatas(request, entity.getId());
            RenderUtil.renderSuccess(entity.getId() + "", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "listFileUser/{id}")
    public void listData(@PathVariable("id") Long id, HttpServletResponse response) {
        if (id != null && id > 0) {
            List<SignFileUser> signFileUserList = signFileUserClient.findSignFileUserBySignId(id);
            JsonEasyUI.renderListJson(response, signFileUserList);
        } else {
            JsonEasyUI.renderListJson(response, new ArrayList<>(0));
        }
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            if (Objects.nonNull(vo.getId()) && vo.getId() > 0){
                iSignFileClient.updateFileId(vo);
            }
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    //查找经办人手机号
    @RequestMapping(value = "findEntrust", method = RequestMethod.POST)
    public void findEntrust(HttpServletResponse response, HttpServletRequest request) {
        List<SignTransactor> signTransactor = new ArrayList<>();
        final String name = request.getParameter("name");
        try {
            EnterpriseAccount companyName = enterpriseAccountClient.getByCompanyName(name);
            if (Objects.nonNull(companyName)){
                signTransactor = signTransactorClient.findSignTransactorByuserId(companyName.getUserId());
            }
        } catch (Exception e) {
            logger.error("errorId:", e);
        }
        RenderUtil.renderJson(JsonUtil.obj2Json(signTransactor), response);
    }

    //查找经办人姓名
    @RequestMapping(value = "findEntrustName/{id}", method = RequestMethod.POST)
    public void findEntrustName(@PathVariable("id") Long id, HttpServletResponse response) {
        SignTransactor one= null;
        try {
              one = signTransactorClient.findOne(id);
        } catch (Exception e) {
            logger.error("errorId:", e);
        }
        RenderUtil.renderJson(one,response);
    }


    /**
     * 生成短链接后刷新页面
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "detail2/{id}")
    public String detail2(Model model, @PathVariable("id") Long id) {
        if(id==0){
            SignFile byAllLimit = signFileClient.findByAllLimit();
             id=byAllLimit.getId();
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
        model.addAttribute("entity",entity);
        model.addAttribute("entityid",id);
        // 印章-公司名称
        model.addAttribute("companyNameJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CUSTOMER_NAME)));
        // 签署印章类型
        model.addAttribute("signTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BasConstants.SIGN_TYPE)));
        return "apply/sign-file-detail";
    }

    //自动生成文件签署
    @RequestMapping(value = "generateDocumentSign/{id}", method = RequestMethod.POST)
    public void generateDocumentSign(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try{
            PmApproveContents approveContents = pmApproveContentsClient.getEntity(id);
            SealUsage sealUsage = JsonUtil.json2Object(SealUsage.class, approveContents.getContents());
            SignFile entity=new SignFile();
            List<String> fileIds = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(approveContents.getFileId());
            if(CollectionUtils.isNotEmpty(fileIds)){
                String resultFileId = fileIds.get(fileIds.size() - 1);
                FileRecord fileRecord = fileRecordClient.findByFileId(resultFileId);
                if (Objects.nonNull(fileRecord) && fileRecord.getFileName().contains(".pdf") || fileRecord.getFileName().contains(".PDF")){
                    entity.setFileId(resultFileId);
                }
            } else {
                RenderUtil.renderFailure("签署附件缺失", response);
                return;
            }
            entity.setFileName(approveContents.getSubject());
            entity.setCreator(ShiroUtil.getCurrentUserName());
            entity.setSignStatus("N");
            entity.setFile("签署文件");
            entity.setEnableFlg(true);
            PmApprove approveNoByApproveId = pmApproceClient.findApproveNoByApproveId(approveContents.getApproveId());
            entity.setSealUsageApproveNo(approveNoByApproveId.getApproveNo());
            entity.setSealUsageApproveId(approveNoByApproveId.getId());
            entity = getService().save(entity);
            List<SignFileUser> lstDeleted =  new ArrayList<>();
            List<SignFileUser> lstUpdated =  new ArrayList<>();
            List<SignFileUser> lstInsert = new ArrayList<>();
            SignFileUser signFileUser=new SignFileUser();
            String value = BsCompanyOurUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, sealUsage.getCompanyName());
            signFileUser.setCompanyName(value);
            String s=sealUsage.getSealType();
            if(sealUsage.getSealType().equals("TS")){
                s="CTR";
            }
            if(sealUsage.getSealType().equals("CS")){
                s="OFC";
            }
            if(sealUsage.getSealType().equals("WS")){
                s="LGS";
            }
            signFileUser.setSignType(s);
            signFileUser.setStatus("N");
            List<SignTransactor> signTransactor = new ArrayList<>();
            EnterpriseAccount companyName = enterpriseAccountClient.getByCompanyName(value);
            if (Objects.nonNull(companyName)){
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
            SignFile signFile = iSignFileClient.getEntity(entity.getId());
            String companyNames = signFileUserList.stream().map(SignFileUser::getCompanyName).collect(Collectors.joining("|"));
            signFile.setCompanyNames(companyNames);
            iSignFileClient.save(signFile);
            //生成短链接
//            SignFile signature = iSignFileClient.generateSignature(entity.getId());
            RenderUtil.renderSuccess("success", response);
        }catch (Exception e){
            logger.error("文件签署发起失败:", e);
            RenderUtil.renderFailure("失败", response);
        }
    }
}
