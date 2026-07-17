package com.spt.bas.web.controller.apply;

import com.hsoft.file.sdk.constant.FileConstant;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.util.Base64Utility;
import com.hsoft.file.sdk.vo.FileRespVo;
import com.hsoft.file.sdk.vo.FileUploadBase64Request;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.report.client.remote.IRptBsCompanyClient;
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
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 申请-业务限制解除
 */
@Controller
@RequestMapping(value = "/apply/businessRestrictRelieve")
public class ApplyBusinessRestrictRelieveController extends PageController<ApplyBusinessRestrictRelieve, BaseVo> {

    @Autowired
    private IApplyBusinessRestrictRelieveClient applyBusinessRestrictRelieveClient;
    @Autowired
    private FileRemote fileRemote;
    @Autowired
    private IFileRecordClient fileRecordClient;
    @Value("${file.show.url}")
    private String fileShowUrl;
    @Autowired
    private IBsCompanyShareClient bsCompanyShareClient;
    @Autowired
    private IRptBsCompanyClient reportCompanyClient;
    @Autowired
    private com.spt.bas.client.remote.IBsCompanyClient companyClient;
    @Autowired
    private IBsCompanyAccountClient bsCompanyAccountClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IBsCompanyCreditClient companyCreditClient;

    @Override
    public BaseClient<ApplyBusinessRestrictRelieve> getService() {
        return applyBusinessRestrictRelieveClient;
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        ApplyBusinessRestrictRelieve entity = getEntity(id);
        if (entity.getId() == null) {
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_MATTERS_TYPE);
        EasyTreeNode nodes = EasyTreeUtil2.getMattersTree(listByCategory, true);
        model.addAttribute("mattersTypeTree", JsonUtil.obj2Json(nodes.getChildren()));
        model.addAttribute("ownRegionType", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_OWN_REGION)));
        model.addAttribute("fileShowUrl", fileShowUrl);
        model.addAttribute("defaultFlg",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DEFAULTFLG)));// 是否
        //授信类别
        model.addAttribute("creditTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_CREDIT_TYPE)));
        return "apply/applyBusinessRestrictRelieve";
    }


    @ModelAttribute("preload")
    public ApplyBusinessRestrictRelieve getEntity(@RequestParam(value = "id", required = false) Long id) {
        ApplyBusinessRestrictRelieve entity = new ApplyBusinessRestrictRelieve();
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
            applyBusinessRestrictRelieveClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    /**
     * 获取逾期信息
     */
    @RequestMapping("getOverdueInfo")
    public void getOverdueInfo(@RequestParam("companyId") Long companyId, HttpServletRequest request, HttpServletResponse response) {
        ApplyBusinessRestrictRelieve vo = new ApplyBusinessRestrictRelieve();
        List<CtrContract> contractList = ctrContractClient.findOverdueContractListByCompanyId(companyId);
        if (CollectionUtils.isNotEmpty(contractList)) {
            vo.setOverdueFlg(true);
            vo.setOverdueCount(contractList.size());
            BigDecimal overdueAmount = BigDecimal.ZERO;
            BigDecimal overduePrincipal = BigDecimal.ZERO;
            for (CtrContract contract : contractList) {
                BigDecimal breachAmount = contract.getBreachAmount();
                if (breachAmount == null) {
                    breachAmount = BigDecimal.ZERO;
                }
                BigDecimal receiveBreachAmount = contract.getReceiveBreachAmount();
                if (receiveBreachAmount == null) {
                    receiveBreachAmount = BigDecimal.ZERO;
                }
                BigDecimal subtract = breachAmount.subtract(receiveBreachAmount);
                overdueAmount = overdueAmount.add(subtract);
                BigDecimal noDealedAmount = contract.getTotalAmount().subtract(contract.getDealedAmount());
                overduePrincipal = overduePrincipal.add(noDealedAmount);
            }
            vo.setOverduePrincipal(overduePrincipal);
            vo.setOverdueAmountTotal(overdueAmount);
        } else {
            vo.setOverdueFlg(false);
        }
        String json = JsonUtil.obj2Json(vo);
        RenderUtil.renderJson(json, response);
    }
    
    /**
     * 是否存在超三天未发货订单
     */
    @RequestMapping("findUnDelivery3Day")
    public void findUnDelivery3Day(@RequestParam("companyId") Long companyId, HttpServletRequest request, HttpServletResponse response) {
        Boolean unDelivery3DayFlg = ctrContractClient.findUnDelivery3Day(companyId);
        HashMap<String, Boolean> map = new HashMap<>();
        map.put("relieveNotDeliver",unDelivery3DayFlg);
        String json = JsonUtil.obj2Json(map);
        RenderUtil.renderJson(json, response);
    }

    /**
     * 获取可用额度
     */
    @RequestMapping("getAvailableCreditAmount")
    public void getAvailableCreditAmount(@RequestParam("companyId") Long companyId, @RequestParam("creditType") String creditType, HttpServletResponse response) {
        BigDecimal availableCreditAmount = BigDecimal.ZERO;
        BsCompanyCredit companyCredit = companyCreditClient.findByCompanyIdAndType(companyId, creditType);
        
        if (Objects.nonNull(companyCredit)) {
            BigDecimal riskAmount = companyCredit.getRiskAmount();
            if (Objects.isNull(riskAmount)) {
                riskAmount = BigDecimal.ZERO;
            }
            BigDecimal temporaryAmount = companyCredit.getTemporaryAmount();
            BigDecimal usedCreditAmount = companyCredit.getUsedCreditAmount();
            availableCreditAmount = riskAmount.add(temporaryAmount).subtract(usedCreditAmount);
        }
        HashMap<String, BigDecimal> map = new HashMap<>();
        map.put("availableCreditAmount",availableCreditAmount);
        String json = JsonUtil.obj2Json(map);
        RenderUtil.renderJson(json, response);
    }
    

}
