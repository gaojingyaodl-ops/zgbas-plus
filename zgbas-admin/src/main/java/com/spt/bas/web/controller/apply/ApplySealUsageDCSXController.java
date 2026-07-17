package com.spt.bas.web.controller.apply;

import com.google.common.base.Splitter;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.BasBrand;
import com.spt.bas.client.entity.SealUsageDCSX;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 代采赊销印章使用申请单
 */
@Controller
@RequestMapping(value = "/apply/sealUsageDCSX")
public class ApplySealUsageDCSXController extends PageController<SealUsageDCSX, BaseVo> {
    @Autowired
    private ISealUsageDCSXClient sealUsageDCSXClient;
    @Autowired
    private IApplyCtrDcsxClinent applyCtrDcsxClinent;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IBsProductTypeClient productTypeClient;
    @Autowired
    private IBasBrandClient brandClient;
    @Autowired
    private IPmApproveClient pmApproceClient;

    @Value("${file.show.url}")
    private String fileShowUrl;


    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model,
                          HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        SealUsageDCSX entity = getEntity(id, processCode);
        // 印章-印章类型
        model.addAttribute("sealTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SEAL_TYPE)));
        // 印章-公司名称
        model.addAttribute("customerNameJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CUSTOMER_NAME)));
        // 印章-文件类型
        model.addAttribute("sealFileTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SEAL_FILE_TYPE)));
        Long approveId=entity.getApproveId();
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());

        model.addAttribute("psv", permissionVo);
        model.addAttribute("status",getStatus(approveId));

        String businessType = entity.getBusinessType();
        Long contractId = entity.getContractId();
        Long ctrContractId = null;
        String url = "";
        ApplyCtrDCSX applyCtrDCSX=null;
        if (StringUtils.isNotBlank(businessType)){
            if (StringUtils.equals(BasConstants.CONTRACT_TYPE_X,businessType)){
               applyCtrDCSX = applyCtrDcsxClinent.getEntity(contractId);
               if (StringUtils.isBlank(entity.getOurBankName())){
                   entity.setOurBankName(applyCtrDCSX.getOurBankName());
               }
               if (StringUtils.isBlank(entity.getOurBankAccount())){
                   entity.setOurBankAccount(applyCtrDCSX.getOurBankAccount());
               }
            }
            Long bsTemplateContractId = applyCtrDCSX.getBsTemplateContractId();
            if(bsTemplateContractId == null){
                if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S,businessType)){
                    String sellContentFileId = applyCtrDCSX.getSellContentFileId();
                    if (!StringUtils.isEmpty(sellContentFileId)) {
                        List<String> sellContentFileIdList = Splitter.on(",").omitEmptyStrings().splitToList(sellContentFileId);
                        if (CollectionUtils.isNotEmpty(sellContentFileIdList)){
                            url = fileShowUrl+"/view/show/"+sellContentFileIdList.get(0);
                        }
                    }
                }
            }
        }
        model.addAttribute("ctrContractId",ctrContractId);
        model.addAttribute("contractTextUrl", url);
        // 包装规格
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PACKINGSPECIFICA)));
        // 质量标准
        model.addAttribute("qualityStandardJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_QUALITYSTANDDARD)));
        // 货品树
        model.addAttribute("productTypeJson",
                JsonUtil.obj2Json(productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId())));
        List<BasBrand> lstBrand = brandClient.findAll();
        // 交货方式
        model.addAttribute("deliveryTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        model.addAttribute("brandJson", JsonUtil.obj2Json(lstBrand));
        model.addAttribute("entity", entity);model.addAttribute("sugxFlg", true);
        if(Objects.nonNull(entity)) {
            String companyName = entity.getCompanyName();
            String ourCompanyName = entity.getOurCompanyName();
            if(StringUtils.equals(BasConstants.COMPANY_NAME_SUGX,companyName) || StringUtils.equals(BasConstants.COMPANY_NAME_SUGX,ourCompanyName)) {
                model.addAttribute("sugxFlg", true);
            } else {
                model.addAttribute("sugxFlg", false);
            }
        }

        return "apply/contract-seal-usage";
    }


    @RequestMapping(value = "showReplaceSignFile")
    public String showReplaceSignFile(Model model) {
        return "apply/replace_sign_file";
    }

    /**
     * 保存替换签署文件
     * @param request
     * @param response
     */
    @RequestMapping(value = "saveReplaceSignFile")
    public void saveReplaceSignFile(HttpServletRequest request, HttpServletResponse response) {
        
        try {
            String fileIds = request.getParameter("fileId");
            String approveId = request.getParameter("approveId");
            String contractNo = request.getParameter("contractNo");
            String companyName = request.getParameter("companyName");
            String ourCompanyName = request.getParameter("ourCompanyName");
            SealUsageDcsxVo vo = new SealUsageDcsxVo();
            vo.setApproveId(Long.valueOf(approveId));
            vo.setFileId(fileIds);
            vo.setContractNo(contractNo);
            vo.setCompanyName(companyName);
            vo.setOurCompanyName(ourCompanyName);
            sealUsageDCSXClient.updateCfcaContractNo(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("替换签署文件失败",e);
            RenderUtil.renderFailure("替换签署文件失败:" + e.getMessage(), response);
        }
       
//        FileIdUpdateVo vo = new FileIdUpdateVo();
//        vo.setFileId(fileIds);
//        pmApproveContentsClient.updateFileId(vo);
        
        
    }

    @RequestMapping(value = "updateReplaceFileId", method = RequestMethod.POST)
    public void updateReplaceFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }
    
    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            pmApproveContentsClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @ModelAttribute("preload")
    public SealUsageDCSX getEntity(@RequestParam(value = "id", required = false) Long id,
                               @RequestParam(value = "processCode", required = false) String processCode) {
        SealUsageDCSX enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (SealUsageDCSX) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                SealUsageDCSX sealUsage = new SealUsageDCSX();
                sealUsage.setSealDate(new Date());
                sealUsage.setId(0L);
                return sealUsage;
            }
        }
        return enObject;
    }

    private String getStatus(Long id) {
        if (id != null && id > 0L) {
//            PmApproveContents entity = pmApproveContentsClient.getEntity(id);
            PmApprove entity = pmApproceClient.getEntity(id);
            if (entity != null) {
                return entity.getStatus();
            }
        }
        return BasConstants.APPROVE_STATUS_N;
    }

    @Override
    public BaseClient<SealUsageDCSX> getService() {
        return sealUsageDCSXClient;
    }
}
