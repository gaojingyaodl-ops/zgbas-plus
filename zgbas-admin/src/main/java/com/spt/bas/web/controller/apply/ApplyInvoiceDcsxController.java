package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyInvoice;
import com.spt.bas.client.entity.FileProcessRel;
import com.spt.bas.client.remote.IApplyCtrDcsxClinent;
import com.spt.bas.client.remote.IApplyInvoiceClient;
import com.spt.bas.client.remote.IFileProcessRelClient;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 代采赊销开票
 */
@Controller
@RequestMapping(value = "/apply/invoiceDcsx")
public class ApplyInvoiceDcsxController extends PageController<ApplyInvoice, BaseVo> {
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IApplyInvoiceClient invoiceClient;
    @Autowired
    private  IApplyCtrDcsxClinent ctrDcsxClinent;
    @Autowired
    private IFileProcessRelClient fileProcessRelClient;
    @Value("${file.show.url}")
    private String fileUrl;

    @Override
    public BaseClient<ApplyInvoice> getService() {
        return invoiceClient;
    }

    /**
     * 审批模板内容
     */
    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        ApplyInvoice entity = getEntity(id);
        model.addAttribute("approveStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        //我方抬头
        model.addAttribute("ourCompanyJson",
                JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        //包装规格-全部
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_IMPORTBUYPACKING)));
        model.addAttribute("entity", entity);
        if (entity.getContractId() != null) {
            ApplyCtrDCSX contract = ctrDcsxClinent.getEntity(entity.getContractId());
            model.addAttribute("productsName", contract.getProductsName());
            model.addAttribute("contractContent", contract);
        }
        String processCode = request.getParameter("processCode");
        // 附件类型
        List<FileProcessRel> fileTypeList = fileProcessRelClient.findList(processCode);
        model.addAttribute("fileTypeJson", JsonUtil.obj2Json(fileTypeList));
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("psv", permissionVo);

        model.addAttribute("isServiceInvoice", request.getParameter("isServiceInvoice"));
        // 双签展示路径
        model.addAttribute("fileServerUrl",fileUrl+"/view/show/");
            return "apply/invoiceDcsx-content";
    }

    @RequestMapping(value = "content2/{id}", method = RequestMethod.GET)
    public String content2(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        ApplyInvoice entity = getEntity(id);
        model.addAttribute("approveStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        //我方抬头
        model.addAttribute("ourCompanyJson",
                JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        //包装规格-全部
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_IMPORTBUYPACKING)));
        model.addAttribute("entity", entity);
        if (entity.getContractId() != null) {
            ApplyCtrDCSX contract = ctrDcsxClinent.getEntity(entity.getContractId());
            model.addAttribute("productsName", contract.getProductsName());
            model.addAttribute("contractContent", contract);
        }
        String processCode = request.getParameter("processCode");
        // 附件类型
        List<FileProcessRel> fileTypeList = fileProcessRelClient.findList(processCode);
        model.addAttribute("fileTypeJson", JsonUtil.obj2Json(fileTypeList));
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("psv", permissionVo);

        model.addAttribute("isServiceInvoice", request.getParameter("isServiceInvoice"));
        // 双签展示路径
        model.addAttribute("fileServerUrl",fileUrl+"/view/show/");
            return "apply/invoiceDcsx-content";
    }


    @ModelAttribute("preload")
    public ApplyInvoice getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                ApplyInvoice entity = new ApplyInvoice();
                entity.setId(0l);
                entity.setStatus(BasConstants.APPROVE_STATUS_N);
                entity.setInvoiceDate(new Date());
                return entity;
            }
        }
        return null;
    }

}
