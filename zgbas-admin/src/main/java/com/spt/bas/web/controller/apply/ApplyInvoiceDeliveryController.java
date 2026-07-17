package com.spt.bas.web.controller.apply;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInvoiceDelivery;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.FileProcessRel;
import com.spt.bas.client.remote.IApplyInvoiceDeliveryClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.IFileProcessRelClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 *      发票寄送审批
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-10 13:42
 */
@Controller
@RequestMapping(value = "/apply/invoiceDelivery")
public class ApplyInvoiceDeliveryController extends PageController<ApplyInvoiceDelivery, BaseVo> {

    @Autowired
    private IApplyInvoiceDeliveryClient applyInvoiceDeliveryClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Override
    public BaseClient<ApplyInvoiceDelivery> getService() {
        return applyInvoiceDeliveryClient;
    }
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IFileProcessRelClient fileProcessRelClient;

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        ApplyInvoiceDelivery entity = getEntity(id, processCode);
        model.addAttribute("entity", entity);
        // 附件类型
        List<FileProcessRel> fileTypeList = fileProcessRelClient.findList(processCode);
        model.addAttribute("fileTypeJson", JsonUtil.obj2Json(fileTypeList));

        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());

        model.addAttribute("psv", permissionVo);
        model.addAttribute("status",getStatus(id));
        return "apply/invoiceDelivery";
    }

    @ModelAttribute("preload")
    public ApplyInvoiceDelivery getEntity(@RequestParam(value = "id", required = false) Long id,
                                      @RequestParam(value = "processCode", required = false) String processCode) {
        if (id != null) {
            if (id > 0){
                return getService().getEntity(id);
            }else {
                ApplyInvoiceDelivery entity = new ApplyInvoiceDelivery();
                entity.setId(0L);
                entity.setStatus(BasConstants.APPROVE_STATUS_N);
                return entity;
            }
        }
        return null;
    }

    private String getStatus(Long id) {
        if (id != null && id > 0L) {
            ApplyInvoiceDelivery entity = applyInvoiceDeliveryClient.getEntity(id);
            if (entity != null) {
                return entity.getStatus();
            }
        }
        return BasConstants.APPROVE_STATUS_N;
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo,
                             HttpServletResponse response) {
        try {
            applyInvoiceDeliveryClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }


    /**
     * 通过contractNo获取合同信息
     * @param contractNo
     * @param response
     */
    @RequestMapping(value = "findByContractNo", method = RequestMethod.POST)
    public void findByContractNo(@RequestParam(value = "contractNo", required = false) String contractNo,HttpServletResponse response) {
        try {
            CtrContract contract = ctrContractClient.findByContractNoV2(contractNo);
            RenderUtil.renderJson(contract, response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

}
